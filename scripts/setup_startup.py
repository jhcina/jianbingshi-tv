#!/usr/bin/env python3
"""One-time startup configuration for measured Xiaomi firmware. No reboot or app launch."""
import argparse
import datetime
import json
import re
import shlex
import subprocess
from pathlib import Path

PACKAGES = {"io.github.jhcina.yitv", "io.github.op7418.guizangtv.yi"}
PROFILES = {("MiTV-MFTR0", "OS3.0.101.0.UFTMATV"), ("MiTV-MTSU0", "OS3.0.119.0.UTSMATV")}

def plan(model, firmware, package, current, replace=False):
    if (model, firmware) not in PROFILES:
        raise ValueError("This firmware is not in the measured startup profiles.")
    if package not in PACKAGES:
        raise ValueError("Unknown application package.")
    target = current.get("start_3rd_app")
    if target not in (None, "", package) and not replace:
        raise ValueError("Another startup target exists; inspect it before using --replace.")
    return {"start_3rd_app": package, "boot_auto_play": "0"}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--serial", required=True)
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--server-port", type=int, default=5037)
    parser.add_argument("--package", choices=sorted(PACKAGES), default="io.github.jhcina.yitv")
    parser.add_argument("--backup", type=Path)
    parser.add_argument("--replace", action="store_true")
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--check", action="store_true")
    mode.add_argument("--apply", action="store_true")
    mode.add_argument("--restore", type=Path)
    args = parser.parse_args()
    base = [args.adb, "-P", str(args.server_port), "-s", args.serial, "shell"]
    def shell(*words):
        command = " ".join(shlex.quote(str(word)) for word in words)
        return subprocess.check_output(base + [command], timeout=20).decode("utf-8", errors="replace").strip()
    model = shell("getprop", "ro.product.model")
    firmware = shell("getprop", "ro.build.version.incremental")
    if (model, firmware) not in PROFILES:
        raise SystemExit("Unsupported firmware; no settings changed.")
    listing = shell("settings", "list", "system")
    current = {}
    for key in ("start_3rd_app", "boot_auto_play"):
        current[key] = next((line.split("=", 1)[1] for line in listing.splitlines() if line.startswith(key + "=")), None)
    if args.restore:
        record = json.loads(args.restore.read_text(encoding="utf-8"))
        if record["model"] != model or record["firmware"] != firmware or record["package"] != args.package:
            raise SystemExit("Backup does not match this firmware/package; no changes.")
        if current["start_3rd_app"] != args.package:
            raise SystemExit("Startup target changed since setup; do not overwrite a newer choice.")
        target = record["settings"]
        if set(target) != {"start_3rd_app", "boot_auto_play"}:
            raise SystemExit("Invalid backup keys.")
        value = target["start_3rd_app"]
        if value not in (None, "") and not re.fullmatch(r"[A-Za-z][A-Za-z0-9_]*(?:\.[A-Za-z0-9_]+)+", value):
            raise SystemExit("Invalid startup target in backup.")
        if target["boot_auto_play"] not in (None, "", "0", "1"):
            raise SystemExit("Invalid signal-source value in backup.")
    else:
        try:
            target = plan(model, firmware, args.package, current, args.replace)
        except ValueError as error:
            raise SystemExit(str(error))
        entry = shell("cmd", "package", "resolve-activity", "--brief", "-a", "android.intent.action.MAIN", "-c", "android.intent.category.INFO", "-p", args.package)
        if "BootGateActivity" not in entry:
            raise SystemExit("The required startup gate is not installed; no changes.")
        if args.check:
            print(json.dumps({"model": model, "firmware": firmware, "current": current, "configured": current == target}, ensure_ascii=False))
            return
        if not args.backup or args.backup.exists():
            raise SystemExit("--apply requires a new --backup path. Existing files are never overwritten.")
        args.backup.parent.mkdir(parents=True, exist_ok=True)
        record = {"model": model, "firmware": firmware, "package": args.package, "time": datetime.datetime.now().astimezone().isoformat(), "settings": current}
        with args.backup.open("x", encoding="utf-8") as stream:
            json.dump(record, stream, ensure_ascii=False, indent=2)
    for key, value in target.items():
        if value is None:
            shell("settings", "delete", "system", key)
        else:
            shell("settings", "put", "system", key, value)
    after = shell("settings", "list", "system")
    for key, value in target.items():
        actual = next((line.split("=", 1)[1] for line in after.splitlines() if line.startswith(key + "=")), None)
        if actual != value:
            raise SystemExit("Readback differs: " + key + ". Keep the backup and inspect before retrying.")
    print("Startup settings read back. No app was launched, no permission was granted, and no reboot was requested. Enable the in-app switch and test when the TV is free.")

if __name__ == "__main__":
    main()

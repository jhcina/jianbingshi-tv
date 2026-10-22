#!/usr/bin/env python3
"""Import photos into a selected TV's runtime gallery. Never writes repository assets."""
import argparse
import io
import json
import subprocess
import tempfile
import uuid
from pathlib import Path
from PIL import Image, ImageOps

PACKAGE = "io.github.jhcina.yitv"


def prepare(source, destination, thumb):
    with Image.open(source) as image:
        image = ImageOps.exif_transpose(image).convert("RGB")
        image = ImageOps.contain(image, (1920, 1080), Image.Resampling.LANCZOS)
        frame = Image.new("RGB", (1920, 1080), "#151719")
        frame.paste(image, ((1920-image.width)//2, (1080-image.height)//2))
        frame.save(destination, "JPEG", quality=94)
        frame.thumbnail((320, 180), Image.Resampling.LANCZOS)
        frame.save(thumb, "JPEG", quality=85)


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("photos", nargs="+", type=Path)
    p.add_argument("--serial", required=True, help="Exact device from adb devices")
    p.add_argument("--adb", default="adb")
    p.add_argument("--server-port", type=int, default=5037)
    p.add_argument("--package", default=PACKAGE, choices=[PACKAGE, "io.github.op7418.guizangtv.yi"])
    args = p.parse_args()
    for photo in args.photos:
        if not photo.is_file(): p.error("Photo does not exist: " + str(photo))
    base = [args.adb, "-P", str(args.server_port), "-s", args.serial]

    def adb(*command):
        return subprocess.run(base+list(command), check=True, capture_output=True).stdout

    if b"package:" not in adb("shell", "pm", "path", args.package):
        raise ValueError("Install and open Yi TV before importing photos")
    folder = "/sdcard/Android/data/"+args.package+"/files/my-wallpapers"
    catalog = folder+"/catalog.json"
    rows = []
    exists = adb("shell", "sh", "-c", "'if [ -f "+catalog+" ]; then echo yes; fi'").strip() == b"yes"
    if exists:
        raw = adb("exec-out", "cat", catalog)
        if len(raw)>65536: raise ValueError("Existing gallery catalog is too large")
        rows = json.loads(raw)
    if not isinstance(rows, list) or len(rows)+len(args.photos)>30:
        raise ValueError("At most 30 photos; existing gallery was not changed")
    with tempfile.TemporaryDirectory(prefix="yi-tv-import-") as temp:
        temp = Path(temp)
        additions = []
        for photo in args.photos:
            token = "import_"+uuid.uuid4().hex
            image, thumb = temp/(token+".jpg"), temp/(token+"_thumb.jpg")
            prepare(photo, image, thumb)
            additions.append((image, thumb))
            rows.append({"name":"我的照片 "+str(len(rows)+1), "file":image.name, "thumb":thumb.name})
        adb("shell", "mkdir", "-p", folder)
        for image, thumb in additions:
            adb("push", str(image), folder+"/"+image.name)
            adb("push", str(thumb), folder+"/"+thumb.name)
        manifest=temp/"catalog.json"
        manifest.write_text(json.dumps(rows,ensure_ascii=False),encoding="utf-8")
        pending=folder+"/catalog-"+uuid.uuid4().hex+".pending"
        adb("push", str(manifest), pending)
        adb("shell", "mv", pending, catalog)
        if json.loads(adb("exec-out", "cat", catalog)) != rows: raise RuntimeError("Verification failed")
    print("Imported and verified. Reopen the desktop, then Settings > Background > My wallpapers.")
    print("Originals are unchanged. Import did not change the current wallpaper or enable any service.")


if __name__ == "__main__": main()

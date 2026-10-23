#!/usr/bin/env python3
"""Opt-in MFTR0 regression check: desktop Back, open file manager, then Home.
Never enables permissions, changes the default launcher, or deletes user data.
"""
import argparse
import json
import subprocess
import time


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--serial',required=True)
    parser.add_argument('--adb',default='adb')
    parser.add_argument('--server-port',type=int,default=5037)
    parser.add_argument('--package',choices=['io.github.jhcina.yitv','io.github.op7418.guizangtv.yi'],default='io.github.jhcina.yitv')
    args=parser.parse_args()
    base=[args.adb,'-P',str(args.server_port),'-s',args.serial,'shell']
    def shell(*command):return subprocess.check_output(base+list(command)).decode('utf-8',errors='replace')
    if shell('getprop','ro.product.model').strip()!='MiTV-MFTR0' or shell('getprop','ro.build.version.incremental').strip()!='OS3.0.101.0.UFTMATV':
        raise SystemExit('This measured Home check is only for MFTR0 OS3.0.101.0.UFTMATV.')
    service=args.package+'/io.github.op7418.guizangtv.HomeReturnService'
    if service not in shell('settings','get','secure','enabled_accessibility_services'):
        raise SystemExit('Home return is not enabled by the device owner; no settings were changed.')
    def wait_focus(package):
        deadline=time.monotonic()+6
        while time.monotonic()<deadline:
            lines=shell('dumpsys','window').splitlines()
            if any('mCurrentFocus=' in x and package+'/' in x for x in lines):return True
            time.sleep(0.2)
        return False
    results=[]
    for cycle in range(3):
        shell('am','start','-W','-n',args.package+'/io.github.op7418.guizangtv.FrameHomeActivity')
        if not wait_focus(args.package):raise SystemExit('Desktop did not become foreground; stopped.')
        shell('input','keyevent','4')
        if not wait_focus(args.package):raise SystemExit('Back left the desktop; regression found.')
        shell('am','start','-W','-a','duokan.mediaexplorer.intent.action.BROWSE_DEVICE_LIST','-p','com.xiaomi.mitv.mediaexplorer')
        if not wait_focus('com.xiaomi.mitv.mediaexplorer'):raise SystemExit('File manager did not open; stopped.')
        shell('input','keyevent','3')
        if not wait_focus(args.package):raise SystemExit('Home did not return to this desktop.')
        results.append({'cycle':cycle+1,'back_stayed_home':True,'app_home_returned':True})
    print(json.dumps({'cycles':results,'real_remote_verified':False,'standby_verified':False}))

if __name__=='__main__':main()

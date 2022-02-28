#!/bin/bash

DEVICE="emulator-5556"
apk_path="$1"
screencap_path="/app/wwwroot/$2"
image_dir="$(dirname $screencap_path)"

function fetch_screencap {
    error_code="$1"
    find "$image_dir" -mmin +59 -type f -name "*.png" -exec rm -f {} \;
    adb -s "$DEVICE" exec-out screencap -p > "$screencap_path"
    if [ -s "$screencap_path" ]; then
        (( error_code += 100 ))
    fi
    return "$error_code"
}

adb -s "$DEVICE" root
adb -s "$DEVICE" uninstall com.f32.fit32 &> /dev/null
if ! adb -s "$DEVICE" install "$apk_path"; then
    exit 1
fi
if ! adb -s "$DEVICE" shell am start -n com.f32.fit32/com.f32.fit32.HomeActivity; then
    exit 2
fi
sleep 5
if ! adb -s "$DEVICE" exec-out uiautomator dump /sdcard/homescreen_actual.xml; then
    exit $(fetch_screencap 3)
fi
if ! adb -s "$DEVICE" pull /sdcard/homescreen_actual.xml /tmp/homescreen_actual.xml; then
    exit $(fetch_screencap 4)
fi
if grep -q "BUNGUS" /tmp/homescreen_actual.xml; then
    exit $(fetch_screencap 5)
fi
if grep -q "System UI isn't responding" /tmp/homescreen_actual.xml; then
    read -r x1 y1 x2 y2 <<< $(sed -n -E 's/^.*text="Wait"[^>]+bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\].*$/\1 \2 \3 \4/p' /tmp/homescreen_actual.xml)
    (( x = (x1 + x2) / 2 ))
    (( y = (y1 + y2) / 2 ))
    if ! adb -s "$DEVICE" shell input tap $x $y; then
        exit $(fetch_screencap 8)
    fi
    sleep 15
    if ! adb -s "$DEVICE" exec-out uiautomator dump /sdcard/homescreen_actual.xml; then
        exit $(fetch_screencap 3)
    fi
    if ! adb -s "$DEVICE" pull /sdcard/homescreen_actual.xml /tmp/homescreen_actual.xml; then
        exit $(fetch_screencap 4)
    fi
    if grep -q "System UI isn't responding" /tmp/homescreen_actual.xml; then
        exit $(fetch_screencap 6)
    fi
fi
if ! cmp -s /app/homescreen_expected.xml /tmp/homescreen_actual.xml; then
    exit $(fetch_screencap 7)
fi
read -r x1 y1 x2 y2 <<< $(sed -n -E 's/^.*text="Help"[^>]+bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\].*$/\1 \2 \3 \4/p' /tmp/homescreen_actual.xml)
(( x = (x1 + x2) / 2 ))
(( y = (y1 + y2) / 2 ))
if ! adb -s "$DEVICE" shell input tap $x $y; then
    exit $(fetch_screencap 8)
fi
if ! adb -s "$DEVICE" exec-out uiautomator dump /sdcard/helpscreen_actual.xml; then
    exit $(fetch_screencap 9)
fi
if ! adb -s "$DEVICE" pull /sdcard/helpscreen_actual.xml /tmp/helpscreen_actual.xml; then
    exit $(fetch_screencap 10)
fi
if ! cmp -s /app/helpscreen_expected.xml /tmp/helpscreen_actual.xml; then
    exit $(fetch_screencap 11)
fi
exit 0

/*
 * This file is part of the RootFW Project: https://github.com/spazedog/rootfw
 *
 * Copyright (c) 2017 Daniel Bergløv, License: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" to WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:JvmName("Common")

package com.spazedog.lib.rootfw.stdlib.utils

import android.os.Process

/**
 * @suppress
 */
private val OCTALS = mapOf<String, Int>(
        "1:r" to 400,
        "2:w" to 200,
        "3:x" to 100,
        "3:s" to 4100,
        "3:S" to 4000,
        "4:r" to 40,
        "5:w" to 20,
        "6:x" to 10,
        "6:s" to 2010,
        "6:S" to 2000,
        "7:r" to 4,
        "8:w" to 2,
        "9:x" to 1,
        "9:t" to 1001,
        "9:T" to 1000
)

/**
 * @suppress
 */
private val SYSUID = mapOf<String, Int>(
        "root" to 0,
        "system" to 1000,
        "radio" to 1001,
        "bluetooth" to 1002,
        "graphics" to 1003,
        "input" to 1004,
        "audio" to 1005,
        "camera" to 1006,
        "log" to 1007,
        "compass" to 1008,
        "mount" to 1009,
        "wifi" to 1010,
        "adb" to 1011,
        "install" to 1012,
        "media" to 1013,
        "dhcp" to 1014,
        "sdcard_rw" to 1015,
        "vpn" to 1016,
        "keystore" to 1017,
        "usb" to 1018,
        "drm" to 1019,
        "mdnsr" to 1020,
        "gps" to 1021,
        "unused1" to 1022,
        "media_rw" to 1023,
        "mtp" to 1024,
        "unused2" to 1025,
        "drmrpc" to 1026,
        "nfc" to 1027,
        "sdcard_r" to 1028,
        "clat" to 1029,
        "loop_radio" to 1030,
        "media_drm" to 1031,
        "package_info" to 1032,
        "sdcard_pics" to 1033,
        "sdcard_av" to 1034,
        "sdcard_all" to 1035,
        "logd" to 1036,
        "shared_relro" to 1037,
        "dbus" to 1038,
        "tlsdate" to 1039,
        "media_ex" to 1040,
        "audioserver" to 1041,
        "metrics_coll" to 1042,
        "metricsd" to 1043,
        "webserv" to 1044,
        "debuggerd" to 1045,
        "media_codec" to 1046,
        "cameraserver" to 1047,
        "firewall" to 1048,
        "trunks" to 1049,
        "nvram" to 1050,
        "dns" to 1051,
        "dns_tether" to 1052,
        "webview_zygote" to 1053,
        "vehicle_network" to 1054,
        "media_audio" to 1055,
        "media_video" to 1056,
        "media_image" to 1057,
        "tombstoned" to 1058,
        "media_obb" to 1059,
        "ese" to 1060,
        "ota_update" to 1061,
        "automotive_evs" to 1062,
        "shell" to 2000,
        "cache" to 2001,
        "diag" to 2002,
        "net_bt_admin" to 3001,
        "net_bt" to 3002,
        "inet" to 3003,
        "net_raw" to 3004,
        "net_admin" to 3005,
        "net_bw_stats" to 3006,
        "net_bw_acct" to 3007,
        "readproc" to 3009,
        "wakelock" to 3010,
        "uhid" to 3011,
        "everybody" to 9997,
        "misc" to 9998,
        "nobody" to 9999
)

/**
 * @suppress
 *
 * Convert an access representation like `-rwxr-xr-x` into
 * an permission octal
 */
internal fun convertToPermissions(access: String): Int {
    var octal = 0;

    for (i in 1 until access.length) {
        octal += OCTALS.get("$i:${access[i]}") ?: 0
    }

    return octal
}

/**
 * @suppress
 */
internal fun convertToUID(uname: String): Int {
    if (uname.matches(Regex("^[0-9]+$"))) {
        return uname.toInt()

    } else if (SYSUID.containsKey(uname)) {
        return SYSUID[uname] ?: -1

    } else if (uname.startsWith('u')) {
        val sep = uname.indexOf("_")

        if (sep > 0) {
            val uid = uname.substring(1, sep).toInt()
            val gid = uname.substring(sep+2).toInt()

            return uid * 100000 + ((Process.FIRST_APPLICATION_UID + gid) % 100000)
        }
    }

    return -1
}

/**
 * @suppress
 */
internal fun convertToUName(uuid: Int): String {
    if (SYSUID.containsValue(uuid)) {
        for (entry in SYSUID) {
            if (entry.value == uuid) {
                return entry.key
            }
        }

    } else if (uuid in 2900..2999 || uuid in 5000..5999) {
        return "oem_reserved"

    } else if (uuid in Process.FIRST_APPLICATION_UID..Process.LAST_APPLICATION_UID) {
        val uid = uuid / 100000
        val gid = uuid % Process.FIRST_APPLICATION_UID

        return "u${uid}_a$gid"
    }

    return "unknown"
}

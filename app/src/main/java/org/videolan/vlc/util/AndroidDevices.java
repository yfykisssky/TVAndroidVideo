/*****************************************************************************
 * AndroidDevices.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.util;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.telephony.TelephonyManager;

import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.VLCApplication;

import java.util.HashSet;

public class AndroidDevices {
    public final static String TAG = "VLC/UiTools/AndroidDevices";

    final static boolean hasNavBar;

    static {
        HashSet<String> devicesWithoutNavBar = new HashSet<String>();
        devicesWithoutNavBar.add("HTC One V");
        devicesWithoutNavBar.add("HTC One S");
        devicesWithoutNavBar.add("HTC One X");
        devicesWithoutNavBar.add("HTC One XL");
        hasNavBar = AndroidUtil.isICSOrLater()
                && !devicesWithoutNavBar.contains(android.os.Build.MODEL);
    }

    public static boolean hasNavBar() {
        return hasNavBar;
    }

    /**
     * hasCombBar test if device has Combined Bar : only for tablet with Honeycomb or ICS
     */
    public static boolean hasCombBar() {
        return (!AndroidDevices.isPhone()
                && ((VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) &&
                (VERSION.SDK_INT <= VERSION_CODES.JELLY_BEAN)));
    }

    public static boolean isPhone() {
        TelephonyManager manager = (TelephonyManager) VLCApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

}

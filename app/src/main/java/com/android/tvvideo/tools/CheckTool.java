package com.android.tvvideo.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class CheckTool {

    public static boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    public static boolean isMobileNum(String mobiles) {

        if (mobiles.length()<11) {
            return false;
        }

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,5-9])|(17[6，7,8]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

}

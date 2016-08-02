package com.android.tvvideo.tools;

import java.io.DataOutputStream;

/**
 * Created by yangfengyuan on 16/8/2.
 */
public class DeviceUtil {


    /*
     * 执行命令
     * @param command
     * 1、获取root权限 "chmod 777 "+getPackageCodePath()
     * 2、关机 reboot -p
     * 3、重启 reboot
     */
    public static boolean execCmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;

    }
}
package com.android.tvvideo.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tvvideo.view.ShutDownDialog;

import java.io.DataOutputStream;
import java.util.Calendar;

/**
 * Created by yangfengyuan on 16/7/18.
 */
public class SystemUtil {

    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();

        if (activeInfo == null) {

            return false;
        }

        return false;

    }

    public static String getMacAddress(Context context){

        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi.getConnectionInfo();

        return info.getMacAddress();

    }

    //得到字符串型版本号
    public static String getVersion(Context context) {
        try {
            PackageManager manager =context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            final String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //得到数字型版本号
    public static int getIntVersion(Context context){

        return Integer.parseInt(getVersion(context).replace(".",""));

    }

    //得到系统的屏幕宽度
    public static int getWindowWidth(Context context){

        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

    }

    //得到系统的屏幕高度
    public static int getWindowHeight(Context context){

        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

    }

    //得到apk的签名字符数组信息
    static Signature[] getSignature(Context context) {
        String pkgname =context.getPackageName();
        /** 通过包管理器获得指定包名包含签名的包信息 **/
        PackageInfo packageInfo=null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /******* 通过返回的包信息获得签名数组 *******/
        Signature[] signatures = packageInfo.signatures;

        return signatures;

    }

    //得到apk的签名字符串信息
    public static String getSignStr(Context context) {

        Signature[] signatures=getSignature(context);
        /******* 循环遍历签名数组拼接应用签名 *******/
        StringBuilder builder=new StringBuilder();
        for (Signature signature : signatures) {
            builder.append(signature.toCharsString());
        }

        /************** 得到应用签名 **************/
        String signature = builder.toString();
        return signature;
    }

    //得到apk的签名的hash取值
    public static int getSignHash(Context context) {

        final String packname = context.getPackageName();
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            int code = sign.hashCode();
            return code;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }


    // 得到本机ip地址
    public static String getLocalHostIp()
    {

        return "192.168.1.1";
       /* String ipaddress = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {

                        return inetAddress.getHostAddress().toString();

                    }
                }
            }
        } catch (SocketException ex) {
            ipaddress="获取本地ip地址失败";
            ex.printStackTrace();
        }
        return ipaddress;*/

    }

    // 得到本机Mac地址
    public static String getLocalMac(Context context)
    {
        WifiManager wifiMng = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        return wifiInfor.getMacAddress();
    }

    public static void setHideKeyBoard(final Context context, final EditText editText){

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(),0);

                    return true;

                }

                return false;

            }

        });

    }

    public static String getServerUrl(Context context){

        String serverIp= ShaPreHelper.readShaPre("settings","server_ip",context);

        String serverPort= ShaPreHelper.readShaPre("settings","server_port",context);

        return "http://www.best673.top/videoedu";

        //return serverIp+":"+serverPort;

    }

    public interface GetLocalTime{

        void time(int year, int month, int day, int hour, int minute, int second);

    }

    public static void getLocalTime(GetLocalTime getLocalTime){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second=c.get(Calendar.SECOND);
        getLocalTime.time(year,month,day,hour,minute,second);
    }

    //关机 reboot -p
    //重启 reboot
    public static void shutDown(Context context){

        ShutDownDialog shutDownDialog=new ShutDownDialog(context);

        shutDownDialog.show();

    }

    public static boolean execCmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
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
                if(process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}


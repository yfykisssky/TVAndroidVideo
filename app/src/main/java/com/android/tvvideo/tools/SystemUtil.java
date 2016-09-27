package com.android.tvvideo.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //得到系统的屏幕宽度
    public static int getWindowWidth(Context context){

        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

    }

    //得到系统的屏幕高度
    public static int getWindowHeight(Context context){

        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

    }

    // 得到本机ip地址
    public static String getLocalHostIp()
    {
        String ipaddress = "";
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
        return ipaddress;

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

        return serverIp+":"+serverPort;

    }

    public static String getServerWs(Context context){

        String serverIp= ShaPreHelper.readShaPre("settings","server_ip",context);

        String serverPort= ShaPreHelper.readShaPre("settings","server_port",context);

        String ip=serverIp.substring(serverIp.indexOf(":"),serverIp.length());

        ip="ws"+ip;

        return ip+":"+serverPort+"/ws/";

    }

    public static String getServerAdPath(Context context){

        String serverIp= ShaPreHelper.readShaPre("settings","server_ip",context);

        String serverPort= ShaPreHelper.readShaPre("settings","server_port",context);

        return serverIp+":"+serverPort+"/ad/";

    }

    public static boolean validateTime(String dateString){
        //使用正则表达式 测试 字符 符合 dddd-dd-dd 的格式(d表示数字)
        Pattern p = Pattern.compile("\\d{4}+[-]\\d{1,2}+[-]\\d{1,2}+");
        Matcher m = p.matcher(dateString);
        if(!m.matches()){	return false;}

        //得到年月日
        String[] array = dateString.split("-");
        int year = Integer.valueOf(array[0]);
        int month = Integer.valueOf(array[1]);
        int day = Integer.valueOf(array[2]);

        if(month<1 || month>12){	return false;}
        int[] monthLengths = new int[]{0, 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if(isLeapYear(year)){
            monthLengths[2] = 29;
        }else{
            monthLengths[2] = 28;
        }
        int monthLength = monthLengths[month];
        if(day<1 || day>monthLength){
            return false;
        }
        return true;
    }

    /** 是否是闰年 */
    private static boolean isLeapYear(int year){
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ;
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

    public static void shutDown(Context context){

        ShutDownDialog shutDownDialog=new ShutDownDialog(context);

        shutDownDialog.show();

    }

    public static void shutDownSystem(Context context){

        final String SHUTDOWN_ACTION="com.hisense.action.powerdown";

        Intent intent = new Intent();
        intent.setAction(SHUTDOWN_ACTION);
        context.sendBroadcast(intent);

    }



/*    public static boolean execCmd(String command) {
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
    }*/

}


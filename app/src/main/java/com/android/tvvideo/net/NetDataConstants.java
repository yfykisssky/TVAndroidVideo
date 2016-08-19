package com.android.tvvideo.net;

/**
 * Created by yangfengyuan on 16/7/21.
 */
public class NetDataConstants {

    public static class INFO_EUME{

        public static final String HOSPITAL_INFO="/hospital";
        public static final String INHOSPITAL_INFO="/inhospital";
        public static final String POLICY_INFO="/policy";
        public static final String RIGHT_INFO="/right";

    }

    public static final String GET_VERSION="/app/getVersionAddress";

    public static final String GET_INFO="/app/getIntroduce";

    public static final String APP_INFO="/app/addAppLog";

    public static final String GET_DOCTOR_LIST="/app/getDoctorList";

    public static final String GET_NURSE_LIST="/app/getNurseList";

    public static final String GET_VIDEO_KIND_LIST="/app/getVideoKindList";

    public static final String GET_VIDEO_LIST_FROM_KIND="/app/getVideoListFromKind";

    public static final String GET_EXAMROOM_INFO="/app/getExamroomInfo";


    public static final String VALIDATE_ACCOUNT="/app/validateAccount";

    public static final String GET_MEAL_KINDS="/app/getMailKinds";//订餐类别

    public static final String GET_RENT_KINDS="/app/getRentKinds";//租赁类别

    public static final String GET_MEAL_LIST="/app/getMailList";//根据类别获取列表

    public static final String GET_RENT_LIST="/app/getRentList";//根据类别获取列表

    public static final String MEAL_ORDER="/app/mailOrder";//使用订餐ID和住院号订餐

    public static final String RENT_ORDER="/app/rentOrder";//使用订餐ID和住院号订餐


    public static final String GET_WEATHER="/app/getWeather";

    public static final String GET_TV_LIST="/app/getTvList";//

    public static final String GET_FEED_LIST="/app/feedBackList";

    public static final String FEED_BACK="/app/feedBack";

    public static final String GET_ADVICE="/app/getAdvice";

    public static final String GET_COMPLAIN="/app/getComplainList";

    public static final String SEND_COMPLAIN="/app/sendComplain";

    public static final String GET_FEE="/app/getFee";

    public static final String SEARCH_PRICE="/app/searchPrice";

    public static final String GET_PERSIONAL="/app/getPersional";


    public static final String GET_AD_DATA="/app/getAdData";//

    public static final String GET_MSG_DATA="/app/getMsgData";//

    public static final String GET_MAX_VOLUME="/app/getMaxVolume";

    public static final String GET_ONOFF_TIME="/app/getOnOffTime";

}

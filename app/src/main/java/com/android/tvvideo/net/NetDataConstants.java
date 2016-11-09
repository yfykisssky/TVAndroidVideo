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

    public static final String PROJECT_NAME="/videoedu";

    public static final String ALL_SERVER_URL=PROJECT_NAME+"/app/";

    public static final String GET_VERSION=ALL_SERVER_URL+"getVersionAddress";

    public static final String GET_INFO=ALL_SERVER_URL+"getIntroduce";

    public static final String APP_INFO=ALL_SERVER_URL+"addAppLog";

    public static final String GET_DOCTOR_LIST=ALL_SERVER_URL+"getDoctorList";

    public static final String GET_NURSE_LIST=ALL_SERVER_URL+"getNurseList";

    public static final String GET_VIDEO_KIND_LIST=ALL_SERVER_URL+"getVideoKindList";

    public static final String GET_VIDEO_LIST_FROM_KIND=ALL_SERVER_URL+"getVideoListFromKind";

    public static final String GET_EXAMROOM_INFO=ALL_SERVER_URL+"getExamroomInfo";

    public static final String GET_TV_LIST=ALL_SERVER_URL+"getTvList";

    public static final String GET_AD_DATA=ALL_SERVER_URL+"getAdData";

    public static final String GET_MSG_DATA=ALL_SERVER_URL+"getMsgData";

    public static final String GET_MAX_VOLUME=ALL_SERVER_URL+"getMaxVolume";

    public static final String VALIDATE_ACCOUNT=ALL_SERVER_URL+"validateAccount";

    public static final String GET_MEAL_KINDS=ALL_SERVER_URL+"getMailKinds";

    public static final String GET_RENT_KINDS=ALL_SERVER_URL+"getRentKinds";

    public static final String GET_MEAL_LIST=ALL_SERVER_URL+"getMailList";

    public static final String GET_RENT_LIST=ALL_SERVER_URL+"getRentList";

    public static final String MEAL_ORDER=ALL_SERVER_URL+"mailOrder";

    public static final String RENT_ORDER=ALL_SERVER_URL+"rentOrder";

    public static final String GET_WEATHER=ALL_SERVER_URL+"getWeather";

    public static final String GET_SATISFACTION_LIST=ALL_SERVER_URL+"feedSatisfactionList";

    public static final String SATISFACTION_FEED_BACK=ALL_SERVER_URL+"satisfactionFeedBack";

    public static final String GET_COMPLAIN_LIST=ALL_SERVER_URL+"getComplainList";

    public static final String SEND_COMPLAIN=ALL_SERVER_URL+"sendComplain";

    public static final String GET_ADVICE=ALL_SERVER_URL+"getAdvice";

    public static final String GET_FEE=ALL_SERVER_URL+"getFee";

    public static final String SEARCH_PRICE=ALL_SERVER_URL+"searchPrice";

    public static final String GET_PERSIONAL=ALL_SERVER_URL+"getPersional";

    public static final String GET_ONOFF_TIME=ALL_SERVER_URL+"getOnOffTime";

    public static final String STATIS_VIDEO=ALL_SERVER_URL+"videoStatistical";

    public static final String GET_SYS_TIME=ALL_SERVER_URL+"getCurrentTime";

}

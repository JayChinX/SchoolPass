package com.yuanding.schoolpass.utils;


import com.yuanding.schoolpass.A_0_App;

/**
 * 
* @ClassName: AppStrStatic
* @Description: TODO(App静态配置)
* @author Jiaohaili 
* @date 2015年11月2日 下午4:35:45
*
 */
public class AppStrStatic {

	public static final String PACKAGE_NAME = "com.yuanding.schoolpass";////备用调用的异常包名
    public static final String VERSION_NAME = "2.5.2";//备用调用的异常版本号
    public static final int VERSION_CODE = 20170501;//备用调用的异常版本Code
	/**************************这里存放本APP中用到的常量*********************/
	//本地测试环境
	public static final String SERVER_LINE_TEST_ENVIRONMENT = "http://192.168.50.221:6060/V6/";
	//线上正式环境
	public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT = "https://api.stu.weixiaobang.net/V6/";
    //预上线环境
//    public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT = "https://test.stu.weixiaobang.net/V6/";
	
	/****************************************帮帮URL*************************************************/
	//本地测试环境//啊本机http://192.168.30.238:7003/heo-api/m/api.do//线下http://192.168.50.32:8080/heo-api/m/api.do
	public static final String SERVER_LINE_TEST_ENVIRONMENT_BANGBANG= "http://192.168.50.32:8080/heo-api/m/api.do";
	//线上正式环境
	public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT_BANGBANG = "https://heoapi.weixiaobang.com/heo-api/m/api.do";
    //预上线环境
//    public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT_BANGBANG = "https://heoapi.weixiaobang.com/heo-api/m/api.do";
	
	//Socket 配置修改   注意！！
    public static final String HOST_ONLINE = "211.67.168.36";// 服务host外：211.67.168.36
    public static final String HOST_ONOFF = "192.168.50.221";// 服务host内：192.168.50.221
    public static final int PORT = 9502;// 服务端口
	    
    //Xutils 
    public final static int XUTILS_DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s请求超时时间,()
    public final static int XUTILS_INCREASE_CONN_TIMEOUT = 1000 * 30; // 30s请求超时时间,()
    
	//推送配置
//	public static final String API_KEY_BAI_DU_PUSH = "BHVIFdlVGNkuZAhLeW6r2PAt";//百度推送线下
	public static final String API_KEY_BAI_DU_PUSH = "XEhpx7KfbiGGeot9WEWug2tY";//百度推送线上
	public static final String API_KEY_YOU_MENG_TONG_JI = "56711d95e0f55aeac90036a5";//友盟统计
    public static final String API_WEIXIN_SHARE_APP_ID = "wx1a624091949895b1";//微信分享app_id
    public static final String API_WEIXIN_SHARE_APP_SECRET = "fcd3e337c8e6fcfebb4353861b1d2b32";//微信分享app_screct
    
	//SD卡图片缓存目录
	public static final String SD_PIC_CACHE = "SchoolPass/Cache";
	//SD卡缓存总目录
	public static final String SD_PIC = "/SchoolPass";
    
	//SP缓存目录
	public static final String SHARE_PREFER_USER_INFO = "school_pass_user_info";
	public static final String SHARE_PREFER_USER_PHONE = "school_pass_user_phone";

	public static final String SHARE_PREFER_COURSE_BG = "school_pass_course_bg";
	public static final String KEY_SHARE_PREFER_COURSE_BG = "key_school_pass_course_bg";
	
	public static final String USER_LOGIN_TOKEN = "user_login_token";
	public static final String USER_LOGIN_UNIQID = "user_login_uniqid";
	public static final String USER_LOGIN_QUNIQID = "user_login_quniqid";
	public static final String USER_LOGIN_QUTOKEN = "user_login_qutoken";
	public static final String USER_LOGIN_PHONE = "user_login_phone";
	public static final String USER_LOGIN_STATUS = "user_login_status";
	public static final String USER_LOGIN_NAME = "user_login_name";
	public static final String USER_LOGIN_POR_URL = "user_login_por_url";
	public static final String USER_BANG_DOU_URL = "user_bangdou_url";
    public static final String USER_COUPON_USE_URL = "user_coupon_use_url";
    public static final String BANG_USER_COUPON_URL = "bang_coupon_url";
	public static final String SHARE_PREFER_USER_ATTDENCE = "school_pass_user_attdence";
	
    public static final String USER_LOGIN_CHANNEL_ID = "user_login_channel_id";
    public static final String KEY_USER_LOGIN_CHANNEL_ID = "key_user_login_channel_id";
    public static final String KEY_USER_LOGIN_CLIENT_ID = "key_user_login_client_id";
    
    public static final String SHARE_PREFER_USER_PUSH_NOTROUBLE = "school_pass_user_notrouble";
    public static final String KEY_SHARE_PREFER_USER_PUSH_NOTROUBLE = "school_pass_user_notrouble_values";
    
    public static final String KEY_SHARE_PREFER_UPDATE_TIME = "school_pass_user_update_time";
    
    /***************固定的url链接和服务电话**************************/
    //用户注册协议
    public static final String LINK_USER_REGEDIT = "https://h5.weixiaobang.net/agreement.html";
    //用户运行权限设置
    public static final String LINK_USER_JURISDICTION = "https://h5.weixiaobang.net/android/models/index.html?type="; 
    //用户产品介绍
    public static final String LINK_USER_INTRODUCTION = "https://h5.weixiaobang.net/about/products.html";
    //用户常见问题
    public static final String LINK_USER_QUESTION = "https://h5.weixiaobang.net/about/commonproblem.html";
    
    //考勤帮助
    public static final String LINK_USER_ATTDENCE_HELP = "https://h5.weixiaobang.net/help/attendance_help2.html";
  //帮帮问题
    public static final String LINK_USER_BEFRIEND_HELP = "https://h5.weixiaobang.net/appbang/doc/index.html";
    //帮帮用户协议
    public static final String LINK_USER_BEFRIEND_HELP_AGREEMENT = "https://h5.weixiaobang.net/appbang/useragreement.html";
   //优惠券中心
    public static String USER_COUPON_URL="";

    /***************密码长度设置**************************/
    public static final int PWD_MIN_LENGTH = 6;//密码最小长度
    public static final int PWD_MAX_LENGTH = 24;//密码最大长度
    
	/***************公共请求参数**************************/
	
	public static final String ZCODE = "zcode";
	public static final String ZCODE_VALUE = "yuanding";
	public static final String SIGN = "sign";
	
	/***************用户状态配置**************************/
	public static final String USER_ROLE_UNDER_REVIEW = "0";//审核中
	public static final String USER_ROLE_INACTIVATED = "1";//未激活 (库中)
	public static final String USER_ROLE_HAVA_CERTIFIED = "2";//已认证
	public static final String USER_ROLE_LOCKED = "3";//已锁定
	public static final String USER_ROLE_NEW_USER = "4";//新用户
	public static final String USER_ROLE_AUDIT_FAILURE = "5";//审核失败
	public static final String USER_ROLE_NO_SUPPLEMENTARY_INFO = "6";//未补充资料
	
	/***************启动页面********************/
	public static final String HelpIntro = "start_intro";
	public static final String SHARE_APP_DATA = "shareAppData";
	
	public static final String HAVA_USER_TOKEN = "have_user_token";
	public static final String ENTER_ZHIBO_ACY_TYPE = "enter_zhibo_type";
	public static final String TAG_USER_IS_DELETE = "tag_user_is_delete";
	
	//课程表
    public static final String SHARE_COURSE_SEMESTER_STATUS = "share_course_semester_status";
    
	//发送微校邦验证短信的号码
	public static final String SEND_YANZHENG_MESSAGE_NO = "10690365714664486104";
	public static final int SEND_YANZHENG_MESSAGE_COUNT = 6;//位数
	
	//短信发送等待时间间隔单位为秒
	public static final int message_interval = 60;// 数组时间间隔
	//两次点击按钮_时间间隔单位为秒
    public static final int interval_double_click = 2000;
	
    //登录点击按钮和评论时间间隔（单位秒）
    public static final int INTERVAL_LOGIN_TIME = 2;
    public static final int INTERVAL_COMMENT_TIME = 10;
    public static final int INTERVAL_RONGYUN_CONNECT = 5;
    
    //评论最多和最少字数的限制
    public static final int WORD_COMMENT_MAX_LIMIT = 150;
    public static final int WORD_COMMENT_MIN_LIMIT = 3;
    
    public static final int INTERVAL_ATTDENCE_TIME = 3;
    public static final int cache_key_side_attdence_time = 3600 * 10;
    
    //首页消息固定项目消息ID
    public static final String TAG_MESSAGE_ID_ASSISTANT = "tag_message_id_assistant";
    public static final String TAG_MESSAGE_ID_OFFICIAL = "tag_message_id_official";
    public static final String TAG_MESSAGE_ID_ATTENCE = "tag_message_id_attence";
    public static final String TAG_MESSAGE_ID_GROUP = "tag_message_id_group";
    
	/***************缓存key值********************/
    //依据网络判断读取本地缓存或者服务器，无缓存时间限制
  	public static final String cache_key_side_found_found = "student_side_found_found__key";
  	public static final String cache_key_side_found_acy = "student_side_found_acy__key";
  	public static final String cache_key_side_found_my = "student_side_found_my __key";
  	public static final String cache_key_repair_detail = "student_repair_detail __key";
  	public static final String cache_key_repair_all = "student_repair_all __key";
  	public static final String cache_key_repair_my = "student_repair_my __key";
    public static final String cache_key_score = "student_score__key";
	public static final String cache_key_lecture_detail = "student_lecture_detail_key";
	public static final String cache_key_lecture = "student_lecture__key";
	public static final String cache_key_acy = "student_acy__key";
	public static final String cache_key_acy_detail = "student_acy_detail_key";
	public static final String cache_key_colleague = "student_colleague_key";
	public static final String cache_key_teacher = "student__key";
	public static final String cache_key_school_in = "student_school_in_key";
    public static final String cache_key_person_info = "student_person_info_key";
    public static final String cache_key_notice_sent = "student_notice_sent_key";
    public static final String cache_key_group_info = "student_group_info_key";
    public static final String cache_key_notice_detail = "student_notice_detail_key";
    public static final String cache_key_notice_unsent = "student_notice_unsent_key";
    public static final String cache_key_notice_draft = "student_notice_draft_key";
    
    public static final String cache_key_notice_list = "student_notice_list_key";
    public static final String cache_key_notice_list_waitsure = "student_notice_list_key_waitsure";
    public static final String cache_key_notice_detail_text = "student_notice_detail_text_key";
    public static final String cache_key_schoolasstistant_list = "student_schoolasstistant_list_key";
    public static final String cache_key_official_list = "student_official_list_key";
    public static final String cache_key_notice_official_detail_text = "student_notice_official_detail_text_key";
    public static final String cache_key_schoolassistant_detail_text = "student_schoolassistant_detail_text_key";
    public static final String cache_key_mess_detail = "student_mess_detail__key";
    public static final String cache_key_mess_detail_sys = "student_mess_detail_sys_key";
    public static final String cache_key_out_school_click ="student_out_school_click_key";
    public static final String cache_key_b_side_course ="b_side_course_click_key";
    public static final String cache_key_student_side_attdence="student_side_attdence_click_key";
    
    public static final String cache_key_attdence_detail = "student_side_attdence_detail_key"; 
    public static final String cache_key_invite_record = "student_side_invite_record_key"; 
    public static final String cache_key_invite_main = "student_side_invite_main_key";       
    public static final String cache_key_info = "student_info__key";    
    public static final String cache_key_info_detail = "student_info_detail_key";   
    public static final String cache_key_sysnotice = "student_attdence_sysnotice";
    public static final String cache_key_personal_statistics="student_attdence_personal_statistics";
    public static final String cache_key_attence_contact="student_attence_contact";
    public static final String cache_key_mess_school_assistant_main = "b_mess_school_assistant_main";
    
    //BANGBANG
    public static final String cache_key_acc_befriend_center_main = "b_acc_befriend_center_main_key";
    public static final String cache_key_query_history_release_task = "query_history_release_task_key";
    public static final String cache_key_query_release_task = "query_release_task_key";
    public static final String cache_key_query_history_acquire_task = "query_history_acquire_task_key";
    public static final String cache_key_query_acquire_task = "query_acquire_task_key";
    public static final String cache_key_person_bill = "person_bill_key";
    
    
	//不根据网络判断，先读取本地缓存，再访问服务器，无缓存时间限制
	public static final String cache_key_my_message = "student_my_message_key";
	public static final String cache_key_mySide = "student_mySide_key";
	public static final String cache_key_my_account = "student_my_account_key";
	
    //有网：读取接口获得最新封面，无网：直接，读取本地缓存
    public static final String cache_key_student_A_1_Start_Acy = "student_A_1_Start_Acy";
    
    //依据网络判断读取本地缓存或者服务器，缓存时间限制为cache_key_side_attdence_time 10H
    public static final String cache_key_side_attdence_list="student_attdence_list";
    
    //支付配置最小金额
    public static int PAY_MONEY = 0;

    /*****************************************动态提示语变量***************************************************/

    //微信分享内容和链接
    public static String default_ShareTitle = "下载"+ A_0_App.APP_NAME+"APP 500邦豆等你来领！";
    public static  String default_ShareContent = A_0_App.APP_NAME+" 我的校园微生活";
    public static String default_shareInviteUrl = "http://www.weixiaobang.com/index.php?m=mobile&c=mobile&a=download";    //微信分享默认的链接

    //短信分享内容
    public static String sms_share_content = "小伙伴邀请你来体验"+A_0_App.APP_NAME+"APP，官方通知、课程表、老师同学通讯录一个"+A_0_App.APP_NAME+"全搞定，还有新用户大礼，快快下载吧！http://t.cn/RVSxTPk";//短信分享默认的内容

    public static String put_title_exit() {
        return "确定退出"+ A_0_App.APP_NAME+"吗?";
    }
    public static String str_about_copyright_bottom() {
        return "Copyright © 2017 weixiaobang.com";
    }
    public static String kicked_offline() {
        return "你的"+A_0_App.APP_NAME+"号在另一个设备登录。如果这不是你本人的操作，你的"+A_0_App.APP_NAME+"账号可能已经泄漏，请及时修改密码";
    }
    public static String str_about_weinxin_number() {
        return "Wei_Xiao_Bang";
    }
    public static String str_app_weixin_copyurlsuccess() {
        return "微信号\"Wei_Xiao_Bang\"已复制到剪切板，请到微信搜索关注！";
    }
    public static String str_regedit_agree_title() {
        return A_0_App.APP_NAME+"注册协议";
    }
    public static String str_register_text() {
        return "注册"+A_0_App.APP_NAME;
    }
    public static String str_guide_not_certified() {
        return "欢迎注册"+A_0_App.APP_NAME+"，您是未认证用户，部分应用暂无使用权限，请先到个人中心提交个人认证资料，等待审核。";
    }
    public static String str_guide_certified() {
        return "欢迎注册"+A_0_App.APP_NAME+"，请到个人中心审核您的个人资料，以便日后准确接收院系、班级的校务信息。";
    }
    public static String str_my_account_invite_defaultsharetitle() {
        return "下载"+A_0_App.APP_NAME+"APP 500邦豆等你来领！";
    }
    public static String str_my_account_invite_defaultsharecontent() {
        return A_0_App.APP_NAME+" 我的校园微生活";
    }


}

package com.yuanding.schoolpass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushConstants;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_A0_Main;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_C1_Task_Status_Details;
import com.yuanding.schoolpass.bean.Cpk_Device_Info;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_GetStartupPic;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.LogUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * @ClassName: A_0_1_Start_Acy
 * @Description: TODO(启动页面)
 * @author Jiaohaili
 * @date 2015年11月2日 下午3:11:57
 */
public class A_1_Start_Acy extends Activity {

    private static final int DELAY_REQUEST_PIC = 4;//设置请求数据时间
    private static final int DELAY_SUCCESS_PIC_URL = 5;//设置成功获取图片停留时间
	private SharedPreferences mSharePre;
    public  ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageView mIVTopImage;
    private Button btn_go_over;
    
    private ACache maACache;
    private JSONObject jsonObject;
    
    private View rootView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PermissionGen.needPermission(A_1_Start_Acy.this, REQUECT_CODE_SDCARD,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!MPermissions.shouldShowRequestPermissionRationale(A_1_Start_Acy.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else{
            if (!MPermissions.shouldShowRequestPermissionRationale(A_1_Start_Acy.this, Manifest.permission.RECORD_AUDIO, REQUECT_CODE_RECORD_AUDIO)) {
                MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
            }else{
                if (!MPermissions.shouldShowRequestPermissionRationale(A_1_Start_Acy.this, Manifest.permission.CAMERA, REQUECT_CODE_CAMERA)) {
                    MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
                }else{
                    if (!MPermissions.shouldShowRequestPermissionRationale(A_1_Start_Acy.this, Manifest.permission.READ_PHONE_STATE, REQUECT_READ_PHONESTATE)) {
                        MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
                    }else{
                        toDoAction();
                    }
                }
            }
        }
    }
	
    private void readCache() {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_student_A_1_Start_Acy+A_0_App.USER_UNIQID);
        if (jsonObject != null) {// 说明有缓存
            String pic = null;
            try {
                pic = jsonObject.getString("pic");
                goShowPicTimer(pic);
                logD(pic + "学生——加载缓存起屏Pic路径");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            loadAnimal(rootView);
        }
    }
	   
    private void readPicData() {
        handler.post(updateThread);// 开始计时
        A_0_App.getApi().getStartupPic(new Inter_GetStartupPic() {

            @Override
            public void onSuccess(String pic) {
                if (isFinishing() || delayTime) {
                    return;
                }
                handler.removeCallbacks(updateThread);
                goShowPicTimer(pic);
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (isFinishing() || delayTime) {
                    return;
                }
                handler.removeCallbacks(updateThread);
                readCache();
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    private void goShowPicTimer(String pic) {
        if (pic.length() > 0 &&!pic.equals("")) {
			if(mIVTopImage.getTag() == null){
			    PubMehods.loadServicePic(imageLoader, pic,mIVTopImage, options);
			    mIVTopImage.setTag(pic);
			}else{
			    if(!mIVTopImage.getTag().equals(pic)){
			        PubMehods.loadServicePic(imageLoader, pic,mIVTopImage, options);
			        mIVTopImage.setTag(pic);
			    }
			}
            handler_success.post(updateThread_success);// 开始显示时间
        }
    }

	//显示动画
	private void loadAnimal(View view) {
//        AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
//        aa.setDuration(DELAY_SHOW_ANIMAL * 1000);
//        view.startAnimation(aa);
//        aa.setAnimationListener(new AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation arg0) {
                showIntroduction();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//        });
    }
	
	// 展示简介页面
    private void showIntroduction() {

//        if (!mSharePre.getBoolean("showIntro", false)) {
//            startAcy(A_1_Start_Acy.this, A_2_Intro_Acy.class);
//        } else {
            // 登录
//            if (null != A_0_App.USER_TOKEN && !"".equals(A_0_App.USER_TOKEN)) {
//                startAcy(A_1_Start_Acy.this, A_Main_Acy.class);
//            } else {
//                startAcy(A_1_Start_Acy.this, A_3_0_Login_Acy.class);
//            }
//        }
    	
    	
//    	if (null != A_0_App.USER_TOKEN && !"".equals(A_0_App.USER_TOKEN)) {
//            startAcy(A_1_Start_Acy.this, A_Main_Acy.class);
//        } else {
//            startAcy(A_1_Start_Acy.this, A_3_0_Login_Acy.class);
//        }
    	
    	
    	if (null != A_0_App.USER_TOKEN && !"".equals(A_0_App.USER_TOKEN)) {
    	    pushOperating(A_1_Start_Acy.this,A_Main_Acy.class);
        } else {
        	/**
        	 * 未登录 先登录 登录完了以后 再判断是点击桌面图标登录进来的 还是点推送登录的 
        	 */        	
            startAcy(A_1_Start_Acy.this, A_3_0_Login_Acy.class);
        }
    }

	private void startAcy(Context packageContext, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(packageContext, cls);
		startActivity(intent);
//		overridePendingTransition(R.anim.animal_slide_in_bottom_normal, R.anim.animal_push_left_out_normal);
		overridePendingTransition(R.anim.animal_push_left_in_normal, R.anim.animal_push_left_out_normal);
		finish();
	}

	// 获得屏幕的尺寸
	private void getScreenSize() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		A_0_App.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
		A_0_App.screenHeigh = metric.heightPixels; // 屏幕高度（像素）
		getDevice_Info();
	}

    // 获得设备信息
    private void getDevice_Info() {
        try {
            TelephonyManager mTm = (TelephonyManager) getApplicationContext().getSystemService(
                    TELEPHONY_SERVICE);
            String imei = mTm.getDeviceId();

            Cpk_Device_Info device = new Cpk_Device_Info();
            device.setCpu(PubMehods.compatibleCPU());
            String pinPai = android.os.Build.BRAND;
            if (pinPai == null)
                pinPai = "";
            device.setPinpai(pinPai);

            String xingHao = android.os.Build.MODEL;
            if (xingHao == null)
                xingHao = "";
            device.setXinghao(xingHao);

            String version = android.os.Build.VERSION.RELEASE;
            if (version == null)
                version = "";
            device.setVerison(version);

            if (imei == null)
                imei = "";
            device.setImei(imei);
            logE(device.getCpu()+"," +device.getPinpai()+","+device.getXinghao()+","+device.getVerison()+","+device.getImei());
            A_0_App.getInstance().setDevice_info(device);
        } catch (Exception e) {
            logE("获取设备信息失败");
        }
    }	   
      
    /********************************************************************************/
    /*
     * 计时器,展示图片广告的倒计时
     */
    private int timer_success = DELAY_SUCCESS_PIC_URL;
    Handler handler_success = new Handler();
    Runnable updateThread_success = new Runnable() {
        public void run() {
            btn_go_over.setText("跳过\n" + timer_success + "S");
            handler_success.postDelayed(updateThread_success, 1000);
            if (timer_success == 0) {
                loadAnimal(rootView);
                handler_success.removeCallbacks(updateThread_success);
            } else {
                btn_go_over.setText("跳过\n" + timer_success + "S");
                timer_success--;
                btn_go_over.setVisibility(View.VISIBLE);
            }
        }
    };
    
    private void cancel_handler_success() {
        handler_success.removeCallbacks(updateThread_success);
        loadAnimal(rootView);
    }
    /********************************************************************************/
    /*
     * 计时器,超过时间设置没有反应，执行此动作
     */
    private boolean delayTime;
    private int timer = DELAY_REQUEST_PIC;

    Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            handler.postDelayed(updateThread,1000);
            if (timer == 0) {
                delayTime = true;
                readCache();
                handler.removeCallbacks(updateThread);
            } else {
                timer--;
            }
        }
    };
    
    //推送操作  起屏、登录页面
    public static void pushOperating(Context context,Class<?> cls) {
        /**
         * 如果已经登录 就判断是不是点击推送进来的
         */
         Intent intent = new Intent(); 
         String reqpagetype = A_0_App.getClickPushMsgReqPage();
         if(!"".equals(reqpagetype) && !reqpagetype.isEmpty()){
             /**
              * 点击推送进来的 跳到推送详情页
              */
             if(reqpagetype.equals("m1")){
                 intent.setClass(context, B_Mess_Notice_Detail.class);
                 intent.putExtra("acy_type", 3);
             }else if(reqpagetype.equals("m2")){
                 intent.setClass(context, B_Mess_Notice_Detail_MessText.class);  
                 intent.putExtra("acy_type", 3);
             }else if(reqpagetype.equals("m3")){
                 intent.setClass(context, B_Mess_Notice_Detail_Sys.class);
                 intent.putExtra("acy_type", 3);
             }else if(reqpagetype.equals("i1")){
                 intent.setClass(context, B_Mess_Notice_Detail.class);
                 intent.putExtra("message_id", A_0_App.getInstance().getLink_id());
                 intent.putExtra("acy_type", 4);
             }else if(reqpagetype.equals("i2")){
                 intent.setClass(context, B_Mess_Notice_Detail_MessText.class);  
                 intent.putExtra("message_id", A_0_App.getInstance().getLink_id());
                 intent.putExtra("acy_type", 4);
             }else if(reqpagetype.equals("i3")){
                 intent.setClass(context, B_Mess_Notice_Detail_Sys.class);
                 intent.putExtra("message_id", A_0_App.getInstance().getLink_id());
                 intent.putExtra("acy_type", 4);
             }else if(reqpagetype.equals("ac1")){
                 intent.setClass(context, B_Side_Acy_list_Detail_Acy.class);
                 intent.putExtra("acy_type", 3);
             }else if(reqpagetype.equals("l1")){
                 intent.setClass(context, B_Side_Lectures_Detail_Acy.class);
                 intent.putExtra("acy_type", 3);
             }else if(reqpagetype.equals("at1")){
                 intent.setClass(context, B_Mess_Attdence_Main_0_Acy.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("in1")){
                 intent.setClass(context, B_Side_Info_1_Detail_Acy.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("of1")){
                 intent.setClass(context, B_Mess_Notice_Detail_Official_News.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("as1")){
                 intent.setClass(context, B_Mess_School_Assistant_1_Detai_Acy.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("index1")){
                 intent.setClass(context, B_Side_Befriend_A0_Main.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("info1")){
                 intent.setClass(context, B_Side_Befriend_C1_Task_Status_Details.class);
                 intent.putExtra("acy_type", 1);//推送进入
                 intent.putExtra("type", 3);
             }else if(reqpagetype.equals("info2")){
                 intent.setClass(context, B_Side_Befriend_C1_Task_Status_Details.class);
                 intent.putExtra("acy_type", 1);//推送进入
                 intent.putExtra("type", 4);
             }else if(reqpagetype.equals("withdraw1")){
                 intent.setClass(context, B_Account_Befriend_Center_Main_1_PresentBind.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("sc1")){
                 intent.setClass(context, B_Side_Course_Acy.class);
                 intent.putExtra("acy_type", 1);//推送进入
             }else if(reqpagetype.equals("coupon1")){
                 intent.setClass(context, Pub_WebView_Load_Coupon.class);
                 intent.putExtra("url_text", A_0_App.getInstance().getCoupon_url()+A_0_App.USER_TOKEN);
             }else if(reqpagetype.equals("leave1")){
                 intent.setClass(context, Pub_WebView_Banner_Acy.class);
                 intent.putExtra("acy_type", 4);//推送进入
                 intent.putExtra("url_text", A_0_App.getInstance().getLeave_detail_url()+A_0_App.USER_TOKEN);
             }
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             A_0_App.setClickPushMsgReqPage("");
             context.getApplicationContext().startActivity(intent);
         }else{
             /**
              * 正常点击桌面图标进来的 直接跳到首页
              */
             intent.setClass(context, cls);
             context.startActivity(intent);
             ((Activity) context).overridePendingTransition(R.anim.animal_push_left_in_normal, R.anim.animal_push_left_out_normal);
             ((Activity) context).finish();
         }
    }

    /*****************************************请求权限和切换网络********************************************************/

    public void toDoAction(){//程序启动开始申请SD卡权限
        A_0_App.getInstance().switchNetSetting();
        try {
            com.baidu.android.pushservice.PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,AppStrStatic.API_KEY_BAI_DU_PUSH);
            PushManager.getInstance().initialize(this.getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("test", "baidu push error");
            Log.e("test", "ge tui error");
        }
        if (A_0_App.getInstance().isRuning()) {
            finish();
            return;
        }
        rootView = View.inflate(this, R.layout.activity_start, null);
        mIVTopImage = (ImageView) rootView.findViewById(R.id.image_Top);
        btn_go_over = (Button) rootView.findViewById(R.id.btn_go_over);
        setContentView(rootView);

        A_0_App.getInstance().addActivity(this);
        mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_APP_DATA, 0);

        imageLoader = A_0_App.getInstance().getimageLoader();
        readPicData();

        if (A_0_App.SERVER_REQUEST_BASE_URL.equals(AppStrStatic.SERVER_FARMAL_ON_LINE_ENVIRONMENT)) {
            logE("--线上版本--");
        } else {
            PubMehods.showToastStr(A_1_Start_Acy.this, "--线下--局域网段---");
            logE("--线下--局域网段---");
        }

        getScreenSize();
        options = A_0_App.getInstance().getOptions(0,0,0);
        btn_go_over.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancel_handler_success();
            }
        });

        A_0_App.setAfterScreenPage(true);
    }


    private static final int REQUECT_CODE_SDCARD = 11;//内存卡
    private static final int REQUECT_CODE_RECORD_AUDIO = 12;//麦克风
    private static final int REQUECT_CODE_CAMERA = 13;//拍照
    private static final int REQUECT_READ_PHONESTATE = 14;//拍照
    private String string_sdcard = "存储卡";
    private String string_record_audio = "麦克风";
    private String string_camera = "拍照";
    private String string_read_phone_state = "手机识别码";

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess()
    {
        MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
    }

    @PermissionGrant(REQUECT_CODE_RECORD_AUDIO)
    public void requestRecordAudioSuccess()
    {
        MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
    }

    @PermissionGrant(REQUECT_CODE_CAMERA)
    public void requestCameraSuccess()
    {
        MPermissions.requestPermissions(A_1_Start_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
    }

    @PermissionGrant(REQUECT_READ_PHONESTATE)
    public void requestPhoneStateSuccess()
    {
        toDoAction();
    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        showTitleDialog(string_sdcard);
    }

    @PermissionDenied(REQUECT_CODE_RECORD_AUDIO)
    public void requestRecordAudioFailed() {
        showTitleDialog(string_record_audio);
    }

    @PermissionDenied(REQUECT_CODE_CAMERA)
    public void requestCameraFailed() {
        showTitleDialog(string_camera);
    }

    @PermissionDenied(REQUECT_READ_PHONESTATE)
    public void requestPhoneStateFailed() {
        showTitleDialog(string_read_phone_state);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_SDCARD)
//    public void requestSdcardSuccess() {
//        PermissionGen.needPermission(A_1_Start_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_RECORD_AUDIO)
//    public void requestRecordAudioSuccess() {
//        PermissionGen.needPermission(A_1_Start_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_CAMERA)
//    public void requestCameraSuccess() {
//        toDoAction();
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_SDCARD)
//    public void requestSdcardFailed() {
//        showTitleDialog(string_sdcard);
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_RECORD_AUDIO)
//    public void requestRecordAudioFailed() {
//        showTitleDialog(string_record_audio);
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_CAMERA)
//    public void requestCameraFailed() {
//        showTitleDialog(string_camera);
//    }

    public void showTitleDialog(String permissionName) {
        final GeneralDialog upDateDialog = new GeneralDialog(A_1_Start_Acy.this,
                R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.str_permission_request);
        upDateDialog.setContent(getPermessionStr(permissionName));
        upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
                A_0_App.getInstance().clearUserSpInfo(false);
                finish();
            }
        });
        upDateDialog.showRightButton(R.string.pub_go_set, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(localIntent);
                upDateDialog.cancel();
                finish();
            }
        });
        upDateDialog.setCancelable(false);
        upDateDialog.setCanceledOnTouchOutside(false);
        upDateDialog.show();
    }

    public String getPermessionStr(String permessName) {
        return "在设置 - 应用 - " + getString(R.string.app_name) + " - 权限中开启" + permessName + "权限，以正常使用"+A_0_App.APP_NAME+"相关功能服务";

    }

    /*****************************************请求权限和切换网络********************************************************/

    @Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static void logD(String msg) {
		LogUtils.logD("A_1_Start_Acy", msg);
	}

	public static void logE(String msg) {
		LogUtils.logE("A_1_Start_Acy", msg);
	}
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
	}
	
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);}
}

package com.yuanding.schoolpass;

import java.util.HashMap;

import android.Manifest;
import android.app.ActivityGroup;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.utils.DensityUtils;
import com.yuanding.schoolpass.utils.LogUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

/**
 * 
* @ClassName: A_Main_Activicy
* @Description: TODO(主页面)
* @author Jiaohaili 
* @date 2015年11月2日 下午8:23:15
*
 */

public class A_Main_Acy extends ActivityGroup {

	private A_0_App mApp = null;
	
	public static enum ActivityID {STARTHOME, PROMATE, ACCOUNT,MORE};
	
	private RelativeLayout mToolRlayoutSearcher = null;
	private RelativeLayout mToolRlayoutPromate = null;
	private RelativeLayout mToolRlayoutAccount = null;
	private RelativeLayout mToolRlayoutMore = null;
	private ImageView mIVSearcher = null;
	private ImageView mIVPromate = null;
	private ImageView mIVAccount = null;
	private ImageView mIVMore = null;
	private TextView mTVTextSearcher = null;
	private TextView mTVTextPromate = null;
	private TextView mTVTextAccount = null;
	private TextView mTVlTextMore = null;
	private TextView tv_index_mess_no_read_count = null;
	private ImageView mIV_Side_No_Read_Tag;
	private HashMap<String, Class> mActivity;
	private ActivityID mCurActivity = null;
	private FrameLayout mContainer = null;
	private LinearLayout lin = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mApp = (A_0_App) this.getApplication();
		mApp.setRuning(true);
		instance = this;
		A_0_App.getInstance().addActivity(this);
		
		mActivity = new HashMap<String, Class>();
		mContainer = (FrameLayout) findViewById(R.id.main_container);
		
		initActivity();
		initToolBarMainViews();
		UpdateView(ActivityID.STARTHOME);
		if (A_0_App.getInstance().getVersion() != null) {
			if (A_0_App.getInstance().getVersion().getVersionCode() == null
					|| A_0_App.getInstance().getVersion().getVersionCode().length() <= 0) {
				A_0_App.getInstance().checkUpdateVersion(instance);
			}
		}
	}
	
	private static A_Main_Acy instance;
	public static A_Main_Acy getInstance() {
		return instance;
	}
	
	private void initActivity() {
		mActivity.put(ActivityID.STARTHOME.toString(), A_Main_My_Message_Acy.class);
		mActivity.put(ActivityID.PROMATE.toString(), A_Main_My_Contact_Acy.class);
		mActivity.put(ActivityID.ACCOUNT.toString(),A_Main_My_Side_Acy.class);
		mActivity.put(ActivityID.MORE.toString(),A_Main_My_Account_Acy.class);
	}
	
	/*
	 * 调用、监听主窗口的工具栏
	 */
	private void initToolBarMainViews() {
		lin = (LinearLayout) findViewById(R.id.toolbar);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.toolbar_main, null);

		LinearLayout toolbarmain = (LinearLayout) layout.findViewById(R.id.toolbarmain);
		toolbarmain.setLayoutParams(new LayoutParams());
//		toolbarmain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		mToolRlayoutSearcher = (RelativeLayout) layout.findViewById(R.id.toolbar_seracher);
		mTVTextSearcher = (TextView) layout.findViewById(R.id.toolb_search_text);
		mIVSearcher = (ImageView) layout.findViewById(R.id.toolb_home);
		mToolRlayoutSearcher.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutPromate = (RelativeLayout) layout.findViewById(R.id.toolbar_promate);
		mTVTextPromate = (TextView) layout.findViewById(R.id.toolb_promate_text);
		mIVPromate = (ImageView) layout.findViewById(R.id.toolb_promate);
		mToolRlayoutPromate.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutAccount = (RelativeLayout) layout.findViewById(R.id.toolbar_account);
		mTVTextAccount = (TextView) layout.findViewById(R.id.toolb_account_text);
		mIVAccount= (ImageView) layout.findViewById(R.id.toolb_account);
		mToolRlayoutAccount.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutMore = (RelativeLayout) layout.findViewById(R.id.toolbar_more);
		mTVlTextMore = (TextView) layout.findViewById(R.id.toolb_more_text);
		mIVMore = (ImageView) layout.findViewById(R.id.toolb_more);
		mToolRlayoutMore.setOnClickListener(onToolbarMainClickListener);
		
		tv_index_mess_no_read_count =(TextView)layout.findViewById(R.id.tv_index_mess_no_read_count);
		mIV_Side_No_Read_Tag = (ImageView)layout.findViewById(R.id.iv_side_no_read_tag);
		lin.removeAllViews();
		lin.addView(layout);
	}
	
    // 首页未读消息数量展示
    public void showMainNoReadMessCount(int no_count) {
        if (tv_index_mess_no_read_count == null)
            return;
        if (no_count <= 0) {
            tv_index_mess_no_read_count.setVisibility(View.GONE);
        } else if (0 < no_count && no_count <= 99) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_message_noread);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this,30);
			layoutParams.setMargins(left,0, 0, 0);// 4个参数按顺序分别是左上右下
            tv_index_mess_no_read_count.setLayoutParams(layoutParams);
			tv_index_mess_no_read_count.setText(no_count + "");
        } else if (99 < no_count && no_count <= 999) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this,33);
			layoutParams.setMargins(left, 0, 0, 0);// 4个参数按顺序分别是左上右下
			tv_index_mess_no_read_count.setLayoutParams(layoutParams);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
            tv_index_mess_no_read_count.setText(no_count + "");
        } else if (no_count > 999) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this, 33);
			layoutParams.setMargins(left, 0, 0, 0);// 4个参数按顺序分别是左上右下
			tv_index_mess_no_read_count.setLayoutParams(layoutParams);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
            tv_index_mess_no_read_count.setText("999+");
        }
    }
    
    // 身边未读消息红点展示
    public void showSideNoReadTag(boolean visible) {
        if (visible) {
            int width = DensityUtils.dip2px(this, 12);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    width,width);
            int left = DensityUtils.dip2px(this, 38);
            int top = DensityUtils.dip2px(this, 10);
            layoutParams.setMargins(left, top, 0, 0);// 4个参数按顺序分别是左上右下
            mIV_Side_No_Read_Tag.setLayoutParams(layoutParams);
            mIV_Side_No_Read_Tag.setVisibility(View.VISIBLE);
        } else {
            mIV_Side_No_Read_Tag.setVisibility(View.GONE);
        }
    }
	
	private OnClickListener onToolbarMainClickListener = new View.OnClickListener() {

		public void onClick(View v) {
		    A_0_App.getInstance().checkUpdateVersion(instance);
			switch (v.getId()) {
			case R.id.toolbar_seracher:
				UpdateView(ActivityID.STARTHOME);
				break;
			case R.id.toolbar_promate:
				UpdateView(ActivityID.PROMATE);
				break;
			case R.id.toolbar_account:
				UpdateView(ActivityID.ACCOUNT);
				break;
			case R.id.toolbar_more:
				UpdateView(ActivityID.MORE);
				break;
			}
		}
	};
	private RelativeLayout.LayoutParams layoutParams;
	
	public boolean UpdateView(ActivityID id) {
		Class activityClass = null;
		if ((activityClass = mActivity.get(id.toString())) == null)
			return false;
		if (!switchActivity(id))
			return false;

		mContainer.removeAllViews();
		Intent intent = new Intent(this, activityClass);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    View view = getLocalActivityManager().startActivity(id.toString(),intent).getDecorView();
		mContainer.addView(view);
		return true;
	}
	
	private boolean switchActivity(ActivityID id) {
		if (id == null || id == mCurActivity)
			return false;
		toolbarItemNormal(mCurActivity);
		toolbarItemSelect(id);
		mCurActivity = id;
		return true;
	}

	/*
	 * 工具栏列表正常状态
	 */
	private void toolbarItemNormal(ActivityID id) {
		if (id == null)
			return;
		switch (id) {
		case STARTHOME:
			if (null != mToolRlayoutSearcher) {
//				mToolRlayoutSearcher.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVSearcher.setBackgroundResource(R.drawable.ic_main_my_mess_nor);
				mTVTextSearcher.setTextColor(getResources().getColor(R.color.main_tool_text_nor));
			}
			break;
		case PROMATE:
			if (null != mToolRlayoutPromate) {
//				mToolRlayoutPromate.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVPromate.setBackgroundResource(R.drawable.ic_main_my_sch_nor);
				mTVTextPromate.setTextColor(getResources().getColor(R.color.main_tool_text_nor));
			}
			break;
		case ACCOUNT:
			if (null != mToolRlayoutAccount) {
//				mToolRlayoutAccount.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVAccount.setBackgroundResource(R.drawable.ic_main_my_side_nor);
				mTVTextAccount.setTextColor(getResources().getColor(R.color.main_tool_text_nor));
			}
			break;
		case MORE:
			if (null != mToolRlayoutMore) {
//				mToolRlayoutMore.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVMore.setBackgroundResource(R.drawable.ic_main_my_acc_nor);
				mTVlTextMore.setTextColor(getResources().getColor(R.color.main_tool_text_nor));
			}
			break;
		}
	}

	/*
	 * 工具栏列表选中状态
	 */
	private void toolbarItemSelect(ActivityID id) {
		if (id == null)
			return;
		switch (id) {
		case STARTHOME:
			if (null != mToolRlayoutSearcher) {
//				mToolRlayoutSearcher.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVSearcher.setBackgroundResource(R.drawable.ic_main_my_mess_pre);
				mTVTextSearcher.setTextColor(getResources().getColor(R.color.main_tool_text_pre));
			}
			break;
		case PROMATE:
			if (null != mToolRlayoutPromate) {
//				mToolRlayoutPromate.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVPromate.setBackgroundResource(R.drawable.ic_main_my_sch_pre);
				mTVTextPromate.setTextColor(getResources().getColor(R.color.main_tool_text_pre));
			}
			break;
		case ACCOUNT:
			if (null != mToolRlayoutAccount) {
//				mToolRlayoutAccount.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVAccount.setBackgroundResource(R.drawable.ic_main_my_side_pre);
				mTVTextAccount.setTextColor(getResources().getColor(R.color.main_tool_text_pre));
			}
			break;

		case MORE:
			if (null != mToolRlayoutMore) {
//				mToolRlayoutMore.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVMore.setBackgroundResource(R.drawable.ic_main_my_acc_pre);
				mTVlTextMore.setTextColor(getResources().getColor(R.color.main_tool_text_pre));
			}
			break;
		}
	}
	
    private void showToast(int id) {
        PubMehods.showToastStr(A_Main_Acy.this, getResources().getString(id));
    }
	
	@SuppressWarnings("main  destroy error")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.setRuning(false);
	}

    public static void logD(String msg) {
        LogUtils.logD("MainActivity", msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("MainActivity", msg);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                	
//                  参数为false代表只有当前activity是task根，指应用启动的第一个activity时，才有效;
//                  如果为true则忽略这个限制，任何activity都可以有效。
                	
                    moveTaskToBack(true);  
//                    final GeneralDialog upDateDialog = new GeneralDialog(A_Main_Acy.this,R.style.Theme_GeneralDialog);
//                    upDateDialog.setTitle(R.string.pub_title);
//                    upDateDialog.setContent(R.string.put_title_exit);
//                    upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            upDateDialog.cancel();
//                        }
//                    });
//                    upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            upDateDialog.cancel();
//                            A_0_App.getInstance().exit(true);
//                        }
//                    });
//                    upDateDialog.show();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

	@Override
	protected void onResume() {
		if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
			MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}else{
			if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.RECORD_AUDIO, REQUECT_CODE_RECORD_AUDIO)) {
				MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
			}else{
				if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.CAMERA, REQUECT_CODE_CAMERA)) {
					MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
				}else{
					if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.READ_PHONE_STATE, REQUECT_READ_PHONESTATE)) {
						MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
					}else{
//						toDoAction();
					}
				}
			}
		}
		super.onResume();
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
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
	}

	@PermissionGrant(REQUECT_CODE_RECORD_AUDIO)
	public void requestRecordAudioSuccess()
	{
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
	}

	@PermissionGrant(REQUECT_CODE_CAMERA)
	public void requestCameraSuccess()
	{
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
	}

	@PermissionGrant(REQUECT_READ_PHONESTATE)
	public void requestPhoneStateSuccess()
	{
//		toDoAction();
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
//        PermissionGen.needPermission(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_RECORD_AUDIO)
//    public void requestRecordAudioSuccess() {
//        PermissionGen.needPermission(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
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

	private GeneralDialog upDateDialog;

	public void showTitleDialog(String permissionName) {
		if (instance == null){
			System.exit(0);
			return;
		}
		if (!upDateDialog.isShowing()) {
			upDateDialog = new GeneralDialog(A_Main_Acy.this,
					R.style.Theme_GeneralDialog);
			upDateDialog.setTitle(R.string.str_permission_request);
			upDateDialog.setContent(getPermessionStr(permissionName));
			upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (upDateDialog != null)
						upDateDialog.cancel();
					android.os.Process.killProcess(android.os.Process.myPid());
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
	}

	public String getPermessionStr(String permessName) {
		return "在设置 - 应用 - " + getString(R.string.app_name) + " - 权限中开启" + permessName + "权限，以正常使用"+A_0_App.APP_NAME+"相关功能服务";

	}
}
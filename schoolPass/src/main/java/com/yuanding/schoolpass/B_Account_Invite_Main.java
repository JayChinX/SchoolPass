package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yuanding.schoolpass.B_Account_Invite_InviteRecord.InviteRecordAdapter;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Account_InviteInfo.MyInviterInfo;
import com.yuanding.schoolpass.bean.Cpk_Account_InviteInfo_ISendInviteInfo;
import com.yuanding.schoolpass.service.Api.BindInviterCallBack;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.InviteInfoCallBack;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.NetUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.WeiXinUtil;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.ShareDialog;
import com.yuanding.schoolpass.view.ShareDialog.Builder;
import com.yuanding.schoolpass.view.ShareDialog.Builder.DialogBtnClickCallBack;


import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;


/**
 * 邀请主页面
 * 
 * @author Administrator
 * 
 */
public class B_Account_Invite_Main extends A_0_CpkBaseTitle_Navi implements OnClickListener{
	LinearLayout layout_inputinviter;
	EditText edit_tjrphonenum;
	Button btn_committjrphonenum;
	LinearLayout layout_invitermsg;
	CircleImageView img_tjrheadimage;
	TextView tx_tjrname, tx_tjrclass;
	LinearLayout layout_share;
	RelativeLayout layout_inviterecord_line; //邀请记录分割线
	Button btn_share;
	TextView tx_top_rec_info_top,tx_detailrule; //顶部和底部的文字内容
	ListView lv_inviterecord;
	LinearLayout layout_loadmore;
	MyOnClickListener mMyOnClickListener;
    Context mContext;

    protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	private View lienr_account_invite_whole_view,lienr_account_invite_loading,lienr_account_invite_loading_error,lienr_account_invite_no_cotent;
	static String TAG = "B_Account_Invite_Main";
	
	String default_ShareTitle = "";
	String default_ShareContent = "";	
	String shareInviteUrl="";    //微信分享url、复制分享链接
	String inviteRuleUrl="";  //邀请规则说明

	private IWXAPI api;
	
	boolean isInstallWeiXin;
	private ACache maACache;
	private JSONObject jsonObject;
	
	boolean isMySendInviteDataLoadSuccWaitShow = false; //我的邀请人信息和我发出的邀请信息 是不是都加载完毕了 都加载完毕的时候 隐藏进度条
	boolean isMyInviterDataLoadSuccWaitShow = false;
	
	boolean isRenZhengUser = false;
	
	static int minloadmore = 6; //当大于等于这个值得时候 显示加载更多 
	static int currentShowrecordcount = 5;  // 当前页面加载的记录个数 必须小于minloadmore的值
	
	 private LinearLayout home_load_loading;
	 private AnimationDrawable drawable;
	 TextView tv_temp_invite;
    private boolean havaSuccessLoadData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_invite);
		//注册到微信
		api = WXAPIFactory.createWXAPI(this, PayStrStatic.WX_APP_ID, true);
		api.registerApp(PayStrStatic.WX_APP_ID);
		mContext = this;
		setTitleText(mContext.getResources().getString(R.string.str_my_account_invite_title));
		
		setZuiYouText(mContext.getResources().getString(R.string.str_my_account_invite_title_zuiyoubian));

		default_ShareTitle = AppStrStatic.str_my_account_invite_defaultsharetitle();
		default_ShareContent = AppStrStatic.str_my_account_invite_defaultsharecontent();

		//判断是否支持分享
		isInstallWeiXin = api.isWXAppSupportAPI();
		initView();		
		initConfig();
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	private void initView() {
		layout_inputinviter = getViewById(R.id.layout_inputinviter);
		edit_tjrphonenum = getViewById(R.id.edit_tjrphonenum);
		btn_committjrphonenum = getViewById(R.id.btn_committjrphonenum);

		layout_invitermsg = getViewById(R.id.layout_invitermsg);
		img_tjrheadimage = (CircleImageView)findViewById(R.id.img_tjrheadimage);
		tx_tjrname = getViewById(R.id.tx_tjrname);
		tx_tjrclass = getViewById(R.id.tx_tjrclass);
		tv_temp_invite = getViewById(R.id.tv_temp_invite);
		btn_share = getViewById(R.id.btn_share);
		tx_detailrule = getViewById(R.id.tx_detailrule);
		tx_top_rec_info_top = getViewById(R.id.tx_top_rec_info_top);
	
		layout_share = getViewById(R.id.layout_share);
		layout_inviterecord_line = getViewById(R.id.layout_inviterecord_line);
		lv_inviterecord = getViewById(R.id.lv_inviterecord);
		layout_loadmore = getViewById(R.id.layout_loadmore);
		
		lienr_account_invite_loading=findViewById(R.id.account_invite_loading);
		lienr_account_invite_loading_error=findViewById(R.id.account_invite_load_error);
		lienr_account_invite_no_cotent=findViewById(R.id.account_invite_no_content);
		lienr_account_invite_whole_view=findViewById(R.id.ll_account_invite_whole_view);
		
		home_load_loading = (LinearLayout) lienr_account_invite_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mMyOnClickListener = new MyOnClickListener();
		btn_committjrphonenum.setOnClickListener(mMyOnClickListener);
		btn_share.setOnClickListener(mMyOnClickListener);
		tx_detailrule.setOnClickListener(mMyOnClickListener);

		lienr_account_invite_loading_error.setOnClickListener(mMyOnClickListener);
		tv_temp_invite.setText("将"+A_0_App.APP_NAME+"介绍给更多人");
		/**
		 * 如果是未认证的不能分享
		 */
		if(A_0_App.USER_STATUS.equals("5")||A_0_App.USER_STATUS.equals("0")){
			isRenZhengUser = false;
			layout_share.setVisibility(View.GONE);
			showTitleBt(ZUI_RIGHT_TEXT, false);
		}else{
			isRenZhengUser = true;
			showTitleBt(ZUI_RIGHT_TEXT, true);
		}
		
	}
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_invite_main+A_0_App.USER_UNIQID);
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
        	showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}
	private void showInfo(JSONObject jsonObject) {
		try {
			int state = jsonObject.optInt("status");
			List<Cpk_Account_InviteInfo_ISendInviteInfo> mlistContact = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();
			if (state != 1) {
				return;
			}
			havaSuccessLoadData = true;
			/** 获取我发出的邀请信息 */
			mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"", Cpk_Account_InviteInfo_ISendInviteInfo.class);
			if (mlistContact != null && mlistContact.size() > 0) {
				InviteRecordAdapter mInviteRecordAdapter = new InviteRecordAdapter(mContext);
				if(mlistContact.size()>=minloadmore){
					List<Cpk_Account_InviteInfo_ISendInviteInfo> mlc = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();;
					for(int i=0;i<currentShowrecordcount;i++){
						mlc.add(mlistContact.get(i));
					}
					mInviteRecordAdapter.setInviteRecordList(mlc);
					lv_inviterecord.setAdapter(mInviteRecordAdapter);
					B_Account_Invite_InviteRecord.setListViewHeightBasedOnChildren(lv_inviterecord);
				}else{
					mInviteRecordAdapter.setInviteRecordList(mlistContact);
					lv_inviterecord.setAdapter(mInviteRecordAdapter);
					B_Account_Invite_InviteRecord.setListViewHeightBasedOnChildren(lv_inviterecord);
				}				
				
			    //showLoadResult(false,true, false);
			}
			//是否显示邀请记录 线
			int count = jsonObject.getInt("count");
			if(count>0){
				layout_inviterecord_line.setVisibility(View.VISIBLE);
			}else{
				layout_inviterecord_line.setVisibility(View.GONE);
			}
			
			/** 是否显示加载更多 */
			if(count>=minloadmore){					
				layout_loadmore.setVisibility(View.VISIBLE);
				layout_loadmore.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent  = new Intent(mContext, B_Account_Invite_InviteRecord.class);
						startActivity(intent);							
					}
				});
			}
			
			/** 获取我的推荐人信息 */
			String infoJsonstr = jsonObject.getString("info");
			if(!infoJsonstr.isEmpty()){
				JSONObject infoJsonstrobject = new JSONObject(infoJsonstr);							
				String userid = infoJsonstrobject.getString("user_id");
				String truename = infoJsonstrobject.getString("true_name");
				String photoname = infoJsonstrobject.getString("photo_url");
				String organ_name = infoJsonstrobject.getString("organ_name");
								
				MyInviterInfo mMyInviterInfo = new MyInviterInfo(userid, truename, photoname, organ_name);
				if (null!=mMyInviterInfo) {
					String uri = mMyInviterInfo.getPhotoUrl();
					if(img_tjrheadimage.getTag() == null){
					    PubMehods.loadServicePic(imageLoader,uri,img_tjrheadimage, options);
					    img_tjrheadimage.setTag(uri);
					}else{
					    if(!img_tjrheadimage.getTag().equals(uri)){
					        PubMehods.loadServicePic(imageLoader,uri,img_tjrheadimage, options);
					        img_tjrheadimage.setTag(uri);
					    }
					}
					if(mMyInviterInfo.getTrueName().isEmpty()){
						tx_tjrname.setText(mContext.getString(R.string.str_my_account_invite_noknow));
					}else{
						tx_tjrname.setText(mMyInviterInfo.getTrueName());
					}
					if(mMyInviterInfo.getOrganName().isEmpty()){
						tx_tjrclass.setText(mContext.getString(R.string.str_my_account_invite_noknow));
					}else{
						tx_tjrclass.setText(mMyInviterInfo.getOrganName());
					}
					layout_invitermsg.setVisibility(View.VISIBLE);
					layout_inputinviter.setVisibility(View.GONE);			
				}


				
			}else {
				layout_invitermsg.setVisibility(View.GONE);
				layout_inputinviter.setVisibility(View.VISIBLE);
			}
			
			/** 获取邀请链接和邀请规则链接  */
			shareInviteUrl = jsonObject.getString("rec_url");
			inviteRuleUrl = jsonObject.getString("rec_info_url");		
			
			//获取页面顶部和底部的文本内容
			 String rec_info_top = jsonObject.optString("rec_info_top");
			 String rec_info_bottom = jsonObject.optString("rec_info_bottom");
			 setTopBottomText(rec_info_top, rec_info_bottom);
			
			//如果是认证用户并且缓存里面有邀请链接  则显示分享布局
			if(isRenZhengUser && !shareInviteUrl.isEmpty()){
				layout_share.setVisibility(View.VISIBLE);
			}else{
				layout_share.setVisibility(View.GONE);
			}
			
			showLoadResult(false,true, false);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

		

		
	}
	private void initConfig() {
		imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.i_default_por_120)
        .showImageForEmptyUri(R.drawable.i_default_por_120) 
        .showImageOnFail(R.drawable.i_default_por_120) // 设置图片加载或解码过程中发生错误显示的图片
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
        .displayer(new RoundedBitmapDisplayer(0)) // 设置成圆角图片
        .build(); // 构建完成

	}
	/**
	 * 获取我的推荐人信息
	 */
	private void getInviterData() {
		A_0_App.getApi().reqInviteInfo(A_0_App.USER_TOKEN, new InviteInfoCallBack() {
			
			/**
			 * 我发出的邀请信息
			 */
			@Override
			public void onSuccessMySendInvitionInfo(List<Cpk_Account_InviteInfo_ISendInviteInfo> mMySendInvitionInfo) {
			    if(isFinishing())
			        return;
				if(null!=mMySendInvitionInfo){
					InviteRecordAdapter mInviteRecordAdapter = new InviteRecordAdapter(mContext);			
					
					if(mMySendInvitionInfo.size()>=minloadmore){
						List<Cpk_Account_InviteInfo_ISendInviteInfo> mlc = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();;
						for(int i=0;i<currentShowrecordcount;i++){
							mlc.add(mMySendInvitionInfo.get(i));
						}
						mInviteRecordAdapter.setInviteRecordList(mlc);
						lv_inviterecord.setAdapter(mInviteRecordAdapter);
						B_Account_Invite_InviteRecord.setListViewHeightBasedOnChildren(lv_inviterecord);
					}else{
						mInviteRecordAdapter.setInviteRecordList(mMySendInvitionInfo);
						lv_inviterecord.setAdapter(mInviteRecordAdapter);
						B_Account_Invite_InviteRecord.setListViewHeightBasedOnChildren(lv_inviterecord);
					}			

				}
				
				isMySendInviteDataLoadSuccWaitShow = true;
				if(isMyInviterDataLoadSuccWaitShow && isMySendInviteDataLoadSuccWaitShow){
					showLoadResult(false,true, false);
				}
			}
			
			/**
			 * 我的邀请人信息
			 */
			@Override
			public void onSuccessMyInviterInfo(MyInviterInfo mMyInviterInfo) {
			    if(isFinishing())
			        return;
				if (null!=mMyInviterInfo) {
					String uri = mMyInviterInfo.getPhotoUrl();
					if(img_tjrheadimage.getTag() == null){
					    PubMehods.loadServicePic(imageLoader,uri,img_tjrheadimage, options);
					    img_tjrheadimage.setTag(uri);
					}else{
					    if(!img_tjrheadimage.getTag().equals(uri)){
					        PubMehods.loadServicePic(imageLoader,uri,img_tjrheadimage, options);
					        img_tjrheadimage.setTag(uri);
					    }
					}
					if(mMyInviterInfo.getTrueName().isEmpty()){
						tx_tjrname.setText(mContext.getString(R.string.str_my_account_invite_noknow));
					}else{
						tx_tjrname.setText(mMyInviterInfo.getTrueName());
					}
					if(mMyInviterInfo.getOrganName().isEmpty()){
						tx_tjrclass.setText(mContext.getString(R.string.str_my_account_invite_noknow));
					}else{
						tx_tjrclass.setText(mMyInviterInfo.getOrganName());
					}
					layout_invitermsg.setVisibility(View.VISIBLE);
					layout_inputinviter.setVisibility(View.GONE);
					
				}else {
					layout_invitermsg.setVisibility(View.GONE);
					layout_inputinviter.setVisibility(View.VISIBLE);
//					showToast("null == mMyInviterInfo");
				}
				isMyInviterDataLoadSuccWaitShow = true;
				if(isMyInviterDataLoadSuccWaitShow && isMySendInviteDataLoadSuccWaitShow){
					showLoadResult(false,true, false);
				}
			}

			@Override
			public void onSuccessCount(final int iSendInvitionCount) {
                if (isFinishing())
                    return;
//				Log.i(TAG, "---我邀请的人的记录个数："+iSendInvitionCount);
				if(iSendInvitionCount>0){
					layout_inviterecord_line.setVisibility(View.VISIBLE);
				}
				
				if(iSendInvitionCount>=minloadmore){					
					layout_loadmore.setVisibility(View.VISIBLE);
					layout_loadmore.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent intent  = new Intent(mContext, B_Account_Invite_InviteRecord.class);
							intent.putExtra("count", iSendInvitionCount+"");
							startActivity(intent);							
						}
					});
				}
//				showLoadResult(false,true, false);
				
			}
			@Override
			public void onSucceccShareUrl(String shareurl) {
				//如果是认证用户，并且返回的分享链接不为空 则显示分享布局
				if(isRenZhengUser && !shareurl.isEmpty()){
					shareInviteUrl = shareurl;
					layout_share.setVisibility(View.VISIBLE);
				}else{
					layout_share.setVisibility(View.GONE);
				}				
				
			}

			@Override
			public void onSucceccInviteRuleUrl(String inviteurl) {
				inviteRuleUrl = inviteurl;
			}
			
			@Override
			public void onSucceccTopAndBottomRecInfoText(String rec_info_top,String rec_info_bottom) {
				setTopBottomText(rec_info_top,rec_info_bottom);				
			}
			
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                if(!havaSuccessLoadData){
                    showLoadResult(false,false, true);
                }
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	/**
	 * 设置顶部和底部文本内容
	 * @param rec_info_top
	 * @param rec_info_bottom
	 */
	private void setTopBottomText(String rec_info_top,String rec_info_bottom) {
		if(null!=rec_info_top && rec_info_top.length()>0){
			tx_top_rec_info_top.setText(rec_info_top);
		}
		if(null!=rec_info_bottom && rec_info_bottom.length()>0){
			rec_info_bottom+="详细规则";
			tx_detailrule.setText(rec_info_bottom);
			String[] keyword = new String[]{"详细规则"};
			SpannableString ss = PubMehods.matcherSearchTitle(B_Account_Invite_Main.this.getResources().getColor(R.color.main_color), rec_info_bottom, keyword);
			if(null!=ss){
				tx_detailrule.setText(ss);
			}
			
		}

	}
	
	/**
	 * 绑定我的推荐人信息
	 */
	private void bindInviter() {
		String phone = edit_tjrphonenum.getText().toString();
		A_0_App.getApi().reqBindInviter(A_0_App.USER_TOKEN, phone, new BindInviterCallBack() {
			
			@Override
			public void onSuccess() {
				if(isFinishing())
				    return;
				PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_bindsuccess));
				hidekeyboard();
				getInviterData();
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) { PubMehods.showToastStr(mContext,msg); }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
		if (whole)
			lienr_account_invite_whole_view.setVisibility(View.VISIBLE);
		else
			lienr_account_invite_whole_view.setVisibility(View.GONE);
		
		if (loadFaile)
			lienr_account_invite_loading_error.setVisibility(View.VISIBLE);
		else
			lienr_account_invite_loading_error.setVisibility(View.GONE);
		if(loading)
		    {
			drawable.start();
			lienr_account_invite_loading.setVisibility(View.VISIBLE);
			}else{
				if (drawable!=null) {
					drawable.stop();
				}
			
			lienr_account_invite_loading.setVisibility(View.GONE);}
	}
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
        case BACK_BUTTON:
            finish();
            break;
        case ZUI_RIGHT_TEXT:
        	//邀请说明
            //startActivity(new Intent(B_Account_Invite_Main.this, B_Account_Invite_InviteRecord.class));
        	inviteIntroduce();
        	break;
        default:
            break;
    }

	}
    public void inviteIntroduce()
    {
    	if(!inviteRuleUrl.isEmpty()){
//    		Log.i(TAG, "----inviteUrl"+inviteRuleUrl);
    		Intent intent=new Intent(B_Account_Invite_Main.this, Pub_WebView_Load_Acy.class);
        	intent.putExtra("url_text", inviteRuleUrl);
        	intent.putExtra("title_text", mContext.getResources().getString(R.string.str_my_account_invite_title_zuiyoubian));
        	intent.putExtra("tag_skip", "1");
        	intent.putExtra("tag_show_refresh_btn", "2");
        	startActivity(intent);
    	}
    }

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_committjrphonenum:
				if(!edit_tjrphonenum.getText().toString().isEmpty()){
					//hidekeyboard();
					bindInviter();
				}else{
					PubMehods.showToastStr(mContext, mContext.getResources().getString(R.string.str_my_account_invite_bindfaileprompt_phonenumiskong));
				}

				break;
			case R.id.btn_share:
				
				ShareDialog.Builder builder = new Builder(mContext, "微信好友","微信朋友圈","复制链接",true, new DialogBtnClickCallBack() {
					
					@Override
					public void OnClickWXFriend() {
						if (isInstallWeiXin){
							shareReq(1);
						}else {
							Toast.makeText(mContext, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void OnClickWXCicle() {
						if (isInstallWeiXin){
							shareReq(2);
						}else {
							Toast.makeText(mContext, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void OnClickCopyUrl() {
//						showToast("copyurl");
						ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
						ClipData myClip;						
						myClip = ClipData.newPlainText("text", default_ShareTitle+"\n"+shareInviteUrl);
						myClipboard.setPrimaryClip(myClip);
						PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_copyurlsuccess));
						
					}
				});					
				builder.create().show();
				break;
			case R.id.tx_detailrule:
				inviteIntroduce();
				break;
			case R.id.account_invite_load_error:
				showLoadResult(true,false, false);
				getInviterData();
				break;
			default:
				break;
			}

		}

	}
	/**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                	
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Scy.this,getResources().getString(R.string.token_timeout));
                    break;
                    
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                    
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Account_Invite_Main.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
    private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        MyAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
        	
        	getInviterData();
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }



	private int mTargetScene;
	private static final int THUMB_SIZE = 150;

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	/**
	 * 
	 * @param sharetype 分享类型
	 * 1 微信朋友 2 微信朋友圈
	 */
	private void shareReq(int sharetype) {
	
		//检查网络
		if(!NetUtils.isConnected(mContext)){
			PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.error_title_net_error));
			return;
		}
		if(1==sharetype){
			mTargetScene = WXSceneSession;
		}
		if(2==sharetype){
			mTargetScene = WXSceneTimeline;
		}
		if(shareInviteUrl.isEmpty()){
			PubMehods.showToastStr(mContext,mContext.getResources().getString(R.string.str_my_account_invite_sharedisable_prompt));
			return;
		}

		//开始微信分享  回调WXEntryActivity
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = shareInviteUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = default_ShareTitle;
		msg.description = default_ShareContent;
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		bmp.recycle();
		msg.thumbData = WeiXinUtil.bmpToByteArray(thumbBmp, true, 32);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = mTargetScene;
		api.sendReq(req);


	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(mContext).onActivityResult(requestCode, resultCode, data);
	};
	
	/**
	 * 隐藏键盘
	 */
	private void hidekeyboard() {
		 InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(edit_tjrphonenum.getWindowToken(), 0);

	}
	public <T extends View> T getViewById(int id) {
		return (T) this.findViewById(id);
	}
	@Override
	protected void onDestroy() {
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}
}

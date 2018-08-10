package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Comment_detail;
import com.yuanding.schoolpass.bean.Cpk_Notice_Detail;
import com.yuanding.schoolpass.utils.FileSizeUtils;
import com.yuanding.schoolpass.service.Api;
import com.yuanding.schoolpass.service.Api.InterNoticeOfficialDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.VerticalImageSpan;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12
 * 官方通知详情
 */
public class B_Mess_Notice_Detail_Official_News extends A_0_CpkBaseTitle_Navi {

    private View mess_notice_detail_load_error,liner_notice_detail_whole,mess_notice_detail_loading;
   // private ImageView iv_notice_detail_banner;
    private TextView tv_message_detail_title,tv_message_detail_notice_time,tv_notice_detail,tv_message_detail_read_count;
    private WebView webview_notice_detail;
    private String message_id;
//    protected ImageLoader imageLoaderBanner;
//    private DisplayImageOptions optionsBanner;
    private ImageOptions bitmapUtils;
    private int acy_type;//页面类型    推送1，正常列表进入2
    private TextView tv_mess_detail_from;
    private ACache maACache;
    private JSONObject jsonObject;
    private LinearLayout liner_notice_detail_container;
    private Cpk_Notice_Detail detail_Notice;
    private boolean havaSuccessLoadData = false;
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    //附件
    private LinearLayout mess_notice_detail_load_others;
    private ProgressBar fileProgressBar;
    private ImageView fileImage;
    private TextView fileName;
    private TextView fileDet;
    private RelativeLayout mess_notice_detail_load_other;
    private String file_name, file_ext, file_url;
    private long file_size;
    private boolean DonDownload = false;
    private String BASE_PATH=android.os.Environment.getExternalStorageDirectory()+ AppStrStatic.SD_PIC+"/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail_official);
        
        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setTitleText("通知详情");
        detail_Notice = new Cpk_Notice_Detail();
        acy_type =  getIntent().getExtras().getInt("acy_type");
        if(acy_type == 2){
            //列表进入
            message_id = getIntent().getExtras().getString("message_id");
        }else{
            //推送进入
        	message_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }
        
        mess_notice_detail_load_error = findViewById(R.id.mess_notice_detail_load_error);
        liner_notice_detail_whole = findViewById(R.id.liner_notice_detail_whole);
        mess_notice_detail_loading=findViewById(R.id.mess_notice_detail_loading);
        
        home_load_loading = (LinearLayout) mess_notice_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        //iv_notice_detail_banner = (ImageView)findViewById(R.id.iv_notice_detail_banner);
        liner_notice_detail_container = (LinearLayout)findViewById(R.id.liner_notice_detail_container);
        tv_message_detail_title =(TextView)findViewById(R.id.tv_message_detail_title_sys);
        tv_message_detail_notice_time =(TextView)findViewById(R.id.tv_message_detail_notice_time_sys);
        tv_message_detail_read_count =(TextView)findViewById(R.id.tv_message_detail_read_count);
        tv_mess_detail_from=(TextView) findViewById(R.id.tv_message_detail_notice_from);
        webview_notice_detail = (WebView)findViewById(R.id.webview_notice_detail_sys);
        tv_notice_detail=(TextView)findViewById(R.id.tv_notice_detail);
        webview_notice_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webview_notice_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
        webview_notice_detail.getSettings().setJavaScriptEnabled(true);
        webview_notice_detail.getSettings().setDefaultTextEncodingName("utf-8"); //设置文本编码
        int size=A_0_App.getInstance().setWebviewSize();
        webview_notice_detail.getSettings().setDefaultFontSize(size);
        webview_notice_detail.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
            	//view.loadUrl(url); 
                return true; 
             }
        });

        //附件
        mess_notice_detail_load_others = (LinearLayout) findViewById(R.id.mess_notice_detail_load_others);
        fileProgressBar = (ProgressBar) findViewById(R.id.fileProgressBar);
        fileImage = (ImageView) findViewById(R.id.fileImage);
        fileName = (TextView) findViewById(R.id.fileName);
        fileDet = (TextView) findViewById(R.id.fileDet);
        mess_notice_detail_load_other = (RelativeLayout) findViewById(R.id.mess_notice_detail_load_other);
        mess_notice_detail_load_other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载
                //开始下载
                if (!DonDownload){
                    if (file_size > 0){
                        downloadFile(file_url, BASE_PATH + file_name);
                    }else {
                        showToastFileStr("空文件，无法下载");
                    }
                }else {
                    try {
                        String substr = file_ext.substring(file_ext.length() - 3, file_ext.length()).trim().toLowerCase();
                        switch (substr) {
                            case "doc":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
                                break;
                            case "ocx":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
                                break;
                            case "xls":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
                                break;
                            case "lsx":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
                                break;
                            case "ptx":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
                                break;
                            case "ppt":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
                                break;
                            case "txt":
                                B_Mess_Notice_Detail_Official_News.this.startActivity(getFileIntent(BASE_PATH + file_name, "text/plain"));
                                break;
                        }
                    }catch (Exception e){
                        showToastFileStr("打开文件失败或没有打开文件的应用");
                    }
                }
            }
        });
//      webview_notice_detail.getSettings().setUseWideViewPort(true); 
//    webview_notice_detail.getSettings().setLoadWithOverviewMode(true);
      
//     webview_notice_detail.getSettings().setJavaScriptEnabled(true);
//     webview_notice_detail.getSettings().setAllowFileAccess(true);
//     webview_notice_detail.getSettings().setPluginState(PluginState.ON);
      
//  第一种： 解决图文混排问题
//    settings.setUseWideViewPort(true); 
//    settings.setLoadWithOverviewMode(true); 
//    第二种： 
//    WebSetting settings = webView.getSettings(); 
//    settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
//    把所有内容放在webview等宽的一列中。（可能会出现页面中链接失效） 
      
//    webSettings.setJavaScriptEnabled(true);
//    webSettings.setAllowFileAccess(true);
//    webSettings.setPluginState(PluginState.ON);
//    webSettings.setUseWideViewPort(true);
//    
//    webView.loadUrl(loadStr);
//    
//    webSettings.setAppCacheEnabled(true);// 设置缓存
//    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式
//    webSettings.setBuiltInZoomControls(true);// 显示缩放控件
//    webSettings.setSupportZoom(true); // 支持缩放
//    webSettings.setDefaultFontSize(16);
//     imageLoaderBanner = A_0_App.getInstance().getimageLoader();
//     optionsBanner = A_0_App.getInstance().getOptions(R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,
//     R.drawable.ic_default_empty_bg);
      bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg,false);  
      if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
          startListtenerRongYun();// 监听融云网络变化
      }
      
      mess_notice_detail_load_error.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View arg0) {
              showLoadResult(true,false, false);
              readData(message_id);
          }
      });
      
      readCache(message_id);
    }
    private void downloadFile(String url, final String path){
        mess_notice_detail_load_other.setClickable(false);
        fileProgressBar.setVisibility(View.VISIBLE);
        A_0_App.getApi().download_Photo(url, path, new Api.Inter_DownLoad_Photo() {
            @Override
            public void onCancelled() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String message) {
                mess_notice_detail_load_other.setClickable(true);
                DonDownload = true;
                showToastFileStr("文件已保存至" + AppStrStatic.SD_PIC +"/文件夹");
            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {

                if (arg0 > 0){
                    double process = 1.0 * arg1 / arg0;
                    Log.i("aaa", "onLoading: " + (int) (process * 100));

                    fileProgressBar.setProgress((int) (process * 100));
                }else {
                    if (arg0 == arg1){
                        fileProgressBar.setProgress(100);
                    }
                }
//                double process = 1.0 * arg1 / arg0;
//                Log.i("aaa", "onLoading: " + (int)(process * 100));
//
//                fileProgressBar.setProgress((int)(process * 100));
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Mess_Notice_Detail_Official_News.this, msg);
                mess_notice_detail_load_other.setClickable(true);
                fileProgressBar.setVisibility(View.GONE);

            }
        });
    }
    public static Intent getFileIntent( String param, String type)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, type);
        return intent;
    }
    private void readCache(String message_id) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_official_detail_text+A_0_App.USER_UNIQID+message_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
    }
    
    private void showInfo(JSONObject jsonObject) {
        Cpk_Notice_Detail notice;
        try {
            JSONObject dd = jsonObject.getJSONObject("info");
            notice = new Cpk_Notice_Detail();
            notice = JSON.parseObject(dd + "", Cpk_Notice_Detail.class);
            JSONArray jsonArrayItem = jsonObject.optJSONArray("comment");
            List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
            if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_Comment_detail.class);
            }
            notice.setList(mlistContact);
            loadData(notice);

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    private  void loadData(Cpk_Notice_Detail notice){
        if(isFinishing())
            return;
        havaSuccessLoadData = true;
        detail_Notice=notice;
        if(notice.getBg_img() != null && notice.getBg_img().length()>0 && !notice.getBg_img().equals("")){
//                    iv_notice_detail_banner.setVisibility(View.VISIBLE);
//                    String uri = notice.getBg_img();
//    				if(iv_notice_detail_banner.getTag() == null){
//    					imageLoaderBanner.displayImage(uri,iv_notice_detail_banner, optionsBanner);
//    					iv_notice_detail_banner.setTag(uri);
//    				}else{
//    				    if(!iv_notice_detail_banner.getTag().equals(uri)){
//    				    	imageLoaderBanner.displayImage(uri,iv_notice_detail_banner, optionsBanner);
//    				        iv_notice_detail_banner.setTag(uri);
//    				    }
//    				}
            //bitmapUtils.display(iv_notice_detail_banner, notice.getBg_img());
        }else{
            //iv_notice_detail_banner.setVisibility(View.GONE);
        }
        if (notice.getTitle() != null && notice.getTitle().length() > 0)

            switch (notice.getIs_appendix()){
                case 0:
                    tv_message_detail_title.setText(notice.getTitle());
                    tv_message_detail_title.setCompoundDrawables(null, null, null, null);
                    mess_notice_detail_load_others.setVisibility(View.GONE);
                    break;
                case 1:
                    VerticalImageSpan  span = new VerticalImageSpan(B_Mess_Notice_Detail_Official_News.this, R.drawable.file_zhizhen);
                    SpannableString spanStr = new SpannableString(notice.getTitle() + "    ");
                    spanStr.setSpan(span, spanStr.length()-1, spanStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    tv_message_detail_title.setText(spanStr);

//                            tv_message_detail_title.setText(notice.getTitle());
//                            Drawable drawable = ContextCompat.getDrawable(B_Mess_Notice_Detail_Official_News.this, R.drawable.file_zhizhen);
//
//                            drawable.setBounds(0, 0, 30,30);
//
//                            tv_message_detail_title.setCompoundDrawables(null, null, drawable, null);

                    tv_message_detail_title.setCompoundDrawablePadding(25);
                    mess_notice_detail_load_others.setVisibility(View.VISIBLE);
                    fileName.setText(notice.getFile_name());
                    file_name = notice.getFile_name();
                    file_ext = notice.getFile_ext();
                    String size = notice.getFile_size();
                    if (size != null){
                        if (size.contains(".")){
                            size = size.substring(0, size.indexOf("."));
                        }
                        file_size = Long.parseLong(size);
                        fileDet.setText(FileSizeUtils.FormetFileSize(Long.parseLong(size)));
                    }

                    file_url = notice.getFile_url();
                    String substr = notice.getFile_ext().substring(notice.getFile_ext().length() - 3, notice.getFile_ext().length()).trim().toLowerCase();
                    switch (substr){
                        case "doc":
                            fileImage.setImageResource(R.drawable.file_word);
                            break;
                        case "ocx":
                            fileImage.setImageResource(R.drawable.file_word);
                            break;
                        case "xls":
                            fileImage.setImageResource(R.drawable.file_xl);
                            break;
                        case "lsx":
                            fileImage.setImageResource(R.drawable.file_xl);
                            break;
                        case "ppt":
                            fileImage.setImageResource(R.drawable.file_ppt);
                            break;
                        case "ptx":
                            fileImage.setImageResource(R.drawable.file_ppt);
                            break;
                        case "txt":
                            fileImage.setImageResource(R.drawable.file_txt);
                            break;
                    }
                    File f = new File(Environment.getExternalStorageDirectory().getPath()+ "//" + AppStrStatic.SD_PIC+"/" + file_name);
                    Log.i("aaa", "showInfo: " + f.exists());
                    if (f.exists()){
                        fileProgressBar.setVisibility(View.VISIBLE);
                        fileProgressBar.setProgress(100);
                        DonDownload = true;
                    }else {
                        DonDownload = false;
                    }
                    break;
            }

        if (notice.getCreate_time() != null && notice.getCreate_time().length() > 0)
            tv_message_detail_notice_time.setText(PubMehods.getFormatDate(
                    Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
        try {
            tv_mess_detail_from.setText(notice.getApp_msg_sign());
        } catch (Exception e) {
        }
        pic_show();
//                if (notice.getContent_type()!=null) {
//                if(notice.getContent_type().equals("1"))
//                {
        webview_notice_detail.setVisibility(View.VISIBLE);
        tv_notice_detail.setVisibility(View.GONE);
        /**
         * 本地读取html
         */
        //webview_notice_detail.loadDataWithBaseURL("",notice.getContent(), "text/html","UTF-8", null);

        PubMehods.saveHtml(notice.getContent(), "TEMP_HTML");
        String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";

        webview_notice_detail.loadUrl(html_path);
        webview_notice_detail.addJavascriptInterface(new JavascriptInterface(B_Mess_Notice_Detail_Official_News.this), "imagelistner");
        webview_notice_detail.setWebViewClient(new MyWebViewClient());
//                }else if(notice.getContent_type().equals("0"))
//                {
//               	 webview_notice_detail.setVisibility(View.GONE);
//               	 tv_notice_detail.setVisibility(View.VISIBLE);
//               	 try {
//               		 tv_notice_detail.setText(notice.getContent());
//    				} catch (Exception e) {
//    					// TODO: handle exception
//    					tv_notice_detail.setText("[表情]");
//    				}
//                }}
        tv_message_detail_read_count.setText(notice.getRead_num());
        showLoadResult(false,true, false);
    }

    /**
     * 多张图片展示
     */
    private void pic_show(){
        if (detail_Notice.getPhoto_url() != null && !detail_Notice.getPhoto_url().equals("")&&
                !detail_Notice.getPhoto_url().equals("null")&&detail_Notice.getPhoto_url().length()>0) {
    		final ArrayList<String> path = new ArrayList<String>();
    		liner_notice_detail_container.setVisibility(View.VISIBLE);
			String notice_photos[]=detail_Notice.getPhoto_url().split(",");
			for (int i = 0; i <notice_photos.length; i++) {
				path.add(notice_photos[i]);
			}
			if (notice_photos.length==1) {
	            liner_notice_detail_container.removeAllViews();
	            LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail_Official_News.this);
	            RelativeLayout.LayoutParams params;
	            ImageView image1 = new ImageView(B_Mess_Notice_Detail_Official_News.this);
	            float density = PubMehods.getDensity(B_Mess_Notice_Detail_Official_News.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - (20) * density;
	            params = new RelativeLayout.LayoutParams((int)imageLayWidth,(int)imageLayWidth);
	            horizonLayout.addView(image1, params);
	            image1.setScaleType(ScaleType.CENTER_CROP);
	            PubMehods.loadBitmapUtilsPic(bitmapUtils,image1, notice_photos[0]);
	            image1.setOnClickListener(new OnClickListener() {

	                @Override
	                public void onClick(View arg0) {
	                	Intent intent = new Intent(B_Mess_Notice_Detail_Official_News.this,
								B_Side_Found_BigImage.class);
						intent.putStringArrayListExtra("path", path);
						intent.putExtra("num", 0);
						startActivity(intent);
	                }
	            });
	            liner_notice_detail_container.addView(horizonLayout);
	        
			} else if(notice_photos.length==2){
				liner_notice_detail_container.removeAllViews();
	            LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail_Official_News.this);
	            LinearLayout.LayoutParams params;
	            float density = PubMehods.getDensity(B_Mess_Notice_Detail_Official_News.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - (36) * density;
	            for (int i = 0; i < notice_photos.length; i++) {
	                params = new LinearLayout.LayoutParams(
	                        (int) (imageLayWidth /2-10), (int) (imageLayWidth / 2-10));
	                params.setMargins(5, 5, 5, 5);
	                ImageView image2 = new ImageView(B_Mess_Notice_Detail_Official_News.this);
	                image2.setLayoutParams(params);
	                final int a = i;
	                image2.setScaleType(ScaleType.CENTER_CROP);
	                horizonLayout.addView(image2);
	                PubMehods.loadBitmapUtilsPic(bitmapUtils,image2, notice_photos[i]);
	               // imageLoaderBanner.displayImage(notice_photos[i],image2, optionsBanner);
	                image2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {

							Intent intent = new Intent(B_Mess_Notice_Detail_Official_News.this,
									B_Side_Found_BigImage.class);
							intent.putStringArrayListExtra("path", path);
							intent.putExtra("num", a);
							startActivity(intent);

						
						}
					});
	            }
	            liner_notice_detail_container.addView(horizonLayout);
	        
			}else if (notice_photos.length> 2 && notice_photos.length<= 9) {
				liner_notice_detail_container.removeAllViews();
	            LinearLayout.LayoutParams params;
	            float density = PubMehods.getDensity(B_Mess_Notice_Detail_Official_News.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - 38*density;
	            int size = notice_photos.length;
	            int yuShu = size % 3;
	            if (yuShu == 0) {
	                int hangNum = (int) (size / 3);
	                for (int i = 0; i < hangNum; i++) {
	                    LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail_Official_News.this);
	                    for (int j = 0; j < 3; j++) {
	                        params = new LinearLayout.LayoutParams(
	                                (int) (imageLayWidth / 3-10),
	                                (int) (imageLayWidth / 3-10));
	                        params.setMargins(5, 5, 5, 5);
	                        ImageView image3 = new ImageView(B_Mess_Notice_Detail_Official_News.this);
	                        image3.setLayoutParams(params);
	                        image3.setScaleType(ScaleType.CENTER_CROP);
	                        horizonLayout.addView(image3);
	                        final int a = i * 3 + j;
	                        //imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                        PubMehods.loadBitmapUtilsPic(bitmapUtils,image3, notice_photos[a]);
	                        image3.setOnClickListener(new OnClickListener() {

	                            @Override
	                            public void onClick(View arg0) {

									Intent intent = new Intent(B_Mess_Notice_Detail_Official_News.this,
											B_Side_Found_BigImage.class);
									intent.putStringArrayListExtra("path", path);
									intent.putExtra("num", a);
									startActivity(intent);

								
	                            }
	                        });
	                    }
	                    liner_notice_detail_container.addView(horizonLayout);
	                }
	            } else {
	                int hangNum = (int) (size / 3) + 1;
	                for (int i = 0; i <= hangNum - 1; i++) {
	                    LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail_Official_News.this);
	                    if (i < hangNum - 1) {
	                        for (int j = 0; j < 3; j++) {
	                            params = new LinearLayout.LayoutParams(
	                                    (int) (imageLayWidth / 3-10),
	                                    (int) (imageLayWidth / 3-10));
	                            params.setMargins(5, 5, 5, 5);
	                            ImageView image3 = new ImageView(B_Mess_Notice_Detail_Official_News.this);
	                            image3.setScaleType(ScaleType.CENTER_CROP);
	                            image3.setLayoutParams(params);
	                            horizonLayout.addView(image3);
	                            final int a = i * 3 + j;
	                           // imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                            PubMehods.loadBitmapUtilsPic(bitmapUtils,image3, notice_photos[a]);
	                            image3.setOnClickListener(new OnClickListener() {

	                                @Override
	                                public void onClick(View arg0) {

	    								Intent intent = new Intent(B_Mess_Notice_Detail_Official_News.this,
	    										B_Side_Found_BigImage.class);
	    								intent.putStringArrayListExtra("path", path);
	    								intent.putExtra("num", a);
	    								startActivity(intent);

	    							
	                                }
	                            });
	                        }
	                        liner_notice_detail_container.addView(horizonLayout);
	                    } else if (i == hangNum - 1) {
	                        for (int j = 0; j < yuShu; j++) {
	                            params = new LinearLayout.LayoutParams(
	                                    (int) (imageLayWidth / 3-10),
	                                    (int) (imageLayWidth / 3-10));
	                            params.setMargins(5, 5, 5, 5);
	                            ImageView image3 = new ImageView(B_Mess_Notice_Detail_Official_News.this);
	                            image3.setScaleType(ScaleType.CENTER_CROP);
	                            image3.setLayoutParams(params);
	                            horizonLayout.addView(image3);
	                            final int a = i * 3 + j;
	                            PubMehods.loadBitmapUtilsPic(bitmapUtils,image3, notice_photos[a]);
	                            //imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                            image3.setOnClickListener(new OnClickListener() {

	                                @Override
	                                public void onClick(View arg0) {

	    								Intent intent = new Intent(B_Mess_Notice_Detail_Official_News.this,
	    										B_Side_Found_BigImage.class);
	    								intent.putStringArrayListExtra("path", path);
	    								intent.putExtra("num", a);
	    								startActivity(intent);

	    							
	                                }
	                            });
	                        }
	                        liner_notice_detail_container.addView(horizonLayout);
	                    }
	                }
	            }
	        
			}
		}
    }
    private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
        if (whole)
            liner_notice_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_notice_detail_whole.setVisibility(View.GONE);
        
        if (loadFaile)
            mess_notice_detail_load_error.setVisibility(View.VISIBLE);
        else
            mess_notice_detail_load_error.setVisibility(View.GONE);
        if(loading){
        	drawable.start();
            mess_notice_detail_loading.setVisibility(View.VISIBLE);
        }else{
        	if (drawable!=null) {
        		drawable.stop();
			}
            mess_notice_detail_loading.setVisibility(View.GONE);
    }}
    
    private void readData(String message_id) {
        A_0_App.getApi().getNoticeOfficialDetail(B_Mess_Notice_Detail_Official_News.this, A_0_App.USER_TOKEN, message_id,new InterNoticeOfficialDetail() {
            
            @Override
            public void onSuccess(Cpk_Notice_Detail notice) {

                
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
                if (!havaSuccessLoadData) {
                    showLoadResult(false, false, true);
                }
                PubMehods.showToastStr(B_Mess_Notice_Detail_Official_News.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
		    goAcy();
			break;
		default:
			break;
		}

	}
	
	private void goData() {
		Intent it = new Intent();
		it.putExtra("read_count","0");
		it.putExtra("repley_count", "0");
		setResult(1, it);
	}
	
    private void goAcy() {
        if (acy_type != 2) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_Notice_Detail_Official_News.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,R.anim.animal_push_right_out_normal);
        }
    }
    
    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
        finish();
    }
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Mess_Notice_Official_News_1.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail_Official_News.this,AppStrStatic.kicked_offline());
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
            
            readData(message_id);
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

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//bitmapUtils=null;
    	drawable.stop();
    	drawable=null;
    	super.onDestroy();
    	System.gc();
    	
    }
	// 监听
  	private class MyWebViewClient extends WebViewClient {
  		@Override
  		public boolean shouldOverrideUrlLoading(WebView view, String url) {

  			if(url.contains("//img")){
  				
  				return true;
  			}else {
  				
  				return super.shouldOverrideUrlLoading(view, url);
  			}
  			
  		}

  		@Override
  		public void onPageFinished(WebView view, String url) {

  			view.getSettings().setJavaScriptEnabled(true);

  			super.onPageFinished(view, url);
  			// html加载完成之后，添加监听图片的点击js函数
  			addImageClickListner();
  			getWebViewAllImgSrc();

  		}

  		@Override
  		public void onPageStarted(WebView view, String url, Bitmap favicon) {
  			view.getSettings().setJavaScriptEnabled(true);

  			super.onPageStarted(view, url, favicon);
  		}

  		@Override
  		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

  			super.onReceivedError(view, errorCode, description, failingUrl);

  		}
  	}
  	
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview_notice_detail.canGoBack()) {
  			webview_notice_detail.goBack();
  			return true;
  		}
  		return super.onKeyDown(keyCode, event);
  	}
 // 注入js函数监听
   	private void addImageClickListner() {
   		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
   		webview_notice_detail.loadUrl("javascript:(function(){" +
   		"var objs = document.getElementsByTagName(\"img\"); " + 
   				"for(var i=0;i<objs.length;i++)  " + 
   		"{"+
   				"objs[i].onclick=function()" +
   		         "{ "+ 
   		           "window.imagelistner.openImage(this.src);  " + 
   		         "}" + 
   		"}" + 
   		"})()");
   	}
   	

   	
   	/**
   	 * 获取当前页面所有img地址为list
   	 * @author Administrator
   	 *
   	 */
       private void getWebViewAllImgSrc() {
       	webview_notice_detail.loadUrl("javascript:(function(){" +
       			"var objs = document.getElementsByTagName(\"img\"); " + 
       			"var imgScr = '';"+
       			   "for(var i=0;i<objs.length;i++){"+
       			       "imgScr =imgScr +  '\\n'+ objs[i].src;"+
       		       " };"+
       		     "window.imagelistner.getAllImg(imgScr);  " + 
//       			"return imgScr;"+
       			"})()");

   	}
   	
   	// js通信接口
   	public class JavascriptInterface {
   	
  			
  		private Context context;
   		final ArrayList<String> image_List = new ArrayList<String>();
   		int temp=0;
   		public JavascriptInterface(Context context) {
   			this.context = context;
   			
   		}

   		public void openImage(String img) {
   			
   			for (int i = 0; i < image_List.size(); i++) {
  				if (image_List.get(i).equals(img)) {
  					temp=i;
  				}
  			}
   			
   			Intent intent = new Intent();
   			intent.putStringArrayListExtra("path", image_List);
  			intent.putExtra("num", temp);
   			intent.setClass(context, B_Side_Found_BigImage.class);
   			context.startActivity(intent);
   			
   		}
   		
   		public void getAllImg(String img) {
   			image_List.clear();
   			String [] temp = null;  
   			temp = img.split("\n"); 
   			//以换行符为url分隔符 第一个元素的null
   			int counts = temp.length-1;
   			for(int i=0;i<temp.length;i++){
   				//以换行符为url分隔符 第一个元素的null
   				if(i==0){continue;}
   				image_List.add(temp[i]);
   			}
   		}
   	}
}

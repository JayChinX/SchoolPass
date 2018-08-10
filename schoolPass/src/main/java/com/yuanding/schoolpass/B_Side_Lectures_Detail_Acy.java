package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Lectures_Detail;
import com.yuanding.schoolpass.service.Api.InterLectureDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 上午11:59:07 类说明
 * 讲座详情
 */ 
public class B_Side_Lectures_Detail_Acy extends A_0_CpkBaseTitle_Navi {

	private View mLinerLoadError,mLinerWholeView,side_lecture_detail_loading;
	private String article_id;
	private TextView tv_lecture_detail_title,tv_lecture_create_time,tv_lecture_detail_browse,tv_lecture_speaker,tv_lectur_detail_period;
	private TextView tv_metting_place,tv_abount_auther;
	private WebView webview_lecture_detail;
//	public final static String CSS_STYLE = "<style>* {font-size:50px;line-height:20px;}p {color:#333333;}</style>";
	private int acy_type;//页面类型    推送3，正常列表进入2
	private Cpk_Side_Lectures_Detail lectures_Detail;
	private ACache maACache;
	private JSONObject jsonObject;
	private TextView tv_lecture_person;
	
	private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_lectures_detail);
		
	
		setTitleText("讲座详情");
		
		acy_type =  getIntent().getExtras().getInt("acy_type");
		if(acy_type == 2){
			//列表进入,转发进入 
			article_id = getIntent().getExtras().getString("lecture_id");
		}else if(acy_type == 3){
			//推送进入3
			article_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
		}
		
		tv_lecture_detail_title = (TextView)findViewById(R.id.tv_lecture_detail_title);
		tv_lecture_create_time = (TextView)findViewById(R.id.tv_lecture_create_time);
		tv_lecture_detail_browse = (TextView)findViewById(R.id.tv_lecture_detail_browse);
		tv_lecture_speaker = (TextView)findViewById(R.id.tv_lecture_speaker);
		tv_lectur_detail_period = (TextView)findViewById(R.id.tv_lectur_detail_period);
		tv_lecture_person=(TextView) findViewById(R.id.tv_lecture_person);
		tv_abount_auther = (TextView)findViewById(R.id.tv_abount_auther);
		tv_metting_place = (TextView)findViewById(R.id.tv_metting_place);
		webview_lecture_detail = (WebView)findViewById(R.id.webview_lecture_detail);
		
		mLinerLoadError = findViewById(R.id.side_lecture_detail_load_error);
		mLinerWholeView = findViewById(R.id.liner_whole_view_001);
		side_lecture_detail_loading = findViewById(R.id.side_lecture_detail_loading);
		
		home_load_loading = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mLinerLoadError.setOnClickListener(onClick);
		
		webview_lecture_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview_lecture_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
		webview_lecture_detail.getSettings().setJavaScriptEnabled(true);
		webview_lecture_detail.getSettings().setDefaultTextEncodingName("utf-8"); //设置文本编码
		webview_lecture_detail.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
            	//view.loadUrl(url); 
                return true; 
             }
        });
		readCache(article_id);
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

    private void readCache(String article_id) {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_lecture_detail + A_0_App.USER_UNIQID + article_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
    }

	private void showInfo(JSONObject jsonObject) {
		Cpk_Side_Lectures_Detail info = getInfo(jsonObject);
		if (info.getContent().equals("")||info.getContent()==null) {
         	showLoadResult(false, false, true);
         	  PubMehods.showToastStr(B_Side_Lectures_Detail_Acy.this, "信息不存在！");
			}else{
				Success(info);
		   }
	}
	@SuppressLint("JavascriptInterface")
	public void Success(Cpk_Side_Lectures_Detail detail) {
		if (isFinishing())
			return;
		havaSuccessLoadData = true;   
		lectures_Detail = detail;
		tv_lecture_detail_title.setText(detail.getTitle());
		tv_lecture_create_time.setText(PubMehods.getFormatDate(detail.getCreate_time(), "yyyy/MM/dd  HH:mm"));
		tv_lecture_detail_browse.setText(detail.getRead_num());
		tv_lecture_speaker.setText(detail.getAuthor());
		tv_lectur_detail_period.setText(detail.getLecture_time());
		tv_abount_auther.setText(detail.getAuthor_desc());
		tv_lecture_person.setText(detail.getOrgan_str());
		tv_metting_place.setText(detail.getPlace());
//		webview_lecture_detail.loadDataWithBaseURL("",detail.getContent(), "text/html","UTF-8", null);
		
		 /**
         * 网络读取html
         */
        PubMehods.saveHtml(detail.getContent(), "TEMP_HTML");
        String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";
        webview_lecture_detail.loadUrl(html_path);
        webview_lecture_detail.addJavascriptInterface(new JavascriptInterface(B_Side_Lectures_Detail_Acy.this), "imagelistner");
        webview_lecture_detail.setWebViewClient(new MyWebViewClient()
        {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		// TODO Auto-generated method stub
        		return true;
        	}
        });
		
		
		showLoadResult(false,true, false);
		
        if(A_0_App.USER_STATUS.equals("2")){
            showTitleBt(ZUI_RIGHT_BUTTON, true);
            setZuiRightBtn(R.drawable.navigationbar_more_share);
        }else{
            showTitleBt(ZUI_RIGHT_BUTTON, false);
        }
	}
	
	
	
	  
	 // 注入js函数监听
	  	private void addImageClickListner() {
	  		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
	  		webview_lecture_detail.loadUrl("javascript:(function(){" +
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
	    	  webview_lecture_detail.loadUrl("javascript:(function(){" +
	      			"var objs = document.getElementsByTagName(\"img\"); " + 
	      			"var imgScr = '';"+
	      			   "for(var i=0;i<objs.length;i++){"+
	      			       "imgScr =imgScr +  '\\n'+ objs[i].src;"+
	      		       " };"+
	      		     "window.imagelistner.getAllImg(imgScr);  " + 
//	      			"return imgScr;"+
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
	
	
	
	
	
	private Cpk_Side_Lectures_Detail getInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
	
			int state = jsonObject.optInt("status");
			Cpk_Side_Lectures_Detail contace = new Cpk_Side_Lectures_Detail();
			if (state == 1) {
				contace=JSON.parseObject(jsonObject+"", Cpk_Side_Lectures_Detail.class);
			}
			return contace;
		
	}

	private PopupWindow statusPopup;
	private LinearLayout mLinerCollct,mLinerForward;

//	private void showWindow(View parent) {
//		if (statusPopup == null) {
//			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View view = layoutInflater.inflate(R.layout.item_lecture_detail, null);
//			mLinerCollct = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_collect);
//			mLinerForward = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_forward);
//			statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		}
//		statusPopup.setFocusable(true);
//		statusPopup.setOutsideTouchable(true);
//		statusPopup.setBackgroundDrawable(new BitmapDrawable());
//		int x=DensityUtils.dip2px(B_Side_Lectures_Detail_Acy.this, 165);
//		statusPopup.showAsDropDown(parent,-x, A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下
//
//		mLinerForward.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (statusPopup != null) {
//					statusPopup.dismiss();
//				}
//				Intent intent=new Intent(B_Side_Lectures_Detail_Acy.this, B_Mess_Forward_Select.class);
//				intent.putExtra("title", lectures_Detail.getTitle());
//				intent.putExtra("content", "主讲："+lectures_Detail.getAuthor()+"\n"+"时间："+PubMehods.getFormatDate(lectures_Detail.getCreate_time(), "yyyy/MM/dd  kk:mm")+"\n"+"地点："+lectures_Detail.getPlace());
//				intent.putExtra("type", "3");
//				intent.putExtra("image", "");
//				intent.putExtra("acy_type", 2);
//				intent.putExtra("noticeId", article_id);
//				startActivity(intent);
//			}
//		});
//		
//		mLinerCollct.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (statusPopup != null) {
//					statusPopup.dismiss();
//				}
//				PubMehods.showToastStr(B_Side_Lectures_Detail_Acy.this, "后续开放");
//			}
//		});
//		
//	}
//	
	private void getLectureDetail(final String article_id) {
		A_0_App.getApi().getLectureDetail(B_Side_Lectures_Detail_Acy.this,A_0_App.USER_TOKEN,article_id, new InterLectureDetail() {
			
			@Override
			public void onSuccess(Cpk_Side_Lectures_Detail detail) {
	             if (isFinishing())
	                    return;
						if (detail.getContent().equals("")|| detail.getContent() == null) {
							showLoadResult(false, false, true);
							PubMehods.showToastStr(B_Side_Lectures_Detail_Acy.this, "信息不存在！");
						} else {
							Success(detail);
						}
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
                PubMehods.showToastStr(B_Side_Lectures_Detail_Acy.this, msg);
                if (!havaSuccessLoadData)
                {
                    showLoadResult(false, false, true);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	private void showLoadResult(boolean loading,boolean show_content, boolean loadFaile) {
		if (show_content)
			mLinerWholeView.setVisibility(View.VISIBLE);
		else
			mLinerWholeView.setVisibility(View.GONE);
		
		if (loadFaile)
			mLinerLoadError.setVisibility(View.VISIBLE);
		else
			mLinerLoadError.setVisibility(View.GONE);
		if(loading){
			drawable.start();
			side_lecture_detail_loading.setVisibility(View.VISIBLE);
		}else{
			if (drawable!=null) {
        		drawable.stop();
			}
			side_lecture_detail_loading.setVisibility(View.GONE);
		}
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_lecture_detail_load_error:
				showLoadResult(true,false, false);
				getLectureDetail(article_id);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
		    goAcy();
			break;
		case ZUI_RIGHT_BUTTON:
			if(A_0_App.USER_STATUS.equals("2")){
				Intent intent=new Intent(B_Side_Lectures_Detail_Acy.this, B_Mess_Forward_Select.class);
				intent.putExtra("title", lectures_Detail.getTitle());
				intent.putExtra("content", "主讲："+lectures_Detail.getAuthor()+"\n"+"时间："+PubMehods.getFormatDate(lectures_Detail.getCreate_time(), "yyyy/MM/dd  kk:mm")+"\n"+"地点："+lectures_Detail.getPlace());
				intent.putExtra("type", "3");
				intent.putExtra("image", "");
				intent.putExtra("acy_type", "2");
				intent.putExtra("noticeId", article_id);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
	
    private void goAcy() {
        if (acy_type == 3) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Side_Lectures_Detail_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            finish();
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
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	drawable.stop();
    	drawable=null;
    	System.gc();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    break;
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Lectures_Detail_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Lectures_Detail_Acy.this,AppStrStatic.kicked_offline());
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
        	getLectureDetail(article_id);
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
}

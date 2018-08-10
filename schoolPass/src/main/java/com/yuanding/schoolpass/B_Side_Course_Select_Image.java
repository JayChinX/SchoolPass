package com.yuanding.schoolpass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.bean.Cpk_Side_Lectures_List;
import com.yuanding.schoolpass.service.Api.InteSideCourse;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.download.FileCourse;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 课程表选择背景图片
 */
public class B_Side_Course_Select_Image extends A_0_CpkBaseTitle_Navi {
	private ACache maACache;
	private JSONObject jsonObject;
	private List<Cpk_Group_Info_item> mList = null;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	private GridView gridView;
	private GridAdapter adapter;
	private SharedPreferences sp;
	private static final String[] AVATARS = {
		 "http://img1.touxiang.cn/uploads/20121212/12-060125_658.jpg",
         "http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg",
         "http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
         "http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
         "http://img1.touxiang.cn/uploads/20130515/15-080722_514.jpg",
         "http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg",
         "http://tupian.qqjay.com/u/2011/0729/e755c434c91fed9f6f73152731788cb3.jpg",
         "http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
         "http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png",
         "http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
         "http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
         "http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg"
         };
	private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mess_course_load_error,liner_course_whole,mess_course_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_course_select_grid);
		setTitleText("选择背景");
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				0, 0,0);
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new GridAdapter();
		mList = new ArrayList<Cpk_Group_Info_item>();
		sp = getSharedPreferences("wxb", Context.MODE_PRIVATE);
		mess_course_load_error = findViewById(R.id.side_course_load_error);
		liner_course_whole = findViewById(R.id.liner_side_course_whole);
		mess_course_loading = findViewById(R.id.side_course_loading);
		gridView.setAdapter(adapter);
		for (int i = 0; i < 6; i++) {
			Cpk_Group_Info_item cpk_Group_Info_item = new Cpk_Group_Info_item();
			cpk_Group_Info_item.setPhoto_url(AVATARS[i]);
			mList.add(cpk_Group_Info_item);
		}
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_b_side_course+A_0_App.USER_UNIQID);
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
        	showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}
	private void showInfo(JSONObject jsonObject) {
		 showLoadResult(false,true, false);
		
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
	        	
	        	readData();
	            return null;
	        }

	        /**
	         * 运行在ui线程中，在doInBackground()执行完毕后执行
	         */
	        @Override
	        protected void onPostExecute(Integer integer) {
//	            logD("上传融云数据完毕");
	        }

	        /**
	         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
	         */
	        @Override
	        protected void onProgressUpdate(Integer... values) {
	        	
	        }
	    }
	 private void readData() {
//		 A_0_App.getApi().getSideCourse(B_Side_Course_Select_Image.this, A_0_App.USER_TOKEN,new InteSideCourse(){
//
//			@Override
//			public void onCancelled() {
//				
//			}
//			@Override
//			public void onStart() {
//				A_0_App.getInstance().showProgreDialog(B_Side_Course_Select_Image.this, "",true);
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				
//			}
//
//			@Override
//			public void onSuccess(List<Cpk_Side_Lectures_List> mList) {
//				showLoadResult(false,true, false);
//			}
//
//			@Override
//			public void onFailure(String msg) {
//				if(isFinishing())
//				return;
//				if(jsonObject==null)
//				{
//			     PubMehods.showToastStr(B_Side_Course_Select_Image.this, msg);
//			     showLoadResult(false,false, true);
//				}
//				
//			}
//			@Override
//			public void onKickedOffline(int state) {
//						if (isFinishing())
//							return;
//						A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Select_Image.this);
//						A_0_App.getInstance().showExitDialog(B_Side_Course_Select_Image.this, getResources().getString(R.string.kicked_offline));
//			}});
	 }
	 private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
	        if (whole)
	            liner_course_whole.setVisibility(View.VISIBLE);
	        else
	            liner_course_whole.setVisibility(View.GONE);
	        
	        if (loadFaile)
	            mess_course_load_error.setVisibility(View.VISIBLE);
	        else
	            mess_course_load_error.setVisibility(View.GONE);
	        if(loading)
	            mess_course_loading.setVisibility(View.VISIBLE);
	        else
	            mess_course_loading.setVisibility(View.GONE);
	    }
	public class GridAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (mList != null)
				return mList.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (lmap.get(posi) == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(
						B_Side_Course_Select_Image.this).inflate(
						R.layout.item_side_course_select, null);
				holder.pb = (ProgressBar) converView.findViewById(R.id.pb);
				holder.rela_course_bg = (RelativeLayout) converView.findViewById(R.id.rala_course_bg);
				holder.por = (ImageView) converView
						.findViewById(R.id.iv_course_bg_thumb);
				holder.change = (ImageView) converView
						.findViewById(R.id.iv_course_bg_tag_change);
				holder.name = (TextView) converView
						.findViewById(R.id.tv_group_member_name);
				lmap.put(posi, converView);
				converView.setTag(holder);
			} else {
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			  final ViewHolder holder1 = holder;
			final String url = mList.get(posi).getPhoto_url();
				try {
					if(sp.getString("select", "").equals(getPath(url))){
							holder.change.setVisibility(View.VISIBLE);
					}else{
						holder.change.setVisibility(View.GONE);
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			RelativeLayout.LayoutParams lp_menpiao1 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp_menpiao1.width = (int) ((A_0_App.screenWidth-200)/3);
			lp_menpiao1.height = (int) ((A_0_App.screenWidth-200)/3);
			holder.por.setLayoutParams(lp_menpiao1);
			if(holder.por.getTag() == null){
			    PubMehods.loadServicePic(imageLoader,url,holder.por, options);
			    holder.por.setTag(url);
			}else{
			    if(!holder.por.getTag().equals(url)){
			        PubMehods.loadServicePic(imageLoader,url,holder.por, options);
			        holder.por.setTag(url);
			    }
			}
			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						File file = new File(getPath(url));
						if (file.exists()) {
							 Editor editor = sp.edit();
							  editor.putString("select", getPath(url));
							  editor.commit();
							 adapter.notifyDataSetChanged();
						} else {

							if (fileSize==0) {
								 new Thread(new Runnable() {
									
									@Override
									public void run() {

										try {
											URL u = new URL(url);
											URLConnection conn = u.openConnection();
											conn.connect();
											InputStream is = conn.getInputStream();
											fileSize = conn.getContentLength();
											if (fileSize>1 || is!= null) {
												B_Side_Course_Select_Image.this.runOnUiThread(new Runnable()  
										        {  
										            public void run()  
										            {  holder1.name.setVisibility(View.VISIBLE);
										            	 holder1.pb.setVisibility(View.VISIBLE);
														 holder1.pb.setVisibility(ProgressBar.VISIBLE);
														 holder1.pb.setMax(fileSize);
										               
										            }  
										  
										        }); 
												
												FileOutputStream fos = new FileOutputStream(getPath(url));
												byte[] bytes = new byte[1024];
												int len = -1;
												while ((len = is.read(bytes)) != -1) {
													fos.write(bytes, 0, len);
													downloadSize += len;
													B_Side_Course_Select_Image.this.runOnUiThread(new Runnable()  
											        {  
											            public void run()  
											            {  
											            	 holder1.name.setText(downloadSize * 100 / fileSize+"%");
											            	 holder1.pb.setProgress(downloadSize);
											            }  
											  
											        }); 
												}
												B_Side_Course_Select_Image.this.runOnUiThread(new Runnable()  
										        {  
										            public void run()  
										            
										            
										            {  
										            	 adapter.notifyDataSetChanged();
										            	 holder1.name.setVisibility(View.GONE);
										            	 holder1.pb.setVisibility(View.GONE);
														 holder1.pb.setVisibility(ProgressBar.GONE);
														 try {
																if (getPath(url).endsWith(".jpg")
																		|| getPath(url).endsWith(".png")) {
																	FileInputStream fis = new FileInputStream(getPath(url));
																}
																downloadSize = 0;
																fileSize = 0;
																A_0_App.course_bg_index=posi;
															} catch (FileNotFoundException e) {
																e.printStackTrace();
															} catch (IOException e) {
																e.printStackTrace();
															}
										            }  
										  
										        }); 
												is.close();
												fos.close();
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									
									}
								}).start();}
						
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}

			});

			return converView;
		}
	}

	static class ViewHolder { // 自定义控件集合
		public ImageView por,change;
		public TextView name;
		public TextView content_tv;
		public ProgressBar pb;
		public RelativeLayout rela_course_bg;
	}


	/**
	 * 文件一共的大小
	 */
	int fileSize = 0;
	/**
	 * 已经下载的大小
	 */
	int downloadSize = 0;
    

	/**
	 * 得到文件的保存路径
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getPath(String image_url) throws IOException {
		String path = FileCourse.setMkdir(this) + File.separator
				+ image_url.substring(image_url.lastIndexOf("/") + 1);
		return path;
		//http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg
		///storage/emulated/0/myfile/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			goData();
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		default:
			break;
		}

	}

	private void goData() {
		Intent it = new Intent();
		it.putExtra("read_count", "0");
		it.putExtra("repley_count", "0");
		setResult(1, it);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				// goData();
				finish();
				overridePendingTransition(R.anim.animal_push_right_in_normal,
						R.anim.animal_push_right_out_normal);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Course_Select_Image.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Course_Select_Image.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter = null;
		mList = null;
	}
}

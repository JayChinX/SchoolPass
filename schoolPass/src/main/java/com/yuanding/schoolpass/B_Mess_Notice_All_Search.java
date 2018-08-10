package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Con;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Notice;
import com.yuanding.schoolpass.service.Api.InterMessSearchList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.MyListView;

/**
 * 
 * @version 创建时间：2015年11月12日 下午3:18:18 全局搜索
 */
public class B_Mess_Notice_All_Search extends A_0_CpkBaseTitle_Navi{
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap_col = new HashMap<Integer, View>();
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap_stu = new HashMap<Integer, View>();
	@SuppressLint("UseSparseArrays")
	private View mLinerReadDataError, mLinerNoContent,liner_lecture_list_whole_view, side_lecture__loading;
	private MyListView listview_user,listview_mess;
	private List<Cpk_Search_Mess_Con> mess_Cons;
	private List<Cpk_Search_Mess_Notice> mess_Notices;
	private Mydapter_User adapter_user;
	private Mydapter_Mess adapter_mess;
	protected Context mContext;
	private EditText mSearchInput;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options_photo;
	private String key="";
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	private TextView search_user,search_mess;
    private RelativeLayout rela_user,rela_mess;
    private Button btn_user,btn_mess;
    private Boolean firstLoad = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_mess_all_search);
		A_0_App.getInstance().addActivity_rongyun(this);
		setTitleText("搜索");
		firstLoad = true;
		imageLoader = A_0_App.getInstance().getimageLoader();
		options_photo  = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		search_user = (TextView) findViewById(R.id.search_user);
		search_mess = (TextView) findViewById(R.id.search_mess);
		rela_user = (RelativeLayout) findViewById(R.id.rela_user);
		rela_mess = (RelativeLayout) findViewById(R.id.rela_mess);
		btn_user = (Button) findViewById(R.id.btn_user);
		btn_mess = (Button) findViewById(R.id.btn_mess);
		
		mSearchInput = (EditText)findViewById(R.id.school_friend_member_search_input);
		liner_lecture_list_whole_view =findViewById(R.id.liner_lecture_list_whole_view);
		listview_user = (MyListView)findViewById(R.id.listview_user);
		listview_mess = (MyListView)findViewById(R.id.listview_mess);
		
		listview_user.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				display();
				return false;
			}
		});
		listview_mess.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				display();
				return false;
			}
		});
		listview_user.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(B_Mess_Notice_All_Search.this,
						B_Mess_Persion_Info.class);
				intent.putExtra("uniqid",mess_Cons.get(position).getUniqid());
				startActivity(intent);
			
			
			}
		});
		
		listview_mess.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

					if (mess_Notices.get(position).getType().equals("4")) {// 短信文本消息
						Intent intent = new Intent(B_Mess_Notice_All_Search.this,B_Mess_Notice_Detail_MessText.class);
						intent.putExtra("acy_type", 2);// 正常列表进入
						intent.putExtra("message_id", mess_Notices.get(position).getMessage_id());
						intent.putExtra("mess_content", mess_Notices.get(position).getTitle());
						startActivityForResult(intent, 1);
					} else {// 正常消息
						Intent intent = new Intent(B_Mess_Notice_All_Search.this,B_Mess_Notice_Detail.class);
						intent.putExtra("acy_type", 2);// 正常列表进入
						intent.putExtra("message_id", mess_Notices.get(position).getMessage_id());
						intent.putExtra("mess_content",mess_Notices.get(position).getTitle());
						startActivityForResult(intent, 1);
					
					
					
				}
			
			
			}
		});
		side_lecture__loading = findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		//tv_blank_name.setText("没有查找到收信人~");
		//iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		
		mLinerReadDataError.setOnClickListener(onClick);
		rela_mess.setOnClickListener(onClick);
		rela_user.setOnClickListener(onClick);
		btn_user.setOnClickListener(onClick);
		btn_mess.setOnClickListener(onClick);
		mess_Cons = new ArrayList<Cpk_Search_Mess_Con>();
		mess_Notices = new ArrayList<Cpk_Search_Mess_Notice>();
		
		adapter_mess = new Mydapter_Mess();
		adapter_user = new Mydapter_User();
		
		listview_mess.setAdapter(adapter_mess);
		listview_user.setAdapter(adapter_user);
//		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//					if (mSearchInput.getText().toString().length() > 0) {
//						key=mSearchInput.getText().toString();
//						showLoadResult(true, false, false, false);
//						getLectureList(key,false);
//						 display();
//					}
//
//					return true;
//
//				}
//
//				return false;
//			}
//
//		});
		
       mSearchInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
                // 文字变动 ， 有未发出的搜索请求，应取消
				key=mSearchInput.getText().toString();
                if (mHandler.hasMessages(MSG_SEARCH)) {
                    mHandler.removeMessages(MSG_SEARCH);
                }
                if (TextUtils.isEmpty(arg0)) {
                    showLoadResult(false, false, false, true);
                } else {// 延迟搜索
                    mHandler.sendEmptyMessageDelayed(MSG_SEARCH, TIME_INPUT_REQUEST); // 自动搜索功能
                }
			
			}
		});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
	}
	
	 private static final int MSG_SEARCH = 1;
	    public static final int TIME_INPUT_REQUEST = 800;
	    private Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message msg) {
	            //搜索请求
	            if (mSearchInput.getText().toString().length() > 0) {
	                showLoadResult(true, false, false, false);
	                getLectureList( mSearchInput.getText().toString(),false);
	            }else{
	                getLectureList( mSearchInput.getText().toString(),false);
	            }
	        }
	    };
	    
	
	private void getLectureList(String keyword,final boolean pullRefresh) {
		A_0_App.getApi().getMessSearchList(B_Mess_Notice_All_Search.this,A_0_App.USER_TOKEN,"0","",keyword,
				new InterMessSearchList() {
			@Override
			public void onSuccess(List<Cpk_Search_Mess_Con> mess_Con,List<Cpk_Search_Mess_Notice> mess_Notice,String userIsMore,String messageIsMore) {

						if (B_Mess_Notice_All_Search.this.isFinishing())
							return;
						if ((mess_Con != null && mess_Con.size() > 0)||(mess_Notice != null && mess_Notice.size() > 0)) {
							mess_Cons.clear();
							mess_Cons = mess_Con;
							
							mess_Notices.clear();
							mess_Notices = mess_Notice;
							
							
							adapter_mess.notifyDataSetChanged();
							adapter_user.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
							if (userIsMore.equals("1")) {
								rela_user.setVisibility(View.VISIBLE);
								
							} else {
								
								rela_user.setVisibility(View.GONE);
							}
							if (messageIsMore.equals("1")) {
								
								rela_mess.setVisibility(View.VISIBLE);
							} else {
								search_user.setVisibility(View.GONE);
								rela_mess.setVisibility(View.GONE);
							}
							if (mess_Con.size()>0) {
								search_user.setVisibility(View.VISIBLE);
							}else{
								search_user.setVisibility(View.GONE);
							}
							if (mess_Notice.size() > 0) {
								search_mess.setVisibility(View.VISIBLE);
							} else {
								search_mess.setVisibility(View.GONE);
							}
						} else {
							
							mess_Cons.clear();
							mess_Notices.clear();
							search_mess.setVisibility(View.GONE);
							search_user.setVisibility(View.GONE);
							adapter_mess.notifyDataSetChanged();
							adapter_user.notifyDataSetChanged();
							rela_mess.setVisibility(View.GONE);
							rela_user.setVisibility(View.GONE);
							showLoadResult(false, true, false, false);
						}

					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (B_Mess_Notice_All_Search.this.isFinishing())
                            return;
                        //share_bottom.setVisibility(View.GONE);
                    	mess_Cons.clear();
						mess_Notices.clear();
						search_mess.setVisibility(View.GONE);
						search_user.setVisibility(View.GONE);
						adapter_mess.notifyDataSetChanged();
						adapter_user.notifyDataSetChanged();
						showLoadResult(false, true, false, false);
                        
                        PubMehods.showToastStr(B_Mess_Notice_All_Search.this, msg);
                        showLoadResult(false, true, false, false);
                    
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}



	private void showLoadResult(boolean loading, boolean wholeView,
			boolean loadFaile, boolean noData) {
		if (wholeView)
			liner_lecture_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_lecture_list_whole_view.setVisibility(View.GONE);
		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);

		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
		
		if (loading) {
				drawable.start();
				side_lecture__loading.setVisibility(View.VISIBLE);
			} else {
				if (drawable != null) {
					drawable.stop();
				}
				side_lecture__loading.setVisibility(View.GONE);
			}
	}

	
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		Intent intent;
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rela_user:
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
				intent=new Intent(B_Mess_Notice_All_Search.this, B_Mess_Notice_All_Search_MoreUser.class);
				intent.putExtra("type", "1");//联系人
				intent.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent);}
				break;
			case R.id.rela_mess:
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
				 intent=new Intent(B_Mess_Notice_All_Search.this, B_Mess_Notice_All_Search_MoreMess.class);
				intent.putExtra("type", "2");//通知
				intent.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent);}
				break;
          case R.id.btn_user:
			  if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
				 intent=new Intent(B_Mess_Notice_All_Search.this, B_Mess_Notice_All_Search_MoreUser.class);
				 intent.putExtra("type", "1");//联系人
				 intent.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent);}
				break;
			case R.id.btn_mess:
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
				intent=new Intent(B_Mess_Notice_All_Search.this, B_Mess_Notice_All_Search_MoreMess.class);
				intent.putExtra("type", "2");//通知
				intent.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent);}
				break;
			case R.id.side_lecture_load_error:
				
				showLoadResult(true, false, false, false);
				getLectureList(key, true);
				break;
			default:
				break;
			}
		}
	};

	public class Mydapter_User extends BaseAdapter {

		@Override
		public int getCount() {
			if (mess_Cons != null)
				return mess_Cons.size();
			return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}

		@Override
		public long getItemId(int v) {
			return v;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap_col.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Mess_Notice_All_Search.this)
						.inflate(R.layout.item_mess_search, null);
				holder.tv_name = (TextView) converView
						.findViewById(R.id.tv_black_list_name);
				holder.tv_phone = (TextView) converView
						.findViewById(R.id.tv_black_list_phone);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_black_list_por);
				lmap_col.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap_col.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			if (posi % 8 == 0) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_one);

			} else if (posi % 8 == 1) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_eight);
			}
			
			
			String uri = mess_Cons.get(posi).getPhoto_url();
			if(holder.iv_photo.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			if (key.toCharArray().length>0) {
				 String temp=mess_Cons.get(posi).getTrue_name();
              for (int i = 0; i < key.toCharArray().length; i++) {
            	  if (temp.contains(key.toCharArray()[i]+"")) {
            		  if (!("<font color='#49c433'></font>").contains(key.toCharArray()[i]+"")) {
            			  temp=temp.replace(key.toCharArray()[i]+"", "<font color='#49c433'>"+key.toCharArray()[i]+"</font>");
					}
            		
				}
				}
              holder.tv_name.setText(Html.fromHtml(temp));
			}
			
			if (mess_Cons.get(posi).getType().equals("1")) {
				holder.tv_phone.setText("教师");
			} else {
				holder.tv_phone.setText("学生");
			}
			
			
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
//		converView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(B_Mess_Notice_All_Search.this,
//							B_Mess_Persion_Info.class);
//					intent.putExtra("uniqid",mess_Cons.get(posi).getUniqid());
//					startActivity(intent);
//				
//				}
//			});
			return converView;
		}

	}

	public class Mydapter_Mess extends BaseAdapter {

		@Override
		public int getCount() {
			if (mess_Notices != null)
				return mess_Notices.size();
			return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}

		@Override
		public long getItemId(int v) {
			return v;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap_stu.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Mess_Notice_All_Search.this)
						.inflate(R.layout.item_mess_message_search, null);
				holder.tv_mess_title = (TextView) converView
						.findViewById(R.id.tv_mess_title);
				holder.tv_mess_time = (TextView) converView
						.findViewById(R.id.tv_mess_time);
				holder.tv_mess_sort = (TextView) converView
						.findViewById(R.id.tv_mess_sort);
				lmap_stu.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap_stu.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			
			if (key.toCharArray().length>0) {
				 String temp=mess_Notices.get(posi).getTitle();
             for (int i = 0; i < key.toCharArray().length; i++) {
            	 if (temp.contains(key.toCharArray()[i]+"")) {
            		 if (!("<font color='#49c433'></font>").contains(key.toCharArray()[i]+"")) {
           	        temp=temp.replace(key.toCharArray()[i]+"", "<font color='#49c433'>"+key.toCharArray()[i]+"</font>");
				}}}
             holder.tv_mess_title.setText(Html.fromHtml(temp));
			}
			
			holder.tv_mess_time.setText(PubMehods.getFormatDate(Long.valueOf(mess_Notices.get(posi).getCreate_time()),"MM/dd HH:mm"));
			holder.tv_mess_sort.setText(mess_Notices.get(posi).getApp_msg_sign());
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
			return converView;
		}

	} 
	class ViewHolder {
		TextView tv_name,tv_mess_title,tv_mess_time,tv_mess_sort;
		TextView tv_phone;
		CircleImageView iv_photo;
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!firstLoad) {
			display();
		} else {
			mSearchInput.requestFocus();
			openKeybord(mSearchInput, B_Mess_Notice_All_Search.this);
			firstLoad = false;
		}
		//display();
	}
	@Override
	public void onDestroy() {
		if (mess_Cons != null) {
			mess_Cons.clear();
		}
		if (mess_Notices != null) {
			mess_Notices.clear();
		}
		drawable.stop();
		drawable=null;
		adapter_mess = null;
		adapter_user = null;
		super.onDestroy();
	}
	public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }
	 @SuppressLint("ClickableViewAccessibility")
	    private  void openKeybord(EditText mEditText, Context mContext) {
	        InputMethodManager imm = (InputMethodManager) mContext
	                .getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	    }
	 private void display(){
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);

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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Search.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Notice_All_Search.this,AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			display();
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		default:
			break;
		}
	}
	
}

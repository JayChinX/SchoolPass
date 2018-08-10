package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AlbumHelper;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.FileUtils;
import com.yuanding.schoolpass.utils.ImageGridAdapter;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.ImageGridAdapter.TextCallback;
import com.yuanding.schoolpass.utils.ImageItem;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * 
 * @author MyPC 选择相册
 *
 */
public class B_Side_Found_Add_ImageGridActivity extends A_0_CpkBaseTitle_Navi {
	
    public static final String EXTRA_IMAGE_LIST = "imagelist";
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	private String type;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				PubMehods.showToastStr(B_Side_Found_Add_ImageGridActivity.this, "最多选择3张图片");
				break;

            default:
                break;
            }
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView(R.layout.activity_image_grid);
        setTitleText("相册");
//      showTitleBt(TEXT_BUTTON, true);
//		setTextBtn("完成");
		showTitleBt(ZUI_RIGHT_TEXT, true);
		setZuiYouText("完成");
		Bimp.act_bool=true;
		type=getIntent().getStringExtra("type");
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		initView();
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

	private void initView() {
		
		
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(B_Side_Found_Add_ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				if (count==0) {
					setZuiYouText("确定");
				}else {
					setZuiYouText("完成" + "(" + count + ")");
				}
				
			}
		});

        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}

        });

    }

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			
//		    Bimp.bmp.clear();
//			Bimp.drr.clear();
//			Bimp.max = 0;										
//			FileUtils.deleteDir();
//			if (Bimp.act_bool) {
//				if (Bimp.add_edit.equals("1")) {
//					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
//							B_Side_Found_Edit_Found.class);
//					Bimp.edit_biaoshi="2";
//					intent.putExtra("type", type);
//					startActivity(intent);
//					Bimp.act_bool = false;
//				} else if (Bimp.add_edit.equals("2")) {
//
//					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
//							B_Side_Found_Add_Found.class);
//					intent.putExtra("type", type);
//					startActivity(intent);
//					Bimp.act_bool = false;
//
//				}
//
//			}
			finish();
			break;
		case ZUI_RIGHT_TEXT:

            ArrayList<String> list = new ArrayList<String>();
            Collection<String> c = adapter.map.values();
            Iterator<String> it = c.iterator();
            for (; it.hasNext();) {
                list.add(it.next());
            }
            if (Bimp.act_bool) {
				if (Bimp.add_edit.equals("1")) {
					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
							B_Side_Found_Edit_Found.class);
					intent.putExtra("type", type);
					Bimp.edit_biaoshi="2";
					startActivity(intent);
					Bimp.act_bool = false;
					 finish();
				} else if (Bimp.add_edit.equals("2")) {

					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
							B_Side_Found_Add_Found.class);
					intent.putExtra("type", type);
					startActivity(intent);
					Bimp.act_bool = false;
					finish();
				}else{
					Bimp.act_bool = false;
				    finish();
			}

			}
           A_0_App.getInstance().exit_rongyun(true);
			for (int i = 0; i < list.size(); i++) {
				if (Bimp.drr.size() <3) {
					
					Bimp.drr.add(list.get(i));
				}
			}
			
			
		
			break;
		default:
			break;
		}
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Add_ImageGridActivity.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_Add_ImageGridActivity.this, AppStrStatic.kicked_offline());
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
	    public boolean dispatchKeyEvent(KeyEvent event) {
	        if (event.getAction() == KeyEvent.ACTION_DOWN) {
	            switch (event.getKeyCode()) {
	                case KeyEvent.KEYCODE_BACK:

//	                	if (Bimp.act_bool) {
//	        				if (Bimp.add_edit.equals("1")) {
//	        					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
//	        							B_Side_Found_Edit_Found.class);
//	        					Bimp.edit_biaoshi="2";
//	        					intent.putExtra("type", type);
//	        					startActivity(intent);
//	        					Bimp.act_bool = false;
//	        				} else if (Bimp.add_edit.equals("2")) {
//
//	        					Intent intent = new Intent(B_Side_Found_Add_ImageGridActivity.this,
//	        							B_Side_Found_Add_Found.class);
//	        					intent.putExtra("type", type);
//	        					startActivity(intent);
//	        					Bimp.act_bool = false;
//
//	        				}
//
//	        			}
	        			finish();
	                    return true;
	                default:
	                    break;
	            }
	        }
	        return super.dispatchKeyEvent(event);
	    }
}

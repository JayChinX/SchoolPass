package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_Type;
import com.yuanding.schoolpass.service.Api.InterSideRepairSent;
import com.yuanding.schoolpass.service.Api.InterSideRepairTypeList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 新增报修
 */
public class B_Side_Repair_Add extends A_0_CpkBaseTitle_Navi {
    private EditText et_title,et_address,et_phone,et_content;
    /**
	 * 类型选择
	 */
	private MySignAdapter adapter;
	private List<Cpk_Side_Repair_Type> mClassList = new ArrayList<Cpk_Side_Repair_Type>();
	private int dialog_show = 0;
	private EditText tv_type;
	private String type_id;
	private String stringphone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_repair_add);
		setTitleText("新增报修");
//		showTitleBt(TEXT_BUTTON, true);
//		setTextBtn("提交");
        setZuiRightBtn(R.drawable.navigationbar_save);
        showTitleBt(ZUI_RIGHT_BUTTON, true);
		tv_type=(EditText) findViewById(R.id.tv_type);
		tv_type.setOnClickListener(onClick);
		et_title=(EditText) findViewById(R.id.et_repair_title);
		et_address=(EditText) findViewById(R.id.et_repair_address);
		et_phone=(EditText) findViewById(R.id.et_repair_phone);
		et_content=(EditText) findViewById(R.id.et_repair__content);
		adapter = new MySignAdapter();
		getData();
		
		stringphone=A_0_App.USER_PHONE;
		et_phone.setText(stringphone);
		tv_type.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
				dialog_show = 1;
				getData();
				}else{}
			}
		});
		et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() > 0) {
					
				} else {
					//btn_sent.setBackgroundResource(R.drawable.unchecked);
				}
			}
		});
		
		/**
		 * 过滤表情
		 */
		et_title.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				  int index = et_title.getSelectionStart() - 1;
				                  if (index > 0) {
				                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
				                        Editable edit = et_title.getText();
				                          edit.delete(index, index + 1);
				                     }
				                 }
			}
		});
		/**
		 * 过滤表情
		 */
		et_address.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				  int index = et_address.getSelectionStart() - 1;
				                  if (index > 0) {
				                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
				                        Editable edit = et_address.getText();
				                          edit.delete(index, index + 1);
				                     }
				                 }
			}
		});
		/**
		 * 过滤表情
		 */
		et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				  int index = et_content.getSelectionStart() - 1;
				                  if (index > 0) {
				                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
				                        Editable edit = et_content.getText();
				                          edit.delete(index, index + 1);
				                     }
				                 }
			}
		});
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			//goData();
			finish();
			break;
			
		case ZUI_RIGHT_BUTTON:
			if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
				sent();
			} else {
//				PubMehods.showToastStr(B_Side_Repair_Add.this,"请勿重复操作！");
			}
			
			break;
		default:
			break;
		}

	}
	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_type:
				dialog_show = 1;
				if (mClassList.size()>0) {
					sign_Dialog();
				}else{
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
						getData();
					}
				}
				
				break;

			default:
				break;
			}
			
		}
		
	};
    public void sent(){
    	if (et_title.getText().toString().equals("")) {
    		PubMehods.showToastStr(B_Side_Repair_Add.this,
					"请输入报修标题");
			return;
		}
    	if (et_address.getText().toString().equals("")) {
    		PubMehods.showToastStr(B_Side_Repair_Add.this,
					"请输入报修地址");
			return;
		}
    	if (et_content.getText().toString().equals("")) {
    		PubMehods.showToastStr(B_Side_Repair_Add.this,
					"请输入报修内容");
			return;
		}
    	if (et_phone.getText().toString().length()>0) {
    		stringphone=et_phone.getText().toString();
		} else {
			stringphone=A_0_App.USER_PHONE;
			
		}
    	if (!PubMehods.isMobileNO(stringphone)) {
			PubMehods.showToastStr(B_Side_Repair_Add.this, "请填写正确的手机号");
			return;
		}
    	 if (isFinishing())
             return;
         A_0_App.getInstance().showProgreDialog(
                 B_Side_Repair_Add.this, "提交中，请稍候",true);
    	A_0_App.getApi().SideRepairSent(A_0_App.USER_TOKEN, et_title.getText().toString(),et_address.getText().toString(),
    			type_id,stringphone,et_content.getText().toString(),
				new InterSideRepairSent() {

					@Override
					public void onSuccess() {
		                if (isFinishing())
		                    return;
						A_0_App.getInstance().CancelProgreDialog(
								B_Side_Repair_Add.this);
						PubMehods.showToastStr(
								B_Side_Repair_Add.this, "提交成功！");
						finish();
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
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Repair_Add.this);
                        PubMehods.showToastStr(
                                B_Side_Repair_Add.this, msg);
                    
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
    }
    /**
	 * 类型数据
	 */
	private void getData() {
		mClassList.clear();
		A_0_App.getApi().getSideRepairTypeList(
				B_Side_Repair_Add.this, A_0_App.USER_TOKEN,
				new InterSideRepairTypeList() {

					public void onSuccess(List<Cpk_Side_Repair_Type> List) {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(
								B_Side_Repair_Add.this);
						mClassList.addAll(List);
						if (dialog_show == 0) {
							if (List.size()>0) {
								type_id=List.get(0).getType_id();
								tv_type.setText(List.get(0).getType_name());
							}
							
						} else {
							sign_Dialog();
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
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Repair_Add.this);
                        PubMehods.showToastStr(
                                B_Side_Repair_Add.this, msg);

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}
	private void goData() {
		Intent it = new Intent();
		it.putExtra("read_count", "0");
		it.putExtra("repley_count", "0");
		setResult(1, it);
	}
	/**
	 * 签名选择
	 */
	void sign_Dialog() {
		final Dialog dialog = new Dialog(B_Side_Repair_Add.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.activity_side_repair_select_type_dialog);
		/*
		 * Window window = dialog.getWindow();
		 * window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		 * window.setWindowAnimations(R.style.mystyle); // 添加动画
		 */dialog.show();
		 LinearLayout liner_side_repair=(LinearLayout) dialog.findViewById(R.id.liner_side_repair);
		ListView listView = (ListView) dialog.findViewById(R.id.lv_side_select_sign);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				type_id=mClassList.get(arg2).getType_id();
				tv_type.setText(mClassList.get(arg2).getType_name());
				dialog.dismiss();
			}
		});
		liner_side_repair.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				dialog.dismiss();
				return false;
			}
		});
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
	 * 
	 * @author MyPC签名adpter
	 * 
	 */
	// 加载列表数据
	public class MySignAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mClassList != null)
				return mClassList.size();
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
		public View getView(int posi, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(
						B_Side_Repair_Add.this).inflate(
						R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView
					.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mClassList.get(posi).getType_name());
			
//				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//				an.setDuration(400);
//				an.setStartOffset(50*posi);
//			   converView.startAnimation(an);
			
			return converView;
		}

	}
	private void clearBusinessList(boolean setNull) {
		if (mClassList != null) {
			mClassList.clear();
			if (setNull)
				mClassList = null;
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Repair_Add.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Repair_Add.this,AppStrStatic.kicked_offline());
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
		clearBusinessList(true);
		adapter=null;
		super.onDestroy();
	}
}
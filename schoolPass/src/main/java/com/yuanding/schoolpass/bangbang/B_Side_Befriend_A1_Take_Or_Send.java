package com.yuanding.schoolpass.bangbang;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.Pub_WebView_Load_Acy;
import com.yuanding.schoolpass.Pub_WebView_Load_Coupon;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bangbang.pay.B_Side_Befriend_B1_Start_Pay_Dialog;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Befriend_Task_Form;
import com.yuanding.schoolpass.bangbang.pay.weixin.WXHelper;
import com.yuanding.schoolpass.bangbang.pay.weixin.WXPayEventBus;
import com.yuanding.schoolpass.bangbang.pay.zhifubao.ZFBHelper;
import com.yuanding.schoolpass.bangbang.pay.zhifubao.ZFBHelper.ZFBPayCallBack;
import com.yuanding.schoolpass.service.Api.InterSideHelpTakeOrSendSent;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.CashierInputFilter;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.date.SlideDateTimeListener;
import com.yuanding.schoolpass.view.date.SlideDateTimePicker;


/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 帮取 帮送
 */
public class B_Side_Befriend_A1_Take_Or_Send extends A_0_CpkBaseTitle_Navi {

	private EditText takeAddress, sendAddress, takeTelephone, sendTelephone, goodsName, timeTake, other, numPay,for_help_pay_coupon;
	private ImageView selectProtocol, getPhone, getOPhone;
	boolean agreeProtocol = true;
	private TextView helpProtocol, numMoney, startPay, takeOrSend;
	private String myPhone;
	int type;
	private Boolean pass = false;
	private SlideDateTimeListener listener;
	private String token = A_0_App.USER_TOKEN;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	int a;
	//优惠券
	private String money_coupon="";
	private String coupon_id="";
	private String coupon_status="0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setView(R.layout.activity_befriend_task_for_help_take_or_send);

		// 微信支付结果 注册EventBus
		EventBus.getDefault().register(this);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 1);

		initView();
		initData();
	}

	private void initView() {
		// TODO 自动生成的方法存根
		takeAddress = (EditText) findViewById(R.id.for_help_take_address);
		sendAddress = (EditText) findViewById(R.id.for_help_send_address);
		takeTelephone = (EditText) findViewById(R.id.for_help_take_telephone);
		sendTelephone = (EditText) findViewById(R.id.for_help_send_telephone);
		for_help_pay_coupon = (EditText) findViewById(R.id.for_help_pay_coupon);

		goodsName = (EditText) findViewById(R.id.for_help_goods);
		timeTake = (EditText) findViewById(R.id.for_help_time);
		other = (EditText) findViewById(R.id.for_help_take_other);
		numPay = (EditText) findViewById(R.id.for_help_pay_num);
		// 设置软键盘
		takeTelephone.setInputType(InputType.TYPE_CLASS_NUMBER);
		sendTelephone.setInputType(InputType.TYPE_CLASS_NUMBER);
		InputFilter[] filters = { new CashierInputFilter() };
		numPay.setFilters(filters);

		selectProtocol = (ImageView) findViewById(R.id.for_help_protocol_select);

		helpProtocol = (TextView) findViewById(R.id.for_help_protocol_help);
		numMoney = (TextView) findViewById(R.id.for_help_num_pay);
		startPay = (TextView) findViewById(R.id.for_help_start_pay);

		takeOrSend = (TextView) findViewById(R.id.for_help_tors);

		getPhone = (ImageView) findViewById(R.id.for_help_take_get_telephone);
		getOPhone = (ImageView) findViewById(R.id.for_help_send_telephone_get);

		 if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
	            startListtenerRongYun();// 监听融云网络变化
	        }
		for_help_pay_coupon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//我的邦豆
				if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
					PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, R.string.str_no_certified_open);
					return;
				}
				if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
						||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
					A_0_App.getInstance().enter_Perfect_information(B_Side_Befriend_A1_Take_Or_Send.this,true);
					return;
				}
				if(numPay.getText().toString().equals("")){
					PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "酬金不能为空!");
					return;
				}
				BigDecimal b1=new BigDecimal(numPay.getText().toString());
				BigDecimal b3=new BigDecimal(100);
				String url_c =A_0_App.USER_COUPON_USE_URL+"&type="+type+"&amount="+(b1.multiply(b3)).intValue();
				if(url_c == null || url_c.equals("")){
					PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "数据请求失败，请重试");
					return;
				}
				Intent intent = new Intent(B_Side_Befriend_A1_Take_Or_Send.this, Pub_WebView_Load_Coupon.class);
				intent.putExtra("title_text", "优惠券");
				intent.putExtra("url_text", url_c);
				intent.putExtra("tag_skip", "0");
				intent.putExtra("tag_show_refresh_btn", "1");
				intent.putExtra("enter", "1");//身边进入
				startActivityForResult(intent, 3);
			}
		});
	}

	private void initData() {
		// TODO 自动生成的方法存根
		myPhone = A_0_App.USER_PHONE;
		switch (type) {
		case 1:
			setTitleText("帮我取");
			sendTelephone.setText(myPhone);
			takeOrSend.setText("取件时间");
			break;
		case 2:
			setTitleText("帮我送");
			takeTelephone.setText(myPhone);
			takeOrSend.setText("送件时间");
			break;
		}

		getFull(takeAddress);
		getFull(sendAddress);
		getFull(takeTelephone);
		getFull(sendTelephone);
		getFull(goodsName);
		getFull(timeTake);
		getFull(other);
		getFull(numPay);

		selectProtocol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (agreeProtocol) {
					agreeProtocol = false;
					selectProtocol.setImageResource(R.drawable.register_box_unselected);
				} else {
					agreeProtocol = true;
					selectProtocol.setImageResource(R.drawable.register_box_selected);
				}

			}
		});
		helpProtocol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(B_Side_Befriend_A1_Take_Or_Send.this, Pub_WebView_Load_Acy.class);
				intent.putExtra("title_text", "帮帮用户协议");
				intent.putExtra("url_text", AppStrStatic.LINK_USER_BEFRIEND_HELP_AGREEMENT);
				intent.putExtra("tag_skip", "1");
				 intent.putExtra("tag_show_refresh_btn", "1");
				startActivity(intent);

			}
		});
		startPay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				if (agreeProtocol) {
					if (pass) {
						send();
					}
				} else {
					PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请选择同意帮帮用户协议");
				}

			}
		});
		timeTake.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				date_choose();
			}
		});

		getPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getTelePhone(1);
			}
		});
		getOPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getTelePhone(2);
			}
		});

		listener = new SlideDateTimeListener() {

			@Override
			public void onDateTimeSet(Date date) {

				String s = sdf.format(date);
				timeTake.setText(s);
			}

			@Override
			public void onDateTimeCancel() {

			}
		};

	}

	/*
	 * 对输入信息进行验证
	 */
	protected void send() {
		// TODO Auto-generated method stub
		if (takeAddress.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入取件地址");
			return;
		}
		if (sendAddress.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入送件地址");
			return;
		}
		if (takeTelephone.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入取件联系人电话");
			return;
		}
		// 1取 2送
		switch (type) {
		case 1:
			if (!PubMehods.isMobileNO(takeTelephone.getText().toString())) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入正确的手机号");
				return;
			}
			if (!PubMehods.isMobileNO(sendTelephone.getText().toString())) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入正确的手机号");
				return;
			}

			if (timeTake.getText().toString().equals("")) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请选择取件时间");
				return;
			}
			break;

		case 2:
			if (!PubMehods.isMobileNO(sendTelephone.getText().toString())) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入正确的手机号");
				return;
			}
			if (!PubMehods.isMobileNO(takeTelephone.getText().toString())) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入正确的手机号");
				return;
			}

			if (timeTake.getText().toString().equals("")) {
				PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请选择送件时间");
				return;
			}
			break;
		}

		if (numPay.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "请输入酬金");
			return;
		} else if (Double.parseDouble(numPay.getText().toString()) < AppStrStatic.PAY_MONEY) {
			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "酬金不能少于" + AppStrStatic.PAY_MONEY + "元");
			return;
		}
		init();

	}

	String ppp = "{\"appId\":\"wx41dbca0a55ba6ea7\",\"timeStamp\":\"1504227803\",\"nonceStr\":\"poqvHoqttx190w63\",\"package\":\"prepay_id=wx2017090109032333759f1c2a0043532902\",\"signType\":\"MD5\",\"paySign\":\"7EA68CB63DEA7AA469F62B866B7014EF\"}\n";
	private void init() {
		// TODO Auto-generated method stub
		Befriend_Task_Form.goods_name = goodsName.getText().toString();
		Befriend_Task_Form.take_phone = takeTelephone.getText().toString();
		Befriend_Task_Form.send_phone = sendTelephone.getText().toString();
		Befriend_Task_Form.take_add = takeAddress.getText().toString();
		Befriend_Task_Form.send_add = sendAddress.getText().toString();
		Befriend_Task_Form.data = timeTake.getText().toString();
		Befriend_Task_Form.others = other.getText().toString();
		if (money_coupon.equals("0")){
			Befriend_Task_Form.num_money = (int) (Double.parseDouble(numPay.getText().toString()) * 100);
		}else{
			BigDecimal b1=new BigDecimal(numPay.getText().toString());
			BigDecimal b3=new BigDecimal(100);
			Befriend_Task_Form.num_money=(b1.multiply(b3)).intValue();
		}
		// 选择支付方式弹窗
		final B_Side_Befriend_B1_Start_Pay_Dialog startPayDialog = new B_Side_Befriend_B1_Start_Pay_Dialog(
				B_Side_Befriend_A1_Take_Or_Send.this, R.style.Theme_dim_Dialog, numMoney.getText().toString());
		startPayDialog.show();
		startPayDialog.setClicklistener(new B_Side_Befriend_B1_Start_Pay_Dialog.ClickListenerInterface() {

			@Override
			public void doConfirm(int payWay) {
				// TODO Auto-generated method stub
				startPayDialog.dismiss();
//				post_data(payWay);
				final WXHelper wxHelper = new WXHelper(B_Side_Befriend_A1_Take_Or_Send.this);
//				PayStrStatic.TASK_ID = id;
				wxHelper.initApi(ppp);
			}

			@Override
			public void doCancel() {
				// TODO Auto-generated method stub
				startPayDialog.dismiss();
			}

		});

	}

	// 发布任务并支付
	public void post_data(int wayPay) {
		int order_from = wayPay;
		// A_0_App.getInstance().showProgreDialog(B_Side_Befriend_A1_Take_Or_Send.this,
		// "", true);
		A_0_App.getApi().SideHelpTakeOrSendSent(token, order_from, type, Befriend_Task_Form.goods_name,
				Befriend_Task_Form.take_phone, Befriend_Task_Form.send_phone, Befriend_Task_Form.take_add,
				Befriend_Task_Form.send_add, Befriend_Task_Form.data, Befriend_Task_Form.others,
				Befriend_Task_Form.num_money,coupon_status,coupon_id,money_coupon, new InterSideHelpTakeOrSendSent() {

					@Override
					public void onSuccess(final String id, String way, final String order) {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(B_Side_Befriend_A1_Take_Or_Send.this);
						Intent intent1 = new Intent("notice");
						sendBroadcast(intent1);
						clear();
//						PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "提交成功！");
						// 调起支付
						switch (way) {
						case "微信":
							final WXHelper wxHelper = new WXHelper(B_Side_Befriend_A1_Take_Or_Send.this);
							PayStrStatic.TASK_ID = id;
							wxHelper.initApi(order);
							break;
						case "支付宝":
							// 解析请求的pic得到orderInfo
							ZFBHelper zfbHelper = new ZFBHelper(B_Side_Befriend_A1_Take_Or_Send.this);
							zfbHelper.payV(order, new ZFBPayCallBack() {

								@Override
								public void onPaySuccess() {
									// TODO Auto-generated method stub
									// 支付成功跳转待领取页
									Intent intent = new Intent(B_Side_Befriend_A1_Take_Or_Send.this,
											B_Side_Befriend_C1_Task_Status_Details.class);
									intent.putExtra("type", 0);
									intent.putExtra("id", id);
									Log.i("支付宝", "支付成功");
									startActivity(intent);
									finish();
								}

								@Override
								public void onPayCancel() {
									// TODO Auto-generated method stub
									// 支付失败弹窗
									PayErrorOrCancel();
								}
							});
							break;
						}
					}
				}, new Inter_Call_Back() {

					@Override
					public void onFinished() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(String msg) {
						// TODO Auto-generated method stub
						if(isFinishing())
		                    return;
						PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, msg);
					}

					@Override
					public void onCancelled() {
						// TODO Auto-generated method stub

					}
				});
	}

	protected void clear() {
		// TODO Auto-generated method stub
		Befriend_Task_Form.goods_name = "";
		Befriend_Task_Form.take_phone = "";
		Befriend_Task_Form.send_phone = "";
		Befriend_Task_Form.take_add = "";
		Befriend_Task_Form.send_add = "";
		Befriend_Task_Form.data = "";
		Befriend_Task_Form.others = "";
		Befriend_Task_Form.num_money = 0;
	}

	public void date_choose() {

		new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(listener).setInitialDate(PubMehods.getFormatDate(PubMehods.getFormatTime(new Date()) + 20 * 60 * 1000 ))
				.setMinDate(PubMehods.getFormatDate(PubMehods.getFormatTime(new Date())))
				 .setMaxDate(PubMehods.getFormatDate(PubMehods.getFormatTime(new Date()) + 7 * 24 * 60 * 60 * 1000))
				.setIs24HourTime(true)
				// .setTheme(SlideDateTimePicker.HOLO_DARK)
				.setIndicatorColor(Color.parseColor("#1EC348")).build().show();
	}



	private void getFull(EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO 自动生成的方法存根
				if (arg0.toString().length() > 0) {
					String a = arg0.charAt(arg0.length() - 1) + "";
					Log.i("shuru", a + "");
					if (!a.equals(".")) {
						if (!numPay.getText().toString().equals("")) {
							DecimalFormat df = new DecimalFormat("0.00");
							double money = Double.parseDouble(numPay.getText().toString());
							double money1=0.00;
							if (money_coupon!=""){
								money1 = Double.parseDouble(money_coupon);
							}
							if (money_coupon!=""&&money>money1){

								numMoney.setText("￥" + df.format(money-money1));
							}else if(money_coupon!=""&&money<=money1){
								coupon_status="0";
								coupon_id="";
								money_coupon="";
								for_help_pay_coupon.setText("");
								numMoney.setText("￥" + df.format(money));
							}else {
								for_help_pay_coupon.setText("");
								numMoney.setText("￥" + df.format(money));
							}

							numMoney.setTextColor(0xffff6600);
						} else {
							numMoney.setText("￥2.00");
						}
					}
				}else {
					coupon_status="0";
					coupon_id="";
					money_coupon="";
					for_help_pay_coupon.setText("");
					numMoney.setText("￥2.00");
					numMoney.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
				if (!takeAddress.getText().toString().equals("") && !sendAddress.getText().toString().equals("")
						&& !takeTelephone.getText().toString().equals("")
						&& !sendTelephone.getText().toString().equals("") && !goodsName.getText().toString().equals("")
						&& !timeTake.getText().toString().equals("") && !numPay.getText().toString().equals("")) {
					if(money_coupon.equals("")||(!money_coupon.equals("")&&(Double.parseDouble(numPay.getText().toString())>Double.parseDouble(money_coupon)/100))){
						startPay.setBackgroundColor(0xffff9900);
						pass = true;
					}else{
						coupon_status="0";
						coupon_id="";
						PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this,"酬金必须大于优惠劵！");
					}


				} else {
					startPay.setBackgroundColor(0xffCCCCCC);
					pass = false;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO 自动生成的方法存根

			}
		});
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		// TODO 自动生成的方法存根
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		}
	}

	private static final int  REQUECT_CODE_CAMERA = 2;
	private static final int  REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
	private static final int REQUECT_CODE_CALLPHONE = 4;
	private static final int  REQUECT_CODE_READ_CONTACTS = 5;
	public void getTelePhone(int a) {
		this.a = a;
		    PermissionGen.needPermission(B_Side_Befriend_A1_Take_Or_Send.this, REQUECT_CODE_READ_CONTACTS,
            new String[] {
        Manifest.permission.READ_CONTACTS
    });
	}
	public void getTelephone(){
		Intent i = new Intent();
		i.setAction(Intent.ACTION_PICK);
		i.setData(ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(i, a);
	}
	//    PermissionGen.needPermission(MainActivity.this, 100,
//            new String[] {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CALL_PHONE
//    });
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	@PermissionSuccess(requestCode = REQUECT_CODE_READ_CONTACTS)
	public void requestContactsSuccess() {
		getTelephone();
	}

	@PermissionFail(requestCode = REQUECT_CODE_READ_CONTACTS)
	public void requestContactsFailed() {
		A_0_App.getInstance().PermissionToas("读取通讯录", B_Side_Befriend_A1_Take_Or_Send.this);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String phoneNumber = null;

		switch (requestCode) {
		case 1:

			switch (resultCode) {
			case RESULT_OK:
				if (data == null) {
					return;
				}

				Uri contactData = data.getData();
				if (contactData == null) {
					return;
				}
				Cursor cursor = managedQuery(contactData, null, null, null, null);
				if (cursor.moveToFirst()) {
					// String name = cursor.getString(cursor
					// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhone = cursor
							.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					if (hasPhone.equalsIgnoreCase("1")) {
						hasPhone = "true";
					} else {
						hasPhone = "false";
					}
					if (Boolean.parseBoolean(hasPhone)) {
						Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
						while (phones.moveToNext()) {
							phoneNumber = phones
									.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							setTitle(phoneNumber);
						}
						phones.close();
					}
				}
				if (phoneNumber != null) {
					takeTelephone.setText(removeAllSpace(phoneNumber));
				}else {
					A_0_App.getInstance().PermissionToas("读取通讯录", B_Side_Befriend_A1_Take_Or_Send.this);
				}
				break;
			}

			break;

		case 2:
			switch (resultCode) {
			case RESULT_OK:
				if (data == null) {
					return;
				}

				Uri contactData = data.getData();
				if (contactData == null) {
					return;
				}
				Cursor cursor = managedQuery(contactData, null, null, null, null);
				if (cursor.moveToFirst()) {
					// String name = cursor.getString(cursor
					// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhone = cursor
							.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					if (hasPhone.equalsIgnoreCase("1")) {
						hasPhone = "true";
					} else {
						hasPhone = "false";
					}
					if (Boolean.parseBoolean(hasPhone)) {
						Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
						while (phones.moveToNext()) {
							phoneNumber = phones
									.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							setTitle(phoneNumber);
						}
						phones.close();
					}
				}
				if (phoneNumber != null) {
					sendTelephone.setText(removeAllSpace(phoneNumber));
				}else {
					A_0_App.getInstance().PermissionToas("读取通讯录", B_Side_Befriend_A1_Take_Or_Send.this);
				}
				break;
			}
			case  3:
				if (data!=null){
				if (data.getStringExtra("money")!=null&&data.getStringExtra("money")!=""){
					coupon_status="1";
					coupon_id=data.getStringExtra("coupon_id");
					 money_coupon=data.getStringExtra("money");
					 for_help_pay_coupon.setText(Double.parseDouble(money_coupon)+"元");
					DecimalFormat df = new DecimalFormat("0.00");
					double money = Double.parseDouble(numPay.getText().toString());
					double money1=0.00;
					if (money_coupon!=""){
						money1 = Double.parseDouble(money_coupon);
					}
					if (money_coupon!=""&&money>=money1){

						numMoney.setText("￥" + df.format(money-money1));
					}else{
						numMoney.setText("￥" + df.format(money));
					}

					numMoney.setTextColor(0xffff6600);
				}else{
					coupon_status="0";
					coupon_id="";
					for_help_pay_coupon.setText("");
					money_coupon="0";
				}

				;}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void PayErrorOrCancel() {
		final B_Side_Befriend_B0_Take_Result_Dialog result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
				B_Side_Befriend_A1_Take_Or_Send.this, R.style.Theme_dim_Dialog, 4,null);
		result_Dialog.show();
		result_Dialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

			@Override
			public void doConfirm() {
				// TODO Auto-generated method stub
				// 重新支付
				result_Dialog.dismiss();
				send();
			}

			@Override
			public void doCancel() {
				// TODO Auto-generated method stub
				// 取消支付
				result_Dialog.dismiss();

			}
		});
	}

	public void onEventMainThread(WXPayEventBus eventBus) {
		// TODO Auto-generated method stub
		String msg = eventBus.getMsg();
		if (msg.equals("用户取消")) {
			PayErrorOrCancel();
		} else if (msg.equals("支付失败")) {
			PayErrorOrCancel();
		} else if (msg.equals("支付成功")) {
			// 支付成功跳转待领取页
//			PubMehods.showToastStr(B_Side_Befriend_A1_Take_Or_Send.this, "支付成功");
			Intent intent = new Intent(B_Side_Befriend_A1_Take_Or_Send.this,
					B_Side_Befriend_C1_Task_Status_Details.class);
			intent.putExtra("type", 0);
			intent.putExtra("id", PayStrStatic.TASK_ID);
			startActivity(intent);
			finish();
		}
	}
	public String removeAllSpace(String str)
	{
		String tmpstr=str.replace(" ","");
		String string = tmpstr.replace("-","");
		return string;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 解除EventBus
		EventBus.getDefault().unregister(this);
		finish();
	}

	 /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            switch (connectionStatus) {
            case CONNECTED:// 连接成功。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                break;
            case DISCONNECTED:// 断开连接。
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                //A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_0_Acy.this,getResources().getString(R.string.token_timeout));
                break;
            case CONNECTING:// 连接中。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                break;
            case NETWORK_UNAVAILABLE:// 网络不可用。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                class LooperThread extends Thread {
                    public void run() {
                        Looper.prepare();
                        A_0_App.getInstance().showExitDialog(B_Side_Befriend_A1_Take_Or_Send.this,
								AppStrStatic.kicked_offline());
                        Looper.loop();
                    }
                }
                LooperThread looper = new LooperThread();
                looper.start();
                break;
            }
        }
    }

}

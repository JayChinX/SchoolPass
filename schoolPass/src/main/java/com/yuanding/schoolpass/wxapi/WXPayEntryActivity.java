package com.yuanding.schoolpass.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import io.rong.eventbus.EventBus;


import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_A1_Take_Or_Send;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_C1_Task_Status_Details;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;
import com.yuanding.schoolpass.bangbang.pay.weixin.WXHelper;
import com.yuanding.schoolpass.bangbang.pay.weixin.WXPayEventBus;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 微信支付 官方标准类
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, PayStrStatic.WX_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		// 支付cheng'gong
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Intent intent = null;
			switch (resp.errCode) {
			case -1:
				// 签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
				Log.i("微信支付结果", "签名错误");
				PubMehods.showToastStr(this, "签名错误");
				EventBus.getDefault().post(new WXPayEventBus("支付失败"));
				finish();

				break;
			case -2:
				// 用户取消
				PubMehods.showToastStr(this, "用户取消支付成功");
				Log.i("微信支付结果", "用户取消支付");
				EventBus.getDefault().post(new WXPayEventBus("用户取消"));
				finish();
				break;
			case 0:
				// 成功
				Log.i("微信支付结果", "支付成功");
				PubMehods.showToastStr(this, "支付成功");
				EventBus.getDefault().post(new WXPayEventBus("支付成功"));
				finish();
				break;

			}
		}
	}
}
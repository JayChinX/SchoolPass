package com.yuanding.schoolpass.bangbang.pay.weixin;


import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 发布任务的微信支付
 */
public class WXHelper {

	public static IWXAPI wxApi;
	public static Context context;

	public WXHelper(Context context) {
		WXHelper.context = context;
	}

	public void initApi(String prepay) {

		JSONObject jsonObject;
		try {
			if (prepay != null) {
				jsonObject = new JSONObject(prepay);
				String timestamp = "1504241177";
				String appId = "wx41dbca0a55ba6ea7";
				String sign = "1020B1B04C5839732B8DE8B344F28036";
				String partnerId = "1403244702";
				String prepayId = "wx2017090112461835ba94148f0116046713";
				String nonceStr = "itVnQZWQjnJ62pCp";
				String packageValue = "Sign=WXPay";
				// appId注册到微信
				wxApi = WXAPIFactory.createWXAPI(context, appId);
				wxApi.registerApp(appId);
				// 检测当前手机是否支持微信支付
				boolean isPaySupported = wxApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				if (isPaySupported) {

					PayReq wxPayReq = new PayReq();
					wxPayReq.appId = appId;
					wxPayReq.partnerId = partnerId;
					wxPayReq.prepayId = prepayId;
					wxPayReq.packageValue = packageValue;
					wxPayReq.nonceStr = nonceStr;
					wxPayReq.timeStamp = timestamp;
					wxPayReq.sign = sign;

					wxApi.sendReq(wxPayReq);
				} else {
					Toast.makeText(context, "请先启动或者升级你的微信客户端来完成微信支付”", Toast.LENGTH_SHORT).show();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

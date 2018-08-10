package com.yuanding.schoolpass.bangbang.pay.weixin;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月24日 微信支付结果的通知eventBus类
 */
public class WXPayEventBus {

	private String mMsg;

	public WXPayEventBus(String Msg) {
		mMsg = Msg;
	}

	public String getMsg() {
		return mMsg;
	}
}

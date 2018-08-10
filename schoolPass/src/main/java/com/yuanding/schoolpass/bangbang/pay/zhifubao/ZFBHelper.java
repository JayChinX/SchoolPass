package com.yuanding.schoolpass.bangbang.pay.zhifubao;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 发布任务的支付宝支付
 */
public class ZFBHelper {

	Context context;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;
	Handler mHandler;
	public ZFBHelper(Context context) {
		this.context = context;

	}

	public interface ZFBPayCallBack {
		void onPaySuccess();

		void onPayCancel();

	}

	public void payV(final String orderInfo, final ZFBPayCallBack callBack) {
		if (TextUtils.isEmpty(PayStrStatic.APPID) || TextUtils.isEmpty(PayStrStatic.RSA_PRIVATE)) {
			new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface, int i) {
							//

						}
					}).show();
			return;
		}
		mHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message arg0) {
				// TODO Auto-generated method stub
				switch (arg0.what) {
				case SDK_PAY_FLAG: {
					@SuppressWarnings("unchecked")
					PayResult payResult = new PayResult((Map<String, String>) arg0.obj);
					/**
					 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					 */
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					Log.i("支付宝支付回调", resultStatus);
					// 判断resultStatus 为9000则代表支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
						callBack.onPaySuccess();

					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						callBack.onPayCancel();

					}
					break;
				}
				case SDK_AUTH_FLAG: {
					@SuppressWarnings("unchecked")
					AuthResult authResult = new AuthResult((Map<String, String>) arg0.obj, true);
					String resultStatus = authResult.getResultStatus();

					// 判断resultStatus 为“9000”且result_code
					// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
					if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
						// 获取alipay_open_id，调支付时作为参数extern_token 的value
						// 传入，则支付账户为该授权账户
						Toast.makeText(context, "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()),
								Toast.LENGTH_SHORT).show();
					} else {
						// 其他状态值则为授权失败
						Toast.makeText(context, "授权失败" + String.format("authCode:%s", authResult.getAuthCode()),
								Toast.LENGTH_SHORT).show();

					}
					break;
				}
				default:
					break;
				}
				return false;
			}
		});

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask((Activity) context);
				Map<String, String> result = alipay.payV2(orderInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

}

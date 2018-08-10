package com.yuanding.schoolpass.bangbang.pay;

import com.yuanding.schoolpass.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 发布任务 支付类型选择弹窗
 */
public class B_Side_Befriend_B1_Start_Pay_Dialog extends Dialog {

	Context context;
	private ImageView weixinYes;
	private ImageView zfbYes;
	int wayPay = 0;
	private String num_pay;

	public B_Side_Befriend_B1_Start_Pay_Dialog(Context context, int theme, String num_pay) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.num_pay = num_pay;
	}

	private ClickListenerInterface clickListenerInterface;

	public interface ClickListenerInterface {
		// 微信1 支付宝0
		public void doConfirm(int Way);

		public void doCancel();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_start_pay, null);
		setContentView(view);
		TextView numPay = (TextView) view.findViewById(R.id.for_help_pay_num_pay);
		ImageView cancelPay = (ImageView) view.findViewById(R.id.for_help_pay_cancel);

		RelativeLayout weixinPay = (RelativeLayout) view.findViewById(R.id.for_help_start_wexin);
		RelativeLayout zfbPay = (RelativeLayout) view.findViewById(R.id.for_help_start_zfb);

		weixinYes = (ImageView) view.findViewById(R.id.for_help_weixin_yes);
		zfbYes = (ImageView) view.findViewById(R.id.for_help_zfb_yes);

		TextView startPaySend = (TextView) view.findViewById(R.id.for_help_pay_start_send);
		numPay.setText( num_pay);
		switch (wayPay) {

		case 0:
			weixinYes.setVisibility(View.GONE);
			zfbYes.setVisibility(View.VISIBLE);
			break;
		case 1:
			weixinYes.setVisibility(View.VISIBLE);
			zfbYes.setVisibility(View.GONE);
			break;
		}
		cancelPay.setOnClickListener(new clickListener());
		weixinPay.setOnClickListener(new clickListener());
		zfbPay.setOnClickListener(new clickListener());
		startPaySend.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// DisplayMetrics d = context.getResources().getDisplayMetrics(); //
		// 获取屏幕宽、高用
		lp.width = LayoutParams.MATCH_PARENT; // 设置宽度充满屏幕
		lp.height = LayoutParams.WRAP_CONTENT; // 设置宽度充满屏幕
		dialogWindow.setAttributes(lp);
	}

	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	private class clickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.for_help_pay_start_send:
				// 支付
				clickListenerInterface.doConfirm(wayPay);
				break;
			case R.id.for_help_pay_cancel:
				// 取消
				clickListenerInterface.doCancel();
				break;
			case R.id.for_help_start_wexin:
				//
				wayPay = 1;
				if (weixinYes != null) {
					weixinYes.setVisibility(View.VISIBLE);
				}
				if (zfbYes != null) {
					zfbYes.setVisibility(View.GONE);
				}
				break;
			case R.id.for_help_start_zfb:
				wayPay = 0;
				if (weixinYes != null) {
					weixinYes.setVisibility(View.GONE);
				}
				if (zfbYes != null) {
					zfbYes.setVisibility(View.VISIBLE);
				}
				break;

			}
		}

	};

}

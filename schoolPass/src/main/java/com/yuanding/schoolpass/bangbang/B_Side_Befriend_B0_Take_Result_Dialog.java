package com.yuanding.schoolpass.bangbang;

import com.yuanding.schoolpass.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 任务领取结果弹窗
 */
public class B_Side_Befriend_B0_Take_Result_Dialog extends Dialog {

	Context context;
	int type;
	String msg;
	private ClickListenerInterface clickListenerInterface;

	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel();
	}

	public B_Side_Befriend_B0_Take_Result_Dialog(Context context, int theme, int type,String msg) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.type = type;
		this.msg = msg;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		View view = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_get_task_dialog, null);
		setContentView(view);
		ImageView taskTS = (ImageView) view.findViewById(R.id.for_help_get_task_ts);
		TextView taskWZ = (TextView) view.findViewById(R.id.for_help_get_task_wz);
		TextView taskBT = (TextView) view.findViewById(R.id.for_help_get_task_btn);
		TextView title = (TextView) view.findViewById(R.id.for_help_get_task_title);
		// 3支付失败
		LinearLayout payResurt = (LinearLayout) view.findViewById(R.id.for_help_pay_resurt_no);
		TextView payResurtNo = (TextView) view.findViewById(R.id.for_help_task_pay_no);
		TextView payResurtYes = (TextView) view.findViewById(R.id.for_help_task_pay_new);
		View line = view.findViewById(R.id.for_help_get_task_line);
		line.setVisibility(View.GONE);
		switch (type) {
		case 1:
			taskTS.setImageResource(R.drawable.bangbang_ico_chenggong);
			taskWZ.setText("任务领取成功！注意按时完成任务才会获得酬金。");
			taskBT.setText("好的");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 2:
			taskTS.setImageResource(R.drawable.bangbang_ico_shibai);
			taskWZ.setText("该任务被抢帮了！下次记得下手要快啊！");
			taskBT.setText("好的");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 3:
			taskTS.setImageResource(R.drawable.bangbang_ico_sx);
			taskWZ.setText("该任务已过期失效！");
			taskBT.setText("知道了");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 4:
			taskTS.setImageResource(R.drawable.bangbang_ico_shibai);
			taskWZ.setText("支付失败，帮帮任务无法发布！");
			taskBT.setVisibility(View.GONE);
			payResurt.setVisibility(View.VISIBLE);
			break;
		case 5:
			taskTS.setImageResource(R.drawable.bangbang_ico_chenggong);
			taskWZ.setText("帮帮任务发布成功！");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 6:// 取消任务 继续等待 确认取消
			taskTS.setImageResource(R.drawable.bangbang_ico_dengdai);
			taskWZ.setText("马上就有人要帮你了！");
			taskBT.setVisibility(View.GONE);
			payResurt.setVisibility(View.VISIBLE);
			payResurtNo.setText("确认取消");
			payResurtYes.setText("继续等待");

			break;
		case 7:// 取消任务 继续等待 确认取消
			taskTS.setImageResource(R.drawable.bangbang_ico_shibai);
			taskWZ.setText("取消任务，会降低您的信用值，影响您以后的任务领取率，请谨慎操作！");
			taskBT.setVisibility(View.GONE);
			payResurt.setVisibility(View.VISIBLE);
			payResurtNo.setText("不取消");
			payResurtYes.setText("继续取消");

			break;
		case 8:
			taskTS.setVisibility(View.GONE);
			taskWZ.setText(msg);
			taskBT.setText("好的");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			line.setVisibility(View.VISIBLE);
			break;
		case 9:
			taskTS.setImageResource(R.drawable.bangbang_ico_shibai);
			taskWZ.setText("自己不能领取自发的任务！");
			taskBT.setText("知道了");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 10:
			taskTS.setImageResource(R.drawable.bangbang_ico_chenggong);
			taskWZ.setText("任务已取消。任务酬金已原路退回（1-7个工作日内到帐）。");
			taskBT.setText("好的");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 11:
			taskTS.setImageResource(R.drawable.bangbang_ico_shibai);
			taskWZ.setText("密码错误，无法提交申请！");
			taskBT.setVisibility(View.GONE);
			payResurt.setVisibility(View.VISIBLE);
			payResurtNo.setText("取消");
			payResurtYes.setText("重新输入");
			break;
		case 12:
			taskTS.setImageResource(R.drawable.bangbang_ico_chenggong); 
			taskWZ.setText("提现申请成功！1-7个工作日内会提现到账，请注意查收。");
			taskBT.setText("好的");
			taskBT.setVisibility(View.VISIBLE);
			payResurt.setVisibility(View.GONE);
			break;
		case 13:
			taskTS.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			title.setText("是否已完成任务？");
			taskWZ.setText("请您完成任务后再进行确认。");
			taskBT.setVisibility(View.GONE);
			payResurt.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
			payResurtNo.setText("还未完成");
			payResurtYes.setText("确认已完成");
			break;
		}

		taskBT.setOnClickListener(new clickListener());
		payResurtNo.setOnClickListener(new clickListener());
		payResurtYes.setOnClickListener(new clickListener());
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
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
			case R.id.for_help_get_task_btn:
				clickListenerInterface.doCancel();
				break;
			case R.id.for_help_task_pay_no:
				clickListenerInterface.doCancel();
				break;
			case R.id.for_help_task_pay_new:
				clickListenerInterface.doConfirm();
				;
				break;

			}
		}

	};
}

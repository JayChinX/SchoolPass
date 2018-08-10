package com.yuanding.schoolpass.bangbang;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bean.Befriend_Task_List;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 任务详情弹窗
 */
public class B_Side_Befriend_B0_Task_Details_Dialog extends Dialog {

	Context context;
	int arg;
	private Befriend_Task_List helpTask;
	private ClickListenerInterface clickListenerInterface;

	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel();

		public void doReport();

	}

	public B_Side_Befriend_B0_Task_Details_Dialog(Context context, int theme, int arg) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.arg = arg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		helpTask = B_Side_Befriend_A0_List_Fragment.mTaskList.get(arg);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_task_details_dialog, null);
		setContentView(view);
		// 举报
		TextView report = (TextView) view.findViewById(R.id.for_help_details_report);
		// 标题
		TextView title = (TextView) view.findViewById(R.id.for_help_details_name);
		// 酬金
		TextView numMoney = (TextView) view.findViewById(R.id.for_help_details_num);

		// 帮助内容
		TextView name = (TextView) view.findViewById(R.id.for_help_task_details_name);
		TextView namer = (TextView) view.findViewById(R.id.for_help_task_details_namer);

		// 取件地址
		LinearLayout take = (LinearLayout) view.findViewById(R.id.for_help_task_details_address);
		LinearLayout send = (LinearLayout) view.findViewById(R.id.for_help_task_send_details_address);
		TextView takeAdd = (TextView) view.findViewById(R.id.for_help_task_details_take);
		// 送件地址
		TextView sendAdd = (TextView) view.findViewById(R.id.for_help_task_details_send);

		// 时间
		TextView data = (TextView) view.findViewById(R.id.for_help_task_details_data);
		TextView time = (TextView) view.findViewById(R.id.for_help_task_details_time);

		// 备注
		TextView other = (TextView) view.findViewById(R.id.for_help_task_details_other);
		LinearLayout others = (LinearLayout) view.findViewById(R.id.for_help_task_details_others);

		// 按钮 取消 举报 我去帮
		TextView no = (TextView) view.findViewById(R.id.for_help_task_details_no);
		TextView yes = (TextView) view.findViewById(R.id.for_help_task_details_yes);
		DecimalFormat df = new DecimalFormat("0.00");
		double money = (double)helpTask.getTotalAmount()/100.00;
		numMoney.setText("￥" + df.format(money));
//		numMoney.setText("￥" + (double)(Math.round(helpTask.getTotalAmount())/100.0));
//		if (helpTask.getTotalAmount() > 0) {
//			float money = (float) helpTask.getTotalAmount() / 100;
//			numMoney.setText("￥" + money);
//		} else {
//			numMoney.setText("￥" + helpTask.getTotalAmount() + ".00");
//		}
		if (helpTask.getSummary().equals("")) {
			others.setVisibility(View.GONE);
		} else {
			others.setVisibility(View.VISIBLE);
			other.setText(helpTask.getSummary());
		}
		name.setText(helpTask.getItems().get(0).getTitle() + "：");
		namer.setText(helpTask.getItems().get(0).getContent());
		switch (helpTask.getType()) {
		case 0:
			take.setVisibility(View.VISIBLE);
			send.setVisibility(View.VISIBLE);
			title.setText("取物品");
			data.setText(helpTask.getItems().get(3).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(3).getContent()));
			takeAdd.setText(helpTask.getItems().get(1).getContent());
			sendAdd.setText(helpTask.getItems().get(2).getContent());
			break;
		case 1:
			take.setVisibility(View.VISIBLE);
			send.setVisibility(View.VISIBLE);
			title.setText("取物品");
			takeAdd.setText(helpTask.getItems().get(1).getContent());
			sendAdd.setText(helpTask.getItems().get(2).getContent());
			data.setText(helpTask.getItems().get(3).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(3).getContent()));
			break;
		case 2:
			take.setVisibility(View.VISIBLE);
			send.setVisibility(View.VISIBLE);
			title.setText("送物品");
			takeAdd.setText(helpTask.getItems().get(1).getContent());
			sendAdd.setText(helpTask.getItems().get(2).getContent());
			data.setText(helpTask.getItems().get(3).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(3).getContent()));
			break;
		case 3:
			take.setVisibility(View.GONE);
			send.setVisibility(View.GONE);
			title.setText("教技能");
			data.setText(helpTask.getItems().get(1).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(1).getContent()));
			break;
		case 4:
			take.setVisibility(View.GONE);
			send.setVisibility(View.GONE);
			title.setText("修物品");
			data.setText(helpTask.getItems().get(1).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(1).getContent()));
			break;
		case 99:
			take.setVisibility(View.GONE);
			send.setVisibility(View.GONE);
			title.setText("帮其他");
			data.setText(helpTask.getItems().get(1).getTitle() + "：");
			time.setText(times(helpTask.getItems().get(1).getContent()));
		}

		report.setOnClickListener(new clickListener());
		no.setOnClickListener(new clickListener());
		yes.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
		dialogWindow.setAttributes(lp);
	}

	String one;

	public String times(String time) {
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		Log.i("时间戳", i + "");
		Date date = new Date(lcc * 1000);
		// int year = date.getYear();
		// int mouth = date.getMonth();
		// int day = date.getDay();
		// int hour = date.getHours();
		// int m = date.getMinutes();
		//
		// Time t=new Time("GMT+8"); // or Time t=new Time("GMT+8"); 加上Time
		// Zone资料
		// t.setToNow(); // 取得系统时间。
		// int yearNow = t.year;
		// int monthNow = t.month;
		// int dayNow = t.monthDay;
		//
		// if (year == yearNow && mouth == monthNow) {
		// if (day == dayNow) {
		// one = "今天 " + hour + ":" + m;
		// }else if (day - dayNow == 1) {
		// one = "明天 " + hour + ":" + m;
		// }
		//
		// }else {
		SimpleDateFormat sd = new SimpleDateFormat("MM/dd HH:mm");
		one = sd.format(date);

		// }

		return one;

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
			case R.id.for_help_task_details_no:
				clickListenerInterface.doConfirm();
				break;
			case R.id.for_help_task_details_yes:
				clickListenerInterface.doCancel();
				break;
			case R.id.for_help_details_report:
				clickListenerInterface.doReport();
				;
				break;

			}
		}

	};
}

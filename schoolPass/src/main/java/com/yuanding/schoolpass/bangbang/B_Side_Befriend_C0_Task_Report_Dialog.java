package com.yuanding.schoolpass.bangbang;

import java.util.ArrayList;
import java.util.List;
import com.yuanding.schoolpass.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 帮帮 任务举报弹窗
 */
public class B_Side_Befriend_C0_Task_Report_Dialog extends Dialog {

	Context context;
	List<ReportBean> reportBeans;
	List<ReportBean> reList;
	private ClickListenerInterface clickListenerInterface;
	String report;

	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel(String rreport);

	}

	public B_Side_Befriend_C0_Task_Report_Dialog(Context context) {
		super(context);
		this.context = context;
	}

	public B_Side_Befriend_C0_Task_Report_Dialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_task_report_dialog, null);
		setContentView(view);
		TextView no = (TextView) view.findViewById(R.id.for_help_report_no);
		TextView yes = (TextView) view.findViewById(R.id.for_help_report_yes);
		GridView gridView = (GridView) view.findViewById(R.id.for_help_report);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		no.setOnClickListener(new clickListener());
		yes.setOnClickListener(new clickListener());
		reportBeans = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			ReportBean reportBean = new ReportBean();
			reportBean.setType(0);

			reportBeans.add(reportBean);
			switch (i) {
			case 0:
				reportBean.setReportName("触犯校规");
				break;
			case 1:
				reportBean.setReportName("聚众斗殴");
				break;
			case 2:
				reportBean.setReportName("黄赌毒");
				break;
			case 3:
				reportBean.setReportName("欺诈信息");
				break;
			case 4:
				reportBean.setReportName("违法信息");
				break;
			case 5:
				reportBean.setReportName("其他方面");
				break;
			}
		}

		// stringList = new ArrayList<>();
		final ReportGridViewAdapter aGridViewAdapter = new ReportGridViewAdapter(context);
		gridView.setAdapter(aGridViewAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				for (ReportBean reportBean : reportBeans) {
					reportBean.setType(0);
				}
				if (reportBeans.get(arg2).getType() == 0) {
					reportBeans.get(arg2).setType(1);
				} else {
					reportBeans.get(arg2).setType(0);
				}
				report = reportBeans.get(arg2).getReportName();
				aGridViewAdapter.notifyDataSetChanged();
			}
		});

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.9);// 宽度设置
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
			case R.id.for_help_report_no:
				clickListenerInterface.doConfirm();
				break;
			case R.id.for_help_report_yes:
				clickListenerInterface.doCancel(report);
				break;

			}
		}

	};

	public class ReportGridViewAdapter extends BaseAdapter {
		Context context;

		public ReportGridViewAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return reportBeans != null ? reportBeans.size() : 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return reportBeans.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			arg1 = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_task_report, arg2, false);
			TextView report = (TextView) arg1.findViewById(R.id.for_help_task_report);
			ReportBean reportBean = reportBeans.get(arg0);
			report.setText(reportBean.getReportName());

			switch (reportBean.getType()) {
			case 0:
				report.setBackgroundResource(R.drawable.bangbang_btn_jubao);
				report.setTextColor(0xff666666);
				break;

			case 1:
				report.setBackgroundResource(R.drawable.bangbang_btn_jubao_hover);
				report.setTextColor(0xffffffff);
				break;
			}
			return arg1;
		}

	}

	public class ReportBean {
		String reportName;
		int type;

		public void setType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}

		public String getReportName() {
			return reportName;
		}
	}
}

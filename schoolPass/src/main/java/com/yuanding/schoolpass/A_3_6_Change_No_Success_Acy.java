package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月10日 下午5:30:23
 * 类说明
 */
public class A_3_6_Change_No_Success_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private Button btn_go_login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_change_no_success);
		
		setTitleText("变更手机号");
		
		btn_go_login = (Button) findViewById(R.id.btn_go_login);
		btn_go_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(A_3_6_Change_No_Success_Acy.this, A_3_0_Login_Acy.class));
				A_0_App.getInstance().clearUserSpInfo(false);
                finish();
			}
		});
		
	}
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		default:
			break;
		}
		
	}
}
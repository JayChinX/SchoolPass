package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月10日 下午3:03:00
 * 忘记密码成功页面
 */
public class A_3_4_Forget_pwd_Succ_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private Button btn_login_account;
	private String acy_type;
	private int type;//1表示密码重置成功,2表示修改密码
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addReSetPwdAcy(this);
		setView(R.layout.activity_acc_forge_success);
		
		type=getIntent().getIntExtra("type", 0);
		if(type==1){
			setTitleText("密码重置成功");
		}else if(type==2)
		{
			setTitleText("修改成功");
		}
		
		acy_type = getIntent().getExtras().getString("acy_type");
		
		btn_login_account = (Button) findViewById(R.id.btn_login_account);
		btn_login_account.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(A_3_4_Forget_pwd_Succ_Acy.this,A_3_0_Login_Acy.class));
				if (acy_type.equals("resetPwd"))
					A_0_App.getInstance().exitResetProcess();
				else
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

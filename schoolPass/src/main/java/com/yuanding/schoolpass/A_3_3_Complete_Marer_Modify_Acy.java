package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月2日 下午6:23:30 修改相关的信息
 * 3个选项  真实姓名 、学号、昵称
 */
public class A_3_3_Complete_Marer_Modify_Acy extends A_0_CpkBaseTitle_Navi {

	private EditText et_account_messge_item;
	private String item_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_complete_mater_modify);

//		setTextBtn("保存");
//		showTitleBt(TEXT_BUTTON, true);
        setZuiRightBtn(R.drawable.navigationbar_save);
        showTitleBt(ZUI_RIGHT_BUTTON, true);
		item_type = getIntent().getExtras().getString("title_name");
		setTitleText(item_type);
		
		et_account_messge_item = (EditText) findViewById(R.id.et_account_messge_item);
		et_account_messge_item.setText(getIntent().getExtras().getString("content"));
		if(item_type.equals("邀请人"))
		{
			et_account_messge_item.setHint("请输入邀请人手机号");
		}
		/**
		 * 过滤表情
		 */
		et_account_messge_item.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				  int index = et_account_messge_item.getSelectionStart() - 1;
				                  if (index > 0) {
				                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
				                        Editable edit = et_account_messge_item.getText();
				                          edit.delete(index, index + 1);
				                     }
				                 }
			}
		});
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			String str = et_account_messge_item.getText().toString();
			Intent it = new Intent();
			it.putExtra("modify_content", str);
			if (item_type.equals(A_3_3_Complete_marer_Acy.TITLE_USER_TURE_NAME)){
				if(str != null && !str.equals("")&& str.length()>0){
					if(PubMehods.judgeStr(str)){
						if (str.length() >= 2 && str.length() <= 20) {
							setResult(1, it);
						} else {
							PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this, "真实名字为2~20位的全汉字");
							return;
						}
					}
					else {
						PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this,"真实名字为2~20位的全汉字");
						return;
					}
				}else{
					PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this,"请输入你的真实名字");
					return;
				}
			}
			else if (item_type.equals(A_3_3_Complete_marer_Acy.TITLE_USER_STUDENT_NO)){
				if(str != null && !str.equals("")&& str.length() > 0){
					if (str.length() >= 5 && str.length() <= 30) {
						if(PubMehods.isNumeric(str)){
							 setResult(4, it);
						}else{
							PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this, "学号为5~30位的数字");
							return;
						}
					}else{
						PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this, "学号为5~30位的数字");
						return;
					}
				}else{
					PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this,"请输入你的学号");
					return;
				}
			}
			else if (item_type.equals(A_3_3_Complete_marer_Acy.TITLE_USER_NIPICK)){    
				if(str != null && !str.equals("")&& str.length() > 0){
					if (str.length() >= 2 && str.length() <= 9) {
						setResult(5, it);
					}else{
						PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this, "昵称为2~9位的字母、数字或汉字");
						return;
					}
				}else{
					PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this,"请输入你的昵称");
					return;
				}
			}
			else if (item_type.equals(A_3_3_Complete_marer_Acy.TITLE_USER_INVITATION_NAME)){ 
				if(str != null && !str.equals("")&& str.length() > 0){
					if (PubMehods.isMobileNO(str)) {
						setResult(11, it);
					}else{
						PubMehods.showToastStr(A_3_3_Complete_Marer_Modify_Acy.this, "请输入正确的手机号");
						return;
					}
				}else{
					setResult(11, it);
				}
			}
			this.finish();
			break;
		default:
			break;
		}
	}

}

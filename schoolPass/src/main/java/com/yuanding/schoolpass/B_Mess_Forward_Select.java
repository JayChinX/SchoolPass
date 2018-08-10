package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.SendMessageCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Get_Service_Time;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.rongyun.WYZFNoticeContent;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月5日 下午4:12:36 类说明 校外本地搜索各项匹配数据
 */
public class B_Mess_Forward_Select extends A_0_CpkBaseTitle_Navi {

	private ListView lv_forward_list;
	private Mydapter adapter;
	private Intent intent;
	private String title, content, image_url, type, acy_type, noticeId,course_user_uniqid;

	private String[] cate_name = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_mess_forward_list);
		
        setTitleText("转发选择");
		intent = getIntent();
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		image_url = intent.getStringExtra("image");
		type = intent.getStringExtra("type");
		acy_type = intent.getStringExtra("acy_type");
		noticeId = intent.getStringExtra("noticeId");
		course_user_uniqid = intent.getStringExtra("course_user_uniqid");
		if (acy_type != null && acy_type.equals("11")) {// 课程表
            cate_name = new String[] {"班级群聊", "我的同学"};
        } else {
            cate_name = new String[] {"班级群聊", "我的同学", "我的老师"};
        }
		
		lv_forward_list = (ListView) findViewById(R.id.lv_forward_list);
		adapter = new Mydapter();
		lv_forward_list.setAdapter(adapter);
		
		lv_forward_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi, long arg3) {
                if (posi == 0) {
                    if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                        if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
                            showForwardDialog(posi);
                        } else {
                            PubMehods.showToastStr(B_Mess_Forward_Select.this,"您的操作过于频繁！");
                        }
                    } else {
                        PubMehods.showToastStr(B_Mess_Forward_Select.this,R.string.error_title_net_error);
                    }
                }else if(posi == 1) {
                    Intent intent=new Intent(B_Mess_Forward_Select.this, B_Mess_Forward_Select_ClassMate.class);
                    intent.putExtra("cate_name", cate_name[1]);
                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                    intent.putExtra("image", image_url);
                    intent.putExtra("type", type);
                    intent.putExtra("acy_type", acy_type);
                    intent.putExtra("noticeId", noticeId);
                    intent.putExtra("course_user_uniqid", course_user_uniqid);
                    startActivity(intent);
                    
                }else if(posi == 2) {
                    Intent intent=new Intent(B_Mess_Forward_Select.this, B_Mess_Forward_Select_Teacher.class);
                    intent.putExtra("cate_name", cate_name[2]);
                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                    intent.putExtra("image", image_url);
                    intent.putExtra("type", type);
                    intent.putExtra("acy_type", acy_type);
                    intent.putExtra("noticeId", noticeId);
                    intent.putExtra("course_user_uniqid", course_user_uniqid);
                    startActivity(intent);
                }
            }
        });
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
		if (RongIM.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
		    System.out.println("融云已连接");
        }else{
            reconnect(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this);
        }
	}
            
    private void showForwardDialog(final int posi) {
        final Dialog upDateDialog = new Dialog(B_Mess_Forward_Select.this,
                R.style.Theme_GeneralDialog);
        upDateDialog.setContentView(R.layout.dialog_repeat);
        TextView textView = (TextView) upDateDialog.findViewById(R.id.tv_dialog_content);
        TextView cancel = (TextView) upDateDialog.findViewById(R.id.tv_left_button);
        TextView summit = (TextView) upDateDialog.findViewById(R.id.tv_right_button);
        ImageView re_photo = (ImageView) upDateDialog.findViewById(R.id.re_photo);
        re_photo.setBackgroundResource(R.drawable.icon_mess_ban_liao);
        textView.setText(cate_name[posi]);
        cancel.setText("取消");
        summit.setText("发送");

        upDateDialog.show();
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upDateDialog.dismiss();

            }
        });
        summit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String requestType = "";
                if (acy_type.equals("11")) {
                    requestType = "1";
                }
                if (RongIM.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
                    getServiceTimeToShare(upDateDialog,requestType,title,content, image_url, type, 
                            noticeId, "", "", "", "", "", acy_type, course_user_uniqid);
                }else{
                    reconnectData(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this,upDateDialog,requestType,title,content, image_url, type, 
                            noticeId, "", "", "", "", "", acy_type, course_user_uniqid);
                }
            }
        });
    }
            
	public class Mydapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cate_name.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int posi, View converView, ViewGroup arg2) {
            if (converView == null) {
                converView = LayoutInflater.from(B_Mess_Forward_Select.this).inflate(
                        R.layout.item_forward, null);
            }
            TextView name = (TextView) converView.findViewById(R.id.tv_forward_name);
            ImageView por = (ImageView) converView.findViewById(R.id.iv_forward_image);
            RelativeLayout rel = (RelativeLayout) converView.findViewById(R.id.rel_out_school_search_auto);
            name.setText(cate_name[posi]);
            if(posi == 0){
                por.setBackgroundResource(R.drawable.icon_mess_ban_liao);
            }else if(posi == 1){
                por.setBackgroundResource(R.drawable.ic_forward_tongxue);
            }if(posi == 2){
                por.setBackgroundResource(R.drawable.ic_forward_laoshi);
            }
            if(A_0_App.isShowAnimation==true){
             if(posi>A_0_App.forward_select_curPosi)
			 {
				A_0_App.forward_select_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			    converView.startAnimation(an);
			 }
            }
            return converView;
        }

    }
	
	//share_time值是接口获取
	private void getServiceTimeToShare(final Dialog upDateDialog,String requestType,final String titleStr, final String share_content, final String imgUrl, final String type,
            final String noticeId,final String notice_sendUserName, String share_time,
            final String unReadCount, final String message_level, final String placeImg,final String acy_type, final String course_user_uniqid) {
	    A_0_App.getInstance().showProgreDialog(B_Mess_Forward_Select.this, "", true);
        A_0_App.getApi().getServiceTime(B_Mess_Forward_Select.this, A_0_App.USER_TOKEN,requestType,new Inter_Get_Service_Time() {
            @Override
            public void onSuccess(String time,String icon) {
                String tempImage_Url = imgUrl;
                if (acy_type.equals("11")) {
                    if (imgUrl == null || imgUrl.length() <= 0) {
                        tempImage_Url = icon;
                    }
                }
                final WYZFNoticeContent cu = new WYZFNoticeContent(titleStr,share_content,tempImage_Url, type,
                        noticeId,notice_sendUserName, time,
                        unReadCount, message_level, placeImg,acy_type, course_user_uniqid);
                if (RongIM.getInstance() != null && cu != null && A_0_App.USER_QUNIQID != null&&A_0_App.USER_QUNIQID.length() > 0) {
                    try {
                        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.GROUP,
                        A_0_App.USER_QUNIQID, cu, null, null,new SendMessageCallback() {
                            @Override
                            public void onSuccess(Integer arg0) {
                                A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                                upDateDialog.dismiss();
                                PubMehods.showToastStr(B_Mess_Forward_Select.this,"转发成功");
                            }

                            @Override
                            public void onError(Integer arg0, ErrorCode arg1) {
                                A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                                upDateDialog.dismiss();
                                PubMehods.showToastStr(B_Mess_Forward_Select.this,"服务器开了小差,请重试！");
                                reconnect(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this);
                            
                            } 
                        });
                    } catch (Exception e) {
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                        upDateDialog.dismiss();
                        PubMehods.showToastStr(B_Mess_Forward_Select.this, "转发失败，请重试");
                    }
                } 
            }
        }, new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                
            }
            
            @Override
            public void onFailure(String msg) {
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                PubMehods.showToastStr(B_Mess_Forward_Select.this, msg);
            }
            
            @Override
            public void onCancelled() {
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                
            }
        });

    }
	
	private void clearData() {
	    cate_name = null;
		adapter = null;
	}

	/**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().getRongIMClient()
                .setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Mess_Forward_Select.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Mess_Forward_Select.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
    private void reconnectData(String token, final Context context,final Dialog upDateDialog,final String requestType,final String titleStr, final String share_content, final String imgUrl, final String type,
            final String noticeId,final String notice_sendUserName, String share_time,
            final String unReadCount, final String message_level, final String placeImg,final String acy_type, final String course_user_uniqid) {
        A_0_App.getInstance().showProgreDialog(context, "",true);
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                A_0_App.getInstance().CancelProgreDialog(context);
                PubMehods.showToastStr(context,"抱歉，Token Incorrect");
            }

            @Override
            public void onSuccess(String s) {
                A_0_App.getInstance().CancelProgreDialog(context);
                getServiceTimeToShare(upDateDialog,requestType,title,content, image_url, type, 
                        noticeId, "", "", "", "", "", acy_type, course_user_uniqid);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                A_0_App.getInstance().CancelProgreDialog(context);
                PubMehods.showToastStr(context, "抱歉，重连接服务器失败，请检查您的网络设置");
            }
        });
    }
    
    private void reconnect(String token, final Context context) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                System.out.println("抱歉，Token错误");
            }

            @Override
            public void onSuccess(String s) {}

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                System.out.println("抱歉，重连接服务器失败，请检查您的网络设置");
            }
        });
    }

	@Override
	protected void onDestroy() {
		clearData();
		super.onDestroy();
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}


	}

}

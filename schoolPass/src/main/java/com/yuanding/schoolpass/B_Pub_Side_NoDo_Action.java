package com.yuanding.schoolpass;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月21日 下午2:17:29 类未做功能空白页展示
 */
public class B_Pub_Side_NoDo_Action extends A_0_CpkBaseTitle_Navi {

	private ImageView iv_blank_por;
	private TextView tv_blank_name;

	private String title;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.pub_read_no_content);

		title = getIntent().getExtras().getString("title");
		type =  getIntent().getExtras().getInt("side_enter_type");
		
		setTitleText(title);
		iv_blank_por = (ImageView) findViewById(R.id.iv_blank_por);
		tv_blank_name = (TextView) findViewById(R.id.tv_blank_name);

		switch (type) {
		case 1://活动
		     iv_blank_por.setBackgroundResource(R.drawable.no_huodong);
		     tv_blank_name.setText("一大波学生福利在酝酿，请稍候再来~");
			break;
		case 2://讲座
		     iv_blank_por.setBackgroundResource(R.drawable.no_jiangzuo);
		     tv_blank_name.setText("天使在唱歌，上帝在跳舞；" + "\n" + "现在是休息时间，暂时没有讲座内容~");
			break;
		case 3://我的收藏
			iv_blank_por.setBackgroundResource(R.drawable.no_sc);
			tv_blank_name.setText("暂时没有感兴趣的收藏，再去逛一逛~");
			break;
		case 4://资料共享
			iv_blank_por.setBackgroundResource(R.drawable.no_ziliao);
			tv_blank_name.setText("还没有共享资料，去分享一个吧~");
			break;
		case 5://成绩
			iv_blank_por.setBackgroundResource(R.drawable.no_cj);
			tv_blank_name.setText("你的成绩尚未公布，再等一等~");
			break;
		case 6://课程表
			iv_blank_por.setBackgroundResource(R.drawable.no_kebiao);
			tv_blank_name.setText("暂时没有数据~");
			break;

		case 7://评教
			iv_blank_por.setBackgroundResource(R.drawable.no_pingjiao);
			tv_blank_name.setText("评教工作尚未开始，暂时没有数据~");
			break;
		case 8://报修
			iv_blank_por.setBackgroundResource(R.drawable.no_baoxiu);
			tv_blank_name.setText("后勤报修正在加紧建设中，请稍后关注~");
			break;
		default:
			break;
		}
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	 /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
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
                    //A_0_App.getInstance().showExitDialog(B_Pub_Side_NoDo_Action.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Pub_Side_NoDo_Action.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
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

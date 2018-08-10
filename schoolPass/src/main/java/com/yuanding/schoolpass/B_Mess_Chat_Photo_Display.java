
package com.yuanding.schoolpass;

import io.rong.imkit.tools.PhotoFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * Created by DragonJ on 15/4/13.
 */
public class B_Mess_Chat_Photo_Display extends A_0_CpkBaseTitle_Navi {
    PhotoFragment mPhotoFragment;
    Uri mUri;
    Uri mDownloaded;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_photo_display);
        setTitleText(getIntent().getExtras().getString("图片"));
        initView();
       // initData();
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }
    protected void initView() {
    	linearLayout=(LinearLayout) findViewById(R.id.liner_titlebar_back_image);
		linearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(R.anim.animal_push_right_in_normal,R.anim.animal_push_right_out_normal);
			}
		});
        mPhotoFragment = (PhotoFragment) getSupportFragmentManager().getFragments().get(0);
    }

//    protected void initData() {
//        Uri uri = getIntent().getParcelableExtra("photo");
//        Uri thumbUri = getIntent().getParcelableExtra("thumbnail");
//
//        mUri = uri;
//        if (uri != null)
//            mPhotoFragment.initPhoto(uri, thumbUri, new PhotoFragment.PhotoDownloadListener() {
//                @Override
//                public void onDownloaded(Uri uri) {
//                    mDownloaded = uri;
//                }
//
//                @Override
//                public void onDownloadError() {
//
//                }
//            });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.icon) {
            if(mDownloaded == null) {
                Toast.makeText(this, "正在下载，请稍后保存！", Toast.LENGTH_SHORT).show();
                return true;
            }

            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path, "RongCloud/Image");
            if(!dir.exists())
                dir.mkdirs();

            File from = new File(mDownloaded.getPath());
            String name = from.getName() + ".jpg";
            File to = new File(dir.getAbsolutePath(), name);
            if(to.exists()) {
                Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
                return true;
            }
            copyFile(from.getAbsolutePath(), to.getAbsolutePath());
        }
        return super.onOptionsItemSelected(item);
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "文件保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mUri != null && mUri.getScheme().startsWith("http")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.de_fix_username, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
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
                    //A_0_App.getInstance().showExitDialog(B_Mess_Chat_Photo_Display.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Chat_Photo_Display.this, AppStrStatic.kicked_offline());
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

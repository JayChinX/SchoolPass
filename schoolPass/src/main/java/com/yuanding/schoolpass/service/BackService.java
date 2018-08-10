package com.yuanding.schoolpass.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.LogUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BackService extends Service {
	private static final long HEART_BEAT_RATE = 5 * 1000;//心跳时间间隔

	public static final String MESSAGE_ACTION="com.yuanding.schoolpass.message_ACTION";
	public static final String HEART_BEAT_ACTION="com.yuanding.schoolpass.heart_beat_ACTION";
	
	private ReadThread mReadThread;

	private LocalBroadcastManager mLocalBroadcastManager;

	private WeakReference<Socket> mSocket;
	private static BackService instance;
	
    public static BackService getInstance() {
        return instance;
    }
	// For heart Beat
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				boolean isSuccess = sendMsg("");//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
				if (!isSuccess) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    if (mReadThread != null && mSocket != null) {
                        mReadThread.release();
                        releaseLastSocket(mSocket);
                    }
                    new InitSocketThread().start();
                    if (A_Main_My_Message_Acy.getInstance() != null
                            && A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                        A_Main_My_Message_Acy.getInstance().ToSayServiceLogin();
                    }
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private long sendTime = 0L;
	private IBackService.Stub iBackService = new IBackService.Stub() {

		@Override
		public boolean sendMessage(String message) throws RemoteException {
			return sendMsg(message);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return iBackService;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		new InitSocketThread().start();
		mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
		
	}
	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
				OutputStream os = soc.getOutputStream();
				String message = msg + "\r\n";
				os.write(message.getBytes());
				os.flush();
				sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void initSocket() {//初始化Socket
		try {
		    logE(A_0_App.SERVER_CONN_BASE_URL);
			Socket so = new Socket(A_0_App.SERVER_CONN_BASE_URL, AppStrStatic.PORT);
			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(so);
			mReadThread.start();
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
		} catch (UnknownHostException e) {
		    logE("UnknownHostException");
		} catch (IOException e) {
		    logE("IOException");
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
		}
	}
	
    public void stopHeartBeat() {
        if (mHandler != null && heartBeatRunnable != null) {
            mHandler.removeCallbacks(heartBeatRunnable);
        }
        if (mReadThread != null) {
            mReadThread.release();
        }
        if (mSocket != null) {
            releaseLastSocket(mSocket);
        }
    }

	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (sk!= null && !sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
		}
	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}

	// Thread to read content from Socket
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@SuppressLint("NewApi")
        @Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(Arrays.copyOf(buffer,
									length)).trim();
							logE("==>接收" + message);
							//收到服务器过来的消息，就通过Broadcast发送出去
							JSONObject object;
	                        try {
	                            object = new JSONObject(message);
	                            String status=object.getString("status");
	                            if (status.equals("0")) {//处理心跳回复
	                                if (object.getString("msg").equals("the param token is required")) {
	                                    Intent intent=new Intent(HEART_BEAT_ACTION);
	                                    mLocalBroadcastManager.sendBroadcast(intent);
	                                }
	                            }else{
	                                //其他消息回复
	                                Intent intent=new Intent(MESSAGE_ACTION);
	                                intent.putExtra("message", message);
	                                mLocalBroadcastManager.sendBroadcast(intent);
	                            }
	                        } catch (JSONException e) {
	                            // TODO Auto-generated catch block
	                        }
						}
					}
				} catch (IOException e) {
				}
			}
		}
	}
	
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("BackService","BackService==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("BackService","BackService==>" + msg);
    }
}

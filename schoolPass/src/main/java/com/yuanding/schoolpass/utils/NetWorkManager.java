package com.yuanding.schoolpass.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author Administrator
 *在使用ConnectivityManager时，必须在AndroidManifest.xml中
 *添加<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
 *否则无法获得系统的许可
 */
public class NetWorkManager {
	
	private ConnectivityManager cnmger=null;
	
	public enum NetWorkStatus{
		CONNECTED,
		CONNECTING,
		DISCONNECTED,
		DISCONNECTING,
		SUSPENDED,
		UNKNOWN,
		NO_NETWORK
	}
	
	public NetWorkManager(Context context)
	{
		cnmger=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	}
	
   /**
     * 指示是否存在正在连接或已连接的网络
     * @return true 表示存在正在连接或已连接的网络
     *         false 表示没有正在连接或已连接的网络
     */
    public boolean isNetWorkCntedOrCnting()
    {
        NetworkInfo ni =cnmger.getActiveNetworkInfo();
        
        if(ni==null || !ni.isConnectedOrConnecting())
        {
            return false;
        }
        else{
            return true;
        }
    }
    
	/**
	 * 指示网络是否已连接成功，并且已建立连接和传递数据
	 * @return
	 */
	public boolean isNetWorkConnected()
	{
		NetworkInfo ni =cnmger.getActiveNetworkInfo();
		if(ni==null || !ni.isConnected())
		{
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
     * 获取当前已连接网络的类型，如3G、WIFI等
     * @return 
     */
    public int getCurNetWorkType()
    {
        NetworkInfo ni =cnmger.getActiveNetworkInfo();
        if(ni==null || !ni.isAvailable())
        {
            return -1;
        }
        else{
            return ni.getType();
        }
    }
	
    
    /**
     * 只用来判断是否存在可用的网络
     */
    public boolean isNetWorkAvailable()
    {
        NetworkInfo ni = cnmger.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable())
        {
            return false;
        }
        else {
            return true;
        }
    }

	/**
	 * 获取当前活跃网络的详细状态
	 * @return 返回枚举类型NetWorkStatus的值
	 */
	public NetWorkStatus getNetWorkStatus()
	{
		NetworkInfo ni =cnmger.getActiveNetworkInfo();
		if(ni!=null)
		{
			NetworkInfo.State st =ni.getState();
			switch(st)
			{
			case CONNECTED:
				return NetWorkStatus.CONNECTED;
			case CONNECTING:
				return NetWorkStatus.CONNECTING;
			case DISCONNECTED:
				return NetWorkStatus.DISCONNECTED;
			case DISCONNECTING:
				return NetWorkStatus.DISCONNECTING;
			case SUSPENDED:
				return NetWorkStatus.SUSPENDED;
			default:
				return NetWorkStatus.UNKNOWN;
			}
		}
		else{
			return NetWorkStatus.NO_NETWORK;
		}
	}
	
}

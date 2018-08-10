
package com.yuanding.schoolpass.service;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_Acy;
import com.yuanding.schoolpass.A_Main_My_Side_Acy;
import com.yuanding.schoolpass.A_Main_My_Side_Acy;
import com.yuanding.schoolpass.bean.Cpk_Side_Red;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年7月26日 上午10:41:36 身边红点的广播监听
 */
public class BackReciverMessage extends BroadcastReceiver {
    // private WeakReference<TextView> textView;
    // public MessageBackReciver(TextView tv) {
    // textView = new WeakReference<TextView>(tv);
    // }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(BackService.HEART_BEAT_ACTION)) {
                // if (A_Main_My_Side_Acy.getInstance() != null &&
                // !A_Main_My_Side_Acy.getInstance().isFinishing())
                // A_Main_My_Side_Acy.getInstance().refreshMainView("Get a heart heat");
                // else
                // A_0_App.getInstance().setSideReciverMessage("Get a heart heat");
            } else {
                String message = intent.getStringExtra("message");
                try {
                    if (message != null && !"".equals(message)) {
                        JSONObject object = new JSONObject(message);
                        String status = object.getString("status");
                        String command = object.getString("command");
                        if (status.equals("1") && command.equals("MODULEMSG")) {//主动推
                            Log.e("BackService", "==>身边获取主动推信息");
                            
                            String module = object.getString("module");
                            Map<String, String> mapTemp = A_0_App.getInstance().readOAuth();
                            if (mapTemp.size() > 0) {
                                mapTemp.remove(module);
                            }
                            if(mapTemp != null)
                                mapTemp.put(module, "1");
                            A_0_App.getInstance().saveOAuth(mapTemp);
                            
                            if (A_Main_My_Side_Acy.getInstance() != null && !A_Main_My_Side_Acy.getInstance().isFinishing()){
                                A_Main_My_Side_Acy.getInstance().refreshMainView(message);
                            }else{
                                if (mapTemp == null || mapTemp.size() == 0)
                                    return;
                                String temp = "";
                                for (Entry<String, String> entry : mapTemp.entrySet()) {
                                    String strval1 = entry.getValue();
                                    temp = strval1 + temp;
                                }

                                if (temp.contains("1")) {
                                    A_Main_Acy.getInstance().showSideNoReadTag(true);
                                } else {
                                    A_Main_Acy.getInstance().showSideNoReadTag(false);
                                }
                            }
                                
                        } else if (status.equals("1") && command.equals("SIDE")) {//获取身边信息
                            Log.e("BackService", "==>身边获取信息成功");
                            
                            JSONArray jsonArrayItem = new JSONArray("["+object.getString("module")+"]");
                            Map<String, String> map_red=new HashMap<String, String>();
                            for (int j = 0; j < jsonArrayItem.length(); j++) {
                                JSONObject myjObject = jsonArrayItem.getJSONObject(j);
                                Iterator iterator = myjObject.keys();                       // joData是JSONObject对象  
                                while(iterator.hasNext()){  
                                    String key = iterator.next() + "";  
                                    map_red.put(key, myjObject.getString(key));
                                }  
                            }
                            A_0_App.getInstance().saveOAuth(map_red);
                            
                            A_0_App.getInstance().setSideReciverMessage(message);
                            if (A_Main_My_Side_Acy.getInstance() != null
                                    && !A_Main_My_Side_Acy.getInstance().isFinishing())
                                A_Main_My_Side_Acy.getInstance().refreshMainView(message);
                        } else if (status.equals("1") && command.equals("LOGIN")){//登录成功
                            A_0_App.getInstance().setLoginReciverMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("BackService", "error---客户端接收到的身边消息==>");
                }
            }
        }
    };
}

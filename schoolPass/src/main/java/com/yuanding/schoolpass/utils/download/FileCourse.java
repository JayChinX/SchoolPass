package com.yuanding.schoolpass.utils.download;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCourse {
	 
    /** 
     * 检验SDcard状态 
     * @return boolean 
     */  
    public static boolean checkSDCard()  
    {  
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))  
        {  
            return true;  
        }else{  
            return false;  
        }  
    }  
    /** 
     * 保存文件文件到目录 
     * @param context 
     * @return  文件保存的目录 
     */  
    public static String setMkdir(Context context)  
    {  
        String filePath;  
        if(checkSDCard())  
        {  
            filePath = Environment.getExternalStorageDirectory()+File.separator+"myfile";  
        }else{  
            filePath = context.getCacheDir().getAbsolutePath()+File.separator+"myfile";  
        }  
        File file = new File(filePath);  
        if(!file.exists())  
        {  
            boolean b = file.mkdirs();  
            Log.e("file", "文件不存在  创建文件    "+b);  
        }else{  
            Log.e("file", "文件存在");  
        }  
        return filePath;  
    }  
}

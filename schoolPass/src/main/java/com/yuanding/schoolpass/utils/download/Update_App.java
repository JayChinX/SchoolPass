package com.yuanding.schoolpass.utils.download;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_3_0_Login_Acy;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bean.Cpk_Version;
import com.yuanding.schoolpass.service.Api.InterCheckVersion;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;

import java.io.File;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年7月22日 下午4:13:19 类说明
 */
public class Update_App{
	
	// ---------------------------------------升级----------------------------------//
	public Update_App() {
		super();
	}

	public static void check_upDate_App(final Context context,final boolean showDialog) {
		if (!((Activity) context).isFinishing()&&showDialog)
			A_0_App.getInstance().showHorizontalDialog(context,context.getResources().getString(R.string.check_new_version),true);
		A_0_App.getApi().checkVersion(new InterCheckVersion() {
			@Override
			public void onSuccess(Cpk_Version cpk_Version){
				if (((Activity) context).isFinishing())
					return;
				if(showDialog)
			        A_0_App.getInstance().CancelHorizontalDialog(context);
				if (cpk_Version != null && cpk_Version.getDownloadUrl().length() > 0) {
				    A_0_App.getInstance().getVersion().setDownloadUrl(cpk_Version.getDownloadUrl());
                    A_0_App.getInstance().getVersion().setUpdateLog(cpk_Version.getUpdateLog());
                    A_0_App.getInstance().getVersion().setVersionCode(cpk_Version.getVersionCode());
                    A_0_App.getInstance().getVersion().setVersionName(cpk_Version.getVersionName());
                    A_0_App.getInstance().getVersion().setIs_require(cpk_Version.getIs_require());
					if (Integer.valueOf(cpk_Version.getVersionCode()) > PubMehods.getVerCode(context)) {
                        File file = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/" + cpk_Version
                                .getDownloadUrl().substring(cpk_Version.getDownloadUrl().lastIndexOf("/") + 1));
                        if (file.exists()) {
                             int downLoadVersion = A_0_App.getInstance().getVersionCode(file.getAbsolutePath());
                             if(downLoadVersion >= Integer.valueOf(cpk_Version.getVersionCode())){
                                 if(cpk_Version.getIs_require().equals("1")){//强制更新
                                     showHaveDownLoadDialog(context,cpk_Version,file,false);
                                 }else{
                                     showHaveDownLoadDialog(context,cpk_Version,file,true);
                                 }
                             }else{
                                 if(cpk_Version.getIs_require().equals("1")){//强制更新
                                     showUpdateDialog(context,cpk_Version,false);
                                 }else{
                                     showUpdateDialog(context,cpk_Version,true);
                                 }
                             }
                        }else{//不存在已下载文件
                            if(cpk_Version.getIs_require().equals("1")){//强制更新
                                showUpdateDialog(context,cpk_Version,false);
                            }else{
                                showUpdateDialog(context,cpk_Version,true);
                            }
                        }
					} else {
					    if(showDialog)   
					        PubMehods.showToastStr(context, "已是最新版本");
					}
				}else{
				    if(showDialog)
				        PubMehods.showToastStr(context, "检查版本失败");
				}
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (((Activity) context).isFinishing())
                    return;
                if(showDialog){
                    A_0_App.getInstance().CancelHorizontalDialog(context);
                    PubMehods.showToastStr(context, "检查版本失败");
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	//canced  true:表示非强制更新     false：强制更新
	public static void showUpdateDialog(final Context con,Cpk_Version cpk_version,final boolean canced) {
		
		String temp = con.getResources().getString(R.string.pub_update);
		final GeneralDialog upDateDialog = new GeneralDialog(con,R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(temp + " " + cpk_version.getVersionName());
		upDateDialog.setCanceledOnTouchOutside(canced);
		upDateDialog.setCancelable(canced);
		upDateDialog.setContent(cpk_version.getUpdateLog());
		if(canced){//可以关闭dialog，非强制更新
		    upDateDialog.showLeftButton(R.string.pub_ignore, new OnClickListener() {
	            @Override
	            public void onClick(View v) {
                    upDateDialog.cancel();
                    A_0_App.getInstance().saveUpdateTime();
	            }
	        });
	        upDateDialog.showRightButton(R.string.pub_update, new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (A_0_App.getInstance().getVersion() != null&& A_0_App.getInstance().getVersion().getDownloadUrl().length() > 0)
	                    if(A_Main_My_Message_Acy.getInstance() != null){
	                        A_Main_My_Message_Acy.getInstance().startDownloadApp(A_0_App.getInstance().getVersion().getDownloadUrl());
	                    }else if(A_3_0_Login_Acy.getInstance() != null){
                            A_3_0_Login_Acy.getInstance().startDownloadApp(A_0_App.getInstance().getVersion().getDownloadUrl());
	                    }
	                else
	                    PubMehods.showToastStr(con, "下载地址异常 ,请退出程序重试");
	                upDateDialog.cancel();
	            }
	        });
		}else{
		    upDateDialog.showMiddleButton(R.string.pub_update, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                        PubMehods.showToastStr(con, "下载失败，请检查网络是否可用");
                        return;
                    }
                    if(!A_0_App.getInstance().mUpdating){
                        if (A_0_App.getInstance().getVersion() != null && A_0_App.getInstance().getVersion().getDownloadUrl().length() > 0){
                            if(A_Main_My_Message_Acy.getInstance() != null){
                                A_Main_My_Message_Acy.getInstance().startDownloadApp(A_0_App.getInstance().getVersion().getDownloadUrl());
                            }else if(A_3_0_Login_Acy.getInstance() != null){
                                A_3_0_Login_Acy.getInstance().startDownloadApp(A_0_App.getInstance().getVersion().getDownloadUrl());
                            }
                            PubMehods.showToastStr(con, "开始下载，请下拉通知栏,查看最新下载进度");
                            A_0_App.getInstance().mUpdating = true;
                        }else{
                            PubMehods.showToastStr(con, "下载地址异常 ,请退出程序重试");
                        }
                    }else{
                        PubMehods.showToastStr(con, "请下拉通知栏，查看最新下载进度");
                    }
                }
            });
		}
		upDateDialog.show();
	}

    //canced  true:表示非强制更新     false：强制更新
    public static void showHaveDownLoadDialog(final Context context,Cpk_Version cpk_version,final File file,final boolean canced) {
        
        String temp = context.getResources().getString(R.string.pub_update);
        final GeneralDialog upDateDialog = new GeneralDialog(context,R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(temp + " " + cpk_version.getVersionName());
        upDateDialog.setCanceledOnTouchOutside(canced);
        upDateDialog.setCancelable(canced);
        upDateDialog.setContent(cpk_version.getUpdateLog());
        if(canced){//可以关闭dialog，非强制更新
            upDateDialog.showLeftButton(R.string.pub_ignore, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    upDateDialog.cancel();
                    A_0_App.getInstance().saveUpdateTime();
                }
            });
            upDateDialog.showRightButton(R.string.pub_hava_download_update, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApkDownloader downloader = new ApkDownloader(context, "");
                    downloader.openFile(file);
                    upDateDialog.cancel();
                }
            });
        }else{
            upDateDialog.showMiddleButton(R.string.pub_hava_download_update, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApkDownloader downloader = new ApkDownloader(context, "");
                    downloader.openFile(file);
                }
            });
        }
        upDateDialog.show();
    }
    
	// ---------------------------------------升级----------------------------------//

}

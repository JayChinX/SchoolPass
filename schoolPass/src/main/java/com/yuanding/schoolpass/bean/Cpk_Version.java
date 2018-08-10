package com.yuanding.schoolpass.bean;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年7月22日 上午10:15:12 类说明
 */
public class Cpk_Version {

	private String versionCode;
	private String versionName;
	private String downloadUrl;
	private String updateLog;
//	private boolean newVersion; // true，表示有新版本
	private String is_require;// 0非强制更新，1是强制更新

	public Cpk_Version() {
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

//	public boolean isNewVersion() {
//		return newVersion;
//	}

	public String getIs_require() {
		return is_require;
	}

	public void setIs_require(String is_require) {
		this.is_require = is_require;
	}

//	public void setNewVersion(boolean newVersion) {
//		this.newVersion = newVersion;
//	}

}

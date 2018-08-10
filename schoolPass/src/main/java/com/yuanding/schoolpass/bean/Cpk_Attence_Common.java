package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午11:34:32  早退，迟到，缺勤，请假
 */
public class Cpk_Attence_Common {

	private String user_id;
	private String class_id;
	private String status;
	private String true_name;
	private String photo_url;
	public String getMeter() {
		return meter;
	}
	public void setMeter(String meter) {
		this.meter = meter;
	}
	private String meter;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getClass_id() {
		return class_id;
	}
	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	
}

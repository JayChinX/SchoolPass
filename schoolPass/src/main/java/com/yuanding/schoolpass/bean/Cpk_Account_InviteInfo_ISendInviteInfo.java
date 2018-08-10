package com.yuanding.schoolpass.bean;

/**
 * 我发出的邀请信息
 * @author Administrator
 *
 */
public  class Cpk_Account_InviteInfo_ISendInviteInfo {


	String type;
	String true_name;
	String user_id;
	String app_type;
	String create_time;
	String status;
	String organ_name;
//	public Cpk_Account_InviteInfo_ISendInviteInfo(String type, String true_name,
//			String user_id, String app_type, String create_time,
//			String status, String organ_name) {
//		super();
//		this.type = type;
//		this.true_name = true_name;
//		this.user_id = user_id;
//		this.app_type = app_type;
//		this.create_time = create_time;
//		this.status = status;
//		this.organ_name = organ_name;
//	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getApp_type() {
		return app_type;
	}
	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrgan_name() {
		return organ_name;
	}
	public void setOrgan_name(String organ_name) {
		this.organ_name = organ_name;
	}

	

}

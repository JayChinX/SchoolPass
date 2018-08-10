package com.yuanding.schoolpass.bean;

//全局搜索信息通知
public class Cpk_Search_Mess_Notice {

	public String id;
	public String school_id;
	public String create_time;
	public String is_delete;
	public String content;
	public String title;
	public String user_id;
	public String res_user_id;
	public String app_msg_sign;
	public String log_user_id;
	public String log_is_delete;
	public String message_id;
	public String type;
	public String getRes_user_id() {
		return res_user_id;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRes_user_id(String res_user_id) {
		this.res_user_id = res_user_id;
	}

	public String getLog_user_id() {
		return log_user_id;
	}

	public void setLog_user_id(String log_user_id) {
		this.log_user_id = log_user_id;
	}

	public String getLog_is_delete() {
		return log_is_delete;
	}

	public void setLog_is_delete(String log_is_delete) {
		this.log_is_delete = log_is_delete;
	}

	public String getApp_msg_sign() {
		return app_msg_sign;
	}

	public void setApp_msg_sign(String app_msg_sign) {
		this.app_msg_sign = app_msg_sign;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}


	public String getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(String is_delete) {
		this.is_delete = is_delete;
	}


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}

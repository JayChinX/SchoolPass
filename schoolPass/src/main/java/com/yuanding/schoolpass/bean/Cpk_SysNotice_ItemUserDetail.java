package com.yuanding.schoolpass.bean;

import android.widget.TextView;

/**
 * 站内通知列表
 * @author Administrator
 *
 */
public class Cpk_SysNotice_ItemUserDetail {

	String comment_photo_url;
	String comment_true_name;
	String comment_school_name;
	String comment_content;
	int comment_create_time;
	
	String to_comment_content;
	String title;
	int to_comment_create_time;
	
	int message_id;
	int comment_type; //1 回复 2 赞 3 删除
	
	public String getComment_true_name() {
		return comment_true_name;
	}
	public void setComment_true_name(String comment_true_name) {
		this.comment_true_name = comment_true_name;
	}
	public String getComment_school_name() {
		return comment_school_name;
	}
	public void setComment_school_name(String comment_school_name) {
		this.comment_school_name = comment_school_name;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public int getComment_create_time() {
		return comment_create_time;
	}
	public void setComment_create_time(int comment_create_time) {
		this.comment_create_time = comment_create_time;
	}
	public String getTo_comment_content() {
		return to_comment_content;
	}
	public void setTo_comment_content(String to_comment_content) {
		this.to_comment_content = to_comment_content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTo_comment_create_time() {
		return to_comment_create_time;
	}
	public void setTo_comment_create_time(int to_comment_create_time) {
		this.to_comment_create_time = to_comment_create_time;
	}
	public int getMessage_id() {
		return message_id;
	}
	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}
	public int getComment_type() {
		return comment_type;
	}
	public void setComment_type(int comment_type) {
		this.comment_type = comment_type;
	}
	public String getComment_photo_url() {
		return comment_photo_url;
	}
	public void setComment_photo_url(String comment_photo_url) {
		this.comment_photo_url = comment_photo_url;
	}

}

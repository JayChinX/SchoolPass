package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * 
 * @author MyPC 失物招领列表实体类
 *
 */
public class Cpk_Side_Lost_List implements Serializable {
	private static final long serialVersionUID = 1L;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String info;
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	private String photo_url;
	private String user_id;
	private String uniqid;
	private String status;
	private String phone;
	private long create_time;
	private String lost_id;
	private String name;
	private long lost_time;
	private String desc;
	private String place;
	private String school_name;
	private String true_name;
	private String user_photo_url;
	private String is_self;
	public String getIs_self() {
		return is_self;
	}
	public void setIs_self(String is_self) {
		this.is_self = is_self;
	}
	public String getSchool_name() {
		return school_name;
	}
	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}
	public String getUser_photo_url() {
		return user_photo_url;
	}
	public void setUser_photo_url(String user_photo_url) {
		this.user_photo_url = user_photo_url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String type;
	
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUniqid() {
		return uniqid;
	}
	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getCreate_time() {
		return create_time;
	} 
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getLost_id() {
		return lost_id;
	}
	public void setLost_id(String lost_id) {
		this.lost_id = lost_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getLost_time() {
		return lost_time;
	}
	public void setLost_time(long lost_time) {
		this.lost_time = lost_time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
}

package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午9:37:58 
 * 报修列表
 */
public class Cpk_Side_Repair_Detail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String info;
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	private String repair_id;
	private String title;
	private String details;
	private String place;
	private String type_name;
	private String phone;
	private String class_name;
	private String status;
	private String reject_details;
	private String true_name;
	private String is_self;
	public String getIs_self() {
		return is_self;
	}
	public void setIs_self(String is_self) {
		this.is_self = is_self;
	}
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}
	private long create_time;
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getRepair_id() {
		return repair_id;
	}
	public void setRepair_id(String repair_id) {
		this.repair_id = repair_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReject_details() {
		return reject_details;
	}
	public void setReject_details(String reject_details) {
		this.reject_details = reject_details;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午9:37:58 
 * 报修列表
 */
public class Cpk_Side_Repair_List implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long create_time;
	private String status;
	private String true_name;
	private String details;

	private String article_id;
	private String repair_id;
	private String title;
	private String type_name;
	private String place;
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
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
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getArticle_id() {
		return article_id;
	}
	public void setArticle_id(String article_id) {
		this.article_id = article_id;
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
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

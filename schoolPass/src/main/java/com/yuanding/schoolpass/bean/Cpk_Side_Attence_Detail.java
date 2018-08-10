package com.yuanding.schoolpass.bean;


/**
 *  
 * @version 创建时间：2015年11月27日 下午2:39:33 考勤详情
 */
public class Cpk_Side_Attence_Detail {
	
	
	private String atd_id;
	private String title;
	private String place;
	/**
	 * 考勤状态 0：未开始，1：正在开始，2：已结束，3：已过期
	 */
	private String status;
	private String create_time;
	private String list;
	private String type;
	private String atd_time;
	private String image_url;
//	/**
//	 * 缺席
//	 */
//	private String absent;
//	/**
//	 * 迟到
//	 */
//	private String late;
//	/**
//	 * 早退
//	 */
//	private String early;
//	/**
//	 * 请假
//	 */
//	private String leave;
//	/**
//	 * 正常
//	 */
//	private String normal;
	
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getAtd_time() {
		return atd_time;
	}
	public void setAtd_time(String atd_time) {
		this.atd_time = atd_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
	}
	public String getAtd_id() {
		return atd_id;
	}
	public void setAtd_id(String atd_id) {
		this.atd_id = atd_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
}

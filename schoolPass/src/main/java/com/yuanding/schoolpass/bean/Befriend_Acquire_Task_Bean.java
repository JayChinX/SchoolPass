package com.yuanding.schoolpass.bean;
/**
 *  
 *   获取领取任务列表：领取 的实体类
 */
public class Befriend_Acquire_Task_Bean {
    /**
     * 任务id
     */
	private String id;
	/**
	 *任务类型
	 */
	private String type;
	/**
	 * 任务标题
	 */
	private String title;
	/**
	 * 工作状态：0_认领中，1_已提交，2_已收款，9_取消
	 */
	private String workStatus;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}
	public String getCreateUsername() {
		return createUsername;
	}
	public void setCreateUsername(String createUsername) {
		this.createUsername = createUsername;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	/**
	 * 发布人姓名
	 */
	private String createUsername;
	/**
	 * 秒
	 */
	private String createTime;
	/**
	 * 失效状态：0_正常，1_已失效
	 */
	private String items;
	
	private String summary;
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}

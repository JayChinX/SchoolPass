package com.yuanding.schoolpass.bean;

import java.io.Serializable;
import java.util.List;

public class Befriend_Pick_Task_Details implements Serializable {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	String tradeNo;
	String payTime;
	String cancelStatus;
	String sex;
	String title;
	String type;
	String creatorPhone;
	String cancelFuture;
	String cancelTime;
	String workCreateTime;
	String workStatus;
	String expiredTime;
	String totalAmount;
	String createUsername;
	String createTime;
	String taskWorkStatus;
	String schoolId;
	String schoolName;
	String orderFrom;
	String id;
	String submitTime;
	String agreePayTime;
	List<itemsBean> items;

	

	public String getAgreePayTime() {
		return agreePayTime;
	}

	public void setAgreePayTime(String agreePayTime) {
		this.agreePayTime = agreePayTime;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getCancelStatus() {
		return cancelStatus;
	}

	public void setCancelStatus(String cancelStatus) {
		this.cancelStatus = cancelStatus;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreatorPhone() {
		return creatorPhone;
	}

	public void setCreatorPhone(String creatorPhone) {
		this.creatorPhone = creatorPhone;
	}

	

	public String getCancelFuture() {
		return cancelFuture;
	}

	public void setCancelFuture(String cancelFuture) {
		this.cancelFuture = cancelFuture;
	}

	public String getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}

	public String getWorkCreateTime() {
		return workCreateTime;
	}

	public void setWorkCreateTime(String workCreateTime) {
		this.workCreateTime = workCreateTime;
	}

	public String getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
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

	public String getTaskWorkStatus() {
		return taskWorkStatus;
	}

	public void setTaskWorkStatus(String taskWorkStatus) {
		this.taskWorkStatus = taskWorkStatus;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getOrderFrom() {
		return orderFrom;
	}

	public void setOrderFrom(String orderFrom) {
		this.orderFrom = orderFrom;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<itemsBean> getItems() {
		return items;
	}

	public void setItems(List<itemsBean> items) {
		this.items = items;
	}

	public class itemsBean implements Serializable {
		private static final long serialVersionUID = 1L;

		public long getSerialversionuid() {
			return serialVersionUID;
		}
		// TODO Auto-generated method stub

		String title;
		String type;
		String content;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}
}

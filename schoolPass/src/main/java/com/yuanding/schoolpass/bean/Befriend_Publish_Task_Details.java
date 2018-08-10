package com.yuanding.schoolpass.bean;

import java.io.Serializable;
import java.util.List;

public class Befriend_Publish_Task_Details implements Serializable {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	String payTime;
	String title;
	String type;
	String creatorPhone;
	String createUsername;
	String destAddress;
	String id;
	String taskStatus;
	String tradeNo;
	String destPhone;
	String srcAddress;
	String expiredTime;
	String cancelTime;
	String total_amount;
	String createTime;
	String misdeedType;
	String nowTime;
	String orderFrom;
	String agreePayTime;

	String automaticPayTime;
	String workId;
	String workSex;
	String workPhone;
	String workSubmitTime;
	String workCreateSchoolName;
	String workCreateTime;
	String workCreateSchoolId;
	String workCreateUsername;
	String workCancelTime;
	String submitTime;

	public String getCouponStatus() {
		return couponStatus;
	}

	public void setCouponStatus(String couponStatus) {
		this.couponStatus = couponStatus;
	}

	public String getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}

	String couponStatus;
	String couponAmount;
	List<itemsBean> items;

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getAgreePayTime() {
		return agreePayTime;
	}

	public void setAgreePayTime(String agreePayTime) {
		this.agreePayTime = agreePayTime;
	}

	public String getNowTime() {
		return nowTime;
	}

	public void setNowTime(String nowTime) {
		this.nowTime = nowTime;
	}

	public String getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
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

	public String getCreateUsername() {
		return createUsername;
	}

	public void setCreateUsername(String createUsername) {
		this.createUsername = createUsername;
	}

	public String getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(String destAddress) {
		this.destAddress = destAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getDestPhone() {
		return destPhone;
	}

	public void setDestPhone(String destPhone) {
		this.destPhone = destPhone;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}

	public String getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getOrderFrom() {
		return orderFrom;
	}

	public void setOrderFrom(String orderFrom) {
		this.orderFrom = orderFrom;
	}

	public String getAutomaticPayTime() {
		return automaticPayTime;
	}

	public void setAutomaticPayTime(String automaticPayTime) {
		this.automaticPayTime = automaticPayTime;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getWorkSex() {
		return workSex;
	}

	public void setWorkSex(String workSex) {
		this.workSex = workSex;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkSubmitTime() {
		return workSubmitTime;
	}

	public void setWorkSubmitTime(String workSubmitTime) {
		this.workSubmitTime = workSubmitTime;
	}

	public String getWorkCreateSchoolName() {
		return workCreateSchoolName;
	}

	public void setWorkCreateSchoolName(String workCreateSchoolName) {
		this.workCreateSchoolName = workCreateSchoolName;
	}

	public String getWorkCreateTime() {
		return workCreateTime;
	}

	public void setWorkCreateTime(String workCreateTime) {
		this.workCreateTime = workCreateTime;
	}

	public String getWorkCreateSchoolId() {
		return workCreateSchoolId;
	}

	public void setWorkCreateSchoolId(String workCreateSchoolId) {
		this.workCreateSchoolId = workCreateSchoolId;
	}
	
	

	public String getWorkCancelTime() {
		return workCancelTime;
	}

	public void setWorkCancelTime(String workCancelTime) {
		this.workCancelTime = workCancelTime;
	}

	public String getWorkCreateUsername() {
		return workCreateUsername;
	}

	public void setWorkCreateUsername(String workCreateUsername) {
		this.workCreateUsername = workCreateUsername;
	}

	public List<itemsBean> getItems() {
		return items;
	}

	public void setItems(List<itemsBean> items) {
		this.items = items;
	}

	
	public String getMisdeedType() {
		return misdeedType;
	}

	public void setMisdeedType(String misdeedType) {
		this.misdeedType = misdeedType;
	}


	public class itemsBean implements Serializable {
		private static final long serialVersionUID = 1L;

		public long getSerialversionuid() {
			return serialVersionUID;
		}

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

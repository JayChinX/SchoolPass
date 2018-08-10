package com.yuanding.schoolpass.bean;
/**
 *  
 *   帮帮  账单历史的实体类
 */
public class Befriend_Bill_Bean {
    /**
     * 备注
     */
	private String summary;
	/**
	 * 金额 分
	 */
	private String totalAmount;
	/**
	 * 交易年份
	 */
	private String tradeYear;
	/**
	 *交易时间，秒
	 */
	private String createTime;
	/**
	 * 待完成：0-无，10-条数
	 */
	private String taskId;
	/**
	 * 0 扣款，1 增加
	 */
	private String tradeType;
	/**
	 * 
	 */
	private String tradeUser;
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTradeYear() {
		return tradeYear;
	}
	public void setTradeYear(String tradeYear) {
		this.tradeYear = tradeYear;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeUser() {
		return tradeUser;
	}
	public void setTradeUser(String tradeUser) {
		this.tradeUser = tradeUser;
	}
	
}

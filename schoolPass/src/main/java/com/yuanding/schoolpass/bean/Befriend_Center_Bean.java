package com.yuanding.schoolpass.bean;
/**
 *  
 *   帮帮  个人中心的实体类
 */
public class Befriend_Center_Bean {
    /**
     * 总未读信息，0-无，10-条数
     */
	private String totalUnreadMessages;
	/**
	 * 待确认打款任务数：0-无，10-条数
	 */
	private String waitPayment;
	/**
	 * 待领取：0-无，10-条数
	 */
	private String waitAcquire;
	/**
	 * 服务中：0-无，10-条数
	 */
	private String onService;
	/**
	 * 待完成：0-无，10-条数
	 */
	private String waitComlpete;
	/**
	 * //待收款：0-无，10-条数
	 */
	private String pendingReceivables;
	/**
	 * //已收款：0-无，10-条数
	 */
	private String alreadyPayment;
	/**
	 * //新任务通知：0-不开启，1-开启
	 */
	private String onNotice;
	/**
	 * //金额 分
	 */
	private String totalAmount;
	/**
	 * 0_未绑定，1_已绑定，2_绑定失败
	 */
	private String bindStatus;
	private String account;
	private String accountName;
	private String withdraw;//周几可提现，1-7 对应周一-周日
	private String withdrawAmountLow;//最低提现金额
	private String withdrawMax;//最高提现次数
	private String couponCount;//优惠券数
	private String couponType;//优惠券数

	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public String getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(String couponCount) {
		this.couponCount = couponCount;
	}

	private String withdrawTimes;//当前提现次数
	public String getWithdrawMax() {
		return withdrawMax;
	}
	public void setWithdrawMax(String withdrawMax) {
		this.withdrawMax = withdrawMax;
	}
	public String getWithdrawTimes() {
		return withdrawTimes;
	}
	public void setWithdrawTimes(String withdrawTimes) {
		this.withdrawTimes = withdrawTimes;
	}
	public String getWithdrawAmountLow() {
		return withdrawAmountLow;
	}
	public void setWithdrawAmountLow(String withdrawAmountLow) {
		this.withdrawAmountLow = withdrawAmountLow;
	}
	public String getAccount() {
		return account;
	}
	public String getWithdraw() {
		return withdraw;
	}
	public void setWithdraw(String withdraw) {
		this.withdraw = withdraw;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBindStatus() {
		return bindStatus;
	}
	public void setBindStatus(String bindStatus) {
		this.bindStatus = bindStatus;
	}
	public String getTotalUnreadMessages() {
		return totalUnreadMessages;
	}
	public void setTotalUnreadMessages(String totalUnreadMessages) {
		this.totalUnreadMessages = totalUnreadMessages;
	}
	public String getWaitPayment() {
		return waitPayment;
	}
	public void setWaitPayment(String waitPayment) {
		this.waitPayment = waitPayment;
	}
	public String getWaitAcquire() {
		return waitAcquire;
	}
	public void setWaitAcquire(String waitAcquire) {
		this.waitAcquire = waitAcquire;
	}
	public String getOnService() {
		return onService;
	}
	public void setOnService(String onService) {
		this.onService = onService;
	}
	public String getWaitComlpete() {
		return waitComlpete;
	}
	public void setWaitComlpete(String waitComlpete) {
		this.waitComlpete = waitComlpete;
	}
	public String getPendingReceivables() {
		return pendingReceivables;
	}
	public void setPendingReceivables(String pendingReceivables) {
		this.pendingReceivables = pendingReceivables;
	}
	public String getAlreadyPayment() {
		return alreadyPayment;
	}
	public void setAlreadyPayment(String alreadyPayment) {
		this.alreadyPayment = alreadyPayment;
	}
	public String getOnNotice() {
		return onNotice;
	}
	public void setOnNotice(String onNotice) {
		this.onNotice = onNotice;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
}

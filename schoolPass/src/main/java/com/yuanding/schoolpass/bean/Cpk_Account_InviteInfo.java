package com.yuanding.schoolpass.bean;

/**
 * 推荐信息
 * @author Administrator
 */
public class Cpk_Account_InviteInfo {

	String status;
	String msg;
	String time;
	String count;

	public Cpk_Account_InviteInfo(String status, String msg, String time,
			String count) {
		super();
		this.status = status;
		this.msg = msg;
		this.time = time;
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * 我的推荐人信息
	 * 
	 * @author Administrator
	 */
	public static class MyInviterInfo {
		String userId;
		String trueName;
		String photoUrl;
		String organName;

		public  MyInviterInfo(String userId, String trueName, String photoUrl,
				String organName) {
			super();
			this.userId = userId;
			this.trueName = trueName;
			this.photoUrl = photoUrl;
			this.organName = organName;
		}
		
		public MyInviterInfo() {
			super();
			
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getTrueName() {
			return trueName;
		}

		public void setTrueName(String trueName) {
			this.trueName = trueName;
		}

		public String getPhotoUrl() {
			return photoUrl;
		}

		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}

		public String getOrganName() {
			return organName;
		}

		public void setOrganName(String organName) {
			this.organName = organName;
		}
	}}



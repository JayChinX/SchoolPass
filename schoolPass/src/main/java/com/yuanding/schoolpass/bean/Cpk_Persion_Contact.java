package com.yuanding.schoolpass.bean;

public class Cpk_Persion_Contact {
	private String uniqid;
	private String token;
	private String phone;
	private String photo_url;
	private String name;
	private String sortLetters; // 显示数据拼音的首字母
	private String utype;// 用户类型
	private String suoxie;// 姓名缩写
	int is_invite; //是否可进行邀请操作，0-不显示邀请按钮，1-可进行邀请操作显示邀请按钮，2-显示已邀请按钮
	
	public String getUniqid() {
		return uniqid;
	}

	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getUtype() {
		return utype;
	}

	public void setUtype(String utype) {
		this.utype = utype;
	}

	public String getSuoxie() {
		return suoxie;
	}

	public void setSuoxie(String suoxie) {
		this.suoxie = suoxie;
	}

	public int getIs_invite() {
		return is_invite;
	}

	public void setIs_invite(int is_invite) {
		this.is_invite = is_invite;
	}
	
	

}
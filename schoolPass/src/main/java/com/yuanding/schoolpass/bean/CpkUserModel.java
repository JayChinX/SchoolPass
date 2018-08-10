package com.yuanding.schoolpass.bean;


public class CpkUserModel {

	/**
	 * @Fields serialVersionUID : TODO(用户实体类)
	 */

	private String user_id;
	private String uniqid;
	private String username;
	private String phone;
	private String sex;
	
	private String photo_url;
	private String name;
//	private String type;
	private String student_number;
	private String student_status;

	private String school_id;
	private String device_token;
	private String pids;
    private String enrol_year;
	
	private String class_id;
	private String classname;
	private String quniqid;
	private String qutoken;
	private String token;
    private String bang_url;
	private String coupon_use_url;//身边帮帮选择使用优惠券
	private String leave_detail_url;

	public String getLeave_detail_url() {
		return leave_detail_url;
	}

	public void setLeave_detail_url(String leave_detail_url) {
		this.leave_detail_url = leave_detail_url;
	}

	public String getCoupon_use_url() {
		return coupon_use_url;
	}

	public void setCoupon_use_url(String coupon_use_url) {
		this.coupon_use_url = coupon_use_url;
	}

	private String coupon_url;
	private String getui_client_id;
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUniqid() {
		return uniqid;
	}

	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public String getStudent_number() {
		return student_number;
	}

	public void setStudent_number(String student_number) {
		this.student_number = student_number;
	}

	public String getStudent_status() {
		return student_status;
	}

	public void setStudent_status(String student_status) {
		this.student_status = student_status;
	}

	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}

	public String getPids() {
		return pids;
	}

	public void setPids(String pids) {
		this.pids = pids;
	}

	public String getEnrol_year() {
		return enrol_year;
	}

	public void setEnrol_year(String enrol_year) {
		this.enrol_year = enrol_year;
	}

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getQuniqid() {
		return quniqid;
	}

	public void setQuniqid(String quniqid) {
		this.quniqid = quniqid;
	}

	public String getQutoken() {
		return qutoken;
	}

	public void setQutoken(String qutoken) {
		this.qutoken = qutoken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
    public String getBang_url() {
        return bang_url;
    }

    public void setBang_url(String bang_url) {
        this.bang_url = bang_url;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getGetui_client_id() {
        return getui_client_id;
    }

    public void setGetui_client_id(String getui_client_id) {
        this.getui_client_id = getui_client_id;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

	public String getCoupon_url() {
		return coupon_url;
	}

	public void setCoupon_url(String coupon_url) {
		this.coupon_url = coupon_url;
	}
}

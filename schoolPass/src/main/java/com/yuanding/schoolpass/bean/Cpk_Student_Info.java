package com.yuanding.schoolpass.bean;

import org.json.JSONObject;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月26日 下午7:32:50 学生信息
 */
public class Cpk_Student_Info {

    private String organ_id;
    private String birthday;
    private String sex;
    private String phone;
    private String school_name;
    
    private String photo_url;
    private String school_id;
    private String politics;
    private String username;
    private String time;
    
    private String uniqid;
    private String name;
    private String organ_name;
    private String user_id;
    
    private String sn_number;
    private String role_name;
    private String teacher_status;
    private String position_title;
    
    private String bang_total;
    private String rec_url; //邀请链接
    private String invite_sms_content; //短信分享内容
    private String type;
    
    private String student_status;
    private String student_number;
    private String enrol_year;
    
    private String class_name;
    private String class_id;
    private String counsellor;
    private String parentid;
    private String recommend_phone;
    private String heo_display;
    private String coupon_dot;//加优惠券是否显示红点  0：没有红点 1：有红点

    public String getCoupon_dot() {
        return coupon_dot;
    }

    public void setCoupon_dot(String coupon_dot) {
        this.coupon_dot = coupon_dot;
    }

    public String getCoupon_url() {
        return coupon_url;
    }

    public void setCoupon_url(String coupon_url) {
        this.coupon_url = coupon_url;
    }

    private String coupon_url;
    public String getHeo_display() {
		return heo_display;
	}

	public void setHeo_display(String heo_display) {
		this.heo_display = heo_display;
	}
    private JSONObject response;
    
    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

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

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getPolitics() {
        return politics;
    }

    public String getInvite_sms_content() {
        return invite_sms_content;
    }

    public void setInvite_sms_content(String invite_sms_content) {
        this.invite_sms_content = invite_sms_content;
    }

    public String getRec_url() {
        return rec_url;
    }

    public void setRec_url(String rec_url) {
        this.rec_url = rec_url;
    }

    public void setPolitics(String politics) {
        this.politics = politics;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCounsellor() {
        return counsellor;
    }

    public void setCounsellor(String counsellor) {
        this.counsellor = counsellor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPosition_title() {
        return position_title;
    }

    public void setPosition_title(String position_title) {
        this.position_title = position_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnrol_year() {
        return enrol_year;
    }

    public void setEnrol_year(String enrol_year) {
        this.enrol_year = enrol_year;
    }

    public String getOrgan_id() {
        return organ_id;
    }

    public void setOrgan_id(String organ_id) {
        this.organ_id = organ_id;
    }

    public String getOrgan_name() {
        return organ_name;
    }

    public void setOrgan_name(String organ_name) {
        this.organ_name = organ_name;
    }

    public String getSn_number() {
        return sn_number;
    }

    public void setSn_number(String sn_number) {
        this.sn_number = sn_number;
    }

    public String getTeacher_status() {
        return teacher_status;
    }

    public void setTeacher_status(String teacher_status) {
        this.teacher_status = teacher_status;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getBang_total() {
        return bang_total;
    }

    public void setBang_total(String bang_total) {
        this.bang_total = bang_total;
    }

    public String getRecommend_phone() {
        return recommend_phone;
    }

    public void setRecommend_phone(String recommend_phone) {
        this.recommend_phone = recommend_phone;
    }

}



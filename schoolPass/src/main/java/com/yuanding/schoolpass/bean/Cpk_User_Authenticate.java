package com.yuanding.schoolpass.bean;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月26日 下午6:00:02 用户认证
 */
public class Cpk_User_Authenticate {

	private String class_name;
	private String enrol_year;
	private String parent_name;//parent_name + class_name  拼接班级
	private String school_name;
	private String politics;
	
	private String school_id;
	private String organ_id;
	private String name;
	private String birthday;
	private String student_number;
	private String sex;
	
	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getEnrol_year() {
		return enrol_year;
	}

	public void setEnrol_year(String enrol_year) {
		this.enrol_year = enrol_year;
	}

	public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public String getPolitics() {
		return politics;
	}

	public void setPolitics(String politics) {
		this.politics = politics;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getStudent_number() {
		return student_number;
	}

	public void setStudent_number(String student_number) {
		this.student_number = student_number;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}

	public String getOrgan_id() {
		return organ_id;
	}

	public void setOrgan_id(String organ_id) {
		this.organ_id = organ_id;
	}

}

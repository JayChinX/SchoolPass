package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月6日 上午11:53:10 通讯录学生—通讯录
 */
public class Cpk_Student_Class_Contact {

	private String organ_id;
	private String organ_name;

	private List<Cpk_Persion_Contact> mlist;

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

	public List<Cpk_Persion_Contact> getMlist() {
		return mlist;
	}

	public void setMlist(List<Cpk_Persion_Contact> mlist) {
		this.mlist = mlist;
	}

}

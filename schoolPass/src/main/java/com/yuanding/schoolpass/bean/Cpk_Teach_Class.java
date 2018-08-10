package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月26日 下午6:59:37 教学机构
 */
public class Cpk_Teach_Class {

	private String child_total_num;//1表示最后一级，0表示还有下级
	private String parent_id;
	private String organ_name;
	private String organ_id;

	public String getChild_total_num() {
		return child_total_num;
	}

	public void setChild_total_num(String child_total_num) {
		this.child_total_num = child_total_num;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getOrgan_name() {
		return organ_name;
	}

	public void setOrgan_name(String organ_name) {
		this.organ_name = organ_name;
	}

	public String getOrgan_id() {
		return organ_id;
	}

	public void setOrgan_id(String organ_id) {
		this.organ_id = organ_id;
	}

}

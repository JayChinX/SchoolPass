package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午1:27:03 校内校外子列表
 */
public class Cpk_School_In_And_Out_Child  implements Serializable{

	/**
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 1L;
	private String create_time;
	private String info_name;
	private String info_icon;
	private String phone;
	private String address;
	private String cate_id;
	
	private String sortLetters;
	private String suoxie;

	public String getSuoxie() {
		return suoxie;
	}

	public void setSuoxie(String suoxie) {
		this.suoxie = suoxie;
	}
	
	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getInfo_name() {
		return info_name;
	}

	public void setInfo_name(String info_name) {
		this.info_name = info_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getInfo_icon() {
		return info_icon;
	}

	public void setInfo_icon(String info_icon) {
		this.info_icon = info_icon;
	}

	public String getCate_id() {
		return cate_id;
	}

	public void setCate_id(String cate_id) {
		this.cate_id = cate_id;
	}
	
}

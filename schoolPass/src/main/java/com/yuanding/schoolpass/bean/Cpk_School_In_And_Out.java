package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * 
 * @ClassName: Cpk_School_In_And_Out
 * @Description: TODO(校内校外)
 * @author Jiaohaili
 * @date 2015年11月27日 上午11:55:18
 * 
 */
public class Cpk_School_In_And_Out{
	
	private String category_icon;
	private String parent_id;
	private String category_id;
	private String category_name;
	private List<Cpk_School_In_And_Out_Child> list;

	
	public String getCategory_icon() {
		return category_icon;
	}

	public void setCategory_icon(String category_icon) {
		this.category_icon = category_icon;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public List<Cpk_School_In_And_Out_Child> getList() {
		return list;
	}

	public void setList(List<Cpk_School_In_And_Out_Child> list) {
		this.list = list;
	}
	
}
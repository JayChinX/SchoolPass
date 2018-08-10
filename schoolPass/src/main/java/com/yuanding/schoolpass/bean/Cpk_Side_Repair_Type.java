package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午9:37:58 
 * 报修类型列表
 */
public class Cpk_Side_Repair_Type implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type_name;
	private String type_id;
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String type_id) {
		this.type_id = type_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

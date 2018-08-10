package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月26日 下午6:38:31 学校列表
 * 
 */
public class Cpk_School_List {

	private String stotal;
	private List<Item_Cate> mList;

	public String getStotal() {
		return stotal;
	}

	public void setStotal(String stotal) {
		this.stotal = stotal;
	}

	public List<Item_Cate> getmList() {
		return mList;
	}

	public void setmList(List<Item_Cate> mList) {
		this.mList = mList;
	}

}

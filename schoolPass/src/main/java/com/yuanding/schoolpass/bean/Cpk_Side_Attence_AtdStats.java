
package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-7 下午11:24:24
 * 考勤详细类
 */
public class Cpk_Side_Attence_AtdStats implements Serializable {

    private String name;
    private String count;
    private String status;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

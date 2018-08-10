package com.yuanding.schoolpass.bean;

import java.util.List;


/**
 *  
 * @version 创建时间：2015年11月27日 下午2:39:33 考勤个人详情
 */
public class Cpk_Side_Attence_Personal {
	
	
	private Cpk_Side_Attence_UserInfo userInfo;
	private List<Cpk_Side_Attence_AtdStats> atdStats;
	private String normalRate;
	private String totalCount;
	public Cpk_Side_Attence_UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(Cpk_Side_Attence_UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public List<Cpk_Side_Attence_AtdStats> getAtdStats() {
		return atdStats;
	}
	public void setAtdStats(List<Cpk_Side_Attence_AtdStats> atdStats) {
		this.atdStats = atdStats;
	}
	public String getNormalRate() {
		return normalRate;
	}
	public void setNormalRate(String normalRate) {
		this.normalRate = normalRate;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
}

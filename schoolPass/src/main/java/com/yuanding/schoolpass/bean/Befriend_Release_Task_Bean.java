package com.yuanding.schoolpass.bean;

/**
 *   获取发布任务列表：待处理 的实体类
 */
public class Befriend_Release_Task_Bean {
	/**
	 * 任务id
	 */
	private String id;
	/**
	 * 任务类型 0_默认，1_帮取，2_帮送，3_帮学，4_帮修，99_帮其他
	 */
	private String type;
	/**
	 * 任务标题
	 */
	private String title;
	/**
	 * 发布时间
	 */
	private String createTime;
	/**
	 * 任务状态：1_等待认领，2_已认领，3_待打款，4_已打款，8_已结束，9_发起人取消后
	 */
	private String taskStatus;
	/**
	 * 冻结状态：0_正常，1_冻结
	 */
	private String freezeStatus;
	private String HeoTaskWorkCreateUsername;
	private String items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}



	public String getFreezeStatus() {
		return freezeStatus;
	}

	public void setFreezeStatus(String freezeStatus) {
		this.freezeStatus = freezeStatus;
	}


	public String getHeoTaskWorkCreateUsername() {
		return HeoTaskWorkCreateUsername;
	}

	public void setHeoTaskWorkCreateUsername(String heoTaskWorkCreateUsername) {
		HeoTaskWorkCreateUsername = heoTaskWorkCreateUsername;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

}

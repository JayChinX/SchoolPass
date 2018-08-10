package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午1:55:22 首页通知信息
 */
public class Cpk_Index_Notice_Message {

	private String app_msg_level;
	private String app_msg_sign;//确定这个是消息来源名字
	private String count;
	private String msg_name; //消息名字
	private String type;

	private String ml_status;
	private String message_id;
	private String title;//发送的内容
	private String bg_img;
	private String create_time;
	
	private String targetId;//等同于UNIQID
	private String conversationtype;

	
	public Cpk_Index_Notice_Message() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    public Cpk_Index_Notice_Message(String app_msg_level, String app_msg_sign, String count,
            String msg_name, String type, String ml_status, String message_id, String title,
            String bg_img, String create_time, String targetId, String conversationtype) {
        super();
        this.app_msg_level = app_msg_level;
        this.app_msg_sign = app_msg_sign;
        this.count = count;
        this.msg_name = msg_name;
        this.type = type;
        this.ml_status = ml_status;
        this.message_id = message_id;
        this.title = title;
        this.bg_img = bg_img;
        this.create_time = create_time;
        this.targetId = targetId;
        this.conversationtype = conversationtype;
    }

    public String getApp_msg_level() {
		return app_msg_level;
	}

	public void setApp_msg_level(String app_msg_level) {
		this.app_msg_level = app_msg_level;
	}

	public String getApp_msg_sign() {
		return app_msg_sign;
	}

	public void setApp_msg_sign(String app_msg_sign) {
		this.app_msg_sign = app_msg_sign;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getMsg_name() {
		return msg_name;
	}

	public void setMsg_name(String msg_name) {
		this.msg_name = msg_name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getMl_status() {
		return ml_status;
	}

	public void setMl_status(String ml_status) {
		this.ml_status = ml_status;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBg_img() {
		return bg_img;
	}

	public void setBg_img(String bg_img) {
		this.bg_img = bg_img;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getConversationtype() {
		return conversationtype;
	}

	public void setConversationtype(String conversationtype) {
		this.conversationtype = conversationtype;
	}
	
}

package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午1:58:26 通知列表
 */
public class Cpk_Notice_List {
    private String ml_status;
    private String log_id;
    private String message_id;
    private String title;

    private String bg_img;
    private String content;
    private String type;

    private String app_msg_sign;
    private String app_msg_level;

    private String read_num;
    private String reply_num;
    private String desc;
    private String create_time;
    private String is_default;//是否为系统默认消息 0：否，1：是
    private String is_new;//1：最新，0：不是最新
    private boolean Selected;
    
    private int message_receipt; //该条消息是否需要回执
    private int is_receipt	; //表示消息是否已回执
    private int is_appendix;

    public int getIs_appendix() {
        return is_appendix;
    }

    public void setIs_appendix(int is_appendix) {
        this.is_appendix = is_appendix;
    }

    public boolean isSelected() {
		return Selected;
	}

	public void setSelected(boolean selected) {
		Selected = selected;
	}

	public String getApp_msg_sign() {
        return app_msg_sign;
    }

    public void setApp_msg_sign(String app_msg_sign) {
        this.app_msg_sign = app_msg_sign;
    }

    public String getRead_num() {
        return read_num;
    }

    public void setRead_num(String read_num) {
        this.read_num = read_num;
    }

    public String getReply_num() {
        return reply_num;
    }

    public void setReply_num(String reply_num) {
        this.reply_num = reply_num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApp_msg_level() {
        return app_msg_level;
    }

    public void setApp_msg_level(String app_msg_level) {
        this.app_msg_level = app_msg_level;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBg_img() {
        return bg_img;
    }

    public void setBg_img(String bg_img) {
        this.bg_img = bg_img;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

	public int getMessage_receipt() {
		return message_receipt;
	}

	public void setMessage_receipt(int message_receipt) {
		this.message_receipt = message_receipt;
	}

	public int getIs_receipt() {
		return is_receipt;
	}

	public void setIs_receipt(int is_receipt) {
		this.is_receipt = is_receipt;
	}

}
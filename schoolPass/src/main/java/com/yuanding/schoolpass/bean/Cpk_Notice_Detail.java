package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:39:33 通知详情
 */
public class Cpk_Notice_Detail {

	private String reply_num;
	private String title;
	private String message_id;;
	private String bg_img;
	private String content;
	private String create_time;
	private String read_num;
	private String log_id;
	private String photo_url;
	private String app_msg_sign;
	private String content_type;
	private long time;
	
	private int message_receipt;
	private int is_receipt;
	private String file_name;
	private String file_size;
	private String file_ext;
	private String file_url;
	private int is_appendix;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getFile_ext() {
		return file_ext;
	}

	public void setFile_ext(String file_ext) {
		this.file_ext = file_ext;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public int getIs_appendix() {
		return is_appendix;
	}

	public void setIs_appendix(int is_appendix) {
		this.is_appendix = is_appendix;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getApp_msg_sign() {
		return app_msg_sign;
	}

	public void setApp_msg_sign(String app_msg_sign) {
		this.app_msg_sign = app_msg_sign;
	}
    public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	private String type;//1是图文，4是短消息
    private String default_type;//是否是系统消息 : 0,否;大于0,是
    private String organ_str;
    
	public String getOrgan_str() {
		return organ_str;
	}

	public void setOrgan_str(String organ_str) {
		this.organ_str = organ_str;
	}

	private List<Cpk_Comment_detail> list;

	public String getReply_num() {
		return reply_num;
	}

	public void setReply_num(String reply_num) {
		this.reply_num = reply_num;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getBg_img() {
		return bg_img;
	}

	public void setBg_img(String bg_img) {
		this.bg_img = bg_img;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
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

	public List<Cpk_Comment_detail> getList() {
		return list;
	}

	public void setList(List<Cpk_Comment_detail> list) {
		this.list = list;
	}

	public String getLog_id() {
		return log_id;
	}

	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefault_type() {
        return default_type;
    }

    public void setDefault_type(String default_type) {
        this.default_type = default_type;
    }
	
}

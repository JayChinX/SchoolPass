package com.yuanding.schoolpass.bean;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:39:33 活动详情
 */
public class Cpk_Info_Detail_Content {

	private String info_id;
	private String title;;
	private String bg_img;
	private String content;
	private long create_time;
	private String comment_count;
	private String like_count;
    private String brows_count;
    
	public String getInfo_id() {
		return info_id;
	}
	public void setInfo_id(String info_id) {
		this.info_id = info_id;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getComment_count() {
		return comment_count;
	}
	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}
	public String getLike_count() {
		return like_count;
	}
	public void setLike_count(String like_count) {
		this.like_count = like_count;
	}
    public String getBrows_count() {
        return brows_count;
    }
    public void setBrows_count(String brows_count) {
        this.brows_count = brows_count;
    }
}

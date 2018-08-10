package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:39:33 活动详情
 */
public class Cpk_Acy_Detail {

	private long create_time;
	private String activity_time;
	private String read_num;;
	private String bg_img;
	private String title;
	private long join_end_time;
	
	private String article_id;
	private String content;
	private String place;
	private String join_num;
	private String comment_num;
	private List<Cpk_Comment_detail> list;
	private String is_enroll;
	
	private long start_time;
	private long end_time;
	
	public String getIs_enroll() {
		return is_enroll;
	}

	public void setIs_enroll(String is_enroll) {
		this.is_enroll = is_enroll;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public String getActivity_time() {
		return activity_time;
	}

	public void setActivity_time(String activity_time) {
		this.activity_time = activity_time;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
	}

	public String getBg_img() {
		return bg_img;
	}

	public void setBg_img(String bg_img) {
		this.bg_img = bg_img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getJoin_num() {
		return join_num;
	}

	public void setJoin_num(String join_num) {
		this.join_num = join_num;
	}

	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public List<Cpk_Comment_detail> getList() {
		return list;
	}

	public void setList(List<Cpk_Comment_detail> list) {
		this.list = list;
	}

	public long getJoin_end_time() {
		return join_end_time;
	}

	public void setJoin_end_time(long join_end_time) {
		this.join_end_time = join_end_time;
	}

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}

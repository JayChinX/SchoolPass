package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午11:34:32  列表
 */
public class Cpk_Acy_list {

	private String join_num;
	private String comment_num;
	private long join_end_time;
	private String create_time;
	private String title;

	private String bg_img;
	private String article_id;
	private long start_time;
	private String share_url;

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
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

	private long end_time;

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

	public long getJoin_end_time() {
		return join_end_time;
	}

	public void setJoin_end_time(long join_end_time) {
		this.join_end_time = join_end_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

}

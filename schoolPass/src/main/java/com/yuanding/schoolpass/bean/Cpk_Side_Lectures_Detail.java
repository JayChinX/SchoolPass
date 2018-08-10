package com.yuanding.schoolpass.bean;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午9:36:34 讲座详情
 */
public class Cpk_Side_Lectures_Detail {

	private String author;
	private String author_desc;
	private String lecture_time;
	private String read_num;
	private long create_time;

	private String title;
	private String content;
	private String place;
	private String article_id;
	private String organ_str;

	public String getOrgan_str() {
		return organ_str;
	}

	public void setOrgan_str(String organ_str) {
		this.organ_str = organ_str;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor_desc() {
		return author_desc;
	}

	public void setAuthor_desc(String author_desc) {
		this.author_desc = author_desc;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
	}

	public String getLecture_time() {
		return lecture_time;
	}

	public void setLecture_time(String lecture_time) {
		this.lecture_time = lecture_time;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

}

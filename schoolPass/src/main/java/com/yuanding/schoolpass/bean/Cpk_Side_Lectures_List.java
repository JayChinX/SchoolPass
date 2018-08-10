package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午9:37:58 
 * 讲座列表
 */
public class Cpk_Side_Lectures_List implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long end_time;
	private String place;
	private String read_num;
	private long start_time;
	private String author;

	private String article_id;
	private long create_time;
	private String title;
	private String author_desc;

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
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

	public String getAuthor_desc() {
		return author_desc;
	}

	public void setAuthor_desc(String author_desc) {
		this.author_desc = author_desc;
	}

}

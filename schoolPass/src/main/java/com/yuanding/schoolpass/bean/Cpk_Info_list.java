package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午11:34:32 活动列表
 */
public class Cpk_Info_list {

    private long create_time;

    private String info_id;
    private String title;
    private String desc;
    private String bg_img;
    private String comment_count;
    private String like_count;
    private String is_jump;
    private String jump_url;
    private String brows_count;
    private String share_url;

    public String getBrows_count() {
        return brows_count;
    }

    public void setBrows_count(String brows_count) {
        this.brows_count = brows_count;
    }

    public String getIs_jump() {
        return is_jump;
    }

    public void setIs_jump(String is_jump) {
        this.is_jump = is_jump;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBg_img() {
        return bg_img;
    }

    public void setBg_img(String bg_img) {
        this.bg_img = bg_img;
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

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }
}

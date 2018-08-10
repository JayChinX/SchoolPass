
package com.yuanding.schoolpass.bean;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-8 上午12:02:40 签到列表
 */
public class Cpk_Side_Attence_List {

    private String atd_id;
    private String title;
    private String place;
    private String atd_status;
    private String create_time;
    
    private String meter;
    private String status;
    private String atd_time;
    private String true_name;
    
//    private String user_success_attdence;//1点击过，其他的为没有
    public String getAtd_id() {
        return atd_id;
    }

    public void setAtd_id(String atd_id) {
        this.atd_id = atd_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMeter() {
        return meter;
    }

    public void setMeter(String meter) {
        this.meter = meter;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAtd_time() {
        return atd_time;
    }

    public void setAtd_time(String atd_time) {
        this.atd_time = atd_time;
    }

    public String getTrue_name() {
        return true_name;
    }

    public void setTrue_name(String true_name) {
        this.true_name = true_name;
    }

    public String getAtd_status() {
        return atd_status;
    }

    public void setAtd_status(String atd_status) {
        this.atd_status = atd_status;
    }

//    public String getUser_success_attdence() {
//        return user_success_attdence;
//    }
//
//    public void setUser_success_attdence(String user_success_attdence) {
//        this.user_success_attdence = user_success_attdence;
//    }

}


package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-7 下午11:24:24
 * 考勤详细类
 */
public class Cpk_Side_Attence implements Serializable {

    private String atd_id;
    private String title;
    private String place;
    private String status;
    private String atd_time;
    
    private String create_time;
    private String absent;
    private String late;
    private String early;
    private String leave;
    
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getEarly() {
        return early;
    }

    public void setEarly(String early) {
        this.early = early;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getAtd_time() {
        return atd_time;
    }

    public void setAtd_time(String atd_time) {
        this.atd_time = atd_time;
    }

}

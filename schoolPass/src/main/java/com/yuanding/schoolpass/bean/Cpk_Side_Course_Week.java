
package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年3月3日 下午2:33:54 一周的Bean
 */
public class Cpk_Side_Course_Week {

    private String week;
    private String month;
    private String detail_id;// 课表详情ID
    private String course_id;
    private List<Cpk_Side_Course_WeekDetail> weekly_list;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List<Cpk_Side_Course_WeekDetail> getWeekly_list() {
        return weekly_list;
    }

    public void setWeekly_list(List<Cpk_Side_Course_WeekDetail> weekly_list) {
        this.weekly_list = weekly_list;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
    

}

package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年3月3日 下午2:35:59
 * 一周中的一天
 */
public class Cpk_Side_Course_WeekDetail {
    
    private String month;
    private String weekday;
    private String date;
    private List<Cpk_Side_Course_CourseDetail> course;
    
    public String getWeekday() {
        return weekday;
    }
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public List<Cpk_Side_Course_CourseDetail> getCourse() {
        return course;
    }
    public void setCourse(List<Cpk_Side_Course_CourseDetail> course) {
        this.course = course;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }

}

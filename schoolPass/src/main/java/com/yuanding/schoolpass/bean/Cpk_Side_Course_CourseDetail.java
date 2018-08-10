
package com.yuanding.schoolpass.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年3月3日 下午2:38:18 一节课
 */
public class Cpk_Side_Course_CourseDetail implements Serializable {
    
    private String coursecode;
    private String coursename;
    private String teachername;
    private int startsection;
    private int endsection;

    private String weekday_id;
    private String place;
    private String cm_id;
    private String courseweek;
    private List<String> organ_names;
    
    private List<Cpk_User_Values> user_values;
    private String class_ids;
    
    private String date;
    private int today;
    
    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }


    public int getStartsection() {
        return startsection;
    }

    public void setStartsection(int startsection) {
        this.startsection = startsection;
    }

    public int getEndsection() {
        return endsection;
    }

    public void setEndsection(int endsection) {
        this.endsection = endsection;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCm_id() {
        return cm_id;
    }

    public void setCm_id(String cm_id) {
        this.cm_id = cm_id;
    }

    public String getCourseweek() {
        return courseweek;
    }

    public void setCourseweek(String courseweek) {
        this.courseweek = courseweek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getToday() {
        return today;
    }

    public void setToday(int today) {
        this.today = today;
    }

    public List<Cpk_User_Values> getUser_values() {
        return user_values;
    }

    public void setUser_values(List<Cpk_User_Values> user_values) {
        this.user_values = user_values;
    }

    public List<String> getOrgan_names() {
        return organ_names;
    }

    public void setOrgan_names(List<String> organ_names) {
        this.organ_names = organ_names;
    }

    public String getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(String weekday_id) {
        this.weekday_id = weekday_id;
    }

    public String getClass_ids() {
        return class_ids;
    }

    public void setClass_ids(String class_ids) {
        this.class_ids = class_ids;
    }
    
}

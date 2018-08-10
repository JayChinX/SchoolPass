
package com.yuanding.schoolpass.bean;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2017年1月11日 下午1:52:44 类说明
 */
public class Cpk_Side_Course_Official {
    private String cm_id;// 课程ID
    private String school_id;// 学校ID
    private String course_name;// 课程名称
    private String first_letter;
    private String course_code;

    private String teacher_uniqid;
    private String course_teacher;// 课程教师名
    private String location;
    private String type_source;// 课程来源 0：官方 1： 教师添加
    private String create_time;

    private String teacher_id;
    
    public String getCm_id() {
        return cm_id;
    }

    public void setCm_id(String cm_id) {
        this.cm_id = cm_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getFirst_letter() {
        return first_letter;
    }

    public void setFirst_letter(String first_letter) {
        this.first_letter = first_letter;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getTeacher_uniqid() {
        return teacher_uniqid;
    }

    public void setTeacher_uniqid(String teacher_uniqid) {
        this.teacher_uniqid = teacher_uniqid;
    }

    public String getCourse_teacher() {
        return course_teacher;
    }

    public void setCourse_teacher(String course_teacher) {
        this.course_teacher = course_teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType_source() {
        return type_source;
    }

    public void setType_source(String type_source) {
        this.type_source = type_source;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

}

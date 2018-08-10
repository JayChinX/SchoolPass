
package com.yuanding.schoolpass.bean;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年10月24日 下午1:48:41 
 * 校务助手列表详情
 */
public class Cpk_School_Assistant_detail {

    private String message_id;
    private String title;
    private String content;
    private String create_time;
    private String is_read;
    
    private String post_id;
    private String btn_enable;//是否显示操作按钮，0：不显示，1：显示
    private String oper_type;//审核状态，1：通过，2：拒绝
    private String btn_attend;
//    private List<Cpk_User_Values> user_values;
    
    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getBtn_attend() {
        return btn_attend;
    }

    public void setBtn_attend(String btn_attend) {
        this.btn_attend = btn_attend;
    }

    public String getBtn_enable() {
        return btn_enable;
    }

    public void setBtn_enable(String btn_enable) {
        this.btn_enable = btn_enable;
    }

    public String getOper_type() {
        return oper_type;
    }

    public void setOper_type(String oper_type) {
        this.oper_type = oper_type;
    }

//    public List<Cpk_User_Values> getUser_values() {
//        return user_values;
//    }
//
//    public void setUser_values(List<Cpk_User_Values> user_values) {
//        this.user_values = user_values;
//    }

}

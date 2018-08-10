
package com.yuanding.schoolpass.bean;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年10月24日 下午1:48:41 
 * 校务助手列表
 */
public class Cpk_School_Assistant_List {

    private String message_id;
    private String title;
    private String content;
    private String create_time;
    private String is_read;
    private String type;
    private String link_id;
    private String jump_module;
    private String message_type;
    private String leave_detail_url;

    
    private boolean Selected;

    public String getLeave_detail_url() {
        return leave_detail_url;
    }

    public void setLeave_detail_url(String leave_detail_url) {
        this.leave_detail_url = leave_detail_url;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }
    
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
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink_id() {
        return link_id;
    }

    public void setLink_id(String link_id) {
        this.link_id = link_id;
    }

    public String getJump_module() {
        return jump_module;
    }

    public void setJump_module(String jump_module) {
        this.jump_module = jump_module;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}


package com.yuanding.schoolpass.bean;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年11月30日 下午5:32:17 校务助手
 */
public class Cpk_School_Assistant_Main {

    private String name;// 标题
    private String type;
    private String content;// 内容
    private String create_time;
    private String unread_num;
    private String icon_url;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUnread_num() {
        return unread_num;
    }

    public void setUnread_num(String unread_num) {
        this.unread_num = unread_num;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

}

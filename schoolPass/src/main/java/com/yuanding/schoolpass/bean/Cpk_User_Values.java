
package com.yuanding.schoolpass.bean;

import java.io.Serializable;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年12月7日 上午10:07:55 类说明
 */
public class Cpk_User_Values implements Serializable{
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

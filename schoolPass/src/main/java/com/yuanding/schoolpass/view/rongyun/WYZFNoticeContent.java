
package com.yuanding.schoolpass.view.rongyun;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.util.Log;

/**
 * @author MyPC自定义转发
 */
@MessageTag(value = "WYZFNoticeContent", flag = MessageTag.ISPERSISTED)
public class WYZFNoticeContent extends MessageContent {
    private String titleStr;// 标题
    private String share_content;// 详情
    private String imgUrl;// 图片（头像）
    private String noticeId;// ID
    private String notice_sendUserName;// 发送者
    private String share_time;// 发送时间
    private String unReadCount;// 未读数量
    private String message_level;// 消息等级
    private String type;// 1.通知详情 2.活动详情 3.讲座详情
    private String placeImg;
    private String acy_type;
    // private String log_id;
    private String course_user_uniqid;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public WYZFNoticeContent(String titleStr, String share_content, String imgUrl, String type,
            String noticeId,String notice_sendUserName, String share_time,
            String unReadCount, String message_level, String placeImg,String acy_type, String course_user_uniqid) {
        this.titleStr = titleStr;
        this.share_content = share_content;
        this.imgUrl = imgUrl;
        this.type = type;
        
        this.noticeId = noticeId;
        this.notice_sendUserName = notice_sendUserName;
        this.share_time = share_time;
        this.unReadCount = unReadCount;
        
        this.message_level = message_level;
        this.placeImg = placeImg;
        this.acy_type = acy_type;
        this.course_user_uniqid = course_user_uniqid;
        // this.log_id = log_id;
    }
    
    public String getAcy_type() {
        return acy_type;
    }

    public void setAcy_type(String acy_type) {
        this.acy_type = acy_type;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        ParcelUtils.writeToParcel(dest, titleStr);
        ParcelUtils.writeToParcel(dest, share_content);
        ParcelUtils.writeToParcel(dest, imgUrl);
        ParcelUtils.writeToParcel(dest, type);

        ParcelUtils.writeToParcel(dest, noticeId);
        ParcelUtils.writeToParcel(dest, notice_sendUserName);
        ParcelUtils.writeToParcel(dest, share_time);
        ParcelUtils.writeToParcel(dest, unReadCount);
        ParcelUtils.writeToParcel(dest, message_level);
        ParcelUtils.writeToParcel(dest, placeImg);
        ParcelUtils.writeToParcel(dest, acy_type);
        ParcelUtils.writeToParcel(dest, course_user_uniqid);
        // ParcelUtils.writeToParcel(dest, log_id);
    }

    /**
     * 构造函数。
     * 
     * @param in 初始化传入的 Parcel。
     */
    public WYZFNoticeContent(Parcel in) {
        setTitleStr(ParcelUtils.readFromParcel(in));
        setDetailStr(ParcelUtils.readFromParcel(in));
        setImgUrl(ParcelUtils.readFromParcel(in));
        setType(ParcelUtils.readFromParcel(in));
        setNoticeId(ParcelUtils.readFromParcel(in));
        setNotice_sendUserName(ParcelUtils.readFromParcel(in));
        setNoticetime(ParcelUtils.readFromParcel(in));
        setUnReadCount(ParcelUtils.readFromParcel(in));
        setMessage_level(ParcelUtils.readFromParcel(in));
        setPlaceImg(ParcelUtils.readFromParcel(in));

        setAcy_type(ParcelUtils.readFromParcel(in));
        setCourse_user_uniqid(ParcelUtils.readFromParcel(in));
        // setLog_id(ParcelUtils.readFromParcel(in));

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        System.out.println(jsonObj+"---------------------1111---------");
        try {
            jsonObj.put("titleStr", titleStr);
            jsonObj.put("share_content", share_content);
            jsonObj.put("imgUrl", imgUrl);
            jsonObj.put("type", type);
            jsonObj.put("noticeId", noticeId);
            jsonObj.put("notice_sendUserName", notice_sendUserName);
            jsonObj.put("share_time", share_time);
            jsonObj.put("unReadCount", unReadCount);
            jsonObj.put("message_level", message_level);
            jsonObj.put("placeImg", placeImg);
            jsonObj.put("acy_type", acy_type);
            jsonObj.put("course_user_uniqid", course_user_uniqid);
            // jsonObj.put("log_id", log_id);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public WYZFNoticeContent(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            System.out.println(jsonObj+"---------------------2222---------");
            titleStr = jsonObj.optString("titleStr");
            share_content = jsonObj.optString("share_content");
            imgUrl = jsonObj.optString("imgUrl");
            type = jsonObj.optString("type");
            noticeId = jsonObj.optString("noticeId");
            notice_sendUserName = jsonObj.optString("notice_sendUserName");
            share_time = jsonObj.optString("share_time");
            unReadCount = jsonObj.optString("unReadCount");
            message_level = jsonObj.optString("message_level");
            placeImg = jsonObj.optString("placeImg");
            acy_type = jsonObj.optString("acy_type");
            course_user_uniqid = jsonObj.optString("course_user_uniqid");
            // log_id = jsonObj.optString("log_id");
        } catch (JSONException e) {
        }

    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<WYZFNoticeContent> CREATOR = new Creator<WYZFNoticeContent>() {

        @Override
        public WYZFNoticeContent createFromParcel(Parcel source) {
            return new WYZFNoticeContent(source);
        }

        @Override
        public WYZFNoticeContent[] newArray(int size) {
            return new WYZFNoticeContent[size];
        }
    };

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getDetailStr() {
        return share_content;
    }

    public void setDetailStr(String detailStr) {
        this.share_content = detailStr;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNotice_sendUserName() {
        return notice_sendUserName;
    }

    public void setNotice_sendUserName(String notice_sendUserName) {
        this.notice_sendUserName = notice_sendUserName;
    }

    public String getNoticetime() {
        return share_time;
    }

    public void setNoticetime(String noticetime) {
        share_time = noticetime;
    }

    public String getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getMessage_level() {
        return message_level;
    }

    public void setMessage_level(String message_level) {
        this.message_level = message_level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaceImg() {
        return placeImg;
    }

    public void setPlaceImg(String placeImg) {
        this.placeImg = placeImg;
    }

    public String getCourse_user_uniqid() {
        return course_user_uniqid;
    }

    public void setCourse_user_uniqid(String course_user_uniqid) {
        this.course_user_uniqid = course_user_uniqid;
    }

}

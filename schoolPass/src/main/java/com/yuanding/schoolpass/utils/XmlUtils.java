
package com.yuanding.schoolpass.utils;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.yuanding.schoolpass.bean.Cpk_Index_Notice_Message;

import android.content.Context;
import android.util.Log;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月22日 下午12:48:27 解析XML
 */
public class XmlUtils {

    public static void serialize(Context context, String filename,
            List<Cpk_Index_Notice_Message> list) {

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlSerializer serializer = factory.newSerializer();

            serializer.setOutput(context.openFileOutput(filename, Context.MODE_PRIVATE), "utf-8");

            serializer.startDocument("utf-8", true);

            serializer.startTag(null, "students");

            for (Cpk_Index_Notice_Message s : list) {

                serializer.startTag(null, "student");
                
                String aa = s.getMessage_id();
                if (aa != null && !aa.equals("null"))
                    serializer.attribute(null, "message_id", aa);
                else
                    serializer.attribute(null, "message_id", "");

                serializer.startTag(null, "app_msg_level");
                String bb = s.getApp_msg_level();
                if (bb != null && !bb.equals("null"))
                    serializer.text(bb);
                else
                    serializer.text("");
                serializer.endTag(null, "app_msg_level");

                serializer.startTag(null, "app_msg_sign");
                String cc = s.getApp_msg_sign();
                if (cc != null && !cc.equals("null"))
                    serializer.text(cc);
                else
                    serializer.text("");
                serializer.endTag(null, "app_msg_sign");
                
                serializer.startTag(null, "bg_img");
                String dd = s.getBg_img();
                if (dd != null && !dd.equals("null"))
                    serializer.text(dd);
                else
                    serializer.text("");
                serializer.endTag(null, "bg_img");
                
                serializer.startTag(null, "conversationtype");
                String ee = s.getConversationtype();
                if (ee != null && !ee.equals("null"))
                    serializer.text(ee);
                else
                    serializer.text("");
                serializer.endTag(null, "conversationtype");
                
                serializer.startTag(null, "count");
                String ff = s.getCount();
                if (ff != null && !ff.equals("null"))
                    serializer.text(ff);
                else
                    serializer.text("");
                serializer.endTag(null, "count");

                serializer.startTag(null, "create_time");
                String gg = s.getCreate_time();
                if (gg != null && !gg.equals("null"))
                    serializer.text(gg);
                else
                    serializer.text("");
                serializer.endTag(null, "create_time");
                
                serializer.startTag(null, "ml_status");
                String ll = s.getMl_status();
                if (ll != null && !ll.equals("null"))
                    serializer.text(ll);
                else
                    serializer.text("");
                serializer.endTag(null, "ml_status");
                
                serializer.startTag(null, "msg_name");
                String hh = s.getMsg_name();
                if (hh != null && !hh.equals("null"))
                    serializer.text(hh);
                else
                    serializer.text("");
                serializer.endTag(null, "msg_name");

                serializer.startTag(null, "targetId");
                String ii = s.getTargetId();
                if (ii != null && !ii.equals("null"))
                    serializer.text(ii);
                else
                    serializer.text("");
                serializer.endTag(null, "targetId");
                
                serializer.startTag(null, "title");
                String jj = s.getTitle();
                if (jj != null && !jj.equals("null"))
                    try {
                        serializer.text(jj);
                    } catch (Exception e) {
                        serializer.text("[表情]");
                        e.printStackTrace();
                    }
                else
                    serializer.text("");
                serializer.endTag(null, "title");

                serializer.startTag(null, "type");
                String kk = s.getType();
                if (kk != null && !kk.equals("null"))
                    serializer.text(kk);
                else
                    serializer.text("");
                serializer.endTag(null, "type");

                serializer.endTag(null, "student");

            }

            serializer.endTag(null, "students");

            serializer.endDocument();

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }

    public static List<Cpk_Index_Notice_Message> parse(Context context, String filename) {

        List<Cpk_Index_Notice_Message> list = new ArrayList<Cpk_Index_Notice_Message>();

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = factory.newPullParser();

            parser.setInput(context.openFileInput(filename), "utf-8");

            Cpk_Index_Notice_Message s = null;

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                    String tag = parser.getName();

                    if (tag.equals("student")) {
                        s = new Cpk_Index_Notice_Message();
                        s.setMessage_id(parser.getAttributeValue(0));

                    } else if (tag.equals("app_msg_level")) {
                        s.setApp_msg_level(parser.nextText());
                    } else if (tag.equals("app_msg_sign")) {
                        s.setApp_msg_sign(parser.nextText());
                    } else if (tag.equals("bg_img")) {
                        s.setBg_img(parser.nextText());
                    } else if (tag.equals("conversationtype")) {
                        s.setConversationtype(parser.nextText());
                    } else if (tag.equals("count")) {
                        s.setCount(parser.nextText());
                    } else if (tag.equals("create_time")) {
                        s.setCreate_time(parser.nextText());
                    } else if (tag.equals("ml_status")) {
                        s.setMl_status(parser.nextText());
                    } else if (tag.equals("msg_name")) {
                        s.setMsg_name(parser.nextText());
                    } else if (tag.equals("targetId")) {
                        s.setTargetId(parser.nextText());
                    }  else if (tag.equals("title")) {
                        s.setTitle(parser.nextText());
                    } else if (tag.equals("type")) {
                        s.setType(parser.nextText());
                    }

                } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("student")) {

                    list.add(s);

                }

                eventType = parser.next();

            }

            for (Cpk_Index_Notice_Message stu : list) {
                Log.e("test", stu.toString());
            }

        } catch (Exception e) {
            return list;
        }

        return list;

    }

}

package com.yuanding.schoolpass.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.B_Mess_Forward_Select;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;
import static com.yuanding.schoolpass.utils.AppStrStatic.default_ShareContent;
import static com.yuanding.schoolpass.utils.AppStrStatic.default_ShareTitle;
import static com.yuanding.schoolpass.utils.Bimp.bmp;

/**
 * Created by Administrator on 2017/4/17.
 */

public class PubForwardOrShare {

    private String share_url_text = "";//分享的URL
    private String share_url_title = "";//分享得标题
    private String share_url_time = "";//分享得时间
    private String share_url_pic = "";//分享得图片
    Context con;
    private String acy_detail_id;
    private String acy_type,type,content ="";

    public PubForwardOrShare(String share_url_text, String share_url_title, String share_url_time, String share_url_pic, Context con, String acy_detail_id, String acy_type, String type, String content) {
        this.share_url_text = share_url_text;
        this.share_url_title = share_url_title;
        this.share_url_time = share_url_time;
        this.share_url_pic = share_url_pic;
        this.con = con;
        this.acy_detail_id = acy_detail_id;
        this.acy_type = acy_type;
        this.type = type;
        this.content = content;
    }
    boolean isInstallWeiXin;
    public void showDialog() {
        //注册到微信
        api = WXAPIFactory.createWXAPI(con, PayStrStatic.WX_APP_ID, true);
        api.registerApp(PayStrStatic.WX_APP_ID);
        isInstallWeiXin = api.isWXAppSupportAPI();

        final Dialog dialog = new Dialog(con, R.style.shareDialog);
        dialog.setContentView(R.layout.dialog_share_side_info);
        RelativeLayout rel_my_weixin_friend = (RelativeLayout) dialog.findViewById(R.id.rel_my_weixin_friend);
        RelativeLayout rel_my_weixin_friend_circle = (RelativeLayout) dialog.findViewById(R.id.rel_my_weixin_friend_circle);
        RelativeLayout rel_my_application_forward = (RelativeLayout) dialog.findViewById(R.id.rel_my_application_forward);
        RelativeLayout rel_share_side_cancel = (RelativeLayout) dialog.findViewById(R.id.rel_share_side_cancel);
        rel_my_weixin_friend.setVisibility(View.VISIBLE);
        rel_my_weixin_friend_circle.setVisibility(View.VISIBLE);
        rel_my_application_forward.setVisibility(View.VISIBLE);
        rel_share_side_cancel.setVisibility(View.VISIBLE);

        rel_my_weixin_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//微信好友
                dialog.dismiss();
                if (isInstallWeiXin){
                    shareReq(1);
                }else {
                    Toast.makeText(con, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rel_my_weixin_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//微信朋友圈
                dialog.dismiss();
                if (isInstallWeiXin){
                    shareReq(2);
                }else {
                    Toast.makeText(con, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rel_my_application_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (A_0_App.USER_STATUS.equals("2")) {//应用内转发
                    Intent intent = new Intent(con, B_Mess_Forward_Select.class);
                    intent.putExtra("title", share_url_title);
                    intent.putExtra("content", content);
                    intent.putExtra("type", type);
                    intent.putExtra("image", share_url_pic);
                    intent.putExtra("acy_type", acy_type);
                    intent.putExtra("noticeId", acy_detail_id);
                    con.startActivity(intent);
                }else{
                    PubMehods.showToastStr(con,R.string.str_no_certified_not_use);
                }
            }
        });
        rel_share_side_cancel.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    private IWXAPI api;
    private int mTargetScene;
    private static final int THUMB_SIZE = 150;

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static Bitmap getBitmap(String imageUrl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        return ImageLoader.getInstance().loadImageSync(imageUrl, options);
    }

    /**
     * @param sharetype 分享类型
     *                  1 微信朋友 2 微信朋友圈
     */
    private void shareReq(int sharetype) {

        //检查网络
        if (!NetUtils.isConnected(con)) {
            PubMehods.showToastStr(con, con.getResources().getString(R.string.error_title_net_error));
            return;
        }

        SHARE_MEDIA mSHARE_MEDIA = SHARE_MEDIA.WEIXIN;
        if (1 == sharetype) {
            mTargetScene = WXSceneSession;
        }
        if (2 == sharetype) {
            mTargetScene = WXSceneTimeline;
        }
        if (share_url_text.isEmpty()) {
            PubMehods.showToastStr(con, con.getResources().getString(R.string.str_my_account_invite_sharedisable_prompt));
            return;
        }

        //开始微信分享  回调WXEntryActivity
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = share_url_text;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = share_url_title;
        msg.description = share_url_text;

        Bitmap thumbBmp;
        thumbBmp = Bitmap.createScaledBitmap(getBitmap(share_url_pic), 150, 150, true);

        msg.thumbData = WeiXinUtil.bmpToByteArray(thumbBmp, true, 32);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);


    }
}

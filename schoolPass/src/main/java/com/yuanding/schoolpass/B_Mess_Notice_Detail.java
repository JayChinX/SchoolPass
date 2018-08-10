
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Comment_detail;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Content_More;
import com.yuanding.schoolpass.bean.Cpk_Notice_Detail;
import com.yuanding.schoolpass.utils.FileSizeUtils;
import com.yuanding.schoolpass.service.Api;
import com.yuanding.schoolpass.service.Api.InterAdd_Notice_Comment;
import com.yuanding.schoolpass.service.Api.InterNoticeCommentList;
import com.yuanding.schoolpass.service.Api.InterNoticeDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.SureReceIptCallBack;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.VerticalImageSpan;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

import static com.yuanding.schoolpass.B_Mess_Notice_Detail_Official_News.getFileIntent;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 通知详情
 */
public class B_Mess_Notice_Detail extends A_0_CpkBaseTitle_Navi {

    private View mess_notice_detail_load_error, liner_notice_detail_whole,
            mess_notice_detail_loading;
    private LinearLayout liner_notice_detail_send;
    private ImageView iv_notice_detail_banner, iv_notice_detail_send;
    private TextView tv_message_detail_title, tv_message_detail_notice_time,
            tv_message_detail_replay_count, tv_message_detail_read_count;
    private LinearLayout tv_notice_detail_comment_count;

    private WebView webview_notice_detail;
    private TextView tv_notice_detail;
    private PullToRefreshListView lv_notice_detail_comment;
    private EditText tv_notice_detail_comment_text;
    private LinearLayout liner_notice_detail_container;

    private Mydapter adapter;
    private String message_id, log_id, mess_content;
    RelativeLayout layout_pinglun, layout_huizhi;

    private List<Cpk_Comment_detail> list;
    private Cpk_Notice_Detail detail_Notice;

    protected ImageLoader imageLoader, imageLoaderBanner;
    private DisplayImageOptions options, optionsBanner;
    private ImageOptions bitmapUtils;
    // private SolveClashListView solveClashListView;

    private int acy_type;//页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
    private int have_read_page = 2;// 已经读的页数
    private boolean readCommentData = false;
    private ACache maACache;
    private JSONObject jsonObject;
    private TextView tv_mess_detial_from;
    private String is_share = "0";

    private LinearLayout liner_acy_detail;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private static long severTime = 0;
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
    private int repfresh = 0;// 避免下拉和上拉冲突
    private boolean havaSuccessLoadData, haveReceipted = false;
    private String school_notice = "";

    /**
     * 附件
     */
    private LinearLayout tv_notice_detail_comment_other;
    private ImageView fileImage;
    private TextView fileName, fileDet;
    private ProgressBar fileProgressBar;
    private RelativeLayout mess_notice_detail_load_other;
    private String file_url;
    private String file_name;
    private String file_ext;
    private long file_size;
    private String BASE_PATH = android.os.Environment.getExternalStorageDirectory() + AppStrStatic.SD_PIC + "/";
    boolean DonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail_havehuizhibtn);

        setTitleText("通知详情");

        detail_Notice = new Cpk_Notice_Detail();
        acy_type = getIntent().getExtras().getInt("acy_type");
        //页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
        if (acy_type == 1) {
            message_id = getIntent().getExtras().getString("message_id");
            mess_content = getIntent().getExtras().getString("mess_content");
            is_share = "1";
        } else if (acy_type == 2) {
            mess_content = getIntent().getExtras().getString("mess_content");
            message_id = getIntent().getExtras().getString("message_id");
            school_notice = getIntent().getExtras().getString("school_notice");
        } else if (acy_type == 3) {
            message_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        } else if (acy_type == 4) {
            message_id = getIntent().getExtras().getString("message_id");
            school_notice = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }
        if (school_notice == null) {
            school_notice = "";
        }
        demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) findViewById(R.id.demo_swiperefreshlayout); // 新增下拉使用

        mess_notice_detail_load_error = findViewById(R.id.mess_notice_detail_load_error);
        liner_notice_detail_whole = findViewById(R.id.liner_notice_detail_whole);
        mess_notice_detail_loading = findViewById(R.id.mess_notice_detail_loading);

        layout_pinglun = (RelativeLayout) findViewById(R.id.layout_pinglun);
        layout_huizhi = (RelativeLayout) findViewById(R.id.layout_huizhi);

        home_load_loading = (LinearLayout) mess_notice_detail_loading
                .findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        lv_notice_detail_comment = (PullToRefreshListView) findViewById(R.id.lv_notice_detail_comment);
        liner_notice_detail_send = (LinearLayout) findViewById(R.id.liner_notice_detail_send);
        iv_notice_detail_send = (ImageView) findViewById(R.id.iv_notice_detail_send);
        tv_notice_detail_comment_text = (EditText) findViewById(R.id.tv_notice_detail_comment_text);

        imageLoaderBanner = A_0_App.getInstance().getimageLoader();
        optionsBanner = A_0_App.getInstance().getOptions(R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg);
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
        bitmapUtils = A_0_App.getBitmapUtils(this, R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg, false);

        list = new ArrayList<Cpk_Comment_detail>();
        adapter = new Mydapter();
        addHeadView(lv_notice_detail_comment);
        lv_notice_detail_comment.setAdapter(adapter);

        lv_notice_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
        lv_notice_detail_comment
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {

                        String label = DateUtils.formatDateTime(
                                getApplicationContext(), System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                        readData(message_id);

                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (repfresh == 0) {
                            repfresh = 1;
                            demo_swiperefreshlayout.setEnabled(false);
                            demo_swiperefreshlayout.setRefreshing(false);
                            getMoreComment(message_id, have_read_page);

                        }
                    }
                });

        /**
         * 新增下拉使用 new add
         */
        demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
        demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
        if (repfresh == 0) {
            repfresh = 1;
            if (lv_notice_detail_comment != null) {
                lv_notice_detail_comment.onRefreshComplete();
            }
            demo_swiperefreshlayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        public void onRefresh() {
                            have_read_page = 1;
                            lv_notice_detail_comment.setMode(Mode.DISABLED);
                            readData(message_id);

                        }

                        ;
                    });
        }
        lv_notice_detail_comment.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                if (demo_swiperefreshlayout != null
                        && lv_notice_detail_comment.getChildCount() > 0
                        && lv_notice_detail_comment.getRefreshableView().getFirstVisiblePosition() == 0
                        && lv_notice_detail_comment.getChildAt(0).getTop() >= lv_notice_detail_comment
                        .getPaddingTop()) {
                    // 解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
                    demo_swiperefreshlayout.setEnabled(true);
                } else {
                    demo_swiperefreshlayout.setEnabled(false);
                }

            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });
        mess_notice_detail_load_error.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false);
                readData(message_id);
            }
        });

        tv_notice_detail_comment_text.setBackgroundResource(R.drawable.bg_edit_source_normal);
        tv_notice_detail_comment_text.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    tv_notice_detail_comment_text
                            .setBackgroundResource(R.drawable.bg_edit_source_focus);
                } else {
                    tv_notice_detail_comment_text.setTextColor(getResources().getColor(
                            R.color.title_no_focus_login));
                }
            }
        });
        tv_notice_detail_comment_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !"".equals(s.toString())) {
                    iv_notice_detail_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, "已达到最大字数限制");
                        tv_notice_detail_comment_text.setText(tv_notice_detail_comment_text
                                .getText().toString()
                                .substring(0, AppStrStatic.WORD_COMMENT_MAX_LIMIT));
                        tv_notice_detail_comment_text
                                .setSelection(AppStrStatic.WORD_COMMENT_MAX_LIMIT);
                    } else {
                        // sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
                    }
                } else {
                    iv_notice_detail_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send_enable));
                }
            }
        });

        // 评论
        liner_notice_detail_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String str = tv_notice_detail_comment_text.getText().toString();
                if (A_0_App.USER_STATUS.equals("2")) {
                    if (str != null && !(str.equals("")) && str.length() >= AppStrStatic.WORD_COMMENT_MIN_LIMIT) {
                        if (!judgeNoNullStr(str)) {
                            PubMehods.showToastStr(B_Mess_Notice_Detail.this, "请输入3个字以上的评论内容");
                            return;
                        }
                        if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
                            commentAcy(message_id, log_id, str);
                        } else {
                            PubMehods.showToastStr(B_Mess_Notice_Detail.this, "您的评论过于频繁！");
                        }

                    } else {
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, "请输入3个字以上的评论内容");
                    }
                } else {
                    PubMehods.showToastStr(B_Mess_Notice_Detail.this, R.string.str_no_certified_not_comment);
                }
            }
        });

        layout_huizhi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    sendSureReceReq(message_id);
                }
            }
        });

        readCache(message_id);

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    /*
     * 不能为空字符串
     */
    private boolean judgeNoNullStr(String str_Str) {
        String str = str_Str.replaceAll(" ", "");
        if (!"".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    private void readCache(String message_id) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_mess_detail
                + A_0_App.USER_UNIQID + message_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

    @SuppressLint("JavascriptInterface")
    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Cpk_Notice_Detail cpk_Notice_Detail = getNotice(jsonObject);
        loadData(cpk_Notice_Detail, -1);

    }

    private void loadData(Cpk_Notice_Detail notice, long time) {
        if (isFinishing())
            return;
        havaSuccessLoadData = true;
        if (notice.getContent().equals("") || notice.getContent() == null) {
            showLoadResult(false, false, true);
            PubMehods.showToastStr(B_Mess_Notice_Detail.this, "信息不存在！");
        } else {

            if (A_0_App.USER_STATUS.equals("2")) {
                showTitleBt(ZUI_RIGHT_BUTTON, true);
                setZuiRightBtn(R.drawable.navigationbar_more_share);
            } else {
                showTitleBt(ZUI_RIGHT_BUTTON, false);
            }
            if (time != -1) {
                have_read_page = 2;
                try {
                    severTime = time * 1000;
                } catch (Exception e) {
                    severTime = 0;
                }
            }
            list.clear();
            detail_Notice = notice;

            if (notice.getBg_img() != null && notice.getBg_img().length() > 0
                    && !notice.getBg_img().equals("")) {
                // iv_notice_detail_banner.setVisibility(View.VISIBLE);
                // imageLoaderBanner.displayImage(notice.getBg_img(),iv_notice_detail_banner,
                // optionsBanner);
                // bitmapUtilsBanner.display(iv_notice_detail_banner,
                // notice.getBg_img());
            } else {
                iv_notice_detail_banner.setVisibility(View.GONE);
            }

            log_id = notice.getLog_id();
            if (notice.getTitle() != null && !notice.getTitle().equals(""))
                try {
                    //附件
                    switch (notice.getIs_appendix()) {
                        case 0:
                            tv_message_detail_title.setText(notice.getTitle());
                            tv_notice_detail_comment_other.setVisibility(View.GONE);
                            break;
                        case 1:
                            VerticalImageSpan span = new VerticalImageSpan(B_Mess_Notice_Detail.this, R.drawable.file_zhizhen);
                            SpannableString spanStr = new SpannableString(notice.getTitle() + "    ");
                            spanStr.setSpan(span, spanStr.length() - 1, spanStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            tv_message_detail_title.setText(spanStr);

//                                            tv_message_detail_title.setText(notice.getTitle());
//                                            Drawable drawable = ContextCompat.getDrawable(B_Mess_Notice_Detail.this, R.drawable.file_zhizhen);
//
//                                            drawable.setBounds(0, 0, 30, 30);
//
//                                            tv_message_detail_title.setCompoundDrawables(null, null, drawable, null);

                            tv_message_detail_title.setCompoundDrawablePadding(25);
                            tv_notice_detail_comment_other.setVisibility(View.VISIBLE);
                            file_name = notice.getFile_name();
                            file_ext = notice.getFile_ext();
                            fileName.setText(notice.getFile_name());
                            String size = notice.getFile_size();
                            if (size != null) {
                                if (size.contains(".")) {
                                    size = size.substring(0, size.indexOf("."));
                                }
                                file_size = Long.parseLong(size);
                                fileDet.setText(FileSizeUtils.FormetFileSize(Long.parseLong(size)));
                            }
                            file_url = notice.getFile_url();
                            String substr = notice.getFile_ext().substring(notice.getFile_ext().length() - 3, notice.getFile_ext().length()).trim().toLowerCase();
                            switch (substr) {
                                case "doc":
                                    fileImage.setImageResource(R.drawable.file_word);
                                    break;
                                case "ocx":
                                    fileImage.setImageResource(R.drawable.file_word);
                                    break;
                                case "xls":
                                    fileImage.setImageResource(R.drawable.file_xl);
                                    break;
                                case "lsx":
                                    fileImage.setImageResource(R.drawable.file_xl);
                                    break;
                                case "ppt":
                                    fileImage.setImageResource(R.drawable.file_ppt);
                                    break;
                                case "ptx":
                                    fileImage.setImageResource(R.drawable.file_ppt);
                                    break;
                                case "txt":
                                    fileImage.setImageResource(R.drawable.file_txt);
                                    break;
                            }
                            File f = new File(Environment.getExternalStorageDirectory().getPath() + "//" + AppStrStatic.SD_PIC + "/" + file_name);
                            Log.i("aaa", "showInfo: " + f.exists());
                            if (f.exists()) {
                                fileProgressBar.setVisibility(View.VISIBLE);
                                fileProgressBar.setProgress(100);
                                DonDownload = true;
                            } else {
                                DonDownload = false;
                            }
                            break;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    tv_message_detail_title.setText("[表情]");
                }
            if (notice.getCreate_time() != null
                    && !notice.getCreate_time().equals("") &&
                    !notice.getCreate_time().equals("null")
                    && notice.getCreate_time().length() > 0) {
                tv_message_detail_notice_time.setText(PubMehods.getFormatDate(
                        Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
            }
            tv_message_detail_read_count.setText(notice.getRead_num());
            tv_message_detail_replay_count.setText(notice.getReply_num());
            if (notice.getContent_type().equals("1")) {
                webview_notice_detail.setVisibility(View.VISIBLE);
                tv_notice_detail.setVisibility(View.GONE);
                // webview_notice_detail.loadDataWithBaseURL("",notice.getContent(),
                // "text/html","UTF-8", null);
                /**
                 * 网络读取html
                 */
                PubMehods.saveHtml(notice.getContent(), "TEMP_HTML");
                String html_path = "file://"
                        + Environment.getExternalStorageDirectory()
                        + "/TEMP_HTML.html";
                webview_notice_detail.loadUrl(html_path);
                webview_notice_detail.addJavascriptInterface(
                        new JavascriptInterface(B_Mess_Notice_Detail.this),
                        "imagelistner");
                webview_notice_detail.setWebViewClient(new MyWebViewClient());
            } else if (notice.getContent_type().equals("0")) {
                webview_notice_detail.setVisibility(View.GONE);
                tv_notice_detail.setVisibility(View.VISIBLE);
                try {
                    tv_notice_detail.setText(notice.getContent());
                } catch (Exception e) {
                    // TODO: handle exception
                    tv_notice_detail.setText("[表情]");
                }
            }
            try {
                tv_mess_detial_from.setText(notice.getApp_msg_sign());
            } catch (Exception e) {
            }

//                            if (notice.getReply_num().equals("0")) {
//                                tv_notice_detail_comment_count.setVisibility(View.GONE);
//                            } else
//                            {
            tv_notice_detail_comment_count.setVisibility(View.VISIBLE);
            //}

            if (notice.getMessage_receipt() == 1 && notice.getIs_receipt() == 0) {
                layout_huizhi.setVisibility(View.VISIBLE);
                layout_pinglun.setVisibility(View.GONE);
            } else {
                layout_huizhi.setVisibility(View.GONE);
                layout_pinglun.setVisibility(View.VISIBLE);
            }

            list = notice.getList();
            if (list.size() == 0) {
                Cpk_Comment_detail all = new Cpk_Comment_detail();
                all.setUser_id("yuanding");
                list.add(all);
            }
            adapter.notifyDataSetChanged();
            // solveClashListView = new SolveClashListView();
            // solveClashListView.setListViewHeightBasedOnChildren(lv_notice_detail_comment,1);
            showLoadResult(false, true, false);
            pic_show();

        }
        if (lv_notice_detail_comment != null) {
            lv_notice_detail_comment.onRefreshComplete();
            lv_notice_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
        }
        demo_swiperefreshlayout.setRefreshing(false);
        repfresh = 0;
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        webview_notice_detail.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "objs[i].onclick=function()" +
                "{ " +
                "window.imagelistner.openImage(this.src);  " +
                "}" +
                "}" +
                "})()");
    }

    /**
     * 获取当前页面所有img地址为list
     *
     * @author Administrator
     */
    private void getWebViewAllImgSrc() {
        webview_notice_detail.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "var imgScr = '';" +
                "for(var i=0;i<objs.length;i++){" +
                "imgScr =imgScr +  '\\n'+ objs[i].src;" +
                " };" +
                "window.imagelistner.getAllImg(imgScr);  " +
                // "return imgScr;"+
                "})()");

    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;
        final ArrayList<String> image_List = new ArrayList<String>();
        int temp = 0;

        public JavascriptInterface(Context context) {
            this.context = context;

        }

        public void openImage(String img) {

            for (int i = 0; i < image_List.size(); i++) {
                if (image_List.get(i).equals(img)) {
                    temp = i;
                }
            }

            Intent intent = new Intent();
            intent.putStringArrayListExtra("path", image_List);
            intent.putExtra("num", temp);
            intent.setClass(context, B_Side_Found_BigImage.class);
            context.startActivity(intent);

        }

        public void getAllImg(String img) {
            image_List.clear();
            String[] temp = null;
            temp = img.split("\n");
            // 以换行符为url分隔符 第一个元素的null
            int counts = temp.length - 1;
            for (int i = 0; i < temp.length; i++) {
                // 以换行符为url分隔符 第一个元素的null
                if (i == 0) {
                    continue;
                }
                image_List.add(temp[i]);
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail.this);
        super.onResume();
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("//img")) {

                return true;
            } else {

                return super.shouldOverrideUrlLoading(view, url);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
            getWebViewAllImgSrc();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    private Cpk_Notice_Detail getNotice(JSONObject jsonObject) {
        // TODO Auto-generated method stub

        int state = jsonObject.optInt("status");
        Cpk_Notice_Detail notice = new Cpk_Notice_Detail();
        if (state == 1) {
            JSONObject dd = jsonObject.optJSONObject("info");
            notice = JSON.parseObject(dd + "", Cpk_Notice_Detail.class);
            List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
            mlistContact = JSON.parseArray(jsonObject.optJSONArray("comment") + "",
                    Cpk_Comment_detail.class);
            notice.setList(mlistContact);

        }
        return notice;

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void addHeadView(PullToRefreshListView listview) {
        ListView listView2 = listview.getRefreshableView();
        View view1 = LayoutInflater.from(B_Mess_Notice_Detail.this).inflate(
                R.layout.activity_mess_notice_head, null);

        iv_notice_detail_banner = (ImageView) view1.findViewById(R.id.iv_notice_detail_banner);
        tv_message_detail_title = (TextView) view1.findViewById(R.id.tv_message_detail_title);
        tv_message_detail_notice_time = (TextView) view1
                .findViewById(R.id.tv_message_detail_notice_time);
        tv_message_detail_replay_count = (TextView) view1
                .findViewById(R.id.tv_message_detail_replay_count);
        tv_message_detail_read_count = (TextView) view1
                .findViewById(R.id.tv_message_detail_read_count);
        tv_notice_detail_comment_count = (LinearLayout) view1
                .findViewById(R.id.tv_notice_detail_comment_count);

        liner_acy_detail = (LinearLayout) view1.findViewById(R.id.liner_acy_detail);

        webview_notice_detail = new WebView(getApplicationContext());
        webview_notice_detail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        liner_acy_detail.addView(webview_notice_detail);
        webview_notice_detail.setHorizontalScrollBarEnabled(false);// 水平不显示
        webview_notice_detail.setVerticalScrollBarEnabled(false); // 垂直不显示
        tv_notice_detail = (TextView) view1.findViewById(R.id.tv_notice_detail);
        tv_mess_detial_from = (TextView) view1.findViewById(R.id.tv_message_detail_notice_from);
        webview_notice_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webview_notice_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
        liner_notice_detail_container = (LinearLayout) view1
                .findViewById(R.id.liner_notice_detail_container);
        webview_notice_detail.getSettings().setJavaScriptEnabled(true);
        webview_notice_detail.getSettings().setDefaultTextEncodingName("utf-8"); // 设置文本编码
        int size = A_0_App.getInstance().setWebviewSize();
        webview_notice_detail.getSettings().setDefaultFontSize(size);
        webview_notice_detail.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                // view.loadUrl(url);
                return true;
            }
        });

        //附件
        tv_notice_detail_comment_other = (LinearLayout) view1.findViewById(R.id.tv_notice_detail_comment_other);
        mess_notice_detail_load_other = (RelativeLayout) view1.findViewById(R.id.mess_notice_detail_load_other);
        fileImage = (ImageView) view1.findViewById(R.id.fileImage);
        fileName = (TextView) view1.findViewById(R.id.fileName);
        fileDet = (TextView) view1.findViewById(R.id.fileDet);
        fileProgressBar = (ProgressBar) view1.findViewById(R.id.fileProgressBar);
        mess_notice_detail_load_other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //开始下载
                if (!DonDownload) {
                    if (file_size > 0) {
                        downloadFile(file_url, BASE_PATH + file_name);
                    } else {
                        showToastFileStr("空文件，无法下载");
                    }
                } else {
                    try {
                        String substr = file_ext.substring(file_ext.length() - 3, file_ext.length()).trim().toLowerCase();
                        switch (substr) {
                            case "doc":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
                                break;
                            case "ocx":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
                                break;
                            case "xls":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
                                break;
                            case "lsx":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
                                break;
                            case "ptx":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
                                break;
                            case "ppt":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
                                break;
                            case "txt":
                                B_Mess_Notice_Detail.this.startActivity(getFileIntent(BASE_PATH + file_name, "text/plain"));
                                break;
                        }
                    } catch (Exception e) {
                        showToastFileStr("打开文件失败或没有打开文件的应用");
                    }
                }

            }
        });
        // webview_notice_detail.getSettings().setUseWideViewPort(true);
        // webview_notice_detail.getSettings().setLoadWithOverviewMode(true);

        // webview_notice_detail.getSettings().setJavaScriptEnabled(true);
        // webview_notice_detail.getSettings().setAllowFileAccess(true);
        // webview_notice_detail.getSettings().setPluginState(PluginState.ON);

        // 第一种： 解决图文混排问题
        // settings.setUseWideViewPort(true);
        // settings.setLoadWithOverviewMode(true);
        // 第二种：
        // WebSetting settings = webView.getSettings();
        // settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // 把所有内容放在webview等宽的一列中。（可能会出现页面中链接失效）

        // webSettings.setJavaScriptEnabled(true);
        // webSettings.setAllowFileAccess(true);
        // webSettings.setPluginState(PluginState.ON);
        // webSettings.setUseWideViewPort(true);
        //
        // webView.loadUrl(loadStr);
        //
        // webSettings.setAppCacheEnabled(true);// 设置缓存
        // webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式
        // webSettings.setBuiltInZoomControls(true);// 显示缩放控件
        // webSettings.setSupportZoom(true); // 支持缩放
        // webSettings.setDefaultFontSize(16);
        listView2.addHeaderView(view1);
        listView2.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // comment_id="";
                tv_notice_detail_comment_text.setHint("评论一条");
                PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail.this);
                return false;
            }
        });
    }

    private void downloadFile(String url, final String path) {
        mess_notice_detail_load_other.setClickable(false);
        fileProgressBar.setVisibility(View.VISIBLE);
        A_0_App.getApi().download_Photo(url, path, new Api.Inter_DownLoad_Photo() {
            @Override
            public void onCancelled() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String message) {
                mess_notice_detail_load_other.setClickable(true);
                DonDownload = true;
                showToastFileStr("文件已保存至" + AppStrStatic.SD_PIC + "/文件夹");
            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {
                double process = 1.0 * arg1 / arg0;

                fileProgressBar.setProgress((int) (process * 100));

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Mess_Notice_Detail.this, msg);
                mess_notice_detail_load_other.setClickable(true);
                fileProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showLoadResult(boolean loading, boolean whole, boolean loadFaile) {
        if (whole)
            liner_notice_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_notice_detail_whole.setVisibility(View.GONE);

        if (loadFaile)
            mess_notice_detail_load_error.setVisibility(View.VISIBLE);
        else
            mess_notice_detail_load_error.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            mess_notice_detail_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            mess_notice_detail_loading.setVisibility(View.GONE);
        }
    }

    private void readData(String message_id) {
        A_0_App.getApi().getNoticeDetail(B_Mess_Notice_Detail.this, school_notice, A_0_App.USER_TOKEN, message_id,
                AppStrStatic.cache_key_mess_detail + A_0_App.USER_UNIQID, is_share,
                new InterNoticeDetail() {

                    @SuppressLint("JavascriptInterface")
                    @Override
                    public void onSuccess(Cpk_Notice_Detail notice, long time) {
                        loadData(notice, time);
                    }

                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, msg);
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true);
                        }
                        if (lv_notice_detail_comment != null) {
                            lv_notice_detail_comment.onRefreshComplete();
                            lv_notice_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
                        }
                        demo_swiperefreshlayout.setRefreshing(false);
                        repfresh = 0;
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });

    }

    private void commentAcy(final String message_id, String log_id, String content) {
        A_0_App.getInstance().showProgreDialog(B_Mess_Notice_Detail.this, "提交评论", true);
        A_0_App.getApi().add_Notice_Detail_Comment(message_id, log_id, A_0_App.USER_TOKEN, content,
                new InterAdd_Notice_Comment() {

                    @Override
                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, "评论成功");
                        tv_notice_detail_comment_text.getText().clear();
                        readData(message_id);
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    // 上拉刷新初始化数据
    private void getMoreComment(String message_id, int page_no) {
        logD(page_no + "通知条数问题");
        if (readCommentData)
            return;
        readCommentData = true;
        A_0_App.getApi().getNoticeCommentList(B_Mess_Notice_Detail.this, A_0_App.USER_TOKEN,
                message_id, String.valueOf(page_no), new InterNoticeCommentList() {

                    @Override
                    public void onSuccess(List<Cpk_Comment_detail> mList) {
                        if (isFinishing())
                            return;
                        // A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);
                        readCommentData = false;
                        if (mList != null && mList.size() > 0) {
                            have_read_page += 1;
                            int totleSize = list.size();
                            for (int i = 0; i < mList.size(); i++) {
                                list.add(mList.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            // solveClashListView = new SolveClashListView();
                            // solveClashListView.setListViewHeightBasedOnChildren(lv_notice_detail_comment,1);
                            // lv_notice_detail_comment.setSelection(totleSize +
                            // 1);
                        } else {
                            PubMehods.showToastStr(B_Mess_Notice_Detail.this, "没有更多了");
                        }
                        if (lv_notice_detail_comment != null) {
                            lv_notice_detail_comment.onRefreshComplete();
                        }
                        repfresh = 0;
                    }

                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        // A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);
                        readCommentData = false;
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, msg);
                        if (lv_notice_detail_comment != null) {
                            lv_notice_detail_comment.onRefreshComplete();
                        }
                        // new add
                        repfresh = 0;
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });

    }

    /**
     * 多张图片展示
     */
    private void pic_show() {
        if (detail_Notice.getPhoto_url() != null && !detail_Notice.getPhoto_url().equals("") &&
                !detail_Notice.getPhoto_url().equals("null")
                && detail_Notice.getPhoto_url().length() > 0) {
            final ArrayList<String> path = new ArrayList<String>();
            liner_notice_detail_container.setVisibility(View.VISIBLE);
            String notice_photos[] = detail_Notice.getPhoto_url().split(",");
            for (int i = 0; i < notice_photos.length; i++) {
                path.add(notice_photos[i]);
            }
            if (notice_photos.length == 1) {
                liner_notice_detail_container.removeAllViews();
                LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail.this);
                RelativeLayout.LayoutParams params;
                ImageView image1 = new ImageView(B_Mess_Notice_Detail.this);
                float density = PubMehods.getDensity(B_Mess_Notice_Detail.this);
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                float imageLayWidth = width - (20) * density;
                params = new RelativeLayout.LayoutParams((int) (imageLayWidth),
                        (int) (imageLayWidth));
                horizonLayout.addView(image1, params);
                image1.setScaleType(ScaleType.CENTER_CROP);
                PubMehods.loadBitmapUtilsPic(bitmapUtils, image1, notice_photos[0]);
                image1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(B_Mess_Notice_Detail.this,
                                B_Side_Found_BigImage.class);
                        intent.putStringArrayListExtra("path", path);
                        intent.putExtra("num", 0);
                        startActivity(intent);
                    }
                });
                liner_notice_detail_container.addView(horizonLayout);

            } else if (notice_photos.length == 2) {
                liner_notice_detail_container.removeAllViews();
                LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail.this);
                LinearLayout.LayoutParams params;
                float density = PubMehods.getDensity(B_Mess_Notice_Detail.this);
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                float imageLayWidth = width - (36) * density;
                for (int i = 0; i < notice_photos.length; i++) {
                    params = new LinearLayout.LayoutParams(
                            (int) (imageLayWidth / 2 - 10), (int) (imageLayWidth / 2 - 10));
                    params.setMargins(5, 5, 5, 5);
                    ImageView image2 = new ImageView(B_Mess_Notice_Detail.this);
                    image2.setLayoutParams(params);
                    final int a = i;
                    image2.setScaleType(ScaleType.CENTER_CROP);
                    horizonLayout.addView(image2);
                    PubMehods.loadBitmapUtilsPic(bitmapUtils, image2, notice_photos[i]);
                    image2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            Intent intent = new Intent(B_Mess_Notice_Detail.this,
                                    B_Side_Found_BigImage.class);
                            intent.putStringArrayListExtra("path", path);
                            intent.putExtra("num", a);
                            startActivity(intent);

                        }
                    });
                }
                liner_notice_detail_container.addView(horizonLayout);

            } else if (notice_photos.length > 2 && notice_photos.length <= 9) {

                liner_notice_detail_container.removeAllViews();
                LinearLayout.LayoutParams params;
                float density = PubMehods.getDensity(B_Mess_Notice_Detail.this);
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                float imageLayWidth = width - (38) * density;
                int size = notice_photos.length;
                int yuShu = size % 3;
                if (yuShu == 0) {
                    int hangNum = (int) (size / 3);
                    for (int i = 0; i < hangNum; i++) {
                        LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail.this);
                        for (int j = 0; j < 3; j++) {
                            params = new LinearLayout.LayoutParams(
                                    (int) (imageLayWidth / 3 - 10),
                                    (int) (imageLayWidth / 3 - 10));
                            params.setMargins(5, 5, 5, 5);
                            ImageView image3 = new ImageView(B_Mess_Notice_Detail.this);
                            image3.setLayoutParams(params);
                            image3.setScaleType(ScaleType.CENTER_CROP);
                            horizonLayout.addView(image3);
                            final int a = i * 3 + j;
                            // imageLoaderBanner.displayImage(notice_photos[a],image3,
                            // optionsBanner);
                            PubMehods.loadBitmapUtilsPic(bitmapUtils, image3, notice_photos[a]);
                            image3.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {

                                    Intent intent = new Intent(B_Mess_Notice_Detail.this,
                                            B_Side_Found_BigImage.class);
                                    intent.putStringArrayListExtra("path", path);
                                    intent.putExtra("num", a);
                                    startActivity(intent);

                                }
                            });
                        }
                        liner_notice_detail_container.addView(horizonLayout);
                    }
                } else {
                    int hangNum = (int) (size / 3) + 1;
                    for (int i = 0; i <= hangNum - 1; i++) {
                        LinearLayout horizonLayout = new LinearLayout(B_Mess_Notice_Detail.this);
                        if (i < hangNum - 1) {
                            for (int j = 0; j < 3; j++) {
                                params = new LinearLayout.LayoutParams(
                                        (int) (imageLayWidth / 3 - 10),
                                        (int) (imageLayWidth / 3 - 10));
                                params.setMargins(5, 5, 5, 5);
                                ImageView image3 = new ImageView(B_Mess_Notice_Detail.this);
                                image3.setLayoutParams(params);
                                image3.setScaleType(ScaleType.CENTER_CROP);
                                horizonLayout.addView(image3);
                                final int a = i * 3 + j;
                                // imageLoaderBanner.displayImage(notice_photos[a],image3,
                                // optionsBanner);
                                PubMehods.loadBitmapUtilsPic(bitmapUtils, image3, notice_photos[a]);
                                image3.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {

                                        Intent intent = new Intent(B_Mess_Notice_Detail.this,
                                                B_Side_Found_BigImage.class);
                                        intent.putStringArrayListExtra("path", path);
                                        intent.putExtra("num", a);
                                        startActivity(intent);

                                    }
                                });
                            }
                            liner_notice_detail_container.addView(horizonLayout);
                        } else if (i == hangNum - 1) {
                            for (int j = 0; j < yuShu; j++) {
                                params = new LinearLayout.LayoutParams(
                                        (int) (imageLayWidth / 3 - 10),
                                        (int) (imageLayWidth / 3 - 10));
                                params.setMargins(5, 5, 5, 5);
                                ImageView image3 = new ImageView(B_Mess_Notice_Detail.this);
                                image3.setLayoutParams(params);
                                image3.setScaleType(ScaleType.CENTER_CROP);
                                horizonLayout.addView(image3);
                                final int a = i * 3 + j;
                                // imageLoaderBanner.displayImage(notice_photos[a],image3,
                                // optionsBanner);
                                PubMehods.loadBitmapUtilsPic(bitmapUtils, image3, notice_photos[a]);
                                image3.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {

                                        Intent intent = new Intent(B_Mess_Notice_Detail.this,
                                                B_Side_Found_BigImage.class);
                                        intent.putStringArrayListExtra("path", path);
                                        intent.putExtra("num", a);
                                        startActivity(intent);

                                    }
                                });
                            }
                            liner_notice_detail_container.addView(horizonLayout);
                        }
                    }
                }

            }
        }
    }

    public class Mydapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list != null)
                return list.size();
            return 0;
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int posi, View converView, ViewGroup arg2) {

            final ViewHolder holder;
            if (converView == null) {
                converView = LayoutInflater.from(B_Mess_Notice_Detail.this).inflate(
                        R.layout.item_info_comment_more, null);
                holder = new ViewHolder();

                holder.iv_comment_user_por = (CircleImageView) converView
                        .findViewById(R.id.iv_comment_user_por);
                holder.tv_comment_user_name = (TextView) converView
                        .findViewById(R.id.tv_comment_user_name);
                holder.tv_comment_user_content = (TextView) converView
                        .findViewById(R.id.tv_comment_user_content);
                holder.tv_comment_user_time = (TextView) converView
                        .findViewById(R.id.tv_comment_user_time);
                holder.tv_detaili_info_surname_count = (TextView) converView
                        .findViewById(R.id.tv_detaili_info_surname_count);
                holder.liner_comment_more = (LinearLayout) converView
                        .findViewById(R.id.liner_comment_more);
                holder.liner_info = (LinearLayout) converView.findViewById(R.id.liner_info);
                holder.liner_detail_info_count = (LinearLayout) converView
                        .findViewById(R.id.liner_detail_info_count);
                holder.iv_detaili_info_surname_count = (ImageView) converView
                        .findViewById(R.id.iv_detaili_info_surname_count);
                holder.rela_itme_info = (RelativeLayout) converView
                        .findViewById(R.id.rela_itme_info);
                holder.rela_itme_info_down = (RelativeLayout) converView
                        .findViewById(R.id.rela_itme_info_down);
                holder.tv_one = (TextView) converView.findViewById(R.id.tv_one);
                holder.tv_comment_user_order = (TextView) converView
                        .findViewById(R.id.tv_comment_user_order);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }

            holder.iv_detaili_info_surname_count
                    .setBackgroundResource(R.drawable.icon_notice_detail_repaly);
            if (list.size() == 1 && list.get(posi).getUser_id().equals("yuanding")) {
                holder.rela_itme_info.setVisibility(View.GONE);

            } else {
                holder.rela_itme_info_down.setVisibility(View.VISIBLE);
                holder.rela_itme_info.setVisibility(View.VISIBLE);
                if (posi % 8 == 0) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_one);

                } else if (posi % 8 == 1) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_two);
                } else if (posi % 8 == 2) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_three);
                } else if (posi % 8 == 3) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_four);
                } else if (posi % 8 == 4) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_five);
                } else if (posi % 8 == 5) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_six);
                } else if (posi % 8 == 6) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_seven);
                } else if (posi % 8 == 7) {
                    holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_eight);
                }
                String uri = list.get(posi).getPhoto_url();
                if (holder.iv_comment_user_por.getTag() == null) {
                    PubMehods.loadServicePic(imageLoader, uri, holder.iv_comment_user_por, options);
                    holder.iv_comment_user_por.setTag(uri);
                } else {
                    if (!holder.iv_comment_user_por.getTag().equals(uri)) {
                        PubMehods.loadServicePic(imageLoader, uri, holder.iv_comment_user_por,
                                options);
                        holder.iv_comment_user_por.setTag(uri);
                    }
                }
                // bitmapUtils.display(holder.iv_comment_user_por,
                // list.get(posi).getPhoto_url());
                holder.liner_detail_info_count.setVisibility(View.GONE);
                holder.tv_comment_user_name.setText(list.get(posi).getName());
                holder.tv_comment_user_content.setText(list.get(posi).getContent());
                if (list.get(posi).getCreate_time() != null)
                    holder.tv_comment_user_time.setText(PubMehods.getTimeDifference(severTime,
                            Long.valueOf(list.get(posi).getCreate_time()) * 1000));
                if (list.get(posi).getReply_comment_list() != null
                        && !list.get(posi).getReply_comment_list().equals("")) {
                    holder.liner_comment_more.removeAllViews();
                    List<Cpk_Info_Detail_Content_More> comment_List_All = new ArrayList<Cpk_Info_Detail_Content_More>();
                    comment_List_All = JSON.parseArray(list.get(posi).getReply_comment_list(),
                            Cpk_Info_Detail_Content_More.class);
                    for (int i = 0; i < comment_List_All.size(); i++) {
                        View view = LayoutInflater.from(B_Mess_Notice_Detail.this).inflate(
                                R.layout.item_side_notice_detail_more, null);
                        TextView textView1 = (TextView) view.findViewById(R.id.tv_side_more_time);
                        TextView textView2 = (TextView) view
                                .findViewById(R.id.tv_side_more_content);
                        textView1.setText(PubMehods.getTimeDifference(severTime,
                                Long.valueOf(comment_List_All.get(i).getCreate_time()) * 1000));
                        textView2.setText(comment_List_All.get(i).getContent());
                        holder.liner_comment_more.addView(view);
                    }
                    // if (temp!=null&&!temp.equals("")) {
                    // holder.liner_info .setVisibility(View.VISIBLE);
                    // SpannableStringBuilder builder = new
                    // SpannableStringBuilder("发信者回复");
                    // ForegroundColorSpan greenSpan = new
                    // ForegroundColorSpan(getResources().getColor(R.color.info_green_name));
                    // builder.setSpan(greenSpan, 0, 5,
                    // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // holder.tv_comment_reply_name.setText(builder);
                    // holder.tv_comment_reply_content.setText("    "+temp);
                    // }else{
                    // holder.liner_info .setVisibility(View.GONE);
                    // }
                } else {
                    holder.liner_info.setVisibility(View.GONE);
                }
            }
            return converView;
        }

    }

    class ViewHolder {
        CircleImageView iv_comment_user_por;
        TextView tv_comment_user_name;
        TextView tv_comment_user_content;
        TextView tv_comment_user_time, tv_detaili_info_surname_count;
        LinearLayout liner_info, liner_detail_info_count;
        ImageView iv_detaili_info_surname_count;
        TextView tv_one;
        RelativeLayout rela_itme_info, rela_itme_info_down;
        TextView tv_comment_user_order;
        LinearLayout liner_comment_more;
    }

    private PopupWindow statusPopup;
    private LinearLayout mLinerCollct, mLinerForward;

    // private void showWindow(View parent) {
    // logE(A_0_App.getInstance().getShowViewHeight() +"aa" +A_0_App.screenHeigh
    // +"bb" +A_0_App.screenWidth);
    // if (statusPopup == null) {
    // LayoutInflater layoutInflater = (LayoutInflater)
    // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    // View view = layoutInflater.inflate(R.layout.item_lecture_detail, null);
    // mLinerCollct = (LinearLayout)
    // view.findViewById(R.id.liner_lecture_detail_collect);
    // mLinerForward = (LinearLayout)
    // view.findViewById(R.id.liner_lecture_detail_forward);
    // statusPopup = new PopupWindow(view,
    // LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    // }
    // statusPopup.setFocusable(true);
    // statusPopup.setOutsideTouchable(true);
    // statusPopup.setBackgroundDrawable(new BitmapDrawable());
    // int x=DensityUtils.dip2px(B_Mess_Notice_Detail.this, 165);
    // statusPopup.showAsDropDown(parent,-x,
    // A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下
    //
    // mLinerForward.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View arg0) {
    // if (statusPopup != null) {
    // statusPopup.dismiss();
    // }
    // Intent intent=new Intent(B_Mess_Notice_Detail.this,
    // B_Mess_Forward_Select.class);
    // intent.putExtra("title", detail_Notice.getTitle());
    // intent.putExtra("content", mess_content);
    // intent.putExtra("type", "1");
    // intent.putExtra("image", detail_Notice.getBg_img());
    // intent.putExtra("acy_type", acy_type+"");
    // intent.putExtra("noticeId", message_id);
    // startActivity(intent);
    // }
    // });
    //
    // mLinerCollct.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View arg0) {
    // if (statusPopup != null) {
    // statusPopup.dismiss();
    // }
    // PubMehods.showToastStr(B_Mess_Notice_Detail.this, "后续开放");
    // }
    // });
    // }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                goAcy();
                break;
            case ZUI_RIGHT_BUTTON:
                if (A_0_App.USER_STATUS.equals("2")) {
                    Intent intent = new Intent(B_Mess_Notice_Detail.this,
                            B_Mess_Forward_Select.class);
                    intent.putExtra("title", detail_Notice.getTitle());
                    if (mess_content == null || mess_content.equals(""))
                        intent.putExtra("content", detail_Notice.getTitle());
                    else
                        intent.putExtra("content", mess_content);
                    intent.putExtra("type", "1");
                    intent.putExtra("image", detail_Notice.getBg_img());
                    intent.putExtra("acy_type", acy_type + "");
                    intent.putExtra("noticeId", message_id);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {

        if (list != null) {
            list.clear();
            list = null;
        }
        liner_acy_detail.removeAllViews();
        webview_notice_detail.removeAllViews();
        webview_notice_detail.destroy();
        webview_notice_detail = null;
        adapter = null;
        detail_Notice = null;
        // bitmapUtils=null;
        // bitmapUtilsBanner=null;
        drawable.stop();
        drawable = null;
        System.gc();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void goData() {
        Intent it = new Intent();
        it.putExtra("read_count", detail_Notice.getRead_num());
        it.putExtra("repley_count", detail_Notice.getReply_num());
        it.putExtra("haveReceipted", haveReceipted);
        setResult(1, it);
    }

    private void goAcy() {
        if (acy_type == 3) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_Notice_Detail.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,
                    R.anim.animal_push_right_out_normal);
        }
        PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail.this);
    }

    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,
                R.anim.animal_push_left_out_normal);
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("B_Mess_Notice_Detail",
                "B_Mess_Notice_Detail==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("B_Mess_Notice_Detail",
                "B_Mess_Notice_Detail==>" + msg);
    }

    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy
                            .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    // A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail.this,
                                    AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }

    private void updateInfo() {
        MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
        updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
        private Context context;

        MyAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {

            readData(message_id);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            // logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    /**
     * 发送确认收到请求
     */
    private void sendSureReceReq(String msgid) {
        A_0_App.getInstance().showProgreDialog(B_Mess_Notice_Detail.this, "", true);

        A_0_App.getApi().sendNoticeSureReceIpt(B_Mess_Notice_Detail.this,
                A_0_App.USER_TOKEN, msgid,
                new SureReceIptCallBack() {
                    @Override
                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        haveReceipted = true;
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);
                        layout_huizhi.setVisibility(View.GONE);
                        layout_pinglun.setVisibility(View.VISIBLE);
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        //new add
                        PubMehods.showToastStr(B_Mess_Notice_Detail.this, msg);
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail.this);


                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });


    }
}

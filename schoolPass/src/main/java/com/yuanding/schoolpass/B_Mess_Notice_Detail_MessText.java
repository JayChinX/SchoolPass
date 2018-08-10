
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Comment_detail;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Content_More;
import com.yuanding.schoolpass.bean.Cpk_Notice_Detail;
import com.yuanding.schoolpass.service.Api.InterAdd_Notice_Comment;
import com.yuanding.schoolpass.service.Api.InterNoticeCommentList;
import com.yuanding.schoolpass.service.Api.InterNoticeDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili?
 * @version 创建时间：2015年11月23日 下午5:22:12 短信文本通知详情
 */
public class B_Mess_Notice_Detail_MessText extends A_0_CpkBaseTitle_Navi {

    private View mess_notice_detail_load_error, liner_notice_detail_whole,
            mess_notice_detail_loading;
    private LinearLayout liner_notice_detail_send;
    private ImageView iv_notice_detail_banner, iv_notice_detail_send;
    private TextView tv_message_detail_title, tv_message_detail_notice_time,
            tv_message_detail_replay_count, tv_message_detail_read_count;
    private LinearLayout tv_notice_detail_comment_count;

    private WebView webview_notice_detail;
    private PullToRefreshListView lv_notice_detail_comment;
    private EditText tv_notice_detail_comment_text;

    private Mydapter adapter;
    private String message_id, log_id, mess_content;
    private String is_share = "0";
    
    private List<Cpk_Comment_detail> list;
    private Cpk_Notice_Detail detail_Notice;

    protected ImageLoader imageLoader, imageLoaderBanner;
    private DisplayImageOptions options, optionsBanner;
    // private BitmapUtils bitmapUtils,bitmapUtilsBanner;
    // private SolveClashListView solveClashListView;

    private int acy_type;//页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
    private int have_read_page = 2;// 已经读的页数
    private boolean readCommentData, havaSuccessLoadData = false;
    private ACache maACache;
    private JSONObject jsonObject;
    private TextView tv_notice_detail_mess;
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private static long severTime = 0;
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
    private int repfresh = 0;// 避免下拉和上拉冲突
    private String school_notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail);

        setTitleText("通知详情");

        detail_Notice = new Cpk_Notice_Detail();
        acy_type = getIntent().getExtras().getInt("acy_type");
        //页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
        if (acy_type == 1) {
            message_id = getIntent().getExtras().getString("message_id");
            is_share = "1";
        } else if (acy_type == 2) {
            mess_content = getIntent().getExtras().getString("mess_content");
            message_id = getIntent().getExtras().getString("message_id");
            school_notice = getIntent().getExtras().getString("school_notice");
        } else if (acy_type == 3) {
            message_id =PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
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

        home_load_loading = (LinearLayout) mess_notice_detail_loading
                .findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        lv_notice_detail_comment = (PullToRefreshListView) findViewById(R.id.lv_notice_detail_comment);
        liner_notice_detail_send = (LinearLayout) findViewById(R.id.liner_notice_detail_send);
        iv_notice_detail_send = (ImageView) findViewById(R.id.iv_notice_detail_send);
        tv_notice_detail_comment_text = (EditText) findViewById(R.id.tv_notice_detail_comment_text);
        tv_notice_detail_mess = (TextView) findViewById(R.id.tv_message_detail_notice_from);
        imageLoaderBanner = A_0_App.getInstance().getimageLoader();
        optionsBanner = A_0_App.getInstance().getOptions(R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg);
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);
        // bitmapUtilsBanner=A_0_App.getBitmapUtils(this,
        // R.drawable.ic_default_acy_empty, R.drawable.ic_default_acy_empty);
        // bitmapUtils=A_0_App.getBitmapUtils(this,
        // R.drawable.ic_defalut_person_center,
        // R.drawable.ic_defalut_person_center);
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

                        };
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
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !"".equals(s.toString()))
                {
                    iv_notice_detail_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "已达到最大字数限制");
                        tv_notice_detail_comment_text.setText(tv_notice_detail_comment_text
                                .getText().toString()
                                .substring(0, AppStrStatic.WORD_COMMENT_MAX_LIMIT));
                        tv_notice_detail_comment_text
                                .setSelection(AppStrStatic.WORD_COMMENT_MAX_LIMIT);
                    } else {
                        // sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
                    }
                } else
                {
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

                if (str != null && !(str.equals(""))
                        && str.length() >= AppStrStatic.WORD_COMMENT_MIN_LIMIT) {
                    if (!judgeNoNullStr(str)) {
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "请输入3个字以上的评论内容");
                        return;
                    }
                    if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
                        commentAcy(message_id, log_id, str);
                    } else {
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "您的评论过于频繁！");
                    }

                } else {
                    PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "请输入3个字以上的评论内容");
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
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_detail_text
                + A_0_App.USER_UNIQID + message_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Cpk_Notice_Detail cpk_Notice_Detail = getNotice(jsonObject);
        loadData(cpk_Notice_Detail,-1);

    }
   private void loadData(Cpk_Notice_Detail notice,long time){
       if (isFinishing())
           return;
       havaSuccessLoadData = true;
       if (notice.getContent().equals("") || notice.getContent() == null) {
           showLoadResult(false, false, true);
           PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "信息不存在！");
       } else {
           if (A_0_App.USER_STATUS.equals("2")) {
               showTitleBt(ZUI_RIGHT_BUTTON, true);
               setZuiRightBtn(R.drawable.navigationbar_more_share);
           } else {
               showTitleBt(ZUI_RIGHT_BUTTON, false);
           }
           if (time!=-1){
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
               iv_notice_detail_banner.setVisibility(View.VISIBLE);
               String uri = notice.getBg_img();
               if (iv_notice_detail_banner.getTag() == null) {
                   PubMehods.loadServicePic(imageLoader, uri,
                           iv_notice_detail_banner, optionsBanner);
                   iv_notice_detail_banner.setTag(uri);
               } else {
                   if (!iv_notice_detail_banner.getTag().equals(uri)) {
                       PubMehods.loadServicePic(imageLoader, uri,
                               iv_notice_detail_banner, optionsBanner);
                       iv_notice_detail_banner.setTag(uri);
                   }
               }
               // bitmapUtilsBanner.display(iv_notice_detail_banner,
               // notice.getBg_img());
           } else {
               iv_notice_detail_banner.setVisibility(View.GONE);
           }

           log_id = notice.getLog_id();
           // tv_message_detail_title.setText(notice.getTitle());
           tv_message_detail_title.setText("手机短信");
           tv_message_detail_notice_time.setText(PubMehods.getFormatDate(
                   Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
           tv_message_detail_read_count.setText(notice.getRead_num());
           tv_message_detail_replay_count.setText(notice.getReply_num());
           webview_notice_detail.loadDataWithBaseURL(null, notice.getContent(),
                   "text/html", "UTF-8", null);
//                            if (notice.getReply_num().equals("0")) {
//                                tv_notice_detail_comment_count.setVisibility(View.GONE);
//                            } else
//                            {
           tv_notice_detail_comment_count.setVisibility(View.VISIBLE);
           //}
           try {
               tv_notice_detail_mess.setText(notice.getApp_msg_sign());
           } catch (Exception e) {

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
       }
       if (lv_notice_detail_comment != null) {
           lv_notice_detail_comment.onRefreshComplete();
           lv_notice_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
       }
       demo_swiperefreshlayout.setRefreshing(false);
       repfresh = 0;
   }
    private Cpk_Notice_Detail getNotice(JSONObject jsonObject) {
        // TODO Auto-generated method stub

        int state = jsonObject.optInt("status");
        Cpk_Notice_Detail notice = new Cpk_Notice_Detail();
        if (state == 1) {
            JSONObject dd = jsonObject.optJSONObject("info");
            notice.setBg_img(dd.optString("bg_img"));
            notice.setContent(dd.optString("content"));
            notice.setCreate_time(dd.optString("create_time"));
            notice.setMessage_id(dd.optString("message_id"));
            notice.setRead_num(dd.optString("read_num"));
            notice.setApp_msg_sign(dd.optString("app_msg_sign"));
            notice.setReply_num(dd.optString("reply_num"));
            notice.setTitle(dd.optString("title"));
            notice.setLog_id(dd.optString("log_id"));
            notice.setType(dd.optString("type"));
            notice.setDefault_type(dd.optString("default_type"));

            List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
            mlistContact = JSON.parseArray(jsonObject.optJSONArray("comment") + "",
                    Cpk_Comment_detail.class);
            notice.setList(mlistContact);

        }
        return notice;

    }

    private void addHeadView(PullToRefreshListView listview) {
        ListView listView2 = listview.getRefreshableView();
        View view1 = LayoutInflater.from(B_Mess_Notice_Detail_MessText.this).inflate(
                R.layout.activity_mess_notice_head_text, null);

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
        webview_notice_detail = (WebView) view1.findViewById(R.id.webview_notice_detail);
        webview_notice_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webview_notice_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
        int size = A_0_App.getInstance().setWebviewSize();
        webview_notice_detail.getSettings().setDefaultFontSize(size);
        webview_notice_detail.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
              // view.loadUrl(url);
                return true;
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
                PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail_MessText.this);
                return false;
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
        A_0_App.getApi().getNoticeDetail(B_Mess_Notice_Detail_MessText.this,school_notice, A_0_App.USER_TOKEN,
                message_id, AppStrStatic.cache_key_notice_detail_text + A_0_App.USER_UNIQID, is_share,
                new InterNoticeDetail() {

                    @Override
                    public void onSuccess(Cpk_Notice_Detail notice, long time) {
                        loadData(notice,time);
                    }
                },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        if (!havaSuccessLoadData)
                        {
                            PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, msg);
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
        A_0_App.getInstance().showProgreDialog(B_Mess_Notice_Detail_MessText.this,"提交评论", true);
        A_0_App.getApi().add_Notice_Detail_Comment(message_id, log_id, A_0_App.USER_TOKEN, content,
                new InterAdd_Notice_Comment() {

                    @Override
                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance()
                                .CancelProgreDialog(B_Mess_Notice_Detail_MessText.this);
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "评论成功");
                        tv_notice_detail_comment_text.getText().clear();
                        readData(message_id);

                    }
                },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance()
                                .CancelProgreDialog(B_Mess_Notice_Detail_MessText.this);
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, msg);
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
        A_0_App.getApi().getNoticeCommentList(B_Mess_Notice_Detail_MessText.this,
                A_0_App.USER_TOKEN, message_id, String.valueOf(page_no),
                new InterNoticeCommentList() {

                    @Override
                    public void onSuccess(List<Cpk_Comment_detail> mList) {
                        if (isFinishing())
                            return;
                        // A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail_MessText.this);
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
                            PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "没有更多了");
                        }
                        if (lv_notice_detail_comment != null) {
                            lv_notice_detail_comment.onRefreshComplete();
                        }
                        repfresh = 0;
                    }
                },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        // A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_Detail_MessText.this);
                        readCommentData = false;
                        PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, msg);
                        if (lv_notice_detail_comment != null) {
                            lv_notice_detail_comment.onRefreshComplete();
                        }
                        repfresh = 0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

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
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int posi, View converView, ViewGroup arg2) {

            final ViewHolder holder;
            if (converView == null) {
                converView = LayoutInflater.from(B_Mess_Notice_Detail_MessText.this).inflate(
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

            } else
            {
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
                if(list.get(posi).getCreate_time() != null)
                    holder.tv_comment_user_time.setText(PubMehods.getTimeDifference(severTime,
                            Long.valueOf(list.get(posi).getCreate_time()) * 1000));
                if (list.get(posi).getReply_comment_list() != null
                        && !list.get(posi).getReply_comment_list().equals("")) {
                    holder.liner_comment_more.removeAllViews();
                    List<Cpk_Info_Detail_Content_More> comment_List_All = new ArrayList<Cpk_Info_Detail_Content_More>();
                    comment_List_All = JSON.parseArray(list.get(posi).getReply_comment_list(),
                            Cpk_Info_Detail_Content_More.class);
                    for (int i = 0; i < comment_List_All.size(); i++) {
                        View view = LayoutInflater.from(B_Mess_Notice_Detail_MessText.this)
                                .inflate(R.layout.item_side_notice_detail_more, null);
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
    // int x=DensityUtils.dip2px(B_Mess_Notice_Detail_MessText.this,165);
    // statusPopup.showAsDropDown(parent, -x,
    // A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下
    //
    // mLinerForward.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View arg0) {
    // if (statusPopup != null) {
    // statusPopup.dismiss();
    // }
    // Intent intent=new Intent(B_Mess_Notice_Detail_MessText.this,
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
    // PubMehods.showToastStr(B_Mess_Notice_Detail_MessText.this, "后续开放");
    // }
    // });
    // }
    //

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                goAcy();
                break;
            case ZUI_RIGHT_BUTTON:
                if (A_0_App.USER_STATUS.equals("2")) {
                    Intent intent = new Intent(B_Mess_Notice_Detail_MessText.this,
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
                    // A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail_MessText.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(
                                    B_Mess_Notice_Detail_MessText.this,
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

    @Override
    protected void onResume() {
        PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail_MessText.this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (list != null) {
            list.clear();
            list = null;
        }
        adapter = null;
        detail_Notice = null;
        // bitmapUtils=null;
        // bitmapUtilsBanner=null;
        drawable.stop();
        drawable = null;
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
        setResult(1, it);
    }

    private void goAcy() {
        if (acy_type == 3) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_Notice_Detail_MessText.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,
                    R.anim.animal_push_right_out_normal);
        }
        PubMehods.closeKeybord(tv_notice_detail_comment_text, B_Mess_Notice_Detail_MessText.this);
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
        com.yuanding.schoolpass.utils.LogUtils.logD("B_Mess_Notice_Detail_MessText",
                "B_Mess_Notice_Detail_MessText==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("B_Mess_Notice_Detail_MessText",
                "B_Mess_Notice_Detail_MessText==>" + msg);
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
}

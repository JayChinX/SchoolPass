package com.yuanding.schoolpass;

import io.rong.imkit.MainActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Befriend_Bill_Bean;
import com.yuanding.schoolpass.bean.Befriend_Center_Bean;
import com.yuanding.schoolpass.service.Api;
import com.yuanding.schoolpass.service.Api.InterPersonalBillList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;


/**
 * 学生个人帮帮》》》》账户余额  列表
 */
public class B_Account_Befriend_Center_Main_1_Balance extends A_0_CpkBaseTitle_Navi {


    private View mLinerReadDataError, mLinerNoContent, liner_com_problem_list_whole_view, com_problem_loading;
    private PullToRefreshListView mPullDownView;
    private List<Befriend_Bill_Bean> mLecturesList;
    private Mydapter adapter;
    private int have_read_page = 1;// 已经读的页数
    private Boolean firstLoad = false;
    private ACache maACache;
    private JSONObject jsonObject;

    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
    private int repfresh = 0;//避免下拉和上拉冲突

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    private String money = "";
    private String bindStatus = "", week = "", withdrawAmountLow = "", max = "", now = "", account = "", accountName = "";
    private TextView tv_balance, tv_week;
    private String push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_help_center_balance);
        setTitleText("账户余额");
        showTitleBt(ZUI_RIGHT_TEXT, true);
        setZuiYouText("联系客服");
        push = getIntent().getStringExtra("push");
        if (push.equals("1")) {
            readData();
        } else {
            if (getIntent().getStringExtra("money") != null) {
                money = getIntent().getStringExtra("money");
                bindStatus = getIntent().getStringExtra("bindStatus");
                week = getIntent().getStringExtra("week");
                withdrawAmountLow = getIntent().getStringExtra("low");
                max = getIntent().getStringExtra("max");
                now = getIntent().getStringExtra("now");
                account = getIntent().getStringExtra("account");
                accountName = getIntent().getStringExtra("accountName");
            }
        }

        firstLoad = true;
        demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        liner_com_problem_list_whole_view = findViewById(R.id.liner_com_problem_list_whole_view);
        mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_com_problem_list);
        mLinerReadDataError = findViewById(R.id.com_problem_load_error);
        mLinerNoContent = findViewById(R.id.com_problem_no_content);
        com_problem_loading = findViewById(R.id.com_problem_loading);

        home_load_loading = (LinearLayout) com_problem_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_jiangzuo);
        tv_blank_name.setText("暂时没余额~");

        mLinerReadDataError.setOnClickListener(onClick);

        mLecturesList = new ArrayList<Befriend_Bill_Bean>();
        adapter = new Mydapter();
        mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);
        addHeadView(mPullDownView);
        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                have_read_page = 1;
                getBillList(have_read_page, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (repfresh == 0) {
                    repfresh = 1;
                    demo_swiperefreshlayout.setEnabled(false);
                    demo_swiperefreshlayout.setRefreshing(false);
                    getMoreBill(have_read_page);
                }
            }

        });
        /**
         * 新增下拉使用
         */
        demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
        demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
        if (repfresh == 0) {
            repfresh = 1;
            if (null != mPullDownView) {
                mPullDownView.onRefreshComplete();
            }
            demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    have_read_page = 1;
                    if (null != mPullDownView) {
                        mPullDownView.setMode(Mode.DISABLED);
                    }

                    getBillList(have_read_page, true);
                }

                ;
            });
        }


        mPullDownView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                if (demo_swiperefreshlayout != null && mPullDownView.getChildCount() > 0 && mPullDownView.getRefreshableView().getFirstVisiblePosition() == 0
                        && mPullDownView.getChildAt(0).getTop() >= mPullDownView.getPaddingTop()) {
                    //解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
                    demo_swiperefreshlayout.setEnabled(true);
                } else {
                    demo_swiperefreshlayout.setEnabled(false);
                }

            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //**************************新增到这**********************
        readCache();

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }


    private void readCache() {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_person_bill + A_0_App.USER_UNIQID);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

    private void addHeadView(PullToRefreshListView mListview) {

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        View view = getLayoutInflater().inflate(R.layout.item_help_top_balance, mListview, false);
        view.setLayoutParams(layoutParams);
        Button for_help_get_task = (Button) view.findViewById(R.id.for_help_get_task);
        tv_balance = (TextView) view.findViewById(R.id.tv_balance);
        tv_week = (TextView) view.findViewById(R.id.tv_week);
        ListView lv = mListview.getRefreshableView();
        lv.addHeaderView(view);
        if (money != null && !money.equals("")) {
            DecimalFormat df = new DecimalFormat("0.00");
            double temp = Double.parseDouble(money) / 100.00;
            tv_balance.setText(df.format(temp) + "");
            switch (Integer.valueOf(week)) {
                case 1:
                    tv_week.setText("(每周一可提现" + max + "次)");
                    break;
                case 2:
                    tv_week.setText("(每周二可提现" + max + "次)");
                    break;
                case 3:
                    tv_week.setText("(每周三可提现" + max + "次)");
                    break;
                case 4:
                    tv_week.setText("(每周四可提现" + max + "次)");
                    break;
                case 5:
                    tv_week.setText("(每周五可提现" + max + "次)");
                    break;
                case 6:
                    tv_week.setText("(每周六可提现" + max + "次)");
                    break;
                case 7:
                    tv_week.setText("(每周日可提现" + max + "次)");
                    break;
                default:
                    break;
            }
        }

        for_help_get_task.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (bindStatus != null && now != null && !money.equals("0") && money != "") {
                    if (Integer.valueOf(now) >= 1) {
                        Intent intent = new Intent(B_Account_Befriend_Center_Main_1_Balance.this, B_Account_Befriend_Center_Main_2_Bill_Present.class);
                        intent.putExtra("bindStatus", bindStatus);
                        intent.putExtra("money", money);
                        intent.putExtra("account", account);
                        intent.putExtra("accountName", accountName);
                        intent.putExtra("low", withdrawAmountLow);
                        startActivityForResult(intent, 1);
                    } else {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_Balance.this, "超过提现次数！");
                    }

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {

            if (requestCode == 1) {
                if (data.getExtras().getString("times") != null) {
                    money = data.getExtras().getString("money");
                    now = data.getExtras().getString("times");
                    DecimalFormat df = new DecimalFormat("0.00");
                    double temp = Double.parseDouble(money) / 100.00;
                    tv_balance.setText(df.format(temp) + "");
                }
            }
        }

    }

    private void showInfo(JSONObject jsonObject) {

        int state = jsonObject.optInt("status");
        List<Befriend_Bill_Bean> mlistContact = new ArrayList<Befriend_Bill_Bean>();
        if (state == 1) {
            mlistContact = JSON.parseArray(jsonObject.optJSONArray("list") + "", Befriend_Bill_Bean.class);
        }
        if (isFinishing())
            return;
        havaSuccessLoadData = true;
        if (mlistContact != null && mlistContact.size() > 0) {
            clearBusinessList();
            mLecturesList = mlistContact;
            adapter.notifyDataSetChanged();
            showLoadResult(false, true, false, false);
        } else {
            showLoadResult(false, false, false, true);
        }
        if (mPullDownView != null) {
            mPullDownView.onRefreshComplete();
            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        }

        demo_swiperefreshlayout.setRefreshing(false);
        repfresh = 0;

    }

    private void readData() {
        A_0_App.getApi().getBefriend_CenterList(B_Account_Befriend_Center_Main_1_Balance.this, A_0_App.USER_TOKEN, new Api.InterBefriend_CenterList() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onSuccess(Befriend_Center_Bean befriend_Center_Bean,
                                  long servertime) {
                if (isFinishing())
                    return;
                money = befriend_Center_Bean.getTotalAmount();
                bindStatus = befriend_Center_Bean.getBindStatus();
                week = befriend_Center_Bean.getWithdraw();
                withdrawAmountLow = befriend_Center_Bean.getWithdrawAmountLow();
                max = befriend_Center_Bean.getWithdrawMax();
                now = befriend_Center_Bean.getWithdrawTimes();
                account = befriend_Center_Bean.getAccount();
                accountName = befriend_Center_Bean.getAccountName();
                if (money != null && !money.equals("")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double temp = Double.parseDouble(money) / 100.00;
                    tv_balance.setText(df.format(temp) + "");
                    switch (Integer.valueOf(week)) {
                        case 1:
                            tv_week.setText("(每周一可提现" + max + "次)");
                            break;
                        case 2:
                            tv_week.setText("(每周二可提现" + max + "次)");
                            break;
                        case 3:
                            tv_week.setText("(每周三可提现" + max + "次)");
                            break;
                        case 4:
                            tv_week.setText("(每周四可提现" + max + "次)");
                            break;
                        case 5:
                            tv_week.setText("(每周五可提现" + max + "次)");
                            break;
                        case 6:
                            tv_week.setText("(每周六可提现" + max + "次)");
                            break;
                        case 7:
                            tv_week.setText("(每周日可提现" + max + "次)");
                            break;
                        default:
                            break;
                    }
                }
            }
        }, new Inter_Call_Back() {

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                //PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, msg);
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void getBillList(final int page_no, final boolean pullRefresh) {
        A_0_App.getApi().getPersonalBillList(B_Account_Befriend_Center_Main_1_Balance.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterPersonalBillList() {
            @Override
            public void onSuccess(List<Befriend_Bill_Bean> befriend_Bill_Beans) {
                if (isFinishing())
                    return;
                havaSuccessLoadData = true;
                if (befriend_Bill_Beans != null && befriend_Bill_Beans.size() > 0) {
                    have_read_page = 2;
                    clearBusinessList();
                    mLecturesList = befriend_Bill_Beans;
                    adapter.notifyDataSetChanged();
                    showLoadResult(false, true, false, false);
                    if (pullRefresh)
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_Balance.this, "刷新成功");
                } else {
                    showLoadResult(false, true, false, false);
                }

                if (null != mPullDownView) {
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                demo_swiperefreshlayout.setRefreshing(false);
                repfresh = 0;
            }
        }, new Inter_Call_Back() {

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                if (!havaSuccessLoadData) {
                    showLoadResult(false, false, true, false);
                }
                PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_Balance.this, msg);

                demo_swiperefreshlayout.setRefreshing(false);
                if (null != mPullDownView) {
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                repfresh = 0;
            }

            @Override
            public void onCancelled() {

            }
        });
    }


    // 上拉刷新初始化数据
    private void getMoreBill(int page_no) {
        if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
            return;
        A_0_App.getApi().getPersonalBillList(B_Account_Befriend_Center_Main_1_Balance.this, A_0_App.USER_TOKEN, String.valueOf(page_no), new InterPersonalBillList() {
            @Override
            public void onSuccess(List<Befriend_Bill_Bean> befriend_Bill_Beans) {
                if (isFinishing())
                    return;
                //A_0_App.getInstance().CancelProgreDialog(B_Side_Lectures_Acy.this);
                if (befriend_Bill_Beans != null && befriend_Bill_Beans.size() > 0) {
                    have_read_page += 1;
                    int totleSize = mLecturesList.size();
                    for (int i = 0; i < befriend_Bill_Beans.size(); i++) {
                        mLecturesList.add(befriend_Bill_Beans.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    //mPullDownView.getRefreshableView().setSelection(totleSize + 1);
                } else {
                    PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_Balance.this, "没有更多了");
                }
                if (null != mPullDownView) {
                    mPullDownView.onRefreshComplete();
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
                A_0_App.getInstance().CancelProgreDialog(B_Account_Befriend_Center_Main_1_Balance.this);
                PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_Balance.this, msg);
                if (null != mPullDownView) {
                    mPullDownView.onRefreshComplete();
                }

                //new add
                repfresh = 0;
            }

            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub

            }
        });

    }

    private void clearBusinessList() {
        if (mLecturesList != null && mLecturesList.size() > 0) {
            mLecturesList.clear();
        }
    }

    private void showLoadResult(boolean loading, boolean wholeView, boolean loadFaile, boolean noData) {
        if (wholeView)
            liner_com_problem_list_whole_view.setVisibility(View.VISIBLE);
        else
            liner_com_problem_list_whole_view.setVisibility(View.GONE);
        if (loadFaile)
            mLinerReadDataError.setVisibility(View.VISIBLE);
        else
            mLinerReadDataError.setVisibility(View.GONE);

        if (noData)
            mLinerNoContent.setVisibility(View.VISIBLE);
        else
            mLinerNoContent.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            com_problem_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            com_problem_loading.setVisibility(View.GONE);
        }
    }

    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_lecture_load_error:
                    showLoadResult(true, false, false, false);
                    clearBusinessList();
                    have_read_page = 1;
                    getBillList(have_read_page, true);
                    break;
                default:
                    break;
            }
        }
    };

    public class Mydapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mLecturesList != null)
                return mLecturesList.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(B_Account_Befriend_Center_Main_1_Balance.this).inflate(R.layout.item_help_blance_detail, null);
                holder.tv_balance_title = (TextView) converView.findViewById(R.id.tv_balance_title);
                holder.tv_balance_time = (TextView) converView.findViewById(R.id.tv_balance_time);
                holder.tv_balance_money = (TextView) converView.findViewById(R.id.tv_balance_money);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_balance_title.setText(mLecturesList.get(posi).getSummary());
            holder.tv_balance_time.setText(PubMehods.getFormatDate(Long.valueOf(mLecturesList.get(posi).getCreateTime()), "yyyy/MM/dd  HH:mm"));
            if (mLecturesList.get(posi).getTradeType() != null) {
                DecimalFormat df = new DecimalFormat("0.00");
                double temp = Double.parseDouble(mLecturesList.get(posi).getTotalAmount()) / 100.00;
                if (mLecturesList.get(posi).getTradeType().equals("0")) {

                    holder.tv_balance_money.setText("-" + df.format(temp));
                    holder.tv_balance_money.setTextColor(getResources().getColor(R.color.attence_leave));
                } else {
                    holder.tv_balance_money.setText("+" + df.format(temp));
                    holder.tv_balance_money.setTextColor(getResources().getColor(R.color.attence_later));
                }
            }

            if (A_0_App.isShowAnimation == true) {
                if (posi > A_0_App.lecture_curPosi) {
                    A_0_App.lecture_curPosi = posi;
                    Animation an = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 1,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    an.setDuration(400);
                    an.setStartOffset(50 * posi);
                    converView.startAnimation(an);
                }
            }
            return converView;
        }

    }

    class ViewHolder {
        TextView tv_balance_title, tv_balance_time, tv_balance_money;
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_TEXT:
                A_0_App.getInstance().callSb(B_Account_Befriend_Center_Main_1_Balance.this, "客服电话", "400-007-3298", new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Account_Befriend_Center_Main_1_Balance.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });
                    }
                });


                break;
            default:
                break;
        }
    }

    private static final int REQUECT_CODE_CAMERA = 2;
    private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
    private static final int REQUECT_CODE_CALLPHONE = 4;

    //    PermissionGen.needPermission(MainActivity.this, 100,
//            new String[] {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CALL_PHONE
//    });
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneSuccess() {
        PubMehods.callPhone(B_Account_Befriend_Center_Main_1_Balance.this, "400-007-3298");
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Account_Befriend_Center_Main_1_Balance.this);
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
            getBillList(have_read_page, false);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstLoad) {
            have_read_page = 1;
            getBillList(have_read_page, false);
        } else {
            firstLoad = false;
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
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Lectures_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_Befriend_Center_Main_1_Balance.this, AppStrStatic.kicked_offline());
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
    protected void onDestroy() {
        if (mLecturesList != null) {
            mLecturesList.clear();
            mPullDownView = null;
        }
        adapter = null;
        drawable.stop();
        drawable = null;
        super.onDestroy();
    }

}

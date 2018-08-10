package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out_Child;
import com.yuanding.schoolpass.service.Api.InterSchoolOutAll;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.contact.CustomEditText;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月5日 下午4:12:36
 *          校外本地搜索各项匹配数据
 */
public class B_Contact_Out_School_Search_Item extends A_0_CpkBaseTitle_Navi {

    private View mLinerReadDataError, mLinerNoContent, lv_out_school_search_loading,
            liner_acy_list_whole_view;
    private PullToRefreshListView mPullDownView;
    private Mydapter adapter;
    private List<Cpk_School_In_And_Out_Child> sortDataList;
    private CustomEditText CustomEditText_out_school_dd;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    // private BitmapUtils bitmapUtils;
    private int have_read_page = 1;// 已经读的页数

    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
    private int repfresh = 0;//避免下拉和上拉冲突

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_contact_out_school_auto);
        setTitleText("搜索");
        sortDataList = new ArrayList<Cpk_School_In_And_Out_Child>();
        demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        liner_acy_list_whole_view = findViewById(R.id.liner_whole_view_all);
        mLinerReadDataError = findViewById(R.id.lv_out_school_load_error_all);
        mLinerNoContent = findViewById(R.id.lv_out_school_no_content_all);
        lv_out_school_search_loading = findViewById(R.id.lv_out_school_search_loading);

        home_load_loading = (LinearLayout) lv_out_school_search_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        ImageView iv_blank_por = (ImageView) mLinerNoContent
                .findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView) mLinerNoContent
                .findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
        tv_blank_name.setText("没有搜索结果");

        mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_out_school_list_search_all);
        CustomEditText_out_school_dd = (CustomEditText) findViewById(R.id.customEditText_out_school_auto);

        imageLoader = A_0_App.getInstance().getimageLoader();

        adapter = new Mydapter();
        mPullDownView.setAdapter(adapter);


        CustomEditText_out_school_dd
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                            if (CustomEditText_out_school_dd.getText()
                                    .toString().length() > 0) {
                                have_read_page = 1;
                                getSchoolInData(CustomEditText_out_school_dd
                                                .getText().toString(), have_read_page,
                                        false);
                            }

                            return true;

                        }

                        return false;
                    }

                });
        mPullDownView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                getApplicationContext(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);
                        have_read_page = 1;
                        getSchoolInData(CustomEditText_out_school_dd.getText()
                                .toString(), have_read_page, true);
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                        if (repfresh == 0) {
                            repfresh = 1;
                            demo_swiperefreshlayout.setEnabled(false);
                            demo_swiperefreshlayout.setRefreshing(false);
                            getMoreData(CustomEditText_out_school_dd.getText().toString(), have_read_page);
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
            if (null != mPullDownView) {
                mPullDownView.onRefreshComplete();
            }
            demo_swiperefreshlayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        public void onRefresh() {

                            have_read_page = 1;
                            if (null != mPullDownView) {
                                mPullDownView.setMode(Mode.DISABLED);
                            }
                            getSchoolInData(CustomEditText_out_school_dd.getText().toString(), have_read_page, true);

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

        mPullDownView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
                                    long arg3) {
                if (posi == 0) {
                    return;
                }
                phoneName = sortDataList.get(posi - 1).getInfo_name();
                phone = sortDataList.get(posi - 1).getPhone();
                A_0_App.getInstance().callSb(B_Contact_Out_School_Search_Item.this, phoneName, phone, new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Contact_Out_School_Search_Item.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });

                    }
                });
            }
        });

        mLinerReadDataError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearData(false);
                have_read_page = 1;
                getSchoolInData(CustomEditText_out_school_dd.getText()
                        .toString(), have_read_page, true);
            }
        });

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    String phoneName;
    String phone;
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
        PubMehods.callPhone(B_Contact_Out_School_Search_Item.this, phone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Contact_Out_School_Search_Item.this);
    }

    public DisplayImageOptions getOption(int cate_id) {
        if (cate_id == 1) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_express_center,
                    R.drawable.school_list_express_center,
                    R.drawable.school_list_express_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Search_Item.this, R.drawable.school_list_express_center, R.drawable.school_list_express_center);

        } else if (cate_id == 2) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_takeout_center,
                    R.drawable.school_list_takeout_center,
                    R.drawable.school_list_takeout_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Search_Item.this, R.drawable.school_list_takeout_center, R.drawable.school_list_takeout_center);
        } else if (cate_id == 3) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_bank_center,
                    R.drawable.school_list_bank_center,
                    R.drawable.school_list_bank_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Search_Item.this, R.drawable.school_list_bank_center, R.drawable.school_list_bank_center);
        } else if (cate_id == 4) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_trip__center,
                    R.drawable.school_list_trip__center,
                    R.drawable.school_list_trip__center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Search_Item.this, R.drawable.school_list_trip__center, R.drawable.school_list_trip__center);
        } else if (cate_id == 5) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_entertainment_center,
                    R.drawable.school_list_entertainment_center,
                    R.drawable.school_list_entertainment_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Search_Item.this, R.drawable.school_list_entertainment_center, R.drawable.school_list_entertainment_center);
        }
        return options;
    }

    // 1,2,3
    private void getSchoolInData(String keyword, int page_no,
                                 final boolean pullRefresh) {
        A_0_App.getApi().getSchoolOutAll(B_Contact_Out_School_Search_Item.this,
                A_0_App.USER_TOKEN, keyword, String.valueOf(page_no),
                new InterSchoolOutAll() {
                    @Override
                    public void onSuccess(List<Cpk_School_In_And_Out_Child> mList) {
                        if (isFinishing())
                            return;
                        if (mList != null && mList.size() > 0) {
                            clearData(false);
                            have_read_page = 2;
                            sortDataList = mList;
                            // adapter.notifyDataSetChanged();
                            showLoadResult(false, true, false, false);
                            if (pullRefresh)
                                PubMehods.showToastStr(
                                        B_Contact_Out_School_Search_Item.this,
                                        "刷新成功");
                        } else {
                            showLoadResult(false, false, false, true);
                            PubMehods.showToastStr(
                                    B_Contact_Out_School_Search_Item.this,
                                    "没有信息");
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
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(B_Contact_Out_School_Search_Item.this, msg);
                        showLoadResult(false, false, true, false);

                        demo_swiperefreshlayout.setRefreshing(false);
                        if (null != mPullDownView) {
                            mPullDownView.onRefreshComplete();
                            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                        }
                        repfresh = 0;

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    // 上拉刷新初始化数据
    private void getMoreData(String keyword, int page_no) {
        A_0_App.getApi().getSchoolOutAll(B_Contact_Out_School_Search_Item.this,
                A_0_App.USER_TOKEN, keyword, String.valueOf(page_no),
                new InterSchoolOutAll() {
                    @Override
                    public void onSuccess(List<Cpk_School_In_And_Out_Child> mList) {
                        if (isFinishing())
                            return;
                        //A_0_App.getInstance().CancelProgreDialog(B_Contact_Out_School_Search_Item.this);
                        if (mList != null && mList.size() > 0) {
                            have_read_page += 1;
                            int totleSize = sortDataList.size();
                            for (int i = 0; i < mList.size(); i++) {
                                sortDataList.add(mList.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            //mPullDownView.getRefreshableView().setSelection(totleSize + 1);
                        } else {
                            PubMehods.showToastStr(
                                    B_Contact_Out_School_Search_Item.this,
                                    "没有更多了");
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
                        repfresh = 0;
                        A_0_App.getInstance().CancelProgreDialog(B_Contact_Out_School_Search_Item.this);
                        PubMehods.showToastStr(
                                B_Contact_Out_School_Search_Item.this, msg);
                        if (null != mPullDownView) {
                            mPullDownView.onRefreshComplete();
                        }

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void showLoadResult(boolean loading, boolean wholeView, boolean loadFaile,
                                boolean noData) {

        if (wholeView)
            liner_acy_list_whole_view.setVisibility(View.VISIBLE);
        else
            liner_acy_list_whole_view.setVisibility(View.GONE);

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
            lv_out_school_search_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            lv_out_school_search_loading.setVisibility(View.GONE);
        }
    }

    public class Mydapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (sortDataList != null)
                return sortDataList.size();
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
            if (converView == null) {
                converView = LayoutInflater.from(
                        B_Contact_Out_School_Search_Item.this).inflate(
                        R.layout.item_contace_out_school_search_result, null);
            }
            final TextView name = (TextView) converView
                    .findViewById(R.id.tv_out_school_result_name);
            CircleImageView por = (CircleImageView) converView
                    .findViewById(R.id.iv_out_school_result_pro);
            RelativeLayout rel = (RelativeLayout) converView
                    .findViewById(R.id.rel_out_school_search_auto);
            name.setText(sortDataList.get(posi).getInfo_name());
            if (posi % 8 == 0) {
                por.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                por.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                por.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                por.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                por.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                por.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                por.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                por.setBackgroundResource(R.drawable.photo_eight);
            }
            String uri = sortDataList.get(posi).getInfo_icon();
            if (por.getTag() == null) {
                PubMehods.loadServicePic(imageLoader, uri, por, getOption(Integer.parseInt(sortDataList.get(posi).getCate_id())));
                por.setTag(uri);
            } else {
                if (!por.getTag().equals(uri)) {
                    PubMehods.loadServicePic(imageLoader, uri, por, getOption(Integer.parseInt(sortDataList.get(posi).getCate_id())));
                    por.setTag(uri);
                }
            }
            //getOption(Integer.parseInt(sortDataList.get(posi).getCate_id())).display(por, sortDataList.get(posi).getInfo_icon());
            rel.setTag(posi);
            rel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneName = sortDataList.get((Integer) v.getTag()).getInfo_name();
                    phone = sortDataList.get((Integer) v.getTag()).getPhone();
                    A_0_App.getInstance().callSb(B_Contact_Out_School_Search_Item.this, phoneName, phone, new A_0_App.PhoneCallBack() {
                        @Override
                        public void sPermission() {
                            PermissionGen.needPermission(B_Contact_Out_School_Search_Item.this, REQUECT_CODE_CALLPHONE,
                                    new String[]{
                                            Manifest.permission.CALL_PHONE
                                    });
                        }
                    });
                }
            });
            if (A_0_App.isShowAnimation == true) {
                if (posi > A_0_App.out_search_curPosi) {
                    A_0_App.out_search_curPosi = posi;
                    Animation an = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    an.setDuration(400);
                    an.setStartOffset(50 * posi);
                    converView.startAnimation(an);
                }
            }
            return converView;
        }

    }

    private void clearData(boolean setNull) {
        if (sortDataList != null) {
            sortDataList.clear();
            if (setNull)
                sortDataList = null;
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
                    //A_0_App.getInstance().showExitDialog(B_Contact_Out_School_Search_Item.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Contact_Out_School_Search_Item.this, AppStrStatic.kicked_offline());
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
        clearData(true);
        //bitmapUtils=null;
        drawable.stop();
        drawable = null;
        super.onDestroy();
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;

            default:
                break;
        }

    }

}

package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out_Child;
import com.yuanding.schoolpass.service.Api.InterSchoolOutList;
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
 * @author Jiaohaili 
 * @version 创建时间：2015年11月21日 下午5:40:45
 *          校外点击宫格列表
 */
public class B_Contact_Out_School_Click_Item_List extends A_0_CpkBaseTitle_Navi {

    private View mLinerReadDataError, mLinerNoContent, liner_acy_list_whole_view, lv_stu_contact_loading;
    private PullToRefreshListView mPullDownView;
    private Mydapter adapter;
    private List<Cpk_School_In_And_Out_Child> list;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    //private BitmapUtils bitmapUtils;

    private int cate_id;
    private int have_read_page = 1;// 已经读的页数
    private ACache maACache;
    private JSONObject jsonObject;
    private boolean havaSuccessLoadData = false;
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
        setView(R.layout.activity_contact_out_school_search);
        demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        cate_id = getIntent().getExtras().getInt("cate_id");
        setTitleText(getIntent().getExtras().getString("cate_name"));
        list = new ArrayList<Cpk_School_In_And_Out_Child>();

        liner_acy_list_whole_view = findViewById(R.id.liner_whole_view_click);
        mLinerReadDataError = findViewById(R.id.lv_out_school_load_error_click);
        mLinerNoContent = findViewById(R.id.lv_out_school_no_content_click);
        lv_stu_contact_loading = findViewById(R.id.lv_stu_contact_loading);

        home_load_loading = (LinearLayout) lv_stu_contact_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
        tv_blank_name.setText("暂无信息~");
        mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_out_school_list_click);
        adapter = new Mydapter();
        mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);

        imageLoader = A_0_App.getInstance().getimageLoader();
        if (cate_id == 1) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_express_center,
                    R.drawable.school_list_express_center,
                    R.drawable.school_list_express_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_express_center, R.drawable.school_list_express_center);

        } else if (cate_id == 2) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_takeout_center,
                    R.drawable.school_list_takeout_center,
                    R.drawable.school_list_takeout_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_takeout_center, R.drawable.school_list_takeout_center);
        } else if (cate_id == 3) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_bank_center,
                    R.drawable.school_list_bank_center,
                    R.drawable.school_list_bank_center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_bank_center, R.drawable.school_list_bank_center);
        } else if (cate_id == 4) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_trip__center,
                    R.drawable.school_list_trip__center,
                    R.drawable.school_list_trip__center);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_trip__center, R.drawable.school_list_trip__center);
        } else if (cate_id == 5) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_entertainment_center,
                    R.drawable.school_list_entertainment_center,
                    R.drawable.school_list_entertainment_center);

            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_entertainment_center, R.drawable.school_list_entertainment_center);
        } else if (cate_id == 6) {
            options = A_0_App.getInstance().getOptions(
                    R.drawable.school_list_pub_img,
                    R.drawable.school_list_pub_img,
                    R.drawable.school_list_pub_img);
            //bitmapUtils=A_0_App.getBitmapUtils(B_Contact_Out_School_Click_Item_List.this, R.drawable.school_list_pub_img, R.drawable.school_list_pub_img);
        }


        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                have_read_page = 1;
                getSchoolInData(cate_id, have_read_page, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (repfresh == 0) {
                    repfresh = 1;
                    demo_swiperefreshlayout.setEnabled(false);
                    demo_swiperefreshlayout.setRefreshing(false);
                    getMoreData(cate_id, have_read_page);
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
            demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {

                    have_read_page = 1;
                    if (null != mPullDownView) {
                        mPullDownView.setMode(Mode.DISABLED);
                    }

                    getSchoolInData(cate_id, have_read_page, true);

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
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi, long arg3) {
                if (posi == 0) {
                    return;
                }
                name = list.get(posi - 1).getInfo_name();
                phone = list.get(posi - 1).getPhone();
                A_0_App.getInstance().callSb(B_Contact_Out_School_Click_Item_List.this, name, phone, new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Contact_Out_School_Click_Item_List.this, REQUECT_CODE_CALLPHONE,
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
                showLoadResult(true, false, false, false);
                getSchoolInData(cate_id, have_read_page, true);
            }
        });

        //getSchoolInData(cate_id,have_read_page,false);
        readCache(cate_id);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    String name;
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
        PubMehods.callPhone(B_Contact_Out_School_Click_Item_List.this, phone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Contact_Out_School_Click_Item_List.this);
    }

    private void readCache(int cate_id) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_out_school_click + A_0_App.USER_UNIQID + cate_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        int state = jsonObject.optInt("status");
        List<Cpk_School_In_And_Out_Child> mlist = new ArrayList<Cpk_School_In_And_Out_Child>();
        if (state == 1) {
            mlist = JSON.parseArray(jsonObject.optJSONArray("clist") + "", Cpk_School_In_And_Out_Child.class);
        }
        if (isFinishing())
            return;
        havaSuccessLoadData = true;
        if (mlist != null && mlist.size() > 0) {
            clearData(false);
            list = mlist;
            adapter.notifyDataSetChanged();
            showLoadResult(false, true, false, false);
        } else {
            showLoadResult(false, false, false, true);
        }
        demo_swiperefreshlayout.setRefreshing(false);
        if (null != mPullDownView) {
            mPullDownView.onRefreshComplete();
            mPullDownView.setMode(Mode.DISABLED);
        }
        repfresh = 0;
    }

    //1,2,3
    private void getSchoolInData(int cate_id, int page_no, final boolean pullRefresh) {
        A_0_App.getApi().getSchoolOutList(B_Contact_Out_School_Click_Item_List.this, A_0_App.USER_TOKEN,
                String.valueOf(cate_id), String.valueOf(page_no),
                new InterSchoolOutList() {
                    @Override
                    public void onSuccess(List<Cpk_School_In_And_Out_Child> mList) {
                        if (isFinishing())
                            return;
                        havaSuccessLoadData = true;
                        if (mList != null && mList.size() > 0) {
                            clearData(false);
                            have_read_page = 2;
                            list = mList;
                            adapter.notifyDataSetChanged();
                            showLoadResult(false, true, false, false);
                            if (pullRefresh)
                                PubMehods.showToastStr(B_Contact_Out_School_Click_Item_List.this, "刷新成功");
                        } else {
                            showLoadResult(false, false, false, true);
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
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true, false);
                        }
                        PubMehods.showToastStr(B_Contact_Out_School_Click_Item_List.this, msg);

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
    private void getMoreData(int cate_id, int page_no) {
        A_0_App.getApi().getSchoolOutList(B_Contact_Out_School_Click_Item_List.this, A_0_App.USER_TOKEN,
                String.valueOf(cate_id), String.valueOf(page_no),
                new InterSchoolOutList() {
                    @Override
                    public void onSuccess(List<Cpk_School_In_And_Out_Child> mList) {
                        if (isFinishing())
                            return;
                        //A_0_App.getInstance().CancelProgreDialog(B_Contact_Out_School_Click_Item_List.this);
                        if (mList != null && mList.size() > 0) {
                            have_read_page += 1;
                            int totleSize = list.size();
                            for (int i = 0; i < mList.size(); i++) {
                                list.add(mList.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            //mPullDownView.getRefreshableView().setSelection(totleSize + 1);
                        } else {
                            PubMehods.showToastStr(B_Contact_Out_School_Click_Item_List.this, "没有更多了");
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
                        A_0_App.getInstance().CancelProgreDialog(B_Contact_Out_School_Click_Item_List.this);
                        PubMehods.showToastStr(B_Contact_Out_School_Click_Item_List.this, msg);
                        if (null != mPullDownView) {
                            mPullDownView.onRefreshComplete();
                        }

                        repfresh = 0;
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }


    private void showLoadResult(boolean loading, boolean wholeView, boolean loadFaile, boolean noData) {

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
            lv_stu_contact_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            lv_stu_contact_loading.setVisibility(View.GONE);
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
                converView = LayoutInflater.from(B_Contact_Out_School_Click_Item_List.this).inflate(R.layout.item_contace_out_school_search_result, null);
            }
            TextView name = (TextView) converView.findViewById(R.id.tv_out_school_result_name);
            CircleImageView por = (CircleImageView) converView.findViewById(R.id.iv_out_school_result_pro);

            name.setText(list.get(posi).getInfo_name());

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
            String uri = list.get(posi).getInfo_icon();
            if (por.getTag() == null) {
                PubMehods.loadServicePic(imageLoader, uri, por, options);
                por.setTag(uri);
            } else {
                if (!por.getTag().equals(uri)) {
                    PubMehods.loadServicePic(imageLoader, uri, por, options);
                    por.setTag(uri);
                }
            }
            //bitmapUtils.display(por, list.get(posi).getInfo_icon());
            if (A_0_App.isShowAnimation == true) {
                if (posi > A_0_App.out_click_curPosi) {
                    A_0_App.out_click_curPosi = posi;
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
        if (list != null) {
            list.clear();
            if (setNull)
                list = null;
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

            getSchoolInData(cate_id, have_read_page, false);
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
                    //A_0_App.getInstance().showExitDialog(B_Contact_Out_School_Click_Item_List.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Contact_Out_School_Click_Item_List.this,AppStrStatic.kicked_offline());
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
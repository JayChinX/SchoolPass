package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out_Child;
import com.yuanding.schoolpass.service.Api.InterSchoolInOutList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月5日 下午4:25:39 类说明
 */
public class B_Contact_Main_In_School extends Fragment {

    private View mLinerReadDataError, mLinerNoContent, liner_class_contact1, viewone, contact_school_acy_loading;
    private PullToRefreshListView mExpand_Listview;
    private ExpandAdapter listAdapter;
    private List<Cpk_School_In_And_Out> schoolIn;
    private ACache maACache;
    private JSONObject jsonObject;
    String callname;
    String phone;
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
    private int repfresh = 0;//避免下拉和上拉冲突

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewone = inflater.inflate(R.layout.activity_contact_school_in, container, false);
        demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        schoolIn = new ArrayList<Cpk_School_In_And_Out>();
        liner_class_contact1 = viewone.findViewById(R.id.liner_class_contact2);
        mLinerReadDataError = viewone.findViewById(R.id.contact_acy_load_error_in);
        contact_school_acy_loading = viewone.findViewById(R.id.contact_school_acy_loading);
        mLinerNoContent = viewone.findViewById(R.id.contact_acy_no_content_in);

        home_load_loading = (LinearLayout) contact_school_acy_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        ImageView iv_blank_por = (ImageView) mLinerNoContent
                .findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView) mLinerNoContent
                .findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
        tv_blank_name.setText("暂无联系人~");

        // 校内
        mExpand_Listview = (PullToRefreshListView) viewone.findViewById(R.id.lv_inschcool);
        listAdapter = new ExpandAdapter();
        mExpand_Listview.setAdapter(listAdapter);

        mExpand_Listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                getSchoolInData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (repfresh == 0) {
                    repfresh = 1;
                    demo_swiperefreshlayout.setEnabled(false);
                    demo_swiperefreshlayout.setRefreshing(false);
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
            mExpand_Listview.onRefreshComplete();
            demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    mExpand_Listview.setMode(Mode.DISABLED);
                    getSchoolInData();

                }

                ;
            });
        }


        mExpand_Listview.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                if (demo_swiperefreshlayout != null && mExpand_Listview.getChildCount() > 0 && mExpand_Listview.getRefreshableView().getFirstVisiblePosition() == 0
                        && mExpand_Listview.getChildAt(0).getTop() >= mExpand_Listview.getPaddingTop()) {
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

        mLinerReadDataError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false, false);
                getSchoolInData();
            }
        });

        mLinerNoContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false, false);
                getSchoolInData();
            }
        });

        if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals("0")) {
            showLoadResult(false, false, false, true);
        } else {
            if (schoolIn != null && schoolIn.size() > 0) {
                listAdapter.notifyDataSetChanged();
                showLoadResult(false, true, false, false);
            } else {
                showLoadResult(true, false, false, false);
                readCache();
            }
        }

        return viewone;

    }

    private void readCache() {
        // TODO Auto-generated method stub
        maACache = ACache.get(getActivity());
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_school_in + A_0_App.USER_UNIQID);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }
    }

    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        List<Cpk_School_In_And_Out> list = getList(jsonObject);
        Success(list);
    }

    private List<Cpk_School_In_And_Out> getList(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        try {
            int state = jsonObject.optInt("status");
            List<Cpk_School_In_And_Out> mlist = new ArrayList<Cpk_School_In_And_Out>();
            if (state == 1) {
                JSONArray parent = jsonObject.optJSONArray("clist");
                if (parent != null && parent.length() > 0) {
                    for (int j = 0; j < parent.length(); j++) {
                        Cpk_School_In_And_Out school_in = new Cpk_School_In_And_Out();
                        school_in.setCategory_icon(parent.getJSONObject(j).optString("category_icon"));
                        school_in.setCategory_id(parent.getJSONObject(j).optString("category_id"));
                        school_in.setCategory_name(parent.getJSONObject(j).optString("category_name"));
                        school_in.setParent_id(parent.getJSONObject(j).optString("parent_id"));
                        JSONArray child = parent.getJSONObject(j).optJSONArray("info");
                        List<Cpk_School_In_And_Out_Child> chileList = new ArrayList<Cpk_School_In_And_Out_Child>();
                        chileList = JSON.parseArray(child + "", Cpk_School_In_And_Out_Child.class);
                        school_in.setList(chileList);
                        mlist.add(school_in);
                    }
                }
            }
            return mlist;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void Success(List<Cpk_School_In_And_Out> mList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        havaSuccessLoadData = true;
        if (mList != null && mList.size() > 0) {
            // 校内
            if (schoolIn != null) {
                schoolIn.clear();
            }
            schoolIn.addAll(mList);
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            showLoadResult(false, true, false, false);
        } else {
            showLoadResult(false, false, false, true);
        }
        demo_swiperefreshlayout.setRefreshing(false);
        if (null != mExpand_Listview) {
            mExpand_Listview.onRefreshComplete();
            mExpand_Listview.setMode(Mode.DISABLED);
        }
        repfresh = 0;
    }

    private void showLoadResult(boolean loading, boolean list, boolean loadFaile, boolean noData) {
        if (list)
            liner_class_contact1.setVisibility(View.VISIBLE);
        else
            liner_class_contact1.setVisibility(View.GONE);

        if (loadFaile)
            mLinerReadDataError.setVisibility(View.VISIBLE);
        else
            mLinerReadDataError.setVisibility(View.GONE);

        if (noData)
            mLinerNoContent.setVisibility(View.VISIBLE);
        else
            mLinerNoContent.setVisibility(View.GONE);
        if (loading) {
            if (drawable != null) {
                drawable.start();
            }
            contact_school_acy_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            contact_school_acy_loading.setVisibility(View.GONE);
        }
    }

    /****************************************
     * 校内
     ****************************************/

    // 通讯录信息类型 1：校内，2：校外
    private void getSchoolInData() {
        if (schoolIn == null)
            return;
        A_0_App.getApi().getSchoolInOutList(getActivity(),
                A_0_App.USER_TOKEN, "1", new InterSchoolInOutList() {
                    @Override
                    public void onSuccess(List<Cpk_School_In_And_Out> mList) {
                        if (schoolIn == null)
                            return;
                        Success(mList);

                        demo_swiperefreshlayout.setRefreshing(false);
                        if (null != mExpand_Listview) {
                            mExpand_Listview.onRefreshComplete();
                            mExpand_Listview.setMode(Mode.DISABLED);
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
                        if (schoolIn == null)
                            return;
                        if (getActivity() == null || getActivity().isFinishing())
                            return;
                        PubMehods.showToastStr(getActivity(), msg);
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true, false);
                        }

                        demo_swiperefreshlayout.setRefreshing(false);
                        if (null != mExpand_Listview) {
                            mExpand_Listview.onRefreshComplete();
                            mExpand_Listview.setMode(Mode.DISABLED);
                        }
                        repfresh = 0;
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    public class ExpandAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (schoolIn != null)
                return schoolIn.size();
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
        public View getView(final int arg0, View convertView, ViewGroup arg2) {
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(getActivity()).inflate(
                                R.layout.item_contact_group_inschool, null);
            }
            TextView name = (TextView) convertView
                    .findViewById(R.id.text_parent_cate_name);
            name.setText(schoolIn.get(arg0).getCategory_name());

            final MyGridView gridview = (MyGridView) convertView
                    .findViewById(R.id.navi_cate_chilid_gridview);
            MyGridAdapter ada = new MyGridAdapter(schoolIn.get(arg0).getList());
            gridview.setAdapter(ada);

            gridview.setTag(R.id.navi_cate_chilid_gridview, schoolIn.get(arg0)
                    .getList());
            return convertView;
        }
    }

    public class MyGridAdapter extends BaseAdapter {

        private List<Cpk_School_In_And_Out_Child> cate_list;

        public MyGridAdapter(List<Cpk_School_In_And_Out_Child> cate) {
            this.cate_list = cate;
        }

        @Override
        public int getCount() {
            if (cate_list == null) {
                return 0;
            }
            return cate_list.size();
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
        public View getView(final int position, View converView, ViewGroup arg2) {
            if (converView == null) {
                converView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_contact_child_inschool, null);
            }
            TextView textView = (TextView) converView
                    .findViewById(R.id.tv_child_cate_grid_item_name);
            textView.setText(cate_list.get(position).getInfo_name());
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    callname = cate_list.get(position).getInfo_name();
                    phone = cate_list.get(position).getPhone();
                    A_0_App.getInstance().callSb(getActivity(), callname, phone, new A_0_App.PhoneCallBack() {
                        @Override
                        public void sPermission() {
                            MPermissions.requestPermissions(B_Contact_Main_In_School.this, REQUECT_CODE_CALLPHONE,
                                    new String[]{
                                            Manifest.permission.CALL_PHONE
                                    });

                        }
                    });


                }
            });
            return converView;
        }
    }

    private static final int REQUECT_CODE_CAMERA = 2;
    private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
    private static final int REQUECT_CODE_CALLPHONE = 4;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUECT_CODE_CALLPHONE)
    public void requestSdcardSuccess()
    {
        PubMehods.callPhone(getContext(), phone);
    }

    @PermissionDenied(REQUECT_CODE_CALLPHONE)
    public void requestSdcardFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", getActivity());
    }

    private void updateInfo() {
        MyAsyncTask updateLectureInfo = new MyAsyncTask(getActivity());
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

            getSchoolInData();
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//	            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    @Override
    public void onDestroy() {
        if (schoolIn != null) {
            schoolIn.clear();
            schoolIn = null;
        }
        jsonObject = null;
        drawable.start();
        drawable = null;
        super.onDestroy();
    }
}

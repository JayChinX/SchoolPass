package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.yuanding.schoolpass.bean.Cpk_School_Assistant_Main;
import com.yuanding.schoolpass.service.Api.InterSchoolAssistantMainList;
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
 * @author 作者 E-mail:
 * @version 创建时间：2016年11月30日 下午5:13:24
 * 校务助手总页面
 */
public class B_Mess_School_Assistant_Main extends A_0_CpkBaseTitle_Navi{

    private View mLinerReadDataError,mLinerNoContent,liner_acy_list_whole_view,side_acy_loading;
    private PullToRefreshListView mPullDownView;
    private Mydapter adapter;
    private int have_read_page = 1;// 已经读的页数
    private List<Cpk_School_Assistant_Main> mSchoolMainList = null;
    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
    //private BitmapUtils bitmapUtils;
    private ACache maACache;
    private JSONObject jsonObject;
    private boolean havaSuccessLoadData = false;
     /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private String hint_title = "";
    private static long severTime = 0;
    private boolean firsetEnterLoader = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_school_main);
        
        setTitleText(R.string.str_school_assistant);
        
        firsetEnterLoader = true;
        hint_title = getResources().getString(R.string.str_no_message_chat);
        demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
        liner_acy_list_whole_view = findViewById(R.id.liner_school_list_whole_view);
        mLinerReadDataError = findViewById(R.id.mess_school_load_error);
        mLinerNoContent = findViewById(R.id.mess_school_no_content);
        side_acy_loading = findViewById(R.id.mess_acy_loading);
        
        home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start(); 
        
        ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_huodong);
        tv_blank_name.setText("无~");
        
        mLinerReadDataError.setOnClickListener(onClick);
        
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.ic_default_banner_empty_bg, R.drawable.ic_default_banner_empty_bg,
                R.drawable.ic_default_banner_empty_bg);
        //bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.ic_default_acy_empty, R.drawable.ic_default_acy_empty);
        mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_mess_school_list);
        mSchoolMainList = new ArrayList<Cpk_School_Assistant_Main>();
        adapter = new Mydapter();
        mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);
        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),DateUtils.FORMAT_SHOW_TIME| DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                        have_read_page = 1;
                        startReadData(have_read_page, true);
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        if (repfresh==0) {
                            repfresh=1;
                            demo_swiperefreshlayout.setEnabled(false);
                            demo_swiperefreshlayout.setRefreshing(false);  
                            getMoreLecture(have_read_page);
                        }
                    }
                });
        /**
           * 新增下拉使用
           */
                demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
                demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
                if (repfresh==0) {
                    repfresh=1;
                    if(null!=mPullDownView){
                        mPullDownView.onRefreshComplete();
                    }
                    demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
                    {
                        public void onRefresh() {                                                           
//                              select.setText("0");
                                have_read_page = 1;
                                if(null!=mPullDownView){
                                    mPullDownView.setMode(Mode.DISABLED);
                                }
                                
                                startReadData(have_read_page, true);
                                                                                    
                        };});
                }
                
                
              mPullDownView.setOnScrollListener(new OnScrollListener() {
                    
                    @Override
                    public void onScrollStateChanged(AbsListView arg0, int arg1) {
                         if (demo_swiperefreshlayout!=null&&mPullDownView.getChildCount() > 0 && mPullDownView.getRefreshableView().getFirstVisiblePosition() == 0
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
        // 点击Item触发的事件
        mPullDownView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
                if (posi == 0) {
                    return;
                }
                String count = mSchoolMainList.get(posi - 1).getUnread_num();
                if (count == null || count.length() <= 0)
                    count = "0";
                Intent intent = new Intent(B_Mess_School_Assistant_Main.this,B_Mess_School_Assistant_0_List_Acy.class);
                intent.putExtra("type",mSchoolMainList.get(posi-1).getType());
                intent.putExtra("title_name",mSchoolMainList.get(posi-1).getName());
                intent.putExtra("count",Integer.parseInt(count));
                intent.putExtra("acy_list_type",mSchoolMainList.get(posi-1).getType());
                intent.putExtra("img_course",mSchoolMainList.get(posi-1).getIcon_url());
                startActivity(intent);
            }
        });
        
        readCache();
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }
    
    private void readCache() {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_mess_school_assistant_main+A_0_App.USER_UNIQID);
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            List<Cpk_School_Assistant_Main> mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"", Cpk_School_Assistant_Main.class);
            showInfo(mlistContact,jsonObject.optLong("time"));
        }else{
            updateInfo();
        }
    }

    private void showInfo(List<Cpk_School_Assistant_Main> mList,long serverTime) {
        // TODO Auto-generated method stub
        try {
            if (isFinishing())
                return;
            try {
                severTime=serverTime*1000;
            } catch (Exception e) {
                severTime=0;
            }
            havaSuccessLoadData = true;
            if (mList != null && mList.size() > 0) {
                clearBusinessList(false);
                have_read_page = 2;
                mSchoolMainList = mList;
                adapter.notifyDataSetChanged();
                showLoadResult(false,true, false, false);
            } else {
                showLoadResult(false,false, false, true);
                PubMehods.showToastStr(B_Mess_School_Assistant_Main.this,"没有活动");
            }
            
            if(null!=mPullDownView){
                mPullDownView.onRefreshComplete();
                mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
            }               
            demo_swiperefreshlayout.setRefreshing(false);  
            repfresh=0;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
        
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
        if(loading){
            drawable.start();
            side_acy_loading.setVisibility(View.VISIBLE);
        }else{
            if (drawable!=null) {
                drawable.stop();
            }
            side_acy_loading.setVisibility(View.GONE);
    }}
    
    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.side_acy_load_error:
                showLoadResult(true,false, false, false);
                have_read_page = 1;
                startReadData(have_read_page,false);
                break;
            default:
                break;
            }
        }
    };
    
    
    // 读取校务列表信息
    private void startReadData(final int page_no, final boolean pullRefresh) {
        A_0_App.getApi().getSchoolAssistantMain(B_Mess_School_Assistant_Main.this,A_0_App.USER_TOKEN,String.valueOf(page_no), new InterSchoolAssistantMainList() {
            @Override
            public void onSuccess(List<Cpk_School_Assistant_Main> mList,long serverTime) {
                if (isFinishing())
                    return;
                showInfo(mList,serverTime);
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
                if(!havaSuccessLoadData){
                    showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Mess_School_Assistant_Main.this, msg);
                
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                }
                demo_swiperefreshlayout.setRefreshing(false);  
                repfresh=0;
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    

    // 上拉刷新初始化数据
    private void getMoreLecture(int page_no) {
        if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
            return;
        A_0_App.getApi().getSchoolAssistantMain(B_Mess_School_Assistant_Main.this,A_0_App.USER_TOKEN,String.valueOf(page_no), new InterSchoolAssistantMainList() {
            @Override
            public void onSuccess(List<Cpk_School_Assistant_Main> mList,long serverTime) {
                if (isFinishing())
                    return;
                try {
                    severTime=serverTime*1000;
                } catch (Exception e) {
                    severTime=0;
                }
                if (mList != null && mList.size() > 0) {
                    have_read_page += 1;
                    int totleSize = mSchoolMainList.size();
                    for (int i = 0; i < mList.size(); i++) {
                        mSchoolMainList.add(mList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    PubMehods.showToastStr(B_Mess_School_Assistant_Main.this,"没有更多了");
                }
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                }
                
                repfresh=0;
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
                A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_Main.this);
                PubMehods.showToastStr(B_Mess_School_Assistant_Main.this, msg);
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                }
                
                repfresh=0;
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
            if (mSchoolMainList != null)
                return mSchoolMainList.size();
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
            
            ViewHolder holder;
            if (converView == null) {
                converView = LayoutInflater.from(B_Mess_School_Assistant_Main.this).inflate(R.layout.item_message, null);
                holder = new ViewHolder();
                
                holder.iv_message_type_pic = (CircleImageView)converView.findViewById(R.id.iv_index_message_type_pic);
                holder.tv_message_auther = (TextView)converView.findViewById(R.id.tv_index_message_auther);
                holder.tv_message_time = (TextView)converView.findViewById(R.id.tv_index_message_time);
                holder.tv_message_content = (TextView)converView.findViewById(R.id.tv_index_message_content);
                holder.tv_index_message_from = (TextView)converView.findViewById(R.id.tv_index_message_from);
                holder.tv_no_read_count = (TextView)converView.findViewById(R.id.tv_no_read_count);
                holder.iv_message_block = (ImageView)converView.findViewById(R.id.iv_message_block);
                
                converView.setTag(holder);
            }else{
                holder = (ViewHolder) converView.getTag();
            }
            
            //设置头像
            String img_url = mSchoolMainList.get(posi).getIcon_url();
            if(img_url != null && img_url.length()>0 && !img_url.equals("")){
                holder.iv_message_type_pic.setVisibility(View.VISIBLE);
                if(holder.iv_message_type_pic.getTag() == null){
                    PubMehods.loadServicePic(imageLoader,img_url,holder.iv_message_type_pic, options);
                    holder.iv_message_type_pic.setTag(img_url);
                }else{
                    if(!holder.iv_message_type_pic.getTag().equals(img_url)){
                        PubMehods.loadServicePic(imageLoader,img_url,holder.iv_message_type_pic, options);
                        holder.iv_message_type_pic.setTag(img_url);
                    }
                }
            }else{
                holder.iv_message_type_pic.setVisibility(View.GONE);
            }
            
            //未读数量
            if (mSchoolMainList != null && mSchoolMainList.size() > 0 && mSchoolMainList.get(posi).getUnread_num() != null 
                    && !mSchoolMainList.get(posi).getUnread_num().equals("")) {
                int no_count = Integer.parseInt(mSchoolMainList.get(posi).getUnread_num());
                if (no_count == 0) {
                    holder.tv_no_read_count.setVisibility(View.GONE);
                } else if (0 < no_count && no_count <= 99) {
                    holder.tv_no_read_count.setVisibility(View.VISIBLE);
                    holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_message_noread);
                    holder.tv_no_read_count.setText(no_count + "");
                }else if(99<no_count&&no_count<=999){
                    holder.tv_no_read_count.setVisibility(View.VISIBLE);
                    holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
                    holder.tv_no_read_count.setText(no_count+"");
                }else if(no_count>999)
                {
                    holder.tv_no_read_count.setVisibility(View.VISIBLE);
                    holder.tv_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
                    holder.tv_no_read_count.setText("999+");
                }
            }else{
                holder.tv_no_read_count.setVisibility(View.GONE);
            }
            
            //消息名字
            if(mSchoolMainList != null && mSchoolMainList.size() > 0 && mSchoolMainList.get(posi).getName() != null 
                    && !mSchoolMainList.get(posi).getName().equals(""))
                 holder.tv_message_auther.setText(mSchoolMainList.get(posi).getName());
            else
                 holder.tv_message_auther.setText("");
            
            //消息内容
            String content = mSchoolMainList.get(posi).getContent();
            if (null != content && content.length() > 0) {
                SpannableStringBuilder builder = PubMehods.splitStrWhereStr(B_Mess_School_Assistant_Main.this, R.color.main_color, content, "\\{#", "#\\}");
                if(builder != null){
                    holder.tv_message_content.setText(builder); 
                }
            }else{
                holder.tv_message_content.setText(hint_title);
            }
            
            //消息时间
            if (mSchoolMainList != null && mSchoolMainList.size() > 0) {
                String time = mSchoolMainList.get(posi).getCreate_time();
                if (time != null && !time.equals("")) {
                    if(content == null || content.equals("")|| content.equals(hint_title)){
                        holder.tv_message_time.setText("");
                    }else{
                        holder.tv_message_time.setText(PubMehods.getTimeHint(Long.valueOf(time),severTime));
                    }
                } else {
                    holder.tv_message_time.setText("");
                }
            }
            
            return converView;
        }

    }
    
    class ViewHolder {
        TextView tv_no_read_count;
        CircleImageView iv_message_type_pic;
        TextView tv_message_auther;
        TextView tv_message_time;
        TextView tv_message_content;
        TextView tv_index_message_from;
        ImageView iv_message_block;
    }

    private void clearBusinessList(boolean setNull) {
        if (mSchoolMainList != null && mSchoolMainList.size() > 0) {
            mSchoolMainList.clear();
            if (setNull)
                mSchoolMainList = null;
        }
    }           
    
    private void updateInfo(){
        MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
        updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
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
            
            startReadData(have_read_page, false);
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
        if (firsetEnterLoader) {
            firsetEnterLoader = false;
        } else {
            have_read_page = 1;
            updateInfo();
        }
        super.onResume();
    }
    
    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Mess_School_Assistant_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_School_Assistant_Main.this,AppStrStatic.kicked_offline());
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
        clearBusinessList(true);
        adapter= null;
        //bitmapUtils=null;
        drawable.stop();
        drawable=null;
        super.onDestroy();
    }
    
    
    @Override
    protected void handleTitleBarEvent(int resId,View v) {
        switch (resId) {
        case BACK_BUTTON:
            finish();
            break;

        default:
            break;
        }
        
    }
}

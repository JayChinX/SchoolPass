
package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.B_Side_Acy_list_Detail_Acy.ViewHolder;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Acy_list;
import com.yuanding.schoolpass.bean.Cpk_Group_Info;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.bean.Cpk_Register_Select_Friend_Acy;
import com.yuanding.schoolpass.service.Api.InterGetFriendList;
import com.yuanding.schoolpass.service.Api.InterGroupInfo;
import com.yuanding.schoolpass.service.Api.InterVerifyFriendList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.MyGridView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月11日 上午9:12:53
 *  忘记密码选择好友
 */
public class A_3_1_2_Forget_Friend_Acy extends A_0_CpkBaseTitle_Navi {
    private View mLinerReadDataError,sv_select_friend,side_acy_loading,acy_forget_friend_error_count;
	private MyGridView gv_forget_friend;
	private MyAdapter adapter;

    private List<Cpk_Register_Select_Friend_Acy> mList = null;

    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
	    
	private Button btn_next_step;
	private String user_phone;
	private int type = 0;
	        
	  private LinearLayout home_load_loading;
	  private AnimationDrawable drawable;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
        if (A_0_App.getInstance().isRuning()) {
            finish();
            return;
        }
    	A_0_App.getInstance().addActivity(this);
        A_0_App.getInstance().addRegisterPwdAcy(this);
    	setView(R.layout.activity_login_forget_friend);
    	
    	setTitleText("好友验证");
        user_phone = getIntent().getExtras().getString("user_phone");
        type = getIntent().getExtras().getInt("acy_type");
        
        sv_select_friend = findViewById(R.id.sv_select_friend);
        acy_forget_friend_error_count = findViewById(R.id.acy_forget_friend_error_count);
        mLinerReadDataError = findViewById(R.id.acy_forget_friend_load_error);
        side_acy_loading=findViewById(R.id.acy_forget_friend_loading);
        
        home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        btn_next_step=(Button) findViewById(R.id.btn_next_step);
        gv_forget_friend=(MyGridView) findViewById(R.id.gv_forget_friend);
        
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center);
        mLinerReadDataError.setOnClickListener(onClick);
        
        mList = new ArrayList<Cpk_Register_Select_Friend_Acy>();
    	btn_next_step.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                if (adapter == null)
                    return;
                String strParams = adapter.getCateIDString();
                if (strParams == null || "".equals(strParams))
                    return;
			    String [] strArrays = strParams .split(",");
			    
                if (strArrays.length != 2) {
                    PubMehods.showToastStr(A_3_1_2_Forget_Friend_Acy.this,"请选出两位你的好友");
                    return;
                }
			    verifyFriendList(user_phone,strParams);
			}
		});
    	
        gv_forget_friend.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi, long arg3) {
                if (mList == null || posi < 0 || posi >= mList.size())
                    return;
                if (mList.get(posi) == null)
                    return;
                if(adapter != null)
                    adapter.chiceState(posi);
            }
        });
        
        getFriendList(user_phone);
    }
    
    private void getFriendList(String mobile) {
        A_0_App.getApi().getFriendList(A_3_1_2_Forget_Friend_Acy.this, mobile, new InterGetFriendList() {
            
            @Override
            public void onSuccess(List<Cpk_Register_Select_Friend_Acy> mFriendList,int state,String message) {
                if(isFinishing())
                    return;
                if (state == 1) {
                    clearData(false);
                    mList = mFriendList;
                    adapter = new MyAdapter(mList.size());
                    gv_forget_friend.setAdapter(adapter);
                    showLoadResult(false, true, false,false);
                }else if (state == 2) {
                    PubMehods.showToastStr(A_3_1_2_Forget_Friend_Acy.this, message);
                    showLoadResult(false,false,false,true);
                    setTitleText("锁定");
                }
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if(isFinishing())
                    return;
                PubMehods.showToastStr(A_3_1_2_Forget_Friend_Acy.this, msg);
                showLoadResult(false,false, true,false);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

    }
    
    // 验证好友
    private void verifyFriendList(String mobile, String uid) {
 A_0_App.getInstance().showProgreDialog(A_3_1_2_Forget_Friend_Acy.this, "", false);
        A_0_App.getApi().verifyFriendList(mobile, uid, new InterVerifyFriendList() {
            
            @Override
            public void onSuccess() {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(A_3_1_2_Forget_Friend_Acy.this);
                Intent intent = new Intent();
                intent.setClass(A_3_1_2_Forget_Friend_Acy.this,A_3_1_3_Forget_SetPassWord_Acy.class);
                intent.putExtra("acy_type", type);
                intent.putExtra("user_phone", user_phone);
                startActivity(intent);
                finish();
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
                A_0_App.getInstance().CancelProgreDialog(A_3_1_2_Forget_Friend_Acy.this);
                PubMehods.showToastStr(A_3_1_2_Forget_Friend_Acy.this, msg);
                adapter = null;
                getFriendList(user_phone);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    

    private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean max_count) {
        
        if (wholeView)
            sv_select_friend.setVisibility(View.VISIBLE);
        else
            sv_select_friend.setVisibility(View.GONE);
        
        if (loadFaile)
            mLinerReadDataError.setVisibility(View.VISIBLE);
        else
            mLinerReadDataError.setVisibility(View.GONE);
        if(loading){
        	drawable.start();
            side_acy_loading.setVisibility(View.VISIBLE);
        }else{
        	if (drawable!=null) {
        		drawable.stop();
			}
        	
            side_acy_loading.setVisibility(View.GONE);
        }
        if(max_count)
            acy_forget_friend_error_count.setVisibility(View.VISIBLE);
        else
            acy_forget_friend_error_count.setVisibility(View.GONE);
    }
    
    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.acy_forget_friend_load_error:
                    showLoadResult(true, false, false,false);
                    getFriendList(user_phone);
                    break;
                default:
                    break;
            }
        }
    };
    
    public boolean isChice[];
    public class MyAdapter extends BaseAdapter {
        
        private boolean isChice[];
        public MyAdapter(int count) {
            isChice = new boolean[count];
            for (int i = 0; i < count; i++) {
                isChice[i] = false;
            }
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
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
        public View getView(int position, View converView, ViewGroup arg2) {
            MyHolder holder;
            if (converView == null) {
                converView = View.inflate(A_3_1_2_Forget_Friend_Acy.this,
                        R.layout.item_login_forget_friend, null);
                holder = new MyHolder();
                holder.iv_friend_por = (CircleImageView) converView.findViewById(R.id.iv_friend_por);
                holder.tv_friend_name = (TextView) converView.findViewById(R.id.tv_friend_name);
                holder.iv_select_friend = (ImageView) converView
                        .findViewById(R.id.iv_select_friend);

                converView.setTag(holder);
            } else {
                holder = (MyHolder) converView.getTag();
            }
            if (position % 8 == 0) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_one);

            } else if (position % 8 == 1) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_two);
            } else if (position % 8 == 2) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_three);
            } else if (position % 8 == 3) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_four);
            } else if (position % 8 == 4) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_five);
            } else if (position % 8 == 5) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_six);
            } else if (position % 8 == 6) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_seven);
            } else if (position % 8 == 7) {
                holder.iv_friend_por.setBackgroundResource(R.drawable.photo_eight);
            }
            String uri = mList.get(position).getPhoto_url();
			if(holder.iv_friend_por.getTag() == null){
			    PubMehods.loadServicePic(imageLoader, uri,holder.iv_friend_por, options);
			    holder.iv_friend_por.setTag(uri);
			}else{
			    if(!holder.iv_friend_por.getTag().equals(uri)){
			        PubMehods.loadServicePic(imageLoader,uri,holder.iv_friend_por, options);
			        holder.iv_friend_por.setTag(uri);
			    }
			}
            holder.tv_friend_name.setText(mList.get(position).getTrue_name());
            if (isChice[position]) {
                holder.iv_select_friend.setBackgroundResource(R.drawable.register_box_selected);
            } else {
                holder.iv_select_friend.setBackgroundResource(R.drawable.register_box_unselected);
            }
            return converView;
        }

        public void chiceState(int post)
        {
            isChice[post] = isChice[post] == true ? false : true;
            this.notifyDataSetChanged();
        }

        private String getCateIDString() {
            String str = "";
            boolean firstData = false;
            for (int i = 0; i < isChice.length; i++) {
                if (isChice[i]) {
                    if (!firstData) {
                        firstData = true;
                        str += mList.get(i).getUser_id();
                    } else {
                        str += "," + mList.get(i).getUser_id();
                    }
                }
            }
            return str;
        }
        
    }
    
    private class MyHolder
    {
        CircleImageView iv_friend_por;
    	TextView tv_friend_name;
    	ImageView iv_select_friend;
    }
    
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        // TODO Auto-generated method stub
    	switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		default:
			break;
		}
    }
    
    private void clearData(boolean setNull) {
        if (mList != null) {
            mList.clear();
            if (setNull)
                mList = null;
        }
    }

    @Override
    protected void onDestroy() {
        clearData(true);
        adapter = null;
        drawable.stop();
        drawable=null;
        super.onDestroy();
    }

}

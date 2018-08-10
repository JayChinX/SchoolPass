package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.bean.Cpk_Persion_Contact;
import com.yuanding.schoolpass.service.Api.ContactListInvative;
import com.yuanding.schoolpass.service.Api.InterContactTeacherList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.NetUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.contact.CharacterParser;
import com.yuanding.schoolpass.view.contact.PinyinComparator;
import com.yuanding.schoolpass.view.contact.SideBar;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolpass.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolpass.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolpass.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月5日 下午4:27:41 
 * 我的老师
 */
public class B_Contact_Main_Teacher extends Fragment implements SideBar.OnTouchingLetterChangedListener, TextWatcher {
    
    private View mLinerReadDataError,mLinerNoContent,liner_class_contact1,viewone,contact_acy_loading;
    // 通讯录
    private SideBar mSideBar;
    private TextView mDialog;
    private PullToRefreshListView mListView;
    private EditText mSearchInput;
    private CharacterParser characterParser;// 汉字转拼音
    private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类
    private List<Cpk_Persion_Contact> sortDataList = new ArrayList<Cpk_Persion_Contact>();
    private SchoolFriendMemberListAdapter mAdapter;
    private ACache macACache;
    private JSONObject jsonObject;
    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean havaSuccessLoadData = false;
  //  private BitmapUtils bitmapUtils;
    private Handler handler=new Handler()
    {
    	public void handleMessage(android.os.Message msg) {
    		A_0_App.teacher_flag=false;
    	};
    };
    
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewone = inflater.inflate(R.layout.activity_contact_student_teacher, container, false);
        demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
         handler.sendEmptyMessageDelayed(22, 2000);
        liner_class_contact1 = viewone.findViewById(R.id.liner_class_contact1);
        mLinerReadDataError = viewone.findViewById(R.id.contact_acy_load_error_colleague);
        mLinerNoContent = viewone.findViewById(R.id.contact_acy_no_content_colleague);
        contact_acy_loading=viewone.findViewById(R.id.contact_acy_loading);
       
        home_load_loading = (LinearLayout) contact_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
        TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
        iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
        tv_blank_name.setText("暂无联系人~");
        
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center,
                R.drawable.ic_defalut_person_center);
        //bitmapUtils=A_0_App.getBitmapUtils(getActivity(), R.drawable.ic_defalut_person_center, R.drawable.ic_defalut_person_center);

        mListView = (PullToRefreshListView) viewone.findViewById(R.id.school_friend_member);
        mSideBar = (SideBar) viewone.findViewById(R.id.school_friend_sidrbar);
        mDialog = (TextView) viewone.findViewById(R.id.school_friend_dialog);
        mSearchInput = (EditText) viewone.findViewById(R.id.school_friend_member_search_input);

        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
       
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(getActivity(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);
                        initData();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                    	if (repfresh==0) {
							repfresh=1;
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
			mListView.onRefreshComplete();
			demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							mListView.setMode(Mode.DISABLED);
							initData();

						};
					});
		}
		
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				 if (demo_swiperefreshlayout!=null&&mListView.getChildCount() > 0 && mListView.getRefreshableView().getFirstVisiblePosition() == 0
			                && mListView.getChildAt(0).getTop() >= mListView.getPaddingTop()) {
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
                showLoadResult(true,false, false, false);
                initData();
            }
        });
        
        mLinerNoContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true,false, false, false);
                initData();
            }
        });
        
        if (A_0_App.USER_STATUS.equals("5")||A_0_App.USER_STATUS.equals("0") ) {
            showLoadResult(false,false, false, true);
        } else {
            mSearchInput.addTextChangedListener(this);
            characterParser = CharacterParser.getInstance();
            pinyinComparator = new PinyinComparator();
            
            mAdapter = new SchoolFriendMemberListAdapter(getActivity(), sortDataList);
            mListView.setAdapter(mAdapter);
            
            sortDataList.clear();
            sortDataList.addAll(A_0_App.getInstance().getmContactTeacher());
            
            if (A_0_App.getInstance().hava_Load_Contact) {
                delayGetData();
            } else {
                A_0_App.getInstance().hava_Load_Contact = true;
//                A_0_App.getInstance().showProgreDialog(getActivity(), "", false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        A_0_App.getInstance().CancelProgreDialog(getActivity());
                        delayGetData();
                    }
                }, 800);
            }
          
        }
        return viewone;
    }
    
    private void delayGetData() {
        if (sortDataList != null && sortDataList.size() > 0) {
            fillData(sortDataList);
            // 根据a-z进行排序源数据
            Collections.sort(sortDataList, pinyinComparator);
            mAdapter.notifyDataSetChanged();
            showLoadResult(false,true, false, false);
        } else {
            showLoadResult(true,false, false, false);
            readCache();
        }
    }
    
    private void readCache() {
        macACache=ACache.get(getActivity());
        jsonObject=macACache.getAsJSONObject(AppStrStatic.cache_key_teacher+A_0_App.USER_UNIQID);
        if (jsonObject!= null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            initData();
        }
    }
    
    private void showLoadResult(boolean loading,boolean list,boolean loadFaile,boolean noData) {
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
        if(loading){
            if (drawable!=null) {
                drawable.start();
            }
            contact_acy_loading.setVisibility(View.VISIBLE);}
        else{
        	if (drawable!=null) {
        		drawable.stop();
			}
            contact_acy_loading.setVisibility(View.GONE);}
    }
    

    /**************************************** 通讯录 ****************************************/
    /**
     * 初始化数据
     */
    private void initData() {

        A_0_App.getApi().getContactTeacherList(getActivity(),A_0_App.USER_TOKEN,new InterContactTeacherList() {
                    @Override
                    public void onSuccess(List<Cpk_Persion_Contact> mList) {
                        if (sortDataList == null)
                            return;
                        Success(mList);
                    }
                },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (sortDataList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        PubMehods.showToastStr(getActivity(), msg);
                        if (!havaSuccessLoadData)
                        {
                            if (sortDataList.size() <= 0)
                                showLoadResult(false, false, true, false);
                        }
                        demo_swiperefreshlayout.setRefreshing(false);  
                        if(null!=mListView){
                            mListView.onRefreshComplete();
                            mListView.setMode(Mode.DISABLED);
                        }
                        repfresh=0;
                    }

                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

    }

    protected void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		List<Cpk_Persion_Contact> list = getList(jsonObject);
		Success(list);
	}

	private  List<Cpk_Persion_Contact> getList(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			List<Cpk_Persion_Contact> mlistContact= new ArrayList<Cpk_Persion_Contact>();
	        if (state == 1) {
	            mlistContact=JSON.parseArray(jsonObject.optJSONArray("tlist")+"", Cpk_Persion_Contact.class);
	        }
	        return mlistContact;
		 
	}
	 public void Success(List<Cpk_Persion_Contact> mList) {
	     havaSuccessLoadData = true;   
         if (mList != null && mList.size() > 0) {
             if (sortDataList != null) {
                 sortDataList.clear();
             }
             sortDataList.addAll(mList);
             fillData(sortDataList);
             
             A_0_App.getInstance().getmContactTeacher().clear();
             A_0_App.getInstance().getmContactTeacher().addAll(mList);
             
             // 根据a-z进行排序源数据
             Collections.sort(sortDataList, pinyinComparator);
             if(mAdapter!=null){
             mAdapter.notifyDataSetChanged();
             }
             showLoadResult(false,true, false, false);
         } else {
        	 if(mAdapter!=null){
             mAdapter.notifyDataSetChanged();
        	 }
             showLoadResult(false,false, false, true);
         }
         demo_swiperefreshlayout.setRefreshing(false);  
         if(null!=mListView){
             mListView.onRefreshComplete();
             mListView.setMode(Mode.DISABLED);
         }
         repfresh=0;
     }
	@Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (mAdapter != null) {
            position = mAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            mListView.getRefreshableView().setSelection(position);
            // mListView.setSelection(position);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
        filterData(s.toString(), sortDataList);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr, List<Cpk_Persion_Contact> list) {
        List<Cpk_Persion_Contact> filterDateList = new ArrayList<Cpk_Persion_Contact>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            filterDateList.clear();
            for (Cpk_Persion_Contact sortModel : list) {
                String name = sortModel.getName();
                String suoxie = sortModel.getSuoxie();
                if (name.indexOf(filterStr.toString()) != -1
                        || suoxie.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                                filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mAdapter.updateListView(filterDateList);
    }

    /**
     * 填充数据
     * 
     * @param list
     */
    private void fillData(List<Cpk_Persion_Contact> list) {
        for (Cpk_Persion_Contact cUserInfoDto : list) {
            if (cUserInfoDto != null && cUserInfoDto.getName() != null) {
                String pinyin = characterParser.getSelling(cUserInfoDto
                        .getName());
                String suoxie = CharacterParser.getFirstSpell(cUserInfoDto
                        .getName());

                cUserInfoDto.setSuoxie(suoxie);
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if ("1".equals(cUserInfoDto.getUtype())) {// 判断是否是管理员
                    cUserInfoDto.setSortLetters("☆");
                } else if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
                    cUserInfoDto.setSortLetters(sortString);
                } else {
                    cUserInfoDto.setSortLetters("#");
                }
            }
        }
    }

    /**
     * 成员列表适配器
     */
    public class SchoolFriendMemberListAdapter extends BaseAdapter implements
            SectionIndexer {

        private LayoutInflater inflater;

        private Activity mActivity;

        private List<Cpk_Persion_Contact> list;

        public SchoolFriendMemberListAdapter(Activity mActivity,
                List<Cpk_Persion_Contact> list) {
            this.mActivity = mActivity;
            this.list = list;
        }

        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         * 
         * @param list
         */
        public void updateListView(List<Cpk_Persion_Contact> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                inflater = (LayoutInflater) mActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.items_contact_list_haveinvatbtn,
                        null);
                holder = new ViewHolder();
                holder.ivHead = (CircleImageView) convertView
                        .findViewById(R.id.iv_contact_por);
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.tv_contact_name);
                holder.tvLetter = (TextView) convertView
                        .findViewById(R.id.catalog);
                holder.tvContent = (LinearLayout) convertView
                        .findViewById(R.id.content);
                holder.btn_invative = (Button) convertView
						.findViewById(R.id.btn_invative);
                convertView.setTag(holder);
            } else {
            	convertView.clearAnimation();
                holder = (ViewHolder) convertView.getTag();
            }

            final Cpk_Persion_Contact dto = list.get(position);

            if (dto != null) {
                // 根据position获取分类的首字母的Char ascii值
                int section = getSectionForPosition(position);
                // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                if (position == getPositionForSection(section)) {
                    holder.tvLetter.setVisibility(View.VISIBLE);
                    holder.tvLetter
                            .setText("☆".equals(dto.getSortLetters()) ? dto
                                    .getSortLetters() + "(管理员)" : dto
                                    .getSortLetters());
                } else {
                    holder.tvLetter.setVisibility(View.GONE);
                }
                holder.tvTitle.setText(dto.getName());
                if (position % 8 == 0) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_one);

                } else if (position % 8 == 1) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_two);
                } else if (position % 8 == 2) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_three);
                } else if (position % 8 == 3) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_four);
                } else if (position % 8 == 4) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_five);
                } else if (position % 8 == 5) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_six);
                } else if (position % 8 == 6) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
                } else if (position % 8 == 7) {
                    holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
                }
                String uri = dto.getPhoto_url();
				if(holder.ivHead.getTag() == null){
				    PubMehods.loadServicePic(imageLoader,uri,holder.ivHead, options);
				    holder.ivHead.setTag(uri);
				}else{
				    if(!holder.ivHead.getTag().equals(uri)){
				        PubMehods.loadServicePic(imageLoader,uri,holder.ivHead, options);
				        holder.ivHead.setTag(uri);
				    }
				}
                  //bitmapUtils.display(holder.ivHead, dto.getPhoto_url());
                holder.tvContent.setTag(dto);
                holder.tvContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cpk_Persion_Contact dd = (Cpk_Persion_Contact) v
                                .getTag();
                        Intent intent = new Intent(
                                getActivity(),
                                B_Mess_Persion_Info.class);
                        intent.putExtra("uniqid", dd.getUniqid());
                        startActivity(intent);
                    }
                });
                
                
				//如果可以显示邀请按钮             
				if(dto.getIs_invite()==1){					
					holder.btn_invative.setBackgroundResource(R.drawable.btn_contact_invat_selector);
					holder.btn_invative.setText("邀请");
					holder.btn_invative.setVisibility(View.VISIBLE);											
					//设置点击事件
					holder.btn_invative.setOnClickListener(new OnClickListener() {							
						@Override
						public void onClick(final View arg0) {							
							
							final GeneralDialog upDateDialog = new GeneralDialog(B_Contact_Main_Teacher.this.getContext(), R.style.Theme_GeneralDialog);
							upDateDialog.setTitle(R.string.pub_title);
							upDateDialog.setContent("您正在邀请 "+dto.getName()+" 使用"+A_0_App.APP_NAME+" APP");
							upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
								@Override
								public void onClick(View v) {
									upDateDialog.cancel();
								}
							});
							upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
								@Override
								public void onClick(View v) {
									
									
									/**
									 * 调接口发送短信
									 */
									if(NetUtils.isConnected(getContext())){
										A_0_App.getApi().reqContactInvative(A_0_App.USER_TOKEN, dto.getUniqid(), new ContactListInvative() {
											
                                            @Override
                                            public void onSuccess(String msg) {
                                                if (sortDataList == null)
                                                    return;
                                                if(getActivity() == null || getActivity().isFinishing())
                                                    return;
                                                if (msg != null && !"".equals(msg))
                                                    PubMehods.showToastStr(getActivity(),msg);
                                             }
										},new Inter_Call_Back() {
                                            
                                            @Override
                                            public void onFinished() {
                                                // TODO Auto-generated method stub
                                                
                                            }
                                            
                                            @Override
                                            public void onFailure(String msg) {
                                                if (sortDataList == null)
                                                    return;
                                                if(getActivity() == null || getActivity().isFinishing())
                                                    return;
                                                if (msg != null && !"".equals(msg))
                                                    PubMehods.showToastStr(getActivity(),msg);
                                             }
                                            
                                            @Override
                                            public void onCancelled() {
                                                // TODO Auto-generated method stub
                                                
                                            }
                                        });
										
										((Button)arg0).setBackgroundResource(R.drawable.btn_yiyaoqing);
										((Button)arg0).setText("已邀请");
										((Button)arg0).setOnClickListener(null);
										list.get(position).setIs_invite(2);
										upDateDialog.cancel();
									}else{
										upDateDialog.cancel();
										PubMehods.showToastStr(getContext(), "请检查您的网络设置");
									}
									
								}
								
							});

							upDateDialog.show();
						}
					});
					
				}else if(dto.getIs_invite()==2){				
					holder.btn_invative.setBackgroundResource(R.drawable.btn_yiyaoqing);
					holder.btn_invative.setText("已邀请");
					holder.btn_invative.setVisibility(View.VISIBLE);
				}else if(dto.getIs_invite()==0){
					holder.btn_invative.setVisibility(View.GONE);
				}

			}
            
            if(A_0_App.isShowAnimation==true){
             if(position>A_0_App.teacher_curPosi||A_0_App.teacher_flag)
			 {

				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*position);
				A_0_App.teacher_curPosi=position;
		        convertView.startAnimation(an);
			 }
            }
            return convertView;
        }

        class ViewHolder {
            CircleImageView ivHead;
            TextView tvLetter;
            TextView tvTitle;
            LinearLayout tvContent;
            Button btn_invative; //邀请
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            
            return list.get(position).getSortLetters().charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public Object[] getSections() {
            return null;
        }
    }
    
    @Override
    public void onDestroyView() {
        if (sortDataList != null) {
            sortDataList.clear();
            sortDataList = null;
        }
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        if (sortDataList != null) {
            sortDataList.clear();
            sortDataList = null;
        }
        drawable.stop();
        drawable=null;
    	super.onDestroy();
    }
}

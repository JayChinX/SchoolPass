package com.yuanding.schoolpass.service;

import io.rong.imlib.model.Conversation.ConversationType;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.bean.Befriend_Acquire_Task_Bean;
import com.yuanding.schoolpass.bean.Befriend_Bill_Bean;
import com.yuanding.schoolpass.bean.Befriend_Center_Bean;
import com.yuanding.schoolpass.bean.Befriend_Pick_Task_Details;
import com.yuanding.schoolpass.bean.Befriend_Publish_Task_Details;
import com.yuanding.schoolpass.bean.Befriend_ReleaseHistroy_Task_Bean;
import com.yuanding.schoolpass.bean.Befriend_Release_Task_Bean;
import com.yuanding.schoolpass.bean.Befriend_Task_List;
import com.yuanding.schoolpass.bean.CpkUserModel;
import com.yuanding.schoolpass.bean.Cpk_Account_InviteInfo.MyInviterInfo;
import com.yuanding.schoolpass.bean.Cpk_Account_InviteInfo_ISendInviteInfo;
import com.yuanding.schoolpass.bean.Cpk_Acy_Detail;
import com.yuanding.schoolpass.bean.Cpk_Acy_list;
import com.yuanding.schoolpass.bean.Cpk_Attence_List_Time;
import com.yuanding.schoolpass.bean.Cpk_Banner_Mode;
import com.yuanding.schoolpass.bean.Cpk_Comment_detail;
import com.yuanding.schoolpass.bean.Cpk_Coupon_Bean;
import com.yuanding.schoolpass.bean.Cpk_Group_Info;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.bean.Cpk_Index_Notice_Message;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail;
import com.yuanding.schoolpass.bean.Cpk_Info_Detail_Comment_List_All;
import com.yuanding.schoolpass.bean.Cpk_Info_ReportType;
import com.yuanding.schoolpass.bean.Cpk_Info_list;
import com.yuanding.schoolpass.bean.Cpk_Module_List;
import com.yuanding.schoolpass.bean.Cpk_Notice_Detail;
import com.yuanding.schoolpass.bean.Cpk_Notice_List;
import com.yuanding.schoolpass.bean.Cpk_Persion_Contact;
import com.yuanding.schoolpass.bean.Cpk_Register_Select_Friend_Acy;
import com.yuanding.schoolpass.bean.Cpk_RongYun_True_Name;
import com.yuanding.schoolpass.bean.Cpk_School_Assistant_List;
import com.yuanding.schoolpass.bean.Cpk_School_Assistant_Main;
import com.yuanding.schoolpass.bean.Cpk_School_Assistant_detail;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out;
import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out_Child;
import com.yuanding.schoolpass.bean.Cpk_School_List;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Con;
import com.yuanding.schoolpass.bean.Cpk_Search_Mess_Notice;
import com.yuanding.schoolpass.bean.Cpk_Side_Attdence_Detail_List;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence_Detail;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence_List;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence_Personal;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_CourseDetail;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_Official;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_Week;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_WeekDetail;
import com.yuanding.schoolpass.bean.Cpk_Side_Lectures_Detail;
import com.yuanding.schoolpass.bean.Cpk_Side_Lectures_List;
import com.yuanding.schoolpass.bean.Cpk_Side_Lost_List;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_Detail;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_List;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_Type;
import com.yuanding.schoolpass.bean.Cpk_Side_Score_List;
import com.yuanding.schoolpass.bean.Cpk_Student_Info;
import com.yuanding.schoolpass.bean.Cpk_SysNotice_ItemUserDetail;
import com.yuanding.schoolpass.bean.Cpk_Teach_Class;
import com.yuanding.schoolpass.bean.Cpk_Top_List;
import com.yuanding.schoolpass.bean.Cpk_User_Authenticate;
import com.yuanding.schoolpass.bean.Cpk_User_Values;
import com.yuanding.schoolpass.bean.Cpk_Version;
import com.yuanding.schoolpass.bean.Item_Cate;
import com.yuanding.schoolpass.https.HttpUtil;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.ApiSignSort;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月25日 下午4:28:06 接口类
 * //会话过期/审核通过/其他手机端登录
 */
public class Api {
	private Context context;
	private String[] error_title;

	public static String TAG = "Api";
	
	public Api(Context con) {
		this.context = con;
		error_title = new String[] {
				context.getResources()
						.getString(R.string.error_title_net_error),
				context.getResources().getString(
						R.string.error_title_net_timeout),
				context.getResources().getString(
						R.string.error_title_analyze_error),
				context.getResources().getString(
						R.string.error_title_request_error) };
	}
	
    /*
     * 公共请求Interface
     */
    public interface Inter_Call_Back {
        void onCancelled();
        void onFinished();
        void onFailure(String msg);
    }
    
    /*
     * 非成功状态的请求处理1、0、-1、101
     */
    public void onFailureBack(Context context, JSONObject jsonObject, Inter_Call_Back iPubCallBack,int state, String message) {
        if(jsonObject == null)
            iPubCallBack.onFailure(message);
        if (state == -1) {
            String logout_type = jsonObject.optString("logout_type");
            String logout_text = jsonObject.optString("logout_text");
            if (logout_type.equals("audit")) {// 审核通过
                A_0_App.getInstance().showExitDialog(context, logout_text);
            } else {// 其他设备登录
                A_0_App.getInstance().showExitToast(context, logout_text);
            }
        } else if (state == 101) {// 该用户已删除
            iPubCallBack.onFailure(AppStrStatic.TAG_USER_IS_DELETE);
        } else {
            iPubCallBack.onFailure(message);
        }
    }
    
    /*
     *本地错误的请求处理 
     */
    public void onErrorBack(Inter_Call_Back iPubCallBack,Throwable error, boolean arg1) {
        if (error instanceof HttpHostConnectException) {
            iPubCallBack.onFailure(error_title[0]);
        } else if (error instanceof UnknownHostException) {
            iPubCallBack.onFailure(error_title[0]);
        } else if (error instanceof ConnectException) {
            iPubCallBack.onFailure(error_title[0]);
        } else if (error instanceof ConnectTimeoutException) {
            iPubCallBack.onFailure(error_title[1]);
        } else if (error instanceof SocketTimeoutException) {
            iPubCallBack.onFailure(error_title[1]);
        } else {
            iPubCallBack.onFailure(error_title[3]);
        }
    }
    
////////////************************************************************************************//////////////////
	/**
     * 上传ChannelID
     */
    public interface Inter_UpLoad_ChannelId {
        void onSuccess(String msg);
    }

    public void upLoad_Chanel_Id(final Context context, String device_token,String getui_client_id,String token, 
            final Inter_UpLoad_ChannelId interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/updatePushDeviceToken";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("device_token", device_token);
        params.put("getui_client_id", getui_client_id);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            
                            if (state == 1) {
                                interCallBack.onSuccess(message);
                            } else {
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 获取服务器时间和其他用
     */
    public interface Inter_Get_Service_Time {
        void onSuccess(String time,String icon);
    }
    
    // 获取服务器时间
    public void getServiceTime(final Context context,String token,String type,final Inter_Get_Service_Time interCallBack,
            final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/getServerTime";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("app_type", "android");
        map.put("app_version", PubMehods.getVerName(context));
        map.put("token", token);
        map.put(AppStrStatic.SIGN, ApiSignSort.getSignature(map));
        HttpUtil.send(context, url, map, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                iPubCallBack.onCancelled();
            }

            @Override
            public void onError(Throwable error, boolean arg1) {
                onErrorBack(iPubCallBack, error, arg1);
            }

            @Override
            public void onFinished() {
                iPubCallBack.onFinished();
            }

            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    int status = jsonObject.optInt("status");
                    String message = jsonObject.optString("msg");
                    String time = jsonObject.optString("time");
                    String icon = jsonObject.optString("icon");
                    if (status == 1) {
                        interCallBack.onSuccess(time,icon);
                    }else{
                        onFailureBack(context, jsonObject, iPubCallBack, status, message);
                    }
                } catch (JSONException e) {
                    iPubCallBack.onFailure(error_title[2]);
                }
            }
        });
    }
    
	/**
	 * 
	 * @ClassName: InterLogin
	 * @Description: TODO(这里用一句话描述这个类的作用)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20 登录接口
	 */
	public interface InterLogin {
		void onSuccess(CpkUserModel user);
	}

	public void Login(final Context context, String ClientID, String device_token,
			String str_phone, String str_pwd,String device_info, final InterLogin interLogin,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/newLogin";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("app_type", "android");
		params.put("app_version", PubMehods.getVerName(context));
		params.put("clientid", ClientID);
		params.put("device_token", device_token);
		params.put("mobile", str_phone);
		params.put("passwd", str_pwd);
		params.put("sysinfo", device_info);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {// 登录成功
								CpkUserModel user = new CpkUserModel();
								user.setUser_id(jsonObject.optString("user_id"));
								user.setUniqid(jsonObject.optString("uniqid"));
								user.setPhone(jsonObject.optString("phone"));
								user.setSex(jsonObject.optString("sex"));
								user.setPhoto_url(jsonObject
										.optString("photo_url"));

								user.setUsername(jsonObject.optString("username"));
								user.setName(jsonObject.optString("name"));
								user.setStudent_number(jsonObject
										.optString("student_number"));
								user.setStudent_status(jsonObject
										.optString("student_status"));
								user.setSchool_id(jsonObject
										.optString("school_id"));
								user.setPids(jsonObject.optString("pids"));

								user.setClassname(jsonObject
										.optString("classname"));
								user.setQuniqid(jsonObject.optString("quniqid"));
								user.setQutoken(jsonObject.optString("qutoken"));

								user.setEnrol_year(jsonObject
										.optString("enrol_year"));
								user.setClass_id(jsonObject
										.optString("class_id"));
								user.setToken(jsonObject.optString("token"));
								user.setBang_url(jsonObject.optString("bang_url"));
                                user.setDevice_token(jsonObject.optString("device_token"));
                                user.setGetui_client_id(jsonObject.optString("getui_client_id"));
                                user.setBang_url(jsonObject.optString("bang_url"));
								user.setCoupon_url(jsonObject.optString("coupon_url"));
								user.setCoupon_use_url(jsonObject.optString("coupon_use_url"));
								interLogin.onSuccess(user);
								jsonObject = null;
							} else {// 登录失败
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 退出登录
	 */
	public interface Inter_Exit_Login {
		void onSuccess();
	}

	public void exit_login(String token, final Inter_Exit_Login interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/logout";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							} else {
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 获取验证码
	 */
	public interface InterCall_Back {
		void onSuccess(String code);
	}

	public void get_yan_zheng_code(String type, String mobile,
			final InterCall_Back interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicommon/newSendSms";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", mobile);
		params.put("type", type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess(jsonObject
										.optString("code"));
							} else {
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
     * 校验手机号的用户状态
     */
    public interface InterPhone_Judge {
        void onSuccess(String auth_status,String recommend_phone,String msg);
    }

    public void judge_yan_zheng_phone(String mobile,final InterPhone_Judge interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/userRegisterStepOne";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                String recommend_phone = jsonObject.optString("recommend_phone");
                                interCallBack.onSuccess(jsonObject.optString("user_status"),recommend_phone, message);
                            } else {
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

	/**
	 * 校验验证码
	 */
	public interface InterCall_Back_Judge {
		void onSuccess(String user_status,String friendlist_is_display,String recommend_phone);
	}

	public void judge_yan_zheng_code(final String type, String mobile,
			String code, final InterCall_Back_Judge interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicommon/newverifySms";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", mobile);
		params.put("type", type);
		params.put("code", code);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
                            String recommend_phone, user_status,friendlist_is_display = "";
                            if (state == 1) {
                                recommend_phone = jsonObject.optString("recommend_phone");
                                user_status = jsonObject.optString("user_status");
                                friendlist_is_display = jsonObject.optString("friendlist_is_display");
                                interCallBack.onSuccess(user_status,friendlist_is_display, recommend_phone);
                            } else {
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(用户认证接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterRenZheng {
		void onSuccess(Cpk_User_Authenticate authen);
	}

	public void Approve(final Context context, String str_phone,
			final InterRenZheng interLogin,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/authStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", str_phone);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								Cpk_User_Authenticate user = new Cpk_User_Authenticate();
								JSONObject temp = jsonObject
										.getJSONObject("uinfo");
								user.setClass_name(temp.optString("class_name"));
								user.setEnrol_year(temp.optString("enrol_year"));
								user.setParent_name(temp
										.optString("parent_name"));// parent_name
								user.setSchool_name(temp
										.optString("school_name"));
								user.setPolitics(temp.optString("politics"));
								user.setName(temp.optString("name"));
								user.setBirthday(temp.optString("birthday"));
								user.setStudent_number(temp
										.optString("student_number"));
								user.setSex(temp.optString("sex"));
								user.setSchool_id(temp.optString("school_id"));
								user.setOrgan_id(temp.optString("class_id"));// class_id
								interLogin.onSuccess(user);
							} else {
							    iPubCallBack.onFailure(message);
							}
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(学校列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSchoolList {
		void onSuccess(String message, Cpk_School_List school);
	}

	public void getSchoolList(final Context context, String keyword,
			String page_no,String phone_no, final InterSchoolList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL+ "Apischool/getSchoolList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("page_no", page_no);
		params.put("page_size", "100");// 100条学校
		params.put("app_version", PubMehods.getVerName(context));
		params.put("device_vin", phone_no);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							Cpk_School_List school = new Cpk_School_List();
							if (1 == state) {
								school.setStotal(jsonObject.optString("stotal"));
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("slist");
								List<Item_Cate> list = new ArrayList<Item_Cate>();
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										JSONObject object = jsonArrayItem
												.getJSONObject(j);
										Item_Cate item = new Item_Cate();
										item.setSchool_id(object
												.optString("school_id"));
										item.setSchool_name(object
												.optString("school_name"));
										list.add(item);
									}
								}
								school.setmList(list);
								iCallBack.onSuccess(message, school);
							} else {
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(教学机构列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterTeachOrganList {
		void onSuccess(List<Cpk_Teach_Class> mlist, String message);
	}

	public void getTeachOrgan(final Context context, String organ_id,
			String school_id, boolean firsetQuest,
			final InterTeachOrganList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apischool/getClassList";
		Map<String, Object> params = new HashMap<String, Object>();
		if (!firsetQuest)
			params.put("organ_id", organ_id);
		params.put("school_id", school_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("clist");
								List<Cpk_Teach_Class> mlistContact = new ArrayList<Cpk_Teach_Class>();
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Teach_Class teach = new Cpk_Teach_Class();
										teach.setChild_total_num(jsonArrayItem
												.getJSONObject(j).optString(
														"is_last"));
										teach.setOrgan_id(jsonArrayItem
												.getJSONObject(j).optString(
														"organ_id"));
										teach.setOrgan_name(jsonArrayItem
												.getJSONObject(j).optString(
														"organ_name"));
										teach.setParent_id((jsonArrayItem
												.getJSONObject(j)
												.optString("parentid")));
										mlistContact.add(teach);
									}
								}
								iCallBack.onSuccess(mlistContact, message);
							} else {
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	public interface InterUser_Regedit {
		void onSuccess(CpkUserModel user);
	}

    /**
     * 注册
     */
   public void get_user_regedit(String device_token,String ClientID, String mobile,
            String passwd,String school_id,String recommend_phone,final InterUser_Regedit interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/userRegisterStepTwo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("passwd", passwd);
        params.put("school_id", school_id);

        params.put("clientid", ClientID);
        params.put("device_token", device_token);
        params.put("app_type", "android");
        params.put("recommend_phone", recommend_phone);
        
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                CpkUserModel user = JSON.parseObject(jsonObject+"", CpkUserModel.class);
                                interCallBack.onSuccess(user);
                            } else {
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
	}

	/**
	 * 上传图片
	 */
	public interface Inter_UpLoad_Photo {
	    void onSuccess(String imageUrl);
	    void onLoading(long arg0, long arg1, boolean arg2);
		void onStart();
		void onWaiting();
	}

	public void upload_Photo(File image_url,
			final Inter_UpLoad_Photo iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicommon/upload";
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, File> params_files = new HashMap<String, File>();
        params_files.put("image_url", image_url);
		HttpUtil.upLoadFile(context, url, params,params_files,AppStrStatic.XUTILS_INCREASE_CONN_TIMEOUT,
				new Callback.ProgressCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String image_url = jsonObject.optString("url");
							if (state == 1) {
							    iCallBack.onSuccess(image_url);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}
                    @Override
                    public void onLoading(long arg0, long arg1, boolean arg2) {
                        iCallBack.onLoading(arg0, arg1, arg2);
                    }

                    @Override
                    public void onStarted() {
                        iCallBack.onStart();
                    }

                    @Override
                    public void onWaiting() {
                        iCallBack.onWaiting();                        
                    }
                    
                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
				});
	}
	
	
	/**
	 * 下载文件
	 */
	public interface Inter_DownLoad_Photo {
	    void onCancelled();
	    void onFinished();
	    void onSuccess(String message);
	    void onLoading(long arg0, long arg1, boolean arg2);
		void onStart();
		void onWaiting();
		void onFailure(String msg);
	}

	public void download_Photo(String down_path,String save_path,final Inter_DownLoad_Photo iCallBack) {
		HttpUtil.downLoadFile(context, down_path, save_path,AppStrStatic.XUTILS_INCREASE_CONN_TIMEOUT,
				new Callback.ProgressCallback<File>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iCallBack.onFinished();
					}

					@Override
					public void onSuccess(File arg0) {
						
					   iCallBack.onSuccess("");
						
					}
                    @Override
                    public void onLoading(long arg0, long arg1, boolean arg2) {
                        iCallBack.onLoading(arg0, arg1, arg2);
                    }

                    @Override
                    public void onStarted() {
                        iCallBack.onStart();
                    }

                    @Override
                    public void onWaiting() {
                        iCallBack.onWaiting();                        
                    }
                    
                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        if (error instanceof HttpHostConnectException) {
                            iCallBack.onFailure(error_title[0]);
                        } else if (error instanceof UnknownHostException) {
                            iCallBack.onFailure(error_title[0]);
                        } else if (error instanceof ConnectTimeoutException) {
                            iCallBack.onFailure(error_title[1]);
                        } else if (error instanceof SocketTimeoutException) {
                            iCallBack.onFailure(error_title[1]);
                        } else {
                            iCallBack.onFailure(error_title[3]);
                        }
                    }
				});
	}
	
	
	/*********************************************帮帮*****************************************************/
	/**
	 * 身边 添加帮帮取送件订单
	 */
	public interface InterSideHelpTakeOrSendSent {
		void onSuccess(String id, String order_from, String order);
	}

	public void SideHelpTakeOrSendSent(String token, int order_from, int type, String name,
			String take_phone, String send_phone, String take_add, String send_add, String data, String others,
			int num_money, String coupon_status,String coupon_id,String coupon_amount,final InterSideHelpTakeOrSendSent interCallBack, final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("command", "save_task");
			params.put("token", token);
			params.put("type", type);
			switch (type) {
			case 1:
				params.put("creator_phone", send_phone);
				params.put("dest_phone", take_phone);
				params.put("src_address", take_add);
				params.put("dest_address", send_add);
				break;
			case 2:
				params.put("creator_phone", take_phone);
				params.put("dest_phone", send_phone);
				params.put("src_address", take_add);
				params.put("dest_address", send_add);
				break;
			case 3:
				params.put("creator_phone", take_phone);
				break;
			case 4:
				params.put("creator_phone", take_phone);
				break;
			case 99:
				params.put("creator_phone", take_phone);
				break;
			}
			params.put("express_name", name);
			params.put("expired_time", data);
			params.put("order_from", order_from);
			params.put("total_amount", num_money > 0 ? num_money : 0);//num_money > 0 ? num_money : 0
			if (others != null) {
				params.put("summary", others);
			}
			//优惠券
		    params.put("coupon_status", coupon_status);
		    params.put("coupon_id", coupon_id);
		    params.put("coupon_amount", coupon_amount);

			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							Log.i("支付", result);
							JSONObject jsonObject = new JSONObject(result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							
							if (state == 1) {
								String jO = jsonObject.optString("list");
								JSONObject obj = new JSONObject(jO);
								String id = obj.optString("id");
								String order_from = obj.optString("order_from");
								String pay_time = obj.optString("pay_time");
								String order = obj.optString("order");
								interCallBack.onSuccess(id, order_from,order);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 身边 接任务订单
	 */
	public interface InterSideHelpGetTaskSent {
			void onSuccess(int result , String id);
	}

	public void SideHelpGetTaskSent(String token, String id,
			final InterSideHelpGetTaskSent interCallBack, final Inter_Call_Back iPubCallBack) {
			String url =A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("token", token);
			params.put("command", "acquire_task");
			params.put("id", id);
	
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
						
							if (state == 1) {
								String resultOther = jsonObject.optString("list");
								JSONObject jO = new JSONObject(resultOther);
								int resultO = jO.optInt("result");
								String id = jO.optString("id");
								interCallBack.onSuccess(resultO, id);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * 获取任务列表的借口
	 **/

	public interface InterHelpTaskList {
		void onSuccess(List<Befriend_Task_List> mList);
	}

	public void getTaskList(final Context context, String token, final String page_no,
			final InterHelpTaskList iCallBack, final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("command", "select_task_list");
			params.put("token", token);
			params.put("page_no", page_no);
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						Log.i("aaaaaaaaa", result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String time = jsonObject.optString("time");
							List<Befriend_Task_List> mlistContact = new ArrayList<Befriend_Task_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
								if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(jsonArrayItem + "", Befriend_Task_List.class);
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 发送举报信息
	 */
	public interface InterReportTaskSent {
		void onSuccess(String message);
	}

	public void ReportTaskSent(String token, String id, String report,
			final InterReportTaskSent interCallBack, final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("token", token);
			params.put("command", "misdeed_task");
			params.put("id", id);
			params.put("type", report);
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							int time = jsonObject.optInt("time");

							if (state == 1) {
								interCallBack.onSuccess(message);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
	
	/**
	 * 
	 * 发布的任务状态接口
	 **/

	public interface InterTaskStatus {
		void onSuccess(Befriend_Publish_Task_Details publishTaskDetails);
	}

	public void getPublishTaskDetails(final Context context, String token, final String id,
			final InterTaskStatus iCallBack, final Inter_Call_Back iPubCallBack) {
			String url =A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("command", "publish_task_details");
			params.put("token", token);
			params.put("id", id);
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							Log.i("任务详情", result);
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							
							Befriend_Publish_Task_Details befriend_Center_Bean = new Befriend_Publish_Task_Details();
							if (state == 1) {
								JSONObject dd = jsonObject.getJSONObject("list");
								befriend_Center_Bean=JSON.parseObject(dd+"", Befriend_Publish_Task_Details.class);
								iCallBack.onSuccess(befriend_Center_Bean);
							} else{
								onFailureBack(context, jsonObject, iPubCallBack, Integer.valueOf(state), message);
							} 
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * 接收的任务状态接口
	 **/

	public interface InterPickTaskStatus {
		void onSuccess(Befriend_Pick_Task_Details pickTaskDetails);
	}

	public void getPickTaskDetails(final Context context, String token, final String id,
			final InterPickTaskStatus iCallBack, final Inter_Call_Back iPubCallBack) {
			String url =A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("command", "acquire_task_details");
			params.put("token", token);
			params.put("id", id);
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							Log.i("任务详情", result);
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							
							Befriend_Pick_Task_Details pickTaskDetails = new Befriend_Pick_Task_Details();
							if (state == 1) {
								JSONObject dd = jsonObject.getJSONObject("list");
								pickTaskDetails=JSON.parseObject(dd+"", Befriend_Pick_Task_Details.class);
								iCallBack.onSuccess(pickTaskDetails);
							} else{
								onFailureBack(context, jsonObject, iPubCallBack, Integer.valueOf(state), message);
							} 
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 
	 * 取消任务状态接口
	 **/

	public interface InterDeleteTaskStatus {
		void onSuccess();
	}

	public void getDeleteTaskDetails(final Context context, String token, final String id,int type,
			final InterDeleteTaskStatus iCallBack, final Inter_Call_Back iPubCallBack) {
			String url =A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
			Map<String, Object> params = new HashMap<String, Object>();
			switch (type) {
			case 1:
				params.put("command", "sponsor_cancel_task");
				break;
			case 2:
				params.put("command", "service_man_cancel_task_work");
				break;
			case 3://完成任务
				params.put("command", "update_acquire_work_complete");
				break;
			case 4://东接任务
				params.put("command", "freeze_task");
				break;
			case 5://确认付款
				params.put("command", "pay_task_work");
				break;
			}
			params.put("token", token);
			params.put("id", id);
			params.put("app_version", PubMehods.getVerName(context));
			params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							
							if (state == 1) {
								iCallBack.onSuccess();
							} else{
								onFailureBack(context, jsonObject, iPubCallBack, Integer.valueOf(state), message);
							} 
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 获取优惠券信息
	 */
	public interface InterGetCouponDetail {
		void onSuccess(Cpk_Coupon_Bean coupon_bean);
	}

	public void getCouponDetail(String token,String id,
									final InterGetCouponDetail interCallBack, final Inter_Call_Back iPubCallBack) {
		String url =A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "pay_coupon_details");
		params.put("id", id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								String resultOther = jsonObject.optString("list");
								Cpk_Coupon_Bean coupon_bean=JSON.parseObject(resultOther,Cpk_Coupon_Bean.class);
								interCallBack.onSuccess(coupon_bean);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}


	/**
	 * 
	 * @Description: TODO(常见问题列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterCom_ProblemList {
		void onSuccess(List<Cpk_Side_Lectures_List> mList,long servertime);
	}

	public void getCom_ProblemList(final Context context, String token,
			final String page_no, final InterCom_ProblemList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilecture/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							List<Cpk_Side_Lectures_List> mlistContact = new ArrayList<Cpk_Side_Lectures_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("llist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Side_Lectures_List contace = new Cpk_Side_Lectures_List();
										contace.setArticle_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setAuthor(jsonArrayItem
												.getJSONObject(j).optString(
														"author"));
										contace.setAuthor_desc(jsonArrayItem
												.getJSONObject(j).optString(
														"author_desc"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"create_time"));
										contace.setEnd_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"end_time"));

										contace.setPlace(jsonArrayItem
												.getJSONObject(j).optString(
														"place"));
										contace.setRead_num(jsonArrayItem
												.getJSONObject(j).optString(
														"read_num"));
										contace.setStart_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"start_time"));
										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));

										mlistContact.add(contace);
									}
									if (page_no.equals("1")) {
										ACache.get(context).put(
												AppStrStatic.cache_key_lecture+A_0_App.USER_UNIQID,
												jsonObject);
									}
								}

								iCallBack.onSuccess(mlistContact,servertime);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact,servertime);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(账单历史列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterPersonalBillList {
		void onSuccess(List<Befriend_Bill_Bean> mList);
	}

	public void getPersonalBillList(final Context context, String token,
			final String page_no, final InterPersonalBillList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("command", "personal_bill");
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							System.out.println(result);
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							List<Befriend_Bill_Bean> befriend_Bill_Beans = new ArrayList<Befriend_Bill_Bean>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
								if (jsonArrayItem != null&& jsonArrayItem.length() > 0) {
									befriend_Bill_Beans=JSON.parseArray(jsonArrayItem+"", Befriend_Bill_Bean.class);
									if (page_no.equals("1")) {
										ACache.get(context).put(AppStrStatic.cache_key_person_bill+A_0_App.USER_UNIQID,jsonObject);
									}
								}

								iCallBack.onSuccess(befriend_Bill_Beans);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(滚动列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterRollingList {
		void onSuccess(String content);
	}

	public void getRollingList(final Context context, String token,
			final InterRollingList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "query_rolling_list");
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							if (state == 1) {
								String content = jsonObject.optString("list");
								iCallBack.onSuccess(content);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(个人中心帮帮接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterBefriend_CenterList {
		void onSuccess(Befriend_Center_Bean befriend_Center_Bean,long servertime);
	}

	public void getBefriend_CenterList(final Context context, String token, final InterBefriend_CenterList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "personal_center");
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.println(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							
							Befriend_Center_Bean befriend_Center_Bean = new Befriend_Center_Bean();
							if (state == 1) {
								JSONObject dd = jsonObject.getJSONObject("list");
								befriend_Center_Bean=JSON.parseObject(dd+"", Befriend_Center_Bean.class);
								ACache.get(context).put(AppStrStatic.cache_key_acc_befriend_center_main+A_0_App.USER_UNIQID,jsonObject);
								iCallBack.onSuccess(befriend_Center_Bean,servertime);
							} else if (state == 0) {
								iCallBack.onSuccess(befriend_Center_Bean,servertime);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 *
	 * @Description: TODO(优惠券h5)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterBefriend_Coupon {
		void onSuccess(String coupon_use_url,String coupon_url);
	}

	public void getBefriend_Coupon(final Context context, String token, final String request_Type, final InterBefriend_Coupon iCallBack, final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL+"Apisystem/getUrlList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.println(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							Befriend_Center_Bean befriend_Center_Bean = new Befriend_Center_Bean();
							if (state == 1) {
								JSONObject dd = jsonObject.getJSONObject("data");

								String coupon_use_url ="", coupon_url = "";
								if (request_Type.equals("1")) {//优惠券
									coupon_use_url=dd.optString("coupon_use_url");
									coupon_url=dd.optString("coupon_url");
								}else if(request_Type.equals("2")) {//帮帮中心
									coupon_use_url=dd.optString("heo_h5_url");
									coupon_url=dd.optString("heo_h5_url");
								}
								iCallBack.onSuccess(coupon_use_url,coupon_url);
							}  else{
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(个人中心帮帮开启推送接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterMessageSwitch{
		void onSuccess(String msg);
	}

	public void getMessageSwitch(final Context context, String token,String status, final InterMessageSwitch iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "new_message_switch");
		params.put("status", status);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							if (state == 1) {
								iCallBack.onSuccess(message);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(支付宝绑定)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterApayBind{
		void onSuccess(String msg,String bindStatus,String account,String accountName);
	}

	public void getApayBind(final Context context, String token,String account,String accountPassword, final InterApayBind iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "bind_alipay");
		params.put("account", account);
		params.put("accountPassword", accountPassword);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.println(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							if (state == 1) {
								String list=jsonObject.optString("list");
								JSONObject jsonObject1 = new JSONObject(list);
								String bindStatus=jsonObject1.optString("bindStatus");
								String account=jsonObject1.optString("account");
								String accountName=jsonObject1.optString("accountName");
								iCallBack.onSuccess(message,bindStatus,account,accountName);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(提现申请)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterApayCrash{
		void onSuccess(String msg,String totalAmount,String withdrawTimes);
	}

	public void getApayCrash(final Context context, String token,String total_money,String pass, final InterApayCrash iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("command", "apply_withdrawals");
		params.put("total_amount", total_money);
		params.put("password", pass);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.println(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							if (state == 1) {
								String list=jsonObject.optString("list");
								JSONObject jsonObject1 = new JSONObject(list);
								String totalAmount=jsonObject1.optString("totalAmount");
								String withdrawTimes=jsonObject1.optString("withdrawTimes");
								
								iCallBack.onSuccess(message,totalAmount,withdrawTimes);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
	   /**
		 * 
		 * @Description: TODO(发布任务列表待处理接口)
		 */
		public interface InterReleaseTaskListMain {
			void onSuccess(List<Befriend_Release_Task_Bean> release_Task_Beans);
		}
		public void getReleaseTaskListMain(final Context context, String token, final InterReleaseTaskListMain iCallBack,final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put("command", "query_release_task");
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
	                new Callback.CommonCallback<String>() {
	                    @Override
	                    public void onCancelled(CancelledException arg0) {
	                        iPubCallBack.onCancelled();
	                    }

	                    @Override
	                    public void onFinished() {
	                        iPubCallBack.onFinished();
	                    }

	                    @Override
	                    public void onSuccess(String result) {
	                        try {
	                        	System.out.println("dai"+result);
	                            JSONObject jsonObject = new JSONObject(result);
	                            int state = jsonObject.optInt("status");
	                            String message = jsonObject.optString("msg");
	                            List<Befriend_Release_Task_Bean> task_Beans = new ArrayList<Befriend_Release_Task_Bean>();
	                            if (state == 1) {
	                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("list");
	                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
	                                	task_Beans = JSON.parseArray(jsonArrayItem_today + "",Befriend_Release_Task_Bean.class);
	                                }
	                                 ACache.get(context).put(AppStrStatic.cache_key_query_release_task+A_0_App.USER_UNIQID, jsonObject);
	                                iCallBack.onSuccess(task_Beans);
	                            	System.out.println(task_Beans.size()+"dai"+jsonArrayItem_today);
	                            }else{
	                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
	                            }
	                        } catch (JSONException e) {
	                            iPubCallBack.onFailure(error_title[2]);
	                        }
	                    }

	                    @Override
	                    public void onError(Throwable error, boolean arg1) {
	                        onErrorBack(iPubCallBack, error, arg1);
	                    }
	                });
		}
		
		 /**
		 * 
		 * @Description: TODO(发布任务列表历史接口)
		 */
		public interface InterReleaseHistoryTaskListMain {
			void onSuccess(List<Befriend_ReleaseHistroy_Task_Bean> release_Task_Beans);
		}

		public void getReleaseHistoryTaskListMain(final Context context, String token,final String page_no, final InterReleaseHistoryTaskListMain iCallBack,final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put("command", "query_history_release_task");
	        params.put("page_no", page_no);
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
	                new Callback.CommonCallback<String>() {
	                    @Override
	                    public void onCancelled(CancelledException arg0) {
	                        iPubCallBack.onCancelled();
	                    }

	                    @Override
	                    public void onFinished() {
	                        iPubCallBack.onFinished();
	                    }

	                    @Override
	                    public void onSuccess(String result) {
	                    	System.out.println(result);
	                        try {
	                            JSONObject jsonObject = new JSONObject(result);
	                            int state = jsonObject.optInt("status");
	                            String message = jsonObject.optString("msg");
	                            List<Befriend_ReleaseHistroy_Task_Bean> task_Beans = new ArrayList<Befriend_ReleaseHistroy_Task_Bean>();
	                            if (state == 1) {
	                                JSONArray jsonArrayItem_ealy = jsonObject.optJSONArray("list");
	                                if (jsonArrayItem_ealy != null && jsonArrayItem_ealy.length() > 0) {
	                                	task_Beans = JSON.parseArray(jsonArrayItem_ealy + "",Befriend_ReleaseHistroy_Task_Bean.class);
	                                }
	                                if(page_no.equals("1"))
	                                {
	                                 ACache.get(context).put(AppStrStatic.cache_key_query_history_release_task+A_0_App.USER_UNIQID, jsonObject
	                                         );
	                                }
	                                iCallBack.onSuccess(task_Beans);
	                            }else{
	                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
	                            }
	                        } catch (JSONException e) {
	                            iPubCallBack.onFailure(error_title[2]);
	                        }
	                    }

	                    @Override
	                    public void onError(Throwable error, boolean arg1) {
	                        onErrorBack(iPubCallBack, error, arg1);
	                    }
	                });
		}
		
		 /**
		 * 
		 * @Description: TODO(领取任务列表待处理接口)
		 */
		public interface InterAcquireTaskListMain {
			void onSuccess(List<Befriend_Acquire_Task_Bean> acquire_Task_Beans);
		}
		public void getAcquireTaskListMain(final Context context, String token, final InterAcquireTaskListMain iCallBack,final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put("command", "query_acquire_task_list");
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
	                new Callback.CommonCallback<String>() {
	                    @Override
	                    public void onCancelled(CancelledException arg0) {
	                        iPubCallBack.onCancelled();
	                    }

	                    @Override
	                    public void onFinished() {
	                        iPubCallBack.onFinished();
	                    }

	                    @Override
	                    public void onSuccess(String result) {
	                    	System.out.println("pp"+result);
	                        try {
	                            JSONObject jsonObject = new JSONObject(result);
	                            int state = jsonObject.optInt("status");
	                            String message = jsonObject.optString("msg");
	                            List<Befriend_Acquire_Task_Bean> task_Beans = new ArrayList<Befriend_Acquire_Task_Bean>();
	                            if (state == 1) {
	                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("list");
	                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
	                                	task_Beans = JSON.parseArray(jsonArrayItem_today + "",Befriend_Acquire_Task_Bean.class);
	                                }
	                                 ACache.get(context).put(AppStrStatic.cache_key_query_acquire_task+A_0_App.USER_UNIQID, jsonObject);
	                                iCallBack.onSuccess(task_Beans);
	                            }else{
	                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
	                            }
	                        } catch (JSONException e) {
	                            iPubCallBack.onFailure(error_title[2]);
	                        }
	                    }

	                    @Override
	                    public void onError(Throwable error, boolean arg1) {
	                        onErrorBack(iPubCallBack, error, arg1);
	                    }
	                });
		}
		
		 /**
		 * 
		 * @Description: TODO(领取任务列表历史接口)
		 */
		public interface InterAcquireHistoryTaskListMain {
			void onSuccess(List<Befriend_Acquire_Task_Bean> acquire_Task_Beans);
		}

		public void getAcquireHistoryTaskListMain(final Context context, String token,final String page_no, final InterAcquireHistoryTaskListMain iCallBack,final Inter_Call_Back iPubCallBack) {
			String url = A_0_App.SERVER_REQUEST_BASE_URL_BANGBANG;
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put("command", "query_acquire_task_history_list");
	        params.put("page_no", page_no);
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
	                new Callback.CommonCallback<String>() {
	                    @Override
	                    public void onCancelled(CancelledException arg0) {
	                        iPubCallBack.onCancelled();
	                    }

	                    @Override
	                    public void onFinished() {
	                        iPubCallBack.onFinished();
	                    }

	                    @Override
	                    public void onSuccess(String result) {
	                        try {
	                            JSONObject jsonObject = new JSONObject(result);
	                            int state = jsonObject.optInt("status");
	                            String message = jsonObject.optString("msg");
	                            List<Befriend_Acquire_Task_Bean> acquire_Task_Beans = new ArrayList<Befriend_Acquire_Task_Bean>();
	                            if (state == 1) {
	                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("list");
	                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
	                                	acquire_Task_Beans = JSON.parseArray(jsonArrayItem_today + "",Befriend_Acquire_Task_Bean.class);
	                                }
	                                if(page_no.equals("1"))
	                                {
	                                 ACache.get(context).put(AppStrStatic.cache_key_query_history_acquire_task+A_0_App.USER_UNIQID, jsonObject
	                                         );
	                                }
	                                iCallBack.onSuccess(acquire_Task_Beans);
	                            }else{
	                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
	                            }
	                        } catch (JSONException e) {
	                            iPubCallBack.onFailure(error_title[2]);
	                        }
	                    }

	                    @Override
	                    public void onError(Throwable error, boolean arg1) {
	                        onErrorBack(iPubCallBack, error, arg1);
	                    }
	                });
		}
	/******************************************** 帮帮 **********************************************************/
	
	 /**
     * 身边 考勤状态
     */
    public interface InterSideAttdenceStatus {
        void onSuccess();
    }

    public void SideAttdenceStatus(String atd_id,String user_id,String type,
            final InterSideAttdenceStatus iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/changeUserAtdStatus";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("atd_id", atd_id);
        params.put("user_id", user_id);
        params.put("type", type);
        params.put("token", A_0_App.USER_TOKEN);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                iCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
	
    /**
     * 身边 开始位置考勤
     */
    public interface InterSideBeginAttdence{
        void onSuccess();
    }

    public void SideBeginAttdence(String atd_id,String time,String lat,String lng,
            final InterSideAttdenceStatus iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/beginAttendance";
        String token=A_0_App.USER_TOKEN;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("atd_id", atd_id);
        params.put("atd_id", atd_id);
        params.put("time", time);
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                        	
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                iCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 身边 考勤有效列表
     */
    public interface InterSideSelectAttdenceTime{
        void onSuccess(List<Cpk_Attence_List_Time> attence_List_Times);
    }

    public void SideSelectAttdenceTime(final InterSideSelectAttdenceTime iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/selectValidAtdTime";
        String token=A_0_App.USER_TOKEN;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_version", PubMehods.getVerName(context));
        params.put("token", token);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                        	
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            List<Cpk_Attence_List_Time> list=new ArrayList<Cpk_Attence_List_Time>();
                            
                            if (state == 1) {
                            	list=JSON.parseArray(jsonObject.optString("list"), Cpk_Attence_List_Time.class);
                                iCallBack.onSuccess(list);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 身边 考勤图片上传
     */
    public interface InterUploadAttdenceImage {
        void onSuccess();
    }

    public void UploadAttdenceImage(String atd_id,String image_url,
            final InterUploadAttdenceImage iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/uploadAtdImage";
        String token=A_0_App.USER_TOKEN;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("atd_id", atd_id);
        params.put("image_url", image_url);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                iCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    
    /**
    /**
     * 
     * @Description: TODO(考勤个人信息)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InteAttdenceattencePersonal {
        void onSuccess(Cpk_Side_Attence_Personal side_Attence_Detail);
    }

    public void getAttdencePersonal(final Context context, String token,
            final String user_id, final InteAttdenceattencePersonal iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/getUserAtdStats";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put("user_id", user_id);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                            	Cpk_Side_Attence_Personal mlistContact= JSON.parseObject(result,
                            			Cpk_Side_Attence_Personal.class);
                                
                                ACache.get(context).put(
                                        AppStrStatic.cache_key_personal_statistics
                                        +A_0_App.USER_UNIQID+ user_id, jsonObject);
                                iCallBack.onSuccess(mlistContact);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

    
	/**
	 * 变更手机号
	 */
	public interface Inter_Change_Mobile {
		void onSuccess();
	}

	public void changge_moblile(String mobile, String code, String passwd,
			String token, final Inter_Change_Mobile interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/modifyMobile";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", mobile);
		params.put("code", code);
		params.put("passwd", passwd);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							} else {
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 变更密码
	 */
	public interface Inter_Change_Pwd {
		void onSuccess();
	}

	public void changge_Pwd(String newPasswd, String oldPasswd, String token,
			final Inter_Change_Pwd interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/modifyPasswd";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newPasswd", newPasswd);
		params.put("oldPasswd", oldPasswd);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 重置密码
	 */
	public interface Inter_Reset_Pwd {
		void onSuccess();
	}

	public void reset_Pwd(String passwd, String mobile,final Inter_Reset_Pwd interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/resetPasswd";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("passwd", passwd);
		params.put("mobile", mobile);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							} else {
								onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 意见反馈
	 */
	public interface Inter_FeedBack {
		void onSuccess(String message);
	}

	public void feedBack(String content, String token,
			final Inter_FeedBack interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/feedback";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("content", content);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess(message);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(获取学生信息接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterStudentInfo {
		void onSuccess(Cpk_Student_Info student);
	}

	public void getStudentInfo(final Context context, final String uniqid,
			String token, final InterStudentInfo iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/getNewInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqid", uniqid);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.print(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
                           
							if (state == 1) {
								Cpk_Student_Info mstudent = new Cpk_Student_Info();
								mstudent = JSON.parseObject(jsonObject + "", Cpk_Student_Info.class);
								mstudent.setResponse(jsonObject);
                                ACache.get(context).put(AppStrStatic.cache_key_my_account+uniqid, jsonObject);
                                ACache.get(context).put(AppStrStatic.cache_key_person_info+uniqid, jsonObject);
								iCallBack.onSuccess(mstudent);
							}else {
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(保存信息接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSavePerInfo {
		void onSuccess(String message,String user_status);
	}

	public void SaveUserInfo(final Context context,String recommend_phone, String name, String sex,
			String username, String student_number, String birthday,
			String politics, String school_id, String organ_id,
			String photo_url, String token, final InterSavePerInfo iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/saveInfo";
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("recommend_phone", recommend_phone);
		params.put("birthday", birthday);
		params.put("name", name);
		params.put("organ_id", organ_id);

		params.put("politics", politics);
		params.put("school_id", school_id);
		params.put("sex", sex);

		params.put("student_number", student_number);
		params.put("username", username);
		params.put("photo_url", photo_url);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								iCallBack.onSuccess(message,jsonObject.optString("user_status"));
							} else {
								iPubCallBack.onFailure(message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	public interface InterStuClassListAndChild {
		void onSuccess(List<Cpk_Persion_Contact> mList);
	}

	/**
	 * 
	 * @Title: getContactList
	 * @Description: TODO(获取融云需要的通讯录)
	 * @param @param context
	 * @param @param token
	 * @param @param iCallBack    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void getStuClassListAndChild(final Context context, String token,
			final InterStuClassListAndChild iCallBack,final Inter_Call_Back iPubCallBack) {

		JSONObject jsonObject = ACache.get(context).getAsJSONObject(
				"getSudentContactInfo");
		if (jsonObject != null) {
			try {
				JSONArray jsonArrayItem = jsonObject.optJSONArray("slist");
				List<Cpk_Persion_Contact> mlistContact = new ArrayList<Cpk_Persion_Contact>();
				if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
					for (int j = 0; j < jsonArrayItem.length(); j++) {
						Cpk_Persion_Contact contace = new Cpk_Persion_Contact();
						String name = jsonArrayItem.getJSONObject(j).optString(
								"name");
						contace.setName(name);
						contace.setPhone(jsonArrayItem.getJSONObject(j)
								.optString("phone"));
						contace.setPhoto_url(jsonArrayItem.getJSONObject(j)
								.optString("photo_url"));
						contace.setToken(jsonArrayItem.getJSONObject(j)
								.optString("token"));
						contace.setUniqid(jsonArrayItem.getJSONObject(j)
								.optString("uniqid"));
						contace.setUtype("0");
						mlistContact.add(contace);
					}
				}
				iCallBack.onSuccess(mlistContact);
			} catch (JSONException e) {
				iPubCallBack.onFailure(error_title[2]);
			}

			return;
		}
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicontacts/getAllStudentList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Persion_Contact> mlistContact = new ArrayList<Cpk_Persion_Contact>();
							if (state == 1) {
								ACache.get(context).put("getSudentContactInfo",
										jsonObject, 3600 * 24);
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("slist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Persion_Contact contace = new Cpk_Persion_Contact();
										String name = jsonArrayItem
												.getJSONObject(j).optString(
														"name");
										contace.setName(name);
										contace.setPhone(jsonArrayItem
												.getJSONObject(j).optString(
														"phone"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setToken(jsonArrayItem
												.getJSONObject(j).optString(
														"token"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));
										contace.setUtype("0");
										mlistContact.add(contace);
									}
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(学生通讯录列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterContactListStu {
		void onSuccess(List<Cpk_Persion_Contact> mList);
	}

	public void getContactStuList(final Context context, String token,
			final InterContactListStu iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicontacts/getContact";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Persion_Contact> mlistContact = new ArrayList<Cpk_Persion_Contact>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("clist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Persion_Contact contace = new Cpk_Persion_Contact();

										String name = jsonArrayItem
												.getJSONObject(j).optString(
														"name");
										contace.setName(name);
										contace.setPhone(jsonArrayItem
												.getJSONObject(j).optString(
														"phone"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setToken(jsonArrayItem
												.getJSONObject(j).optString(
														"token"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));
										contace.setUtype("0");
										contace.setIs_invite(jsonArrayItem
												.getJSONObject(j).optInt(
														"is_invite"));
										mlistContact.add(contace);
									}
									ACache.get(context).put(
											AppStrStatic.cache_key_colleague+A_0_App.USER_UNIQID,
											jsonObject);
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(通讯录老师列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterContactTeacherList {
		void onSuccess(List<Cpk_Persion_Contact> mList);
	}

	public void getContactTeacherList(final Context context, String token,
			final InterContactTeacherList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apicontacts/getTeacherContact";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Persion_Contact> mlistContact = new ArrayList<Cpk_Persion_Contact>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("tlist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Persion_Contact contace = new Cpk_Persion_Contact();

										String name = jsonArrayItem
												.getJSONObject(j).optString(
														"name");
										contace.setName(name);
										contace.setPhone(jsonArrayItem
												.getJSONObject(j).optString(
														"phone"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setToken(jsonArrayItem
												.getJSONObject(j).optString(
														"token"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));
										contace.setIs_invite(jsonArrayItem
												.getJSONObject(j).optInt(
														"is_invite"));
										contace.setUtype("0");
										mlistContact.add(contace);
									}
									ACache.get(context).put(
											AppStrStatic.cache_key_teacher+A_0_App.USER_UNIQID,
											jsonObject);
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(校内校外列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSchoolInOutList {
		void onSuccess(List<Cpk_School_In_And_Out> mList);
	}

	public void getSchoolInOutList(final Context context, String token,
			String type, final InterSchoolInOutList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicontacts/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("type", type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_School_In_And_Out> mlist = new ArrayList<Cpk_School_In_And_Out>();
							if (state == 1) {
								JSONArray parent = jsonObject
										.optJSONArray("clist");
								if (parent != null && parent.length() > 0) {
									for (int j = 0; j < parent.length(); j++) {
										Cpk_School_In_And_Out school_in = new Cpk_School_In_And_Out();
										school_in.setCategory_icon(parent
												.getJSONObject(j).optString(
														"category_icon"));
										school_in.setCategory_id(parent
												.getJSONObject(j).optString(
														"category_id"));
										school_in.setCategory_name(parent
												.getJSONObject(j).optString(
														"category_name"));
										school_in.setParent_id(parent
												.getJSONObject(j).optString(
														"parent_id"));
										JSONArray child = parent.getJSONObject(
												j).optJSONArray("info");
										List<Cpk_School_In_And_Out_Child> chileList = new ArrayList<Cpk_School_In_And_Out_Child>();
										if (child != null && child.length() > 0) {
											for (int i = 0; i < child.length(); i++) {
												Cpk_School_In_And_Out_Child child_in = new Cpk_School_In_And_Out_Child();
												child_in.setAddress(child
														.getJSONObject(i)
														.optString("address"));
												child_in.setCreate_time(child
														.getJSONObject(i)
														.optString(
																"create_time"));
												String name = child
														.getJSONObject(i)
														.optString("info_name");
												child_in.setInfo_name(name);
												// child_in.setInfo_icon(child.getJSONObject(i).optString("info_icon"));
												child_in.setPhone(child
														.getJSONObject(i)
														.optString("phone"));
												chileList.add(child_in);
											}
										}
										school_in.setList(chileList);
										mlist.add(school_in);
									}
									ACache.get(context).put(
											AppStrStatic.cache_key_school_in+A_0_App.USER_UNIQID,
											jsonObject);
								}
								iCallBack.onSuccess(mlist);
							} else if (state == 0) {
								iCallBack.onSuccess(mlist);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(校内校外列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSchoolOutList {
		void onSuccess(List<Cpk_School_In_And_Out_Child> mList);
	}

	public void getSchoolOutList(final Context context, String token,
			final String cate_id, final String page_no,
			final InterSchoolOutList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apicontacts/getOutsideSchool";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("cate_id", cate_id);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_School_In_And_Out_Child> mlist = new ArrayList<Cpk_School_In_And_Out_Child>();
							if (state == 1) {
								JSONArray parent = jsonObject.optJSONArray("clist");
								mlist=JSON.parseArray(parent +"", Cpk_School_In_And_Out_Child.class);
                                if (page_no.equals("1"))
                                {
                                    ACache.get(context).put(AppStrStatic.cache_key_out_school_click
                                                    + A_0_App.USER_UNIQID + cate_id, jsonObject);
                                }
							    iCallBack.onSuccess(mlist);
							} else if (state == 0) {
								iCallBack.onSuccess(mlist);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(校内校外列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSchoolOutAll {
		void onSuccess(List<Cpk_School_In_And_Out_Child> mList);
	}

	public void getSchoolOutAll(final Context context, String token,
			String keyword, String page_no, final InterSchoolOutAll iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apicontacts/getOutsideSchoolSearch";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("keyword", keyword);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_School_In_And_Out_Child> mlist = new ArrayList<Cpk_School_In_And_Out_Child>();
							if (state == 1) {
								JSONArray parent = jsonObject
										.optJSONArray("clist");
								for (int i = 0; i < parent.length(); i++) {
									Cpk_School_In_And_Out_Child child_in = new Cpk_School_In_And_Out_Child();
									child_in.setAddress(parent.getJSONObject(i)
											.optString("address"));
									child_in.setCreate_time(parent
											.getJSONObject(i).optString(
													"create_time"));
									String name = parent.getJSONObject(i)
											.optString("info_name");
									child_in.setInfo_name(name);
									child_in.setInfo_icon(parent.getJSONObject(
											i).optString("info_icon"));
									child_in.setPhone(parent.getJSONObject(i)
											.optString("phone"));
									child_in.setCate_id(parent.getJSONObject(i)
											.optString("cate_id"));
									mlist.add(child_in);
								}
								iCallBack.onSuccess(mlist);
							} else if (state == 0) {
								iCallBack.onSuccess(mlist);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(首页全局搜索)
	 * @author 
	 * @date 
	 */
	public interface InterMessSearchList {
		void onSuccess(List<Cpk_Search_Mess_Con> mess_Cons,List<Cpk_Search_Mess_Notice> mess_Notices,String userIsMore,String messageIsMore);
	}

	public void getMessSearchList(final Context context,final String token,final String type, String page_no, final String keyword, final InterMessSearchList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apisearch/index";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("keyword", keyword);
		params.put("type", type);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							System.out.println(token+"M"+result);
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String userIsMore="";
							String messageIsMore="";
							if (type.equals("0")) {
								userIsMore = jsonObject.optString("userIsMore");
							    messageIsMore = jsonObject.optString("messageIsMore");
							}
							List<Cpk_Search_Mess_Con> mess_Cons = new ArrayList<Cpk_Search_Mess_Con>();
							List<Cpk_Search_Mess_Notice> mess_Notices = new ArrayList<Cpk_Search_Mess_Notice>();
							if (state == 1) {
								if (type.equals("0")) {
									JSONArray jsonArrayItem = jsonObject.optJSONArray("userList");
									if (jsonArrayItem != null&& jsonArrayItem.length() > 0) {
										mess_Cons = JSON.parseArray(jsonArrayItem + "",Cpk_Search_Mess_Con.class);
										
									}
									JSONArray jsonArrayItem1 = jsonObject.optJSONArray("messageList");
									if (jsonArrayItem1 != null&& jsonArrayItem1.length() > 0) {
										mess_Notices = JSON.parseArray(jsonArrayItem1 + "",Cpk_Search_Mess_Notice.class);
										
									}
								} else if(type.equals("1")){
									JSONArray jsonArrayItem = jsonObject.optJSONArray("userList");
									if (jsonArrayItem != null&& jsonArrayItem.length() > 0) {
										mess_Cons = JSON.parseArray(jsonArrayItem + "",Cpk_Search_Mess_Con.class);
										
									}
								}else{
									JSONArray jsonArrayItem1 = jsonObject.optJSONArray("messageList");
									System.out.println("KK"+jsonArrayItem1);
									if (jsonArrayItem1 != null&& jsonArrayItem1.length() > 0) {
										mess_Notices = JSON.parseArray(jsonArrayItem1 + "",Cpk_Search_Mess_Notice.class);
										
									}
								}
								
								iCallBack.onSuccess(mess_Cons,mess_Notices,userIsMore,messageIsMore);
							} else if (state == 0) {
								iCallBack.onSuccess(mess_Cons,mess_Notices,userIsMore,messageIsMore);
							} else {
								onFailureBack(context, jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(首页通知信息接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterIndexNoticeMess {
		void onSuccess(long time,List<Cpk_Index_Notice_Message> mList);
	}

	public void getIndexNoticeMess(final Context context, String token,boolean isFirstRequest,
			final InterIndexNoticeMess iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apimessage/getNewIndexMessage";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app_version", PubMehods.getVerName(context));
		params.put("token", token);
        String temp_Values ="";
        if(isFirstRequest){
            temp_Values = "1";
        }else{
            temp_Values = "0";
        }
        params.put("isFirstRequest", temp_Values);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long time=jsonObject.optLong("time");
							
                            boolean tag_assistant = false;
                            JSONObject jsonObject_Assistant = jsonObject.optJSONObject("assistantMsg");
                            if (jsonObject_Assistant != null) {
                                String judgeTemp = jsonObject_Assistant.optString("title", "");
                                if (judgeTemp != null && !judgeTemp.equals("") && !judgeTemp.equals("null")) {
                                    tag_assistant = true;
                                } else {
                                    tag_assistant = false;
                                }
                            }
                            JSONObject jsonObject_Official = jsonObject.optJSONObject("systemMsg");
                            JSONObject jsonObject_Attdence = jsonObject.optJSONObject("attendance");
                            
							List<Cpk_Index_Notice_Message> mlistContact = new ArrayList<Cpk_Index_Notice_Message>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject.optJSONArray("msg");
								if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
								    mlistContact=JSON.parseArray(jsonObject.optJSONArray("msg")+"",Cpk_Index_Notice_Message.class);
                                    if (mlistContact.size() > 0 && jsonObject_Assistant != null && tag_assistant) {
                                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_ASSISTANT);
                                        message_one.setMsg_name(context.getResources().getString(R.string.str_school_assistant));
                                        message_one.setCreate_time(jsonObject_Assistant.optString("create_time",""));
                                        message_one.setCount(jsonObject_Assistant.optString("count",""));
                                        message_one.setApp_msg_sign("");//消息来源
                                        message_one.setTitle(jsonObject_Assistant.optString("title",""));
                                        mlistContact.add(message_one);
									}
                                    if(mlistContact.size() > 0 && jsonObject_Official != null){
                                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_OFFICIAL);
                                        message_one.setMsg_name(context.getResources().getString(R.string.app_name));
                                        message_one.setCreate_time(jsonObject_Official.optString("create_time",""));
                                        message_one.setCount(jsonObject_Official.optString("count",""));
                                        message_one.setApp_msg_sign("");//消息来源
                                        message_one.setTitle(jsonObject_Official.optString("title",""));
                                        mlistContact.add(message_one);
                                    }
                                    if(mlistContact.size() > 0 && jsonObject_Attdence != null){
                                        Cpk_Index_Notice_Message message_one = new Cpk_Index_Notice_Message();
                                        message_one.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_ATTENCE);
                                        message_one.setMsg_name(context.getResources().getString(R.string.str_attence_title));
                                        message_one.setCreate_time(jsonObject_Attdence.optString("create_time",""));
                                        message_one.setCount(jsonObject_Attdence.optString("atd_count",""));
                                        message_one.setApp_msg_sign("");
                                        message_one.setTitle(jsonObject_Attdence.optString("title",""));
                                        mlistContact.add(message_one);
                                    }
                                    if (mlistContact.size() > 0 && A_0_App.USER_STATUS.equals("2")) {// 加入群聊
                                        Cpk_Index_Notice_Message qunliao = new Cpk_Index_Notice_Message();
                                        qunliao.setMessage_id(AppStrStatic.TAG_MESSAGE_ID_GROUP);
                                        qunliao.setMsg_name(context.getResources().getString(R.string.str_group_chat));
                                        qunliao.setApp_msg_sign(context.getResources().getString(R.string.str_no_message_chat));
                                        qunliao.setCount("0");
                                        qunliao.setCreate_time("");
                                        qunliao.setTitle("");
                                        qunliao.setTargetId(A_0_App.USER_QUNIQID);
                                        qunliao.setConversationtype(ConversationType.GROUP + "");
                                        mlistContact.add(qunliao);
                                    }
                                    ACache.get(context).put(AppStrStatic.cache_key_my_message + A_0_App.USER_UNIQID
                                                    + A_0_App.USER_STATUS, jsonObject);
								}
								iCallBack.onSuccess(time,mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(time,mlistContact);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(通知列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterRongYunName {
		void onSuccess(List<Cpk_RongYun_True_Name> mList);
	}

	public void getRongYunName(final Context context, String ids, String token,
			final InterRongYunName iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getUniqids";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_RongYun_True_Name> mlistContact = new ArrayList<Cpk_RongYun_True_Name>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("ulist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_RongYun_True_Name contace = new Cpk_RongYun_True_Name();
										contace.setName(jsonArrayItem
												.getJSONObject(j).optString(
														"name"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setTargetId(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));
                                        contace.setIs_delete(jsonArrayItem.getJSONObject(j).optString(
                                                "is_delete"));
										mlistContact.add(contace);
									}
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 批量更新
	 */
	public interface InterUpdateList {
		void onSuccess();
	}

	public void updateNoticeInfo(String level, String token,
			final InterUpdateList interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apimessage/batchUpdateMsgStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("level", level);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
     * 批量更新官方消息
     */
    public interface InterUpdateOfficialList {
        void onSuccess();
    }

    public void updateNoticeOfficialInfo(String token,
            final InterUpdateOfficialList interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/batchUpdateSystemStatus";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                interCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

    //批量更新校务助手信息
    public void updateSchool_AssistantInfo(String token,String level,
            final Inter_UpLoad_ChannelId interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiassistantmsg/batchRead";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("level", level);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                interCallBack.onSuccess(message);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

	/**
	 * 
	 * @Description: TODO(通知列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterNoticeList {
		void onSuccess(List<Cpk_Notice_List> mList,int unreceipt_num);
	}

	public void getNoticeList(final Context context, String token,
			final String level, final String page_no,
			final InterNoticeList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("level", level);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject( result);							
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							int unreceipt_num = jsonObject.optInt("unreceipt_num");
							List<Cpk_Notice_List> mlistContact = new ArrayList<Cpk_Notice_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("mlist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Notice_List contace = new Cpk_Notice_List();
										contace.setMl_status(jsonArrayItem
												.getJSONObject(j).optString(
														"ml_status"));
										contace.setLog_id(jsonArrayItem
												.getJSONObject(j).optString(
														"log_id"));
										contace.setMessage_id(jsonArrayItem
												.getJSONObject(j).optString(
														"message_id"));
										contace.setApp_msg_level(jsonArrayItem
												.getJSONObject(j).optString(
														"app_msg_level"));
										contace.setApp_msg_sign(jsonArrayItem
												.getJSONObject(j).optString(
														"app_msg_sign"));

										contace.setBg_img(jsonArrayItem
												.getJSONObject(j).optString(
														"bg_img"));
										contace.setContent(jsonArrayItem
												.getJSONObject(j).optString(
														"content"));
										contace.setDesc(jsonArrayItem
												.getJSONObject(j).optString(
														"desc"));
										contace.setRead_num(jsonArrayItem
												.getJSONObject(j).optString(
														"read_num"));
										contace.setReply_num(jsonArrayItem
												.getJSONObject(j).optString(
														"reply_num"));

										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));
										contace.setType(jsonArrayItem
												.getJSONObject(j).optString(
														"type"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));
										contace.setIs_default(jsonArrayItem
												.getJSONObject(j).optString(
														"is_default"));
										contace.setIs_new(jsonArrayItem
												.getJSONObject(j).optString(
														"is_new"));
										contace.setMessage_receipt(jsonArrayItem
												.getJSONObject(j).optInt(
														"message_receipt"));
										contace.setIs_receipt(jsonArrayItem
												.getJSONObject(j).optInt(
														"is_receipt"));
										contace.setIs_appendix(jsonArrayItem
												.getJSONObject(j).optInt(
														"is_appendix"));
										mlistContact.add(contace);
									}
									
								}
								if (page_no.equals("1")) {
									ACache.get(context)
											.put(AppStrStatic.cache_key_notice_list+A_0_App.USER_UNIQID
													+ level, jsonObject);
								}
								iCallBack.onSuccess(mlistContact,unreceipt_num);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact,unreceipt_num);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
		
	   /**
     * 
     * @Description: TODO(官方通知列表接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InterNoticeOfficialList {
        void onSuccess(List<Cpk_Notice_List> mList);
    }

    public void getNoticeOfficialList(final Context context, String token, final String page_no,
            final InterNoticeOfficialList iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getSystemList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("page_no", page_no);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            List<Cpk_Notice_List> mlistContact = new ArrayList<Cpk_Notice_List>();
                            if (state == 1) {
                                JSONArray jsonArrayItem = jsonObject
                                        .optJSONArray("mlist");
                                if (jsonArrayItem != null
                                        && jsonArrayItem.length() > 0) {
                                    for (int j = 0; j < jsonArrayItem.length(); j++) {
                                        Cpk_Notice_List contace = new Cpk_Notice_List();
                                        contace.setMl_status(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "ml_status"));
                                        contace.setLog_id(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "log_id"));
                                        contace.setMessage_id(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "message_id"));
                                        contace.setApp_msg_level(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "app_msg_level"));
                                        contace.setApp_msg_sign(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "app_msg_sign"));

                                        contace.setBg_img(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "bg_img"));
                                        contace.setContent(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "content"));
                                        contace.setDesc(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "desc"));
                                        contace.setRead_num(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "read_num"));
                                        contace.setReply_num(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "reply_num"));

                                        contace.setTitle(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "title"));
                                        contace.setType(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "type"));
                                        contace.setCreate_time(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "create_time"));
                                        contace.setIs_default(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "is_default"));
                                        contace.setIs_new(jsonArrayItem
                                                .getJSONObject(j).optString(
                                                        "is_new"));
                                        mlistContact.add(contace);
                                    }
                                    
                                }
                                if (page_no.equals("1")) {
                                    ACache.get(context)
                                            .put(AppStrStatic.cache_key_official_list+A_0_App.USER_UNIQID, jsonObject);
                                }
                                iCallBack.onSuccess(mlistContact);
                            } else if (state == 0) {
                                iCallBack.onSuccess(mlistContact);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 
     * @Description: TODO(校务列表列表接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InterSchoolAssistantMainList {
        void onSuccess(List<Cpk_School_Assistant_Main> mList,long serverTime);
    }

    public void getSchoolAssistantMain(final Context context, String token,
            final String page_no, final InterSchoolAssistantMainList iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiassistantmsg/getNewestInfo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("page_no", page_no);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            long serverTime=jsonObject.getLong("time");
                            List<Cpk_School_Assistant_Main> mlistContact = new ArrayList<Cpk_School_Assistant_Main>();
                            if (state == 1) {
                                JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
                                if (jsonArrayItem != null&& jsonArrayItem.length() > 0) {
                                    mlistContact = JSON.parseArray(jsonArrayItem+"", Cpk_School_Assistant_Main.class);
                                    if (page_no.equals("1")) {
                                        ACache.get(context).put(AppStrStatic.cache_key_mess_school_assistant_main + 
                                                A_0_App.USER_UNIQID,jsonObject);
                                    }
                                }
                                iCallBack.onSuccess(mlistContact,serverTime);
                            } else if (state == 0) {
                                iCallBack.onSuccess(mlistContact,serverTime);
                            } else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
  * 
  * @Description: TODO(首页校务助手列表接口)
  * @author Jiaohaili
  * @date 2015年11月26日 下午4:24:20
  */
 public interface InterSchoolAssistantList {
     void onSuccess(List<Cpk_School_Assistant_List> mList);
 }

 public void getSchoolAssistantList(final Context context,final String type, String token, final String page_no,
         final InterSchoolAssistantList iCallBack,final Inter_Call_Back iPubCallBack) {
     String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiassistantmsg/getList";
     Map<String, Object> params = new HashMap<String, Object>();
     params.put("type", type);
     params.put("token", token);
     params.put("page_no", page_no);
     params.put("app_version", PubMehods.getVerName(context));
     params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
     HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
             new Callback.CommonCallback<String>() {
                 @Override
                 public void onCancelled(CancelledException arg0) {
                     iPubCallBack.onCancelled();
                 }

                 @Override
                 public void onFinished() {
                     iPubCallBack.onFinished();
                 }

                 @Override
                 public void onSuccess(String result) {
                     try {
                         JSONObject jsonObject = new JSONObject(
                                 result);
                         int state = jsonObject.optInt("status");
                         String message = jsonObject.optString("msg");
                         List<Cpk_School_Assistant_List> mlistContact = new ArrayList<Cpk_School_Assistant_List>();
                         if (state == 1) {
                             JSONArray jsonArrayItem = jsonObject
                                     .optJSONArray("mlist");
                             if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                 mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_School_Assistant_List.class);
                             }
                             if (page_no.equals("1")) {
                                 ACache.get(context).put(AppStrStatic.cache_key_schoolasstistant_list + type + A_0_App.USER_UNIQID, jsonObject);
                             }
                             iCallBack.onSuccess(mlistContact);
                         } else if (state == 0) {
                             iCallBack.onSuccess(mlistContact);
                         }else{
                             onFailureBack(context,jsonObject, iPubCallBack, state, message);
                         }
                     } catch (JSONException e) {
                         iPubCallBack.onFailure(error_title[2]);
                     }
                 }

                 @Override
                 public void onError(Throwable error, boolean arg1) {
                     onErrorBack(iPubCallBack, error, arg1);
                 }
             });
 }
 
	/**
	 * 首页通知删除
	 */
	public interface InterNoticeDelete {
		void onSuccess();
	}

	public void NoticeDelete(String token,String messageIds,
			final InterNoticeDelete interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/deleteMessage";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("messageIds", messageIds);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
     * 首页官方通知删除
     */
    public interface InterNoticeOfficalDelete {
        void onSuccess();
    }

    public void NoticeOfficalDelete(String token,String messageIds,
            final InterNoticeOfficalDelete interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/deleteSystemMessage";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("messageIds", messageIds);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                interCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
	
	/**
	 * 
	 * @Description: TODO(通知详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterNoticeDetail {
		void onSuccess(Cpk_Notice_Detail notice,long time);
	}

	public void getNoticeDetail(final Context context, String school_notice,String token,
			final String message_id, final String notice_deatil_type,String is_share,
			final InterNoticeDetail iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("message_id", message_id);
		params.put("assistant_message_id", school_notice);
		
		params.put("is_share", is_share);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								long time=jsonObject.optLong("time");
					            JSONObject dd = jsonObject.getJSONObject("info");
					            Cpk_Notice_Detail notice = new Cpk_Notice_Detail();
					            notice=JSON.parseObject(dd+"", Cpk_Notice_Detail.class);  
								JSONArray jsonArrayItem = jsonObject.optJSONArray("comment");
								List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
								if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
								    mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Comment_detail.class);
								}
                                if(notice_deatil_type.equals(AppStrStatic.cache_key_notice_detail_text+A_0_App.USER_UNIQID))
                                {
                                  ACache.get(context).put(AppStrStatic.cache_key_notice_detail_text+A_0_App.USER_UNIQID+message_id, jsonObject);
                                }else if(notice_deatil_type.equals(AppStrStatic.cache_key_mess_detail+A_0_App.USER_UNIQID))
                                {
                                  ACache.get(context).put(AppStrStatic.cache_key_mess_detail+A_0_App.USER_UNIQID+message_id, jsonObject);
                                }
								notice.setList(mlistContact);
								
								iCallBack.onSuccess(notice,time);
							
									
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

    /*
    * @Description: TODO(校务助手详情接口)
    * @author Jiaohaili
    * @date 2015年11月26日 下午4:24:20
    */
   public interface InterNoticeSchoolAssistantDetail {
       void onSuccess(Cpk_School_Assistant_detail notice);
   }

   public void getNoticeSchoolAssistantDetail(final Context context, String token,
           final String message_id,final InterNoticeSchoolAssistantDetail iCallBack,final Inter_Call_Back iPubCallBack) {
       String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiassistantmsg/getInfo";
       Map<String, Object> params = new HashMap<String, Object>();
       params.put("token", token);
       params.put("message_id", message_id);
       params.put("app_version", PubMehods.getVerName(context));
       params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

       HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
               new Callback.CommonCallback<String>() {
                   @Override
                   public void onCancelled(CancelledException arg0) {
                       iPubCallBack.onCancelled();
                   }

                   @Override
                   public void onFinished() {
                       iPubCallBack.onFinished();
                   }

                   @Override
                   public void onSuccess(String result) {
                       try {
                           JSONObject jsonObject = new JSONObject(
                                   result);
                           int state = jsonObject.optInt("status");
                           String message = jsonObject.optString("msg");
                            if (state == 1) {
                                JSONObject dd = jsonObject.optJSONObject("info");
                                Cpk_School_Assistant_detail notice = new Cpk_School_Assistant_detail();
                                notice = JSON.parseObject(dd + "",Cpk_School_Assistant_detail.class);
//                                List<Cpk_User_Values> list_values = new ArrayList<Cpk_User_Values>();
//                                list_values = JSON.parseArray(dd.optJSONArray("userInfo") + "", Cpk_User_Values.class);
//                                notice.setUser_values(list_values);
                                ACache.get(context).put(AppStrStatic.cache_key_schoolassistant_detail_text +
                                                A_0_App.USER_UNIQID + message_id, jsonObject);
                               iCallBack.onSuccess(notice);
                                   
                           }else{
                               onFailureBack(context,jsonObject, iPubCallBack, state, message);
                           }
                       } catch (JSONException e) {
                           iPubCallBack.onFailure(error_title[2]);
                       }
                   }

                   @Override
                   public void onError(Throwable error, boolean arg1) {
                       onErrorBack(iPubCallBack, error, arg1);
                   }
               });
   }
    
    /**
     * 接受委派
     */
    public interface InterAcceptTheDelegate {
        void onSuccess(String msg);
    }

    public void acceptTheDelegate(String message_id,String post_id, String token,
            final InterAcceptTheDelegate interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendhelp/postAccept";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("post_id", post_id);
        params.put("message_id", message_id);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                interCallBack.onSuccess(message);
                            } else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }


        /**
      * 
      * @Description: TODO(官方通知详情接口)
      * @author Jiaohaili
      * @date 2015年11月26日 下午4:24:20
      */
     public interface InterNoticeOfficialDetail {
         void onSuccess(Cpk_Notice_Detail notice);
     }
    
     public void getNoticeOfficialDetail(final Context context, String token,
             final String message_id,final InterNoticeOfficialDetail iCallBack,final Inter_Call_Back iPubCallBack) {
         String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getSystemInfo";
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("token", token);
         params.put("message_id", message_id);
         params.put("app_version", PubMehods.getVerName(context));
         params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
    
         HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                 new Callback.CommonCallback<String>() {
                     @Override
                     public void onCancelled(CancelledException arg0) {
                         iPubCallBack.onCancelled();
                     }
    
                     @Override
                     public void onFinished() {
                         iPubCallBack.onFinished();
                     }
    
                     @Override
                     public void onSuccess(String result) {
                         try {
                             
                             JSONObject jsonObject = new JSONObject(
                                     result);
                             int state = jsonObject.optInt("status");
                             String message = jsonObject.optString("msg");
                             if (state == 1) {
                                 JSONObject dd = jsonObject.getJSONObject("info");
                                 Cpk_Notice_Detail notice = new Cpk_Notice_Detail();
                                 notice=JSON.parseObject(dd+"", Cpk_Notice_Detail.class);  
                                 JSONArray jsonArrayItem = jsonObject.optJSONArray("comment");
                                 List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
                                 if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                     mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Comment_detail.class);
                                 }
                                 ACache.get(context).put(AppStrStatic.cache_key_notice_official_detail_text+A_0_App.USER_UNIQID+message_id, jsonObject);
                                 notice.setList(mlistContact);
                                 
                                 iCallBack.onSuccess(notice);
                             
                                     
                             }else{
                                 onFailureBack(context,jsonObject, iPubCallBack, state, message);
                             }
                         } catch (JSONException e) {
                             iPubCallBack.onFailure(error_title[2]);
                         }
                     }
    
                     @Override
                     public void onError(Throwable error, boolean arg1) {
                         onErrorBack(iPubCallBack, error, arg1);
                     }
                 });
     }
     
	/**
	 * 添加活动评论
	 */
	public interface InterAdd_Notice_Comment {
		void onSuccess();
	}

	public void add_Notice_Detail_Comment(String message_id, String log_id,
			String token, String content,
			final InterAdd_Notice_Comment interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/addComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("message_id", message_id);
		params.put("token", token);
		params.put("content", content);
		params.put("log_id", log_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(通知评论列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterNoticeCommentList {
		void onSuccess(List<Cpk_Comment_detail> mList);
	}

	public void getNoticeCommentList(final Context context, String token,
			String message_id, String page_no,
			final InterNoticeCommentList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apimessage/getCommentList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("message_id", message_id);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("clist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Comment_detail contace = new Cpk_Comment_detail();
										contace.setMessage_id(jsonArrayItem
												.getJSONObject(j).optString(
														"message_id"));
										contace.setUser_id(jsonArrayItem
												.getJSONObject(j).optString(
														"user_id"));
										contace.setContent(jsonArrayItem
												.getJSONObject(j).optString(
														"content"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));

										contace.setUsername(jsonArrayItem
												.getJSONObject(j).optString(
														"username"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setName(jsonArrayItem
												.getJSONObject(j).optString(
														"name"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));

										mlistContact.add(contace);
									}
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(评论活动列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterCommentList {
		void onSuccess(List<Cpk_Comment_detail> mList);
	}

	public void getCommentList(final Context context, String token,
			String article_id, String page_no, final InterCommentList iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apiactivity/getCommentList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("article_id", article_id);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("clist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {

									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Comment_detail contace = new Cpk_Comment_detail();
										contace.setArticle_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setUser_id(jsonArrayItem
												.getJSONObject(j).optString(
														"user_id"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));
										contace.setContent(jsonArrayItem
												.getJSONObject(j).optString(
														"content"));

										contace.setUsername(jsonArrayItem
												.getJSONObject(j).optString(
														"username"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));
										contace.setName(jsonArrayItem
												.getJSONObject(j).optString(
														"name"));
										contace.setReply_comment_info(jsonArrayItem
												.getJSONObject(j).optString(
														"reply_comment_info"));
										contace.setIsLiked(jsonArrayItem
												.getJSONObject(j).optString(
														"isLiked"));
										contace.setTo_comment_id(jsonArrayItem
												.getJSONObject(j).optString(
														"to_comment_id"));
										contace.setLike_count(jsonArrayItem
												.getJSONObject(j).optString(
														"like_count"));
										contace.setComment_id(jsonArrayItem
												.getJSONObject(j).optString(
														"comment_id"));
										contace.setStorey(jsonArrayItem
												.getJSONObject(j).optString(
														"storey"));
										contace.setSchool_name(jsonArrayItem
												.getJSONObject(j).optString(
														"school_name"));
										mlistContact.add(contace);
									}
								
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(讲座列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterLectureList {
		void onSuccess(List<Cpk_Side_Lectures_List> mList,long servertime);
	}

	public void getLectureList(final Context context, String token,
			final String page_no, final InterLectureList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilecture/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							List<Cpk_Side_Lectures_List> mlistContact = new ArrayList<Cpk_Side_Lectures_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("llist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Side_Lectures_List contace = new Cpk_Side_Lectures_List();
										contace.setArticle_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setAuthor(jsonArrayItem
												.getJSONObject(j).optString(
														"author"));
										contace.setAuthor_desc(jsonArrayItem
												.getJSONObject(j).optString(
														"author_desc"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"create_time"));
										contace.setEnd_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"end_time"));

										contace.setPlace(jsonArrayItem
												.getJSONObject(j).optString(
														"place"));
										contace.setRead_num(jsonArrayItem
												.getJSONObject(j).optString(
														"read_num"));
										contace.setStart_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"start_time"));
										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));

										mlistContact.add(contace);
									}
									if (page_no.equals("1")) {
										ACache.get(context).put(
												AppStrStatic.cache_key_lecture+A_0_App.USER_UNIQID,
												jsonObject);
									}
								}

								iCallBack.onSuccess(mlistContact,servertime);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact,servertime);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(讲座详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterLectureDetail {
		void onSuccess(Cpk_Side_Lectures_Detail detail);
	}

	public void getLectureDetail(final Context context, String token,
			final String article_id, final InterLectureDetail iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilecture/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("article_id", article_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								Cpk_Side_Lectures_Detail contace = new Cpk_Side_Lectures_Detail();
								contace.setArticle_id(jsonObject
										.optString("article_id"));
								contace.setAuthor(jsonObject
										.optString("author"));
								contace.setAuthor_desc(jsonObject
										.optString("author_desc"));
								contace.setContent(jsonObject
										.optString("content"));
								contace.setCreate_time(jsonObject
										.getLong("create_time"));

								contace.setLecture_time(jsonObject
										.optString("lecture_time"));
								contace.setPlace(jsonObject.optString("place"));
								contace.setRead_num(jsonObject
										.optString("read_num"));
								contace.setTitle(jsonObject.optString("title"));
								ACache.get(context).put(
										AppStrStatic.cache_key_lecture_detail+A_0_App.USER_UNIQID
												+ article_id, jsonObject);
								iCallBack.onSuccess(contace);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(轮播图接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterBannerList {
		void onSuccess(List<Cpk_Banner_Mode> mList);
	}

	public void getBannerList(final Context context, String token,
			final InterBannerList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/getAdv";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Banner_Mode> mlistContact = new ArrayList<Cpk_Banner_Mode>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("alist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Banner_Mode contace = new Cpk_Banner_Mode();
										contace.setId(jsonArrayItem
												.getJSONObject(j).optString(
														"id"));
										contace.setImage_url(jsonArrayItem
												.getJSONObject(j).optString(
														"image_url"));
										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));
										contace.setType(jsonArrayItem
												.getJSONObject(j).optString(
														"type"));
										contace.setUrl(jsonArrayItem
												.getJSONObject(j).optString(
														"url"));

										mlistContact.add(contace);
									}
									ACache.get(context).put(
											AppStrStatic.cache_key_mySide+A_0_App.USER_UNIQID,
											jsonObject);
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(活动评论列表)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterAcyCommList {
		void onSuccess(List<Cpk_Comment_detail> list);
	}

	public void getAcyCommList(final Context context, String token,
			String article_id, String page_no, final InterAcyCommList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL
				+ "Apiactivity/getCommentList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("article_id", article_id);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Comment_detail> mlistContact = new ArrayList<Cpk_Comment_detail>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("clist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Comment_detail contace = new Cpk_Comment_detail();
										contace.setMessage_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setContent(jsonArrayItem
												.getJSONObject(j).optString(
														"content"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));
										contace.setName(jsonArrayItem
												.getJSONObject(j).optString(
														"name"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"pic_url"));

										contace.setUser_id(jsonArrayItem
												.getJSONObject(j).optString(
														"user_id"));
										contace.setUsername(jsonArrayItem
												.getJSONObject(j).optString(
														"username"));

										mlistContact.add(contace);
									}
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 活动报名
	 */
	public interface InterAcy_Apply {
		void onSuccess();
	}

	public void get_Acy_Apply(String article_id, String token,
			final InterAcy_Apply interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/join";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("article_id", article_id);
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 添加活动评论
	 */
	public interface InterAdd_Acy_Comment {
		void onSuccess();
	}

	public void add_Acy_Comment(String to_comment_id,String article_id, String token,
			String content, final InterAdd_Acy_Comment interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/addComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("article_id", article_id);
		params.put("token", token);
		params.put("content", content);
		params.put("to_comment_id", to_comment_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 活动评论点赞
	 */
	public interface InterInfo_Ac_Comment {
		void onSuccess();
	}

	public void get_Info_Ac_Comment(String comment_id, String token,String like_type,
			final InterInfo_Ac_Comment interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/commentLikeHandle";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("comment_id", comment_id);
		params.put("token", token);
		params.put("like_type", like_type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	

	/**
	 * 
	 * @Description: TODO(活动详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
		public interface InterAcyDetail {
			void onSuccess(Cpk_Acy_Detail notice,String isJoin, String isStop,String time);
		}

       public void getAcyDetail(final Context context, String token,final String article_id,final InterAcyDetail iCallBack
               ,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("article_id", article_id);
 		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String isJoin = jsonObject.optString("isJoin");
							String isStop = jsonObject.optString("isStop");
							String time = jsonObject.optString("time");
							if (state == 1) {
								JSONObject dd = jsonObject.getJSONObject("info");
								Cpk_Acy_Detail notice = new Cpk_Acy_Detail();
								notice=JSON.parseObject(dd+"", Cpk_Acy_Detail.class);
								JSONArray jsonArrayItem = jsonObject.optJSONArray("comment");
								List<Cpk_Comment_detail> mlistContact= new ArrayList<Cpk_Comment_detail>();
								if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
									mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Comment_detail.class);
								}
								ACache.get(context).put(AppStrStatic.cache_key_acy_detail+A_0_App.USER_UNIQID+article_id, jsonObject);
								notice.setList(mlistContact);
								iCallBack.onSuccess(notice,isJoin,isStop,time);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 
	 * @Description: TODO(活动列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterAcyList {
		void onSuccess(List<Cpk_Acy_list> mList,long serverTime);
	}

	public void getAcyList(final Context context, String token,
			final String page_no, final InterAcyList iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiactivity/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long serverTime=jsonObject.getLong("time");
							List<Cpk_Acy_list> mlistContact = new ArrayList<Cpk_Acy_list>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("alist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Acy_list contace = new Cpk_Acy_list();
										contace.setArticle_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setJoin_num(jsonArrayItem
												.getJSONObject(j).optString(
														"join_num"));
										contace.setComment_num(jsonArrayItem
												.getJSONObject(j).optString(
														"comment_num"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));
										contace.setJoin_end_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"join_end_time"));

										contace.setBg_img(jsonArrayItem
												.getJSONObject(j).optString(
														"bg_img"));
										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));
                                        contace.setStart_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"start_time"));
                                        contace.setEnd_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"end_time"));
										contace.setShare_url(jsonArrayItem
												.getJSONObject(j).optString(
														"share_url"));
										mlistContact.add(contace);
									}
									if (page_no.equals("1")) {
										ACache.get(context).put(
												AppStrStatic.cache_key_acy+A_0_App.USER_UNIQID,
												jsonObject);
									}
								}

								iCallBack.onSuccess(mlistContact,serverTime);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact,serverTime);
							} else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(活动列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterGroupInfo {
		void onSuccess(Cpk_Group_Info info);
	}

	public void getGroupInfo(final Context context, String token,
			String uniqid, final InterGroupInfo iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirong/getGroupInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("uniqid", uniqid);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								Cpk_Group_Info info = new Cpk_Group_Info();
								info.setClassname(jsonObject
										.optString("classname"));
								info.setCounsellor(jsonObject
										.optString("counsellor"));

								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("rlist");
								List<Cpk_Group_Info_item> mlistContact = new ArrayList<Cpk_Group_Info_item>();
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Group_Info_item contace = new Cpk_Group_Info_item();
										contace.setName(jsonArrayItem
												.getJSONObject(j).optString(
														"name"));
										contace.setPhone(jsonArrayItem
												.getJSONObject(j).optString(
														"phone"));
										contace.setPhoto_url(jsonArrayItem
												.getJSONObject(j).optString(
														"photo_url"));
										contace.setRtoken(jsonArrayItem
												.getJSONObject(j).optString(
														"rtoken"));
										contace.setUsername(jsonArrayItem
												.getJSONObject(j).optString(
														"username"));
										contace.setUniqid(jsonArrayItem
												.getJSONObject(j).optString(
														"uniqid"));

										mlistContact.add(contace);
									}
									ACache.get(context).put(
											AppStrStatic.cache_key_group_info+A_0_App.USER_UNIQID,
											jsonObject);
								}

								info.setMlist(mlistContact);
								iCallBack.onSuccess(info);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(失物招领列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterLostList {
		void onSuccess(List<Cpk_Side_Lost_List> mList);
	}

	public void getLostList(final Context context, final String type, String token,
			final String page_no, final InterLostList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("type", type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Lost_List> mlistContact = new ArrayList<Cpk_Side_Lost_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("llist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(
											jsonArrayItem + "",
											Cpk_Side_Lost_List.class);
									if (page_no.equals("1")) {
										if (type.equals("2")) {
											ACache.get(context).put(
													AppStrStatic.cache_key_side_found_found+A_0_App.USER_UNIQID,
													jsonObject);
										} else if(type.equals("1")){
											ACache.get(context).put(
													AppStrStatic.cache_key_side_found_acy+A_0_App.USER_UNIQID,
													jsonObject);
										}else{
											ACache.get(context).put(
													AppStrStatic.cache_key_side_found_my+A_0_App.USER_UNIQID,
													jsonObject);
										}
										
									}
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(转发失物招领详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InteFoundDetail {
		void onSuccess(Cpk_Side_Lost_List cpk_Side_Lost_List);
	}

	public void getFoundDetail(final Context context, String token,
			final String lost_id, final InteFoundDetail iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put("lost_id", lost_id);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							Cpk_Side_Lost_List mlistContact = new Cpk_Side_Lost_List();
							if (state == 1) {
								Cpk_Side_Lost_List mlistContact1 = JSON.parseObject(result,
										Cpk_Side_Lost_List.class);
								
								mlistContact=JSON.parseObject(mlistContact1.getInfo(),Cpk_Side_Lost_List.class);
								iCallBack.onSuccess(mlistContact);
								
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(失物招领列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterLostSearchList {
		void onSuccess(List<Cpk_Side_Lost_List> mList);
	}

	public void getLostSearchList(final Context context,  String token,
			final String page_no,final String keyword, final InterLostSearchList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("keyword", keyword);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Lost_List> mlistContact = new ArrayList<Cpk_Side_Lost_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("llist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(
											jsonArrayItem + "",
											Cpk_Side_Lost_List.class);
									
								}
								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(成绩列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterScoreList {
		void onSuccess(List<Cpk_Side_Score_List> mList);
	}

	public void getScoreList(final Context context, String token,
			String page_no, final InterScoreList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiachievement/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Score_List> mlistContact = new ArrayList<Cpk_Side_Score_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("alist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(
											jsonArrayItem + "",
											Cpk_Side_Score_List.class);
									ACache.get(context).put(
											AppStrStatic.cache_key_score+A_0_App.USER_UNIQID,
											jsonObject);
								}

								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 身边 添加报修
	 */
	public interface InterSideRepairSent {
		void onSuccess();
	}

	public void SideRepairSent(String token, String title, String place,
			String type_id, String phone, String details,
			final InterSideRepairSent interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirepair/add";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("title", title);
		params.put("place", place);
		params.put("type_id", type_id);
		params.put("phone", phone);
		params.put("details", details);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 身边 报修状态
	 */
	public interface InterSideRepairStatus {
		void onSuccess();
	}

	public void SideRepairStatus(String token,String repair_id,
			final InterSideRepairStatus interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirepair/changeStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("repair_id", repair_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 身边 失物招领状态
	 */
	public interface InterSideFoundStatus {
		void onSuccess();
	}

	public void SideFoundStatus(String token,String lost_id,
			final InterSideFoundStatus interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/changeStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("lost_id", lost_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 身边 失物招领状态
	 */
	public interface InterSideFoundDelete {
		void onSuccess();
	}

	public void SideFoundDelete(String token,String lost_id,
			final InterSideFoundDelete interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/delete";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("lost_id", lost_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 身边 添加失物招领
	 */
	public interface InterSideFoundSent {
		void onSuccess();
	}

	public void SideFoundSent(String token, String lost_id, String name,
			String place, String lost_time, String type, String phone,String desc,String photo_url,
			 final InterSideFoundSent interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilost/add";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("lost_id", lost_id);
		params.put("name", name);
		params.put("place", place);
		params.put("lost_time", lost_time);
		params.put("type", type);
		params.put("phone", phone);
		params.put("desc", desc);
		params.put("photo_url", photo_url);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);

							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

    /**
     * 
     * @Description: TODO(课程表--主列表接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InteSideCourse {
        void onSuccess(Cpk_Side_Course_Week course_week,String totle_weeks,String now_week,String semester_id,
                String message,String semester_status);
    }

    public void getSideCourse(final Context context, String token,final String week,String course_id,
            String course_user_uniqid,String share_time,String message_id,final InteSideCourse iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/getCourseTableList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("week", week);
        params.put("token", token);
        params.put("course_id", course_id);
        params.put("course_user_uniqid", course_user_uniqid);
        params.put("share_time", share_time);
        params.put("message_id", message_id);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            String now_week = jsonObject.optString("curr_week");
                            String totle_weeks = jsonObject.optString("count_week");
                            String semester_id = jsonObject.optString("semester_id");
                            String semester_status = jsonObject.optString("semester_status");
                            String semester_msg = jsonObject.optString("semester_msg");
                            if (state == 1) {
                                JSONObject jsonArrayItem = jsonObject.optJSONObject("clist");
                                Cpk_Side_Course_Week item_AA = null ;
                                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                    item_AA = new Cpk_Side_Course_Week();
                                    String request_Week = jsonArrayItem.optString("week");
                                    item_AA.setWeek(request_Week);
                                    String month = jsonArrayItem.optString("month");
                                    item_AA.setMonth(month);
                                    item_AA.setDetail_id(jsonArrayItem.optString("detail_id"));
                                    item_AA.setCourse_id(jsonArrayItem.optString("course_id"));
                                    
                                    List<Cpk_Side_Course_WeekDetail> listBB = new ArrayList<Cpk_Side_Course_WeekDetail>();
                                    JSONArray itemOne = jsonArrayItem.optJSONArray("weekly_list");
                                    if (itemOne != null && itemOne.length() > 0) {
                                        for (int j = 0; j < itemOne.length(); j++) {
                                            Cpk_Side_Course_WeekDetail item_BB = new Cpk_Side_Course_WeekDetail();
                                            String date = itemOne.getJSONObject(j).optString("date");
                                            item_BB.setDate(date);
                                            String weekday = itemOne.getJSONObject(j).optString("weekday");
                                            item_BB.setWeekday(weekday);
                                            item_BB.setMonth(month);
                                            List<Cpk_Side_Course_CourseDetail> listCC= new ArrayList<Cpk_Side_Course_CourseDetail>();
                                            JSONArray dd = itemOne.getJSONObject(j).optJSONArray("course");
                                            if (dd != null && dd.length() > 0) {
//                                                    listCC = JSON.parseArray(dd+"",Cpk_Side_Course_CourseDetail.class);
                                                for (int k = 0; k < dd.length(); k++) {
                                                    Cpk_Side_Course_CourseDetail itemCC = new Cpk_Side_Course_CourseDetail();
                                                    itemCC.setCoursecode(dd.getJSONObject(k).optString("coursecode"));
                                                    itemCC.setCoursename(dd.getJSONObject(k).optString("coursename"));
                                                    itemCC.setEndsection(dd.getJSONObject(k).optInt("endsection"));
                                                    itemCC.setStartsection(dd.getJSONObject(k).optInt("startsection"));
                                                    itemCC.setTeachername(dd.getJSONObject(k).optString("teachername"));
                                                    
                                                    itemCC.setPlace(dd.getJSONObject(k).optString("place"));
                                                    itemCC.setCourseweek(dd.getJSONObject(k).optString("courseweek"));
                                                    itemCC.setCm_id(dd.getJSONObject(k).optString("cm_id"));
                                                    itemCC.setToday(Integer.parseInt(weekday));
                                                    itemCC.setWeekday_id(dd.getJSONObject(k).optString("weekday_id"));
                                                    itemCC.setDate(date);
                                                    
                                                    String organ_names = dd.getJSONObject(k).optString("organ_names");
                                                    List<String> mList = new ArrayList<String>();
                                                    if (organ_names != null&& !organ_names.equals("") && organ_names.length() > 0)
                                                    {
                                                        String[] temp = organ_names.split(",");
                                                        for (int i = 0; i < temp.length; i++) {
                                                            mList.add(temp[i]);
                                                        }
                                                    }
                                                    
                                                    itemCC.setOrgan_names(mList);
                                                    
                                                    List<Cpk_User_Values> list_values = new ArrayList<Cpk_User_Values>();
                                                    list_values = JSON.parseArray(dd.getJSONObject(k).optJSONArray("course_detail") + "", Cpk_User_Values.class);
                                                    itemCC.setUser_values(list_values);
                                                    
                                                    itemCC.setClass_ids(dd.getJSONObject(k).optString("class_ids"));
                                                    
                                                    listCC.add(itemCC);
                                                 }
                                            }
                                            item_BB.setCourse(listCC);
                                            listBB.add(item_BB);
                                        }
                                    }
                                    item_AA.setWeekly_list(listBB);
                                    if (week == null || week.equals(""))
                                        ACache.get(context).put(
                                                AppStrStatic.cache_key_b_side_course + A_0_App.USER_UNIQID + request_Week,jsonObject);
                                    else
                                        ACache.get(context).put(AppStrStatic.cache_key_b_side_course
                                                        + A_0_App.USER_UNIQID + week, jsonObject);
                                }
                                iCallBack.onSuccess(item_AA,totle_weeks,now_week,semester_id,semester_msg,semester_status);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 复制课程表
     */
    public void copyOtherCourse(String token, String share_time, String course_user_uniqid,String semester_id,String course_id,
            final Inter_UpLoad_ChannelId interCallBack, final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/copy";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("share_time", share_time);
        params.put("course_user_uniqid", course_user_uniqid);
        params.put("semester_id", semester_id);
        params.put("course_id", course_id);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params, AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            int time = jsonObject.optInt("time");

                            if (state == 1) {
                                interCallBack.onSuccess(message);
                            } else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    public interface AppNotice_InviteInstallAppCallBack {
        void onSuccess(String message);
    }
    
    /**
     * 课程表-添加班级
     */
    public void to_Add_Course_Class(String weekday_id,String ids, String token,String type,final AppNotice_InviteInstallAppCallBack iCallBack
            ,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/addClassOrGroup";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("weekday_id", weekday_id);
        params.put("ids", ids);
        params.put("type", type);
        params.put("app_version", PubMehods.getVerName(context));        
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);                
                            
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                iCallBack.onSuccess(message);
                            }else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }


    public interface Inter_Side_Course_Add {
        void onSuccess(int state,String message);
    }
    
    /**
     * 课程表-增加课程
     */
    public void add_Course_Action(String semester_id,String jsonWeeks,String course_name,String cm_id,String token,
            String is_sure,String is_edit,String weekday_id,String teacher_name,final Inter_Side_Course_Add iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/addCourse";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("semester_id", semester_id);//学期ID
        params.put("weeks", jsonWeeks);//上课周数
        params.put("course_name", course_name);//课程名称
        params.put("cm_id", cm_id);//课程ID 如果从列表选的则必传 ，用户自定义的课程名称，则课程ID可不传或传空
        params.put("app_version", PubMehods.getVerName(context));    
        params.put("is_sure", is_sure);//默认 0，是 1
        params.put("is_edit", is_edit);//编辑1,添加 0
        params.put("weekday_id", weekday_id);//编辑传值,其他“”
        params.put("teacher_name", teacher_name);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);                
                            
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                iCallBack.onSuccess(state,message);
                            }else if(state ==2){
                                iCallBack.onSuccess(state,message);
                            }else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    public interface Inter_Side_Course_Add_Sure {
        void onSuccess(String message);
    }
    /**
     * 课程表-增加课程
     */
    public void add_Course_Action_Sure(String semester_id,String jsonWeeks,String course_name,String cm_id,String token,
            String is_sure,String is_edit,String weekday_id,String teacher_name,final Inter_Side_Course_Add_Sure iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/addCourse";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("semester_id", semester_id);//学期ID
        params.put("weeks", jsonWeeks);//上课周数
        params.put("course_name", course_name);//课程名称
        params.put("cm_id", cm_id);//课程ID 如果从列表选的则必传 ，用户自定义的课程名称，则课程ID可不传或传空
        params.put("app_version", PubMehods.getVerName(context));    
        params.put("is_sure", is_sure);//默认 0，是 1
        params.put("is_edit", is_edit);//编辑1,添加 0
        params.put("weekday_id", weekday_id);//编辑传值,其他“”
        params.put("teacher_name", teacher_name);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);                
                            
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                iCallBack.onSuccess(message);
                            }else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 课程表-删除课程
     */
    public void del_Course_Action(String weekday_id,String detail_id, String token,final AppNotice_InviteInstallAppCallBack iCallBack
            ,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicourse/deleteCourse";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("weekday_id", weekday_id);//课表每天详情ID
        params.put("detail_id", detail_id);//课表详情ID
        params.put("app_version", PubMehods.getVerName(context));        
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);                
                            
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");

                            if (state == 1) {
                                iCallBack.onSuccess(message);
                            }else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 
     * @Description: TODO(课程表--选择课程接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InterOfficialCourseList {
        void onSuccess(List<Cpk_Side_Course_Official> mCourseList);
    }

    public void getOfficialCourseList(final Context context, String keyword,
            String page_no,String token, final InterOfficialCourseList iCallBack,final Inter_Call_Back iPubCallBack) {

        String url = A_0_App.SERVER_REQUEST_BASE_URL+ "Apicourse/getCourseList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("keyword", keyword);
        params.put("page_no", page_no);
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                List<Cpk_Side_Course_Official> mList  = new ArrayList<Cpk_Side_Course_Official>();
                                JSONArray jsonArray = jsonObject.optJSONArray("list");
                                if(jsonArray != null && jsonArray.length() >0){
                                    mList = JSON.parseArray(jsonArray + "", Cpk_Side_Course_Official.class);
                                }
                                iCallBack.onSuccess(mList);
                            } else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

	/**
	 * 
     * @Description: TODO(课程表--选择任课教师接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public void getOfficialTeacherList(final Context context, String keyword,String page_no,String token, 
            String course_name,String cm_id,final InterOfficialCourseList iCallBack,final Inter_Call_Back iPubCallBack) {

        String url = A_0_App.SERVER_REQUEST_BASE_URL+ "Apicourse/getCourseNameByTeacherList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("keyword", keyword);
        params.put("page_no", page_no);
        params.put("token", token);
        params.put("course_name", course_name);
        params.put("cm_id", cm_id);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                List<Cpk_Side_Course_Official> mList  = new ArrayList<Cpk_Side_Course_Official>();
                                JSONArray jsonArray = jsonObject.optJSONArray("list");
                                if(jsonArray != null && jsonArray.length() >0){
                                    mList = JSON.parseArray(jsonArray + "", Cpk_Side_Course_Official.class);
                                }
                                iCallBack.onSuccess(mList);
                            } else {
                                onFailureBack(context, jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

    
	/**
	 * 
	 * @Description: TODO(报修列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InteRepairList {
		void onSuccess(List<Cpk_Side_Repair_List> mList);
	}

	public void getRepairList(final Context context, String token,
			final String page_no, final String type,
			final InteRepairList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirepair/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		
		params.put("app_version", PubMehods.getVerName(context));
		params.put("type", type);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Repair_List> mlistContact = new ArrayList<Cpk_Side_Repair_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("rlist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(
											jsonArrayItem + "",
											Cpk_Side_Repair_List.class);
									if (page_no.equals("1")) {
										if (type.equals("1")) {
											ACache.get(context)
													.put(AppStrStatic.cache_key_repair_all+A_0_App.USER_UNIQID,
															jsonObject);
										} else {
											ACache.get(context)
													.put(AppStrStatic.cache_key_repair_my+A_0_App.USER_UNIQID,
															jsonObject);
										}

									}
								}

								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(报修详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InteRepairDetail {
		void onSuccess(Cpk_Side_Repair_Detail cpk_Side_Repair_Detail);
	}

	public void getRepairDetail(final Context context, String token,
			final String repair_id, final InteRepairDetail iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirepair/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		
		params.put("app_version", PubMehods.getVerName(context));
		params.put("repair_id", repair_id);
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							Cpk_Side_Repair_Detail mlistContact = new Cpk_Side_Repair_Detail();
							if (state == 1) {
								Cpk_Side_Repair_Detail mlistContact1 = JSON.parseObject(result,
										Cpk_Side_Repair_Detail.class);
								mlistContact=JSON.parseObject(mlistContact1.getInfo(),
										Cpk_Side_Repair_Detail.class);
								ACache.get(context).put(
										AppStrStatic.cache_key_repair_detail+A_0_App.USER_UNIQID
												+ repair_id,jsonObject);
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(报修类型接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterSideRepairTypeList {
		void onSuccess(List<Cpk_Side_Repair_Type> mList);
	}

	public void getSideRepairTypeList(final Context context, String token,
			final InterSideRepairTypeList iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apirepair/getType";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Repair_Type> mlistContact = new ArrayList<Cpk_Side_Repair_Type>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("tlist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									mlistContact = JSON.parseArray(
											jsonArrayItem + "",
											Cpk_Side_Repair_Type.class);
								}
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

	/**
	 * 
	 * @Description: TODO(课程表背景列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InteCourseBgList {
		void onSuccess(List<Cpk_Side_Lectures_List> mList);
	}

	public void getCourseBgList(final Context context, String token,
			final String page_no, final InteCourseBgList iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apilecture/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Side_Lectures_List> mlistContact = new ArrayList<Cpk_Side_Lectures_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("llist");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Side_Lectures_List contace = new Cpk_Side_Lectures_List();
										contace.setArticle_id(jsonArrayItem
												.getJSONObject(j).optString(
														"article_id"));
										contace.setAuthor(jsonArrayItem
												.getJSONObject(j).optString(
														"author"));
										contace.setAuthor_desc(jsonArrayItem
												.getJSONObject(j).optString(
														"author_desc"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"create_time"));
										contace.setEnd_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"end_time"));

										contace.setPlace(jsonArrayItem
												.getJSONObject(j).optString(
														"place"));
										contace.setRead_num(jsonArrayItem
												.getJSONObject(j).optString(
														"read_num"));
										contace.setStart_time(jsonArrayItem
												.getJSONObject(j).getLong(
														"start_time"));
										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));

										mlistContact.add(contace);
									}
									if (page_no.equals("1")) {
										ACache.get(context).put(
												AppStrStatic.cache_key_lecture+A_0_App.USER_UNIQID,
												jsonObject);
									}
								}

								iCallBack.onSuccess(mlistContact);
							} else if (state == 0) {
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

    /**
     * 
     * @Description: TODO(考勤列表)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InterGetMyAtdList {
        void onSuccess(List<Cpk_Side_Attence_List> mList,long sysTime);
    }

    public void getMyAtdList(final Context context, String token,String page_no,final InterGetMyAtdList iCallBack
            ,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getMyAtdList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put("page_no", page_no);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            long sysTime = jsonObject.getLong("time");
                            List<Cpk_Side_Attence_List> mToadayList = new ArrayList<Cpk_Side_Attence_List>();
                            if (state == 1) {
                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("list");
                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
                                    mToadayList = JSON.parseArray(jsonArrayItem_today + "",Cpk_Side_Attence_List.class);
                                }
                                iCallBack.onSuccess(mToadayList,sysTime);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    /**
	 * 
	 * @Description: TODO(考勤统计接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterAttdenceList {
		void onSuccess(List<Cpk_Side_Attdence_Detail_List> mList,String title);
	}

	public void getAttdenceList(final Context context, String token,
			final InterAttdenceList iCallBack,final Inter_Call_Back iPubCallBack) {
	    String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getMyAtdStatsList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
	    params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String title=jsonObject.optString("title");
							List<Cpk_Side_Attdence_Detail_List> mlistContact = new ArrayList<Cpk_Side_Attdence_Detail_List>();
                            if (state == 1) {
                                JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
                                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                    mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_Side_Attdence_Detail_List.class);
                                    ACache.get(context).put(AppStrStatic.cache_key_attdence_detail + A_0_App.USER_UNIQID, jsonObject);
                                }
            
                                iCallBack.onSuccess(mlistContact, title);
                            } else if (state == 0) {
								iCallBack.onSuccess(mlistContact,title);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}

    /**
     * 开始签到考勤
     */
    public interface InterSubmitMyLocation {
        void onSuccess();
    }

    public void submitMyLocation(String token, String atd_id, String lat,String lng,
            final InterSubmitMyLocation interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/submitMyLocation";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("lat", lat);
        params.put("atd_id", atd_id);
        params.put("lng", lng);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                interCallBack.onSuccess();
                            } else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    
    /**
	 * 
	 * @Description: TODO(考勤列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterAttdenceListMain {
		void onSuccess(List<Cpk_Side_Attence> mToadayList,List<Cpk_Side_Attence> mEarlyList);
	}

	public void getAttdenceListMain(final Context context, String token,
			final String page_no, final InterAttdenceListMain iCallBack,final Inter_Call_Back iPubCallBack) {
	    String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/getList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put("page_no", page_no);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            List<Cpk_Side_Attence> mToadayList = new ArrayList<Cpk_Side_Attence>();
                            List<Cpk_Side_Attence> mEarlyList = new ArrayList<Cpk_Side_Attence>();
                            if (state == 1) {
                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("today");
                                JSONArray jsonArrayItem_early = jsonObject.optJSONArray("early");
                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
                                    mToadayList = JSON.parseArray(jsonArrayItem_today + "",Cpk_Side_Attence.class);
                                }
                                if (jsonArrayItem_early != null && jsonArrayItem_early.length() > 0) {
                                    mEarlyList = JSON.parseArray(jsonArrayItem_early + "",Cpk_Side_Attence.class);
                                }
                                if(page_no.equals("1"))
                                {
                                 ACache.get(context).put(AppStrStatic.cache_key_side_attdence_list+A_0_App.USER_UNIQID, jsonObject,
                                         AppStrStatic.cache_key_side_attdence_time);
                                }
                                iCallBack.onSuccess(mToadayList,mEarlyList);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
	}

	 /**
		 * 
		 * @Description: TODO(关联教师列表接口)
		 * @author Jiaohaili
		 * @date 2015年11月26日 下午4:24:20
		 */
		public interface InterAttdenceContactList {
			void onSuccess(List<Cpk_RongYun_True_Name> mList);
		}

		public void getAttdenceContactList(final Context context, String token,
				 final InterAttdenceContactList iCallBack,final Inter_Call_Back iPubCallBack) {
		    String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/getLinkTeacher";
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
	                new Callback.CommonCallback<String>() {
	                    @Override
	                    public void onCancelled(CancelledException arg0) {
	                        iPubCallBack.onCancelled();
	                    }

	                    @Override
	                    public void onFinished() {
	                        iPubCallBack.onFinished();
	                    }

	                    @Override
	                    public void onSuccess(String result) {
	                        try {
	                            JSONObject jsonObject = new JSONObject(result);
	                            int state = jsonObject.optInt("status");
	                            String message = jsonObject.optString("msg");
	                            List<Cpk_RongYun_True_Name> mList = new ArrayList<Cpk_RongYun_True_Name>();
	                            if (state == 1) {
	                                JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
	                                if (jsonArrayItem!= null && jsonArrayItem.length() > 0) {
	                                	mList = JSON.parseArray(jsonArrayItem + "",Cpk_RongYun_True_Name.class);
	                                }
	                               
	                                 ACache.get(context).put(AppStrStatic.cache_key_attence_contact+A_0_App.USER_UNIQID, jsonObject);
	                                iCallBack.onSuccess(mList);
	                            } else{
	                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
	                            }
	                        } catch (JSONException e) {
	                            iPubCallBack.onFailure(error_title[2]);
	                        }
	                    }

	                    @Override
	                    public void onError(Throwable error, boolean arg1) {
	                        onErrorBack(iPubCallBack, error, arg1);
	                    }
	                });
		}

	/**
     * 身边 添加考勤
     */
    public interface InterSideAddAttence {
        void onSuccess(String message,String atd_id);
    }

    public void sideAddAttence(String token, String title, String place,
            String user_id,final InterSideAddAttence iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/add";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("title", title);
        params.put("place", place);
        params.put("user_id", user_id);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            String atd_id = jsonObject.optString("atd_id");
                            
                            if (state == 1) {
                                iCallBack.onSuccess(message,atd_id);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    /**
    /**
     * 
     * @Description: TODO(考勤详情接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InteAttdenceDetail {
        void onSuccess(Cpk_Side_Attence_Detail side_Attence_Detail,String current_time);
    }

    public void getAttdenceDetail(final Context context, String token,
            final String atd_id, final InteAttdenceDetail iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiattendance/getInfo";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("app_version", PubMehods.getVerName(context));
        params.put("atd_id", atd_id);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_INCREASE_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                        	
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            String time = jsonObject.optString("time");
                            if (state == 1) {
                                Cpk_Side_Attence_Detail mlistContact= JSON.parseObject(jsonObject.optString("info"),
                                        Cpk_Side_Attence_Detail.class);
                                
                                ACache.get(context).put(
                                        AppStrStatic.cache_key_attdence_detail
                                        +A_0_App.USER_UNIQID+ atd_id, jsonObject);
                                iCallBack.onSuccess(mlistContact,time);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

    
    /**
     * 
     * @Description: TODO(忘记密码获取好友列表接口)
     * @author Jiaohaili
     * @date 2015年11月26日 下午4:24:20
     */
    public interface InterGetFriendList {
        void onSuccess(List<Cpk_Register_Select_Friend_Acy> mFriendList,int state,String message);
    }

    public void getFriendList(final Context context, String mobile, final InterGetFriendList iCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/getFriendList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_version", PubMehods.getVerName(context));
        params.put("mobile", mobile);
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            List<Cpk_Register_Select_Friend_Acy> mToadayList = new ArrayList<Cpk_Register_Select_Friend_Acy>();
                            if (state == 1||state == 2) {
                                JSONArray jsonArrayItem_today = jsonObject.optJSONArray("list");
                                if (jsonArrayItem_today != null && jsonArrayItem_today.length() > 0) {
                                    mToadayList = JSON.parseArray(jsonArrayItem_today + "",Cpk_Register_Select_Friend_Acy.class);
                                }
                                iCallBack.onSuccess(mToadayList,state,message);
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }

    /**
     * 验证所选好友
     */
    public interface InterVerifyFriendList{
        void onSuccess();
    }

    public void verifyFriendList(String mobile, String uids, final InterVerifyFriendList interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiuser/verifyFriendList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("uids", uids);
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        iPubCallBack.onCancelled();
                    }

                    @Override
                    public void onFinished() {
                        iPubCallBack.onFinished();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                interCallBack.onSuccess();
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
    /**
     * 获取启动页广告
     */
    public interface Inter_GetStartupPic {
        void onSuccess(String pic);
    }

    public void getStartupPic(final Inter_GetStartupPic interCallBack,final Inter_Call_Back iPubCallBack) {
        String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicommon/getStartupPic";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_version", PubMehods.getVerName(context));
        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
        
        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        
                    }

                    @Override
                    public void onFinished() {
                        
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            if (state == 1) {
                                String pic = jsonObject.optString("pic");
                                interCallBack.onSuccess(pic);
                                 ACache.get(context).put(AppStrStatic.cache_key_student_A_1_Start_Acy+A_0_App.USER_UNIQID,jsonObject);
                            } else {
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
                            jsonObject = null;
                        } catch (JSONException e) {
                            iPubCallBack.onFailure(error_title[2]);
                        }
                    }

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
                });
    }
    
	/**
	 * @功能描述：获取app版本信息 ，升级
	 */
	public interface InterCheckVersion {
		void onSuccess(Cpk_Version cpk_Version);
	}

	public void checkVersion(final InterCheckVersion iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicommon/getVersion";
		Map<String, Object> params = new HashMap<String, Object>();

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {
								Cpk_Version cpk_Version = new Cpk_Version();
								cpk_Version.setVersionCode(jsonObject
										.optString("code"));
								cpk_Version.setVersionName(jsonObject
										.optString("title"));
								cpk_Version.setIs_require(jsonObject
										.optString("is_require"));
								cpk_Version.setDownloadUrl(jsonObject
										.optString("url"));

                                String logs = "";
                                String tempStr = jsonObject.optString("content");
                                if (tempStr != null && tempStr.length() > 0) {
                                    String[] temp = tempStr.split("\\|");
                                    for (int i = 0; i < temp.length; i++) {
                                        if (i == 0) {
                                            logs = temp[i];
                                        } else {
                                            logs += "\n" + temp[i];
                                        }
                                    }
                                }
                                cpk_Version.setUpdateLog(logs);

								iCallBack.onSuccess(cpk_Version);
							} else {
								iPubCallBack.onFailure(message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * @功能描述：获取我的推荐人信息
	 */
	public interface InviteInfoCallBack {
		void onSuccessMyInviterInfo(MyInviterInfo mMyInviterInfo);
		void onSuccessMySendInvitionInfo(List<Cpk_Account_InviteInfo_ISendInviteInfo> mMySendInvitionInfo);
		void onSuccessCount(int iSendInvitionCount);
		void onSucceccShareUrl(String shareurl);
		void onSucceccInviteRuleUrl(String inviteurl);
		void onSucceccTopAndBottomRecInfoText(String rec_info_top,String rec_info_bottom);
	}

	/**
	 * 获取我的推荐人信息
	 * @param token
	 * @param iCallBack
	 */
	public void reqInviteInfo(String token,final InviteInfoCallBack iCallBack,final Inter_Call_Back iPubCallBack) {
		    String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/getRecommendInfo";
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
	        
	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {
				@Override
				public void onError(Throwable error, boolean arg1) {
					onErrorBack(iPubCallBack, error, arg1);
				
				}

				@Override
				public void onSuccess(String result) {
					try {
//						if(result.isEmpty()){
//							iPubCallBack.onFailure(error_title[2]);
//							return;
//						}
						JSONObject jsonObject = new JSONObject(result);
						int state = jsonObject.optInt("status");
						String message = jsonObject.optString("msg");
//						long servertime=jsonObject.getLong("time");
						if (state == 1) {
							/**
							 * 处理数据
							 */
							int count = jsonObject.optInt("count");
							iCallBack.onSuccessCount(count);						
							//获取我的推荐人信息
							String infoJsonstr = jsonObject.optString("info");
							if(infoJsonstr.isEmpty()){
								iCallBack.onSuccessMyInviterInfo(null);		
							}else{
								JSONObject infoJsonstrobject = new JSONObject(infoJsonstr);							
								String userid = infoJsonstrobject.optString("user_id");
								String truename = infoJsonstrobject.optString("true_name");
								String photoname = infoJsonstrobject.optString("photo_url");
								String organ_name = infoJsonstrobject.optString("organ_name");
								MyInviterInfo mMyInviterInfo = new MyInviterInfo(userid, truename, photoname, organ_name);
								iCallBack.onSuccessMyInviterInfo(mMyInviterInfo);																					
							}	
													
							//获取我推荐的人的信息
//							String mySendInvitionInfoJsonstr
							List<Cpk_Account_InviteInfo_ISendInviteInfo> mlistContact = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();
							 JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
							 if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
	                             mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_Account_InviteInfo_ISendInviteInfo.class);    
	                             iCallBack.onSuccessMySendInvitionInfo(mlistContact);
	                         }else{
	                        	 iCallBack.onSuccessMySendInvitionInfo(null);
	                         }
							 
							 //获取分享链接
							 String shareurl = jsonObject.optString("rec_url");
							 iCallBack.onSucceccShareUrl(shareurl);
							 
							//获取邀请规则链接
							 String inviteurl = jsonObject.optString("rec_info_url");
							 iCallBack.onSucceccInviteRuleUrl(inviteurl);
							 
							//获取页面顶部和底部的文本内容
							 String rec_info_top = jsonObject.optString("rec_info_top");
							 String rec_info_bottom = jsonObject.optString("rec_info_bottom");
							 iCallBack.onSucceccTopAndBottomRecInfoText(rec_info_top, rec_info_bottom);
							 
							 ACache.get(context).put(AppStrStatic.cache_key_invite_main+A_0_App.USER_UNIQID,jsonObject);
						}else{
						    onFailureBack(context,jsonObject, iPubCallBack, state, message);
						}
						
					} catch (JSONException e) {
						iPubCallBack.onFailure(error_title[2]);
					}
					
				}
				
				@Override
				public void onCancelled(CancelledException arg0) {
					iPubCallBack.onCancelled();
				}

				@Override
				public void onFinished() {
					iPubCallBack.onFinished();
				}
			});
	}
	
	
	/**
	 * @功能描述：获取邀请人列表
	 */
	public interface ISendInvitionListCallBack {
		void onSuccessMySendInvitionInfo(List<Cpk_Account_InviteInfo_ISendInviteInfo> mMySendInvitionInfo);
	}

	/**
	 * 获取邀请人列表
	 * @param token
	 * @param iCallBack
	 */
	public void reqISendInvitionList(String token,final int page_no, final ISendInvitionListCallBack iCallBack,final Inter_Call_Back iPubCallBack) {
		 String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/getRecommendList";
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("page_no", String.valueOf(page_no));
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {

				@Override
				public void onError(Throwable error, boolean arg1) {
					onErrorBack(iPubCallBack, error, arg1);
				}

				@Override
				public void onSuccess(String result) {
					try {
//						if(result.isEmpty()){
//							iPubCallBack.onFailure(error_title[2]);
//							return;
//						}
						JSONObject jsonObject = new JSONObject(result);
						int state = jsonObject.optInt("status");
						String message = jsonObject.optString("msg");
						
						if (state == 1) {
							/**
							 * 处理数据
							 */
							List<Cpk_Account_InviteInfo_ISendInviteInfo> mlistContact = new ArrayList<Cpk_Account_InviteInfo_ISendInviteInfo>();
							 JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
							 if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
	                             mlistContact = JSON.parseArray(jsonArrayItem + "", Cpk_Account_InviteInfo_ISendInviteInfo.class);      
	                             iCallBack.onSuccessMySendInvitionInfo(mlistContact);
	                             if (page_no==1) {
										ACache.get(context).put(
												AppStrStatic.cache_key_invite_record+A_0_App.USER_UNIQID,
												jsonObject);
									}
							 }else{
	                        	 iCallBack.onSuccessMySendInvitionInfo(null);
	                         }
						}else{
						    onFailureBack(context,jsonObject, iPubCallBack, state, message);
						}

					} catch (JSONException e) {
						iPubCallBack.onFailure(error_title[2]);
					}
					
				}
				
				@Override
				public void onCancelled(CancelledException arg0) {
					iPubCallBack.onCancelled();
				}

				@Override
				public void onFinished() {
					iPubCallBack.onFinished();
				}
			});
	}
	
	
	/**
	 * @功能描述：绑定推荐人
	 */
	public interface BindInviterCallBack {
		void onSuccess();
	}
	
	
	/**
	 * 绑定推荐人
	 * @param token
	 * @param iCallBack
	 */
	public void reqBindInviter(String token,String phone, final BindInviterCallBack iCallBack,final Inter_Call_Back iPubCallBack) {
		    String url = A_0_App.SERVER_REQUEST_BASE_URL + "ApiCenter/recommandBind";
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("token", token);
	        params.put("phone", phone);
	        params.put("app_version", PubMehods.getVerName(context));
	        params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
	        HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {
				@Override
				public void onError(Throwable error, boolean arg1) {
					onErrorBack(iPubCallBack, error, arg1);
					
				}

				@Override
				public void onSuccess(String result) {
					try {
//						if(result.isEmpty()){
//							iPubCallBack.onFailure(error_title[2]);
//							return;
//						}
						JSONObject jsonObject = new JSONObject(result);
						int state = jsonObject.optInt("status");
						String message = jsonObject.optString("msg");
												
						if (state == 1) {
							/**
							 * 处理数据
							 */
							iCallBack.onSuccess();
						} else{
						    onFailureBack(context,jsonObject, iPubCallBack, state, message);
						}
						
					} catch (JSONException e) {
						iPubCallBack.onFailure(error_title[2]);
					}
					
				}
				
				@Override
				public void onCancelled(CancelledException arg0) {
					iPubCallBack.onCancelled();
				}

				@Override
				public void onFinished() {
					iPubCallBack.onFinished();
				}
			});
	}
	/**
	 * 
	 * @Description: TODO(校园资讯列表接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterInfoList {
		void onSuccess(List<Cpk_Info_list> mList,long servertime);
	}

	public void getInfoList(final Context context, String token, final String page_no,String cate_id,
			final InterInfoList iCallBack,final Inter_Call_Back iPubCallBack) {

		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/getList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("cate_id", cate_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							long servertime=jsonObject.getLong("time");
							List<Cpk_Info_list> mlistContact = new ArrayList<Cpk_Info_list>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("list");
								if (jsonArrayItem != null&& jsonArrayItem.length() > 0) {
									mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Info_list.class);
									
								}
								if(page_no.equals("1"))
								{
								  ACache.get(context).put(AppStrStatic.cache_key_info+A_0_App.USER_UNIQID, jsonObject);
								}
								iCallBack.onSuccess(mlistContact,servertime);
							} else if (state == 0){
								iCallBack.onSuccess(mlistContact,servertime);
							}else {
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 校园资讯详情点赞
	 */
	public interface InterInfo_Like {
		void onSuccess();
	}

	public void get_Info_Like(String info_id, String token,String like_type,
			final InterInfo_Like interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/infoLikeHandle";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("info_id", info_id);
		params.put("token", token);
		params.put("like_type", like_type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
	/**
	 * 校园评论点赞
	 */
	public interface InterInfo_Comment {
		void onSuccess();
	}

	public void get_Info_Comment(String comment_id, String token,String like_type,
			final InterInfo_Comment interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/commentLikeHandle";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("comment_id", comment_id);
		params.put("token", token);
		params.put("like_type", like_type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 校园资讯回复某个人
	 */
	public interface InterInfo_reply{
		void onSuccess();
	}

	public void get_info_reply(String to_comment_id, String info_id,String token,
			String content, final InterInfo_reply interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/addComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("to_comment_id", to_comment_id);
		params.put("info_id", info_id);
		params.put("token", token);
		params.put("content", content);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(资讯详情接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterInfoDetail {
		void onSuccess(Cpk_Info_Detail info,String severTime);
	}

      public void getInfoDetail(final Context context, String token,final String info_id,final InterInfoDetail iCallBack
              ,final Inter_Call_Back iPubCallBack) {
		
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("info_id", info_id);
 		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							String time = jsonObject.optString("time");
							if (state == 1) {
								Cpk_Info_Detail notice = JSON.parseObject(result, Cpk_Info_Detail.class);
								ACache.get(context).put(AppStrStatic.cache_key_info_detail+A_0_App.USER_UNIQID+info_id, jsonObject);
								iCallBack.onSuccess(notice,time);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
      
      /**
  	 * 
  	 * @Description: TODO(校园资讯评论列表接口)
  	 * @author Jiaohaili
  	 * @date 2015年11月26日 下午4:24:20
  	 */
  	public interface InterInfotCommentList {
  		void onSuccess(List<Cpk_Info_Detail_Comment_List_All> mList,String total);
  	}

  	public void getInfotCommentList(final Context context, String token,
  			String info_id, String page_no, final InterInfotCommentList iCallBack,final Inter_Call_Back iPubCallBack) {

  		String url = A_0_App.SERVER_REQUEST_BASE_URL+ "Apiinfo/getCommentList";
  		Map<String, Object> params = new HashMap<String, Object>();
  		params.put("token", token);
  		params.put("info_id", info_id);
  		params.put("page_no", page_no);
  		params.put("app_version", PubMehods.getVerName(context));
  		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
  		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
  				new Callback.CommonCallback<String>() {
  					@Override
  					public void onCancelled(CancelledException arg0) {
  						iPubCallBack.onCancelled();
  					}

  					@Override
  					public void onFinished() {
  						iPubCallBack.onFinished();
  					}

  					@Override
  					public void onSuccess(String result) {
  						try {
  							JSONObject jsonObject = new JSONObject(
  									result);
  							int state = jsonObject.optInt("status");
  							String message = jsonObject.optString("msg");
  							String totalCount = jsonObject.optString("totalCount");
  							List<Cpk_Info_Detail_Comment_List_All> mlistContact = new ArrayList<Cpk_Info_Detail_Comment_List_All>();
  							if (state == 1) {
  								JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
  								mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Info_Detail_Comment_List_All.class);
  								iCallBack.onSuccess(mlistContact,totalCount);
  							} else if (state == 0) {
  								iCallBack.onSuccess(mlistContact,totalCount);
  							}else{
  							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
  							}
  						} catch (JSONException e) {
  							iPubCallBack.onFailure(error_title[2]);
  						}
  					}

  					@Override
  					public void onError(Throwable error, boolean arg1) {
  						onErrorBack(iPubCallBack, error, arg1);
  					}
  				});
  	}
  	
  	/**
	 * 举报类型
	 */
	public interface InterInfo_Type{
		void onSuccess(List<Cpk_Info_ReportType> reportTypes);
	}

	public void get_info_Type(String token,final InterInfo_Type interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/getReportType";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Info_ReportType>  types=new ArrayList<Cpk_Info_ReportType>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
								types=JSON.parseArray(jsonArrayItem+"", Cpk_Info_ReportType.class);
								interCallBack.onSuccess(types);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 校园资讯举报
	 */
	public interface InterInfo_addReport{
		void onSuccess();
	}

	public void get_info_addReport(String comment_id, String info_id,String token,
			String type, final InterInfo_addReport interCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiinfo/addReport";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("comment_id", comment_id);
		params.put("info_id", info_id);
		params.put("token", token);
		params.put("type", type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								interCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	/**
	 * 
	 * @Description: TODO(身边接口)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterGetInfo{
		void onSuccess(List<Cpk_Banner_Mode> mList,List<Cpk_Module_List> moduleContact,Cpk_Top_List top_List);
	}

	public void GetInfoList(String token,final InterGetInfo iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apiapp/getInfo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						System.out.print(result);
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Banner_Mode> mlistContact = new ArrayList<Cpk_Banner_Mode>();
							List<Cpk_Module_List> moduleContact = new ArrayList<Cpk_Module_List>();
							Cpk_Top_List top_List=new Cpk_Top_List();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject.optJSONArray("adList");
								JSONArray jsonArrayItem2 = jsonObject.optJSONArray("moduleList");
								JSONObject jsonArrayItem3 = jsonObject.optJSONObject("info");
								mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Banner_Mode.class);
								moduleContact=JSON.parseArray(jsonArrayItem2+"", Cpk_Module_List.class);
								top_List=JSON.parseObject(jsonArrayItem3+"", Cpk_Top_List.class);
								ACache.get(context).put(AppStrStatic.cache_key_mySide+A_0_App.USER_UNIQID, jsonObject);
								
								iCallBack.onSuccess(mlistContact,moduleContact,top_List);
							
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
	
	/**
	 * 系统通知列表 
	 * @author Administrator
	 *
	 */
	public interface SysNoticeListCallBack {
		void onSuccess(List<Cpk_SysNotice_ItemUserDetail> mList,String serverTime);
	}
	

	/**
	 * 获取系统通知列表
	 * @param type 1：活动 2：校园资讯
	 * @param context
	 * @param token
	 * @param page_no
	 * @param iCallBack
	 */
	public void getSysNoticeList(final String type, final Context context,
			String token, final String page_no, final SysNoticeListCallBack iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicenter/SystemCommentNotice";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("page_no", page_no);
		params.put("type", type);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
                            JSONObject jsonObject = new JSONObject(result);
                            int state = jsonObject.optInt("status");
                            String message = jsonObject.optString("msg");
                            String time = jsonObject.optString("time");
                            List<Cpk_SysNotice_ItemUserDetail> mSysNoticeList = new ArrayList<Cpk_SysNotice_ItemUserDetail>();
                            if (state == 1) {
                                JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
                                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                                     mSysNoticeList = JSON.parseArray(jsonArrayItem + "", Cpk_SysNotice_ItemUserDetail.class);                              
                                }
                                
                                if("1".equals(page_no)){
                                    ACache.get(context).put(AppStrStatic.cache_key_sysnotice+type+A_0_App.USER_UNIQID, jsonObject);
                                }
                                
                                iCallBack.onSuccess(mSysNoticeList,time);
                            
                            }else{
                                onFailureBack(context,jsonObject, iPubCallBack, state, message);
                            }
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

                    @Override
                    public void onError(Throwable error, boolean arg1) {
                        onErrorBack(iPubCallBack, error, arg1);
                    }
				});
	}
	
	
	/**
	 * 通讯录邀请
	 */
	public interface ContactListInvative {
		void onSuccess(String message);
	}

	/**
	 * 通讯录页面 邀请按钮
	 * @param token
	 * @param uniqid
	 * @param mContactListInvative
	 */
	public void reqContactInvative(String token,String uniqid,final ContactListInvative mContactListInvative
	        ,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apicontacts/postInvite";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("uniqid", uniqid);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					    iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
					    iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");

							if (state == 1) {
								mContactListInvative.onSuccess(message);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
							jsonObject = null;
						} catch (JSONException e) {
						    iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	/**
	 * 
	 * @Description: TODO(通知列表接口 待确认列表)
	 * @author Jiaohaili
	 * @date 2015年11月26日 下午4:24:20
	 */
	public interface InterNoticeListWaitSure {
		void onSuccess(List<Cpk_Notice_List> mList);
	}

	public void getNoticeListWaitSure(final Context context, String token,
			final String level, final String page_no,final InterNoticeListWaitSure iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/getUnreceiptList";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("level", level);
		params.put("page_no", page_no);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(
									result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							List<Cpk_Notice_List> mlistContact = new ArrayList<Cpk_Notice_List>();
							if (state == 1) {
								JSONArray jsonArrayItem = jsonObject
										.optJSONArray("list");
								if (jsonArrayItem != null
										&& jsonArrayItem.length() > 0) {
									for (int j = 0; j < jsonArrayItem.length(); j++) {
										Cpk_Notice_List contace = new Cpk_Notice_List();
										contace.setMl_status(jsonArrayItem
												.getJSONObject(j).optString(
														"ml_status"));
										contace.setLog_id(jsonArrayItem
												.getJSONObject(j).optString(
														"log_id"));
										contace.setMessage_id(jsonArrayItem
												.getJSONObject(j).optString(
														"message_id"));
										contace.setApp_msg_level(jsonArrayItem
												.getJSONObject(j).optString(
														"app_msg_level"));
										contace.setApp_msg_sign(jsonArrayItem
												.getJSONObject(j).optString(
														"app_msg_sign"));

										contace.setBg_img(jsonArrayItem
												.getJSONObject(j).optString(
														"bg_img"));
										contace.setContent(jsonArrayItem
												.getJSONObject(j).optString(
														"content"));
										contace.setDesc(jsonArrayItem
												.getJSONObject(j).optString(
														"desc"));
										contace.setRead_num(jsonArrayItem
												.getJSONObject(j).optString(
														"read_num"));
										contace.setReply_num(jsonArrayItem
												.getJSONObject(j).optString(
														"reply_num"));

										contace.setTitle(jsonArrayItem
												.getJSONObject(j).optString(
														"title"));
										contace.setType(jsonArrayItem
												.getJSONObject(j).optString(
														"type"));
										contace.setCreate_time(jsonArrayItem
												.getJSONObject(j).optString(
														"create_time"));
										contace.setIs_default(jsonArrayItem
												.getJSONObject(j).optString(
														"is_default"));
										contace.setIs_new(jsonArrayItem
												.getJSONObject(j).optString(
														"is_new"));
										contace.setMessage_receipt(jsonArrayItem
												.getJSONObject(j).optInt(
														"message_receipt"));
										contace.setIs_receipt(jsonArrayItem
												.getJSONObject(j).optInt(
														"is_receipt"));
										mlistContact.add(contace);
									}
									
								}
								if (page_no.equals("1")) {
									ACache.get(context)
											.put(AppStrStatic.cache_key_notice_list_waitsure+A_0_App.USER_UNIQID
													+ level, jsonObject);
								}
								iCallBack.onSuccess(mlistContact);
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
	/**
	 * 
	 * @Description: 单个通知确认回执
	 * @author hlx
	 */
	public interface SureReceIptCallBack {
		void onSuccess();
	}

	public void sendNoticeSureReceIpt(final Context context, String token,
			final String message_id, final SureReceIptCallBack iCallBack,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/userReceipt";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("message_id", message_id);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN, ApiSignSort.getSignature(params));
		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {								
								iCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}

					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	

	/**
	 * 批量确认回执
	 * @param context
	 * @param token
	 * @param iCallBack
	 */
	public void sendAllNoticeSureReceIpt(final Context context, String token,final String level, final SureReceIptCallBack iCallBack
	        ,final Inter_Call_Back iPubCallBack) {
		String url = A_0_App.SERVER_REQUEST_BASE_URL + "Apimessage/batchReceipt";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		params.put("level", level);
		params.put("app_version", PubMehods.getVerName(context));
		params.put(AppStrStatic.SIGN,ApiSignSort.getSignature(params));

		HttpUtil.send(context, url, params,AppStrStatic.XUTILS_DEFAULT_CONN_TIMEOUT,
				new Callback.CommonCallback<String>() {
					@Override
					public void onCancelled(CancelledException arg0) {
						iPubCallBack.onCancelled();
					}

					@Override
					public void onFinished() {
						iPubCallBack.onFinished();
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							int state = jsonObject.optInt("status");
							String message = jsonObject.optString("msg");
							if (state == 1) {								
								iCallBack.onSuccess();
							}else{
							    onFailureBack(context,jsonObject, iPubCallBack, state, message);
							}
						} catch (JSONException e) {
							iPubCallBack.onFailure(error_title[2]);
						}
					}
					
					@Override
					public void onError(Throwable error, boolean arg1) {
						onErrorBack(iPubCallBack, error, arg1);
					}
				});
	}
	
	
}

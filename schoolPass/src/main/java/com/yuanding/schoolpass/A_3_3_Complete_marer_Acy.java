package com.yuanding.schoolpass;

import io.rong.imkit.MainActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Student_Info;
import com.yuanding.schoolpass.service.Api.InterSavePerInfo;
import com.yuanding.schoolpass.service.Api.InterStudentInfo;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_UpLoad_Photo;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.date.SlideDateTimeListener;
import com.yuanding.schoolpass.view.date.SlideDateTimePicker;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月10日 上午11:23:01
 *          完善资料
 *          注册页面 regedit_acy，账户页面account_mess，激活页面certivication_acy
 *          此处男女 和标题的匹配，修改中文时需要注意
 */
public class A_3_3_Complete_marer_Acy extends A_0_CpkBaseTitle_Navi {

    public static final String TITLE_USER_TURE_NAME = "真实姓名";
    public static final String TITLE_USER_STUDENT_NO = "学号";
    public static final String TITLE_USER_NIPICK = "昵称";
    public static final String TITLE_USER_INVITATION_NAME = "邀请人";
    private RelativeLayout rel_complete_name, rel_complete_school, rel_complete_class, rel_complete_student_no;
    private RelativeLayout rel_complete_nipic, rel_complete_sex, rel_complete_birthday, rel_complete_zhengzhi;
    private RelativeLayout rel_complete_hometown, rel_complete_student_invite;
    private TextView et_comp_mater_name, et_comp_mater_school, et_comp_mater_banji, et_comp_mater_xue_hao;
    private TextView et_comp_mater_sex, et_comp_mater_nipick, et_comp_date_both, et_comp_zheng_zhi;
    private TextView et_comp_hometown, et_comp_mater_invite_person;
    private CircleImageView iv_account_por;
    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
    private View view_divider;
    //private BitmapUtils bitmapUtils;
    private View mLinerReadDataError, liner_acy_list_whole_view, complere_acy_loading;
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private String schoolId, organ_id = "", phont_url = "";
    private boolean upLoadPhoto = false;
    public static String temppath = "";
    private boolean show_Title_Toast = false;
    private String recommend_phone = "";

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String s = sdf.format(date);
            et_comp_date_both.setText(s);
        }


        @Override
        public void onDateTimeCancel() {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_complete_mater);
        if (getIntent().getExtras() != null) {
            show_Title_Toast = getIntent().getExtras().getBoolean("show_Title_Toast");
        }
        if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO) ||
                A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
            if (show_Title_Toast) {
                PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, R.string.str_need_to_improve_the_information);
            }
        }

        liner_acy_list_whole_view = findViewById(R.id.sv_all_view);
        mLinerReadDataError = findViewById(R.id.complete_load_error);
        complere_acy_loading = findViewById(R.id.complete_acy_loading);

        home_load_loading = (LinearLayout) complere_acy_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        mLinerReadDataError.setOnClickListener(onClick);

        rel_complete_student_invite = (RelativeLayout) findViewById(R.id.rel_complete_student_invite);
        view_divider = findViewById(R.id.view_divider);
        setTitleText(R.string.str_persion_message);
        rel_complete_student_invite.setVisibility(View.GONE);

        setZuiRightBtn(R.drawable.navigationbar_save);
        showTitleBt(ZUI_RIGHT_BUTTON, true);

        configImageLoader();
        //bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.i_person_img, R.drawable.i_person_img);
        et_comp_mater_name = (TextView) findViewById(R.id.et_comp_mater_name);
        et_comp_mater_school = (TextView) findViewById(R.id.et_comp_mater_school);
        et_comp_mater_banji = (TextView) findViewById(R.id.et_comp_mater_banji);
        et_comp_mater_xue_hao = (TextView) findViewById(R.id.et_comp_mater_xue_hao);

        et_comp_mater_sex = (TextView) findViewById(R.id.et_comp_mater_sex);
        et_comp_mater_nipick = (TextView) findViewById(R.id.et_comp_mater_nipick);
        et_comp_date_both = (TextView) findViewById(R.id.et_comp_date_both);
        et_comp_zheng_zhi = (TextView) findViewById(R.id.et_comp_zheng_zhi);
        et_comp_hometown = (TextView) findViewById(R.id.et_comp_hometown);
        et_comp_mater_invite_person = (TextView) findViewById(R.id.et_comp_mater_invite_person);
        iv_account_por = (CircleImageView) findViewById(R.id.iv_account_por);


        rel_complete_name = (RelativeLayout) findViewById(R.id.rel_complete_name);
        rel_complete_school = (RelativeLayout) findViewById(R.id.rel_complete_school);
        rel_complete_class = (RelativeLayout) findViewById(R.id.rel_complete_class);
        rel_complete_student_no = (RelativeLayout) findViewById(R.id.rel_complete_student_hh);

        rel_complete_nipic = (RelativeLayout) findViewById(R.id.rel_complete_nipic);
        rel_complete_sex = (RelativeLayout) findViewById(R.id.rel_complete_sex);
        rel_complete_birthday = (RelativeLayout) findViewById(R.id.rel_complete_birthday);
        rel_complete_zhengzhi = (RelativeLayout) findViewById(R.id.rel_complete_zhengzhi);
        rel_complete_name.setOnClickListener(onclick);
        rel_complete_school.setOnClickListener(onclick);
        rel_complete_class.setOnClickListener(onclick);
        rel_complete_student_no.setOnClickListener(onclick);

        rel_complete_nipic.setOnClickListener(onclick);
        rel_complete_sex.setOnClickListener(onclick);
        rel_complete_birthday.setOnClickListener(onclick);
        rel_complete_zhengzhi.setOnClickListener(onclick);
        rel_complete_student_invite.setOnClickListener(onclick);
        if (A_0_App.getInstance() != null && A_0_App.getInstance().getStudentInfo() != null) {
            showLoadResult(false, true, false);
            showUserInfo();
        } else {
            readUserInfo();
        }

        iv_account_por.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                upLoadPhoto = true;

                photo();

            }
        });
    }

    OnClickListener onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            String student_status = A_0_App.getInstance().getStudentInfo().getStudent_status();
            switch (v.getId()) {
                case R.id.rel_complete_name://审核中   已激活    不可改      审核失败可以改
                    upLoadPhoto = false;
                    if (student_status.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE) ||
                            student_status.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {//审核失败和未完善信息用户
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Modify_Acy.class);
                        intent.putExtra("title_name", TITLE_USER_TURE_NAME);
                        intent.putExtra("content", et_comp_mater_name.getText().toString());
                        startActivityForResult(intent, 1);
                    } else if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "您已是认证用户，不能修改，如需修改请联系班主任或辅导员");
                    }
                    break;
                case R.id.rel_complete_school://审核中   已激活    不可改      审核失败可以改
                    upLoadPhoto = false;
                    if (student_status.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE) ||
                            student_status.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {//审核失败和未完善信息用户
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Selcet_School.class);
                        intent.putExtra("title_name", "选择学校");
                        intent.putExtra("user_phone", A_0_App.getInstance().getStudentInfo().getPhone());
                        startActivityForResult(intent, 2);
                    } else if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "您已是认证用户，不能修改，如需修改请联系班主任或辅导员");
                    }
                    break;
                case R.id.rel_complete_class://审核中   已激活    不可改      审核失败可以改
                    upLoadPhoto = false;
                    if (student_status == null) {
                        return;
                    }
                    if (student_status.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE) ||
                            student_status.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {//审核失败和未完善信息用户
                        if (null == schoolId || "".equals(schoolId)) {
                            PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请先选择学校");
                            return;
                        }
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Selcet_Class.class);
                        intent.putExtra("title_name", "选择班级");
                        intent.putExtra("schoolId", schoolId);
                        startActivityForResult(intent, 3);
                    } else if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "您已是认证用户，不能修改，如需修改请联系班主任或辅导员");
                    }
                    break;
                case R.id.rel_complete_student_hh: //已激活     审核中  不可改
                    upLoadPhoto = false;
                    if (student_status == null) {
                        return;
                    }
                    if (student_status.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE) ||
                            student_status.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {//审核失败和未完善信息用户
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Modify_Acy.class);
                        intent.putExtra("title_name", TITLE_USER_STUDENT_NO);
                        intent.putExtra("content", et_comp_mater_xue_hao.getText().toString());
                        startActivityForResult(intent, 4);
                    } else if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "您已是认证用户，不能修改，如需修改请联系班主任或辅导员");
                    }
                    break;
                case R.id.rel_complete_sex: //已激活   审核中  不可改
                    upLoadPhoto = false;
                    if (student_status.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)) {
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Nodify_Acy.class);
                        intent.putExtra("title_name", "性别");
                        intent.putExtra("content", et_comp_mater_sex.getText().toString());
                        startActivityForResult(intent, 6);
                    }
                    break;
                case R.id.rel_complete_nipic: //审核中不可改
                    upLoadPhoto = false;
                    if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Modify_Acy.class);
                        intent.putExtra("title_name", TITLE_USER_NIPICK);
                        intent.putExtra("content", et_comp_mater_nipick.getText().toString());
                        startActivityForResult(intent, 5);
                    }
                    break;
                case R.id.rel_complete_birthday://审核中不可改
                    upLoadPhoto = false;
                    if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        date_choose();
                    /*DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(A_3_3_Complete_marer_Acy.this, "1990年1月1日 00:00");
					dateTimePicKDialog.dateTimePicKDialog(et_comp_date_both);*/
                    }
                    break;
                case R.id.rel_complete_zhengzhi://审核中不可改
                    upLoadPhoto = false;
                    if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Nodify_Acy.class);
                        intent.putExtra("title_name", "政治面貌");
                        intent.putExtra("content", et_comp_zheng_zhi.getText().toString());
                        startActivityForResult(intent, 8);
                    }
                    break;
                case R.id.rel_complete_student_invite: //邀请人审核中不可改
                    upLoadPhoto = false;
                    if (student_status.equals("0")) {//审核中
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在审核，无法修改！");
                    } else {
                        intent.setClass(A_3_3_Complete_marer_Acy.this, A_3_3_Complete_Marer_Modify_Acy.class);
                        intent.putExtra("title_name", TITLE_USER_INVITATION_NAME);
                        intent.putExtra("content", et_comp_mater_invite_person.getText().toString());
                        startActivityForResult(intent, 11);
                    }
                    break;

                default:
                    break;
            }

        }
    };

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (upLoadPhoto) {
            try {
                if (resultCode != RESULT_CANCELED) {
                    switch (requestCode) {
                        case IMAGE_REQUEST_CODE:
                            if (data != null) {
                                startPhotoZoom(data.getData());
                            }
                            break;
                        case CAMERA_REQUEST_CODE:
                            String path_temp = Environment.getExternalStorageDirectory() + "/"
                                    + A_3_3_Complete_marer_Acy.temppath + IMAGE_FILE_NAME;
                            Bitmap bitmap = Bimp.revitionImageSize(path_temp);
                            Bitmap bitmap2 = PubMehods.rotateBitmapByDegree(bitmap, PubMehods.getBitmapDegree(path_temp));
                            saveMyBitmap(A_3_3_Complete_marer_Acy.temppath + "pic", bitmap2);
                            File tempFile = new File(path_temp);
                            startPhotoZoom(Uri.fromFile(tempFile));
                            break;
                        case RESULT_REQUEST_CODE:
                            if (data != null) {
                                getImageToView(data);
                            }
                            break;
                    }
                }
            } catch (Exception e) {

            }
        } else {

            if (data != null && data.getExtras() != null && data.getExtras().getString("modify_content") != null) {

                String result = data.getExtras().getString("modify_content");
                if (resultCode == 11) {
                    //邀请人
                    et_comp_mater_invite_person.setText(result);
                }
                if (!"".equals(result)) {
                    if (requestCode == 1) {
                        // 名字
                        et_comp_mater_name.setText(result);
                    } else if (requestCode == 2) {
                        // 学校
                        schoolId = data.getExtras().getString("school_id");
                        et_comp_mater_school.setText(result);

                        organ_id = "";
                        et_comp_mater_banji.setText(null);

                        A_0_App.logE(schoolId + result);
                    } else if (requestCode == 3) {
                        // 班级
                        et_comp_mater_banji.setText(result);
                        organ_id = data.getExtras().getString("class_id");

                    } else if (requestCode == 4) {
                        // 学号
                        et_comp_mater_xue_hao.setText(result);
                    } else if (requestCode == 5) {
                        // 昵称
                        et_comp_mater_nipick.setText(result);
                    } else if (requestCode == 6) {
                        // 性别
                        et_comp_mater_sex.setText(result);
                    } else if (requestCode == 8) {
                        // 政治面貌
                        et_comp_zheng_zhi.setText(result);
                    } else if (requestCode == IMAGE_REQUEST_CODE) {
                        startPhotoZoom(data.getData());
                    } else if (requestCode == CAMERA_REQUEST_CODE) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory() + "/" + A_3_3_Complete_marer_Acy.temppath
                                        + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else if (requestCode == RESULT_REQUEST_CODE) {
                        getImageToView(data);
                    }
                }
            }
        }
    }

    //	激活用户 获取已有资料  不能改的  #FF6699
    //	激活用户 获取已有资料   能改的   #333333    3-2-3
    //	个人资料   审核中   #666666
    //	个人资料   审核失败    #333333       
    //	个人资料   其他        不能改（前5个）   #666666（后面的）   能改    #333333         5-3
    //	新用户  “必填”2字   #27BAFF
    //	新用户  时  #333333   

    private void showUserInfo() {
        phont_url = A_0_App.getInstance().getStudentInfo().getPhoto_url();
        String name = A_0_App.getInstance().getStudentInfo().getName();
        String school = A_0_App.getInstance().getStudentInfo().getSchool_name();
        String banji = A_0_App.getInstance().getStudentInfo().getClass_name();
        String xuehao = A_0_App.getInstance().getStudentInfo().getStudent_number();

        String sex = A_0_App.getInstance().getStudentInfo().getSex();
        String nipick = A_0_App.getInstance().getStudentInfo().getUsername();
        String birthday = A_0_App.getInstance().getStudentInfo().getBirthday();
        String zhengzhi = A_0_App.getInstance().getStudentInfo().getPolitics();
//			String hometown = A_0_App.getInstance().getStudentInfo().get

        schoolId = A_0_App.getInstance().getStudentInfo().getSchool_id();
        organ_id = A_0_App.getInstance().getStudentInfo().getClass_id();

        if (phont_url != null && phont_url.length() > 0 && !phont_url.equals("")) {
            String uri = phont_url;
            if (iv_account_por.getTag() == null) {
                PubMehods.loadServicePic(imageLoader, uri, iv_account_por, options);
                iv_account_por.setTag(uri);
            } else {
                if (!iv_account_por.getTag().equals(uri)) {
                    PubMehods.loadServicePic(imageLoader, uri, iv_account_por, options);
                    iv_account_por.setTag(uri);
                }
            }
            //bitmapUtils.display(iv_account_por, phont_url);
        } else
            iv_account_por.setBackgroundResource(R.drawable.i_default_por_120);

        if (name != null && name.length() > 0)
            et_comp_mater_name.setText(name);
        if (school != null && school.length() > 0)
            et_comp_mater_school.setText(school);
        if (banji != null && banji.length() > 0)
            et_comp_mater_banji.setText(banji);
        if (xuehao != null && xuehao.length() > 0)
            et_comp_mater_xue_hao.setText(xuehao);


        if (sex != null && sex.length() > 0) {
            if (sex.equals("1")) {
                et_comp_mater_sex.setText("男");
            } else {
                et_comp_mater_sex.setText("女");
            }
        }

        recommend_phone = A_0_App.getInstance().getStudentInfo().getRecommend_phone();
        if (recommend_phone != null) {
            if (recommend_phone.equals("") || recommend_phone.length() <= 0) {
                rel_complete_student_invite.setVisibility(View.VISIBLE);
                view_divider.setVisibility(View.VISIBLE);
            } else {
                rel_complete_student_invite.setVisibility(View.GONE);
                view_divider.setVisibility(View.GONE);
            }

        } else {
            recommend_phone = "";
            view_divider.setVisibility(View.GONE);
        }

        if (nipick != null && nipick.length() > 0)
            et_comp_mater_nipick.setText(nipick);

        if (birthday != null && birthday.length() > 0)
            et_comp_date_both.setText(birthday);

        if (zhengzhi != null && zhengzhi.length() > 0)
            et_comp_zheng_zhi.setText(zhengzhi);

//			if (hometown != null && hometown.length() > 0)
//			et_comp_hometown.setText(nipick);

        if (A_0_App.USER_STATUS.equals("0")) {
            et_comp_mater_name.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_school.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_banji.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_xue_hao.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_sex.setTextColor(getResources().getColor(R.color.start_title_col));

            et_comp_mater_nipick.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_date_both.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_zheng_zhi.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_invite_person.setTextColor(getResources().getColor(R.color.start_title_col));
        } else if (A_0_App.USER_STATUS.equals("5") || A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {
            et_comp_mater_name.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_school.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_banji.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_xue_hao.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_sex.setTextColor(getResources().getColor(R.color.black_code));

            et_comp_mater_nipick.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_date_both.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_zheng_zhi.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_invite_person.setTextColor(getResources().getColor(R.color.black_code));
        } else {
            et_comp_mater_name.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_school.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_banji.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_xue_hao.setTextColor(getResources().getColor(R.color.start_title_col));
            et_comp_mater_sex.setTextColor(getResources().getColor(R.color.start_title_col));

            et_comp_mater_nipick.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_date_both.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_zheng_zhi.setTextColor(getResources().getColor(R.color.black_code));
            et_comp_mater_invite_person.setTextColor(getResources().getColor(R.color.black_code));
        }
    }

    private void SaveUserInfo(String name, String sex, String username, String student_number,
                              String birthday, String politics, String school_id, String organ_id, String photo_url) {
        A_0_App.getInstance().showProgreDialog(A_3_3_Complete_marer_Acy.this, "正在注册，请稍候", false);
        A_0_App.getApi().SaveUserInfo(A_3_3_Complete_marer_Acy.this, recommend_phone, name, sex, username,
                student_number, birthday, politics, school_id, organ_id, photo_url, A_0_App.USER_TOKEN, new InterSavePerInfo() {
                    @Override
                    public void onSuccess(String message, String user_status) {
                        modifyPersionInf(A_0_App.USER_UNIQID, A_0_App.USER_NAME, phont_url);
                        A_0_App.getInstance().updateUserLoginInfo(user_status);
                        A_0_App.getInstance().CancelProgreDialog(A_3_3_Complete_marer_Acy.this);
                        if (message != null && !message.equals(""))
                            PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, message);
                        finish();
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        A_0_App.getInstance().CancelProgreDialog(A_3_3_Complete_marer_Acy.this);
                        PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });

    }


    private void configImageLoader() {
        imageLoader = ImageLoader.getInstance();
        options = A_0_App.getInstance().getOptions(R.drawable.i_default_por_120,
                R.drawable.i_default_por_120,
                R.drawable.i_default_por_120);
    }

    private void readUserInfo() {
        A_0_App.getApi().getStudentInfo(A_3_3_Complete_marer_Acy.this, A_0_App.USER_UNIQID,
                A_0_App.USER_TOKEN, new InterStudentInfo() {
                    @Override
                    public void onSuccess(Cpk_Student_Info student) {
                        if (isFinishing())
                            return;
                        if (A_0_App.USER_UNIQID.equals(student.getUniqid())) {
                            A_0_App.getInstance().updateUserLoginInfo(student.getStudent_status(), student.getUniqid(), student.getPhone(),
                                    student.getName(), student.getPhoto_url());
                        }
                        A_0_App.getInstance().setStudentInfo(student);
                        showUserInfo();
                        showLoadResult(false, true, false);
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
//                        A_0_App.getInstance().CancelProgreDialog(A_Main_My_Account_Acy.this);
                        if (msg.equals(AppStrStatic.TAG_USER_IS_DELETE)) {
                            A_0_App.getInstance().showExitDialog(A_3_3_Complete_marer_Acy.this, getResources().getString(R.string.str_user_is_delete));
                            PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "该用户已删除");
                        } else {
                            showLoadResult(false, false, true);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_BUTTON:

                String temp_sex = et_comp_mater_sex.getText().toString();
                String sex;

                String politics = et_comp_zheng_zhi.getText().toString();
                String niPick = et_comp_mater_nipick.getText().toString();
                String birthday = et_comp_date_both.getText().toString().replace(" ", "");

                String name = et_comp_mater_name.getText().toString();
                String number = et_comp_mater_xue_hao.getText().toString();
                String recommend_phone = et_comp_mater_invite_person.getText().toString();
                if (null == name || "".equals(name)) {
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请输入你的真实名字");
                    return;
                }

                if (null == schoolId || "".equals(schoolId)) {
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请选择你的学校");
                    return;
                }
                if (null == organ_id || "".equals(organ_id)) {
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请选择你的班级");
                    return;
                }
                if (null == number || "".equals(number)) {
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请输入你的学号");
                    return;
                }
                if (null == temp_sex || "".equals(temp_sex)) {
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "请选择你的性别");
                    return;
                }
                if (temp_sex.equals("男")) {
                    sex = "1";
                } else {
                    sex = "0";
                }

                if (A_0_App.getInstance().getStudentInfo().getStudent_status().equals("0")) {//审核中
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "信息审核中，无法修改");
                } else if (A_0_App.getInstance().getStudentInfo().getStudent_status().equals("5")) { //认证用户
                    if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                        SaveUserInfo(name, sex, niPick, number, birthday, politics, schoolId, organ_id, phont_url);
                    } else {

                    }

                } else {
                    if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                        SaveUserInfo(name, sex, niPick, number, birthday, politics, schoolId, organ_id, phont_url);
                    } else {

                    }
                }
                break;
            default:
                break;
        }
    }

    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.complete_load_error:
                    showLoadResult(true, false, false);
                    readUserInfo();
                    break;
                default:
                    break;
            }
        }
    };

    private void showLoadResult(boolean loading, boolean wholeView, boolean loadFaile) {

        if (wholeView)
            liner_acy_list_whole_view.setVisibility(View.VISIBLE);
        else
            liner_acy_list_whole_view.setVisibility(View.GONE);

        if (loadFaile)
            mLinerReadDataError.setVisibility(View.VISIBLE);
        else
            mLinerReadDataError.setVisibility(View.GONE);

        if (loading) {
            drawable.start();
            complere_acy_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            complere_acy_loading.setVisibility(View.GONE);
        }
    }

    /**
     * 头像
     */
    private static final String IMAGE_FILE_NAME = "pic.jpg";
    String pic;

    Bitmap photo;

    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            saveMyBitmap(A_3_3_Complete_marer_Acy.temppath + "pic", photo);

            pic = android.os.Environment.getExternalStorageDirectory()
                    + AppStrStatic.SD_PIC + "/" + A_3_3_Complete_marer_Acy.temppath + "pic.jpg";
            PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "正在上传头像");
            A_0_App.getInstance().showProgreDialog(A_3_3_Complete_marer_Acy.this, "请稍候", false);

            A_0_App.getApi().upload_Photo(new File(pic), new Inter_UpLoad_Photo() {

                @Override
                public void onSuccess(String imageUrl) {
                    A_0_App.getInstance().CancelProgreDialog(A_3_3_Complete_marer_Acy.this);
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "上传头像成功");
                    phont_url = "";
                    phont_url = imageUrl;
                    A_0_App.logE(imageUrl);
                    Drawable drawable = new BitmapDrawable(photo);
                    iv_account_por.setImageDrawable(drawable);
                }

                @Override
                public void onLoading(long arg0, long arg1, boolean arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStart() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWaiting() {
                    // TODO Auto-generated method stub

                }
            }, new Inter_Call_Back() {

                @Override
                public void onFinished() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFailure(String msg) {
                    A_0_App.getInstance().CancelProgreDialog(A_3_3_Complete_marer_Acy.this);
                    PubMehods.showToastStr(A_3_3_Complete_marer_Acy.this, "头像上传失败！请重新上传！");
                }

                @Override
                public void onCancelled() {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    // 保存图片
    public void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File dir = new File(
                android.os.Environment.getExternalStorageDirectory() + AppStrStatic.SD_PIC);
        if (!dir.exists() && !dir.isDirectory())
            dir.mkdir();
        if (mBitmap == null)
            return;

        FileOutputStream fos;
        try {
            File bitmapFile = new File(
                    android.os.Environment.getExternalStorageDirectory()
                            + AppStrStatic.SD_PIC, bitName + ".jpg");

            fos = new FileOutputStream(bitmapFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 头像上传
     */
    void photo() {
        final Dialog dialog = new Dialog(A_3_3_Complete_marer_Acy.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.choosepicture);
        dialog.show();
        Button btn1 = (Button) dialog.findViewById(R.id.btn1);
        Button btn2 = (Button) dialog.findViewById(R.id.btn2);
        Button btn3 = (Button) dialog.findViewById(R.id.btn3);
        btn3.setText("取消");
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //拍照 如果是6.0 弹窗  否则  提示
                dialog.dismiss();
                PermissionGen.needPermission(A_3_3_Complete_marer_Acy.this, REQUECT_CODE_CAMERA,
                        new String[]{
                                Manifest.permission.CAMERA
                        });
            }
        });
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*");
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery,
                        IMAGE_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        btn3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    ;
    private static final int REQUECT_CODE_CAMERA = 2;
    private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
    private static final int REQUECT_CODE_CALLPHONE = 4;

    //    PermissionGen.needPermission(MainActivity.this, 100,
//            new String[] {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CALL_PHONE
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQUECT_CODE_CAMERA)
    public void requestPhotoSuccess() {
        openPhoto();
    }

    @PermissionFail(requestCode = REQUECT_CODE_CAMERA)
    public void requestPhotoFailed() {
        A_0_App.getInstance().PermissionToas("摄像头", A_3_3_Complete_marer_Acy.this);
    }


    private void openPhoto() {//拍照 方法
        A_3_3_Complete_marer_Acy.temppath = System.currentTimeMillis() + "";
        Intent intentFromCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), A_3_3_Complete_marer_Acy.temppath + IMAGE_FILE_NAME)));
        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    public static boolean getCameraPermission(Context context) {

        boolean isCanUse = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
            isCanUse = true;
        } catch (Exception e) {
            isCanUse = false;
        } finally {
            // 释放相机，这个必须要，必须要，必须要！！！！
            if (mCamera != null) {
                try {
                    mCamera.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return isCanUse;
        }
    }

    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(A_3_3_Complete_marer_Acy.this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 200);
        intent.putExtra("aspectY", 200);

        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    // 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public void date_choose() {

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                //.setMinDate(minDate)
                //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(Color.parseColor("#1EC348"))
                .build()
                .show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //bitmapUtils=null;
        super.onDestroy();
    }


    // 修改单个人的融云信息
    private void modifyPersionInf(final String target, final String name, final String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(target, name, uri));
    }
}
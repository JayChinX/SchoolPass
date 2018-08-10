
package com.yuanding.schoolpass.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.x;
import org.xutils.image.ImageOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.view.PublicShowToast;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月2日 下午4:21:14 公共类
 */
public class PubMehods {

    // 手机号判断
    // public static boolean isMobileNO(String mobiles){
    // Pattern p =
    // Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
    // Matcher m = p.matcher(mobiles);
    // return m.matches();
    // }

    public static boolean isMobileNO(String mobiles) {
        boolean isTel = true; // 标记位：true-是手机号码；false-不是手机号码
        if (mobiles.length() == 11) {
            for (int i = 0; i < mobiles.length(); i++) {
                char c = mobiles.charAt(i);
                if (!Character.isDigit(c)) {
                    isTel = false;
                    break;
                }
            }
        } else {
            isTel = false;
        }
        return isTel;
    }

    // 拨号界面
    public static void callPhoneAcy(Context con, String no) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ""));
        con.startActivity(intent);
    }

    public static void callPhone(Context con, String no) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(con.checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + no));
                con.startActivity(intent);
            }else{
                A_0_App.getInstance().PermissionToas("拨打电话", (Activity)con);
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + no));
            con.startActivity(intent);
        }
    }

    // 邮箱号判断
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    // 从字符串转为字符串
    public static String switchMoneyMeDian(String str) {
        if ((!"".equals(str)) && (str != null)) {
            int dian = str.indexOf(".");
            return str.substring(0, dian + 3);
        } else {
            return str;
        }
    }
    
    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     * @param String s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int getStrLength(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }
    
    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     * 
     * @param String s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static double getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }
    
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    // 截取字符串
    public static String formatStr(String s, int count) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("Unicode");
        int nWord = 0;

        int n = 0;
        int i = 2;
        for (; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                n++;
                if (i % 2 == 0) {
                    nWord++;
                }
            }
            if (n >= count) {
                return s.substring(0, nWord) + "...";
            }
        }
        return s;
    }

    /**
     * 手机状态的判断 静音模式返回true
     */
    public static boolean judgePhoneModel(Context con) {
        AudioManager audioMa = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);
        if (audioMa.getRingerMode() == AudioManager.RINGER_MODE_SILENT
                || audioMa.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            return true;
        }
        return false;
    }

    public static int getVerCode(Context con) {
        int verCode = -1;
        try {
            verCode = con.getPackageManager().getPackageInfo(getAppPackageName(con), 1).versionCode;
        } catch (Exception e) {
            verCode = AppStrStatic.VERSION_CODE;
        }
        return verCode;
    }

    public static String getVerName(Context con) {
        String verName = "";
        try {
            verName = con.getPackageManager().getPackageInfo(getAppPackageName(con), 1).versionName;
        } catch (Exception e) {
            verName = AppStrStatic.VERSION_NAME;
        }
        return verName;

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序包名称
     */
    public static String getAppPackageName(Context context) {
        String packName = "";
        try {
            packName = context.getPackageName();
        } catch (Exception e) {
            packName = AppStrStatic.PACKAGE_NAME;
        }
        return packName;
    }

    /**
     * 参数为Int类型
     */
    public static void showToastStr(Context con, int str) {
        if (!((Activity) con).isFinishing())
            PublicShowToast.showCustHighStr(con, con.getResources().getString(str),
                    (int) con.getResources().getDimension(R.dimen.space_60));
    }

    /**
     * 参数为Str类型
     */
    public static void showToastStr(Context con, String str) {
        if (!((Activity) con).isFinishing())
            PublicShowToast.showCustHighStr(con, str,
                    (int) con.getResources().getDimension(R.dimen.space_60));
    }

    /**
     * 打开软键盘
     * 
     * @param mEditText 输入框
     * @param mContext 上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext)
    {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     * 
     * @param mEditText 输入框
     * @param mContext 上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext)
    {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * @Title: getMD5
     * @Description: TODO(md5 加密)
     * @param @param info
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(
                            Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /************************************************************************************/

    /**
     * 计算时间差 当天显示时间，比如：08:30（冒号为英文小写） 昨天显示“昨天” 昨天以前本周之内显示星期，如“周一”
     * 本周以前显示日期，如“10/20”
     */
    public static String getTimeHint(long oldTime, long newTime) {
        String str = null;
        int oneDayInterVal = 24 * 60 * 60;
        long currentTime;
        if (newTime == 0) {
            currentTime = Long.valueOf(subStrTime(System.currentTimeMillis()));
        } else {
            currentTime = Long.valueOf(subStrTime(newTime));
        }

        String endTime = getFormatDate(currentTime, "yyyy/MM/dd") + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dt2 = null;
        try {
            dt2 = sdf.parse(endTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long time_24_Interval = currentTime - dt2.getTime() / 1000;
        long time_old_new_interval = currentTime - oldTime;

        if (time_old_new_interval <= time_24_Interval) {
            str = getFormatDate(oldTime, "HH:mm");
        } else if (time_old_new_interval > time_24_Interval
                && time_old_new_interval <= (time_24_Interval + oneDayInterVal)) {
            str = "昨天";
        } else {
            long start = 0, end = 0;

            if (StringData() == 1) {
                start = currentTime - time_24_Interval;
                end = currentTime - time_24_Interval + 7 * oneDayInterVal;
            }

            if (StringData() == 2) {
                start = currentTime - time_24_Interval - oneDayInterVal;
                end = currentTime - time_24_Interval + 6 * oneDayInterVal;
            }

            if (StringData() == 3) {
                start = currentTime - time_24_Interval - 2 * oneDayInterVal;
                end = currentTime - time_24_Interval + 5 * oneDayInterVal;
            }

            if (StringData() == 4) {
                start = currentTime - time_24_Interval - 3 * oneDayInterVal;
                end = currentTime - time_24_Interval + 4 * oneDayInterVal;
            }
            if (StringData() == 5) {
                start = currentTime - time_24_Interval - 4 * oneDayInterVal;
                end = currentTime - time_24_Interval + 3 * oneDayInterVal;
            }
            if (StringData() == 6) {
                start = currentTime - time_24_Interval - 5 * oneDayInterVal;
                end = currentTime - time_24_Interval + 2 * oneDayInterVal;
            }
            if (StringData() == 7) {
                start = currentTime - time_24_Interval - 6 * oneDayInterVal;
                end = currentTime - time_24_Interval + 1 * oneDayInterVal;
            }
            if (oldTime >= start && oldTime < end) {// 本周内
                str = dayForWeek(getFormatDate(oldTime, "yyyy-MM-dd"));
            } else {// 本周外
                str = getFormatDate(oldTime, "MM/dd");
            }
        }
        return str;
    }

    // 将时间戳转为字符串 格式yyyy/MM/dd HH:mm:ss 此格式支持24小时
    public static String getFormatDate(long cc_time, String pattern) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(cc_time * 1000L));

        return re_StrTime;

    }

    // 将时间戳转为字符串 格式月/日/ 此格式支持24小时
    public static String getFormatDat(long cc_time) {
        String re_StrTime = null;
        Date date = new Date(cc_time * 1000L);
        String month = (date.getMonth() + 1) + "";
        String day = date.getDate() + "";
        re_StrTime = month + "月" + day + "日";
        return re_StrTime;

    }

    // 将时间戳转为字符串 格式月/日/ 此格式支持24小时
    public static String getFormatDateMor(long cc_time) {
        String re_StrTime = null;
        Date date = new Date(cc_time * 1000L);
        String month = (date.getMonth() + 1) + "";
        String day = date.getDate() + "";
        String mintue = date.getHours() + "";
        String second = date.getMinutes() + "";
        re_StrTime = month + "月" + day + "日     " + mintue + ":" + second;
        return re_StrTime;

    }
    
    //将date转换为时间戳
    public static long getFormatTime(Date date){
    	
    	return date.getTime();
    }
  //将时间戳转换为date
    public static Date getFormatDate(long time){
    	 Date date = new Date(time);
    	return date;
    }
    
    /**
     * 
     * @Title: checkUpdate
     * @Description: TODO(是否检查更新)
     * @return boolean    true=需要，false=不需要
     */
    public static boolean checkUpdate(long last,long currentTime) {
        if (last == 0) {//安装后第一次检查
            return true;
        }
        long lastTime = Long.valueOf(last);
        long diff_day = (currentTime - lastTime) / (1000 * 3600 * 24);

        if (((int) diff_day) >= 1) {
            return true;
        }else{
            if (isSameDayOfMillis(lastTime, currentTime)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    /**
     * 计算时间差,判断是否间隔一天
     */
    private boolean calueTime(long lastTime,long currentTime) {
        long diff_day = (currentTime - lastTime) / (1000 * 3600 * 24);
        if (((int) diff_day) >= 1) {
            return true;
        }else{
            if (isSameDayOfMillis(lastTime, currentTime)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    //判断是否为同一天
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY && toDay(ms1) == toDay(ms2);
    }
    
    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    // 计算活动讲座距离开始结束的时间 天
    public static String get_ac_RemainDate(long cc_time) {
        // 1代表讲座讲座显示到秒，2表示活动，活动显示到天
        int day = (int) cc_time / 3600 / 24 + 1;
        return day + "";
    }

    // 计算活动讲座距离开始结束的时间
    public static String getRemainDate(long cc_time) {
        // 1代表讲座讲座显示到秒，2表示活动，活动显示到天
        int day = (int) cc_time / 3600 / 24;
        return day + "";
    }

    // 计算活动讲座距离开始结束的时间
    public static String getRemainTime(long cc_time) {
        // 1代表讲座讲座显示到秒，2表示活动，活动显示到天
        int day = (int) cc_time / 3600 / 24;
        int hour = (int) cc_time / 3600 % 24;
        int min = (int) cc_time / 60 % 60;
        int second = (int) cc_time % 60;
        return hour + "";
    }

    // 计算活动讲座距离开始结束的时间
    public static String getRemainMinute(long cc_time) {
        // 1代表讲座讲座显示到秒，2表示活动，活动显示到天
        int day = (int) cc_time / 3600 / 24;
        int hour = (int) cc_time / 3600 % 24;
        int min = (int) cc_time / 60 % 60;
        int second = (int) cc_time % 60;
        return min + "";
    }

    // 今天是周几判断
    public static int StringData() {
        Calendar c = Calendar.getInstance();
        // int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int day = c.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
            // week=week-1;
        } else {
            day = day - 1;
        }
        return day;
    }

    public static String dayForWeek(String pTime) {
        Calendar calendar = Calendar.getInstance();
        String[] temp = pTime.split("-");
        int year = Integer.valueOf(temp[0]);
        int month = Integer.valueOf(temp[1]);
        int date = Integer.valueOf(temp[2]);
        calendar.set(year, month - 1, date);
        int number = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] str = {
                "周日", "周一", "周二", "周三", "周四", "周五", "周六",
        };

        return str[number];
    }

    public static String subStrTime(long time) {
        String str = time + "";
        if (time > 0) {
            str = str.substring(0, 10);
        }
        return str;
    }

    // 为月份+/n
    public static String subMonthAdd(String timeStr) {
        String str = timeStr.substring(0, timeStr.length() - 1) + "\n"
                + timeStr.substring(timeStr.length() - 1, timeStr.length());
        return str;
    }

    // 提取字符串中的数组
    public static String subStrNum(String str) {

        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        str = m.replaceAll("").trim();
        return str;
    }

    // 随机数字的获取(不包含传入的这个整形)
    public static int getRandomIntData(int duringNum) {
        Random r = new Random();
        int n3 = r.nextInt(duringNum);
        ;
        return n3;
    }

    /*
     *  *****************************************间隔虚拟日期的计算************************
     * ***********************
     */

    /**
     * 计算两个日期之间相差的天数
     * 
     * @param smdate较小的时间
     * @param bdate较大的时间
     * @return 相差天数
     * @throws ParseException 未来时间与当前时间的差额
     */
    public static String getTimeDiffeng(long fectureTime) {
        Date smdate = new Date(Long.valueOf(subStrTime(System.currentTimeMillis())) * 1000);
        Date bdate = new Date(fectureTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            bdate = sdf.parse(sdf.format(bdate));
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        if (between_days <= 0)
            return "0";
        return String.valueOf(between_days);
    }

    //去掉非中文和特殊符
    public static boolean judgeStr(String str) {
        char[] nickchar = str.toCharArray();
        for (int i = 0; i < nickchar.length; i++) {
            if (!isChinese(nickchar[i])) {
                return false;
            }
            if (isFuHao(nickchar[i])) {
                return false;
            }
        }
        return true;
    }
    
    //特殊符号
    private static boolean isFuHao(char str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？《》]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(String.valueOf(str));
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    //中文
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    // 数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 字母
    public static boolean isLetter(String str) {
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 多次点击
    private static long lastClickTime;

    public synchronized static boolean isFastClick(int interval) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000 * interval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /*
     * 获取处理器型号
     */
    public static String compatibleCPU() {
        String CPU_ABI = "";
        try {
            CPU_ABI = android.os.Build.CPU_ABI;
        } catch (Exception e) {
            CPU_ABI = "获取处理器型号失败";
        }
        return CPU_ABI;
    }

    // 获取本机号码
    public static String getPhoneNo(Context context) {
        String tempStr, strPhoneNum = "";
        TelephonyManager phoneMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tempStr = phoneMgr.getLine1Number();
        if (tempStr != null && !tempStr.equals("")) {
            strPhoneNum = tempStr.substring(tempStr.length() - 11);
        }
        return strPhoneNum;
    }

    public static String getDeviceID(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = phoneMgr.getDeviceId(); // imei
        return imei;
    }

    // 获得当前年份
    public static String getCurentYear() {
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.YEAR) + "";
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
        public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
      
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
    private static InputFilter emojiFilter = new InputFilter() {

        Pattern emoji = Pattern.compile(

                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",

                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                int dstart,

                int dend) {

            Matcher emojiMatcher = emoji.matcher(source);

            if (emojiMatcher.find()) {

                return "";

            }
            return null;

        }
    };

    public static InputFilter[] emojiFilters = {
        emojiFilter
    };
    /**
     * @param codePoint过滤表情
     * @return false:不包含表情
     */
    public static boolean isEmojiCharacter(char codePoint) {
        if (codePoint == 0xFF0C || codePoint == 0xFF01)
        {
            return false;
        }
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
    
    /*
     * 检测是否有emoji表情
     * false:此字符串不包含表情
     */
    public static boolean containsEmoji(String source) {     
        int len = source.length();  
        for (int i = 0; i < len; i++) {  
            char codePoint = source.charAt(i);  
            if (isEmojiCharacter(codePoint)) {
                return true;  
            }  
        }  
        return false;  
    }

    /**
     * @param context分辨率转换
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    public static void saveSharePreferStr(Context mContext, String key, String value) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("wxbShareprefer",
                Context.MODE_PRIVATE);
        Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSharePreferStr(Context mContext, String key) {
        SharedPreferences sp = mContext
                .getSharedPreferences("wxbShareprefer", Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void saveHtml(String s, String name) {

        File f = new File(Environment.getExternalStorageDirectory() + "/" + name
                + ".html");

        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream outStream = new FileOutputStream(
                    Environment.getExternalStorageDirectory() + "/" + name
                            + ".html", true);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "utf-8");
            writer.write(s);
            writer.flush();
            writer.close();// 记得关闭

            outStream.close();
        } catch (Exception e) {
            Log.e("m", "file write error");
        }
    }

    /**
     * 计算相差多长时间
     * 
     * @param millis
     * @return
     */
    public static String getTimeDifference(long severTime, long millis) {
        if (severTime == 0) {
            severTime = System.currentTimeMillis();
        }
        long minute = getSubTractLongTime(millis, severTime);
        if (minute < 60) {
            if (minute < 1) {
                return "1分钟前";
            } else {
                return "" + minute + "分钟前";
            }
        } else if (minute >= 60 && minute < 60 * 24) {
            return "" + minute / 60 + "小时前";
        }
        // else if(minute >= 60*24 && minute < 60 * 24*2) {
        // return "昨天 "+PubMehods.getFormatDate(millis/1000, "HH:mm");
        // }
        else {
            return "" + PubMehods.getFormatDate(millis / 1000, "yyyy/MM/dd HH:mm");
        }
    }

    /**
     * 获取日期相差分钟
     * 
     * @param beginDate
     * @param endDate
     * @return
     */
    @SuppressLint("UseValueOf")
    public static int getSubTractLongTime(Long beginDate, Long endDate) {
        long day = (endDate - beginDate) / (60 * 1000);
        if (day < 0) {
            day = 0;
        }
        return new Long(day).intValue();
    }

    /*
     * 不能为空字符串
     */
    public static boolean judgeNoNullStr(String str_Str) {
        String str = str_Str.replaceAll(" ", "");
        if (!"".equals(str)) {
            return true;
        } else {
            return false;
        }
    }
    
    // ImageLoader异步加载服务器图片
    public static void loadServicePic(ImageLoader imageLoader, String uri, ImageView imageAware,
            DisplayImageOptions options) {
        if (imageLoader == null)
            return;
        if (uri == null) {
            uri = "";
        }
        imageLoader.displayImage(uri, imageAware, options);
    }
    
    // BitmapUtils异步加载服务器图片
    public static void loadBitmapUtilsPic(ImageOptions imageOptions, ImageView imageView, String uri) {
        if (imageOptions == null)
            return;
        if (uri == null) {
            uri = "";
        }
        x.image().bind(imageView, uri, imageOptions);
    }
    
    /**
     * 
     * @Title: splitStrWhereStr
     * @Description: TODO(裁切字符串根据成对相对应的特殊符号)
     * @param @param content
     * @param @param firstTag
     * @param @param endTag
     * @param @return    设定文件
     * @return SpannableStringBuilder    返回类型
     * @throws
     */
    public static SpannableStringBuilder splitStrWhereStr(Context context,int color,String content,String firstTag,String endTag){
        SpannableStringBuilder builderTemp_001 = null;
        if (null != content && content.length() > 0) {
            String[] contentArr001  = content.split(endTag); //将字符串以 #} 分成两部分 
            builderTemp_001 = new SpannableStringBuilder();
            try {
                if(null!=contentArr001) {
                    for (int i = 0; i < contentArr001.length; i++) {
                        if (i == contentArr001.length - 1) {
                            String[] contentArr002 = contentArr001[i].split(firstTag);
                            if (contentArr002.length > 1) {
                                String text_first = "";
                                String text_end = "";
                                if (contentArr002.length > 0)
                                    text_first = contentArr002[0];
                                if (contentArr002.length > 1)
                                    text_end = contentArr002[1];
                                if (null != text_first && null != text_end) {
                                    SpannableStringBuilder builder = new SpannableStringBuilder(text_first + text_end);      
                                    //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
                                    ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(context.getResources().getColor(color));  
                                    builder.setSpan(greenSpan2, text_first.length(), text_first.length()+text_end.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
                                    builderTemp_001.append(builder);
                                }
                            }else {
                                builderTemp_001.append(contentArr001[contentArr001.length - 1]);
                            }
                        } else {
                            String[] contentArr002 = contentArr001[i].split(firstTag);
                            String text_first = "";
                            String text_end = "";
                            if (contentArr002.length > 0)
                                text_first = contentArr002[0];
                            if (contentArr002.length > 1)
                                text_end = contentArr002[1];
                            if (null != text_first && null != text_end) {
                                SpannableStringBuilder builder = new SpannableStringBuilder(text_first + text_end);      
                                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
                                ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(context.getResources().getColor(color));  
                                builder.setSpan(greenSpan2, text_first.length(), text_first.length()+text_end.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
                                builderTemp_001.append(builder);
                            }
                        }
                    }
                    return builderTemp_001;
                };
            } catch (Exception e) {
            }
        }
        return builderTemp_001;
    }
    
    /**
   	 * 多个关键字高亮显示 textview.set(mSpannableString)
   	 * @param color
   	 * @param text
   	 * @param keyword
   	 * @return
   	 */
   	public static SpannableString matcherSearchTitle(int color, String text,  String[] keyword) {  
           SpannableString s = new SpannableString(text);  
           for (int i = 0; i < keyword.length; i++) {  
               Pattern p = Pattern.compile(keyword[i]);  
               Matcher m = p.matcher(s);  
               while (m.find()) {  
                   int start = m.start();  
                   int end = m.end();  
                   s.setSpan(new ForegroundColorSpan(color), start, end,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
               }  
           }  
           return s;  
       }
   	
    // 拼接密码长度提示
    public static String getPwdSetTitle() {
        return "密码为" + AppStrStatic.PWD_MIN_LENGTH + "-" + AppStrStatic.PWD_MAX_LENGTH + "位数字或字母，区分大小写";
    }
    
    // 拼接密码长度提示
    public static String ToastPwdSetTitle() {
        return "请确认密码的长度为" + AppStrStatic.PWD_MIN_LENGTH + "-" + AppStrStatic.PWD_MAX_LENGTH + "位的数字或字母";
    }
    
}

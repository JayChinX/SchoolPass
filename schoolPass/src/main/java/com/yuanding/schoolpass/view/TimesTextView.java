package com.yuanding.schoolpass.view;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.yuanding.schoolpass.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * @author qinxiaojie 
 * @version 创建时间：2017年2月7日  倒计时优化
 */
public class TimesTextView extends LinearLayout {
    private long mday, mhour, mmin, msecond;//天，小时，分钟，秒
    private boolean run = false; //是否启动了
    Timer timer = new Timer();
    TextView Vday, Vhour, Vmin, Vseconds;

    public TimesTextView(Context context) {
        super(context);
        iniUI(context);
    }

    public TimesTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        iniUI(context);
    }

    public void iniUI(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.view_time_texviews, null);
        Vday = (TextView) myView.findViewById(R.id.tv_days);
        Vhour = (TextView) myView.findViewById(R.id.tv_hours);
        Vmin = (TextView) myView.findViewById(R.id.tv_minutes);
        Vseconds = (TextView) myView.findViewById(R.id.tv_seconds);
        addView(myView);
    }

    public TimesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniUI(context);
    }

    private Handler mHandler = new Handler() {

    };

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void start() {
        if (!isRun()) {
            setRun(true);
            timer.schedule(task, 1000, 1000);
        }
    }

    /**
     * 根据传进来的时间差 为textview 赋值
     *
     * @param duration
     */
    public void setTimes(long duration) {
        Date date = new Date(duration);
        Date date1 = new Date(1L);
        mday = duration / 60 / 60 / 24;
        mhour = (duration - mday * 60* 60 * 24) / 3600;

        mmin = (duration - mhour * 60 * 60 - mday * 3600 * 24) / 60;
        msecond = (duration - mmin * 60 - mhour * 3600- mday * 3600 * 24);
    }
   

    /**
     * 倒计时计算
     */
    private void ComputeTime() {
        msecond--;
        if (msecond < 0) {
            mmin--;
            msecond = 59;
            if (mmin < 0) {
                mmin = 59;
                mhour--;
                if (mhour < 0) {
                    // 倒计时结束
                    mhour = 24;
                    mday--;
                }
            }
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            mHandler.post(new Runnable() {      // UI thread
                @Override
                public void run() {
                    run = true;
                    ComputeTime();
                    if (mday < 0) {
                        setVisibility(View.GONE);

                        setRun(false);
                    }
                    
                    Vday.setText(mday + "");
                    Vhour.setText(mhour < 10 ? ("0" + mhour) : mhour + "");
                    Vseconds.setText(msecond < 10 ? ("0" + msecond) : msecond + "");
                    Vmin.setText(mmin < 10 ? ("0" + mmin) : mmin + "");
                }
            });
        }
    };
}

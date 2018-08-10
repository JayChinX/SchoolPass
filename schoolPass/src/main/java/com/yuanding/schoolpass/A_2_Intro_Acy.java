package com.yuanding.schoolpass;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.LogUtils;



public class A_2_Intro_Acy extends Activity {
	
    private ViewPager mViewPager;
    private MyPagerAdapter myAdapter;
    private LayoutInflater mInflater;
    private int mCurrentIndex;
	private List<View> mList;
    private View mViewOne,mViewTwo,mViewThree,mViewFour = null;
    private ImageView mIVone,mIVtwo,mIVthree,mIVfour;
    private int[] images = {
            R.drawable.intro_one, R.drawable.intro_two,
            R.drawable.intro_three,R.drawable.intro_four};
    private Bitmap [] bitmap= null;
//    private ImageView mIvEnterHome;
	private boolean FirstScrolle = false;
	
	private ViewGroup pageControl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.introduct_acy);
		A_0_App.getInstance().addActivity(this);
//        mIvEnterHome=(ImageView)findViewById(R.id.enterHome);
		
        intiBitmap();
        myAdapter = new MyPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.awesomepager);
        
        mList = new ArrayList<View>();
        mInflater = getLayoutInflater();
        
        mViewOne = mInflater.inflate(R.layout.introduction_one, null);
        mViewTwo = mInflater.inflate(R.layout.introduction_two, null);
        mViewThree = mInflater.inflate(R.layout.introduction_three, null);
        mViewFour = mInflater.inflate(R.layout.introduction_four, null);
        
        mIVone = (ImageView)mViewOne.findViewById(R.id.iv_intro_one);
        mIVtwo = (ImageView)mViewTwo.findViewById(R.id.iv_intro_two);
        mIVthree = (ImageView)mViewThree.findViewById(R.id.iv_intro_three);
        mIVfour = (ImageView)mViewFour.findViewById(R.id.iv_intro_four);
       
        this.pageControl = (ViewGroup) this.findViewById(R.id.liner_arrow);
        		
        mIVone.setImageBitmap(bitmap[0]);
        mIVtwo.setImageBitmap(bitmap[1]);
        mIVthree.setImageBitmap(bitmap[2]);
        mIVfour.setImageBitmap(bitmap[3]);
        mList.add(mViewOne);
        mList.add(mViewTwo);
        mList.add(mViewThree);
        mList.add(mViewFour);
        
        
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                if (mCurrentIndex < arg0 + 1) {
                    mCurrentIndex++;
                } else {
                    mCurrentIndex--;
                }
                logD("" + arg0);
                generatePageControl();
            }

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == (mList.size() - 1)) {
					if (FirstScrolle) {
//						goMainAcy();//最后一页右滑退出
					}
					FirstScrolle = true;
				} else {
					FirstScrolle = false;
				}
			}

            public void onPageScrollStateChanged(int arg0) {

            }
        });
        
//		mIvEnterHome.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				goMainAcy();
//			}
//		});
		
        (mList.get(mList.size()-1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goMainAcy();
			}
		});
        
		mViewPager.setAdapter(myAdapter);
		generatePageControl();
		
	}
	
    private void intiBitmap() {
        int imageLength = images.length;
        bitmap = new Bitmap[imageLength];
        for (int i = 0; i < imageLength; i++) {
            bitmap[i] = readBitMap(images[i]);
        }
    }
	
    public Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = this.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

	private void generatePageControl() {
		pageControl.removeAllViews();
		for (int i = 0; i < mList.size(); i++) {
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 15, 15); 
			imageView.setLayoutParams(lp);
			if (mCurrentIndex == i) {
				if (mCurrentIndex == (mList.size()-1)) {
//					mIvEnterHome.setVisibility(View.VISIBLE);
				} else {
//					mIvEnterHome.setVisibility(View.GONE);
				}
				 imageView.setImageResource(R.drawable.intro_indicator_focused);
			} else {
				if (mCurrentIndex == (mList.size()-1)) {
//					mIvEnterHome.setVisibility(View.VISIBLE);
				} else {
//					mIvEnterHome.setVisibility(View.GONE);
				}
				imageView.setImageResource(R.drawable.intro_indicator);
            }
			 this.pageControl.addView(imageView);
		}
	}
	
   private class MyPagerAdapter extends PagerAdapter {

        @Override
		public int getCount() {
			if (mList != null)
				return mList.size();
			else
				return 0;
		}

        @Override
        public Object instantiateItem(View collection, int position) {
            ((ViewPager) collection).addView(mList.get(position), 0);
            
            return mList.get(position);
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView(mList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
   
	private void goMainAcy() {
		// 看完之后才真正的退出
		SharedPreferences mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_APP_DATA, 0);
		Editor editor = mSharePre.edit();
		editor.putBoolean("showIntro", true);
		editor.commit();

		startActivity(new Intent(A_2_Intro_Acy.this, A_3_0_Login_Acy.class));
		overridePendingTransition(R.anim.animal_slide_in_bottom_normal, R.anim.animal_push_left_out_normal);
		finish();
	}
   
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
   
    public static void logD(String msg) {
        LogUtils.logD("A_2_Intro_Acy", msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("A_2_Intro_Acy", msg);
    }
	
    private void recycleImage() {
        if (bitmap != null && bitmap.length > 0) {
            for (int i = 0; i < bitmap.length; i++) {
                if (!bitmap[i].isRecycled()) {
                    bitmap[i].recycle();
                    bitmap[i] = null;
                    logD("回收图片");
                }
            }
            bitmap = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        if (mList != null || myAdapter != null) {
            mList = null;
            myAdapter = null;
        }
        recycleImage();
        if (images.length > 0) {
            images = null;
        }
        super.onDestroy();
    }
    protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);}
}

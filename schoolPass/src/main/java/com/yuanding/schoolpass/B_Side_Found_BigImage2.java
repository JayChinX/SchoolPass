package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.photoview.PhotoView;
import io.rong.photoview.PhotoViewAttacher.OnViewTapListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xutils.image.ImageOptions;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.yuanding.schoolpass.service.Api.Inter_DownLoad_Photo;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.HackyViewPager;
/**
 * 
 * @author 融云查看大图
 *
 */
public class B_Side_Found_BigImage2 extends Activity {

    private ArrayList<View> listViews = null;
    private HackyViewPager pager;
    private MyPageAdapter adapter;
    private PhotoView img;
    private Intent intent;
    private ArrayList<String> path = new ArrayList<String>();
    private int page;
    private ImageOptions bitmapUtils;
    private TextView side_found_image_first,side_found_image_two;
    private Uri uri,thumbUri;
    private  String BASE_PATH=android.os.Environment.getExternalStorageDirectory()+ AppStrStatic.SD_PIC+"/";
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setContentView(R.layout.activity_side_found_bigimage);
        intent = getIntent();
        bitmapUtils=A_0_App.getBitmapUtils(B_Side_Found_BigImage2.this, R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,false);
       
        uri = getIntent().getParcelableExtra("photo");
        thumbUri = getIntent().getParcelableExtra("thumbnail");
        
        side_found_image_first=(TextView) findViewById(R.id.side_found_image_first);
        side_found_image_two=(TextView) findViewById(R.id.side_found_image_two);
        pager = (HackyViewPager) findViewById(R.id.found_viewpager);
        pager.addOnPageChangeListener(pageChangeListener);
         initListViews(thumbUri+"", 0);//
       
	    side_found_image_first.setText(1+"/");
        side_found_image_two.setText("1");
        adapter = new MyPageAdapter(listViews);// 
        pager.setAdapter(adapter);//
        pager.setCurrentItem(page);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
       
    }

   
	private void initListViews(final String bm, final int i) {
		if (listViews == null){
			listViews = new ArrayList<View>(path.size());
		}
		if(bm.endsWith(".gif")){
			final GifImageView gifImg = new GifImageView(this);			
			gifImg.setBackgroundColor(0xff000000);
			gifImg.setScaleType(ScaleType.FIT_CENTER);
			gifImg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			
			/**
			 * 异步请求gif字节码
			 */
			new AsyncTask<Void, Void, byte[]>() {		
				
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					//先预加载默认的图片
					PubMehods.loadBitmapUtilsPic(bitmapUtils,gifImg, bm);
					listViews.add(i, gifImg);
					gifImg.setTag(i);
				}

				@Override
				protected byte[] doInBackground(Void... params) {
					byte[] gifbyte = null;
					HttpURLConnection conn = null;
					try {
						URL url = new URL(bm);
						conn = (HttpURLConnection) url.openConnection();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						InputStream in = conn.getInputStream();
						if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
							// 连接不成功
							return null;
						}

						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = in.read(buffer)) > 0) {
							out.write(buffer, 0, len);
						}
						gifbyte = out.toByteArray();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						conn.disconnect();
					}

					return gifbyte;

				}

				protected void onPostExecute(byte[] gifbyte) {

					// 判断是否是gif图
					Movie gif = Movie.decodeByteArray(gifbyte, 0, gifbyte.length);
					if (gif != null) {
						
						GifDrawable gifDrawable = null;
						try {
							gifDrawable = new GifDrawable(gifbyte);
						} catch (IOException e) {
							e.printStackTrace();
						}
						gifImg.setImageDrawable(gifDrawable);
					} else {
						Bitmap gifBitmap = BitmapFactory.decodeByteArray(gifbyte,0, gifbyte.length);
						gifImg.setImageBitmap(gifBitmap);
					}
					
					gifImg.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							finish();
							
						}
					});
					
					gifImg.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View arg0) {


							final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Found_BigImage2.this,R.style.Theme_GeneralDialog);
							upDateDialog.setTitle(R.string.pub_title);
							upDateDialog.setContent("确定保存当前图片?");
							upDateDialog.showLeftButton(R.string.pub_sure,
									new OnClickListener() {
										@Override
										public void onClick(View v) {
										upDateDialog.cancel();											
										String newStr=""+System.currentTimeMillis();
										downloadFile(bm,BASE_PATH+ newStr + ".jpg");
										}
									});
							upDateDialog.showRightButton(R.string.pub_cancel,
									new OnClickListener() {
										@Override
										public void onClick(View v) {
											upDateDialog.cancel();
											

										}
									});
							
							upDateDialog.show();
							return false;
						}
					});
					
					listViews.add((Integer) gifImg.getTag(), gifImg);
					

				};

			}.execute();
			
		}else{
			img = new PhotoView(this);//
			img.setBackgroundColor(0xff000000);
			img.setScaleType(ScaleType.FIT_CENTER);
			PubMehods.loadBitmapUtilsPic(bitmapUtils,img, bm);
			img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			img.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View arg0, float arg1, float arg2) {
					finish();

				}
			});
						
			img.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {


					final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Found_BigImage2.this,R.style.Theme_GeneralDialog);
					upDateDialog.setTitle(R.string.pub_title);
					upDateDialog.setContent("确定保存当前图片?");
					upDateDialog.showLeftButton(R.string.pub_sure,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
								upDateDialog.cancel();											
								String newStr=""+System.currentTimeMillis();
								downloadFile(bm, BASE_PATH+ newStr + ".jpg");
									
								}
							});
					upDateDialog.showRightButton(R.string.pub_cancel,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									upDateDialog.cancel();
									

								}
							});
					
					upDateDialog.show();
					return false;
				}
			});
			
			
			listViews.add(img);
		}
//
	}

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {// 
            side_found_image_first.setText(arg0+1+"/");
            side_found_image_two.setText(""+path.size());
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {//

        }

        public void onPageScrollStateChanged(int arg0) {//

        }
    };

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;// content

        private int size;// 

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {// 
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {//
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {// 
            //((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {// 
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
             
                
            } catch (Exception e) {
            }
           
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_BigImage2.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_BigImage2.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
  	 private void downloadFile(final String url, final String path) {
  		A_0_App.getApi().download_Photo(url, path, new Inter_DownLoad_Photo() {
			
			@Override
			public void onWaiting() {
				
			}
			
			@Override
			public void onSuccess(String message) {
				PubMehods.showToastStr(B_Side_Found_BigImage2.this, path);
			}
			
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				
			}
			
			@Override
			public void onFinished() {
				
			}
			
			@Override
			public void onFailure(String msg) {
				PubMehods.showToastStr(B_Side_Found_BigImage2.this, "保存失败！请检查网络和SD卡");
			}
			
			@Override
			public void onCancelled() {
				
			}
		});
  	 }  
}

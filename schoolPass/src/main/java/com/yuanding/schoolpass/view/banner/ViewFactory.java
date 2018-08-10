package com.yuanding.schoolpass.view.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yuanding.schoolpass.R;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

	/**
	 * 获取ImageView视图的同时加载显示url
	 * 
	 * @param text
	 * @return
	 */
    private static DisplayImageOptions options;
    private static ImageLoader imageloader;
    @SuppressLint("NewApi")
    public static ImageView getImageView(Context context,String url) {
        ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.view_banner, null);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.ic_default_banner_empty_bg)
        .showImageForEmptyUri(R.drawable.ic_default_banner_empty_bg) 
        .showImageOnFail(R.drawable.ic_default_banner_empty_bg) // 设置图片加载或解码过程中发生错误显示的图片
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .bitmapConfig(Bitmap.Config.RGB_565)//默认是ARGB_8888，使用RGB_565会比使用ARGB_8888少消耗2倍的内
//      .displayer(new RoundedBitmapDisplayer(0))//不推荐用！！！！是否设置为圆角，弧度为多少,他会创建新的ARGB_8888格式的Bitmap对象；
        .build(); // 构建完成
        imageloader.displayImage(url,imageView, options);
        return imageView;
    }
    
//  @SuppressLint("NewApi")
//  public static ImageView getImageView(Context context, String url) {
//      LogUtils.e(url);
//      BitmapUtils bitmapUtils=new BitmapUtils(context);
//      ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.view_banner, null);
//      bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_banner_empty);
//      //ImageLoader.getInstance().displayImage(url, imageView);
//      imageView.setAdjustViewBounds(true);
//      imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//      imageView.setImageResource(R.drawable.ic_default_banner_empty);
//      bitmapUtils.display(imageView, url);
//      return imageView;
//  }
}

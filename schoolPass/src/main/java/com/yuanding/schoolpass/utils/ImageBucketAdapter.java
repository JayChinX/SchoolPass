package com.yuanding.schoolpass.utils;

import java.util.List;

import org.xutils.image.ImageOptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.B_Side_Found_Add_BitmapCache;
import com.yuanding.schoolpass.B_Side_Found_Add_BitmapCache.ImageCallback;
import com.yuanding.schoolpass.R;


public class ImageBucketAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private ImageOptions bitmapUtils;
	Activity act;
	/**
	 * 
	 */
	List<ImageBucket> dataList;
	B_Side_Found_Add_BitmapCache cache;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
	    bitmapUtils=A_0_App.getBitmapUtils(act, R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,false);
		this.act = act;
		dataList = list;
		cache = new B_Side_Found_Add_BitmapCache();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView name;
		private TextView count;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder;
		if (arg1 == null) {
			holder = new Holder();
			arg1 = View.inflate(act, R.layout.item_image_bucket, null);
			holder.iv = (ImageView) arg1.findViewById(R.id.image);
			holder.selected = (ImageView) arg1.findViewById(R.id.isselected);
			holder.name = (TextView) arg1.findViewById(R.id.name);
			holder.count = (TextView) arg1.findViewById(R.id.count);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		ImageBucket item = dataList.get(arg0);
		holder.count.setText(item.count+"");
		holder.name.setText(item.bucketName);
		holder.selected.setVisibility(View.GONE);
		if (item.imageList != null && item.imageList.size() > 0) {
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			if (sourcePath!=null) {
				holder.iv.setTag(sourcePath);
				PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv,"file://"+sourcePath);
				//cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
			}
			
		} else {
			holder.iv.setImageBitmap(null);
			Log.e(TAG, "no images in bucket " + item.bucketName);
		}
		return arg1;
	}

}

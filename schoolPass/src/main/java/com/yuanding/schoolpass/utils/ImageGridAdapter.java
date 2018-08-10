package com.yuanding.schoolpass.utils;

import java.util.LinkedHashMap;
import java.util.List;

import org.xutils.image.ImageOptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.B_Side_Found_Add_BitmapCache;
import com.yuanding.schoolpass.B_Side_Found_Add_BitmapCache.ImageCallback;
import com.yuanding.schoolpass.R;


/**
 * 
 * @author MyPC 失物招领图片选择相关
 *
 */
public class ImageGridAdapter extends BaseAdapter {
	private ImageOptions bitmapUtils;
	private TextCallback textcallback = null;
	final String TAG = getClass().getSimpleName();
	Activity act;
	List<ImageItem> dataList;
	public LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	B_Side_Found_Add_BitmapCache cache;
	private Handler mHandler;
	private int selectTotal = 0;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
				}
			} else {
			}
		}
	};

	public static interface TextCallback {
		public void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
	    bitmapUtils=A_0_App.getBitmapUtils(act, R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,false);
		this.act = act;
		dataList = list;
		cache = new B_Side_Found_Add_BitmapCache();
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();   
		}
		final ImageItem item = dataList.get(position);

		if (item.imagePath != null && item.imagePath != "") {
			holder.iv.setTag(item.imagePath);
			
			PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv,"file://"+item.imagePath);
//			cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
//					callback);
		}
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.icon_data_select);  
			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else {
			holder.selected.setImageResource(-1);
			holder.text.setBackgroundColor(0x00000000);
		}
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = dataList.get(position).imagePath;
				System.out.println(dataList.get(position).thumbnailPath+">>>>"+path);
				if (path != null && path != "") {
				if ((Bimp.drr.size() + selectTotal) <3) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.selected
								.setImageResource(R.drawable.icon_data_select);
						holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
						selectTotal++;
						if (textcallback != null)
							textcallback.onListen(selectTotal);
						map.put(path, path);

					} else if (!item.isSelected) {
						holder.selected.setImageResource(-1);
						holder.text.setBackgroundColor(0x00000000);
						selectTotal--;
						if (textcallback != null)
							textcallback.onListen(selectTotal);
						map.remove(path);
					}
				} else if ((Bimp.drr.size() + selectTotal) >=3) {
					if (item.isSelected == true) {
						item.isSelected = !item.isSelected;
						holder.selected.setImageResource(-1);
						selectTotal--;
						map.remove(path);

					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}
			}}

		});

		return convertView;
	}
}

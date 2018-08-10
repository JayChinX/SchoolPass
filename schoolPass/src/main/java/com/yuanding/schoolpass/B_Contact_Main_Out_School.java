package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanding.schoolpass.bean.Cpk_School_In_And_Out_Child;
import com.yuanding.schoolpass.view.MyGridView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月5日 下午4:25:50 类说明
 */
public class B_Contact_Main_Out_School extends Fragment {

	private View viewone;
	private MyGridView gv_outschool;
	private GridAdapter adapter;
	// List<Cpk_School_In_And_Out> schoolOut;
	private EditText mSearchOutSchool;
	private List<Cpk_School_In_And_Out_Child> all_cate_data = new ArrayList<Cpk_School_In_And_Out_Child>();

//	protected ImageLoader imageLoader, imageLoaderOut;
//	private DisplayImageOptions options, optionsOut;

//	private int[] cate_id = { 1, 2, 3, 4, 5, 6};
	private int[] cate_id = { 1, 2, 6, 4, 3, 5};
    private int[] cate_image = { R.drawable.school_list_express,
            R.drawable.school_list_takeout,R.drawable.school_list_pub, R.drawable.school_list_trip,
            R.drawable.school_list_bank,R.drawable.school_list_entertainment};
//	private String[] cate_name = { "快递", "外卖", "银行", "出行", "娱乐", "公共"};
    private String[] cate_name = { "快递", "外卖", "公共服务", "出行", "银行", "娱乐" };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.activity_contact_school_out, container, false);

		// 校外
		mSearchOutSchool = (EditText) viewone.findViewById(R.id.CustomEditText_OutSchool_Search);
		gv_outschool = (MyGridView)viewone.findViewById(R.id.gv_outschool);
		adapter = new GridAdapter();
		gv_outschool.setAdapter(adapter);
		mSearchOutSchool.setInputType(InputType.TYPE_NULL);
//		// 校外Item点击
//		gv_outschool.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
//					long arg3) {
//				Intent intent = new Intent(getActivity(),
//						B_Contact_Out_School_Click_Item_List.class);
//				intent.putExtra("cate_id", cate_id[posi]);
//				intent.putExtra("cate_name", cate_name[posi]);
//				startActivity(intent);
//			}
//		});
		// 点击搜索校外全部
				mSearchOutSchool.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getActivity(),
								B_Contact_Out_School_Search_Item.class);
						startActivity(intent);
					}
				});
		
//		// 点击搜索校外全部
//		mSearchOutSchool.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View arg0, boolean arg1) {
//				LogUtils.e(arg1 + "arg1");
//				if (arg1) {
//					mSearchOutSchool.requestFocus();
//					Intent intent = new Intent(getActivity(),
//							B_Contact_Out_School_Search_Item.class);
//					startActivity(intent);
//				}
//			}
//		});
		
		return viewone;
	}

	public class GridAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return cate_id.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(getActivity())
						.inflate(R.layout.item_contact_outschool, null);
			}
			ImageView por = (ImageView) converView
					.findViewById(R.id.iv_out_school_por);
			TextView name = (TextView) converView
					.findViewById(R.id.tv_out_school_name);
			name.setText(cate_name[posi]);
			por.setBackgroundResource(cate_image[posi]);
			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							B_Contact_Out_School_Click_Item_List.class);
					intent.putExtra("cate_id", cate_id[posi]);
					intent.putExtra("cate_name", cate_name[posi]);
					startActivity(intent);
				}
			});
			return converView;
		}

	}
}

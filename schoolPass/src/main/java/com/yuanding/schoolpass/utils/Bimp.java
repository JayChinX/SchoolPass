package com.yuanding.schoolpass.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * 
 * @author MyPC 选择图片需要的工具
 *
 */
public class Bimp {
	/**
	 * 1编辑2添加
	 */
	public static String add_edit="";
	/**
	 * 判断直接编辑1，不是2
	 */
	public static String edit_biaoshi="";
	public static String found_name="";
	public static String found_place="";
	public static String found_phone="";
	public static String found_date="";
	public static String found_desc="";
	public static String found_lost_id="";
	public static int max = 0;
	public static boolean act_bool = true;
	public static boolean upload_biao = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();	
	public static List<String> drr = new ArrayList<String>();
	public static List<String> upload_path = new ArrayList<String>();
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 480)
					&& (options.outHeight >> i <= 800)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				if (options.inSampleSize>=4&&options.inSampleSize<=8) {
					options.inSampleSize=options.inSampleSize-2;
				}
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}

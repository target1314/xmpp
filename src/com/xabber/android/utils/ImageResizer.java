/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xabber.android.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * A simple subclass of {@link ImageWorker} that resizes images from resources
 * given a target width and height. Useful for when the input images might be
 * too large to simply load directly into memory.
 */
public class ImageResizer {

	/**
	 * 发送图片保存目录
	 * 
	 * @param context
	 * @param bmp
	 * @return
	 */
	public static String saveFile(Context context, Bitmap bmp) {
		String sdStatue = Environment.getExternalStorageState();

		if (!sdStatue.endsWith(Environment.MEDIA_MOUNTED)) {
			return sdStatue;
		}
		FileOutputStream b = null;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "sendImage" + "/");
		file.mkdirs();
		String pictureName = Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "sendImage" + "/"
				+ System.currentTimeMillis() + ".jpg";
		try {
			b = new FileOutputStream(pictureName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 40, b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pictureName;

	}

	/**
	 * 接受图片文件保存目录
	 * 
	 * @param context
	 *            上下文对象
	 * @param imagePath
	 *            缓存图片子文件夹
	 * @return
	 */
	public static String getImagePath(Context context, String imagePath) {
		String sdStatue = Environment.getExternalStorageState();

		if (!sdStatue.endsWith(Environment.MEDIA_MOUNTED)) {
			return sdStatue;
		}
		FileOutputStream b = null;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "receiveImage" + "/");
		file.mkdirs();
		String pictureName = Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "receiveImage" + "/"
				+ imagePath;
		try {
			b = new FileOutputStream(pictureName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pictureName;
	}

	/**
	 * 接受图片文件保存目录
	 * 
	 * @param context
	 *            上下文对象
	 * @param imagePath
	 *            缓存图片子文件夹
	 * @return
	 */
	public static String getVoicePath(Context context, String imagePath) {
		String sdStatue = Environment.getExternalStorageState();

		if (!sdStatue.endsWith(Environment.MEDIA_MOUNTED)) {
			return sdStatue;
		}
		FileOutputStream b = null;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "receiveVoice" + "/");
		file.mkdirs();
		String pictureName = Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "receiveVoice" + "/"
				+ imagePath;
		try {
			b = new FileOutputStream(pictureName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pictureName;
	}

	/**
	 * 上传头像保存目录
	 * 
	 * @param context
	 *            上下文对象
	 * @param imagePath
	 *            缓存图片子文件夹
	 * @return
	 */
	public static String uploadImage(Context context, Bitmap bmp) {
		String sdStatue = Environment.getExternalStorageState();

		if (!sdStatue.endsWith(Environment.MEDIA_MOUNTED)) {
			return sdStatue;
		}
		FileOutputStream b = null;
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "avatar" + "/");
		file.mkdirs();
		String pictureName = Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "avatar" + "/"
				+ System.currentTimeMillis() + ".jpg";
		try {
			b = new FileOutputStream(pictureName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 40, b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pictureName;
	}

	/**
	 * 打开相册读取图片
	 * 
	 * @param intent
	 * @param context
	 * @return
	 */
	public static String getPictureSelectedPath(Intent intent, Activity activity) {
		Uri uri = intent.getData();
		Cursor cursor = activity
				.managedQuery(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToNext();
		String path = cursor.getString(index);
		return path;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further.
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}

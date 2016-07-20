package com.xabber.android.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class FormatTools {
	
	  private static FormatTools tools = new FormatTools();  
	  
	    public static FormatTools getInstance() {  
	        if (tools == null) {  
	            tools = new FormatTools();  
	            return tools;  
	        }  
	        return tools;  
	    }  
	  
	    public InputStream Byte2InputStream(byte[] b) {  
	        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
	        return bais;  
	    }  
	  
	    public static byte[] InputStream2Bytes(InputStream is) {  
	        String str = "";  
	        byte[] readByte = new byte[1024];  
			int readCount = -1;  
	        try {  
	            while ((readCount = is.read(readByte, 0, 1024)) != -1) {  
	                str += new String(readByte).trim();  
	            }  
	            return str.getBytes();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	  
	    public static InputStream Bitmap2InputStream(Bitmap bm) {  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
	        return is;  
	    }  
	  
	    public InputStream Bitmap2InputStream(Bitmap bm, int quality) {  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);  
	        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
	        return is;  
	    }  
	  
	    public Bitmap InputStream2Bitmap(InputStream is) {  
	        return BitmapFactory.decodeStream(is);  
	    }  
	  
	    public static InputStream Drawable2InputStream(Drawable d) {  
	        Bitmap bitmap = drawable2Bitmap(d);  
	        return Bitmap2InputStream(bitmap);  
	    }  
	  
	    public Drawable InputStream2Drawable(InputStream is) {  
	        Bitmap bitmap = this.InputStream2Bitmap(is);  
	        return this.bitmap2Drawable(bitmap);  
	    }  
	  
	    public byte[] Drawable2Bytes(Drawable d) {  
	        Bitmap bitmap = drawable2Bitmap(d);  
	        return this.Bitmap2Bytes(bitmap);  
	    }  
	  
	    public Drawable Bytes2Drawable(byte[] b) {  
	        Bitmap bitmap = this.Bytes2Bitmap(b);  
	        return this.bitmap2Drawable(bitmap);  
	    }  
	  
	    public byte[] Bitmap2Bytes(Bitmap bm) {  
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
	        return baos.toByteArray();  
	    }  
	  
	    public static Bitmap Bytes2Bitmap(byte[] b) {  
	        if (b.length != 0) {  
	            return BitmapFactory.decodeByteArray(b, 0, b.length);  
	        }  
	        return null;  
	    }  
	  
	    public static Bitmap drawable2Bitmap(Drawable drawable) {  
	        Bitmap bitmap = Bitmap  
	                .createBitmap(  
	                        drawable.getIntrinsicWidth(),  
	                        drawable.getIntrinsicHeight(),  
	                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
	                                : Bitmap.Config.RGB_565);  
	        Canvas canvas = new Canvas(bitmap);  
	        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
	                drawable.getIntrinsicHeight());  
	        drawable.draw(canvas);  
	        return bitmap;  
	    }  
	  
	    public Drawable bitmap2Drawable(Bitmap bitmap) {  
	        BitmapDrawable bd = new BitmapDrawable(bitmap);  
	        Drawable d = (Drawable) bd;  
	        return d;  
	    }  
   }

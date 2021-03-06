package com.qian.pos.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * http://blog.csdn.net/leechee_1986/article/details/25049243
 * 个人感觉这一篇博客写的要更好一点
 */
public class BitmapUtil {
	public static Bitmap createImageThumbnail(String filePath,int lenth){  
	     Bitmap bitmap = null;  
	     BitmapFactory.Options opts = new BitmapFactory.Options();  
	     opts.inJustDecodeBounds = true;  
	     BitmapFactory.decodeFile(filePath, opts);  
	     opts.inSampleSize = computeSampleSize(opts, -1, lenth*lenth);  
	    // opts.inSampleSize = computeSampleSize(opts, -1, 128*128);  
	     opts.inJustDecodeBounds = false;  
	  
	     try {  
	         bitmap = BitmapFactory.decodeFile(filePath, opts);  
	     }catch (Exception e) {  
	        // TODO: handle exception  
	    }  
	    return bitmap;  
	}  
	  
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {  
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}  
	  
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {  
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
	    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));  
	    if (upperBound < lowerBound) {  
	        // return the larger one when there is no overlapping zone.  
	        return lowerBound;  
	    }  
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
	        return 1;  
	    } else if (minSideLength == -1) {  
	        return lowerBound;  
	    } else {  
	        return upperBound;  
	    }  
	}  
}

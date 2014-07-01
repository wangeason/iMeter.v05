package com.hexing.imeter.utils;

import it.sauronsoftware.base64.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hexing.imeter.Login;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    public static boolean isFolderExists(String strFolder) {
    	File file = new File(strFolder);
    	if (!file.exists()) {
    		if (file.mkdirs()) {
    			return true;
    		} else {
    			return false;
    		}
    	}
		return true;
    }
    
    public static boolean existSDcard() {
    	if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
            .getExternalStorageState())) {
    		return true;
    	} else
    		return false;
    }
    /** 
     * ����ָ����ͼ��·���ʹ�С����ȡ����ͼ 
     * �˷���������ô��� 
     *     1. ʹ�ý�С���ڴ�ռ䣬��һ�λ�ȡ��bitmapʵ����Ϊnull��ֻ��Ϊ�˶�ȡ��Ⱥ͸߶ȣ� 
     *        �ڶ��ζ�ȡ��bitmap�Ǹ��ݱ���ѹ������ͼ�񣬵����ζ�ȡ��bitmap����Ҫ������ͼ�� 
     *     2. ����ͼ����ԭͼ������û�����죬����ʹ����2.2�汾���¹���ThumbnailUtils��ʹ 
     *        ������������ɵ�ͼ�񲻻ᱻ���졣 
     * @param imagePath ͼ���·�� 
     * @param width ָ�����ͼ��Ŀ�� 
     * @param height ָ�����ͼ��ĸ߶� 
     * @return ���ɵ�����ͼ 
     */  
    public static Bitmap getImageThumbnail(String imagePath, int screenwidth, int screenheight) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // ��Ϊ false  
        // �������ű�  
        int h = options.outHeight;  
        int w = options.outWidth;  
        int beWidth = w / screenwidth;  
        int beHeight = h / screenheight;  
        int be = 1;  
        if (beWidth > beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 0) {  
            be = 1;  
        }  
        options.inSampleSize = be;  
        // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����  
        if (beWidth > beHeight) {  
        	bitmap = ThumbnailUtils.extractThumbnail(bitmap, screenwidth, screenwidth*h/w,  
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);    
        } else {  
        	bitmap = ThumbnailUtils.extractThumbnail(bitmap, screenheight*w/h, screenheight,  
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        }  
        
        return bitmap;  
    }
    public static String getArchivesFolder(String meterNumber) {
		// TODO Auto-generated method stub
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String    date    =    sDateFormat.format(new    java.util.Date());
		if (null == meterNumber){
			String ArchivesFolder = Environment.getExternalStorageDirectory().getPath()+ "/" +
				"Hexing" + "/" + "Archives" + "/" + Login.mOperator ;
			return ArchivesFolder;
		} else {
			String MeterFolder = Environment.getExternalStorageDirectory().getPath()+ "/" +
					"Hexing" + "/" + "Archives" + "/" + Login.mOperator + "/" + date + "/" + meterNumber;
			return MeterFolder;
		}
	}
    public static Meter readFile(String date, String message) {
		// TODO Auto-generated method stub
		String FilePath = Environment.getExternalStorageDirectory().getPath()+ "/" + "Hexing" + "/" + "Archives"
				+ "/" + Login.mOperator + "/" + date + "/" + message + "/" +message + ".txt";
		Meter meter = new Meter("","","","","","");
		
		
		try {
			FileInputStream fileJson = new FileInputStream (FilePath);
			int length = fileJson.available();
			
			String res = new String();
			byte[] Bytes = new byte[length];

			
			fileJson.read(Bytes);
			res = EncodingUtils.getString(Bytes, "UTF-8");
			fileJson.close();
			JSONArray array = new JSONArray(new String(res));
			//Ŀǰ�������ֻ�ö�ȡһ��������ݣ�δ����������ȡ��֧�����Ϣ
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				
				meter.setMeterNumber(item.getString("MeterNumber"));
				meter.setCustomerNumber(item.getString("CustomerNumber"));
				meter.setAddress(item.getString("Address"));
				meter.setEnergyActive(item.getString("EnergyActive"));
				meter.setLatitude(item.getString("Latitude"));
				meter.setLongtitude(item.getString("Longtitude"));
			}
			return meter;
		} catch (Exception e) {
			return meter;
		}
		
	}
    public static Long readFileID(String date, String message) {
		// TODO Auto-generated method stub
		String FilePath = Environment.getExternalStorageDirectory().getPath()+ "/" + "Hexing" + "/" + "Archives"
				+ "/" + Login.mOperator + "/" + date + "/" + message + "/" +message + ".txt";
		long _id = 0;
		
		
		try {
			FileInputStream fileJson = new FileInputStream (FilePath);
			int length = fileJson.available();
			
			String res = new String();
			byte[] Bytes = new byte[length];

			
			fileJson.read(Bytes);
			res = EncodingUtils.getString(Bytes, "UTF-8");
			fileJson.close();
			//JSONArray array = new JSONArray(new String(res));
			//Ŀǰ�������ֻ�ö�ȡһ��������ݣ�δ����������ȡ��֧�����Ϣ
			//for (int i = 0; i < array.length(); i++) {
			JSONObject item =new JSONObject(new String(res));
				
			_id = item.getLong(TaskProvider.C_ID);
			//}
			return _id;
		} catch (Exception e) {
			return _id;
		}
		
	}
    public static void saveFile(Bitmap bm, String fileName) throws IOException {  
		
        File myCaptureFile = new File(fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    } 
    public static Bitmap getImageFromAssetsFile(String fileName, Context context)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = context.getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  
	  }
    public static byte[] Base64BytesFromInstream(InputStream inStream) throws Exception {
		try {
		
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;

			while ((len = inStream.read(data)) != -1) {
				outStream.write(data, 0, len);
			}
			inStream.close();
			Log.d("-----------------Base64-bytes from instream:", Base64.encode(outStream.toByteArray()).length+"");
			
			return Base64.encode(outStream.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    public static boolean isWiFiActive(Context inContext) {
		WifiManager mWifiManager = (WifiManager) inContext
		.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			System.out.println("**** WIFI is on");
		    return true;
		} else {
		    System.out.println("**** WIFI is off");
		    return false;   
		}
	}
    public static byte[] bytesFromInstream(InputStream inStream) throws Exception {
		try {
		
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;

			while ((len = inStream.read(data)) != -1) {
				outStream.write(data, 0, len);
			}
			inStream.close();
			
			Log.d("------------------bytes from instream:", outStream.toByteArray().length+"");
			return outStream.toByteArray();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

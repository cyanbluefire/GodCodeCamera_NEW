package com.uboss.godcodecamera.app.MyUtil;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.uboss.godcodecamera.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LocalDataUtil {

	private static final String TAG = "SaveDataUtil";

	public static void SaveSharedPre(String dataName, String data, String fileName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
	    SharedPreferences.Editor editor = sp.edit();
	    editor.putString(dataName, data);
	    editor.commit();
		Log.i(TAG, "Save "+dataName+"="+data+" in "+fileName+" Successfully");
		
	}
	public static String ReadSharePre(String fileName, String dataName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
			String data = sp.getString(dataName, "");
			Log.i(TAG, "Read "+dataName+"="+data+" from "+fileName+" Successfully");
			return data;

	}
	public static void SaveBoolean(String dataName, boolean data, String fileName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(dataName, data);
		editor.commit();
		Log.i(TAG, "Save "+dataName+"="+data+" in "+fileName+" Successfully");
	}
	public static boolean ReadBoolean(String fileName, String dataName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
		boolean data = sp.getBoolean(dataName, false);
		Log.i(TAG, "Read "+dataName+"="+data+" from "+fileName+" Successfully");
		return data;

	}


	/**
	 * 保存string-set类型数据
	 * @param dataName
	 * @param data
	 * @param fileName
	 */
	public static void SaveSharedPre(String dataName, Set<String> data, String fileName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putStringSet(dataName, data);
		editor.commit();

		Log.i(TAG, "Save "+dataName+"="+data+" Successfully");
//		Set set = new HashSet(Arrays.asList(array));
//		List list = new ArrayList();
//		Set set = new ArraySet(list);
	}
	public static void saveStringSet(HashMap<String,ArrayList> data, String filename, Context mContext){
		Log.i(TAG, "saveStringSet() HashMap<String,ArrayList>");
		//先清除原来的数据
		clearSpFile(filename);
		Iterator it = data.keySet().iterator();
		for(int i=0;i<data.size();i++){
			String str_map =it.next().toString();
//           Array arr = map_allmessage.get(it.next()).toArray();

//            Set set = new HashSet(Arrays.asList(map_allmessage.get(it.next()).toArray()));
			Log.i(TAG,"it.next()=="+str_map);
			Set set = new HashSet(data.get(str_map));
			Log.i(TAG,"set=="+set);

			LocalDataUtil.SaveSharedPre(str_map, set, filename);
			Toast.makeText(mContext, "已保存", Toast.LENGTH_SHORT).show();
		}

	}
	public static void saveStringSet(Set<String> data, String dataName, String filename, Context mContext){
		Log.i(TAG, "saveStringSet() Set<String>");
		//先清除原来的数据
		clearSpFile(filename);
		SharedPreferences sp = mContext.getSharedPreferences(filename, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putStringSet(dataName, data);
		editor.commit();
	}
	public static Set<String> ReadStringSet(String fileName, String dataName){
		SharedPreferences sp = App.getContext().getSharedPreferences(fileName, 0);
		Set<String> data = sp.getStringSet(dataName, null);
		Log.i(TAG, "Read "+dataName+"="+data+" Successfully");
		return data;
	}

	/**
	 * 清除SharedPreference文件内容
	 * @param filename
	 */
	public static void clearSpFile(String filename){

		SharedPreferences sp = App.getContext().getSharedPreferences(filename, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		Log.i(TAG, "file::"+filename+ " delet Successfully");
	}

	public static Uri pathToUri(String path){
			Log.d(TAG, "path1 is " + path);
			if (path != null) {
				path = Uri.decode(path);
				Log.d(TAG, "path2 is " + path);
				ContentResolver cr = App.getContext().getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(")
						.append(MediaStore.Images.ImageColumns.DATA)
						.append("=")
						.append("'" + path + "'")
						.append(")");
				Cursor cur = cr.query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.ImageColumns._ID },
						buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur
						.moveToNext()) {
					index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0) {
					//do nothing
				} else {
					Uri uri_temp = Uri
							.parse("content://media/external/images/media/"
									+ index);
					Log.d(TAG, "uri_temp is " + uri_temp);
					if (uri_temp != null) {
						return uri_temp;
					}
				}
			}
		return null;
	}
}

package com.jd.dice;

import java.lang.reflect.Field;

import com.jd.dice.R.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

/**
 * Convert the string set of Monoply locations 
 * @author jdiamond
 *
 */
public class LocationUtils {

	private static LocationUtils _INSTANCE = null;
	private Context context;
	private static Object lock = new Object();
	
	static int[] ids = new int[40];    // for each space
	
	private LocationUtils(Context context) {
		this.context = context;
		try {
			for (int i=0;i<ids.length;i++) {
				String name = String.format("loc_%d", i);
				Field field = R.string.class.getField(name);
				ids[i] = field.getInt(field);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static LocationUtils getInstance(Context context) {
		if (_INSTANCE == null) {
			synchronized(lock) {
				if (_INSTANCE == null) {
					_INSTANCE = new LocationUtils(context);
				}
			}
		}
		return _INSTANCE;
	}
	
	/**
	 * Convert a location number to a place name
	 * @param location
	 * @return
	 */

	public CharSequence getName(int location) {
		return context.getString(ids[location]);
	}

	public int getBackgroundColor(int mNewPos) {
		return Color.argb(100, 0xED, 0x93, 0x15);
	}

	
}

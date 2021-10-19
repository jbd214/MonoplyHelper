package com.jd.dice.utils;

import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public final class TokenUtils {

	private static final String CLASS_NAME = TokenUtils.class.getName();
	
	private static String[] tokens = { "monopoly_token_barrow.png", "monopoly_token_boot.png","monopoly_token_car.png", 
			"monopoly_token_dog.png","monopoly_token_hat.png","monopoly_token_iron.png", 
			"monopoly_token_ship.png", "monopoly_token_thimble.png"
	};
	
	public final static String[] getTokenPaths() {
		return tokens;
	}
	public final static String getTokenPath(int i) {
		return tokens[i];
	}
	public static int getLength() {
		return tokens.length;
	}
	
	public static Bitmap getBitmap(Activity activity, int tokenId) {
		AssetManager assetManager = activity.getAssets();
		InputStream inputStream = null;
		Bitmap result = null;
		
        try {
        		inputStream = assetManager.open(TokenUtils.getTokenPath(tokenId));
        		result = BitmapFactory.decodeStream(inputStream);
        		inputStream.close();
        }
        catch (Exception e) {
        	Log.v(CLASS_NAME, e.getMessage());
        }
		return result;
	}
}

package com.jd.dice.list;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.jd.dice.R;
import com.jd.dice.TokenList;
import com.jd.dice.utils.TokenUtils;

public class ListTokenAdapter extends BaseAdapter {
	private final TokenList tokenList;
	private AssetManager assetManager = null;
	
	
			
	
	public ListTokenAdapter(TokenList tokenList) {
		super();
		this.tokenList = tokenList;
		assetManager = tokenList.getAssets();
		
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) tokenList
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.token_row, parent, false);

		ImageButton token = (ImageButton) rowView
				.findViewById(R.id.tokenImage);

		InputStream inputStream = null;
        try {
        		inputStream = assetManager.open(TokenUtils.getTokenPath(position));
        		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        		token.setImageBitmap(bitmap);
        		inputStream.close();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        token.setOnClickListener(new TokenListener(position));
        
        rowView.setClickable(true);
		return rowView;
	}


	@Override
	public int getCount() {
		return TokenUtils.getLength();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	private class TokenListener implements OnClickListener {
		private int itemNum;
		
		private TokenListener(int num) {
			this.itemNum = num;
		}
		
		@Override
		public void onClick(View v) {
			tokenList.buttonPressed(itemNum);
			
		}
	}
}

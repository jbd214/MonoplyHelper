
package com.jd.dice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.jd.dice.enums.Token;

public class TokenList extends ListActivity {
	

	public static final String ICON_ID = "iconId";
	public static final String ICON_POSITION = "iconPosition";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.token_list);

		List<Map<String,Integer>> data = getTokenData();
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				 R.layout.token_row, new String[] { "tokenIcon"}, new int[] {R.id.tokenImage});

		
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		
	}


	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = getIntent();
		intent.putExtra(ICON_POSITION, position);
		intent.putExtra(ICON_ID, id);
		setResult(RESULT_OK, intent);
		finish();
	}



	private List<Map<String,Integer>> getTokenData() {
		 List<Map<String,Integer>> list = new ArrayList<Map<String,Integer>>();
		 Map<String,Integer> map;
		 int[] items = Token.getValues();
		 for (int i=0;i<items.length;i++) {
			 map = new HashMap<String,Integer>();
			 map.put("tokenIcon", items[i]);
			 list.add(map);
		 }
		 
		 return list;
		 
	}



	public void buttonPressed(int itemNum) {
		// TODO Auto-generated method stub
		
	}
	 
}

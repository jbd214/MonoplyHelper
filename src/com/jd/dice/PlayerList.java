/*
 * Copyright (C) 2008 Google Inc.
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

package com.jd.dice;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.jd.dice.list.ListPlayersAdapter;

public class PlayerList extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private PlayerDbAdapter mDbHelper;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_list);
		mDbHelper = new PlayerDbAdapter(this);
		mDbHelper.open();
		fillData();
		ListView listView = getListView();
		registerForContextMenu(listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
	}

	private void fillData() {
		// Get all of the rows from the database and create the item list
		Player[] players = mDbHelper.getAllPlayers();
		
		// Now create a simple cursor adapter and set it to display
		ListPlayersAdapter adapter = new ListPlayersAdapter(this, players);
		setListAdapter(adapter);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_list_players, menu);
        return true;
    }
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addPlayer:
			createPlayer();
			return true;
		case R.id.newGame:
			mDbHelper.resetGame();
			Intent i = new Intent(this, Dice.class);
			startActivity(i);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void moveUp(Player player) {
		mDbHelper.movePlayerToFirst((long) player.getId());
		fillData();
	}
	
	public void delete(Player player) {
		mDbHelper.deletePlayer(player.getId());
		fillData();
	}

	private void createPlayer() {
		Intent i = new Intent(this, PlayerEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	public void edit(Player player) {
		Intent i = new Intent(this, PlayerEdit.class);
		i.putExtra(PlayerEdit.KEY_PLAYER, player);
		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PlayerEdit.class);
		i.putExtra(PlayerDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
	    fillData();
	}
}

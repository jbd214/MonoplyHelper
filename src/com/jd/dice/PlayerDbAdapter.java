/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jd.dice;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class PlayerDbAdapter {

	public static final String KEY_NAME = "name";
	public static final String KEY_TOKEN = "tokenId";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TURNORDER = "turnOrder";
	public static final String KEY_CURRENT_PLAYER = "player";

	private static final String TAG = "PlayerDbAdapter";
	private DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb = null;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_PLAYER_TABLE = "create table players (_id integer primary key autoincrement, "
			+ "name text not null, location integer not null, turnOrder integer not null, token integer not null)";

	private static final String DATABASE_CREATE_CURRENT_PLAYER_TABLE = "create table currentPlayer (_id integer primary key autoincrement, player integer not null)";
	private static final String DATABASE_CREATE_CURRENT_PLAYER = "INSERT INTO currentPlayer (player) VALUES (-1)";

	
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "players";
	private static final String DATABASE_TABLE_CURRENT_PLAYER = "currentPlayer";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE_PLAYER_TABLE);
			db.execSQL(DATABASE_CREATE_CURRENT_PLAYER_TABLE);
			db.execSQL(DATABASE_CREATE_CURRENT_PLAYER);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS players");
			db.execSQL("DROP TABLE IF EXISTS currentPlayer");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public PlayerDbAdapter(Context ctx) {
		this.mCtx = ctx;
	    //ctx.deleteDatabase(DATABASE_NAME);
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public PlayerDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		if (mDb == null) {
			mDb = mDbHelper.getWritableDatabase();
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * @param mPlayerTokenId 
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public Player createPlayer(String name, int location, Integer tokenId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_LOCATION, location);
		initialValues.put(KEY_TOKEN, tokenId);

		int playerCount = getNumPlayers();
		initialValues.put(KEY_TURNORDER, playerCount + 1);
		Player player = new Player();
		player.setName(name);
		player.setTokenId(tokenId);
		player.setLocation(location);
		player.setTurnOrder(playerCount + 1);
		long result = mDb.insert(DATABASE_TABLE, null, initialValues);
		if (result == -1) {
			return null;
		}
		player.setId(result);
		return player;
	}

	/**
	 * Delete a player Move everyone whose turn comes after this player up one
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deletePlayer(long rowId) {
		this.mDb.execSQL("UPDATE players SET turnOrder = turnOrder - 1 where turnOrder > (SELECT turnOrder FROM PLAYERS WHERE _ID = "
				+ rowId + ")");
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	private Cursor fetchAllPlayers() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_LOCATION, KEY_TURNORDER }, null, null, null, null,
				KEY_TURNORDER);
	}

	public int getNumPlayers() {
		int count = 0;
		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_LOCATION }, null, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count++;
			}
		}
		cursor.close();
		return count;
	}

	/**
	 * Get the current player
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Player fetchCurrentPlayer() throws SQLException {
		int value = -1;
		Player result = null;
		Cursor cursor = mDb.query(true, DATABASE_TABLE_CURRENT_PLAYER,
				new String[] { KEY_CURRENT_PLAYER }, null, null, null, null,
				null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				value = cursor.getInt(0);
				cursor.close();
			}
		}

		if (value != -1) {
			Cursor cursor1 = mDb.query(true, DATABASE_TABLE, new String[] {
					KEY_ROWID, KEY_NAME, KEY_LOCATION, KEY_TURNORDER }, KEY_ROWID
					+ "=" + value, null, null, null, null, null);
			if (cursor1 != null) {
				if (cursor1.moveToFirst()) {
					result = new Player();
					result.setId(cursor.getInt(0));
					result.setName(cursor.getString(1));
					result.setLocation(cursor.getInt(2));
					result.setTurnOrder(cursor.getInt(3));

				}
				cursor1.close();
			}
			return result;

		}

		if (result == null) {
			result = new Player();
			result.setId(0);
			result.setName("No Player");
			result.setLocation(0);
		}
		return result;
	}

	public Player fetchPlayer(long rowId) throws SQLException {
		Player player = new Player();

		Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_LOCATION, KEY_TURNORDER }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				player.setId(cursor.getInt(0));
				player.setName(cursor.getString(1));
				player.setLocation(cursor.getInt(2));
				player.setTurnOrder(cursor.getInt(3));

			}
			cursor.close();
		}
		return player;
	}


	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId
	 *            id of note to update
	 * @param title
	 *            value to set note title to
	 * @param body
	 *            value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updatePlayer(Player player) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, player.getName());
		args.put(KEY_LOCATION, player.getLocation());
		args.put(KEY_TOKEN, player.getTokenId());

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + player.getId(), null) > 0;
	}

	/**
	 * store the current location
	 * 
	 * @param playerId
	 * @param locationId
	 */
	public boolean updateLocation(Player player, int nextLocation) {
		ContentValues args = new ContentValues();
		args.put(KEY_LOCATION, nextLocation);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + player.getId(), null) > 0;

	}

	/**
	 * Record the id of the current player
	 * @param player
	 * @return
	 */
	public boolean setCurrentPlayer(Player player) {
		boolean rc = false;
		ContentValues args = new ContentValues();
		args.put(KEY_CURRENT_PLAYER, player.getId());
		
		int rowsUpdated = mDb.update(DATABASE_TABLE_CURRENT_PLAYER, args, KEY_ROWID + "= 0", null);
		
		if (rowsUpdated == 1) {
			rc = true;
		}
		else {
			System.out.println("rows updated=" + rowsUpdated);
		}
		return rc;
	}

	/**
	 * Make a player first
	 * @param mRowId
	 */
	public void movePlayerToFirst(Long mRowId) {
		mDb.execSQL("UPDATE players SET turnOrder = turnOrder + 1 where turnOrder < (SELECT turnOrder FROM PLAYERS WHERE _ID = "
				+ mRowId + ")");
		mDb.execSQL("UPDATE players SET turnOrder = 1 where _ID = "
				+ mRowId);
	}

	/**
	 * get all players
	 * 
	 * @return
	 */
	public Player[] getAllPlayers() {
		List<Player> result = new ArrayList<Player>();
		Cursor cursor = fetchAllPlayers();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Player player = new Player();
				player.setId(cursor.getInt(0));
				player.setName(cursor.getString(1));
				player.setLocation(cursor.getInt(2));
				player.setTurnOrder(cursor.getInt(3));
				result.add(player);
			}
		}
		cursor.close();
		return result.toArray(new Player[] {});
	}

	public void resetGame() {
		mDb.execSQL("UPDATE players SET LOCATION = 0");
		mDb.execSQL("UPDATE currentPlayer SET PLAYER = (SELECT _ID FROM PLAYERS WHERE TURNORDER = 1) WHERE _ID = 0");
	}

	/**
	 * Return the player that comes after the current player
	 * @param currentPlayer
	 * @return
	 */
	public Player getNextPlayer(Player currentPlayer) {
		
		int target = 0;
		Player[] list = getAllPlayers();
		for (int i=0;i<list.length;i++) {
			Player p = list[i];
			if (p.equals(currentPlayer)) {
				target = i;
				break;
			}
		}
		target = (target + 1) % list.length;
		
		setCurrentPlayer(list[target]);
		return list[target];
	}
}

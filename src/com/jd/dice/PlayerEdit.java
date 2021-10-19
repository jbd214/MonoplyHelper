package com.jd.dice;

import com.jd.dice.utils.TokenUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class PlayerEdit extends Activity {

	private static final String CLASS_NAME = PlayerEdit.class.getName();
	
	public static final String KEY_PLAYER = "_key";
	private static final int ACTIVITY_CHOOSE_ICON = 0;
	private PlayerDbAdapter mDbHelper;
	private EditText mPlayerName;
	private Player currentPlayer;

	private ImageButton mPlayerToken;

	private Integer mPlayerTokenId = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new PlayerDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.player_edit);

		mPlayerName = (EditText) findViewById(R.id.player_name);
		mPlayerToken = (ImageButton) findViewById(R.id.playerToken);
		if (savedInstanceState != null) {
			currentPlayer = (Player) savedInstanceState.getParcelable(KEY_PLAYER);
		}
		populateFields();

	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(KEY_PLAYER, currentPlayer);

	}

	private void saveState() {
		String playerName = mPlayerName.getText().toString();

		if (currentPlayer == null) {
			currentPlayer = mDbHelper.createPlayer(playerName, 0, mPlayerTokenId);
		} else {
			currentPlayer.setName(playerName);
			mDbHelper.updatePlayer(currentPlayer);
		}
		mDbHelper.close();
	}

	private void populateFields() {
		if (currentPlayer != null) {
			mPlayerName.setText(currentPlayer.getName());
			int id = mPlayerTokenId;
			if (id == -1) {
				id = currentPlayer.getTokenId();
			}
			mPlayerToken.setImageBitmap(TokenUtils.getBitmap(this,id));
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.confirm:
			saveState();
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.playerToken:
			Intent intent = new Intent(this, TokenList.class);
			startActivityForResult(intent, ACTIVITY_CHOOSE_ICON);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (ACTIVITY_CHOOSE_ICON == requestCode) {
			if (intent == null) {
				System.out.println("no intent");
			} else {
				Bundle bundle = intent.getExtras();
				Long pos1 = bundle.getLong(TokenList.ICON_ID);
				Integer location = bundle.getInt(TokenList.ICON_POSITION);
				if (pos1 == null) {
					System.out.println("pos is null");
				}
				else {
					//mPlayerTokenId = pos;
					System.out.println("id=" + pos1);
					System.out.println("pos=" + location);
				}
			}
		}
	}
}

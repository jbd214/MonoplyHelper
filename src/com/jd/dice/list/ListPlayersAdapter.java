package com.jd.dice.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jd.dice.Player;
import com.jd.dice.PlayerList;
import com.jd.dice.R;

public class ListPlayersAdapter extends BaseAdapter {
	private static int maxSize;
	private final PlayerList playerList;
	private Player[] players;

	public ListPlayersAdapter(PlayerList playerList, Player[] players) {
		super();
		this.playerList = playerList;
		this.players = players;
		maxSize = getMaxSize();
	}

	/**
	 * get longest player name
	 * @return
	 */
	private int getMaxSize() {
		int result = -1;
		for (Player p : this.players) {
			if (p.getName().length() > result) {
				result = p.getName().length();
			}
		}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) playerList
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.player_row, parent, false);

		TextView textPlayerTurn = (TextView) rowView
				.findViewById(R.id.FirstText);
		TextView textPlayerName = (TextView) rowView
				.findViewById(R.id.SecondText);

		ImageButton moveUpButton = (ImageButton) rowView
				.findViewById(R.id.ButtonMoveToFirst);

		moveUpButton.setFocusable(false);
		ImageButton deleteButton = (ImageButton) rowView
				.findViewById(R.id.deletePlayerButton);
		deleteButton.setFocusable(false);

		ImageButton editButton = (ImageButton) rowView
				.findViewById(R.id.ButtonEdit);

		String text = Integer.toString(players[position].getTurnOrder());

		textPlayerTurn.setText(text + ") ");
		textPlayerName.setText(pad(players[position].getName()));

		ListPlayersClickListener listeners = new ListPlayersClickListener(
				playerList, players[position]);
		moveUpButton.setOnClickListener(listeners);
		deleteButton.setOnClickListener(listeners);
		editButton.setOnClickListener(listeners);

		rowView.setClickable(true);
		return rowView;
	}

	private CharSequence pad(String name) {
		StringBuffer buffer = new StringBuffer(name);
		for (int i = name.length(); i <= maxSize; i++) {
			buffer.append(" ");
		}
		return buffer;
	}

	@Override
	public int getCount() {
		return players.length;
	}

	@Override
	public Object getItem(int arg0) {
		return players[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return players[arg0].getId();
	}

	private static class ListPlayersClickListener implements
			View.OnClickListener {

		private Player player;
		private PlayerList playerList;

		public ListPlayersClickListener(PlayerList playerList, Player player) {
			this.player = player;
			this.playerList = playerList;
		}

		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.ButtonMoveToFirst:
				playerList.moveUp(player);
				break;
			case R.id.deletePlayerButton:
				playerList.delete(player);
				break;
			case R.id.ButtonEdit:
				playerList.edit(player);
				break;
			}
		}
	}
}

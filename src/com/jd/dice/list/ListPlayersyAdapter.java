package com.jd.dice.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jd.dice.Player;
import com.jd.dice.PlayerList;
import com.jd.dice.R;

public class ListPlayersyAdapter extends BaseAdapter {
	private final PlayerList playerList;
	private Player[] players;

	public ListPlayersyAdapter(PlayerList playerList, Player[] players) {
		super();
		this.playerList = playerList;
		this.players = players;
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
		Button moveUpButton = (Button) rowView
				.findViewById(R.id.moveUpListButton);

		String text = Integer.toString(players[position].getTurnOrder());

		textPlayerTurn.setText(text);
		textPlayerName.setText(players[position].getName());
		moveUpButton.setText("Move Up");
		moveUpButton.setOnClickListener(new MoveUpClickListener(playerList, players[position]));
		return rowView;
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
	
	private static class MoveUpClickListener implements View.OnClickListener {

	    private Player player;
	    private PlayerList playerList;

		public MoveUpClickListener(PlayerList playerList, Player player) {
			this.player = player;
			this.playerList = playerList;
		}

		public void onClick(View view) {
			playerList.moveUp(player);
	    }
	}
}

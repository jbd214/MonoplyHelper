package com.jd.dice;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Player implements Parcelable {
	
	public static String CLASS_NAME = PlayerEdit.class.getName();
	
	private String name;
	private long id;
	private int location;
	private int turnorder;
	private int tokenId;
	
	public Player(Parcel in) {
		name =	in.readString();
		id = in.readLong();
		location = in.readInt();
		turnorder = in.readInt();
		tokenId = in.readInt();
	}
	
	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public Player() {
		id = 0;
		location = 0;
		turnorder = 1;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public void setTurnOrder(int order) {
		this.turnorder = order;
		
	}
	public int getTurnOrder() {
		return this.turnorder;
		
	}
	@Override
	public String toString() {
		return "Player [name=" + name + ", id=" + id + ", location=" + location
				+ ", turnorder=" + turnorder + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public int describeContents() {
		return hashCode();
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.v(CLASS_NAME, "writeToParcel..."+ flags);
		dest.writeString(name);
		dest.writeLong(id);
		dest.writeInt(location);
		dest.writeInt(turnorder);
		dest.writeInt(tokenId);
	
	}
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in); 
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}

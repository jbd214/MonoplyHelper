package com.jd.dice.enums;

import com.jd.dice.R;

public enum Token {
    BARROW(R.drawable.monopoly_token_barrow),
    BOOT(R.drawable.monopoly_token_boot),
    DOG(R.drawable.monopoly_token_dog),
    HAT(R.drawable.monopoly_token_hat),
    IRON(R.drawable.monopoly_token_iron),
    SHIP(R.drawable.monopoly_token_ship),
    THIMBLE(R.drawable.monopoly_token_thimble),
	CAR(R.drawable.monopoly_token_car);
	
	private Token(int id) {
		this.id = id;
	}
	
	private int id;
	
	/**
	 * Get the values
	 * @return
	 */
	public static int[] getValues() {
		int[] result = new int[values().length];
		for (int i=0,size=values().length;i<size;i++) {
			Token token = values()[i];
			result[i] = token.id;
		}
		return result;
	}
}

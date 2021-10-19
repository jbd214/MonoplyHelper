package com.jd.dice;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Dice extends Activity {

private static final String BLANK_DIE = "blank.png";
  
private static final int ACTIVITY_CREATE_PLAYER = 0;
private static final int ACTIVITY_LIST_PLAYERS = 1;

private static final int TOTAL_SPACES =  40;

private static final String IMAGE_LIGHT_BULB = "lightbulb.png";

private static final String IMAGE_TRAIN = "train.png";
private static final String IMAGE_FAUCET = "faucet.png";

private Bitmap dice[] = new Bitmap[6];
private PlayerDbAdapter mDbHelper;
private Player currentPlayer;
private LocationUtils locationUtils;
private int mNewPos;
private int width = 200;
private int len = 100;

private Bitmap blankDie;
private Bitmap lightBulb;
private Bitmap train;
private Bitmap clearLocation;

private Bitmap faucetImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_dice);
        locationUtils = LocationUtils.getInstance(this);        
        loadImages();
        setDie1(1);
        setDie2(1);
        
        mDbHelper = new PlayerDbAdapter(this);
		mDbHelper.open();
		
		if (currentPlayer == null) {
			currentPlayer = mDbHelper.fetchCurrentPlayer();
		}
		
		populateFields();
		

		
    }

    private void loadImages() {
    	
        // Get the AssetManager
        AssetManager manager = getAssets();
 
    	 // Read a Bitmap from Assets
        InputStream inputStream = null;
        try {
        	for (int i=0;i<6;i++) {
        		inputStream = manager.open(String.format("die%d.png", i + 1));
        		dice[i] = BitmapFactory.decodeStream(inputStream);
        		inputStream.close();
        	}
        	// load the blank die
        	inputStream = manager.open(BLANK_DIE);
    		blankDie = BitmapFactory.decodeStream(inputStream);
    		inputStream.close();
    		
    		inputStream = manager.open(IMAGE_LIGHT_BULB);
    		lightBulb = BitmapFactory.decodeStream(inputStream);
    		inputStream.close();
    		
    		inputStream = manager.open(IMAGE_TRAIN);
    		train = BitmapFactory.decodeStream(inputStream);
    		inputStream.close();
    		
    		inputStream = manager.open(IMAGE_FAUCET);
    		faucetImage = BitmapFactory.decodeStream(inputStream);
    		inputStream.close();
    		
    		// create a clear image bitmap based on the background color
    		 this.clearLocation = Bitmap.createBitmap(width, len, Bitmap.Config.RGB_565);
    		 int color = getResources().getColor(R.color.myColor);
    		 for (int i=0;i<width;i++) {
    	       	 for (int j=0;j<len;j++) {
    	       		this.clearLocation.setPixel(i, j, color);
    	       	}
    		 }
    		
    		
        } catch (IOException e) {
          e.printStackTrace();
        }
		
	}

    private void setDie1(int value) {
        // Assign the bitmap to an ImageView in this layout
        ImageView dieView = (ImageView) findViewById(R.id.imageView1);
        dieView.setImageBitmap(dice[value-1]);
        dieView.invalidate();

    }
    
    private void setDie2(int value) {
        // Assign the bitmap to an ImageView in this layout
        ImageView dieView = (ImageView) findViewById(R.id.imageView2);
        dieView.setImageBitmap(dice[value-1]);
        dieView.invalidate();
    }
    
    /**
     * Clear the die between roles
     */
    private void clearDice() {
        // Assign the bitmap to an ImageView in this layout
        ImageView dieView = (ImageView) findViewById(R.id.imageView2);
        dieView.setImageBitmap(blankDie);
        dieView = (ImageView) findViewById(R.id.imageView1);
        dieView.setImageBitmap(blankDie);
    }
    
	public void onClick(View view) {
        switch (view.getId()) {
        case R.id.rollDiceButton:
        	rollDice();
        	break;
        case R.id.nextPlayerButton:
        	advancePlayer();
        	break;
        }
    }
	

	private void advancePlayer() {
		PlayerDbAdapter dbHelper = this.mDbHelper;
		mDbHelper.updateLocation(this.currentPlayer, mNewPos);
		this.currentPlayer = dbHelper.getNextPlayer(this.currentPlayer);
		clearDice();
		clearNewImage();
		populateFields();
	}

	private void rollDice() {
		TextView total = (TextView) findViewById(R.id.totalValue);
    	total.setText("");
    	int val1 = 1;
		int val2 = 1;
    	
		val1 = (int) Math.round(Math.random() * 5) + 1;
		val2 = (int) Math.round(Math.random() * 5) + 1;
	    setDie1(val1);
	    setDie2(val2);
	    total.setText(Integer.toString(val1+val2));
        //TextView newPosField = (TextView) findViewById(R.id.newPosition);
         mNewPos = (currentPlayer.getLocation() + val1 + val2) % TOTAL_SPACES;
         /*
        newPosField.setText(this.locationUtils.getName(mNewPos));
        newPosField.setBackgroundColor(this.locationUtils.getBackgroundColor(mNewPos));
        newPosField.setTextColor(Color.BLACK);
        */
        		//this.locationUtils.getTextColor(mNewPos));
        /*
        Button rollButton = (Button) findViewById(R.id.rollDiceButton);
        rollButton.setClickable(false);
        Button nextButton = (Button) findViewById(R.id.nextPlayerButton);
        nextButton.setClickable(true);
        */
        
        setNewImage(this.locationUtils.getName(mNewPos).toString());
	}

	private void clearNewImage() {
		
		ImageView location = (ImageView) findViewById(R.id.imageView3);
		location.setImageBitmap(clearLocation);
	}

private void setNewImage(String locationName) {
		
		ImageView location = (ImageView) findViewById(R.id.imageView3);
		Bitmap bmp = Bitmap.createBitmap(width, len, Bitmap.Config.RGB_565);
        
		Canvas canvas = new Canvas(bmp);
        Paint background = new Paint();
        background.setColor(Color.WHITE);
        canvas.drawPaint(background);
        
        int topHeight = len / 3;
        
        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setTextSize(14);
        
       canvas.drawBitmap(faucetImage, 4, 4, p1);
       canvas.drawText("Water Works", 25, topHeight + 50, p1);
        
        addBorderToCanvas(canvas);
        
        location.setImageBitmap(bmp);
        
	}

	private void addBorderToCanvas(Canvas canvas) {
		
		Paint borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);
		canvas.drawLine(0, 0, width, 0, borderPaint);        // top
        canvas.drawLine(0, 0, 0, len, borderPaint);          // left side
        canvas.drawLine(width-1, 0, width-1, len-1, borderPaint);  // right side
        canvas.drawLine(0, len-1, width, len-1, borderPaint);    // bottom
	}

	private void setRailRoadImage(String locationName) {
		
		ImageView location = (ImageView) findViewById(R.id.imageView3);
		Bitmap bmp = Bitmap.createBitmap(width, len, Bitmap.Config.RGB_565);
        
		Canvas canvas = new Canvas(bmp);
        Paint background = new Paint();
        background.setColor(Color.WHITE);
        canvas.drawPaint(background);
        
        int topHeight = len / 3;
        
        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setTextSize(14);
        
       canvas.drawBitmap(train, 4, 4, p1);
       canvas.drawText("B. & O. Railroad", 25, topHeight + 50, p1);
        
        canvas.drawLine(0, 0, width, 0, p1);        // top
        canvas.drawLine(0, 0, 0, len, p1);          // left side
        canvas.drawLine(width-1, 0, width-1, len-1, p1);  // right side
        canvas.drawLine(0, len-1, width, len-1, p1);    // bottom
        
        location.setImageBitmap(bmp);
        
	}

   private void setNewImageElectricCompany(String locationName) {
		
		ImageView location = (ImageView) findViewById(R.id.imageView3);
		Bitmap bmp = Bitmap.createBitmap(width, len, Bitmap.Config.RGB_565);
        
		Canvas canvas = new Canvas(bmp);
        Paint background = new Paint();
        background.setColor(Color.WHITE);
        canvas.drawPaint(background);
        
        int topHeight = len / 3;
        
        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setTextSize(14);
        
       canvas.drawBitmap(lightBulb, 4, 4, p1);
       canvas.drawText("Electric Company", 25, topHeight + 45, p1);
        
        canvas.drawLine(0, 0, 0, width, p1);        // top
        canvas.drawLine(0, 0, 0, len, p1);          // left side
        canvas.drawLine(width-1, 0, width-1, len-1, p1);  // right side
        canvas.drawLine(0, len-1, width, len-1, p1);    // bottom
        
        location.setImageBitmap(bmp);
        
	}
	
	private void setNewImageProperty(String locationName) {
		
		ImageView location = (ImageView) findViewById(R.id.imageView3);
		Bitmap bmp = Bitmap.createBitmap(width, len, Bitmap.Config.RGB_565);
        
		Canvas canvas = new Canvas(bmp);
        Paint background = new Paint();
        background.setColor(Color.GRAY);
        canvas.drawPaint(background);
        
        int topHeight = len / 3;
        for (int i=0;i<width;i++) {
       	 for (int j=0;j<topHeight;j++) {
       		 bmp.setPixel(i, j, Color.BLUE);
       	 }
       	 for (int j=topHeight+1;j<len;j++) {
       		 bmp.setPixel(i, j, Color.WHITE);
       	 }
        }
        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setTextSize(14);
        
       canvas.drawText(locationName, 15, topHeight + 20, p1);
        
        canvas.drawLine(0, 0, 0, width, p1);        // top
        canvas.drawLine(0, 0, 0, len, p1);          // left side
        canvas.drawLine(width-1, 0, width-1, len-1, p1);  // right side
        canvas.drawLine(0, len-1, width, len-1, p1);    // bottom
        
        location.setImageBitmap(bmp);
        
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_dice, menu);
        return true;
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PlayerDbAdapter.KEY_CURRENT_PLAYER, currentPlayer);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.setCurrentPlayer(currentPlayer);
	}

	@Override
	protected void onResume() {
		super.onResume();
		currentPlayer = null;
		populateFields();
	}
	
	private void populateFields() {
		if (currentPlayer == null) {
			currentPlayer = mDbHelper.fetchCurrentPlayer();
		}
		if (currentPlayer != null) {
			
			TextView currentPos = (TextView) findViewById(R.id.currentPosition);
			if (currentPos != null) {
				currentPos.setText(locationUtils.getName(currentPlayer.getLocation()));
			}
			TextView playerName = (TextView) findViewById(R.id.currentPlayerField);
			playerName.setText(currentPlayer.getName());
			
			/*
			TextView newPosition = (TextView) findViewById(R.id.newPosition);
			newPosition.setText("");
			*/
		}
		
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.addPlayer:
			i = new Intent(this, PlayerEdit.class);
			startActivityForResult(i, ACTIVITY_CREATE_PLAYER);
			return true;
		case R.id.listPlayers:
			i = new Intent(this, PlayerList.class);
			startActivityForResult(i, ACTIVITY_LIST_PLAYERS);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

}

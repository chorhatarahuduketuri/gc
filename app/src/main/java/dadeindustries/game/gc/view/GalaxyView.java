package dadeindustries.game.gc.view;

import java.util.ArrayList;
import java.util.Collection;

import android.provider.Settings;
import com.example.gc.R;

import dadeindustries.game.gc.logic.Core;
import dadeindustries.game.gc.model.Sector;
import dadeindustries.game.gc.model.Ship;
import dadeindustries.game.gc.model.GlobalGameData;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class GalaxyView extends View implements OnTouchListener, OnKeyListener {

	private Context ctxt; // needed for Toast debugging

	// Display details
	private final static int NUM_SQUARES_IN_ROW = 5;
	private Paint paint = new Paint();
	private int displayWidth = 0;
	private int displayHeight = 0;
	private static int SQUARE_SIZE;

	// co-ordinates of the top left of the viewport
	// in real world co-ordinates
	protected Point viewPort = new Point(2, 2);

	static final int PADDING = 10;

	// Global Bitmaps
	private Bitmap up = null; // UP emblem bitmap
	private Bitmap mo = null; // Morphers emblem bitmap
	private Bitmap p1, p2 = null;

	// Game data structures REFERENCE
	ArrayList<Ship> ships;
	public Sector[][] sectors;

	// Gesture stuff
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	// View.OnTouchListener gestureListener;

	private Core core;

	private int currentX = -1;
	private int currentY = -1;


	public GalaxyView(Context context, Core c) {
		super(context);
		ctxt = context;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		SQUARE_SIZE = displayWidth / NUM_SQUARES_IN_ROW;
		setBackgroundColor(Color.BLACK);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(3);
		loadBitmaps();
		core = c;
		ships = (ArrayList<Ship>) c.getShips();
		sectors = c.sectors;

		// Enable gesture detection
		gestureDetector = new GestureDetector(ctxt, new GestureListener());

		setOnTouchListener(this);

		// Enable keyboard detection
		setOnKeyListener(this);

		setFocusable(true);
		requestFocus();
	}

	private void loadBitmaps() {
		up = BitmapFactory.decodeResource(getResources(), R.drawable.up);
		mo = BitmapFactory.decodeResource(getResources(), R.drawable.morphers);
		p1 = BitmapFactory.decodeResource(getResources(), R.drawable.planet1);
		p2 = BitmapFactory.decodeResource(getResources(), R.drawable.planet2);
	}

	public void setViewPortPosition(int x, int y) {
		if (x >= 0 && y >= 0 && x < GlobalGameData.galaxySizeX & y < GlobalGameData.galaxySizeY) {
			viewPort.x = x;
			viewPort.y = y;
			invalidate();
		}
	}

	private Rect r = new Rect();

	@Override
	public void onDraw(Canvas canvas) {

		// vertical lines
		for (int i = 1; i <= displayWidth; i++) {
			canvas.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE, displayHeight,
					paint);
		}

		// horizontal lines
		for (int k = 1; k < displayHeight; k++) {
			canvas.drawLine(0, k * SQUARE_SIZE, displayWidth, k * SQUARE_SIZE,
					paint);
		}

		// TODO: Draw purple squares for all unexplored areas
		int j = 0;
		int i = 0;

		// Draw System bitmaps
		for (i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (j = 0; j < GlobalGameData.galaxySizeY; j++) {
				if (sectors[i][j].hasSystem()) {
					int planetX = sectors[i][j].getX();
					int planetY = sectors[i][j].getY();

					if ((planetX >= viewPort.x)
							&& (planetX <= viewPort.x + NUM_SQUARES_IN_ROW)
							&& (planetY >= viewPort.y)) {

						int x = (planetX - viewPort.x) * SQUARE_SIZE;
						int y = (planetY - viewPort.y) * SQUARE_SIZE;
						r.left = x + (SQUARE_SIZE / 2);
						r.top = y + (SQUARE_SIZE / 2);
						r.right = x + (SQUARE_SIZE / 2) * 2;
						r.bottom = y + (SQUARE_SIZE / 2) * 2;

						canvas.drawBitmap(p2, null, r, paint);
						paint.setTextSize(16 * getResources().getDisplayMetrics().density);
						canvas.drawText(sectors[i][j].getSystem().getName(), x+PADDING, y
								+ (SQUARE_SIZE / 2), paint);
					}
				}
			}
		}

		// ships
		for (i = 0; i < ships.size(); i++) {

			int shipx = ships.get(i).getX();
			int shipy = ships.get(i).getY();

			if ((shipx >= viewPort.x)
					&& (shipx <= viewPort.x + NUM_SQUARES_IN_ROW)
					&& (shipy >= viewPort.y)) {

				int x = (shipx - viewPort.x) * SQUARE_SIZE;
				int y = (shipy - viewPort.y) * SQUARE_SIZE;
				r.left = x;
				r.top = y;
				r.right = x + SQUARE_SIZE / 2;
				r.bottom = y + SQUARE_SIZE / 2;

				// TODO: Need to handle case where multiple ships in the same
				// system
				switch (ships.get(i).getFaction()) {

					case UNITED_PLANETS:
						canvas.drawBitmap(up, null, r, paint);
						break;

					case MORPHERS:
						canvas.drawBitmap(mo, null, r, paint);
						break;

					default:
						// do nothing

				}
				// might be nice to be able to centre the text
				canvas.drawText(ships.get(i).getShipName(), x+PADDING, y+(PADDING*3)
						+ (SQUARE_SIZE / 2), paint);
			}
		}

		// highlight current selection
		if (currentX >= 0 && currentY >= 0) {
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);

			int a = currentX * SQUARE_SIZE;
			int b = currentY * SQUARE_SIZE;
			paint.setStrokeWidth(6);
			canvas.drawRect(a, b, a + SQUARE_SIZE, b + SQUARE_SIZE, paint);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
		}
	}


	@Override
	public boolean onTouch(View view, MotionEvent motion) {

		gestureDetector.onTouchEvent(motion);

		// when finger lifts off screen
		if (motion.getAction() == 1) {

			int x = (int) (motion.getX() / SQUARE_SIZE);
			int y = (int) (motion.getY() / SQUARE_SIZE);
			currentX = x;
			currentY = y;

			// if ship is selected
			if (isShipSelected(currentX, currentY)) {
				Toast.makeText(ctxt, "Ship selected", Toast.LENGTH_SHORT)
						.show();
				// TODO: display options/commands
			}

			if (isSystemSelected(currentX, currentY)) {
				Toast.makeText(ctxt, "System selected", Toast.LENGTH_SHORT)
							.show();
			}
		}

		invalidate();

		return true;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		return false;
	}

	void moveGridUp() {
		if (viewPort.y > 0) {
			viewPort.y--;
		}
	}

	void moveGridDown() {
		if (viewPort.y < GlobalGameData.galaxySizeY) {
			viewPort.y++;
		}
	}

	void moveGridLeft() {
		if (viewPort.x > 0) {
			viewPort.x--;
		}
	}

	void moveGridRight() {
		if (viewPort.x < GlobalGameData.galaxySizeX) {
			viewPort.x++;
		}
	}

	private void makeToast(String s) {
		Toast.makeText(ctxt, s, Toast.LENGTH_SHORT).show();
	}

	Point translateViewCoodsToGameCoods(int viewx, int viewy) {
		Point p = new Point(viewx + viewPort.x, viewy + viewPort.y);
		return p;
	}

	boolean isShipSelected(int x, int y) {

		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);

		for (Ship s : ships) {
			if (gameCoods.x == s.getX() && gameCoods.y == s.getY()) {
				return true;
			}
		}
		return false;
	}

	boolean isSystemSelected(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);

		if (sectors[gameCoods.x][gameCoods.y].hasSystem()){
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.wtf("Current co-ords", ""+viewPort.x + " " + viewPort.y);
		switch (keyCode) {

			case 19:
				moveGridUp();
				break;

			case 20:
				moveGridDown();
				break;

			case 21:
				moveGridLeft();
				break;

			case 22:
				moveGridRight();
				break;

			default:
				return false;
		}

		invalidate();
		return true;
	}

	/**
	 * A popup menu that gives the player some options about the entity
	 * TODO: This should eventually be generic to handle ships, planets, and other
	 * things.
	 */
	void showMenu() {

		CharSequence colors[] = new CharSequence[]{
				"Move", "Attack", "Raid", "Build", "Colonise", "Suicide"};

		AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
		builder.setTitle("Menu");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				switch (which) {
					case 0:
						Toast.makeText(ctxt, "Move where?", Toast.LENGTH_LONG).show();
					default:
						Log.wtf("Clicked ", "" + which);

				}
			}
		});

		// If a ship is selected then show the menu
		if (isShipSelected(currentX, currentY)) {
			builder.show();
		}
	}

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public void onLongPress(MotionEvent e) {
			int x = (int) (e.getX() / SQUARE_SIZE);
			int y = (int) (e.getY() / SQUARE_SIZE);
			currentX = x;
			currentY = y;
			showMenu();
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			try {
				Log.i("Gesture", "Gesture call");
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {

					// up - down swipe
					if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						Log.i("Gesture", "Left");
						moveGridDown();
					} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						Log.i("Gesture", "Right");
						moveGridUp();
					}

				}

				// right - left swipe
				else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("Gesture", "Left");
					moveGridRight();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("Gesture", "Right");
					moveGridLeft();
				}

			} catch (Exception e) {
				Log.e("ERR", e.getLocalizedMessage());
			}
			return false;
		}
	}
}
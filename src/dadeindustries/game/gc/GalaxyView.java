package dadeindustries.game.gc;

import java.util.ArrayList;
import com.example.gc.R;
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

	// Map details
	protected static final int MAP_HEIGHT = 50;
	protected static final int MAP_WIDTH = 50;

	// Global Bitmaps
	private Bitmap up = null; // UP emblem bitmap
	private Bitmap mo = null; // Morphers emblem bitmap

	// Game data structures
	ArrayList<Ship> ships = new ArrayList<Ship>();
	ArrayList<System> systems = new ArrayList<System>();

	// Gesture stuff
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	// View.OnTouchListener gestureListener;

	public GalaxyView(Context context) {
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
		loadTestShips();

		// Gesture detection
		gestureDetector = new GestureDetector(ctxt, new GestureListener());
		/*
		 * gestureListener = new View.OnTouchListener() { public boolean
		 * onTouch(View v, MotionEvent event) { return
		 * gestureDetector.onTouchEvent(event); } };
		 */

		setOnTouchListener(this);
		// setOnTouchListener(gestureListener);
		setOnKeyListener(this);
		setFocusable(true);
		requestFocus();
	}

	private void loadBitmaps() {
		up = BitmapFactory.decodeResource(getResources(), R.drawable.up);
		mo = BitmapFactory.decodeResource(getResources(), R.drawable.morphers);
	}

	public void setViewPortPosition(int x, int y) {
		if (x >= 0 && y >= 0 && x < MAP_WIDTH & y < MAP_HEIGHT) {
			viewPort.x = x;
			viewPort.y = y;
			invalidate();
		}
	}

	private void loadTestShips() {
		ships.add(new Ship(4, 4, Ship.Faction.UNITED_PLANETS, "HMS Douglas"));
		ships.add(new Ship(5, 6, Ship.Faction.MORPHERS, "Kdfkljsdf"));
	}

	Rect r = new Rect();

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

		// ships
		for (int i = 0; i < ships.size(); i++) {

			int shipx = ships.get(i).x;
			int shipy = ships.get(i).y;

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
				switch (ships.get(i).side) {

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
				canvas.drawText(ships.get(i).name, x, y + (SQUARE_SIZE / 2),
						paint);
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

	private int currentX = -1;
	private int currentY = -1;

	@Override
	public boolean onTouch(View view, MotionEvent motion) {

		gestureDetector.onTouchEvent(motion);

		// when finger lifts off screen
		if (motion.getAction() == 1) {

			int x = (int) (motion.getX() / SQUARE_SIZE);
			int y = (int) (motion.getY() / SQUARE_SIZE);
			currentX = x;
			currentY = y;
		}
		invalidate();

		return true;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		return false;
	}

	void moveGridUp() {
		viewPort.y--;
	}

	void moveGridDown() {
		viewPort.y++;
	}

	void moveGridLeft() {
		viewPort.x--;
	}

	void moveGridRight() {
		viewPort.x++;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// TODO: Update highlighted square

		switch (keyCode) {

		case 19:
			viewPort.y--;
			break;

		case 20:
			viewPort.y++;
			break;

		case 21:
			viewPort.x--;
			break;

		case 22:
			viewPort.x++;
			break;

		default:
			return false;
		}

		invalidate();
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				Log.i("Gesture", "Gesture call");
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
					
					// up - down swipe
					if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						Log.i("Gesture", "Left");
						moveGridUp();
					} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						Log.i("Gesture", "Right");
						moveGridDown();
					}
					
				}
				
				// right - left swipe
				else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("Gesture", "Left");
					moveGridLeft();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.i("Gesture", "Right");
					moveGridRight();
				}
								
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
}
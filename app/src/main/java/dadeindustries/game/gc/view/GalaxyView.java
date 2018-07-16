package dadeindustries.game.gc.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gc.R;

import java.util.ArrayList;
import java.util.List;

import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.SpacecraftOrder;
import dadeindustries.game.gc.model.factionartifacts.ColonyShip;
import dadeindustries.game.gc.model.factionartifacts.CombatShip;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;

import static dadeindustries.game.gc.model.GlobalGameData.isHumanFaction;

public class GalaxyView extends View implements OnTouchListener, OnKeyListener {

	// Display details
	private static int NUM_SQUARES_IN_ROW = 4;
	private static int NUM_SQUARES_IN_COLUMN = 0;
	private static int SQUARE_SIZE;

	public Sector[][] sectors;

	// co-ordinates of the top left of the viewport
	// in real world co-ordinates
	protected Point viewPort = new Point(2, 2);
	private Context ctxt; // needed for Toast debugging
	private Paint paint = new Paint();

	// Global Bitmaps
	private Bitmap up, mo, p1, p2; // Bitmap variables
	private GestureDetector gestureDetector;

	private MediaPlayer sound_yessir, sound_reporting, sound_setting_course;

	private GlobalGameData globalGameData;

	private int currentX, currentY;
	private Rect r = new Rect();

	private int SELECT_MODE = 0;
	private Spaceship selectedShip;
	private Sector selectedSector;


	public GalaxyView(Context context) {
		super(context);
		init(context);
	}

	/* This constructor is needed for "inflating" the UI from the XML layout */
	public GalaxyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		GlobalGameData globalGameData = new GlobalGameData(10, 10);
		ctxt = context;

		/* Load sound effects */
		sound_yessir = MediaPlayer.create(context, R.raw.yessir);
		sound_reporting = MediaPlayer.create(context, R.raw.reporting);
		sound_setting_course = MediaPlayer.create(context, R.raw.setting_course);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int displayWidth = metrics.widthPixels;
		int displayHeight = metrics.heightPixels;

		NUM_SQUARES_IN_ROW = NUM_SQUARES_IN_ROW + ((displayWidth / 500) * 2);

		SQUARE_SIZE = displayWidth / NUM_SQUARES_IN_ROW;
		NUM_SQUARES_IN_COLUMN = displayHeight / SQUARE_SIZE;
		setBackgroundColor(Color.BLACK);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(3);
		loadBitmaps();
		this.globalGameData = globalGameData;
		sectors = globalGameData.getSectors();

		// Enable gesture detection
		gestureDetector = new GestureDetector(ctxt, new GestureListener());

		setOnTouchListener(this);

		// Enable keyboard detection
		setOnKeyListener(this);

		setFocusable(true);
		requestFocus();

		setViewPortPosition(0, 0);
	}

	public GlobalGameData getGlobalGameData() {
		return globalGameData;
	}

	private void loadBitmaps() {
		up = BitmapFactory.decodeResource(getResources(), R.drawable.up);
		mo = BitmapFactory.decodeResource(getResources(), R.drawable.morphers);
		p1 = BitmapFactory.decodeResource(getResources(), R.drawable.system1);
		p2 = BitmapFactory.decodeResource(getResources(), R.drawable.system2);
	}

	/* TODO: Add stricter parameter checking. */
	public void setViewPortPosition(int x, int y) {
		if (x >= 0 && y >= 0 && x < GlobalGameData.galaxySizeX & y < GlobalGameData.galaxySizeY) {
			viewPort.x = x;
			viewPort.y = y;
			invalidate();
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		final int PADDING = 10;

		// vertical lines
		for (int i = 1; i <= getResources().getDisplayMetrics().widthPixels; i++) {
			canvas.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE,
					getResources().getDisplayMetrics().heightPixels,
					paint);
		}

		// horizontal lines
		for (int k = 1; k < getResources().getDisplayMetrics().heightPixels; k++) {
			canvas.drawLine(0, k * SQUARE_SIZE,
					getResources().getDisplayMetrics().heightPixels,
					k * SQUARE_SIZE,
					paint);
		}

		// TODO: Draw purple squares for all unexplored areas

		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				// Draw System bitmaps
				if (sectors[i][j].hasSystem()) {
					int systemX = sectors[i][j].getX();
					int systemY = sectors[i][j].getY();

					if ((systemX >= viewPort.x)
							&& (systemX <= viewPort.x + NUM_SQUARES_IN_ROW)
							&& (systemY >= viewPort.y)) {

						int x = (systemX - viewPort.x) * SQUARE_SIZE;
						int y = (systemY - viewPort.y) * SQUARE_SIZE;
						r.left = x + (SQUARE_SIZE / 2);
						r.top = y + (SQUARE_SIZE / 2);
						r.right = x + (SQUARE_SIZE / 2) * 2;
						r.bottom = y + (SQUARE_SIZE / 2) * 2;

						canvas.drawBitmap(p2, null, r, paint);
						paint.setTextSize(16 * getResources().getDisplayMetrics().density);
						canvas.drawText(sectors[i][j].getSystem().getName(), x + PADDING, y
								+ (SQUARE_SIZE / 2), paint);
					}
				}

				// Units

				if (sectors[i][j].hasShips() == false) {
					continue;
				}

				int shipx = i;
				int shipy = j;


				if ((shipx >= viewPort.x)
						&& (shipx <= viewPort.x + NUM_SQUARES_IN_ROW)
						&& (shipy >= viewPort.y)) {

					int x = (shipx - viewPort.x) * SQUARE_SIZE;
					int y = (shipy - viewPort.y) * SQUARE_SIZE;
					r.left = x;
					r.top = y;
					r.right = x + SQUARE_SIZE / 2;
					r.bottom = y + SQUARE_SIZE / 2;

					// TODO: Need to handle case where multiple units in the same
					// system

					ArrayList<Spaceship> ships = sectors[i][j].getUnits();
					for (Spaceship ship : ships) {

						switch (ship.getFaction()) {

							case UNITED_PLANETS:
								canvas.drawBitmap(up, null, r, paint);
								break;

							case MORPHERS:
								canvas.drawBitmap(mo, null, r, paint);
								break;

							default:
								// do nothing
						}

						canvas.drawText(ship.getShipName(), x + PADDING, y + (PADDING * 3)
								+ (SQUARE_SIZE / 2), paint);
					}

				}
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

		// Put text in top left corner indicating the current turn number
		canvas.drawText("Turn " + globalGameData.getTurn(),
				viewPort.x + PADDING, viewPort.y + PADDING * 3, paint);
		canvas.drawText("Credits " + globalGameData.getHumanPlayerCredits(),
				viewPort.x + PADDING, viewPort.y + PADDING * 3 * 2, paint);

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

			if (SELECT_MODE == 1) {
				Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
				UnitActions.setCourse(selectedShip, gameCoods.x, gameCoods.y);
				SELECT_MODE = 0;
				makeToast("Set a course for " + currentX + "," + currentY + "!");
				sound_setting_course.start();
			}

			// if ship is selected
			if (isShipSelected(currentX, currentY)) {
				Log.wtf("Ship selected", currentX + " " + currentY);
			}

			if (isSystemSelected(currentX, currentY)) {
				Log.wtf("System selected", currentX + " " + currentY);
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
		Log.e("Number of squares ", "" + NUM_SQUARES_IN_COLUMN);
		if (viewPort.y < (GlobalGameData.galaxySizeY - (NUM_SQUARES_IN_COLUMN + 1))) {
			viewPort.y++;
		}
	}

	void moveGridLeft() {
		if (viewPort.x > 0) {
			viewPort.x--;
		}
	}

	void moveGridRight() {
		if (viewPort.x < (GlobalGameData.galaxySizeX - NUM_SQUARES_IN_ROW)) {
			viewPort.x++;
		}
	}

	public void makeToast(String s) {
		Toast.makeText(ctxt, s, Toast.LENGTH_SHORT).show();
	}

	private Point translateViewCoodsToGameCoods(int viewx, int viewy) {
		Point p = new Point(viewx + viewPort.x, viewy + viewPort.y);
		if (p.x >= GlobalGameData.galaxySizeX) {
			p.x = GlobalGameData.galaxySizeX - 1;
		}
		if (p.y >= GlobalGameData.galaxySizeY) {
			p.y = GlobalGameData.galaxySizeY - 1;
		}
		return p;
	}

	boolean isShipSelected(int x, int y) {

		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);

		if (sectors[gameCoods.x][gameCoods.y].hasShips()) {
			for (Spaceship u : sectors[gameCoods.x][gameCoods.y].getUnits()) {
				if (isHumanFaction((u.getFaction()))) {
					return true;
				}
			}
		}
		return false;
	}

	Sector getSelectedSector(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
		return sectors[gameCoods.x][gameCoods.y];
	}

	Spaceship getSelectedShip(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);

		return sectors[gameCoods.x][gameCoods.y].getUnits().get(0);
	}

	ArrayList<Spaceship> getSelectedShips(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
		return sectors[gameCoods.x][gameCoods.y].getUnits();
	}

	ArrayList<Spaceship> getSelectedFactionShips(int x, int y, Faction faction) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
		ArrayList list = new ArrayList();
		for (Spaceship ship : sectors[gameCoods.x][gameCoods.y].getUnits()) {
			if (ship.getFaction() == faction) {
				list.add(faction);
			}
		}

		return list;
	}

	System getSelectedSystem(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
		return sectors[gameCoods.x][gameCoods.y].getSystem();
	}

	boolean isSystemSelected(int x, int y) {
		Point gameCoods = this.translateViewCoodsToGameCoods(x, y);
		Log.e("Co-od translation", "view coods (" + x + "," + y + ")" + "-> (" +
				gameCoods.x + "," + gameCoods.y + ")");

		if (gameCoods.x > GlobalGameData.galaxySizeX || gameCoods.y > GlobalGameData.galaxySizeY) {
			return false;
		}

		if (sectors[gameCoods.x][gameCoods.y].hasSystem()) {
			return true;
		} else {
			return false;
		}
	}


	boolean isSystemSelectedMine(Faction faction, int x, int y) {
		if (isSystemSelected(x, y) == false) {
			return false;
		}

		if (globalGameData.getSectors()[x][y].getSystem().getFaction() == faction) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.wtf("Current co-ords", "" + viewPort.x + " " + viewPort.y);
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


	private void setSelectedShipForOnClick() {
		selectedShip = (isHumanFaction(
				getSelectedShip(currentX, currentY).getFaction()) ?
				getSelectedShip(currentX, currentY) :
				null);
	}

	public void showShipMenu(final Spaceship ship) {
		CharSequence colors[] = new CharSequence[]{
				SpacecraftOrder.MOVE.name(),
				SpacecraftOrder.ATTACK.name(),
				SpacecraftOrder.COLONISE.name()};

		AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
		builder.setTitle(ship.getShipName());
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				switch (which) {
					case 0:
						selectedShip = ship;
						// new

						SELECT_MODE = (selectedShip != null) ? 1 : 0;
						break;
					case 1:
						setSelectedShipForOnClick();
						UnitActions.attackSystem(selectedShip, globalGameData);
						break;
					case 2:
						setSelectedShipForOnClick();
						UnitActions.coloniseSystem(selectedShip, globalGameData);
						break;
					default:
						Log.wtf("Clicked ", "" + which);
				}
			}
		});

		AlertDialog shipDialog = builder.create();
		int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
		shipDialog.getWindow().setLayout(100, WRAP_CONTENT);
		shipDialog.show();
		sound_yessir.start();
	}

	public void showMultipleShipMenu(final Sector sector) {

		Faction humanFaction = globalGameData.getHumanFaction();

		CharSequence items[] = new CharSequence[sector.getUnits(humanFaction).size()];

		for (int i = 0; i < sector.getUnits(humanFaction).size(); i++) {
			if (globalGameData.isHumanFaction(sector.getUnits().get(i).getFaction())) {
				items[i] = sector.getUnits().get(i).getShipName();
			}
		}

		AlertDialog.Builder menu = new AlertDialog.Builder(ctxt);
		menu.setTitle("Select a ship");
		menu.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int option) {
				dialog.dismiss();
				showShipMenu(sector.getUnits().get(option));
			}
		}).show();
	}

	public void showSystemMenu(final System system) {
		CharSequence items[] = new CharSequence[]{
				"Build CombatShip", "Build ColonyShip"};
		AlertDialog.Builder sysMenu = new AlertDialog.Builder(ctxt);
		String title = system.getName();

		if (system.hasFaction()) {
			title = title + " (" + system.getFaction().toString() + ")";
		} else {
			title = title + " (no faction)";
		}

		sysMenu.setTitle(title);

		if (isHumanFaction(system.getFaction())) {

			sysMenu.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int option) {
					dialog.dismiss();
					switch (option) {
						case 0:
							// Build combatship
							CombatShip combat = new CombatShip(globalGameData.getSectors()
									[system.getX()][system.getY()],
									system.getFaction(), "New CombatShip", 2, 4);
							system.addToQueue(combat);
							makeToast("Building combat ship");

						case 1:
							// Build Colonyship
							ColonyShip colony = new ColonyShip(globalGameData.getSectors()
									[system.getX()][system.getY()],
									system.getFaction(), "New ColonyShip", 0, 4);
							system.addToQueue(colony);
							makeToast("Building colony ship");
					}
				}
			});

		} else {
			sysMenu.setMessage("No information to display");
		}
		sysMenu.show();
	}

	/**
	 * A popup menu that gives the player some options about the entity
	 * TODO: This should eventually be generic to handle units, planets, and other
	 * things.
	 */
	public void showMenu() {

		if (isShipSelected(currentX, currentY) &&
				isSystemSelected(currentX, currentY)) {

			CharSequence menuOptions[] = new CharSequence[]{
					"SYSTEM",
					"SHIPS"};

			AlertDialog.Builder topMenu = new AlertDialog.Builder(ctxt);
			topMenu.setTitle("Menu");
			topMenu.setItems(menuOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int option) {

					switch (option) {
						// SYSTEM
						case 0:
							Log.wtf("Clicked", "Selected system menu!");
							showSystemMenu(getSelectedSystem(currentX, currentY));
							break;

						// SHIPS
						case 1:
							if (getSelectedFactionShips(currentX, currentY,
									globalGameData.getHumanFaction()).size() > 1) {
								showMultipleShipMenu(getSelectedSector(currentX, currentY));
							} else {
								showShipMenu(getSelectedShip(currentX, currentY));
							}
							break;
						default:
							Log.wtf("Clicked ", "" + option + " on global menu");
					}
				}
			}).show();
		} else if (isShipSelected(currentX, currentY)) {
			Log.wtf("GUI", "ship selected");
			if (getSelectedFactionShips(currentX, currentY, globalGameData.getHumanFaction()).size() > 1) {
				showMultipleShipMenu(getSelectedSector(currentX, currentY));
			} else {
				showShipMenu(getSelectedShip(currentX, currentY));
			}
		} else if (isSystemSelected(currentX, currentY)) {
			showSystemMenu(getSelectedSystem(currentX, currentY));
			Log.wtf("GUI", "system selected");
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

			final int SWIPE_MIN_DISTANCE = 120;
			final int SWIPE_MAX_OFF_PATH = 250;
			final int SWIPE_THRESHOLD_VELOCITY = 200;

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
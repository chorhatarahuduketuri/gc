package ready;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class JGalaxyView extends JPanel implements KeyListener {
	
	private GlobalGameData globalGameData;
	public Sector[][] sectors;

	private int currentX, currentY;
	private Rect r = new Rect();
	
	private BufferedImage up, mo, p1, p2, wh; // Bitmap variables

	private int SELECT_MODE = 0;
	private boolean CURRENTLY_ORDERING = false;

	private Spaceship selectedShip;

	private final int PADDING = 10;
	private final Color THE_UNDISCOVERED_COUNTRY = new Color(51, 0, 51);
	
	final static int WIDTH_IN_PIXELS = 300;
	final static int HEIGHT_IN_PIXELS = 300;
	
	// Display details
	private static int NUM_SQUARES_IN_ROW = 4;
	private static int NUM_SQUARES_IN_COLUMN = 0;
	private static int SQUARE_SIZE;

	
	private void loadBitmaps() throws IOException {
		//System.out.println(this.getClass().;
		up = ImageIO.read(JGalaxyView.class.getResourceAsStream("/resources/up.png"));
		mo = ImageIO.read(JGalaxyView.class.getResourceAsStream("/resources/morphers.png"));
		p1 = ImageIO.read(JGalaxyView.class.getResourceAsStream("/resources/system1.png"));
		p2 = ImageIO.read(JGalaxyView.class.getResourceAsStream("/resources/system2.png"));
	}
	
	protected Point viewPort = new Point(2, 2);

	public JGalaxyView() {
		this.setBackground(Color.BLACK);
		this.setForeground(Color.WHITE);
		globalGameData = new GlobalGameData(10, 10);
		sectors = globalGameData.getSectors();

		try {
			
			//Path resourceDirectory = Paths.get("resources");
			//System.out.println(resourceDirectory);


			
			loadBitmaps();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void drawGrid(Graphics paint) {
		// Save colour before using it
		Color savedColor = paint.getColor();
		
		paint.setColor(Color.WHITE);
		this.setBackground(Color.BLACK);

		// Draw vertical lines
		for (int i = viewPort.x; i <= viewPort.x + WIDTH_IN_PIXELS;
			 i = i + SQUARE_SIZE) {
			paint.drawLine(i + SQUARE_SIZE, 0, i + SQUARE_SIZE,
					HEIGHT_IN_PIXELS);
		}

		// Draw horizontal lines
		for (int k = viewPort.y; k < viewPort.y + HEIGHT_IN_PIXELS;
			 k = k + SQUARE_SIZE) {
			paint.drawLine(0, k + SQUARE_SIZE,
					HEIGHT_IN_PIXELS,
					k + SQUARE_SIZE);
		}
		
		paint.setColor(savedColor);
	}
	
	public void onDraw(Graphics paint) {
		Graphics canvas = paint;
		/* Paint all sectors */
		for (int i = viewPort.x; i < globalGameData.galaxySizeX; i++) {
			for (int j = viewPort.y; j < globalGameData.galaxySizeY; j++) {

				try {

					Sector sector = sectors[i][j];

					// Draw a purple square if unexplored
					if (globalGameData.getHumanPlayer().isVisible(sector)) {
						paint.setColor(Color.BLACK);
					} else {
						paint.setColor(THE_UNDISCOVERED_COUNTRY);
					}

					int x = (sector.getX() - viewPort.x) * SQUARE_SIZE;
					int y = (sector.getY() - viewPort.y) * SQUARE_SIZE;
					r.left = x;
					r.top = y;
					r.right = x + (SQUARE_SIZE);
					r.bottom = y + (SQUARE_SIZE);
					canvas.drawRect(r.left, r.top, r.right, r.bottom);

					// Draw wormhole
					//drawWormhole(sector, canvas);
					// Draw System bitmaps
					//drawSystem(sector, canvas);
					// Draw Ship bitmaps
					drawShip(canvas, sector);

				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}
		}

		drawGrid(canvas);

		/* Labels are drawn on top of sectors once all the sectors have been painted  */
		for (Object s : globalGameData.getHumanPlayer().getDiscoveredSectors()) {
			drawSystem((Sector) s, canvas);
			drawSystemLabel(canvas, (Sector) s);
			drawShipLabel(canvas, (Sector) s);
		}

		drawTopLeftInformation(canvas);
	}
	
	private void drawShip(Graphics canvas, Sector sector) {
		int x, y;
		int shipx = sector.getX();
		int shipy = sector.getY();

		if ((shipx >= viewPort.x)
				&& (shipx <= viewPort.x + NUM_SQUARES_IN_ROW)
				&& (shipy >= viewPort.y)
				&& globalGameData.getHumanPlayer().isVisible(sector) == true
				) {

			x = (shipx - viewPort.x) * SQUARE_SIZE;
			y = (shipy - viewPort.y) * SQUARE_SIZE;
			r.left = x;
			r.top = y;
			r.right = x + SQUARE_SIZE / 2;
			r.bottom = y + SQUARE_SIZE / 2;

			// TODO: Need to handle case where multiple units in the same system

			ArrayList<Spaceship> ships = sector.getUnits();
			for (Spaceship ship : ships) {

				switch (ship.getOwner().getIntelligence()) {

					case HUMAN:
						getGlobalGameData().getHumanPlayer().discover(sector);
						canvas.drawImage(up, r.left, r.top, r.right, r.bottom, null);
						System.out.println("Drawing ship");
						break;

					case ARTIFICIAL:
						//canvas.drawBitmap(mo, null, r, canvas);
						break;

					default:
						// do nothing
				}
			}
		}		
	}
	
	public GlobalGameData getGlobalGameData() {
		return globalGameData;
	}

	private void drawWormhole(Sector sector, Graphics canvas) {
		// TODO Auto-generated method stub
		
	}

	private void drawTopLeftInformation(Graphics canvas) {
		// TODO Auto-generated method stub
		
	}

	private void drawShipLabel(Graphics canvas, Sector s) {
		// TODO Auto-generated method stub
		
	}

	private void drawSystemLabel(Graphics canvas, Sector s) {
		// TODO Auto-generated method stub
		
	}

	private void drawSystem(Sector s, Graphics canvas) {
		// TODO Auto-generated method stub
		
	}

	public void paint(Graphics paint) {
		onDraw(paint);

	}

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("FrameDemo");
		Container pane = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH_IN_PIXELS, HEIGHT_IN_PIXELS);
		frame.setResizable(true);
		frame.setTitle("GalaxyConquest (Desktop Edition)");
		//frame.getContentPane().add(new JGalaxyView());
		pane.add(new JGalaxyView(), BorderLayout.CENTER);
		pane.add(new JButton("END TURN"), BorderLayout.PAGE_END);
		frame.setBackground(Color.BLACK);
		frame.setVisible(true);
		
		/* Based on the screen size... determine how many sector squares can be seen at a time */
		NUM_SQUARES_IN_ROW = NUM_SQUARES_IN_ROW + ((WIDTH_IN_PIXELS / 500) * 2);
		SQUARE_SIZE = WIDTH_IN_PIXELS / NUM_SQUARES_IN_ROW;
		NUM_SQUARES_IN_COLUMN = HEIGHT_IN_PIXELS / SQUARE_SIZE;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

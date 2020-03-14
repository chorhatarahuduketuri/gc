package ready;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dadeindustries.game.gc.model.GlobalGameData;

public class JGalaxyView extends JPanel {
	
	final static int WIDTH_IN_PIXELS = 300;
	final static int HEIGHT_IN_PIXELS = 400;
	
	// Display details
	private static int NUM_SQUARES_IN_ROW = 4;
	private static int NUM_SQUARES_IN_COLUMN = 0;
	private static int SQUARE_SIZE;
	
	protected Point viewPort = new Point(2, 2);

	public JGalaxyView() {
		this.setBackground(Color.BLACK);
		this.setForeground(Color.BLACK);
	}
	
	public void paint(Graphics paint) {
		
		// Save colour before using it
		Color savedColor = paint.getColor();
		paint.setColor(Color.BLACK);

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

	public static void main(String[] args) {

		GlobalGameData globalGameData = new GlobalGameData(10, 10);
		
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH_IN_PIXELS, HEIGHT_IN_PIXELS);
		frame.setResizable(false);
		frame.setTitle("GalaxyConquest (Desktop Edition)");
		frame.getContentPane().add(new JGalaxyView());
		frame.setVisible(true);
		
		/* Based on the screen size... determine how many sector squares can be seen at a time */
		NUM_SQUARES_IN_ROW = NUM_SQUARES_IN_ROW + ((WIDTH_IN_PIXELS / 500) * 2);
		SQUARE_SIZE = WIDTH_IN_PIXELS / NUM_SQUARES_IN_ROW;
		NUM_SQUARES_IN_COLUMN = HEIGHT_IN_PIXELS / SQUARE_SIZE;
	}
}

package dadeindustries.game.gc.model;

import java.util.ArrayList;


public class System {

	private String name;
	protected ArrayList<Planet> planets = null;

	private int x, y;

	public System(String name, int x, int y) {
		this.x = x;
		this.y = y;
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}
}

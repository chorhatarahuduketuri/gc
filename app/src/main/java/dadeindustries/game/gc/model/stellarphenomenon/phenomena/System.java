package dadeindustries.game.gc.model.stellarphenomenon.phenomena;

import java.util.ArrayList;


public class System {

	protected ArrayList<Planet> planets = null;
	private String name;
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

package dadeindustries.game.gc.model;

import java.util.ArrayList;


public class SolarSystem {

	private String name;
	protected ArrayList<Planet> planets = null;

	public SolarSystem(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

package dadeindustries.game.gc.model.stellarphenomenon;

import java.util.ArrayList;

import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;

public class Sector {

	private System system = null;

	/**
	 * TODO: flesh out this class.
	 * create method to take in a System, and any other data necessary to define
	 * factors of a system.
	 */

	private int x;
	private int y;

	private ArrayList<Spaceship> units = new ArrayList<Spaceship>();

	public Sector(int x, int y, System system) {
		this.x = x;
		this.y = y;
		this.system = system;
	}

	public boolean hasSystem() {
		if (system == null) {
			return false;
		} else {
			return true;
		}
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void addShip(Spaceship s) {
		units.add(s);
	}

	public ArrayList<Spaceship> getUnits() {
		return units;
	}

	public boolean hasShips() {
		if (units.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
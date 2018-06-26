package dadeindustries.game.gc.model.StellarPhenomenon;

import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.StellarPhenomenon.Phenomena.System;
import java.util.ArrayList;

public class Sector {

	private System system = null;

	/**
	 * TODO: flesh out this class.
	 * create method to take in a System, and any other data necessary to define
	 * factors of a system.
	 */

	private int x;
	private int y;

	public ArrayList<Unit> ships = new ArrayList<Unit>();

	public Sector(int x, int y) {
		this.x = x;
		this.y = y;
	}

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

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void addShip(Unit s) {
		ships.add(s);
	}

	public ArrayList<Unit> getShips() {
		return ships;
	}

	public boolean hasShips() {
		if (ships.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

}

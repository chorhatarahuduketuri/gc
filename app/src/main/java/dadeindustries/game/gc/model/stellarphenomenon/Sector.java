package dadeindustries.game.gc.model.stellarphenomenon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;

public class Sector {


	/**
	 * TODO: flesh out this class.
	 * create method to take in a System, and any other data necessary to define
	 * factors of a system.
	 */

	private int x;
	private int y;
	private System system = null;
	private HashMap<Player, Boolean> discovered = new HashMap<Player, Boolean>();
	private HashMap<Player, Boolean> visible = new HashMap<Player, Boolean>();

	private ArrayList<Spaceship> units = new ArrayList<Spaceship>();

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

	public void setSystem(System system) {
		this.system = system;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Coordinates getCoordinates() {
		return new Coordinates(getX(), getY());
	}

	public void addShip(Spaceship s) {
		units.add(s);
		discover(s.getOwner());
	}

	public boolean isDiscovered(Player player) {
		return discovered.containsKey(player);
	}

	public void discover(Player player) {
		discovered.put(player, true);
	}

	public ArrayList<Spaceship> getUnits() {
		return units;
	}

	public int numberOfPlayersInSector() {
		Set set = new HashSet();
		for (Spaceship ship : getUnits()) {
			if (!set.contains(ship.getOwner())) {
				set.add(ship.getOwner());
			}
		}
		return set.size();
	}


	public ArrayList<Spaceship> getUnits(Player player) {
		ArrayList<Spaceship> list = new ArrayList<Spaceship>();
		for (Spaceship ship : getUnits()) {
			if (ship.getOwner() == player) {
				list.add(ship);
			}
		}
		return list;
	}

	public boolean hasShips() {
		if (units.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

}

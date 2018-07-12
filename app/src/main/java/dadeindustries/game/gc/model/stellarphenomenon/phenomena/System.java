package dadeindustries.game.gc.model.stellarphenomenon.phenomena;

import java.lang.reflect.Array;
import java.util.ArrayList;

import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class System {

	private String name;
	private int x, y;
	private Faction faction;

	private System(String name, int x, int y, Faction faction) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.faction = faction;
	}

	public static boolean createNewSystem(String name, int x, int y, Faction owner, Sector[][] sectors) {
		if (!sectors[x][y].hasSystem()) {
			sectors[x][y].setSystem(new System(name, x, y, owner));
			return true;
		} else {
			return false;
		}
	}

	private ArrayList<String> buildQueue = new ArrayList<String>();

	public String getQueueHead() {
		if (buildQueue.size() == 0) {
			return "Empty";
		} else {
			return buildQueue.get(0);
		}
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

	public boolean hasFaction() {
		return faction != null;
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}
}
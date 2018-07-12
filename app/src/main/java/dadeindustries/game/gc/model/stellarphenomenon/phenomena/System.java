package dadeindustries.game.gc.model.stellarphenomenon.phenomena;

import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class System {

	private String name;
	private int x, y;
	private Faction owner;

	private System(String name, int x, int y, Faction owner) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.owner = owner;
	}

	public static boolean createNewSystem(String name, int x, int y, Faction owner, Sector[][] sectors) {
		if (!sectors[x][y].hasSystem()) {
			sectors[x][y].setSystem(new System(name, x, y, owner));
			return true;
		} else {
			return false;
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

	public boolean hasOwner() {
		return owner != null;
	}

	public Faction getOwner() {
		return owner;
	}

	public void setOwner(Faction owner) {
		this.owner = owner;
	}
}

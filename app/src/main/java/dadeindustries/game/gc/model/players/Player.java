package dadeindustries.game.gc.model.players;

//import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;

import dadeindustries.game.gc.model.enums.Extant;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.Intelligence;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class Player {

	private final Faction faction;
	private final Intelligence intelligence;
	private Extant extant;
	private int credits;
	private boolean dead = false;

	private HashSet<Sector> discovered = new HashSet<Sector>();
	private HashSet<Sector> visible = new HashSet<Sector>();

	public Player(Faction faction, Intelligence intelligence, Extant extant, int credits) {
		this.faction = faction;
		this.intelligence = intelligence;
		this.extant = extant;
		this.credits = credits;
	}

	public Faction getFaction() {
		return faction;
	}

	public Intelligence getIntelligence() {
		return intelligence;
	}

	public int getCredits() {
		return credits;
	}

	public void addCredits(int additionalCredits) {
		this.credits += additionalCredits;
	}

	public boolean subtractCredits(int subtractedCredits) {
		if (this.credits - subtractedCredits < 0) {
			return false;
		} else {
			this.credits -= subtractedCredits;
			return true;
		}
	}

	public boolean isDead() {
		return dead;
	}

	public void surrender() {
		dead = true;
	}

	public boolean hasDiscovered(Sector sector) {
		return discovered.contains(sector);
	}

	public HashSet getDiscoveredSectors() {
		return discovered;
	}

	public void discover(Sector sector) {
		discovered.add(sector);
	}

	public boolean isVisible(Sector sector) {
		return visible.contains(sector);
	}

	public void removeAllVisibility() {
		visible.clear();
	}

	public void makeVisible(Sector s) {
		visible.add(s);
	}
}

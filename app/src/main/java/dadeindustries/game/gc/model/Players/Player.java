package dadeindustries.game.gc.model.Players;

import dadeindustries.game.gc.model.Enums.Extant;
import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.Enums.Intelligence;

public class Player {

	private final Faction faction;
	private final Intelligence intelligence;
	private Extant extant;
	private int credits;

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
}

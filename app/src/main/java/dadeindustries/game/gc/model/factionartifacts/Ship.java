package dadeindustries.game.gc.model.factionartifacts;

import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

//to be made a subclass of 'commandableUnits' or some such, to that units and starbases are similar
public class Ship extends Unit {
	public Ship(Sector currentLocation, Faction faction, String shipname) {
		super(currentLocation, faction, shipname);
	}
}

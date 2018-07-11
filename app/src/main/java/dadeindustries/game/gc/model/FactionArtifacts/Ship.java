package dadeindustries.game.gc.model.FactionArtifacts;

import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

//to be made a subclass of 'commandableUnits' or some such, to that units and starbases are similar
public class Ship extends Unit {
	public Ship(Sector currentLocation, Faction faction, String shipname) {
		super(currentLocation, faction, shipname);
	}
}

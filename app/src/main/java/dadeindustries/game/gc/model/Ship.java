package dadeindustries.game.gc.model;

//to be made a subclass of 'commandableUnits' or some such, to that ships and starbases are similar
public class Ship extends Unit {

	
	public Ship(Sector currentLocation, GlobalGameData.Faction faction, String shipname)
	{
		super(currentLocation, faction, shipname);
	}
}

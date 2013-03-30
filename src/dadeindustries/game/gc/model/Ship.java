package dadeindustries.game.gc.model;

public class Ship {

	protected Sector currentLocation;
	protected GlobalGameData.Faction side;
	protected String name = null;
	
	public Ship(Sector currentLocation, GlobalGameData.Faction faction, String shipname)
	{
		side = faction;
		name = shipname;
		this.currentLocation = currentLocation;
	}
	
	public int getX(){
		return currentLocation.getX();
	}
	
	public int getY(){
		return currentLocation.getY();
	}
	
	public GlobalGameData.Faction getFaction(){
		return side;
	}
	
	public String getShipName(){
		return name;
	}
}

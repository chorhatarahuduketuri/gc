package dadeindustries.game.gc.model;

//all controllable units
public abstract class Unit {
	//FIELDS
	protected Sector currentLocation;
	protected GlobalGameData.Faction side;
	protected String unitName = null;
	private enum Order {
		MOVE
		,SUICIDE
		,COLONISE
		,BUILD_STARBASE
		,ATTACK_SYSTEM
//		,RAID
	}
	
	private int[] targetArea = new int[2]; 
	
	//CONSTRUCTORS
	public Unit(Sector currentLocation, GlobalGameData.Faction faction, String shipname) {
		side = faction;
		unitName = shipname;
		this.currentLocation = currentLocation;
	}
	
	//FUNCTIONS
	public void setDestination(int[] destination) throws Exception {
		//probably find a better way to handle this later, 
		//also needs to make sure destination actually 
		//exists and is within the ship's range
		if (destination.length!=2) {
			throw new Exception();
		}
		if(destination[0] >= GlobalGameData.galaxySizeX && destination[0] < 0) {
			throw new Exception();
		}
		if(destination[1] >= GlobalGameData.galaxySizeY && destination[1] < 0) {
			throw new Exception();
		}
		//should add something in here about starbases not moving? no, leave that in subclass Starbase 
		targetArea[0] = destination[0];
		targetArea[1] = destination[1];
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
		return unitName;
	}

}

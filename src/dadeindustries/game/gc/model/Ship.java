package dadeindustries.game.gc.model;

public class Ship {

	protected int x = 0;
	protected int y = 0;
	protected GlobalGameData.Faction side;
	protected String name = null;
	
	public Ship(int xinit, int yinit, GlobalGameData.Faction faction, String shipname)
	{
		x = xinit;
		y = yinit;
		side = faction;
		name = shipname;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public GlobalGameData.Faction getFaction(){
		return side;
	}
	
	public String getShipName(){
		return name;
	}
}

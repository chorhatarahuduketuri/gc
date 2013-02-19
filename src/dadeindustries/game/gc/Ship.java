package dadeindustries.game.gc;

public class Ship {

	public static enum Faction { UNITED_PLANETS, INIATS, GHZRGORZ, MORPHERS }
	
	
	int x = 0;
	int y = 0;
	Faction side;
	String name = null;
	
	public Ship(int xinit, int yinit, Faction faction, String shipname)
	{
		x = xinit;
		y = yinit;
		side = faction;
		name = shipname;
	}
}

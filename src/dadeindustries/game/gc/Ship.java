package dadeindustries.game.gc;

public class Ship {

	public static enum Faction { UNITED_PLANETS, INIATS, GHZRGORZ, MORPHERS }
		
	protected int x = 0;
	protected int y = 0;
	protected Faction side;
	protected String name = null;
	
	public Ship(int xinit, int yinit, Faction faction, String shipname)
	{
		x = xinit;
		y = yinit;
		side = faction;
		name = shipname;
	}
}

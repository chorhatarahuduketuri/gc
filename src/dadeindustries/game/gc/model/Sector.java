package dadeindustries.game.gc.model;

public class Sector {
	
	private SolarSystem system;

	/**
	 * TODO: flesh out this class. 
	 * create method to take in a System, and any other data necessary to define 
	 * factors of a system. 
	 */
	
	private final int x;
	private final int y;
	
	public Sector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}

}

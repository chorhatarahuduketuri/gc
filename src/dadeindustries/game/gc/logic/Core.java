package dadeindustries.game.gc.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.Sector;
import dadeindustries.game.gc.model.Ship;
import dadeindustries.game.gc.model.System;

/*
 * This is where all the games data structures are kept
 * separate from the game interfaces views
 */
public class Core {

	
	private Collection<Ship> ships = new ArrayList<Ship>();
	private Collection<System> systems = new ArrayList<System>();
	private Collection<Sector> sectors = new Vector<Sector>();
	
	public Core(){
		loadTestShips();
	}	
	
	private void loadTestShips() {
		ships.add(new Ship(4, 4, GlobalGameData.Faction.UNITED_PLANETS, "HMS Douglas"));
		ships.add(new Ship(5, 6, GlobalGameData.Faction.MORPHERS, "Kdfkljsdf"));
	}

	public void addShip(Ship newship){
		ships.add(newship);
	}
	
	public Collection<Ship> getShips(){
		return ships;
	}	
}

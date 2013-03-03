package dadeindustries.game.gc.logic;

import java.util.ArrayList;

import dadeindustries.game.gc.model.Ship;
import dadeindustries.game.gc.model.System;

/*
 * This is where all the games data structures are kept
 * separate from the game interfaces views
 */
public class Core {

	
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private ArrayList<System> systems = new ArrayList<System>();
	
	public Core(){
		loadTestShips();
	}	
	
	private void loadTestShips() {
		ships.add(new Ship(4, 4, Ship.Faction.UNITED_PLANETS, "HMS Douglas"));
		ships.add(new Ship(5, 6, Ship.Faction.MORPHERS, "Kdfkljsdf"));
	}

	public void addShip(Ship newship){
		ships.add(newship);
	}
	
	public ArrayList<Ship> getShips(){
		return ships;
	}	
}

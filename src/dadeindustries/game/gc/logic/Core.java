package dadeindustries.game.gc.logic;

import java.util.ArrayList;
import java.util.Collection;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.Sector;
import dadeindustries.game.gc.model.Ship;
import dadeindustries.game.gc.model.SolarSystem;

/*
 * This is where all the data structures relevant 
 * to an instance of a game are kept, separate 
 * from the views
 * 
 * it's also where the turn method is 
 * 
 */
public class Core {

	//FIELDS
	private Collection<Ship> ships = new ArrayList<Ship>();
	private Collection<SolarSystem> solarSystems = new ArrayList<SolarSystem>();
	private Sector[][] sectors = new Sector[50][50];

	//CONSTRUCTORS
	public Core() {
		for (int i = 0; i <= GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j <= GlobalGameData.galaxySizeY; j++) {
				sectors[i][j] = new Sector(i,j);
			}
		}
		loadTestShips();
	}

	//FUNCTIONS
	public boolean processTurn() {
		
		//if turn fails, return false
		return true;
	}
	
	private void loadTestShips() {
		ships.add(new Ship(sectors[3][3],
				GlobalGameData.Faction.UNITED_PLANETS, "HMS Douglas"));
		ships.add(new Ship(sectors[4][5],
				GlobalGameData.Faction.MORPHERS, "Kdfkljsdf"));
	}

	public void addShip(Ship newship) {
		ships.add(newship);
	}

	public Collection<Ship> getShips() {
		return ships;
	}
}

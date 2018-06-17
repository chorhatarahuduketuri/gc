package dadeindustries.game.gc.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.Sector;
import dadeindustries.game.gc.model.Ship;
import dadeindustries.game.gc.model.System;

/*
 * This is where all the data structures relevant
 * to an instance of a game are kept, separate
 * from the views
 *
 * it's also where the turn method is
 *
 */
public class Core {

	public Sector[][] sectors = new Sector[GlobalGameData.galaxySizeX][GlobalGameData.galaxySizeY];
	//FIELDS
	private Collection<Ship> ships = new ArrayList<Ship>();


	//CONSTRUCTORS
	public Core() {
		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				sectors[i][j] = new Sector(i, j);
			}
		}
		loadTestShips();
		loadTestPlanets();
	}

	//FUNCTIONS
	public boolean processTurn() {

		//if turn fails, return false
		return true;
	}

	private void loadTestShips() {
		addShip(new Ship(sectors[3][3], GlobalGameData.Faction.UNITED_PLANETS, "HMS Douglas"));
		addShip(new Ship(sectors[4][5], GlobalGameData.Faction.MORPHERS, "ISS Yuri"));
	}

	private void loadTestPlanets() {
		sectors[3][4] = new Sector(3, 4, new System("Planet X", 3, 4));
		sectors[4][4] = new Sector(4, 4, new System("Planet Y", 3, 4));

		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				if (sectors[i][j].hasSystem() == true) {
					Log.wtf("Planet loaded", "Planet loaded at " + i + "," + j);
				}
			}
		}
	}

	public void addShip(Ship newship) {
		ships.add(newship);
	}

	public Collection<Ship> getShips() {
		return ships;
	}

	public Sector[][] getSectors() {
		return sectors;
	}
}

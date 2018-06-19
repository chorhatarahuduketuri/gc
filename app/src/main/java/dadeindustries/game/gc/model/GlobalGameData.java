package dadeindustries.game.gc.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import dadeindustries.game.gc.model.FactionArtifacts.Ship;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;
import dadeindustries.game.gc.model.StellarPhenomenon.Phenomena.System;
import dadeindustries.game.gc.model.Enums.Faction;

/**
 * whereupon the overall definition of the universe is held.
 * races, galaxy size,
 *
 * @author krongoth, adrianlshaw
 */
public class GlobalGameData {

	//FIELDS
	public static int galaxySizeX = 10;
	public static int galaxySizeY = 10;
	// Game data structures REFERENCE
	public Sector[][] sectors = new Sector[GlobalGameData.galaxySizeX][GlobalGameData.galaxySizeY];
	private Collection<Ship> ships = new ArrayList<Ship>();


	//CONSTRUCTORS
	public GlobalGameData(int x, int y) {
		galaxySizeX = x;
		galaxySizeY = y;
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
		addShip(new Ship(sectors[2][2], Faction.UNITED_PLANETS, "HMS Douglas"));
		addShip(new Ship(sectors[3][4], Faction.MORPHERS, "ISS Yuri"));
	}

	private void loadTestPlanets() {
		sectors[3][4] = new Sector(2, 3, new System("Planet X", 3, 4));
		sectors[4][4] = new Sector(3, 3, new System("Planet Y", 3, 4));

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

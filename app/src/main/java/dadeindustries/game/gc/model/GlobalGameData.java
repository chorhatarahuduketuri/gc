package dadeindustries.game.gc.model;

import android.util.Log;

import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.FactionArtifacts.Ship;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.StellarPhenomenon.Phenomena.System;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

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
	private static int turn = 0;
	private static int playerCredits = 0;
	// Game data structures REFERENCE
	public Sector[][] sectors = new Sector[GlobalGameData.galaxySizeX][GlobalGameData.galaxySizeY];


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

	private void loadTestShips() {
		sectors[2][2].addShip(new Ship(sectors[2][2], Faction.UNITED_PLANETS, "HMS Douglas"));
		sectors[3][4].addShip(new Ship(sectors[0][0], Faction.MORPHERS, "ISS Yuri"));
		sectors[1][1].addShip(new Ship(sectors[1][1], Faction.UNITED_PLANETS, "USS Dade"));
	}

	private void loadTestPlanets() {
		sectors[3][4] = new Sector(2, 3, new System("Planet X", 3, 4));
		sectors[4][4] = new Sector(3, 3, new System("Planet Y", 3, 4));

		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				if (sectors[i][j].hasSystem()) {
					Log.wtf("Planet loaded", "Planet loaded at " + i + "," + j);
				}
			}
		}
	}

	public Sector[][] getSectors() {
		return sectors;
	}

	public int getTurn() {
		return turn;
	}

	public static void setTurn(int turn) {
		GlobalGameData.turn = turn;
	}

	public int getPlayerCredits() {
		return playerCredits;
	}


}

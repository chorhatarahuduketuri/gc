package dadeindustries.game.gc.model;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.model.FactionArtifacts.Ship;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
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

        ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

        /* For each sector of the galaxy */
        for (int x = 0; x < galaxySizeX; x++) {
            for (int y = 0; y < galaxySizeY; y++ ) {

                /* Get the list of ships within the sector */
                ArrayList<Unit> localships = sectors[x][y].getShips();

                /* If there are any ships */
                if (localships.size() > 0) {

                    /* Then find ships with a set course */
                    for (int u = 0; u < localships.size(); u++) {
                        Coordinates currentCoods = new Coordinates(x, y);
                        Coordinates destCoods = localships.get(u).continueCourse();

                        /* If any ship has a course set */
                        if (destCoods != null) {
                            Unit unit = localships.get(u);

                            /* Prepare the move for this ship */
                            pendingMoves.add(new PendingMove(unit, destCoods.x, destCoods.y));
                            /* Remove ship from this sector */
                            localships.remove(u);
                        }
                    }
                }
            }
        }

        for (PendingMove p : pendingMoves) {
            Unit unit = p.unit;
            unit.setSector(sectors[p.x][p.y]);
            Log.wtf("Next: ", ""+p.x + "," +p.y);
            sectors[p.x][p.y].addShip(unit);
        }


        //if turn fails, return false
		return true;
	}

	private void loadTestShips() {
		sectors[2][2].addShip( new Ship(sectors[2][2], Faction.UNITED_PLANETS, "HMS Douglas"));
		sectors[3][4].addShip( new Ship(sectors[0][0], Faction.MORPHERS, "ISS Yuri"));
		sectors[1][1].addShip( new Ship(sectors[1][1], Faction.UNITED_PLANETS, "USS Dade"));
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

	public Sector[][] getSectors() {
		return sectors;
	}


    class PendingMove {
        Unit unit;
        public int x, y;


        public PendingMove(Unit u, int destX, int destY) {
            unit = u;
            x = destX;
            y = destY;
        }
    }


}

package dadeindustries.game.gc.model;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.model.Enums.Extant;
import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.Enums.Intelligence;
import dadeindustries.game.gc.model.FactionArtifacts.Ship;
import dadeindustries.game.gc.model.Players.Player;
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
	private static Sector[][] sectors = new Sector[GlobalGameData.galaxySizeX][GlobalGameData.galaxySizeY];
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<Mind> minds = new ArrayList<Mind>();
	private static int turn = 0;

	//CONSTRUCTORS

	public GlobalGameData(int x, int y) {
		galaxySizeX = x;
		galaxySizeY = y;
		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				sectors[i][j] = new Sector(i, j);
			}
		}
		insertTestShips();
		insertTestSystems();
		createTestPlayers();
		createTestMinds();
	}

	//FUNCTIONS

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static ArrayList<Mind> getMinds() {
		return minds;
	}


	public static boolean isHumanFaction(Faction faction) {
		for (Player p : players) {
			if (p.getFaction().equals(faction)) {
				return p.getIntelligence().equals(Intelligence.HUMAN);
			}
		}
		return false;
	}

	private void createTestPlayers() {
		players.add(new Player(Faction.UNITED_PLANETS, Intelligence.HUMAN, Extant.EXISTENT, 10));
		players.add(new Player(Faction.MORPHERS, Intelligence.ARTIFICIAL, Extant.EXISTENT, 10));
	}

	//Must be called **AFTER** createTestPlayers!!!!
	private void createTestMinds() {
		for (Player p : players) {
			if (p.getIntelligence().equals(Intelligence.ARTIFICIAL)) {
				minds.add(new Mind(p));
			}
		}
	}

	private void insertTestShips() {
		sectors[2][2].addShip(new Ship(sectors[2][2], Faction.UNITED_PLANETS, "HMS Douglas"));
		sectors[1][1].addShip(new Ship(sectors[1][1], Faction.UNITED_PLANETS, "USS Dade"));
		sectors[1][3].addShip(new Ship(sectors[1][3], Faction.MORPHERS, "ISS Yuri"));
		sectors[7][7].addShip(new Ship(sectors[7][7], Faction.MORPHERS, "ISS Ensa"));
	}

	private void insertTestSystems() {
		sectors[1][1].setSystem(new System("Planet X", 1, 1));
		sectors[3][3].setSystem(new System("Planet Y", 3, 3));

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

	// Assumes there is exactly one human player.
	public int getHumanPlayerCredits() {
		for (Player p : players) {
			if (p.getIntelligence().equals(Intelligence.HUMAN)) {
				return p.getCredits();
			}
		}
		return 0;
	}
}

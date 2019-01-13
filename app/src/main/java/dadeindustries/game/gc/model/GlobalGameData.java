package dadeindustries.game.gc.model;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.model.enums.Extant;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.Intelligence;
import dadeindustries.game.gc.model.factionartifacts.ColonyShip;
import dadeindustries.game.gc.model.factionartifacts.CombatShip;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.Wormhole;

/**
 * whereupon the overall definition of the universe is held.
 * races, galaxy size,
 *
 * @author krongoth, adrianlshaw
 */
public class GlobalGameData {

	public static int galaxySizeX = 10;
	public static int galaxySizeY = 10;
	private static Sector[][] sectors = new Sector[GlobalGameData.galaxySizeX][GlobalGameData.galaxySizeY];
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<Mind> minds = new ArrayList<Mind>();
	private static int turn = 0;

	public GlobalGameData(int x, int y) {
		galaxySizeX = x;
		galaxySizeY = y;
		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				sectors[i][j] = new Sector(i, j);
			}
		}
		createTestPlayers();
		createTestMinds();
		insertTestSystems();
		insertTestShips();
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static ArrayList<Mind> getMinds() {
		return minds;
	}

	public static boolean isHumanPlayer(Player player) {
		for (Player p : players) {
			if (p.equals(player)) {
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
		sectors[2][2].addShip(new CombatShip(players.get(0), sectors[2][2], Faction.UNITED_PLANETS, "HMS Douglas", 2, 4));
		sectors[1][1].addShip(new CombatShip(players.get(0), sectors[1][1], Faction.UNITED_PLANETS, "USS Dade", 2, 4));
		sectors[1][2].addShip(new ColonyShip(players.get(0), sectors[1][2], Faction.UNITED_PLANETS, "USS Adrian", 0, 4));
		sectors[1][3].addShip(new CombatShip(players.get(1), sectors[1][3], Faction.MORPHERS, "ISS Yuri", 2, 4));
		sectors[7][7].addShip(new CombatShip(players.get(1), sectors[7][7], Faction.MORPHERS, "ISS Ensa", 2, 4));
		sectors[0][0] = new Wormhole(0, 0, sectors[8][8]);
		sectors[8][8] = new Wormhole(8,8, sectors[0][0]);

	}

	private void insertTestSystems() {
		System.createNewSystem("United Planets Homeworld", 1, 1, players.get(0), sectors);
		System.createNewSystem("System X", 1, 2, null, sectors);
		System.createNewSystem("Morphers Homeworld", 1, 6, players.get(1), sectors);
		System.createNewSystem("System Y", 3, 5, null, sectors);

		for (int i = 0; i < GlobalGameData.galaxySizeX; i++) {
			for (int j = 0; j < GlobalGameData.galaxySizeY; j++) {
				if (sectors[i][j].hasSystem()) {
					Log.wtf("System loaded", "System loaded at " + i + "," + j);
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

	// Assumes there is exactly one human player
	public Player getHumanPlayer() {
		for (Player player : players) {
			if (player.getIntelligence().equals(Intelligence.HUMAN)) {
				return player;
			}
		}
		return null; // this must never happen
	}

}

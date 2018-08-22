package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.factionartifacts.ColonyShip;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;

public class TurnProcessor {

	/**
	 * endTurn is called when the End Turn button is pressed.
	 * <p>
	 * This method exists to call other methods that execute the processing of a turn.
	 *
	 * @param globalGameData This is the game state to process into the next turn.
	 */
	public ArrayList<Event> endTurn(GlobalGameData globalGameData) {
		computeAiTurns(globalGameData);
		return processTurn(globalGameData);
	}

	public ArrayList<Event> processTurn(GlobalGameData globalGameData) {

		ArrayList<Event> events = new ArrayList<Event>();
		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

		GlobalGameData.setTurn(globalGameData.getTurn() + 1);

		updateBuildQueues(globalGameData.getSectors(), events);

		processUnitActions(globalGameData, pendingMoves, events); // handle unit movements

		processPendingMoves(globalGameData, pendingMoves);

		processConflicts(globalGameData, events); // handle unit battles

		// Detect if a player has won the game
		Player didAnyoneWin = detectWinCondition(globalGameData);

		if (didAnyoneWin != null) {
			Event winevent = new Event(Event.EventType.WINNER,
					didAnyoneWin.getIntelligence() + " intelligence won the game!",
					null);
			events.add(winevent);
		}

		return events;
	}

	private ArrayList<Spaceship> getAllShipsForPlayer(Sector[][] sectors, Player player) {

		ArrayList<Spaceship> returnShips = new ArrayList<Spaceship>();

		for (int i = 0; i < sectors.length; i++) {
			for (int j = 0; j < sectors.length; j++) {
				for (Spaceship spaceship : sectors[i][j].getUnits()) {
					if (spaceship.getOwner() == player) {
						returnShips.add(spaceship);
					}
				}
			}
		}
		return returnShips;
	}

	private ArrayList<System> getAllSystemsForPlayer(Sector[][] sectors, Player player) {

		ArrayList returnSystems = new ArrayList();

		for (int i = 0; i < sectors.length; i++) {
			for (int j = 0; j < sectors.length; j++) {
				if (sectors[i][j].hasSystem()) {
					if (sectors[i][j].getSystem().hasOwner()) {
						if (sectors[i][j].getSystem().getOwner() == player) {
							returnSystems.add(sectors[i][j].getSystem());
						}
					}
				}
			}
		}
		return returnSystems;
	}

	/**
	 * Detect if someone has won the game
	 *
	 * @param globalGameData global game state
	 * @return Player if someone has won
	 */
	private Player detectWinCondition(GlobalGameData globalGameData) {

		Player winningPlayer = null;
		int activePlayerCount = 0;

		for (Player player : globalGameData.getPlayers()) {
			boolean alive = false;

			ArrayList<Spaceship> ships = getAllShipsForPlayer(globalGameData.getSectors(), player);
			ArrayList<System> systems = getAllSystemsForPlayer(globalGameData.getSectors(), player);

			/* If player has a colony ship then they can still win */
			for (Spaceship ship : ships) {
				if (ship instanceof ColonyShip) {
					alive = true;
				}
			}

			/* If player still has a system then they can still win */
			if (systems.size() > 0) {
				alive = true;
			}

			/* If they have none of these things then it's game over for them */
			if (alive == false) {
				player.surrender();
			} else {
				activePlayerCount++;
			}
		}

		/* If one player is active, WE HAVE A WINNER */
		if (activePlayerCount == 1) {
			for (Player player : globalGameData.getPlayers()) {
				if (player.isDead() == false) {
					winningPlayer = player;
				}
			}
		}

		return winningPlayer;
	}

	/**
	 * Go through each sector in the galaxy and advance the build queue.
	 * If ships are ready then place them in their sector.
	 *
	 * @param sectors the game's sectors
	 * @param events  a list of events that we can append to, which is eventually returned to player
	 */
	private void updateBuildQueues(Sector[][] sectors, ArrayList<Event> events) {

		// For each sector
		for (int i = 0; i < sectors.length; i++) {
			for (int j = 0; j < sectors.length; j++) {

				// If sector has a system
				if (sectors[i][j].hasSystem()) {

					Spaceship ship = sectors[i][j].getSystem().getBuildQueueHead();

					// If ship is ready then add it to the system's list of ships
					if (ship != null) {
						sectors[i][j].addShip(ship);
						// Create a notification for the player
						events.add(new Event(Event.EventType.UNIT_CONSTRUCTION_COMPLETE,
								"New ship at " + sectors[i][j].getSystem().getName(),
								sectors[i][j].getCoordinates()));
					}

					// If occupied system has an empty build queue then make an event
					if (sectors[i][j].getSystem().isBuildQueueEmpty() &&
							sectors[i][j].getSystem().hasOwner()) {
						events.add(new Event(Event.EventType.EMPTY_QUEUE,
								"Empty queue at " + sectors[i][j].getSystem().getName(),
								null));
					}
				}
			}
		}
	}

	/**
	 * computeAiTurns computes the AIs orders and adds them to the order lists.
	 * <p>
	 * This method executes the AI players decision making processes and adds the orders they give
	 * to the GlobalGameData so that they can be executed by the mechanics code.
	 *
	 * @param globalGameData The global game state to be computed on by the AIs
	 */
	private void computeAiTurns(GlobalGameData globalGameData) {
		ArrayList<Mind> minds = GlobalGameData.getMinds();
		Sector[][] sectors = globalGameData.getSectors();

		for (Mind mind : minds) {
			Player player = mind.getPlayer();
			List<System> systemList = getAllSystemsForPlayer(sectors, player);
			List<Spaceship> spaceshipList = getAllShipsForPlayer(sectors, player);
			mind.computeTurn(globalGameData, systemList, spaceshipList);
		}
	}

	/**
	 * The UI creates a finite number of "pending moves" per turn.
	 *
	 * @param globalGameData The global state with all the sectors (there is only one instance)
	 * @param pendingMoves   A list of actions a player has made this turn.
	 */
	private void processPendingMoves(GlobalGameData globalGameData, ArrayList<PendingMove> pendingMoves) {
		for (PendingMove pendingMove : pendingMoves) {
			Log.d("processPendingMoves", pendingMove.unit.getShipName() + pendingMove.getX() + pendingMove.getY());
			Spaceship pendingMoveUnit = pendingMove.getUnit();
			pendingMoveUnit.setSector(globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()]);
			Log.wtf("Next: ", "" + pendingMove.getX() + "," + pendingMove.getY());
			globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].addShip(pendingMoveUnit);
		}
	}

	private void processConflicts(GlobalGameData globalGameData, ArrayList<Event> events) {

		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				if (globalGameData.getSectors()[x][y].numberOfPlayersInSector() > 1) {
					events.add(UnitActions.processBattle(globalGameData.getSectors()[x][y]));
				}

				/* Remove dead ships */
				Iterator<Spaceship> iterator = globalGameData.getSectors()[x][y].getUnits().iterator();

				while (iterator.hasNext()) {
					Spaceship aUnit = iterator.next();
					if (aUnit.getCurrentHP() <= 0) {
						Log.wtf("Battle", aUnit.getShipName() + " destroyed");

						iterator.remove();
					}
				}
			}
		}
	}

	/**
	 * The UI creates a finite number of "pending moves" per turn.
	 * A pending move may, for example, be a command to move a unit from sector to another.
	 * This method look for units that require moving and adds them to the provided list.
	 *
	 * @param globalGameData The global state with all the sectors (there is only one instance)
	 * @param pendingMoves   A list of actions a player has made this turn.
	 */
	private void processUnitActions(GlobalGameData globalGameData, ArrayList<PendingMove> pendingMoves, ArrayList<Event> events) {
		// For each sector of the galaxy
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				Sector sector = globalGameData.getSectors()[x][y];
				/* Get the list of units within the sector */
				ArrayList<Spaceship> localShips = sector.getUnits();
				List<Integer> shipsToRemove = new LinkedList<Integer>();

				/* If there are any units */
				if (localShips.size() > 0) {

					/* Then find units with a set course */
					for (int u = 0; u < localShips.size(); u++) {
						Coordinates destinationCoordinates = UnitActions.continueCourse(localShips.get(u));

						/* If any ship has a course set */
						if (destinationCoordinates != null) {
							Spaceship unit = localShips.get(u);

							/* Prepare the move for this ship */
							pendingMoves.add(new PendingMove(unit, destinationCoordinates.x, destinationCoordinates.y));
							/* Remove ship from this sector */
							shipsToRemove.add(u);
						} else if (localShips.get(u) instanceof ColonyShip) {
							if (((ColonyShip) localShips.get(u)).isColonising()) {
								if (sector.hasSystem()) {
									if (sector.getSystem().hasOwner() == false) {
										Log.wtf("Colony", "Colonised");
										UnitActions.coloniseSystem(localShips.get(u), globalGameData);
										/* Remove ship from this sector */
										shipsToRemove.add(u);
										events.add(new Event(Event.EventType.COLONISE,
												sector.getSystem().getName() +
														" was colonised", new Coordinates(x, y)));
									}
								}
							}
						}
					}
				}
				Collections.sort(shipsToRemove, Collections.reverseOrder());
				for (int i : shipsToRemove) {
					localShips.remove(i);
				}
			}
		}
	}

	/**
	 * PendingMove
	 * <p>
	 * This describes a move that a unit has been ordered to make, and is yet to be executed.
	 */
	public class PendingMove {
		private Spaceship unit;
		private int x, y;


		public PendingMove(Spaceship u, int destX, int destY) {
			unit = u;
			x = destX;
			y = destY;
		}

		public Spaceship getUnit() {
			return unit;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
}

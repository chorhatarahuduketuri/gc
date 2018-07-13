package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

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

		processUnitActions(globalGameData, pendingMoves); // handle unit movements

		processPendingMoves(globalGameData, events, pendingMoves); // handle unit battles

		return events;
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
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				for (Spaceship u : globalGameData.getSectors()[x][y].getUnits()) {
					for (Mind m : globalGameData.getMinds()) {
						if (m.getPlayer().getFaction().equals(u.getFaction())) {
							m.giveOrder(u);
						}
					}
				}
			}
		}
	}

	/**
	 * The UI creates a finite number of "pending moves" per turn.
	 * This method handles pending battles.
	 *
	 * @param globalGameData The global state with all the sectors (there is only one instance)
	 * @param events         An existing list of events that can be appended to with new events.
	 * @param pendingMoves   A list of actions a player has made this turn.
	 */
	private void processPendingMoves(GlobalGameData globalGameData, ArrayList<Event> events, ArrayList<PendingMove> pendingMoves) {
		for (PendingMove pendingMove : pendingMoves) {
			Spaceship pendingMoveUnit = pendingMove.getUnit();
			pendingMoveUnit.setSector(globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()]);
			Log.wtf("Next: ", "" + pendingMove.getX() + "," + pendingMove.getY());
			globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].addShip(pendingMoveUnit);

			/* Detect if battles occur */
			for (Spaceship unit : globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].getUnits()) {
				/* If there are two opposing factions */
				if (unit.getFaction() != pendingMoveUnit.getFaction()) {
					events.add(UnitActions.processBattle(globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()]));
				}
			}

			/* Remove dead ships */
			Iterator<Spaceship> iterator = globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].getUnits().iterator();

			while (iterator.hasNext()) {
				Spaceship aUnit = iterator.next();
				if (aUnit.getCurrentHP() <= 0) {
					Log.wtf("Battle", aUnit.getShipName() + " destroyed");

					iterator.remove();
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
	private void processUnitActions(GlobalGameData globalGameData, ArrayList<PendingMove> pendingMoves) {
		// For each sector of the galaxy
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				Sector sector = globalGameData.getSectors()[x][y];
				/* Get the list of units within the sector */
				ArrayList<Spaceship> localShips = sector.getUnits();

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
							localShips.remove(u);
						}
					}
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

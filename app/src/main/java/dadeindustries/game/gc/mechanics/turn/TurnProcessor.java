package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

public class TurnProcessor {

	/**
	 * endTurn is called when the End Turn button is pressed.
	 * <p>
	 * This method exists to call other methods that execute the processing of a turn.
	 *
	 * @param globalGameData This is the game state to process into the next turn.
	 */
	public void endTurn(GlobalGameData globalGameData) {
		computeAiTurns(globalGameData);
		processTurn(globalGameData);
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
				for (Unit u : globalGameData.getSectors()[x][y].getUnits()) {
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
	 * processTurn executes the pending moves and other actions listed in the GlobalGameData object.
	 * <p>
	 * First, it increments the turn counter by 1.
	 * <p>
	 * Second, it iterates through the sectors of the game space and finds all the ships, and makes
	 * a list of the move orders they're going to execute in this turn.
	 * <p>
	 * Third, it iterates through all the ships and moves them to new sectors.
	 *
	 * @param globalGameData The game state to be moved on by one turn
	 */
	public void processTurn(GlobalGameData globalGameData) {

		GlobalGameData.setTurn(globalGameData.getTurn() + 1);

		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

		/* For each sector of the galaxy */
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				pendingMoves = processUnitActions(globalGameData.getSectors()[x][y]);
			}
		}

		for (PendingMove p : pendingMoves) {
			Unit unit = p.getUnit();
			unit.setSector(globalGameData.getSectors()[p.getX()][p.getY()]);
			Log.wtf("Next: ", "" + p.getX() + "," + p.getY());
			globalGameData.getSectors()[p.getX()][p.getY()].addShip(unit);
		}
	}

	/**
	 * processUnitActions collects the PendingMove orders of ships in the galaxy
	 * <p>
	 * This method makes a list of the next PendingMove objects in the courses of all units in the
	 * sector passed as the parameter.
	 *
	 * @param sector The sector that the units are in
	 * @return ArrayList of PendingMove objects that represent the moves executed this turn
	 */
	private ArrayList<PendingMove> processUnitActions(Sector sector) {
		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();
		/* Get the list of units within the sector */
		ArrayList<Unit> localShips = sector.getUnits();

		/* If there are any units */
		if (localShips.size() > 0) {

			/* Then find units with a set course */
			for (int u = 0; u < localShips.size(); u++) {
				Coordinates destinationCoordinates = UnitActions.continueCourse(localShips.get(u));

				/* If any ship has a course set */
				if (destinationCoordinates != null) {
					Unit unit = localShips.get(u);

					/* Prepare the move for this ship */
					pendingMoves.add(new PendingMove(unit, destinationCoordinates.x, destinationCoordinates.y));
					/* Remove ship from this sector */
					localShips.remove(u);
				}
			}
		}
		return pendingMoves;
	}

	/**
	 * PendingMove
	 *
	 * This describes an atomic move action that is part of a course that a unit has been ordered
	 * to travel, and is yet to be executed.
	 */
	public class PendingMove {
		private Unit unit;
		private int x, y;


		public PendingMove(Unit u, int destX, int destY) {
			unit = u;
			x = destX;
			y = destY;
		}

		public Unit getUnit() {
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

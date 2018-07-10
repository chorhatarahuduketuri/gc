package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

public class TurnProcessor {

	public ArrayList<Event> endTurn(GlobalGameData globalGameData) {
		computeAiTurns(globalGameData);
		return processTurn(globalGameData);
	}

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

	private Event processBattle(Sector sector) {

		Event result = new Event(Event.EventType.BATTLE, "Enemy ships encountered",
				new Coordinates(sector.getX(), sector.getY()));

		for (int i = 0; i < sector.getUnits().size(); i++) {
			int attack = sector.getUnits().get(i).getAttackLevel();
			int target = 0;

			int j = i;

			while (sector.getUnits().get(j).getFaction() == sector.getUnits().get(i).getFaction()) {
				j++;
				if (j >= sector.getUnits().size()) {
					target = 0;
					j = 0;
				} else {
					target = j;
				}
			}
			sector.getUnits().get(target).damage(attack);

			// Put result in an Event object and return it
			String log = sector.getUnits().get(i).getShipName() + " attacked " +
					sector.getUnits().get(target).getShipName() + " with " + attack + " damage";
			result.appendDescription(log);
			Log.wtf("Battle", log);
		}

		for (Unit unit : sector.getUnits()) {
			if (unit.getCurrentHP() <= 0) {
				result.appendDescription(unit.getShipName() + " was destroyed");
			}
		}
		return result;
	}

	public ArrayList<Event> processTurn(GlobalGameData globalGameData) {

		GlobalGameData.setTurn(globalGameData.getTurn() + 1);

		ArrayList<Event> events = new ArrayList<Event>();

		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

		/* For each sector of the galaxy */
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				processUnitActions(globalGameData.getSectors()[x][y], pendingMoves);
			}
		}

		for (PendingMove pendingMove : pendingMoves) {
			Unit pendingMoveUnit = pendingMove.getUnit();
			pendingMoveUnit.setSector(globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()]);
			Log.wtf("Next: ", "" + pendingMove.getX() + "," + pendingMove.getY());
			globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].addShip(pendingMoveUnit);

			/* Detect if battles occur */
			for (Unit unit : globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].getUnits()) {
				/* If there are two opposing factions */
				if (unit.getFaction() != pendingMoveUnit.getFaction()) {
					events.add(processBattle(globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()]));
				}
			}

			/* Remove dead ships */
			Iterator<Unit> iterator = globalGameData.getSectors()[pendingMove.getX()][pendingMove.getY()].getUnits().iterator();

			while (iterator.hasNext()) {
				Unit aUnit = iterator.next();
				if (aUnit.getCurrentHP() <= 0) {
					Log.wtf("Battle", aUnit.getShipName() + " destroyed");

					iterator.remove();
				}
			}
		}

		//if turn fails, return false
		return events;
	}

	private void processUnitActions(Sector sector, ArrayList<PendingMove> pendingMoves) {
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
	}

	/**
	 * PendingMove
	 * This describes a move that a unit has been ordered to make, and is yet to be executed.
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

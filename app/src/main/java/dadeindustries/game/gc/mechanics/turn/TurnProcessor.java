package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.ai.Mind;
import dadeindustries.game.gc.mechanics.units.UnitActions;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.Enums.Faction;
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

	private ArrayList<String> battleSummaries = new ArrayList<String>();

	private Event processBattle(Sector s) {

		int numberOfFactions = Faction.values().length;

		// Create a separate unit list for each faction
		ArrayList factions[] = new ArrayList[numberOfFactions];
		for (int i = 0; i < numberOfFactions; i++) {
			factions[i] = new ArrayList();
		}

		for (Unit u : s.getUnits()) {
			factions[u.getFaction().ordinal()].add(u);
		}

		// TODO: add code here to pick: who shoots first, reduce ship health, remove ships, etc.

		// Put result in an Event object and return it
		Event result = new Event(Event.EventType.BATTLE, "Enemy ships encountered",
				new Coordinates(s.getX(), s.getY()));
		return result;
	}

	public ArrayList<Event> processTurn(GlobalGameData globalGameData) {

		GlobalGameData.setTurn(globalGameData.getTurn() + 1);

		ArrayList<Event> events = new ArrayList<Event>();

		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

		/* For each sector of the galaxy */
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				processUnitActions(globalGameData.getSectors()[x][y], pendingMoves, x, y);
			}
		}

		for (PendingMove p : pendingMoves) {
			Unit unit = p.getUnit();
			unit.setSector(globalGameData.getSectors()[p.getX()][p.getY()]);
			Log.wtf("Next: ", "" + p.getX() + "," + p.getY());
			globalGameData.getSectors()[p.getX()][p.getY()].addShip(unit);

			/* Detect if battles occur */
			for (Unit u : globalGameData.getSectors()[p.getX()][p.getY()].getUnits()) {
				/* If there are two opposing factions */
				if (u.getFaction() != unit.getFaction()) {
					events.add(processBattle(globalGameData.getSectors()[p.getX()][p.getY()]));
				}
			}
		}


		//if turn fails, return false
		return events;
	}

	private void processUnitActions(Sector sector, ArrayList<PendingMove> pendingMoves, int x, int y) {
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

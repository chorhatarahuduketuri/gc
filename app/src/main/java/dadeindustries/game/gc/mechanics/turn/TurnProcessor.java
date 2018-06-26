package dadeindustries.game.gc.mechanics.turn;

import android.util.Log;

import java.util.ArrayList;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

public class TurnProcessor {

	public void endTurn(GlobalGameData globalGameData) {
		processTurn(globalGameData, globalGameData.sectors);
	}

	//FUNCTIONS
	public boolean processTurn(GlobalGameData globalGameData, Sector[][] sectors) {

		GlobalGameData.setTurn(globalGameData.getTurn() + 1);

		ArrayList<PendingMove> pendingMoves = new ArrayList<PendingMove>();

		/* For each sector of the galaxy */
		for (int x = 0; x < GlobalGameData.galaxySizeX; x++) {
			for (int y = 0; y < GlobalGameData.galaxySizeY; y++) {
				processUnitActions(sectors[x][y], pendingMoves, x, y);


			}
		}

		for (PendingMove p : pendingMoves) {
			Unit unit = p.getUnit();
			unit.setSector(sectors[p.getX()][p.getY()]);
			Log.wtf("Next: ", "" + p.getX() + "," + p.getY());
			sectors[p.getX()][p.getY()].addShip(unit);
		}


		//if turn fails, return false
		return true;
	}

	private void processUnitActions(Sector sector, ArrayList<PendingMove> pendingMoves, int x, int y) {
		/* Get the list of units within the sector */
		ArrayList<Unit> localShips = sector.getUnits();

		/* If there are any units */
		if (localShips.size() > 0) {

			/* Then find units with a set course */
			for (int u = 0; u < localShips.size(); u++) {
				Coordinates currentCoordinates = new Coordinates(x, y);
				Coordinates destinationCoordinates = localShips.get(u).continueCourse();

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

		public void setUnit(Unit unit) {
			this.unit = unit;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}
}

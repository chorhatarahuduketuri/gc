package dadeindustries.game.gc.mechanics.units;

import android.util.Log;

import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

public class UnitActions {

	public static void setCourse(Unit unit, int destX, int destY) {

		Log.wtf("Plotting", "...");

		int srcX = unit.getX();
		int srcY = unit.getY();

		unit.clearCourse(); // Remove any previous unit.getCourse

		while (srcX < destX) {

			if (srcY == destY) {
				if (destX == srcX) {
					break;
				}
			} else if (srcY < destY) {
				srcY++;
			} else {
				srcY--;
			}

			if (srcX != destX) {
				srcX++;
			}
			Log.wtf("Plotting", srcX + "," + srcY);
			unit.addToCourse(new Coordinates(srcX, srcY));
		}

		while (destX <= srcX) {

			if (srcY == destY) {
				if (destX == srcX) {
					break;
				}
			} else if (srcY < destY) {
				srcY++;
			} else {
				srcY--;
			}

			if (srcX != (destX)) {
				srcX--;
			}

			Log.wtf("Plotting", srcX + "," + srcY);
			unit.addToCourse(new Coordinates(srcX, srcY));

		}
		Log.wtf("Plotting", "Finished");
	}

	public static Coordinates continueCourse(Unit unit) {
		if (unit.hasCourse()) {
			return unit.getNextCoordinatesInCourse();
		} else {
			return null;
		}
	}

	public static Event processBattle(Sector sector) {

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

			// Put result in an Event object
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
}

package dadeindustries.game.gc.mechanics.units;

import android.util.Log;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;

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
}

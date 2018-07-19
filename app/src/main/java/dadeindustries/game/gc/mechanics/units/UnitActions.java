package dadeindustries.game.gc.mechanics.units;

import android.util.Log;

import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.factionartifacts.Spacecraft;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class UnitActions {

	public static void setCourse(Spaceship unit, int destX, int destY) {

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

	public static Coordinates continueCourse(Spaceship unit) {
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

			while (sector.getUnits().get(j).getOwner() == sector.getUnits().get(i).getOwner()) {
				j++;
				if (j >= sector.getUnits().size()) {
					target = 0;
					j = 0;
				} else {
					target = j;
				}
			}

			/* If attacker is alive and well and if target is alive */
			if ((sector.getUnits().get(i).getCurrentHP() > 0) &&
					(sector.getUnits().get(target).getCurrentHP() > 0)) {

				/* Damage the target */
				sector.getUnits().get(target).damage(attack);

				/* Put result in an Event object */
				String log = sector.getUnits().get(i).getShipName() + " attacked " +
						sector.getUnits().get(target).getShipName() + " with " + attack + " damage";
				result.appendDescription(log);
				Log.wtf("Battle", log);
			}
		}

		for (Spaceship unit : sector.getUnits()) {
			if (unit.getCurrentHP() <= 0) {
				result.appendDescription(unit.getShipName() + " was destroyed");
			}
		}
		return result;
	}

	public static void coloniseSystem(Spacecraft selectedShip, GlobalGameData globalGameData) {
		Sector selectedSector = globalGameData.getSectors()[selectedShip.getX()][selectedShip.getY()];
		if (selectedSector.hasSystem() && !selectedSector.getSystem().hasOwner()) {
			selectedSector.getSystem().setOwner(selectedShip.getOwner());
		}
	}

	public static void attackSystem(Spacecraft selectedShip, GlobalGameData globalGameData) {
		Sector selectedSector = globalGameData.getSectors()[selectedShip.getX()][selectedShip.getY()];
		if (selectedSector.hasSystem() && selectedSector.getSystem().hasOwner() && selectedSector.getSystem().getOwner() != selectedShip.getOwner()) {
			selectedSector.getSystem().setOwner(null);
		}
	}
}

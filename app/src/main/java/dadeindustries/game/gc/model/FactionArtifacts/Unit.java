package dadeindustries.game.gc.model.FactionArtifacts;

import android.util.Log;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;
import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.Cood;
import java.util.ArrayDeque;

//all controllable units
public abstract class Unit {
	//FIELDS
	protected Sector currentLocation;
	protected Faction side;
	protected String unitName = null;

	//CONSTRUCTORS
	public Unit(Sector currentLocation, Faction faction, String shipname) {
		side = faction;
		unitName = shipname;
		this.currentLocation = currentLocation;
        course = new ArrayDeque<Cood>();
    }

	public int getX() {
		return currentLocation.getX();
	}

	public int getY() {
		return currentLocation.getY();
	}

	public Faction getFaction() {
		return side;
	}

	public String getShipName() {
		return unitName;
	}

    public void setSector(Sector newlocation) {
        currentLocation = newlocation;
    }

	private enum Order {
		MOVE, SCUTTLE, COLONISE, BUILD_STARBASE, ATTACK_SYSTEM
//		,RAID
	}

    private ArrayDeque<Cood> course;

    public void setCourse(int destX, int destY) {

        Log.wtf("Plotting", "...");

        int srcX = getX();
        int srcY = getY();

        while (srcX < destX) {

            if (srcY == destY) {
                if (destX == srcX) {
                    break;
                }
            }
            else if (srcY < destY) {
                srcY++;
            } else {
                srcY--;
            }

            if (srcX != destX) {
                srcX++;
            }
            Log.wtf("Plotting", srcX + "," + srcY);
            course.add(new Cood(srcX, srcY));
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

            Log.wtf("Plotting",  srcX + "," + srcY);
            course.add(new Cood(srcX, srcY));

        }
        Log.wtf("Plotting", "Finished");
    }

    public Cood continueCourse() {
        if (!course.isEmpty()) {
            return course.remove();
        } else {
            return null;
        }
    }

}

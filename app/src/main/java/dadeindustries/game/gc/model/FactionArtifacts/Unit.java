package dadeindustries.game.gc.model.FactionArtifacts;

import java.util.ArrayDeque;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.Enums.Faction;
import dadeindustries.game.gc.model.StellarPhenomenon.Sector;

//all controllable units
public abstract class Unit {
	//FIELDS
	protected Sector currentLocation;
	protected Faction faction;
	protected String unitName = null;
	private ArrayDeque<Coordinates> course;

	//CONSTRUCTORS
	public Unit(Sector currentLocation, Faction faction, String shipname) {
		this.faction = faction;
		unitName = shipname;
		this.currentLocation = currentLocation;
		course = new ArrayDeque<Coordinates>();
	}

	public int getX() {
		return currentLocation.getX();
	}

	public int getY() {
		return currentLocation.getY();
	}

	public Faction getFaction() {
		return faction;
	}

	public String getShipName() {
		return unitName;
	}

	public void setSector(Sector newLocation) {
		currentLocation = newLocation;
	}

	public boolean hasCourse() {
		return !course.isEmpty();
	}

	public Coordinates getNextCoordinatesInCourse() {
		return course.remove();
	}

	public void addToCourse(Coordinates coordinates) {
		course.add(coordinates);
	}

	public void clearCourse() {
		course.clear();
	}

	private enum Order {
		MOVE, SCUTTLE, COLONISE, BUILD_STARBASE, ATTACK_SYSTEM
//		,RAID
	}

}

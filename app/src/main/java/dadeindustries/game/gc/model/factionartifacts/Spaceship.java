package dadeindustries.game.gc.model.factionartifacts;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.List;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.SpacecraftOrder;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

//all controllable units
public abstract class Spaceship implements Spacecraft {
	protected Sector currentLocation;
	protected Faction faction;
	protected String unitName;
	protected List<SpacecraftOrder> validOrders;
	private ArrayDeque<Coordinates> course;
	private int attackLevel;
	private int currentHP;

	public Spaceship(Sector currentLocation, Faction faction, String shipName, int attackLevel, int startingHP) {
		this.faction = faction;
		unitName = shipName;
		this.currentLocation = currentLocation;
		this.attackLevel = attackLevel;
		this.currentHP = startingHP;
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

	public int getAttackLevel() {
		return attackLevel;
	}

	public void damage(int hp) {
		currentHP = currentHP - hp;
	}

	public int getCurrentHP() {
		Log.wtf("Battle", getShipName() + " has " + currentHP + " HP");
		return currentHP;
	}

	public List<SpacecraftOrder> getValidOrders() {
		return validOrders;
	}
}
package dadeindustries.game.gc.model.factionartifacts;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.List;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.SpacecraftOrder;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.Wormhole;

//all controllable units
public abstract class Spaceship implements Spacecraft {
	protected Player player;
	protected Sector currentLocation;
	protected Faction faction;
	protected String unitName;
	protected List<SpacecraftOrder> validOrders;
	private ArrayDeque<Coordinates> course;
	private int attackLevel;
	private int currentHP;
	private boolean enteringWormhole = false;

	public Spaceship(Player player, Sector currentLocation, Faction faction, String shipName, int attackLevel, int startingHP) {
		this.player = player;
		this.faction = faction;
		unitName = shipName;
		this.currentLocation = currentLocation;
		this.attackLevel = attackLevel;
		this.currentHP = startingHP;
		course = new ArrayDeque<Coordinates>();
	}

	public Player getOwner() {
		return player;
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
		Log.d("addToCourse", this.getShipName() + " " + coordinates.toString());
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

	public void enterWormhole() {
		if (currentLocation instanceof Wormhole) {
			enteringWormhole = true;
		}
	}

	public boolean isEnteringWormhole() {
		return enteringWormhole;
	}

	public void setEnteringWormhole(boolean bool) {
		enteringWormhole = enteringWormhole;
	}


}

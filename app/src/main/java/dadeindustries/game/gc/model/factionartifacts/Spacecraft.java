package dadeindustries.game.gc.model.factionartifacts;

import java.util.List;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.SpacecraftOrder;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public interface Spacecraft {
	int getX();

	int getY();

	Player getOwner();

	Faction getFaction();

	String getShipName();

	void setSector(Sector newLocation);

	boolean hasCourse();

	Coordinates getNextCoordinatesInCourse();

	void addToCourse(Coordinates coordinates);

	void clearCourse();

	int getAttackLevel();

	void damage(int hp);

	int getCurrentHP();

	List<SpacecraftOrder> getValidOrders();
}

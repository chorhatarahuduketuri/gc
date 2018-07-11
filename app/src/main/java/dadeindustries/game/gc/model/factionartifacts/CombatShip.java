package dadeindustries.game.gc.model.factionartifacts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dadeindustries.game.gc.model.enums.Faction;
import dadeindustries.game.gc.model.enums.SpacecraftOrder;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class CombatShip extends Spaceship {

	protected List<SpacecraftOrder> validOrders = new LinkedList<SpacecraftOrder>(Arrays.asList(SpacecraftOrder.MOVE, SpacecraftOrder.ATTACK));

	public CombatShip(Sector currentLocation, Faction faction, String shipName, int attackLevel, int startingHP) {
		super(currentLocation, faction, shipName, attackLevel, startingHP);
	}
}

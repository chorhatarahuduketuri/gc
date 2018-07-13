package dadeindustries.game.gc.ai;

import java.util.LinkedList;
import java.util.List;

import dadeindustries.game.gc.ai.concepts.Location;
import dadeindustries.game.gc.model.GlobalGameData;
import dadeindustries.game.gc.model.factionartifacts.ColonyShip;
import dadeindustries.game.gc.model.factionartifacts.CombatShip;
import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;
import dadeindustries.game.gc.model.stellarphenomenon.phenomena.System;

/**
 * A mind is a proxy for a human being with respect to the game, and as such they contain a player
 * which they control and play with in the same way that a human being who is playing the game has a
 * player that they control and play with.
 */
public class Mind {

	private Player player;
	private double combatColonyShipRatio = 0.8;

	public Mind(Player player) {
		this.player = player;
	}

	/**
	 * Computes the AI players moves for this turn.
	 *
	 * @param globalGameData The galaxy
	 * @param systemList     All systems this Minds faction currently controls
	 * @param spaceshipList  All ships this Minds faction currently controls
	 */
	public void computeTurn(GlobalGameData globalGameData, List<System> systemList, List<Spaceship> spaceshipList) {
		buildNewShips(globalGameData, systemList, spaceshipList);
		List<Sector> inhabitedEnemySectors = getInhabitedEnemySectors(globalGameData);
		List<ColonyShip> colonyShips = getColonyShips(spaceshipList);
		if (colonyShips != null) {
			// Search for uninhabited worlds
			List<Sector> uninhabitedSectors = getSectorsWithUninhabitedSystems(globalGameData);
			// If uninhabited worlds exist
			if (uninhabitedSectors != null) {
				for (Sector sector : uninhabitedSectors) {
					// If colony ship there
					for (ColonyShip colonyShip : colonyShips) {
						if (colonyShip.getX() == sector.getX() && colonyShip.getY() == sector.getY()) {
							// Colonise it
							// TODO: order colony ship to colonise
						} else {
							// Move towards it.
							// TODO: set colony ship course toward sector
						}
					}
				}

			} else {
				// Find colony ships without courses
				if (colonyShips != null) {
					// Find inhabited enemy sectors
					for (ColonyShip colonyShip : colonyShips) {
						// Move them towards inhabited enemy world
						// TODO: order colony ship to attack
					}
				}

			}
		}
		// If combat ships exist
		List<CombatShip> combatShips = getCombatShips(spaceshipList);
		// If over enemy world
		for (CombatShip combatShip : combatShips) {
			for (Sector sector : inhabitedEnemySectors) {
				if (combatShip.getX() == sector.getX() && combatShip.getY() == sector.getY()) {
					// Kill enemy world
					// TODO: order ship to attack
				}
			}
		}
		// Locate enemy ships
		List<Spaceship> enemyShips = getEnemyShips(globalGameData);
		// If enemy ships exist
		if (enemyShips != null) {
			// Move towards enemy ships
			for (CombatShip combatShip : combatShips) {
				// TODO: locate nearest enemy ship
				Location nearestEnemyShip = calculateLocationOfNearestEnemyShip(combatShip, enemyShips);
				// TODO: move ship towards it
			}
		} else {
			// Move towards enemy worlds
			// TODO: locate nearest inhabited enemy sector
			// TODO: move ship towards it
		}
	}

	/**
	 * Gets list of all enemy ships in the galaxy.
	 *
	 * @param globalGameData The galaxy
	 * @return List of Spaceships that belong to enemies, null if no enemy ships exist.
	 */
	private List<Spaceship> getEnemyShips(GlobalGameData globalGameData) {
		return null;
	}

	/**
	 * TODO: implement this
	 *
	 * @param globalGameData
	 * @return List of Sectors that are inhabited by the enemy, null otherwise.
	 */
	private List<Sector> getInhabitedEnemySectors(GlobalGameData globalGameData) {
		return null;
	}

	/**
	 * TODO: implement this
	 *
	 * @param globalGameData
	 * @return List of Systems that are uninhabited, null if there are none
	 */
	private List<Sector> getSectorsWithUninhabitedSystems(GlobalGameData globalGameData) {
		return null;
	}

	private List<ColonyShip> getColonyShips(List<Spaceship> spaceshipList) {
		List<ColonyShip> colonyShips = new LinkedList<ColonyShip>();
		for (Spaceship ship : spaceshipList) {
			if (ship instanceof ColonyShip) {
				colonyShips.add((ColonyShip) ship);
			}
		}
		return colonyShips;
	}

	private List<CombatShip> getCombatShips(List<Spaceship> spaceshipList) {
		List<CombatShip> combatShips = new LinkedList<CombatShip>();
		for (Spaceship ship : spaceshipList) {
			if (ship instanceof CombatShip) {
				combatShips.add((CombatShip) ship);
			}
		}
		return combatShips;
	}

	private void buildNewShips(GlobalGameData globalGameData, List<System> systemList, List<Spaceship> spaceshipList) {
		// Determine spaceship ratio
		boolean shouldBuildCombatShips = shouldBuildCombatShips(spaceshipList);
		// If any system has empty build queue
		for (System system : systemList) {
			if (!system.isBuildQueueEmpty()) {
				addOptimalShipToBuildQueue(globalGameData, spaceshipList, shouldBuildCombatShips, system);
			}
		}
	}

	private void addOptimalShipToBuildQueue(GlobalGameData globalGameData, List<Spaceship> spaceshipList, boolean shouldBuildCombatShips, System system) {
		Sector sectorSystemIsIn = globalGameData.getSectors()[system.getX()][system.getY()];
		// Order any systems not already building a spaceship to build one of the most important type
		system.addToQueue(shouldBuildCombatShips
				?
				new CombatShip(sectorSystemIsIn, getPlayer().getFaction(), "AutoShip" + spaceshipList.size() + 1, 2, 4)
				:
				new ColonyShip(sectorSystemIsIn, getPlayer().getFaction(), "AutoCol" + spaceshipList + 1, 0, 4));
	}

	public Player getPlayer() {
		return player;
	}

	private boolean shouldBuildCombatShips(List<Spaceship> spaceshipList) {
		double totalShips = spaceshipList.size();
		double numberOfCombatShips = 0;
		for (Spaceship spaceship : spaceshipList) {
			if (spaceship instanceof CombatShip) {
				numberOfCombatShips++;
			}
		}
		return numberOfCombatShips / totalShips < combatColonyShipRatio;
	}

	private int calculateShortestDistanceBetweenTwoSectors(Sector a, Sector b) {
		int xDiff = Math.abs(a.getX() - b.getX());
		int yDiff = Math.abs(a.getY() - b.getY());
		return (xDiff < yDiff ? xDiff : yDiff) - Math.abs(xDiff - yDiff);
	}

	private int calculateShortestDistanceBetweenTwoLocations(Location a, Location b) {
		int xDiff = Math.abs(a.getX() - b.getX());
		int yDiff = Math.abs(a.getY() - b.getY());
		return (xDiff < yDiff ? xDiff : yDiff) - Math.abs(xDiff - yDiff);
	}

	private Location calculateLocationOfNearestEnemyShip(Spaceship spaceship, List<Spaceship> enemyShips){
		Location nearestLocation = new Location(spaceship.getX(), spaceship.getY());
		Location spaceshipLocation = new Location(spaceship.getX(), spaceship.getY());
		int distanceToNearestEnemyShip = 100000;
		for (Spaceship enemyShip : enemyShips) {
			Location enemyShipLocation = new Location(enemyShip.getX(), enemyShip.getY());
			int distanceToThisEnemyShip = calculateShortestDistanceBetweenTwoLocations(spaceshipLocation, enemyShipLocation);
			if (distanceToThisEnemyShip <distanceToNearestEnemyShip){
				nearestLocation = enemyShipLocation;
				distanceToNearestEnemyShip = distanceToThisEnemyShip;
			}
		}
		return nearestLocation;
	}
}

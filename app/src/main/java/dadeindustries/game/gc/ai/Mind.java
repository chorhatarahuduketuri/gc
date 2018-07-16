package dadeindustries.game.gc.ai;

import java.util.LinkedList;
import java.util.List;

import dadeindustries.game.gc.ai.concepts.Location;
import dadeindustries.game.gc.mechanics.units.UnitActions;
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

	private final int veryLongDistance = 100000;
	private Player player;
	private double combatColonyShipRatio = 0.8;
	private int combatShipCounter = 0;
	private int colonyShipCounter = 0;

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
		computeAndGiveColonyShipOrders(globalGameData, spaceshipList, inhabitedEnemySectors);
		computeAndGiveCombatShipOrders(globalGameData, spaceshipList, inhabitedEnemySectors);
	}

	/**
	 * This method will give order to all of the AIs combat ships with the following strategy:
	 * <ul>
	 * <li>If in an inhabited enemy system, kill it</li>
	 * <li>If not, then go to the nearest enemy ship to engage it in battle</li>
	 * <li>If there aren't any enemy ships, go to the nearest enemy world</li>
	 * </ul>
	 *
	 * @param globalGameData        Global game state to look at and make decisions about
	 * @param spaceshipList         List of it's own spaceships that it's going to give orders to
	 * @param inhabitedEnemySectors List of enemy controlled sectors that this AI will kill as a second objective
	 */
	private void computeAndGiveCombatShipOrders(GlobalGameData globalGameData, List<Spaceship> spaceshipList, List<Sector> inhabitedEnemySectors) {
		// If combat ships exist
		List<CombatShip> combatShips = getCombatShips(spaceshipList);
		// Locate enemy ships
		List<Spaceship> enemyShips = getEnemyShips(globalGameData);
		for (CombatShip combatShip : combatShips) {
			// If over enemy world, kill it
			if (isShipInInhabitedEnemySector(combatShip, inhabitedEnemySectors)) {
				UnitActions.attackSystem(combatShip, globalGameData);
			} else if (!enemyShips.isEmpty()) {
				// If there are enemy ships, find and kill them
				// locate nearest enemy ship
				Location nearestEnemyShip = calculateLocationOfNearestEnemyShip(combatShip, enemyShips);
				// Set course towards it
				UnitActions.setCourse(combatShip, nearestEnemyShip.getX(), nearestEnemyShip.getY());
			} else {
				// If there are inhabited enemy worlds, go there
				// Locate nearest inhabited enemy sector
				Location nearestInhabitedEnemySector = calculateLocationOfNearestSectorFromListOfSectors(new Location(combatShip.getX(), combatShip.getY()), inhabitedEnemySectors);
				// Set course towards it
				UnitActions.setCourse(combatShip, nearestInhabitedEnemySector.getX(), nearestInhabitedEnemySector.getY());
			}
		}
	}

	/**
	 * This method will give orders to all of the AIs colony ships with the following strategy:
	 * <ul>
	 * <li>If over an uninhabited system, colonise it</li>
	 * <li>If not, move to the nearest uninhabited system to colonise it</li>
	 * <li>If there are no uninhabited systems, and the colony ship is over an inhabited enemy system, kill it</li>
	 * <li>If there are no uninhabited systems, and the colony ship isn't over an inhabited enemy system, then move towards the nearest inhabited enemy system. </li>
	 * </ul>
	 *
	 * @param globalGameData        Global game state to look at and make decisions about
	 * @param spaceshipList         List of it's own spaceships that it's going to give orders to
	 * @param inhabitedEnemySectors List of enemy controlled sectors that this AI will kill and colonise as a second objective
	 */
	private void computeAndGiveColonyShipOrders(GlobalGameData globalGameData, List<Spaceship> spaceshipList, List<Sector> inhabitedEnemySectors) {
		List<ColonyShip> colonyShips = getColonyShips(spaceshipList);
		if (!colonyShips.isEmpty()) {
			// Search for uninhabited worlds
			List<Sector> uninhabitedSectors = getSectorsWithUninhabitedSystems(globalGameData);
			for (ColonyShip colonyShip : colonyShips) {
				if (!uninhabitedSectors.isEmpty()) {
					// If uninhabited worlds exist, go colonise them
					Location locationOfNearestUninhabitedSector = calculateLocationOfNearestSectorFromListOfSectors(new Location(colonyShip.getX(), colonyShip.getY()), uninhabitedSectors);
					if (colonyShip.getX() == locationOfNearestUninhabitedSector.getX() && colonyShip.getY() == locationOfNearestUninhabitedSector.getY()) {
						UnitActions.coloniseSystem(colonyShip, globalGameData);
					} else {
						UnitActions.setCourse(colonyShip, locationOfNearestUninhabitedSector.getX(), locationOfNearestUninhabitedSector.getY());
					}
				} else {
					// If there are no uninhabited worlds, go kill some
					Location colonyShipLocation = new Location(colonyShip.getX(), colonyShip.getY());
					// Find nearest inhabited enemy sector
					Location nearestInhabitedEnemyWorld = calculateLocationOfNearestSectorFromListOfSectors(colonyShipLocation, inhabitedEnemySectors);
					if (nearestInhabitedEnemyWorld.equals(colonyShipLocation)) {
						// Order colony ship to attack
						UnitActions.attackSystem(colonyShip, globalGameData);
					} else {
						// Move them towards inhabited enemy world
						UnitActions.setCourse(colonyShip, nearestInhabitedEnemyWorld.getX(), nearestInhabitedEnemyWorld.getY());
					}
				}
			}
		}
	}

	private boolean isShipInInhabitedEnemySector(CombatShip combatShip, List<Sector> inhabitedEnemySectors) {
		for (Sector sector : inhabitedEnemySectors) {
			if (combatShip.getX() == sector.getX() && combatShip.getY() == sector.getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets list of all enemy ships in the galaxy.
	 *
	 * @param globalGameData The galaxy
	 * @return List of Spaceships that belong to enemies
	 */
	private List<Spaceship> getEnemyShips(GlobalGameData globalGameData) {
		List<Spaceship> enemyShips = new LinkedList<Spaceship>();
		Sector[][] galaxy = globalGameData.getSectors();
		for (int x = 0; x < galaxy.length; x++) {
			for (int y = 0; y < galaxy[0].length; y++) {
				if (galaxy[x][y].hasShips()) {
					for (Spaceship spaceshipInSector :
							galaxy[x][y].getUnits()) {
						if (spaceshipInSector.getFaction() != getPlayer().getFaction()) {
							enemyShips.add(spaceshipInSector);
						}
					}
				}
			}
		}
		return enemyShips;
	}

	/**
	 * Returns a list of all sectors in the galaxy that have systems that are inhabited by enemies.
	 *
	 * @param globalGameData
	 * @return List of Sectors that are inhabited by the enemy
	 */
	private List<Sector> getInhabitedEnemySectors(GlobalGameData globalGameData) {
		List<Sector> sectorsWithInhabitedEnemySystemsIn = new LinkedList<Sector>();
		for (Sector sector : getSectorsWithSystemsIn(globalGameData)) {
			if (sector.getSystem().hasFaction() && sector.getSystem().getFaction() != getPlayer().getFaction()) {
				sectorsWithInhabitedEnemySystemsIn.add(sector);
			}
		}
		return sectorsWithInhabitedEnemySystemsIn;
	}

	/**
	 * Returns a list of all sectors in the galaxy that have systems that are uninhabited.
	 *
	 * @param globalGameData
	 * @return List of Systems that are uninhabited
	 */
	private List<Sector> getSectorsWithUninhabitedSystems(GlobalGameData globalGameData) {
		List<Sector> sectorsWithUninhabitedSystemsIn = new LinkedList<Sector>();
		for (Sector sector : getSectorsWithSystemsIn(globalGameData)) {
			if (!sector.getSystem().hasFaction()) {
				sectorsWithUninhabitedSystemsIn.add(sector);
			}
		}
		return sectorsWithUninhabitedSystemsIn;
	}

	private List<Sector> getSectorsWithSystemsIn(GlobalGameData globalGameData) {
		List<Sector> sectorsWithSystemsIn = new LinkedList<Sector>();
		Sector[][] galaxy = globalGameData.getSectors();
		for (Sector[] sectors : galaxy) {
			for (Sector sector : sectors) {
				if (sector.hasSystem()) {
					sectorsWithSystemsIn.add(sector);
				}
			}
		}
		return sectorsWithSystemsIn;
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
		boolean shouldBuildCombatShips = shouldIBuildCombatShips(spaceshipList);
		// If any system has empty build queue
		for (System system : systemList) {
			if (system.isBuildQueueEmpty()) {
				addOptimalShipToBuildQueue(globalGameData, shouldBuildCombatShips, system);
			}
		}
	}

	private void addOptimalShipToBuildQueue(GlobalGameData globalGameData, boolean shouldBuildCombatShips, System system) {
		Sector sectorSystemIsIn = globalGameData.getSectors()[system.getX()][system.getY()];
		// Order any systems not already building a spaceship to build one of the most important type
		system.addToQueue(shouldBuildCombatShips
				?
				new CombatShip(sectorSystemIsIn, getPlayer().getFaction(), getNextShipName(CombatShip.class), 2, 4)
				:
				new ColonyShip(sectorSystemIsIn, getPlayer().getFaction(), getNextShipName(ColonyShip.class), 0, 4));
	}

	private boolean shouldIBuildCombatShips(List<Spaceship> spaceshipList) {
		double totalShips = spaceshipList.size();
		double numberOfCombatShips = 0;
		for (Spaceship spaceship : spaceshipList) {
			if (spaceship instanceof CombatShip) {
				numberOfCombatShips++;
			}
		}
		return numberOfCombatShips / totalShips < combatColonyShipRatio;
	}

	private int calculateShortestDistanceBetweenTwoLocations(Location a, Location b) {
		int xDiff = Math.abs(a.getX() - b.getX());
		int yDiff = Math.abs(a.getY() - b.getY());
		return Math.abs((xDiff < yDiff ? xDiff : yDiff) + Math.abs(xDiff - yDiff));
	}

	private Location calculateLocationOfNearestEnemyShip(Spaceship spaceship, List<Spaceship> enemyShips) {
		Location nearestLocation = new Location(spaceship.getX(), spaceship.getY());
		Location spaceshipLocation = new Location(spaceship.getX(), spaceship.getY());
		int distanceToNearestEnemyShip = veryLongDistance;
		for (Spaceship enemyShip : enemyShips) {
			Location enemyShipLocation = new Location(enemyShip.getX(), enemyShip.getY());
			int distanceToThisEnemyShip = calculateShortestDistanceBetweenTwoLocations(spaceshipLocation, enemyShipLocation);
			if (distanceToThisEnemyShip < distanceToNearestEnemyShip) {
				nearestLocation = enemyShipLocation;
				distanceToNearestEnemyShip = distanceToThisEnemyShip;
			}
		}
		return nearestLocation;
	}

	private Location calculateLocationOfNearestSectorFromListOfSectors(Location startLocation, List<Sector> sectors) {
		Location nearestLocation = startLocation;
		int distanceToNearestSector = veryLongDistance;
		for (Sector sector : sectors) {
			Location sectorLocation = new Location(sector.getX(), sector.getY());
			int distanceToSector = calculateShortestDistanceBetweenTwoLocations(startLocation, sectorLocation);
			if (distanceToSector < distanceToNearestSector) {
				nearestLocation = sectorLocation;
				distanceToNearestSector = distanceToSector;
			}
		}
		return nearestLocation;
	}

	private String getNextShipName(Class clazz) {
		if (clazz.equals(CombatShip.class)) {
			return "MCombat" + combatShipCounter++;
		} else {
			return "MColony" + colonyShipCounter++;
		}
	}

	public Player getPlayer() {
		return player;
	}
}
package dadeindustries.game.gc.model.stellarphenomenon.phenomena;

import java.util.ArrayList;

import dadeindustries.game.gc.model.factionartifacts.Spaceship;
import dadeindustries.game.gc.model.players.Player;
import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class System {

	private String name;
	private int x, y;
	private Player player;
	private ArrayList<QueueItem> buildQueue = new ArrayList<QueueItem>();

	private System(String name, int x, int y, Player player) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.player = player;
	}

	public static boolean createNewSystem(String name, int x, int y, Player owner, Sector[][] sectors) {
		if (!sectors[x][y].hasSystem()) {
			sectors[x][y].setSystem(new System(name, x, y, owner));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks the head of the build queue for completion.
	 * This method should be called on each turn.
	 *
	 * @return Returns ship when it has been completed
	 */
	public Spaceship getBuildQueueHead() {
		if (buildQueue.size() > 0) {
			if (buildQueue.get(0).countdown > 0) {
				buildQueue.get(0).decrementCountdown();
			} else {
				return buildQueue.remove(0).ship;
			}
		}
		return null;
	}

	public void addToQueue(Spaceship ship) {
		buildQueue.add(new QueueItem(3, ship));
	}

	public boolean isBuildQueueEmpty() {
		return buildQueue.isEmpty();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	public boolean hasOwner() {
		return player != null;
	}

	public Player getOwner() {
		return player;
	}

	public void setOwner(Player player) {
		this.player = player;
	}

	class QueueItem {

		Spaceship ship;
		int countdown;

		public QueueItem(int countdown, Spaceship ship) {
			this.countdown = countdown;
			this.ship = ship;
		}

		public void decrementCountdown() {
			if (countdown > 0) {
				countdown = countdown - 1;
			}
		}
	}
}

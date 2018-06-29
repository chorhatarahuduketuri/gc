package dadeindustries.game.gc.ai;

import dadeindustries.game.gc.model.Coordinates;
import dadeindustries.game.gc.model.FactionArtifacts.Unit;
import dadeindustries.game.gc.model.Players.Player;

/**
 * A mind is a proxy for a human being with respect to the game, and as such they contain a player
 * which they control and play with in the same way that a human being who is playing the game has a
 * player that they control and play with.
 */
public class Mind {

	Player player;

	public Mind(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void giveOrder(Unit u) {
		if (!u.hasCourse() && (u.getX() != 3 || u.getY() != 3)) {
			int destX = u.getX();
			int destY = u.getY();
			while (destX != 3 || destY != 3) {
				if (destX != 3) {
					if (destX > 3) {
						destX--;
					} else {
						destX++;
					}
				}
				if (destY != 3) {
					if (destY > 3) {
						destY--;
					} else {
						destY++;
					}
				}
				u.addToCourse(new Coordinates(destX, destY));
			}
		} else if (u.getX() == 3 && u.getY() == 3) {
			u.clearCourse();
			u.addToCourse(new Coordinates(2, 3));
			u.addToCourse(new Coordinates(2, 2));
			u.addToCourse(new Coordinates(3, 2));
			u.addToCourse(new Coordinates(4, 2));
			u.addToCourse(new Coordinates(4, 3));
			u.addToCourse(new Coordinates(4, 4));
			u.addToCourse(new Coordinates(3, 4));
			u.addToCourse(new Coordinates(2, 4));
			u.addToCourse(new Coordinates(2, 3));
			u.addToCourse(new Coordinates(3, 3));
		}
	}
}

package dadeindustries.game.gc.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * whereupon the overall definition of the universe is held.
 * races, galaxy size,
 *
 * @author krongoth, adrianlshaw
 */
public class GlobalGameData {

	public static int galaxySizeX = 50;
	public static int galaxySizeY = 50;
	// Game data structures REFERENCE
	protected ArrayList<Ship> ships;
	protected Collection<Sector> sectors;

	public GlobalGameData(int x, int y) {
		galaxySizeX = x;
		galaxySizeY = y;
	}
	public enum Faction {UNITED_PLANETS, INIATS, GHZRGORZ, MORPHERS}
}

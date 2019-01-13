package dadeindustries.game.gc.model.stellarphenomenon.phenomena;

import dadeindustries.game.gc.model.stellarphenomenon.Sector;

public class Wormhole extends Sector {

	private Sector connectedTo;

	public Wormhole(int x, int y, Sector connectedTo) {
		super(x, y);
		this.connectedTo = connectedTo;
	}

	public Sector getConnectedTo() {
		return connectedTo;
	}
}

package dadeindustries.game.gc.model;

public class Planet {

	protected String name;

	private boolean colonisable = false;
	private int initPopulation = 0;
	private int maxPopulation = 300;

	private int growthRatePerTurn = 1;

	public Planet(String name, boolean canColonise) {
		this.name = name;
		colonisable = canColonise;
	}
}

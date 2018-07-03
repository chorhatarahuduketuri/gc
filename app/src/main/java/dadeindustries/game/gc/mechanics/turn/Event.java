package dadeindustries.game.gc.mechanics.turn;

import dadeindustries.game.gc.model.Coordinates;

public class Event {
	public enum EventType {
		BATTLE, RANDOM_EVENT
	}

	private EventType type;
	private String description;
	private Coordinates coordinates;

	public Event(EventType type, String description, Coordinates coods) {
		this.description = description;
		this.type = type;
		this.coordinates = coods;
	}

	public EventType getEventType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}
}

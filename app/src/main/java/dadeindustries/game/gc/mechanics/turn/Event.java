package dadeindustries.game.gc.mechanics.turn;

import dadeindustries.game.gc.model.Coordinates;

public class Event {
	private EventType type;
	private String description;
	private Coordinates coordinates;

	public Event(EventType type, String description, Coordinates coordinates) {
		this.description = description;
		this.type = type;
		this.coordinates = coordinates;
	}

	public EventType getEventType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public void appendDescription(String string) {
		description = description + "\n* " + string;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public enum EventType {
		BATTLE, RANDOM_EVENT
	}
}

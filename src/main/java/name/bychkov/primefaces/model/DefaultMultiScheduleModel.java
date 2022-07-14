package name.bychkov.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.primefaces.model.ScheduleEvent;

public class DefaultMultiScheduleModel implements MultiScheduleModel, Serializable {
	private static final long serialVersionUID = -7164411821077003613L;

	private Map<Object, List<ScheduleEvent<?>>> events;
	private boolean eventLimit = false;
	
	public DefaultMultiScheduleModel() {
		events = new HashMap<>();
	}
	
	public DefaultMultiScheduleModel(Map<Object, List<ScheduleEvent<?>>> events) {
		this.events = events;
	}
	
	@Override
	public Set<Object> getKeys() {
		return events.keySet();
	}
	
	@Override
	public void addEvent(Object key, ScheduleEvent<?> event) {
		event.setId(UUID.randomUUID().toString());
		var lEvents = events.get(key);
		if (lEvents == null) {
			lEvents = events.put(key, new ArrayList<ScheduleEvent<?>>());
		}
		events.get(key).add(event);
	}

	@Override
	public boolean deleteEvent(ScheduleEvent<?> event) {
		var result = false;
		for (List<ScheduleEvent<?>> lEvents : events.values()) {
			result = result | lEvents.remove(event);
		}
		return result;
	}
	
	@Override
	public boolean deleteEvent(Object key, ScheduleEvent<?> event) {
		return events.get(key).remove(event);
	}

	@Override
	public List<ScheduleEvent<?>> getEvents() {
		var result = new ArrayList<ScheduleEvent<?>>();
		for (var lEvents : events.values()) {
			result.addAll(lEvents);
		}
		return result;
	}
	
	@Override
	public List<ScheduleEvent<?>> getEvents(Object key) {
		return events.get(key);
	}

	@Override
	public ScheduleEvent<?> getEvent(String id) {
		for (var lEvents : events.values()) {
			for (var event : lEvents) {
				if (event.getId().equals(id)) {
					return event;
				}
			}
		}

		return null;
	}
	
	@Override
	public ScheduleEvent<?> getEvent(Object key, String id) {
		for (ScheduleEvent<?> event : events.get(key)) {
			if (event.getId().equals(id)) {
				return event;
			}
		}
		
		return null;
	}

	@Override
	public void updateEvent(ScheduleEvent<?> event) {
		Object key = null;
		var index = -1;

		outer: for (var entry : events.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				if (entry.getValue() .get(i).getId().equals(event.getId())) {
					key = entry.getKey();
					index = i;

					break outer;
				}
			}
		}

		if (key != null && index >= 0) {
			events.get(key).set(index, event);
		}
	}
	
	@Override
	public void updateEvent(Object key, ScheduleEvent<?> event) {
		int index = -1;

		var lEvents = events.get(key);
		for (int i = 0; i < lEvents.size(); i++) {
			if (lEvents.get(i).getId().equals(event.getId())) {
				index = i;

				break;
			}
		}

		if (index >= 0) {
			lEvents.set(index, event);
		}
	}

	@Override
	public int getEventCount() {
		var count = 0;
		for (var lEvents : events.values()) {
			count += lEvents.size();
		}
		return count;
	}
	
	@Override
	public int getEventCount(Object key) {
		return events.get(key).size();
	}

	@Override
	public void clear() {
		events = new HashMap<>();
	}
	
	@Override
	public void clear(Object key) {
		events.put(key, new ArrayList<>());
	}

	@Override
	public boolean isEventLimit() {
		return eventLimit;
	}

	public void setEventLimit(boolean eventLimit) {
		this.eventLimit = eventLimit;
	}
}
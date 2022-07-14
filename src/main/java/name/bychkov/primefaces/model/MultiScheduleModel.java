package name.bychkov.primefaces.model;

import java.util.List;
import java.util.Set;

import org.primefaces.model.ScheduleEvent;

public interface MultiScheduleModel {
	Set<Object> getKeys();
	void addEvent(Object key, ScheduleEvent<?> event);

	boolean deleteEvent(ScheduleEvent<?> event);
	boolean deleteEvent(Object key, ScheduleEvent<?> event);

	List<ScheduleEvent<?>> getEvents();
	List<ScheduleEvent<?>> getEvents(Object key);

	ScheduleEvent<?> getEvent(String id);
	ScheduleEvent<?> getEvent(Object key, String id);

	void updateEvent(ScheduleEvent<?> event);
	void updateEvent(Object key, ScheduleEvent<?> event);

	int getEventCount();
	int getEventCount(Object key);

	void clear();
	void clear(Object key);

	boolean isEventLimit();
}
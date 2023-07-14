package name.bychkov.primefaces.component.multischedule;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.el.ELException;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import org.primefaces.component.schedule.Schedule;
import org.primefaces.component.schedule.ScheduleRenderer;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import name.bychkov.primefaces.model.MultiScheduleModel;

@FacesRenderer(componentFamily = MultiSchedule.COMPONENT_FAMILY, rendererType = MultiSchedule.DEFAULT_RENDERER)
public class MultiScheduleRenderer extends ScheduleRenderer {
	private static final Logger LOGGER = Logger.getLogger(MultiScheduleRenderer.class.getName());

	private String encodeKeys(FacesContext context, MultiSchedule multiSchedule) throws IOException {
		MultiScheduleModel model = (MultiScheduleModel) multiSchedule.getValue();
		String resourceGroupField = multiSchedule.getResourceGroupField();
		StringBuilder jsonResources = new StringBuilder();
		if (model != null) {
			SortedSet<Object> keys = new TreeSet<>(model.getKeys());
			for (Object key : keys) {
				if (jsonResources.length() != 0) {
					jsonResources.append(",");
				}
				Object resourceGroupFieldValue = null;
				if (resourceGroupField != null) {
					try {
						Field field = key.getClass().getDeclaredField(resourceGroupField);
						field.setAccessible(true);
						resourceGroupFieldValue = field.get(key);
					}
					catch (NoSuchFieldException | SecurityException e) {
						LOGGER.warning(() -> "Error has acquired while getting field " + resourceGroupField + " of class " + key.getClass().getName());
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						LOGGER.warning(() -> "Error has acquired while getting field value " + resourceGroupField + " of object " + key.toString());
					}
				}
				jsonResources.append("{id: '").append(key.toString()).append("', title: '").append(key.toString())
					.append(resourceGroupFieldValue != null ? "', " + resourceGroupField + ": '" : "").append(resourceGroupFieldValue != null ? resourceGroupFieldValue.toString() : "")
					.append("'}");
			}
		}
		
		return new StringBuilder("[").append(jsonResources).append("]").toString();
	}
	
	@Override
	protected void encodeEventsAsJSON(FacesContext context, Schedule schedule, ScheduleModel model) throws IOException {
		MultiSchedule multiSchedule = (MultiSchedule) schedule;
		MultiScheduleModel multiModel = (MultiScheduleModel) model;
		ZoneId zoneId = CalendarUtils.calculateZoneId(multiSchedule.getTimeZone());

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

		JSONArray jsonEvents = new JSONArray();

		if (multiModel != null) {
			Set<Object> resources = multiModel.getKeys();
			for (Object resource : resources) {
				List<ScheduleEvent<?>> events = multiModel.getEvents(resource);
				for (ScheduleEvent<?> event : events) {
					JSONObject jsonObject = new JSONObject();
	
					jsonObject.put("id", event.getId());
					if (LangUtils.isNotBlank(event.getGroupId())) {
						jsonObject.put("groupId", event.getGroupId());
					}
					jsonObject.put("title", event.getTitle());
					jsonObject.put("start", dateTimeFormatter.format(event.getStartDate().atZone(zoneId)));
					if (event.getEndDate() != null) {
						jsonObject.put("end", dateTimeFormatter.format(event.getEndDate().atZone(zoneId)));
					}
					jsonObject.put("allDay", event.isAllDay());
					if (event.isDraggable() != null) {
						jsonObject.put("startEditable", event.isDraggable());
					}
					if (event.isResizable() != null) {
						jsonObject.put("durationEditable", event.isResizable());
					}
					jsonObject.put("overlap", event.isOverlapAllowed());
					if (event.getStyleClass() != null) {
						jsonObject.put("classNames", event.getStyleClass());
					}
					if (event.getDescription() != null) {
						jsonObject.put("description", event.getDescription());
					}
					if (event.getUrl() != null) {
						jsonObject.put("url", event.getUrl());
					}
					if (event.getDisplay() != null) {
						jsonObject.put("display", Objects.toString(event.getDisplay(), null));
					}
					if (event.getBackgroundColor() != null) {
						jsonObject.put("backgroundColor", event.getBackgroundColor());
					}
					if (event.getBorderColor() != null) {
						jsonObject.put("borderColor", event.getBorderColor());
					}
					if (event.getTextColor() != null) {
						jsonObject.put("textColor", event.getTextColor());
					}

					jsonObject.put("resourceId", resource);
	
					if (event.getDynamicProperties() != null) {
						for (Map.Entry<String, Object> dynaProperty : event.getDynamicProperties().entrySet()) {
							String key = dynaProperty.getKey();
							Object value = dynaProperty.getValue();
							if (value instanceof LocalDateTime) {
								value = ((LocalDateTime) value).format(dateTimeFormatter);
							}
							jsonObject.put(key, value);
						}
					}
	
					jsonEvents.put(jsonObject);
				}
			}
		}

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("events", jsonEvents);

		ResponseWriter writer = context.getResponseWriter();
		writer.write(jsonResponse.toString());
	}

	private String getLicenseKey(FacesContext context) {
		String licenseKey = context.getExternalContext().getInitParameter(MultiSchedule.LICENSE_KEY);
		try {
			if (licenseKey != null) {
				licenseKey = context.getApplication().evaluateExpressionGet(context, licenseKey, String.class);
			}
		} catch (ELException e) {
			LOGGER.log(Level.FINE, e, () -> "Try to process context parameter " + MultiSchedule.LICENSE_KEY + " as EL-expression");
		}
		return licenseKey;
	}

	@Override
	protected void encodeScript(FacesContext context, Schedule schedule) throws IOException {
		MultiSchedule multiSchedule = (MultiSchedule) schedule;
		WidgetBuilder wb = getWidgetBuilder(context);
		
		wb.init("MultiSchedule", multiSchedule)
				.attr("defaultView", translateViewName(multiSchedule.getView().trim()))
				.attr("locale", multiSchedule.calculateLocale(context).toString().toLowerCase().replace("_", "-")) //adjust locale to FullCalendar-locale
				.attr("tooltip", multiSchedule.isTooltip(), false)
				.attr("eventLimit", multiSchedule.getValue().isEventLimit(), false)
				//timeGrid offers an additional eventLimit - integer value; see https://fullcalendar.io/docs/eventLimit; not exposed yet by PF-schedule
				.attr("lazyFetching", false);

		String licenseKey = getLicenseKey(context);
		if (licenseKey != null) {
			wb.attr("schedulerLicenseKey", licenseKey);
		} else {
			throw new FacesException("Cannot find license key for multischedule, use " + MultiSchedule.LICENSE_KEY + " context-param to define one");
		}
		wb.attr("datesAboveResources", multiSchedule.isDatesAboveResources(), false);
		if (multiSchedule.getResourceAreaWidth() != null) {
			wb.attr("resourceAreaWidth", multiSchedule.getResourceAreaWidth());
		}
		if (multiSchedule.getResourceLabelText() != null) {
			wb.attr("resourceLabelText", multiSchedule.getResourceLabelText());
		}
		wb.attr("resourcesInitiallyExpanded", multiSchedule.isResourcesInitiallyExpanded());
		if (multiSchedule.getSlotWidth() != null) {
			wb.attr("slotWidth", multiSchedule.getSlotWidth());
		}
		wb.append(",resources:").append(encodeKeys(context, multiSchedule));

		Object initialDate = multiSchedule.getInitialDate();
		if (initialDate != null) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
			wb.attr("defaultDate", ((LocalDate) initialDate).format(dateTimeFormatter), null);

		}

		if (multiSchedule.isShowHeader()) {
			wb.append(",header:{left:'")
					.append(multiSchedule.getLeftHeaderTemplate()).append("'")
					.attr("center", multiSchedule.getCenterHeaderTemplate())
					.attr("right", translateViewNames(multiSchedule.getRightHeaderTemplate()))
					.append("}");
		}
		else {
			wb.attr("header", false);
		}

		boolean isShowWeekNumbers = multiSchedule.isShowWeekNumbers();

		wb.attr("allDaySlot", multiSchedule.isAllDaySlot(), true)
				.attr("slotDuration", multiSchedule.getSlotDuration(), "00:30:00")
				.attr("scrollTime", multiSchedule.getScrollTime(), "06:00:00")
				.attr("timeZone", multiSchedule.getClientTimeZone(), "local")
				.attr("minTime", multiSchedule.getMinTime(), null)
				.attr("maxTime", multiSchedule.getMaxTime(), null)
				.attr("aspectRatio", multiSchedule.getAspectRatio(), Double.MIN_VALUE)
				.attr("weekends", multiSchedule.isShowWeekends(), true)
				.attr("eventStartEditable", multiSchedule.isDraggable(), true)
				.attr("eventDurationEditable", multiSchedule.isResizable(), true)
				.attr("slotLabelInterval", multiSchedule.getSlotLabelInterval(), null)
				.attr("eventTimeFormat", multiSchedule.getTimeFormat(), null) //https://momentjs.com/docs/#/displaying/
				.attr("weekNumbers", isShowWeekNumbers, false)
				.attr("nextDayThreshold", multiSchedule.getNextDayThreshold(), "09:00:00")
				.attr("slotEventOverlap", multiSchedule.isSlotEventOverlap(), true)
				.attr("urlTarget", multiSchedule.getUrlTarget(), "_blank")
				.attr("noOpener", multiSchedule.isNoOpener(), true);

		String columnFormat = multiSchedule.getColumnHeaderFormat() != null ? multiSchedule.getColumnHeaderFormat() : multiSchedule.getColumnFormat();
		if (columnFormat != null) {
			wb.append(",columnFormatOptions:{" + columnFormat + "}");
		}

		String displayEventEnd = multiSchedule.getDisplayEventEnd();
		if (displayEventEnd != null) {
			if (displayEventEnd.equals("true") || displayEventEnd.equals("false")) {
				wb.nativeAttr("displayEventEnd", displayEventEnd);
			}
			else {
				wb.nativeAttr("displayEventEnd", "{" + displayEventEnd + "}");
			}
		}

		String extender = multiSchedule.getExtender();
		if (extender != null) {
			wb.nativeAttr("extender", extender);
		}

		if (isShowWeekNumbers) {
			String weekNumCalculation = multiSchedule.getWeekNumberCalculation();
			String weekNumCalculator = multiSchedule.getWeekNumberCalculator();

			if (weekNumCalculation.equals("custom")) {
				if (weekNumCalculator != null) {
					wb.append(",weekNumberCalculation: function(){ return ")
							.append(multiSchedule.getWeekNumberCalculator())
							.append("}");
				}
			}
			else {
				wb.attr("weekNumberCalculation", weekNumCalculation, "local");
			}
		}

		encodeClientBehaviors(context, multiSchedule);

		wb.finish();
	}

	/**
	 * Translates old FullCalendar-ViewName (<=V3) to new FullCalendar-ViewName (>=V4)
	 * @param viewNameOld
	 * @return
	 */
	private String translateViewName(String viewNameOld) {
		switch (viewNameOld) {
			case "month":
				return "dayGridMonth";
			case "basicWeek":
				return "dayGridWeek";
			case "basicDay":
				return "dayGridDay";
			case "agendaWeek":
				return "timeGridWeek";
			case "agendaDay":
				return "timeGridDay";
			default:
				return viewNameOld;
		}
	}

	private String translateViewNames(String viewNamesOld) {
		if (viewNamesOld != null) {
			return Stream.of(viewNamesOld.split(","))
					.map(v -> translateViewName(v.trim()))
					.collect(Collectors.joining(","));
		}

		return null;
	}
}
package name.bychkov.primefaces.component.multischedule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.WidgetBuilder;

import name.bychkov.primefaces.model.MultiScheduleModel;

@FacesRenderer(componentFamily = MultiSchedule.COMPONENT_FAMILY, rendererType = MultiSchedule.DEFAULT_RENDERER)
public class MultiScheduleRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		MultiSchedule multiSchedule = (MultiSchedule) component;
		String clientId = multiSchedule.getClientId(context);
		String viewId = clientId + "_view";
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		if (params.containsKey(viewId)) {
			multiSchedule.setView(params.get(viewId));
		}

		decodeBehaviors(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		MultiSchedule multiSchedule = (MultiSchedule) component;

		if (context.getExternalContext().getRequestParameterMap().containsKey(multiSchedule.getClientId(context))) {
			JSONArray jsonEvents = encodeEvents(context, multiSchedule, multiSchedule.getValue());
			
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("events", jsonEvents);
			
			ResponseWriter writer = context.getResponseWriter();
			writer.write(jsonResponse.toString());
		}
		else {
			encodeMarkup(context, multiSchedule);
			encodeScript(context, multiSchedule);
		}
	}

	protected String encodeKeys(FacesContext context, MultiScheduleModel model) throws IOException {
		StringBuilder jsonResources = new StringBuilder();
		
		if (model != null) {
			SortedSet<Object> keys = new TreeSet<>(model.getKeys());
			for (Object key : keys) {
				if (jsonResources.length() != 0) {
					jsonResources.append(",");
				}
				jsonResources.append("{id: '").append(key.toString()).append("', title: '").append(key.toString()).append("'}");
			}
		}
		
		return new StringBuilder("[").append(jsonResources).append("]").toString();
	}
	
	protected JSONArray encodeEvents(FacesContext context, MultiSchedule multiSchedule, MultiScheduleModel model) throws IOException {
		ZoneId zoneId = CalendarUtils.calculateZoneId(multiSchedule.getTimeZone());

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

		JSONArray jsonEvents = new JSONArray();

		if (model != null) {
			Set<Object> resources = model.getKeys();
			for (Object resource : resources) {
				List<ScheduleEvent<?>> events = model.getEvents(resource);
				for (ScheduleEvent<?> event : events) {
					JSONObject jsonObject = new JSONObject();
	
					jsonObject.put("id", event.getId());
					if (event.getGroupId() != null && event.getGroupId().length() > 0) {
						jsonObject.put("groupId", event.getGroupId());
					}
					jsonObject.put("title", event.getTitle());
					jsonObject.put("start", dateTimeFormatter.format(event.getStartDate().atZone(zoneId)));
					jsonObject.put("end", dateTimeFormatter.format(event.getEndDate().atZone(zoneId)));
					jsonObject.put("allDay", event.isAllDay());
					jsonObject.put("editable", event.isEditable());
					jsonObject.put("overlap", event.isOverlapAllowed());
					jsonObject.put("className", event.getStyleClass());
					jsonObject.put("description", event.getDescription());
					jsonObject.put("url", event.getUrl());
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

		return jsonEvents;
	}

	protected void encodeScript(FacesContext context, MultiSchedule multiSchedule) throws IOException {
		String clientId = multiSchedule.getClientId(context);
		WidgetBuilder wb = getWidgetBuilder(context);
		
		wb.init("MultiSchedule", multiSchedule.resolveWidgetVar(context), clientId)
				.attr("defaultView", translateViewName(multiSchedule.getView().trim()))
				.attr("datesAboveResources", multiSchedule.isDatesAboveResources(), false)
				.attr("locale", multiSchedule.calculateLocale(context).toString().toLowerCase().replace("_", "-")) //adjust locale to FullCalendar-locale
				.attr("tooltip", multiSchedule.isTooltip(), false)
				.attr("eventLimit", multiSchedule.getValue().isEventLimit(), false)
				//timeGrid offers an additional eventLimit - integer value; see https://fullcalendar.io/docs/eventLimit; not exposed yet by PF-schedule
				.attr("lazyFetching", false);

		String licenseKey = context.getExternalContext().getInitParameter(MultiSchedule.LICENSE_KEY);
		if (licenseKey != null) {
			wb.attr("schedulerLicenseKey", licenseKey);
		} else {
			throw new FacesException("Cannot find license key for multischedule, use " + MultiSchedule.LICENSE_KEY + " context-param to define one");
		}
		wb.append(",resources:").append(encodeKeys(context, multiSchedule.getValue()));

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

	protected void encodeMarkup(FacesContext context, MultiSchedule multiSchedule) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = multiSchedule.getClientId(context);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if (multiSchedule.getStyle() != null) {
			writer.writeAttribute("style", multiSchedule.getStyle(), "style");
		}
		if (multiSchedule.getStyleClass() != null) {
			writer.writeAttribute("class", multiSchedule.getStyleClass(), "style");
		}

		encodeStateParam(context, multiSchedule);

		writer.endElement("div");
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	protected void encodeStateParam(FacesContext context, MultiSchedule multiSchedule) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String id = multiSchedule.getClientId(context) + "_view";
		String view = multiSchedule.getView();

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("autocomplete", "off", null);
		if (view != null) {
			writer.writeAttribute("value", view, null);
		}
		writer.endElement("input");
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
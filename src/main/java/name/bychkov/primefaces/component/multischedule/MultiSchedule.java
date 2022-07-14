package name.bychkov.primefaces.component.multischedule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MapBuilder;

import name.bychkov.primefaces.model.MultiScheduleModel;

@ResourceDependencies({
	@ResourceDependency(library = "primefaces", name = "multischedule/multischedule.css"),
	@ResourceDependency(library = "primefaces", name = "components.css"),
	@ResourceDependency(library = "primefaces", name = "core.js"),
	@ResourceDependency(library = "primefaces", name = "components.js"),
	@ResourceDependency(library = "primefaces", name = "multischedule/multischedule.js")
})
public class MultiSchedule extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "name.bychkov.primefaces.component.multischedule.MultiScheduleRenderer";

	public enum PropertyKeys {

		widgetVar,
		value,
		locale,
		aspectRatio,
		view,
		initialDate,
		showWeekends,
		style,
		styleClass,
		draggable,
		resizable,
		showHeader,
		leftHeaderTemplate,
		centerHeaderTemplate,
		rightHeaderTemplate,
		allDaySlot,
		slotDuration,
		scrollTime,
		minTime,
		maxTime,
		slotLabelInterval,
		slotLabelFormat,
		timeFormat,
		columnFormat,
		columnHeaderFormat,
		timeZone,
		clientTimeZone,
		tooltip,
		showWeekNumbers,
		extender,
		displayEventEnd,
		weekNumberCalculation,
		weekNumberCalculator,
		nextDayThreshold,
		slotEventOverlap,
		urlTarget,
		noOpener
	}

	public MultiSchedule() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getWidgetVar() {
		return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}

	public void setWidgetVar(String widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
	}

	public MultiScheduleModel getValue() {
		return (MultiScheduleModel) getStateHelper().eval(PropertyKeys.value, null);
	}

	public void setValue(org.primefaces.model.ScheduleModel value) {
		getStateHelper().put(PropertyKeys.value, value);
	}

	public Object getLocale() {
		return getStateHelper().eval(PropertyKeys.locale, null);
	}

	public void setLocale(Object locale) {
		getStateHelper().put(PropertyKeys.locale, locale);
	}

	public double getAspectRatio() {
		return (Double) getStateHelper().eval(PropertyKeys.aspectRatio, Double.MIN_VALUE);
	}

	public void setAspectRatio(double aspectRatio) {
		getStateHelper().put(PropertyKeys.aspectRatio, aspectRatio);
	}

	public String getView() {
		return (String) getStateHelper().eval(PropertyKeys.view, "resourceTimeGridDay");
	}

	public void setView(String view) {
		getStateHelper().put(PropertyKeys.view, view);
	}

	public Object getInitialDate() {
		return getStateHelper().eval(PropertyKeys.initialDate, null);
	}

	public void setInitialDate(Object initialDate) {
		getStateHelper().put(PropertyKeys.initialDate, initialDate);
	}

	public boolean isShowWeekends() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showWeekends, true);
	}

	public void setShowWeekends(boolean showWeekends) {
		getStateHelper().put(PropertyKeys.showWeekends, showWeekends);
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}

	public void setStyle(String style) {
		getStateHelper().put(PropertyKeys.style, style);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}

	public void setStyleClass(String styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, styleClass);
	}

	public boolean isDraggable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
	}

	public void setDraggable(boolean draggable) {
		getStateHelper().put(PropertyKeys.draggable, draggable);
	}

	public boolean isResizable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
	}

	public void setResizable(boolean resizable) {
		getStateHelper().put(PropertyKeys.resizable, resizable);
	}

	public boolean isShowHeader() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
	}

	public void setShowHeader(boolean showHeader) {
		getStateHelper().put(PropertyKeys.showHeader, showHeader);
	}

	public String getLeftHeaderTemplate() {
		return (String) getStateHelper().eval(PropertyKeys.leftHeaderTemplate, "prev,next today");
	}

	public void setLeftHeaderTemplate(String leftHeaderTemplate) {
		getStateHelper().put(PropertyKeys.leftHeaderTemplate, leftHeaderTemplate);
	}

	public String getCenterHeaderTemplate() {
		return (String) getStateHelper().eval(PropertyKeys.centerHeaderTemplate, "title");
	}

	public void setCenterHeaderTemplate(String centerHeaderTemplate) {
		getStateHelper().put(PropertyKeys.centerHeaderTemplate, centerHeaderTemplate);
	}

	public String getRightHeaderTemplate() {
		return (String) getStateHelper().eval(PropertyKeys.rightHeaderTemplate, "dayGridMonth,timeGridWeek,timeGridDay");
	}

	public void setRightHeaderTemplate(String rightHeaderTemplate) {
		getStateHelper().put(PropertyKeys.rightHeaderTemplate, rightHeaderTemplate);
	}

	public boolean isAllDaySlot() {
		return (Boolean) getStateHelper().eval(PropertyKeys.allDaySlot, true);
	}

	public void setAllDaySlot(boolean allDaySlot) {
		getStateHelper().put(PropertyKeys.allDaySlot, allDaySlot);
	}

	public String getSlotDuration() {
		return (String) getStateHelper().eval(PropertyKeys.slotDuration, "00:30:00");
	}

	public void setSlotDuration(String slotDuration) {
		getStateHelper().put(PropertyKeys.slotDuration, slotDuration);
	}

	public String getScrollTime() {
		return (String) getStateHelper().eval(PropertyKeys.scrollTime, "06:00:00");
	}

	public void setScrollTime(String scrollTime) {
		getStateHelper().put(PropertyKeys.scrollTime, scrollTime);
	}

	public String getMinTime() {
		return (String) getStateHelper().eval(PropertyKeys.minTime, null);
	}

	public void setMinTime(String minTime) {
		getStateHelper().put(PropertyKeys.minTime, minTime);
	}

	public String getMaxTime() {
		return (String) getStateHelper().eval(PropertyKeys.maxTime, null);
	}

	public void setMaxTime(String maxTime) {
		getStateHelper().put(PropertyKeys.maxTime, maxTime);
	}

	public String getSlotLabelInterval() {
		return (String) getStateHelper().eval(PropertyKeys.slotLabelInterval, null);
	}

	public void setSlotLabelInterval(String slotLabelInterval) {
		getStateHelper().put(PropertyKeys.slotLabelFormat, slotLabelInterval);
	}

	public String getSlotLabelFormat() {
		return (String) getStateHelper().eval(PropertyKeys.slotLabelFormat, null);
	}

	public void setSlotLabelFormat(String slotLabelFormat) {
		getStateHelper().put(PropertyKeys.slotLabelFormat, slotLabelFormat);
	}

	public String getTimeFormat() {
		return (String) getStateHelper().eval(PropertyKeys.timeFormat, null);
	}

	public void setTimeFormat(String timeFormat) {
		getStateHelper().put(PropertyKeys.timeFormat, timeFormat);
	}

	public String getColumnFormat() {
		return (String) getStateHelper().eval(PropertyKeys.columnFormat, null);
	}

	public void setColumnFormat(String columnFormat) {
		getStateHelper().put(PropertyKeys.columnFormat, columnFormat);
	}

	public String getColumnHeaderFormat() {
		return (String) getStateHelper().eval(PropertyKeys.columnHeaderFormat, null);
	}

	public void setColumnHeaderFormat(String columnHeaderFormat) {
		getStateHelper().put(PropertyKeys.columnHeaderFormat, columnHeaderFormat);
	}

	public Object getTimeZone() {
		return getStateHelper().eval(PropertyKeys.timeZone, null);
	}

	public void setTimeZone(Object timeZone) {
		getStateHelper().put(PropertyKeys.timeZone, timeZone);
	}

	public String getClientTimeZone() {
		return (String) getStateHelper().eval(PropertyKeys.clientTimeZone, "local");
	}

	public void setClientTimeZone(String clientTimeZone) {
		getStateHelper().put(PropertyKeys.clientTimeZone, clientTimeZone);
	}

	public boolean isTooltip() {
		return (Boolean) getStateHelper().eval(PropertyKeys.tooltip, false);
	}

	public void setTooltip(boolean tooltip) {
		getStateHelper().put(PropertyKeys.tooltip, tooltip);
	}

	public boolean isShowWeekNumbers() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showWeekNumbers, false);
	}

	public void setShowWeekNumbers(boolean showWeekNumbers) {
		getStateHelper().put(PropertyKeys.showWeekNumbers, showWeekNumbers);
	}

	public String getExtender() {
		return (String) getStateHelper().eval(PropertyKeys.extender, null);
	}

	public void setExtender(String extender) {
		getStateHelper().put(PropertyKeys.extender, extender);
	}

	public String getDisplayEventEnd() {
		return (String) getStateHelper().eval(PropertyKeys.displayEventEnd, null);
	}

	public void setDisplayEventEnd(String displayEventEnd) {
		getStateHelper().put(PropertyKeys.displayEventEnd, displayEventEnd);
	}

	public String getWeekNumberCalculation() {
		return (String) getStateHelper().eval(PropertyKeys.weekNumberCalculation, "local");
	}

	public void setWeekNumberCalculation(String weekNumberCalculation) {
		getStateHelper().put(PropertyKeys.weekNumberCalculation, weekNumberCalculation);
	}

	public String getWeekNumberCalculator() {
		return (String) getStateHelper().eval(PropertyKeys.weekNumberCalculator, null);
	}

	public void setWeekNumberCalculator(String weekNumberCalculator) {
		getStateHelper().put(PropertyKeys.weekNumberCalculator, weekNumberCalculator);
	}

	public String getNextDayThreshold() {
		return (String) getStateHelper().eval(PropertyKeys.nextDayThreshold, "09:00:00");
	}

	public void setNextDayThreshold(String nextDayThreshold) {
		getStateHelper().put(PropertyKeys.nextDayThreshold, nextDayThreshold);
	}

	public boolean isSlotEventOverlap() {
		return (Boolean) getStateHelper().eval(PropertyKeys.slotEventOverlap, true);
	}

	public void setSlotEventOverlap(boolean slotEventOverlap) {
		getStateHelper().put(PropertyKeys.slotEventOverlap, slotEventOverlap);
	}

	public String getUrlTarget() {
		return (String) getStateHelper().eval(PropertyKeys.urlTarget, "_blank");
	}

	public void setUrlTarget(String urlTarget) {
		getStateHelper().put(PropertyKeys.urlTarget, urlTarget);
	}

	public boolean isNoOpener() {
		return (Boolean) getStateHelper().eval(PropertyKeys.noOpener, true);
	}

	public void setNoOpener(boolean noOpener) {
		getStateHelper().put(PropertyKeys.noOpener, noOpener);
	}

	public static final String COMPONENT_TYPE = MultiSchedule.class.getName();

	private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
			.put("dateSelect", SelectEvent.class)
			.put("eventSelect", SelectEvent.class)
			.put("eventMove", ScheduleEntryMoveEvent.class)
			.put("eventResize", ScheduleEntryResizeEvent.class)
			.put("viewChange", SelectEvent.class)
			.build();
	private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

	@Override
	public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
		return BEHAVIOR_EVENT_MAPPING;
	}

	@Override
	public Collection<String> getEventNames() {
		return EVENT_NAMES;
	}

	Locale calculateLocale(FacesContext facesContext) {
		return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
	}

	@Override
	public void queueEvent(FacesEvent event) {
		FacesContext context = getFacesContext();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
		String clientId = getClientId(context);
		ZoneId zoneId = CalendarUtils.calculateZoneId(this.getTimeZone());

		if (ComponentUtils.isRequestSource(this, context)) {

			AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
			FacesEvent wrapperEvent = null;

			if (eventName.equals("dateSelect")) {
				String selectedDateStr = params.get(clientId + "_selectedDate");
				LocalDateTime selectedDate =  CalendarUtils.toLocalDateTime(zoneId, selectedDateStr);
				SelectEvent<?> selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedDate);
				selectEvent.setPhaseId(behaviorEvent.getPhaseId());

				wrapperEvent = selectEvent;
			}
			else if (eventName.equals("eventSelect")) {
				String selectedEventId = params.get(clientId + "_selectedEventId");
				ScheduleEvent<?> selectedEvent = getValue().getEvent(selectedEventId);

				wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedEvent);
			}
			else if (eventName.equals("eventMove")) {
				String movedEventId = params.get(clientId + "_movedEventId");
				ScheduleEvent<?> movedEvent = getValue().getEvent(movedEventId);
				int yearDelta = Double.valueOf(params.get(clientId + "_yearDelta")).intValue();
				int monthDelta = Double.valueOf(params.get(clientId + "_monthDelta")).intValue();
				int dayDelta = Double.valueOf(params.get(clientId + "_dayDelta")).intValue();
				int minuteDelta = Double.valueOf(params.get(clientId + "_minuteDelta")).intValue();

				LocalDateTime startDate = movedEvent.getStartDate();
				LocalDateTime endDate = movedEvent.getEndDate();
				startDate = startDate.plusYears(yearDelta).plusMonths(monthDelta).plusDays(dayDelta).plusMinutes(minuteDelta);
				endDate = endDate.plusYears(yearDelta).plusMonths(monthDelta).plusDays(dayDelta).plusMinutes(minuteDelta);
				movedEvent.setStartDate(startDate);
				movedEvent.setEndDate(endDate);

				wrapperEvent = new ScheduleEntryMoveEvent(this, behaviorEvent.getBehavior(), movedEvent,
						yearDelta, monthDelta, dayDelta, minuteDelta);
			}
			else if (eventName.equals("eventResize")) {
				String resizedEventId = params.get(clientId + "_resizedEventId");
				ScheduleEvent<?> resizedEvent = getValue().getEvent(resizedEventId);

				int startDeltaYear = Double.valueOf(params.get(clientId + "_startDeltaYear")).intValue();
				int startDeltaMonth = Double.valueOf(params.get(clientId + "_startDeltaMonth")).intValue();
				int startDeltaDay = Double.valueOf(params.get(clientId + "_startDeltaDay")).intValue();
				int startDeltaMinute = Double.valueOf(params.get(clientId + "_startDeltaMinute")).intValue();

				LocalDateTime startDate = resizedEvent.getStartDate();
				startDate = startDate.plusYears(startDeltaYear).plusMonths(startDeltaMonth).plusDays(startDeltaDay).plusMinutes(startDeltaMinute);
				resizedEvent.setStartDate(startDate);

				int endDeltaYear = Double.valueOf(params.get(clientId + "_endDeltaYear")).intValue();
				int endDeltaMonth = Double.valueOf(params.get(clientId + "_endDeltaMonth")).intValue();
				int endDeltaDay = Double.valueOf(params.get(clientId + "_endDeltaDay")).intValue();
				int endDeltaMinute = Double.valueOf(params.get(clientId + "_endDeltaMinute")).intValue();

				LocalDateTime endDate = resizedEvent.getEndDate();
				endDate = endDate.plusYears(endDeltaYear).plusMonths(endDeltaMonth).plusDays(endDeltaDay).plusMinutes(endDeltaMinute);
				resizedEvent.setEndDate(endDate);

				wrapperEvent = new ScheduleEntryResizeEvent(this, behaviorEvent.getBehavior(), resizedEvent,
						startDeltaYear, startDeltaMonth, startDeltaDay, startDeltaMinute,
						endDeltaYear, endDeltaMonth, endDeltaDay, endDeltaMinute);
			}
			else if (eventName.equals("viewChange")) {
				wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getView());
			}

			if (wrapperEvent == null) {
				throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
			}

			wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

			super.queueEvent(wrapperEvent);
		}
		else {
			super.queueEvent(event);
		}
	}

	@Override
	public void processUpdates(FacesContext context) {
		if (!isRendered()) {
			return;
		}

		super.processUpdates(context);

		ValueExpression expr = getValueExpression(PropertyKeys.view.toString());
		if (expr != null) {
			expr.setValue(getFacesContext().getELContext(), getView());
			getStateHelper().remove(PropertyKeys.view);
		}
	}
}
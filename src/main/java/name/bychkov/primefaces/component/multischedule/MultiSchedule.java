package name.bychkov.primefaces.component.multischedule;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.context.FacesContext;

import java.util.Locale;

import org.primefaces.component.schedule.Schedule;
import org.primefaces.util.LocaleUtils;

@ResourceDependency(library = "primefaces", name = "multischedule/multischedule.css")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "multischedule/multischedule.js")
public class MultiSchedule extends Schedule {

	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String COMPONENT_TYPE = MultiSchedule.class.getName();
	public static final String DEFAULT_RENDERER = "name.bychkov.primefaces.component.multischedule.MultiScheduleRenderer";
	public static final String LICENSE_KEY = "primefaces.SCHEDULE_LICENSE_KEY";

	public enum OwnPropertyKeys {

		datesAboveResources,
		
		resourceGroupField,
		resourceAreaWidth,
		resourceLabelText,
		resourcesInitiallyExpanded,
		slotWidth
	}

	public MultiSchedule() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getView() {
		return (String) getStateHelper().eval(PropertyKeys.view, "resourceTimeGridDay");
	}

	public String getRightHeaderTemplate() {
		return (String) getStateHelper().eval(PropertyKeys.rightHeaderTemplate, "resourceTimeGridDay,resourceTimeGridWeek");
	}

	public boolean isDatesAboveResources() {
		return (Boolean) getStateHelper().eval(OwnPropertyKeys.datesAboveResources, false);
	}

	public void setDatesAboveResources(boolean datesAboveResources) {
		getStateHelper().put(OwnPropertyKeys.datesAboveResources, datesAboveResources);
	}

	public String getResourceGroupField() {
		return (String) getStateHelper().eval(OwnPropertyKeys.resourceGroupField);
	}

	public void setResourceGroupField(String resourceGroupField) {
		getStateHelper().put(OwnPropertyKeys.resourceGroupField, resourceGroupField);
	}

	public String getResourceAreaWidth() {
		return (String) getStateHelper().eval(OwnPropertyKeys.resourceAreaWidth, "30%");
	}

	public void setResourceAreaWidth(String resourceAreaWidth) {
		getStateHelper().put(OwnPropertyKeys.resourceAreaWidth, resourceAreaWidth);
	}

	public String getResourceLabelText() {
		return (String) getStateHelper().eval(OwnPropertyKeys.resourceLabelText, "Resources");
	}

	public void setResourceLabelText(String resourceLabelText) {
		getStateHelper().put(OwnPropertyKeys.resourceLabelText, resourceLabelText);
	}

	public boolean isResourcesInitiallyExpanded() {
		return (Boolean) getStateHelper().eval(OwnPropertyKeys.resourcesInitiallyExpanded, true);
	}

	public void setResourcesInitiallyExpanded(boolean resourcesInitiallyExpanded) {
		getStateHelper().put(OwnPropertyKeys.resourcesInitiallyExpanded, resourcesInitiallyExpanded);
	}

	public Integer getSlotWidth() {
		return (Integer) getStateHelper().eval(OwnPropertyKeys.slotWidth);
	}

	public void setSlotWidth(Integer slotWidth) {
		getStateHelper().put(OwnPropertyKeys.slotWidth, slotWidth);
	}

	Locale calculateLocale(FacesContext facesContext) {
		return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
	}
}
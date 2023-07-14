# primefaces-multischedule
MultiSchedule component for Primefaces 12 Jakarta.

In addition to standard Primefaces Schedule possibilities (Outlook Calendar, iCal), MultiSchedule enables to use Vertical Resource View and Timeline View - Premium possibilities of FullCalendar.

**IMPORTANT**: Need to add [license key](https://fullcalendar.io/license) into `web.xml`:

```xml
<context-param>
	<param-name>primefaces.SCHEDULE_LICENSE_KEY</param-name>
	<param-value>LICENSE KEY HERE</param-value>
</context-param>
```

![Component in Vertical Resource View](/timeline.png)
Vertical Resource View

![Component in Timeline View](/timegrid.png)
Timeline View


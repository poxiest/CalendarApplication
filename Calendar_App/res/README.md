## Program Instructions

There are three modes available for this program. Interactive, headless, and GUI.

### Interactive Mode

Interactive mode allows for inputing commands into the terminal after executing the
jar file.

To run in interactive, navigate to the directory containing the CalendarApp.jar file and run:

`java -jar CalendarApp.jar --mode interactive`

The list of valid commands is then as follows:

`create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString>`

Creates a single event in the calendar.

`create event  <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times`

Creates a recurring event that repeats N times on specific weekdays.

`create event  <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>`

Creates a recurring event until a specific date (inclusive).

`create event  <eventName> on <dateStringTtimeString>`

Creates a single all day event.

`create event <eventName> on <dateString> repeats <weekdays> for <N> times`

Creates a recurring all day event that repeats N times on specific weekdays.

`create event <eventName> on <dateString> repeats <weekdays> until <dateString>`

Creates a recurring all day event until a specific date (inclusive).

`edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>`

Changes the property (e.g., subject) of all events starting at a specific date/time and have the same event name.

`edit events <property> <eventName> <NewPropertyValue>`

Change the property (e.g., name) of all events with the same event name.

`print events on <dateString>`

Prints a bulleted list of all events on that day along with their start and end time and location (if any).

`print events from <dateStringTtimeString> to <dateStringTtimeString>`

Prints a bulleted list of all events in the given interval including their start and end times and location (if any).

`export cal fileName.csv`

Exports the calendar as a csv file that can be imported to Google Calendar app.

`show status on <dateStringTtimeString>`

Prints "Busy" if the user has events scheduled during the provided date time and "Available" otherwise.

`create calendar --name <calName> --timezone area/location`

Creates a new calendar with the specified name and timezone.

`edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>`

Edits a property (name | timezone) of a calendar.

`use calendar --name <name-of-calendar>`

Sets the active calendar.

`copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>`

Copy an event from one calendar to the target calendar, converting events to the target timezone if needed.

`copy events on <dateString> --target <calendarName> to <dateString>`

Copies all events on a given date to the target calendar, converting events to the target timezone if needed.

`copy events between <dateString> and <dateString> --target <calendarName> to <dateString>`

Copies all events in the provided interval to the target calendar where the begin on the provided date, converting events to the target timezone if needed.

`exit`

Exits the program.


### Headless Mode

Headless Mode allows for any of the commands listed above to be processed from a text file. Each command must be on its own line and lines must end with a newline character. To run in headless mode run:

`java -jar CalendarApp.jar --mode headless path-of-script-file`

### GUI

The GUI provides a graphical user interface for users to interact with thier calendars.

To run with the GUI, run:

`java -jar CalendarApp.jar `


the application works for commands that has only lowercase alphabets.
the application does not exit itself gracefully.
error message exposes internals of the code.
even though command design pattern is used, inside the class there are switch cases for parsing, this breaks open close principle and the code grows when the new commands come.
the application is not supporting event names with spaces in both interactive and headless
application does not throws an exception when there is no exit command in the headless mode. and this is not documented in the readme as well.
headless mode continues to execute code even if there is an error
conflict is not working as expected
edit event also not working


controller to view test is not there 
controller to model test is not there 
mockcontroller is not actually mock, it is not working as expected
their full day is not 00:00 to 00:00, but 00:00 to 23.98
test case cannot be written for showcalendar command class as the previous team have not done any kind of mocking for both controller and the model.
no location means it is considered as offline

export works differently in CLI for dates and it works different for GUI
CLI date has / separated 
GUI date has - separated
GUI import expects -

The same set of events when exported from CLI is exported into GUI, says conflict of events. 
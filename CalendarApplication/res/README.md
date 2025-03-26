# Calendar Application

This is a calendar application that allows users to manage events through a text-based interface.
Users can create, edit, and view events in a calendar, as well as export the calendar data to CSV
format
which is supported by Google Calendar. Apart from this users can also manage multiple calendars,
do operations on a specific calendar, copy events from one calendar to other calendar.

## Getting Started

## Running the Application

The application can run in two modes:

1. **Interactive Mode**: Allows users to enter commands directly through the command line.
2. **Headless Mode**: Processes commands from a file without requiring user interaction.

Download the .jar file present in /res directory

### Interactive Mode

To run the application in interactive mode:

```bash
java -jar <file.jar> --mode interactive
```

Once the application starts, you can enter commands directly in the console.
For command reference check res/commands/validCalendarCommands.txt file.

### Headless Mode

To run the application in headless mode compile the java main file similar to interactive and:

```bash
java -jar <file.jar> --mode headless {absolute_path}
```

The command files are present inside res/commands/

## Supported Commands

The application supports the following commands:

### Create a new Calendar

```
create calendar --name <calendarName> --timezone area/location
```

Create a calendar with the name and timezone specified.

### Edit a calendar

```
edit calendar --name <calendarName> --property <propertyName> <propertyValue>
```

Changes the property value of the given calendar.
Property can take the following values:

1. name - Case Sensitive
2. timezone - Follows IANA TimeZone Database Format

### Use a specific Calendar

```
use calendar --name <calendarName>
```

Set a specific calendar as the active calendar. The operations done post this will be done on the
active calendar.

### Creating Events

```
create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
```

Creates a single event in the calendar. The event will be rejected in case of a conflict with
exiting event.

### Creating Recurring Events

```
create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
```

Creates a recurring event that repeats N times on specific weekdays.

```
create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
```

Creates a recurring event until a specific date (inclusive).

### Creating All-Day Events

```
create event --autoDecline <eventName> on <dateString>
```

Creates a single all-day event.

```
create event <eventName> on <dateString> repeats <weekdays> for <N> times
```

Creates a recurring all-day event that repeats N times on specific weekdays.
Weekdays can have the following possible values:
'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday, 'F' is Friday, 'S' is Saturday,
and 'U' is Sunday.

```
create event <eventName> on <dateString> repeats <weekdays> until <dateString>
```

Creates a recurring all-day event until a specific date (inclusive).

### Editing Events

## 1. Editing a Single Event

To modify a specific event at a given date and time, use:

```plaintext
edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
```

Property can take the following values:

1. eventname
2. from
3. to
4. description
5. location
6. visibility

To modify multiple events, use:

```
edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
```

"events" Changes the property (e.g., name) of all events starting at a specific date/time
and have the same event name.

```
edit events <property> <eventName> with <NewPropertyValue>
```

Change the property (e.g., name) of all events with the same event name.

For the above two commands Property can take the following values:

1. eventname
2. from
3. to
4. description
5. location
6. visibility
7. recurring_days
8. occurrence_count
9. recurrence_end_date

### Viewing Events

```
print events on <dateString>
```

Prints a bulleted list of all events on that day along with their start and end time and location (
if any).

```
print events from <dateStringTtimeString> to <dateStringTtimeString>
```

Prints a bulleted list of all events in the given interval including their start and end times and
location (if any).

### Exporting Calendar

```
export cal fileName.csv
```

Exports the calendar as a CSV file that can be imported to Google Calendar app. The command also
prints the absolute path of the generated CSV file.

### Checking Status

```
show status on <dateStringTtimeString>
```

Prints busy status if the user has events scheduled on a given day and time, otherwise, available.

### Copying Events between Calendars

```
copy event <eventName> on <dateStringTimeString> --target <calendarName> to <dateStringTimeString>
```

Copies the event on the specified time from the active calendar to the target calendar on the
specified date.

```
copy events on <dateString> --target <calendarName> to <dateString>
```

Copies all the events on a given date from the active calendar to the target calendar on the
specified date.

```
copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
```

Copies all the events specified between the given time period from the active calendar
to the target calendar on the specified date.

## Features

All the Required Features are working.

<b>Major Features:</b>

1. Supports multiple calendars
2. Edit Calendar properties (name and timezone)
3. Copy events from one calendar to another
4. Create a single and recurring event
5. Edit properties of the events
6. Print Events
7. Export Calendar as CSV compatible with Google Calendar
8. Show status

## New Features and changes:

New ICalendar and ICalendarRepository has been introduced.

### ICalendar

Encapsulates a single calendar's data and behaviour. It stores the name, timezone and
IEventRepository which contains a list of events.

### ICalendarRepository

Represents a repository for managing multiple calendars. It has functionality to retrieve, add or
edit calendars. It also supports copying between calendars.

### Refactoring Event Management for Multi-Calendar Support

In the previous design, `CalendarModel` was responsible for both storing a `List<IEvent>` and
handling all event-related operations since only one calendar was involved.

With the introduction of a **multi-calendar system**, these responsibilities have been refactored
for better separation of concerns:

- A new interface, `IEventRepository`, now encapsulates the storage and management of events.
- It provides dedicated methods to **add**, **edit**, and **retrieve** events.
- `CalendarModel` has shifted its role to act primarily as a **manager**, coordinating between
  calendars and delegating event-related operations to the corresponding `IEventRepository`.

This architectural change improves **modularity**, supports **scalability**, and adheres to the *
*Single Responsibility Principle** by cleanly separating event logic from calendar coordination.

### Separation of Formatting and Export Responsibilities

In the earlier design, the `CalendarModel` handled both **formatting operations for printing** and *
*I/O operations for exporting** calendar data.

To follow better architectural practices, these responsibilities have now been moved to the *
*controller layer**. This promotes a cleaner separation of concerns by keeping the model focused on
data and business logic, while the controller manages presentation and I/O logic.

To avoid exposing the entire `List<IEvent>` directly to the controller, a set of dedicated **DTOs (
Data Transfer Objects)** has been introduced:

- `PrintEventsResponseDTO`
- `CalendarExporterDTO`
- `CopyEventRequestDTO`

These DTOs provide only the necessary data required for their respective operations, ensuring *
*encapsulation**, **data safety**, and **clear boundaries** between layers.

With this refactor:

- The **controller** handles all formatting and export logic.
- The **model** only provides filtered data in the form of DTOs.

This change aligns with **SOLID principles**, especially the **Interface Segregation** and **Single
Responsibility** principles.

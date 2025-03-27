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

---

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

---

## Features

All the Required Features are working.

### Major Features:

1. Supports multiple calendars
2. Edit Calendar properties (name and timezone)
3. Copy events from one calendar to another
4. Create a single and recurring event
5. Edit properties of the events
6. Print Events
7. Export Calendar as CSV compatible with Google Calendar
8. Show status

## Project Structure

### 1. **Controller**

The `Controller` package is responsible for handling interactions with the user, both through
headless (CLI) and interactive modes.

The controller also handles any kind of IO operation in the application, it is responsible for the export command in the application (adhering to MVC).

Interfaces are introduced for controller, exporting and commands for scalability and extensibility.

Commands Design Pattern is used for all the calendar commands, making it extensible for new commands and support undo if needed. 

Custom exceptions are thrown with proper error message making it easier for the user to understand what went wrong where. 
#### **Interfaces**

- **`ICalendarController`**: Defines the commands for both interactive and headless modes of
  calendar operations.
- **`ICalendarExporter`**: Interface for exporting events to various formats (currently only CSV).

#### **Package `impl`**

- **`CalendarControllerFactory`**: Factory class that initializes the correct `CalendarController`
  based on the selected mode (`--mode`).

#### **Package `commands`**

- **Command Design Pattern**: The package is built using the Command Design Pattern, enabling easy
  addition of new commands.

    - **Interfaces**:
        - **`Command`**: Defines a common interface for all command classes.
        - **`CommandProperties`**: Enum that categorizes different types of commands (e.g., create, update, copy events, etc).

    - **Package `impl`**: Contains concrete implementations for the different calendar operations (
      e.g., creating, updating, deleting events).

#### **Package `exporter`**

- **CSV Export**: Contains functionality for exporting events to CSV format.
    - **Constants**: Defines ICalendarExporter implementations for different file extensions (CSV
      for now).

#### **Exceptions**

- **`InvalidCommandException`**: Thrown when an invalid or unsupported command is encountered.

---

### 2. **Model**

The `Model` package contains the core of the calendar application. It manages calendars, events, and
repositories for storing and retrieving data.

Interfaces are introduced for the Calendar and the Event representation.
Like wise there are two repositories, ICalendarRepository and IEventRepository, which manipulates these data on any command that runs. 
This ensures separation of concern and hold the data and operations togethers ensuring data encapsulation. 

Search events strategies are written for better readability and addition of new search strategy if needed. 

Throws appropriate custom error and with proper message. 

#### **Interfaces**

- **`ICalendar`**: Defines the data class for a calendar.
- **`ICalendarModel`**: Orchestrates the calendar application and handles event operations.
- **`ICalendarRepository`**: Defines operations for interacting with calendars (e.g., creating,
  updating, copying).
- **`IEvent`**: Defines the structure and behavior of events in the calendar.
- **`IEventRepository`**: Handles operations related to events (e.g., creating, updating, searching
  events, etc.).
- **`SearchEventsStrategy`**: Interface for different event search strategies.

#### **Enums**

- **`EventVisibility`**: Enum that defines the visibility of events (e.g., private, public).
- **`SearchType`**: Enum representing different search strategies (e.g., by date, by name).

#### **Exceptions**

- **`EventConflictException`**: Thrown when trying to add a new event that conflicts with an
  existing event's time.

#### **Package `impl`**

- **`Calendar`**: Concrete implementation of `ICalendar`, managing the calendar's data and
  operations.
- **`CalendarModel`**: Orchestrates calendar events and operations, coordinating with
  `CalendarRepository` and `EventRepository`.
- **`CalendarPropertyUpdater`**: Dynamically updates calendar properties, dispatching changes to
  `CalendarRepository`.
- **`CalendarRepository`**: Handles all data-related operations for calendars.
- **`Event`**: Concrete implementation of `IEvent` that holds event data (e.g., title, time,
  visibility).
- **`EventPropertyUpdater`**: Dynamically updates event properties and dispatches updates to
  `EventRepository`.
- **`EventRepository`**: Handles all data-related operations for events.

#### **Package `searchStrategies`**

- Contains concrete implementations of various search strategies used by `EventRepository` to search
  events (e.g., by name, by date range).
- Strategies are
    - ExactMatch, matches the given exact name, exact start and end time if present
    - InBetween, matches events inclusive of the given start and end time and anything in between
    - Overlapping, matches events if there's any overlap with the given times 

---

### 3. **View**

The `View` package is responsible for displaying the output to the user.

#### **Interfaces**

- **`ICalendarView`**: Defines the interface for displaying calendar-related messages to the user.

#### **Package `impl`**

- **`CLIView`**: Concrete implementation of `ICalendarView` for the command-line interface (CLI),
  handling user interactions and output display.

---

### 4. **Utils**

The `Utils` package contains utility classes that centralize common logic and functionality used
throughout the application.

#### **`TimeUtil`**

- Contains all time-related logic for the calendar application (e.g., time zone conversion, duration
  calculations, formatting).
- **Impact of Changes**: Modifying `TimeUtil` will affect all parts of the application that rely on
  time-based operations.

---

## Calendar Application Design Overview

The design choices made for this calendar application are well-structured, promoting
maintainability, scalability, and extensibility. Here's why this design is a good choice:

## 1. Use of Interfaces and Abstraction

The design uses interfaces like `ICalendarController`, `ICalendar`, `IEvent`, etc., to define clear
contracts for each part of the application:

- **Flexibility**: You can easily replace or modify parts of the system without affecting the rest.
- **Testability**: Interfaces allow easier testing by mocking dependencies.
- **Loose Coupling**: Components interact via well-defined interfaces, reducing dependencies.

## 2. Command Design Pattern

The **Command Design Pattern** is used in the `commands` package:

- **Scalability**: New commands (e.g., `DeleteEvent`, `UpdateEvent`) can be easily added.
- **Decoupling**: The `Controller` doesn't need to know the details of each command.
- **Undo/Redo**: The pattern can be extended to support undo/redo functionality.

## 3. Factory Pattern for CalendarController Initialization

The **Factory Pattern** (`CalendarControllerFactory`) helps initialize the `CalendarController`
based on the `--mode`:

- **Flexibility**: New modes (like a web mode) can be added without affecting other code.
- **Single Responsibility**: The factory handles all initialization, reducing complexity in the
  controller.
- **Centralized Initialization**: All setup logic is in one place, improving maintainability.

## 4. Interface for Searching Events

- **Extensibility**: You can easily add new search strategies.
- **Separation of strategies**: Each search strategy is encapsulated in its own class, making the code
  cleaner and more maintainable.

## 5. Exception Handling

Custom exceptions like `InvalidCommandException` and `EventConflictException` are used to handle
specific errors:

- **Clear Error Handling**: It's easy to identify where errors come from.
- **Separation of Error Logic**: Different errors are handled in their own classes, keeping the code
  organized.

## 6. Time Utils

The `TimeUtil` class handles all time-related logic:

- **Consistency**: All time-related operations are done in one place, reducing the risk of bugs.
- **Single Responsibility**: The class only handles time-related tasks, following the Single
  Responsibility Principle.

---

## Project Contributors

- **Harikrishna Nagarajan** - Event modeling, Export functionality, Time Functions and relevant test
  cases.
- **Sri Vishaak Ramesh Babu** - Controller, Commands parsing, Calendar modeling and E2E test cases.

---
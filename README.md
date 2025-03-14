# Calendar Application

This is a calendar application that allows users to manage events through a text-based interface. Users can create, edit, and view events in a calendar, as well as export the calendar data to CSV format
which is supported by Google Calendar.

## Getting Started

## Running the Application

The application can run in two modes:

1. **Interactive Mode**: Allows users to enter commands directly through the command line.
2. **Headless Mode**: Processes commands from a file without requiring user interaction.

### Interactive Mode

To run the application in interactive mode:

```bash
java cd src/main/java
```
```bash
javac calendarapp/Main.java
```
```bash
java calendarapp.Main --mode interactive
```

Once the application starts, you can enter commands directly in the console.
For reference use res/commands/validCommands.txt file.

### Headless Mode

To run the application in headless mode compile the java main file similar to interactive and:

```bash
java calendarapp.Main --mode headless {absolute_path}
```
The commands file is present inside res/commands/validCommands.txt
## Supported Commands

The application supports the following commands:

### Creating Events

```
create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
```
Creates a single event in the calendar. The `--autoDecline` option is optional and indicates if the event should be rejected in case of a conflict.

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
'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday, 'F' is Friday, 'S' is Saturday, and 'U' is Sunday.

```
create event <eventName> on <dateString> repeats <weekdays> until <dateString>
```
Creates a recurring all-day event until a specific date (inclusive).

### Editing Events

```
edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
```
Changes the property (e.g., name) of the given event.
Property can take the following values:
1. eventname
2. from
3. to
4. description
5. location
6. visibility
7. recurring_days
8. occurrence_count
9. recurrence_end_date

```
edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
```
Changes the property (e.g., name) of all events starting at a specific date/time and have the same event name.

```
edit events <property> <eventName> with <NewPropertyValue>
```
Change the property (e.g., name) of all events with the same event name.

### Viewing Events

```
print events on <dateString>
```
Prints a bulleted list of all events on that day along with their start and end time and location (if any).

```
print events from <dateStringTtimeString> to <dateStringTtimeString>
```
Prints a bulleted list of all events in the given interval including their start and end times and location (if any).

### Exporting Calendar

```
export cal fileName.csv
```
Exports the calendar as a CSV file that can be imported to Google Calendar app. The command also prints the absolute path of the generated CSV file.

### Checking Status

```
show status on <dateStringTtimeString>
```
Prints busy status if the user has events scheduled on a given day and time, otherwise, available.

For a complete list of supported commands, please refer to the file `res/commands/validCommands.txt` included with the application.

## Features
All the Required Features are working.

<b>Major Features:</b>
1. Create a single and recurring event (with and without autoDecline)
2. Edit all the event properties for single and recurring events (with and without autoDecline)
3. Print Events
4. Export Calendar as CSV compatible with Google Calendar
5. Show status

## Contribution
1. Sri Vishaak Ramesh Babu - 
i. Designed and implemented controller and view
ii. Integrated Controller with Model
iii. E2E test cases for the Calendar Application

2. Harikrishna Nagarajan -
i. Designed and implemented Model
ii. Unit test cases for Model
## Notes

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
java -jar target/calendar-app.jar --mode interactive
```

Once the application starts, you can enter commands directly in the console.
For reference use res/validCommands.txt file.

### Headless Mode

To run the application in headless mode:

```bash
java -jar target/calendar-app.jar --mode headless res/validCommands.txt
```

Replace `res/validCommands.txt` with the path to your own file containing calendar commands.

## Command Examples

The application supports the following types of commands:

### Creating Events

```
create event "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00
create event --autoDecline "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00
create event "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 description "Meeting details" location "Conference Room" visibility private
```

### Creating Recurring Events

```
create event "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 repeats MW for 5 times
create event "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 repeats TR until 2025-04-10T11:00
```

### Creating All-Day Events

```
create event "Event Name" on 2025-03-10
create event "Event Name" on 2025-03-10 description "Holiday"
create event "Event Name" on 2025-03-10 repeats MWF for 3 times
```

### Editing Events

```
edit event eventname "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 with "New Event Name"
edit event from "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 with 2025-03-10T11:00
edit event description "Event Name" from 2025-03-10T10:00 to 2025-03-10T11:00 with "Updated description"
```

### Viewing Events

```
print events on 2025-03-10
print events from 2025-03-10T09:00 to 2025-03-10T12:00
```

### Exporting Calendar

```
export cal filename.csv
```

### Checking Status

```
show status on 2025-03-10T10:00
```

## Notes

- The `--autoDecline` flag indicates if an event should be automatically declined in case of a conflict.
- Weekday codes: 'M' (Monday), 'T' (Tuesday), 'W' (Wednesday), 'R' (Thursday), 'F' (Friday), 'S' (Saturday), 'U' (Sunday).

For a complete list of supported commands, please refer to the file `res/validCommands.txt` included with the application.
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
For reference use res/commands/validCommands.txt file.

### Headless Mode

To run the application in headless mode:

```bash
java -jar target/calendar-app.jar --mode headless res/commands/validCommands.txt
```

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


## Notes

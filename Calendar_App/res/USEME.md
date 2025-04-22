## GUI Instructions

- The main view of the calendar app shows the current month of the calendar.
- Each day is a button that can be clicked to reveal the Day View.
- The Day View can also be accessed by the Day/Month radio buttons at the top of the screen.
- The active calendar is displayed by name in the dropdown menu in the header.
- New calendars can be created using the New Calendar button which sits next to the calendar dropdown.
- The current timezone and month/day of the calendar are visible in the top left.
- The '<' and '>' buttons navigate to either the previous/next month or day depending on the current view.
- The import button allows selecting a csv file from your computer and uploading events if they are int the format:
Subject,Start date,Start time,End Date,End Time,All Day Event,Description,Location,Private. Times must be in the format hh:mm and dates must be in the format yyyy-MM-dd. The all day event and private columns must then be either "true" or "false" string, without quotes. All other events are string values.
- The export button then exports all events in the active calendar to a file named calendar.csv in the present working directory.
- The Day View shows all events on the selected day in the component on the left hand side.
- The Day View allows for creation of events using the form on the right hand side.
- Selecting either the single or recurring radio button will display the corresponding form for either a single or recurring event.
- Click events in the left hand side component to reveal the view/edit modal.
- All values may be changed. For recurring events, if changing recurring parameters such as weekday, ends on date, or after n occurrences, the event series must be updated instead of a single instance.
- The edit, edit this and following, and edit all buttons in the recurring view/edit modal will edit the individual event, the selected event and all events following it in the series, or all events in the series respectively.
- The view/edit event modal will change dynamically based on if the event is recurring or a single.

### Analytics

1. Click the Analytics button in the header panel of the GUI to view the analytics.
2. Choose the required start and end date for the analytics and generate the analytics for the given time frame.
3. Click Close Analytics button to go back to the month view. Closing will not refresh the results screen, it will retain the last generated analytics.
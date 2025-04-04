package calendarapp.view.impl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Main class for Calendar UI application.
 */
public class CalendarUI {

  /**
   * Main method to start the application.
   */
  public static void main(String[] args) {
    // Use the Event Dispatch Thread for Swing applications
    SwingUtilities.invokeLater(() -> {
      try {
        // Set look and feel to the system look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Create and display the calendar UI
      CalendarFrame frame = new CalendarFrame();
      frame.setVisible(true);
    });
  }

  /**
   * Main application frame for the calendar UI.
   */
  static class CalendarFrame extends JFrame {

    // UI Components
    private JPanel mainPanel;
    private JPanel calendarPanel;
    private JPanel sidebarPanel;

    private JButton createButton;
    private JPanel calendarListPanel;
    private JButton monthButton;

    private MonthCalendarPanel monthCalendarPanel;
    private JPanel dayViewPanel;
    private JPanel weekViewPanel;
    private JPanel yearViewPanel;

    /**
     * Constructor initializes the UI components.
     */
    public CalendarFrame() {
      super("Calendar");
      initializeUI();
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void initializeUI() {
      // Set up the main frame
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(900, 600);
      setLocationRelativeTo(null);

      // Create main panel with border layout
      mainPanel = new JPanel(new BorderLayout());

      // Initialize sidebar and calendar panels
      initializeSidebar();
      initializeCalendarPanel();

      // Add panels to main panel
      mainPanel.add(sidebarPanel, BorderLayout.WEST);
      mainPanel.add(calendarPanel, BorderLayout.CENTER);

      // Set content pane
      setContentPane(mainPanel);
    }

    /**
     * Initializes the sidebar panel with create button and calendar list.
     */
    private void initializeSidebar() {
      sidebarPanel = new JPanel();
      sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
      sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
      sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      sidebarPanel.setBackground(Color.WHITE);

      // Create button - fully aligned to the left
      createButton = new JButton("+ Create");
      createButton.setHorizontalAlignment(SwingConstants.LEFT);
      createButton.setFont(new Font("Arial", Font.PLAIN, 14));
      createButton.setFocusPainted(false);

      // Use a left-aligned flow layout with no margins for the button
      JPanel createButtonPanel = new JPanel(new BorderLayout());
      createButtonPanel.setBackground(Color.WHITE);
      createButtonPanel.add(createButton, BorderLayout.WEST);
      createButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      createButtonPanel.setMaximumSize(new Dimension(180, 40));

      // My Calendars panel properly aligned
      JPanel myCalendarsHeaderPanel = new JPanel(new BorderLayout());
      myCalendarsHeaderPanel.setBackground(Color.WHITE);
      myCalendarsHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      myCalendarsHeaderPanel.setMaximumSize(new Dimension(180, 30));

      JLabel myCalendarsLabel = new JLabel("My Calendars");
      myCalendarsLabel.setFont(new Font("Arial", Font.BOLD, 14));

      JButton expandButton = new JButton("^");
      expandButton.setPreferredSize(new Dimension(20, 20));
      expandButton.setBorderPainted(false);
      expandButton.setContentAreaFilled(false);
      expandButton.setFocusPainted(false);

      myCalendarsHeaderPanel.add(myCalendarsLabel, BorderLayout.WEST);
      myCalendarsHeaderPanel.add(expandButton, BorderLayout.EAST);

      // Calendar list panel with radio buttons
      calendarListPanel = new JPanel();
      calendarListPanel.setLayout(new BoxLayout(calendarListPanel, BoxLayout.Y_AXIS));
      calendarListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      calendarListPanel.setBackground(Color.WHITE);

      // Initialize calendar radio buttons with action listeners
      String[] defaultCalendars = {"Default", "Personal", "Work"};
      ButtonGroup calendarGroup = new ButtonGroup(); // Only one calendar active at a time

      for (String calendarName : defaultCalendars) {
        JRadioButton radioButton = new JRadioButton(calendarName);
        radioButton.setBackground(Color.WHITE);
        radioButton.setSelected(calendarName.equals("Default")); // Default selected initially
        radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        calendarGroup.add(radioButton); // Add to button group so only one can be selected

        // Add action listener to load appropriate calendar
        radioButton.addActionListener(e -> {
          // When selected, load the corresponding calendar
          JRadioButton source = (JRadioButton)e.getSource();
          if (source.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Loading " + calendarName + " calendar",
                "Calendar Selection",
                JOptionPane.INFORMATION_MESSAGE);

            // In a real implementation, you would load calendar data here
            // For demo, add a sample event
            LocalDate today = LocalDate.now();
            monthCalendarPanel.clearEvents(); // Clear existing events
            monthCalendarPanel.addSampleEvent(
                today,
                calendarName + " Event",
                calendarName.equals("Default") ? new Color(0, 120, 200, 150) :
                    calendarName.equals("Personal") ? new Color(200, 0, 120, 150) :
                        new Color(0, 200, 0, 150)
            );
          }
        });

        calendarListPanel.add(radioButton);
      }

      // Add components to sidebar
      sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      sidebarPanel.add(createButtonPanel); // Use the panel instead of button directly
      sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      sidebarPanel.add(myCalendarsHeaderPanel);
      sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      sidebarPanel.add(calendarListPanel);

      // Add expand/collapse functionality
      expandButton.addActionListener(e -> {
        boolean isVisible = calendarListPanel.isVisible();
        calendarListPanel.setVisible(!isVisible);
        expandButton.setText(isVisible ? "v" : "^");
      });

      // Add create button functionality to show dialog
      createButton.addActionListener(e -> showCreateEventDialog());
    }

    /**
     * Initializes the main calendar panel.
     */
    private void initializeCalendarPanel() {
      calendarPanel = new JPanel(new BorderLayout());
      calendarPanel.setBackground(Color.WHITE);
      calendarPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      // Header panel with title and month selector
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      headerPanel.setBackground(Color.WHITE);

      JLabel calendarLabel = new JLabel("Calendar");
      calendarLabel.setFont(new Font("Arial", Font.BOLD, 18));

      monthButton = new JButton("Month ^");
      monthButton.setPreferredSize(new Dimension(120, 30));
      monthButton.setFont(new Font("Arial", Font.PLAIN, 14));

      headerPanel.add(calendarLabel, BorderLayout.WEST);
      headerPanel.add(monthButton, BorderLayout.EAST);

      // Initialize view panels
      monthCalendarPanel = new MonthCalendarPanel();
      dayViewPanel = createDayView();
      weekViewPanel = createWeekView();
      yearViewPanel = createYearView();

      // Add a popup menu for month button
      JPopupMenu viewMenu = new JPopupMenu();
      String[] viewOptions = {"Day", "Week", "Month", "Year"};

      for (String option : viewOptions) {
        JMenuItem menuItem = new JMenuItem(option);
        menuItem.addActionListener(e -> {
          monthButton.setText(option + " ^");
          // We would switch views based on selection in a real implementation
        });
        viewMenu.add(menuItem);
      }

      monthButton.addActionListener(e -> {
        viewMenu.show(monthButton, 0, monthButton.getHeight());
      });

      // Add functionality to view options
      for (String viewType : viewOptions) {
        Component comp = viewMenu.getComponentAtIndex(java.util.Arrays.asList(viewOptions).indexOf(viewType));
        if (comp instanceof JMenuItem) {
          JMenuItem item = (JMenuItem) comp;
          item.addActionListener(e -> {
            monthButton.setText(viewType + " ^");
            if (viewType.equals("Month")) {
              showMonthView();
            } else if (viewType.equals("Day")) {
              showDayView();
            } else if (viewType.equals("Week")) {
              showWeekView();
            } else if (viewType.equals("Year")) {
              showYearView();
            }
          });
        }
      }

      // Add components to calendar panel
      calendarPanel.add(headerPanel, BorderLayout.NORTH);
      calendarPanel.add(monthCalendarPanel, BorderLayout.CENTER);
    }

    /**
     * Shows the day view.
     */
    private void showDayView() {
      calendarPanel.remove(monthCalendarPanel);
      calendarPanel.remove(weekViewPanel);
      calendarPanel.remove(yearViewPanel);
      calendarPanel.add(dayViewPanel, BorderLayout.CENTER);
      calendarPanel.revalidate();
      calendarPanel.repaint();
    }

    /**
     * Shows the week view.
     */
    private void showWeekView() {
      calendarPanel.remove(monthCalendarPanel);
      calendarPanel.remove(dayViewPanel);
      calendarPanel.remove(yearViewPanel);
      calendarPanel.add(weekViewPanel, BorderLayout.CENTER);
      calendarPanel.revalidate();
      calendarPanel.repaint();
    }

    /**
     * Shows the month view.
     */
    private void showMonthView() {
      calendarPanel.remove(dayViewPanel);
      calendarPanel.remove(weekViewPanel);
      calendarPanel.remove(yearViewPanel);
      calendarPanel.add(monthCalendarPanel, BorderLayout.CENTER);
      calendarPanel.revalidate();
      calendarPanel.repaint();
    }

    /**
     * Shows the year view.
     */
    private void showYearView() {
      calendarPanel.remove(monthCalendarPanel);
      calendarPanel.remove(dayViewPanel);
      calendarPanel.remove(weekViewPanel);
      calendarPanel.add(yearViewPanel, BorderLayout.CENTER);
      calendarPanel.revalidate();
      calendarPanel.repaint();
    }

    /**
     * Creates a simple day view.
     */
    private JPanel createDayView() {
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBackground(Color.WHITE);

      // Day header panel
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(240, 240, 240));

      JLabel dateLabel = new JLabel(LocalDate.now().format(
          DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
      dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
      dateLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
      headerPanel.add(dateLabel, BorderLayout.CENTER);

      // Time slots panel
      JPanel timePanel = new JPanel();
      timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
      timePanel.setBackground(Color.WHITE);

      // Add time slots from 8 AM to 8 PM
      for (int hour = 8; hour <= 20; hour++) {
        String hourLabel = String.format("%d:00 %s",
            hour > 12 ? hour - 12 : hour,
            hour >= 12 ? "PM" : "AM");

        JPanel hourPanel = new JPanel(new BorderLayout());
        hourPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        hourPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        hourPanel.setPreferredSize(new Dimension(panel.getWidth(), 60));

        JLabel timeLabel = new JLabel(hourLabel);
        timeLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        hourPanel.add(timeLabel, BorderLayout.WEST);

        timePanel.add(hourPanel);
      }

      // Add components to main panel
      panel.add(headerPanel, BorderLayout.NORTH);

      // Wrap time panel in scroll pane
      JScrollPane scrollPane = new JScrollPane(timePanel);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(null);
      panel.add(scrollPane, BorderLayout.CENTER);

      return panel;
    }

    /**
     * Creates a simple week view.
     */
    private JPanel createWeekView() {
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBackground(Color.WHITE);

      // Week header panel
      JPanel headerPanel = new JPanel(new GridLayout(1, 7));
      headerPanel.setBackground(new Color(240, 240, 240));

      // Get current week dates
      LocalDate today = LocalDate.now();
      LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

      // Add day headers for the week
      for (int i = 0; i < 7; i++) {
        LocalDate date = startOfWeek.plusDays(i);
        JLabel dayLabel = new JLabel(date.format(DateTimeFormatter.ofPattern("EEE, MMM d")));
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        headerPanel.add(dayLabel);
      }

      // Time grid panel
      JPanel gridPanel = new JPanel(new GridLayout(0, 7));
      gridPanel.setBackground(Color.WHITE);

      // Add time slots for each day
      for (int hour = 8; hour <= 20; hour++) {
        for (int day = 0; day < 7; day++) {
          JPanel hourPanel = new JPanel();
          hourPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

          if (day == 0) {
            // Add time label for first column
            String hourLabel = String.format("%d:00", hour > 12 ? hour - 12 : hour);
            JLabel timeLabel = new JLabel(hourLabel);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            hourPanel.add(timeLabel);
          }

          gridPanel.add(hourPanel);
        }
      }

      // Add components to main panel
      panel.add(headerPanel, BorderLayout.NORTH);

      // Wrap grid panel in scroll pane
      JScrollPane scrollPane = new JScrollPane(gridPanel);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(null);
      panel.add(scrollPane, BorderLayout.CENTER);

      return panel;
    }

    /**
     * Creates a simple year view.
     */
    private JPanel createYearView() {
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBackground(Color.WHITE);

      // Year header
      int currentYear = LocalDate.now().getYear();
      JLabel yearLabel = new JLabel(String.valueOf(currentYear));
      yearLabel.setFont(new Font("Arial", Font.BOLD, 20));
      yearLabel.setHorizontalAlignment(SwingConstants.CENTER);
      yearLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

      // Months grid panel
      JPanel monthsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
      monthsPanel.setBackground(Color.WHITE);
      monthsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

      // Add mini calendar for each month
      for (int month = 1; month <= 12; month++) {
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Month name
        YearMonth yearMonth = YearMonth.of(currentYear, month);
        JLabel monthNameLabel = new JLabel(yearMonth.getMonth().getDisplayName(
            TextStyle.FULL, Locale.getDefault()));
        monthNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monthNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        monthNameLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Mini calendar for month
        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 1, 1));

        // Add day headers
        for (DayOfWeek day : DayOfWeek.values()) {
          JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.NARROW, Locale.getDefault()));
          dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
          dayLabel.setFont(new Font("Arial", Font.PLAIN, 9));
          daysPanel.add(dayLabel);
        }

        // Add days for the month
        LocalDate firstOfMonth = LocalDate.of(currentYear, month, 1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();

        // Add empty cells for days before the first of month
        for (int i = 1; i < dayOfWeekValue; i++) {
          daysPanel.add(new JLabel());
        }

        // Add days
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
          JLabel dayLabel = new JLabel(String.valueOf(day));
          dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
          dayLabel.setFont(new Font("Arial", Font.PLAIN, 9));

          // Highlight current day
          if (LocalDate.now().equals(LocalDate.of(currentYear, month, day))) {
            dayLabel.setOpaque(true);
            dayLabel.setBackground(new Color(220, 240, 255));
          }

          daysPanel.add(dayLabel);
        }

        // Add to month panel
        monthPanel.add(monthNameLabel, BorderLayout.NORTH);
        monthPanel.add(daysPanel, BorderLayout.CENTER);

        // Add to months grid
        monthsPanel.add(monthPanel);
      }

      // Add components to main panel
      panel.add(yearLabel, BorderLayout.NORTH);
      panel.add(monthsPanel, BorderLayout.CENTER);

      return panel;
    }

    /**
     * Shows a dialog to create a new event.
     */
    private void showCreateEventDialog() {
      JDialog createDialog = new JDialog(this, "Create Event", true);
      createDialog.setSize(400, 400);
      createDialog.setLocationRelativeTo(this);

      // Main panel with padding
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      // Create form panel with GridLayout
      JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

      // Add form fields
      formPanel.add(new JLabel("Event Name:"));
      JTextField eventNameField = new JTextField();
      formPanel.add(eventNameField);

      formPanel.add(new JLabel("Start Time:"));
      JTextField startTimeField = new JTextField("YYYY-MM-DD HH:MM");
      formPanel.add(startTimeField);

      formPanel.add(new JLabel("End Time:"));
      JTextField endTimeField = new JTextField("YYYY-MM-DD HH:MM");
      formPanel.add(endTimeField);

      formPanel.add(new JLabel("Description:"));
      JTextField descriptionField = new JTextField();
      formPanel.add(descriptionField);

      formPanel.add(new JLabel("Location:"));
      JTextField locationField = new JTextField();
      formPanel.add(locationField);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);

      // Add action listeners
      cancelButton.addActionListener(e -> createDialog.dispose());

      saveButton.addActionListener(e -> {
        // In a real implementation, we would save the event
        // For now, just close the dialog and add a sample event
        createDialog.dispose();

        // Add sample event to the calendar
        LocalDate today = LocalDate.now();
        monthCalendarPanel.addSampleEvent(today, eventNameField.getText(), new Color(0, 100, 200, 150));
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      createDialog.add(mainPanel);

      createDialog.setVisible(true);
    }
  }

  /**
   * A custom panel for displaying a month view calendar.
   */
  static class MonthCalendarPanel extends JPanel {

    private final Color headerBackground = new Color(240, 240, 240);
    private final Color todayBackground = new Color(220, 240, 255);
    private final Color dayBackground = Color.WHITE;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    private LocalDate today;

    /**
     * Constructor initializes the calendar with the current month.
     */
    public MonthCalendarPanel() {
      this.currentYearMonth = YearMonth.now();
      this.selectedDate = LocalDate.now();
      this.today = LocalDate.now();

      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder());
      setBackground(Color.WHITE);

      buildCalendar();
    }

    /**
     * Builds the calendar view for the current year-month.
     */
    private void buildCalendar() {
      removeAll();

      // Month-year navigation panel
      JPanel monthYearPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      monthYearPanel.setBackground(Color.WHITE);

      JButton prevMonthButton = new JButton("<");
      prevMonthButton.setFocusPainted(false);

      JLabel monthYearLabel = new JLabel(currentYearMonth.format(
          DateTimeFormatter.ofPattern("MMMM yyyy")));
      monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));

      JButton nextMonthButton = new JButton(">");
      nextMonthButton.setFocusPainted(false);

      monthYearPanel.add(prevMonthButton);
      monthYearPanel.add(monthYearLabel);
      monthYearPanel.add(nextMonthButton);

      // Create header panel for days of week
      JPanel headerPanel = new JPanel(new GridLayout(1, 7));
      headerPanel.setBackground(headerBackground);
      headerPanel.setPreferredSize(new Dimension(getWidth(), 30)); // Fixed smaller height

      // Add day headers
      for (int i = 0; i < 7; i++) {
        // Adjust to display Sunday as first day (typical in US calendars)
        DayOfWeek day = DayOfWeek.of(i == 0 ? 7 : i);
        JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dayLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        headerPanel.add(dayLabel);
      }

      // Create grid panel for days - using most of the space
      JPanel daysPanel = new JPanel(new GridLayout(0, 7));
      daysPanel.setBackground(Color.WHITE);

      // Get the first day of month
      LocalDate firstOfMonth = currentYearMonth.atDay(1);

      // Get the day of week for the first day (1 = Monday, 7 = Sunday in ISO)
      int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();

      // Adjust for Sunday as first day of week in many locales
      int offset = dayOfWeekValue % 7;

      // Add empty cells for days before the first of month
      for (int i = 0; i < offset; i++) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(dayBackground);
        emptyPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        daysPanel.add(emptyPanel);
      }

      // Add cells for each day of the month
      int daysInMonth = currentYearMonth.lengthOfMonth();
      for (int day = 1; day <= daysInMonth; day++) {
        LocalDate date = currentYearMonth.atDay(day);
        JPanel dayPanel = createDayPanel(date);
        daysPanel.add(dayPanel);
      }

      // Add event listeners for month navigation
      prevMonthButton.addActionListener(e -> {
        currentYearMonth = currentYearMonth.minusMonths(1);
        buildCalendar();
      });

      nextMonthButton.addActionListener(e -> {
        currentYearMonth = currentYearMonth.plusMonths(1);
        buildCalendar();
      });

      // Use a container panel for proper layout
      JPanel containerPanel = new JPanel(new BorderLayout());
      containerPanel.add(headerPanel, BorderLayout.NORTH);
      containerPanel.add(daysPanel, BorderLayout.CENTER);

      // Add panels to main layout
      add(monthYearPanel, BorderLayout.NORTH);
      add(containerPanel, BorderLayout.CENTER); // Days panel now takes the center

      revalidate();
      repaint();
    }

    /**
     * Creates a panel for a single day in the calendar.
     *
     * @param date The date to create the panel for
     * @return A JPanel representing the day
     */
    private JPanel createDayPanel(LocalDate date) {
      JPanel dayPanel = new JPanel(new BorderLayout());
      dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      // Set background color based on whether it's today or selected
      if (date.equals(today)) {
        dayPanel.setBackground(todayBackground);
      } else {
        dayPanel.setBackground(dayBackground);
      }

      // Create the day number label
      JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
      dayLabel.setBorder(new EmptyBorder(5, 5, 0, 0));
      dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));

      // Panel for events (would be populated with events)
      JPanel eventsPanel = new JPanel();
      eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
      eventsPanel.setBackground(dayPanel.getBackground());

      dayPanel.add(dayLabel, BorderLayout.NORTH);
      dayPanel.add(eventsPanel, BorderLayout.CENTER);

      // Add click listener to select day
      dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          selectedDate = date;
          buildCalendar(); // Rebuild to update selection
        }
      });

      return dayPanel;
    }

    /**
     * Adds a sample event to the calendar for demonstration.
     *
     * @param date The date of the event
     * @param title The event title
     * @param color The event color
     */
    public void addSampleEvent(LocalDate date, String title, Color color) {
      // This is a simplified way to add an event for demonstration
      Component[] components = getComponents();
      if (components.length < 2) return;

      // Find the container panel
      Component containerPanel = null;
      for (Component comp : components) {
        if (comp instanceof JPanel) {
          containerPanel = comp;
          break;
        }
      }

      if (containerPanel == null) return;

      // Find the days panel in the container
      Component daysPanel = null;
      for (Component comp : ((Container)containerPanel).getComponents()) {
        if (comp instanceof JPanel && !comp.equals(components[0])) {
          daysPanel = comp;
          break;
        }
      }

      if (daysPanel == null) return;

      // Find the correct day panel
      int dayOfMonth = date.getDayOfMonth();
      int firstDayOffset = date.withDayOfMonth(1).getDayOfWeek().getValue() % 7;
      int dayIndex = firstDayOffset + dayOfMonth - 1;

      // Check if the date is in the current month view
      if (date.getMonthValue() != currentYearMonth.getMonthValue() ||
          date.getYear() != currentYearMonth.getYear() ||
          dayIndex >= ((JPanel)daysPanel).getComponentCount()) {
        return;
      }

      // Get the day panel
      Component dayComp = ((JPanel)daysPanel).getComponent(dayIndex);
      if (!(dayComp instanceof JPanel)) return;
      JPanel dayPanel = (JPanel)dayComp;

      // Find the events panel within the day panel
      Component[] dayComponents = dayPanel.getComponents();
      for (Component comp : dayComponents) {
        if (comp instanceof JPanel) {
          JPanel eventsPanel = (JPanel)comp;

          // Create an event indicator
          JPanel eventIndicator = new JPanel();
          eventIndicator.setBackground(color);
          eventIndicator.setPreferredSize(new Dimension(eventsPanel.getWidth(), 15));
          eventIndicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));

          JLabel eventLabel = new JLabel(title);
          eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));

          eventsPanel.add(eventIndicator);
          eventsPanel.add(eventLabel);
          eventsPanel.revalidate();
          break;
        }
      }

      revalidate();
      repaint();
    }

    /**
     * Clears all events from the calendar view.
     */
    public void clearEvents() {
      Component[] components = getComponents();
      if (components.length < 2) return;

      // Find the container panel
      Component containerPanel = null;
      for (Component comp : components) {
        if (comp instanceof JPanel) {
          containerPanel = comp;
          break;
        }
      }

      if (containerPanel == null) return;

      // Find the days panel in the container
      Component daysPanel = null;
      for (Component comp : ((Container)containerPanel).getComponents()) {
        if (comp instanceof JPanel && !comp.equals(components[0])) {
          daysPanel = comp;
          break;
        }
      }

      if (daysPanel == null) return;

      // Clear events from all day panels
      for (Component dayComp : ((Container)daysPanel).getComponents()) {
        if (dayComp instanceof JPanel) {
          JPanel dayPanel = (JPanel)dayComp;

          // Find the events panel within the day panel
          for (Component comp : dayPanel.getComponents()) {
            if (comp instanceof JPanel) {
              JPanel eventsPanel = (JPanel)comp;
              eventsPanel.removeAll();
              eventsPanel.revalidate();
              break;
            }
          }
        }
      }

      revalidate();
      repaint();
    }
  }
}
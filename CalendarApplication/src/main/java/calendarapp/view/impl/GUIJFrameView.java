package calendarapp.view.impl;


import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calendarapp.controller.Features;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.view.GUIView;

/**
 * Consolidated implementation of the calendar view using Swing.
 * This class contains all UI components in a single file.
 */
public class GUIJFrameView implements GUIView {

  // Main frame components
  private JFrame mainFrame;
  private JPanel mainPanel;

  // Header components
  private JPanel headerPanel;
  private JLabel titleLabel;
  private JButton dayButton;
  private JButton weekButton;
  private JButton monthButton;

  // Sidebar components
  private JPanel sidebarPanel;
  private JButton createEventButton;
  private JButton createCalendarButton;
  private JPanel calendarListPanel;
  private ButtonGroup calendarGroup;

  // Content components
  private JPanel contentPanel;
  private JPanel navigationPanel;
  private JPanel calendarPanel;
  private JButton prevButton;
  private JButton nextButton;
  private JLabel dateLabel;

  // Details panel components
  private JPanel detailsPanel;
  private JPanel detailsHeaderPanel;
  private JPanel detailsContentPanel;
  private JLabel detailsDateLabel;
  private JButton closeDetailsButton;

  // Controller reference
  private Features controller;

  // Data structures
  private List<String> calendarNames = new ArrayList<>();
  private final Map<String, Color> calendarColors = new HashMap<>();
  private String activeCalendar = "Default";
  private String currentViewType = "month"; // Default view
  private LocalDate currentDate = LocalDate.now();
  private LocalDate selectedDate = LocalDate.now();
  private List<PrintEventsResponseDTO> currentEvents = new ArrayList<>();

  // Random generator for colors
  private final Random random = new Random();

  @Override
  public void addFeatures(Features controller) {
    this.controller = controller;

    // Set up the UI components
    createFrame();
    createComponents();

    // Set up initial default calendars for demo
    setupDefaultCalendars();

    // Assemble the UI
    assembleUI();

    // Register listeners
    registerListeners();
  }

  /**
   * Creates the main application frame.
   */
  private void createFrame() {
    mainFrame = new JFrame("Calendar");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(900, 600);
    mainFrame.setLocationRelativeTo(null);

    mainPanel = new JPanel(new BorderLayout());
    mainFrame.setContentPane(mainPanel);
  }

  /**
   * Creates all UI components.
   */
  private void createComponents() {
    // Create header panel
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    headerPanel.setBackground(Color.WHITE);

    titleLabel = new JLabel("Calendar");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

    JPanel viewTypePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    viewTypePanel.setBackground(Color.WHITE);

    dayButton = new JButton("Day");
    weekButton = new JButton("Week");
    monthButton = new JButton("Month");

    viewTypePanel.add(dayButton);
    viewTypePanel.add(weekButton);
    viewTypePanel.add(monthButton);

    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(viewTypePanel, BorderLayout.EAST);

    // Create sidebar panel
    sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setPreferredSize(new Dimension(200, 0));
    sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    sidebarPanel.setBackground(Color.WHITE);

    createEventButton = new JButton("+ Create Event");
    createEventButton.setHorizontalAlignment(SwingConstants.LEFT);
    createEventButton.setFont(new Font("Arial", Font.PLAIN, 14));
    createEventButton.setFocusPainted(false);

    createCalendarButton = new JButton("+ Create Calendar");
    createCalendarButton.setHorizontalAlignment(SwingConstants.LEFT);
    createCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
    createCalendarButton.setFocusPainted(false);

    JPanel createEventPanel = new JPanel(new BorderLayout());
    createEventPanel.setBackground(Color.WHITE);
    createEventPanel.add(createEventButton, BorderLayout.WEST);
    createEventPanel.setMaximumSize(new Dimension(180, 40));
    createEventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel createCalendarPanel = new JPanel(new BorderLayout());
    createCalendarPanel.setBackground(Color.WHITE);
    createCalendarPanel.add(createCalendarButton, BorderLayout.WEST);
    createCalendarPanel.setMaximumSize(new Dimension(180, 40));
    createCalendarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel calendarHeaderPanel = new JPanel(new BorderLayout());
    calendarHeaderPanel.setBackground(Color.WHITE);
    calendarHeaderPanel.setMaximumSize(new Dimension(180, 30));
    calendarHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel calendarLabel = new JLabel("My Calendars");
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 14));

    JButton expandButton = new JButton("^");
    expandButton.setPreferredSize(new Dimension(20, 20));
    expandButton.setBorderPainted(false);
    expandButton.setContentAreaFilled(false);
    expandButton.setFocusPainted(false);

    calendarHeaderPanel.add(calendarLabel, BorderLayout.WEST);
    calendarHeaderPanel.add(expandButton, BorderLayout.EAST);

    calendarListPanel = new JPanel();
    calendarListPanel.setLayout(new BoxLayout(calendarListPanel, BoxLayout.Y_AXIS));
    calendarListPanel.setBackground(Color.WHITE);
    calendarListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    calendarGroup = new ButtonGroup();

    // Add sidebar components
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    sidebarPanel.add(createEventPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    sidebarPanel.add(createCalendarPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    sidebarPanel.add(calendarHeaderPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    sidebarPanel.add(calendarListPanel);

    // Add expand/collapse functionality
    expandButton.addActionListener(e -> {
      boolean isVisible = calendarListPanel.isVisible();
      calendarListPanel.setVisible(!isVisible);
      expandButton.setText(isVisible ? "v" : "^");
    });

    // Create main content panel
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    contentPanel.setBackground(Color.WHITE);

    navigationPanel = new JPanel(new BorderLayout());
    navigationPanel.setBackground(Color.WHITE);
    navigationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    prevButton = new JButton("<");
    nextButton = new JButton(">");

    dateLabel = new JLabel(formatDateForView(currentDate, currentViewType));
    dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
    dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

    JPanel navButtonPanel = new JPanel(new FlowLayout());
    navButtonPanel.setBackground(Color.WHITE);
    navButtonPanel.add(prevButton);
    navButtonPanel.add(nextButton);

    navigationPanel.add(dateLabel, BorderLayout.CENTER);
    navigationPanel.add(navButtonPanel, BorderLayout.EAST);

    calendarPanel = new JPanel();
    calendarPanel.setBackground(Color.WHITE);

    contentPanel.add(navigationPanel, BorderLayout.NORTH);
    contentPanel.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);

    // Create details panel
    detailsPanel = new JPanel();
    detailsPanel.setLayout(new BorderLayout());
    detailsPanel.setPreferredSize(new Dimension(250, 0));
    detailsPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    detailsPanel.setBackground(Color.WHITE);

    detailsHeaderPanel = new JPanel(new BorderLayout());
    detailsHeaderPanel.setBackground(Color.WHITE);

    detailsDateLabel = new JLabel("Events");
    detailsDateLabel.setFont(new Font("Arial", Font.BOLD, 16));

    closeDetailsButton = new JButton("×");
    closeDetailsButton.setFont(new Font("Arial", Font.BOLD, 18));
    closeDetailsButton.setBorderPainted(false);
    closeDetailsButton.setContentAreaFilled(false);
    closeDetailsButton.setFocusPainted(false);

    detailsHeaderPanel.add(detailsDateLabel, BorderLayout.WEST);
    detailsHeaderPanel.add(closeDetailsButton, BorderLayout.EAST);

    detailsContentPanel = new JPanel();
    detailsContentPanel.setLayout(new BoxLayout(detailsContentPanel, BoxLayout.Y_AXIS));
    detailsContentPanel.setBackground(Color.WHITE);

    detailsPanel.add(detailsHeaderPanel, BorderLayout.NORTH);
    detailsPanel.add(new JScrollPane(detailsContentPanel), BorderLayout.CENTER);

    // Hide details panel initially
    detailsPanel.setVisible(false);
  }

  /**
   * Sets up default calendars for demonstration.
   */
  private void setupDefaultCalendars() {
    // Add default calendars
    calendarNames.add("Default");
    calendarNames.add("Personal");
    calendarNames.add("Work");

    // Set default calendar colors
    calendarColors.put("Default", new Color(0, 120, 200, 150));
    calendarColors.put("Personal", new Color(200, 0, 120, 150));
    calendarColors.put("Work", new Color(0, 200, 0, 150));
  }

  /**
   * Assembles all UI components.
   */
  private void assembleUI() {
    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(detailsPanel, BorderLayout.EAST);

    // Set up initial calendar data
    updateCalendarList(calendarNames);

    // Initialize with month view
    updateCalendarView();
  }

  /**
   * Registers action listeners with the components.
   */
  private void registerListeners() {
    // View type listeners
    dayButton.setActionCommand("day");
    weekButton.setActionCommand("week");
    monthButton.setActionCommand("month");

    ActionListener viewTypeListener = e -> {
      String viewType = e.getActionCommand();
      changeViewType(viewType);
    };

    dayButton.addActionListener(viewTypeListener);
    weekButton.addActionListener(viewTypeListener);
    monthButton.addActionListener(viewTypeListener);

    // Create event button listener
    createEventButton.addActionListener(e -> showCreateEventForm());

    // Create calendar button listener
    createCalendarButton.addActionListener(e -> showCreateCalendarForm());

    // Navigation button listeners
    prevButton.addActionListener(e -> navigateToPrevious());
    nextButton.addActionListener(e -> navigateToNext());

    // Close details panel listener
    closeDetailsButton.addActionListener(e -> detailsPanel.setVisible(false));
  }

  @Override
  public void display() {
    mainFrame.setVisible(true);

    // Load initial events
    LocalDate today = LocalDate.now();
    String formattedDate = today.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    controller.loadEvents(formattedDate, formattedDate, currentViewType);
  }

  @Override
  public void updateEvents(List<PrintEventsResponseDTO> events) {
    this.currentEvents = events;
    updateCalendarView();

    // If the details panel is visible, update it too
    if (detailsPanel.isVisible()) {
      updateDetailsPanel(events);
    }
  }

  @Override
  public void updateCalendarList(List<String> calendarNames) {
    this.calendarNames = calendarNames;

    // Generate colors for any new calendars
    for (String calendarName : calendarNames) {
      if (!calendarColors.containsKey(calendarName)) {
        calendarColors.put(calendarName, generateRandomColor());
      }
    }

    refreshCalendarList();
  }

  /**
   * Refreshes the calendar list in the sidebar.
   */
  private void refreshCalendarList() {
    calendarListPanel.removeAll();
    calendarGroup = new ButtonGroup();

    for (String calendarName : calendarNames) {
      JRadioButton radioButton = new JRadioButton(calendarName);
      radioButton.setBackground(Color.WHITE);
      radioButton.setSelected(calendarName.equals(activeCalendar));
      radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
      radioButton.setFont(new Font("Arial", Font.PLAIN, 13));

      // Add color indicator next to calendar name
      JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      radioPanel.setBackground(Color.WHITE);

      JPanel colorIndicator = new JPanel();
      colorIndicator.setPreferredSize(new Dimension(12, 12));
      colorIndicator.setBackground(calendarColors.getOrDefault(calendarName, Color.GRAY));
      colorIndicator.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

      radioPanel.add(colorIndicator);
      radioPanel.add(Box.createRigidArea(new Dimension(5, 0)));
      radioPanel.add(radioButton);

      calendarGroup.add(radioButton);

      // Store the calendar name with the button for use in the action listener
      radioButton.setActionCommand(calendarName);

      // Add action listener to handle calendar selection
      radioButton.addActionListener(e -> {
        if (radioButton.isSelected()) {
          controller.setActiveCalendar(radioButton.getText());
        }
      });

      calendarListPanel.add(radioPanel);
      calendarListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    calendarListPanel.revalidate();
    calendarListPanel.repaint();
  }

  @Override
  public void setActiveCalendar(String calendarName) {
    this.activeCalendar = calendarName;

    // Update radio button selection
    for (Component comp : calendarListPanel.getComponents()) {
      if (comp instanceof JPanel) {
        JPanel panel = (JPanel) comp;
        for (Component panelComp : panel.getComponents()) {
          if (panelComp instanceof JRadioButton) {
            JRadioButton rb = (JRadioButton) panelComp;
            if (rb.getText().equals(calendarName)) {
              rb.setSelected(true);
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public void showConfirmation(String message) {
    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showError(String errorMessage) {
    JOptionPane.showMessageDialog(mainFrame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void showStatus(String dateTime, String status) {
    // Show status in a dialog
    String message = "Status for " + dateTime + ": " + status;
    JOptionPane.showMessageDialog(mainFrame, message, "Availability Status",
        JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showCreateEventForm() {
    // Create and show the event dialog
    EventFormDialog dialog = new EventFormDialog(mainFrame, controller, "Create Event", null);
    dialog.setVisible(true);
  }

  @Override
  public void showEditEventForm(PrintEventsResponseDTO event) {
    // Create and show the event dialog with event data
    EventFormDialog dialog = new EventFormDialog(mainFrame, controller, "Edit Event", event);
    dialog.setVisible(true);
  }

  @Override
  public void showCreateCalendarForm() {
    // Create and show the calendar dialog
    CalendarFormDialog dialog = new CalendarFormDialog(mainFrame, controller);
    dialog.setVisible(true);
  }

  @Override
  public void changeViewType(String viewType) {
    currentViewType = viewType;
    dateLabel.setText(formatDateForView(currentDate, viewType));
    updateCalendarView();

    // Request events for the new date range
    String startDate = currentDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    String endDate = startDate;

    if ("week".equals(viewType)) {
      endDate = currentDate.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    } else if ("month".equals(viewType)) {
      endDate = currentDate.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"
          + "-yyyy"));
    }

    controller.loadEvents(startDate, endDate, viewType);
  }

  /**
   * Updates the calendar view based on the current view type.
   */
  private void updateCalendarView() {
    calendarPanel.removeAll();

    if ("day".equals(currentViewType)) {
      setupDayView();
    } else if ("week".equals(currentViewType)) {
      setupWeekView();
    } else { // month
      setupMonthView();
    }

    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  /**
   * Formats a date according to the current view type.
   *
   * @param date     The date to format
   * @param viewType The view type
   * @return Formatted date string
   */
  private String formatDateForView(LocalDate date, String viewType) {
    if ("day".equals(viewType)) {
      return date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    } else if ("week".equals(viewType)) {
      LocalDate endOfWeek = date.plusDays(6);
      return date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) +
          " to " +
          endOfWeek.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    } else { // month
      return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }
  }

  /**
   * Navigates to the previous time period based on view type.
   */
  private void navigateToPrevious() {
    if ("day".equals(currentViewType)) {
      currentDate = currentDate.minusDays(1);
    } else if ("week".equals(currentViewType)) {
      currentDate = currentDate.minusWeeks(1);
    } else { // month
      currentDate = currentDate.minusMonths(1);
    }

    dateLabel.setText(formatDateForView(currentDate, currentViewType));
    updateCalendarView();

    // Request events for the new date range
    String startDate = currentDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    String endDate = startDate;

    if ("week".equals(currentViewType)) {
      endDate = currentDate.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    } else if ("month".equals(currentViewType)) {
      endDate = currentDate.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"
          + "-yyyy"));
    }

    controller.loadEvents(startDate, endDate, currentViewType);
  }

  /**
   * Navigates to the next time period based on view type.
   */
  private void navigateToNext() {
    if ("day".equals(currentViewType)) {
      currentDate = currentDate.plusDays(1);
    } else if ("week".equals(currentViewType)) {
      currentDate = currentDate.plusWeeks(1);
    } else { // month
      currentDate = currentDate.plusMonths(1);
    }

    dateLabel.setText(formatDateForView(currentDate, currentViewType));
    updateCalendarView();

    // Request events for the new date range
    String startDate = currentDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    String endDate = startDate;

    if ("week".equals(currentViewType)) {
      endDate = currentDate.plusDays(6).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    } else if ("month".equals(currentViewType)) {
      endDate = currentDate.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"
          + "-yyyy"));
    }

    controller.loadEvents(startDate, endDate, currentViewType);
  }

  /**
   * Sets up the day view.
   */
  private void setupDayView() {
    calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));

    // Day view shows hourly intervals
    for (int hour = 0; hour < 24; hour++) {
      JPanel hourPanel = new JPanel(new BorderLayout());
      hourPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

      String hourString = String.format("%02d:00", hour);
      JLabel hourLabel = new JLabel(hourString);
      hourLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

      JPanel eventPanel = new JPanel();
      eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));

      // Add any events for this hour
      for (PrintEventsResponseDTO event : currentEvents) {
        // Check if event occurs in this hour
        // (This is simplified - would need parsing of event times)

        // Example: Add event indicator
        JPanel eventIndicator = new JPanel(new BorderLayout());
        eventIndicator.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        eventIndicator.add(new JLabel(event.getEventName()), BorderLayout.CENTER);
        eventPanel.add(eventIndicator);
      }

      hourPanel.add(hourLabel, BorderLayout.WEST);
      hourPanel.add(eventPanel, BorderLayout.CENTER);

      calendarPanel.add(hourPanel);
    }
  }

  /**
   * Sets up the week view.
   */
  private void setupWeekView() {
    calendarPanel.setLayout(new GridLayout(0, 7)); // 7 days

    // Week view shows 7 days with events
    LocalDate weekStart = currentDate;

    for (int day = 0; day < 7; day++) {
      LocalDate date = weekStart.plusDays(day);

      JPanel dayPanel = new JPanel(new BorderLayout());
      dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      // Day header
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(240, 240, 240));

      JLabel dayLabel = new JLabel(date.format(DateTimeFormatter.ofPattern("EEE MM/dd")));
      dayLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
      headerPanel.add(dayLabel, BorderLayout.CENTER);

      // Event list area
      JPanel eventsPanel = new JPanel();
      eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));

      // Add any events for this day
      for (PrintEventsResponseDTO event : currentEvents) {
        // Check if event occurs on this day
        // (This is simplified - would need parsing of event dates)

        // Example: Add event indicator
        JPanel eventIndicator = new JPanel(new BorderLayout());
        eventIndicator.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        eventIndicator.add(new JLabel(event.getEventName()), BorderLayout.CENTER);
        eventsPanel.add(eventIndicator);
      }

      dayPanel.add(headerPanel, BorderLayout.NORTH);
      dayPanel.add(new JScrollPane(eventsPanel), BorderLayout.CENTER);

      // Add click listener to select day
      final LocalDate selectedDay = date;
      dayPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectedDate = selectedDay;
          showEventsForDate(selectedDay);
        }
      });

      calendarPanel.add(dayPanel);
    }
  }

  /**
   * Sets up the month view.
   */
  private void setupMonthView() {
    calendarPanel.setLayout(new GridLayout(0, 7)); // 7 days per week

    // Month view shows a traditional calendar grid
    YearMonth yearMonth = YearMonth.from(currentDate);
    LocalDate firstOfMonth = yearMonth.atDay(1);

    // Add day of week headers
    String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String day : daysOfWeek) {
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(240, 240, 240));
      headerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
      headerPanel.add(dayLabel, BorderLayout.CENTER);

      calendarPanel.add(headerPanel);
    }

    // Get the day of week for the first day (0 = Sunday, 6 = Saturday)
    int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue() % 7;

    // Add empty cells for days before the first of month
    for (int i = 0; i < dayOfWeekValue; i++) {
      JPanel emptyPanel = new JPanel();
      emptyPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      calendarPanel.add(emptyPanel);
    }

    // Add cells for each day of the month
    int daysInMonth = yearMonth.lengthOfMonth();
    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate date = yearMonth.atDay(day);

      JPanel dayPanel = new JPanel(new BorderLayout());
      dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      // Day number
      JLabel dayLabel = new JLabel(String.valueOf(day));
      dayLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

      // Event indicators panel
      JPanel eventsPanel = new JPanel();
      eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));

      // Count events for this day
      int eventCount = 0;
      for (PrintEventsResponseDTO event : currentEvents) {
        // Check if event occurs on this day
        // (This is simplified - would need parsing of event dates)
        eventCount++;
      }

      // Show event count indicator
      if (eventCount > 0) {
        JLabel countLabel = new JLabel(eventCount + " event" + (eventCount > 1 ? "s" : ""));
        countLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        countLabel.setForeground(Color.DARK_GRAY);
        eventsPanel.add(countLabel);
      }

      dayPanel.add(dayLabel, BorderLayout.NORTH);
      dayPanel.add(eventsPanel, BorderLayout.CENTER);

      // Add click listener to select day
      final LocalDate selectedDay = date;
      dayPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectedDate = selectedDay;
          showEventsForDate(selectedDay);
        }
      });

      calendarPanel.add(dayPanel);
    }
  }

  /**
   * Shows events for a specific date in the details panel.
   *
   * @param date The date to show events for
   */
  private void showEventsForDate(LocalDate date) {
    detailsDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")));

    // Request events for the selected date
    String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    controller.loadEvents(formattedDate, formattedDate, "day");

    // Show the details panel
    detailsPanel.setVisible(true);
  }

  /**
   * Updates the events displayed in the details panel.
   *
   * @param events The events to display
   */
  private void updateDetailsPanel(List<PrintEventsResponseDTO> events) {
    detailsContentPanel.removeAll();

    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events scheduled");
      noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      detailsContentPanel.add(noEventsLabel);
    } else {
      for (PrintEventsResponseDTO event : events) {
        // Create an event panel
        JPanel eventPanel = createEventPanel(event);
        eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        detailsContentPanel.add(eventPanel);
        detailsContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      }
    }

    // Add a button to create a new event for this date
    JButton addEventButton = new JButton("+ Add Event");
    addEventButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    addEventButton.addActionListener(e -> showCreateEventForm());

    detailsContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    detailsContentPanel.add(addEventButton);

    detailsContentPanel.revalidate();
    detailsContentPanel.repaint();
  }

  /**
   * Creates a panel to display a single event.
   *
   * @param event The event to display
   * @return A panel containing the event details
   */
  private JPanel createEventPanel(PrintEventsResponseDTO event) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    // Create info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);

    // Time info
    JLabel timeLabel = new JLabel(event.getStartTime() + " - " + event.getEndTime());
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Title
    JLabel titleLabel = new JLabel(event.getEventName());
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Add info to panel
    infoPanel.add(timeLabel);
    infoPanel.add(titleLabel);

    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      JLabel locationLabel = new JLabel(event.getLocation());
      locationLabel.setFont(new Font("Arial", Font.ITALIC, 11));
      locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      infoPanel.add(locationLabel);
    }

    // Edit button
    JButton editButton = new JButton("Edit");
    editButton.setFont(new Font("Arial", Font.PLAIN, 12));
    editButton.addActionListener(e -> showEditEventForm(event));

    // Add components to main panel
    panel.add(infoPanel, BorderLayout.CENTER);
    panel.add(editButton, BorderLayout.EAST);

    return panel;
  }

  /**
   * Generates a random color for new calendars.
   *
   * @return A random color with transparency
   */
  private Color generateRandomColor() {
    int r = random.nextInt(256);
    int g = random.nextInt(256);
    int b = random.nextInt(256);
    return new Color(r, g, b, 150); // With transparency
  }

  /**
   * Dialog for creating or editing an event.
   */
  class EventFormDialog extends JDialog {
    private final PrintEventsResponseDTO eventToEdit;

    // Form fields
    private final JTextField eventNameField;
    private final JTextField startTimeField;
    private final JTextField endTimeField;
    private final JTextField locationField;
    private final JComboBox<String> calendarComboBox;
    private final JCheckBox autoDeclineCheckbox;

    public EventFormDialog(JFrame parent, Features controller,
                           String title, PrintEventsResponseDTO event) {
      super(parent, title, true);
      this.eventToEdit = event;

      setSize(400, 450);
      setLocationRelativeTo(parent);

      // Main panel with padding
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      // Create form panel with GridLayout
      JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

      // Add form fields
      formPanel.add(new JLabel("Event Name:"));
      eventNameField = new JTextField(event != null ? event.getEventName() : "");
      formPanel.add(eventNameField);

      formPanel.add(new JLabel("Start Time:"));
      startTimeField = new JTextField(event != null ? event.getStartTime().toString() : "");
      formPanel.add(startTimeField);

      formPanel.add(new JLabel("End Time:"));
      endTimeField = new JTextField(event != null ? event.getEndTime().toString() : "");
      formPanel.add(endTimeField);

      formPanel.add(new JLabel("Location:"));
      locationField = new JTextField(event != null ? event.getLocation() : "");
      formPanel.add(locationField);

      formPanel.add(new JLabel("Calendar:"));
      calendarComboBox = new JComboBox<>();
      // Add calendar names to the combo box
      for (String name : calendarNames) {
        calendarComboBox.addItem(name);
      }
      calendarComboBox.setSelectedItem(activeCalendar);
      formPanel.add(calendarComboBox);

      formPanel.add(new JLabel("Auto Decline Conflicts:"));
      autoDeclineCheckbox = new JCheckBox();
      formPanel.add(autoDeclineCheckbox);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);

      // Add action listeners
      cancelButton.addActionListener(e -> dispose());

      saveButton.addActionListener(e -> {
        if (event == null) {
          // Create new event
          controller.createEvent(
              eventNameField.getText(),
              startTimeField.getText(),
              endTimeField.getText(),
              "", // recurringDays
              "", // occurrenceCount
              "", // recurrenceEndDate
              "",
              locationField.getText(),
              "", // visibility
              autoDeclineCheckbox.isSelected()
          );
        } else {
          // Edit existing event
          controller.editEvent(
              event.getEventName(),
              event.getStartTime().toString(),
              event.getEndTime().toString(),
              "name", // property to edit
              eventNameField.getText() // new value
          );

          // Would need additional calls to edit other properties
        }

        dispose();
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      add(mainPanel);
    }
  }

  /**
   * Dialog for creating a new calendar.
   */
  class CalendarFormDialog extends JDialog {
    // Form fields
    private final JTextField nameField;
    private final JTextField timezoneField;

    public CalendarFormDialog(JFrame parent, Features controller) {
      super(parent, "Create Calendar", true);

      setSize(350, 200);
      setLocationRelativeTo(parent);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

      formPanel.add(new JLabel("Calendar Name:"));
      nameField = new JTextField();
      formPanel.add(nameField);

      formPanel.add(new JLabel("Timezone:"));
      timezoneField = new JTextField("GMT");
      formPanel.add(timezoneField);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);

      cancelButton.addActionListener(e -> dispose());

      saveButton.addActionListener(e -> {
        String calendarName = nameField.getText().trim();
        String timezone = timezoneField.getText().trim();

        if (calendarName.isEmpty()) {
          JOptionPane.showMessageDialog(this,
              "Please enter a calendar name.",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        controller.createCalendar(calendarName, timezone);
        dispose();
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      add(mainPanel);
    }
  }
}


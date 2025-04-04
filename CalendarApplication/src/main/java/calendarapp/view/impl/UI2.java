package calendarapp.view.impl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main class for Calendar UI application.
 */
public class UI2 {

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
   * Simple class to represent a calendar event
   */
  static class CalendarEvent {
    private String title;
    private String description;
    private String location;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private Color color;
    private String calendarName;

    public CalendarEvent(String title, String description, String location,
                         LocalDate date, String startTime, String endTime,
                         Color color, String calendarName) {
      this.title = title;
      this.description = description;
      this.location = location;
      this.date = date;
      this.startTime = startTime;
      this.endTime = endTime;
      this.color = color;
      this.calendarName = calendarName;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Color getColor() { return color; }
    public String getCalendarName() { return calendarName; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setColor(Color color) { this.color = color; }
    public void setCalendarName(String calendarName) { this.calendarName = calendarName; }
  }

  /**
   * Main application frame for the calendar UI.
   */
  static class CalendarFrame extends JFrame {

    // UI Components
    private JPanel mainPanel;
    private JPanel calendarPanel;
    private JPanel sidebarPanel;
    private JPanel eventDetailsPanel;

    private JButton createButton;
    private JButton createCalendarButton;
    private JPanel calendarListPanel;
    private JButton monthButton;

    private MonthCalendarPanel monthCalendarPanel;

    // Data structures
    private Map<String, Color> calendarColors = new HashMap<>();
    private Map<LocalDate, List<CalendarEvent>> events = new HashMap<>();
    private List<String> calendarNames = new ArrayList<>();
    private String activeCalendar = "Default";

    /**
     * Constructor initializes the UI components.
     */
    public CalendarFrame() {
      super("Calendar");

      // Initialize default calendars
      calendarNames.add("Default");
      calendarNames.add("Personal");
      calendarNames.add("Work");

      // Set default calendar colors
      calendarColors.put("Default", new Color(0, 120, 200, 150));
      calendarColors.put("Personal", new Color(200, 0, 120, 150));
      calendarColors.put("Work", new Color(0, 200, 0, 150));

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
      initializeEventDetailsPanel();

      // Add panels to main panel
      mainPanel.add(sidebarPanel, BorderLayout.WEST);
      mainPanel.add(calendarPanel, BorderLayout.CENTER);
      mainPanel.add(eventDetailsPanel, BorderLayout.EAST);

      // Hide event details panel initially
      eventDetailsPanel.setVisible(false);

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
      createButton = new JButton("+ Create Event");
      createButton.setHorizontalAlignment(SwingConstants.LEFT);
      createButton.setFont(new Font("Arial", Font.PLAIN, 14));
      createButton.setFocusPainted(false);

      // Create Calendar button
      createCalendarButton = new JButton("+ Create Calendar");
      createCalendarButton.setHorizontalAlignment(SwingConstants.LEFT);
      createCalendarButton.setFont(new Font("Arial", Font.PLAIN, 14));
      createCalendarButton.setFocusPainted(false);

      // Use a left-aligned flow layout with no margins for the buttons
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
      buttonPanel.setBackground(Color.WHITE);

      JPanel createButtonPanel = new JPanel(new BorderLayout());
      createButtonPanel.setBackground(Color.WHITE);
      createButtonPanel.add(createButton, BorderLayout.WEST);
      createButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      createButtonPanel.setMaximumSize(new Dimension(180, 40));

      JPanel createCalendarButtonPanel = new JPanel(new BorderLayout());
      createCalendarButtonPanel.setBackground(Color.WHITE);
      createCalendarButtonPanel.add(createCalendarButton, BorderLayout.WEST);
      createCalendarButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      createCalendarButtonPanel.setMaximumSize(new Dimension(180, 40));

      buttonPanel.add(createButtonPanel);
      buttonPanel.add(createCalendarButtonPanel);

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

      // Initialize calendar radio buttons
      refreshCalendarList();

      // Add components to sidebar
      sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      sidebarPanel.add(buttonPanel);
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

      // Add create calendar button functionality
      createCalendarButton.addActionListener(e -> showCreateCalendarDialog());
    }

    /**
     * Refreshes the calendar list in the sidebar.
     */
    private void refreshCalendarList() {
      calendarListPanel.removeAll();

      ButtonGroup calendarGroup = new ButtonGroup(); // Only one calendar active at a time

      for (String calendarName : calendarNames) {
        JRadioButton radioButton = new JRadioButton(calendarName);
        radioButton.setBackground(Color.WHITE);
        radioButton.setSelected(calendarName.equals(activeCalendar));
        radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add a small colored square indicator next to calendar name
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(Color.WHITE);

        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(12, 12));
        colorIndicator.setBackground(calendarColors.getOrDefault(calendarName, Color.GRAY));

        radioPanel.add(colorIndicator);
        radioPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        radioPanel.add(radioButton);

        calendarGroup.add(radioButton);

        // Add action listener to load appropriate calendar with confirmation
        radioButton.addActionListener(e -> {
          JRadioButton source = (JRadioButton)e.getSource();
          String selectedCalendar = calendarName;

          if (source.isSelected() && !selectedCalendar.equals(activeCalendar)) {
            // Show confirmation dialog
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Switch to " + selectedCalendar + " calendar?",
                "Confirm Calendar Switch",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
              // User confirmed, so switch calendar
              activeCalendar = selectedCalendar;
              updateCalendarView();
            } else {
              // User canceled, revert selection to previous calendar
              for (Component comp : calendarListPanel.getComponents()) {
                if (comp instanceof JPanel) {
                  JPanel panel = (JPanel) comp;
                  for (Component panelComp : panel.getComponents()) {
                    if (panelComp instanceof JRadioButton) {
                      JRadioButton rb = (JRadioButton) panelComp;
                      if (rb.getText().equals(activeCalendar)) {
                        rb.setSelected(true);
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
        });

        calendarListPanel.add(radioPanel);
        calendarListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      }

      calendarListPanel.revalidate();
      calendarListPanel.repaint();
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

      monthButton = new JButton("Month");
      monthButton.setPreferredSize(new Dimension(120, 30));
      monthButton.setFont(new Font("Arial", Font.PLAIN, 14));

      headerPanel.add(calendarLabel, BorderLayout.WEST);
      headerPanel.add(monthButton, BorderLayout.EAST);

      // Initialize month calendar panel
      monthCalendarPanel = new MonthCalendarPanel(this);

      // Add components to calendar panel
      calendarPanel.add(headerPanel, BorderLayout.NORTH);
      calendarPanel.add(monthCalendarPanel, BorderLayout.CENTER);
    }

    /**
     * Initializes the event details panel.
     */
    private void initializeEventDetailsPanel() {
      eventDetailsPanel = new JPanel();
      eventDetailsPanel.setLayout(new BorderLayout());
      eventDetailsPanel.setPreferredSize(new Dimension(250, getHeight()));
      eventDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)
      ));
      eventDetailsPanel.setBackground(Color.WHITE);

      // Header with selected date
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(Color.WHITE);

      JLabel dateLabel = new JLabel("Events");
      dateLabel.setFont(new Font("Arial", Font.BOLD, 16));

      JButton closeButton = new JButton("×");
      closeButton.setFont(new Font("Arial", Font.BOLD, 18));
      closeButton.setBorderPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.setFocusPainted(false);
      closeButton.addActionListener(e -> eventDetailsPanel.setVisible(false));

      headerPanel.add(dateLabel, BorderLayout.WEST);
      headerPanel.add(closeButton, BorderLayout.EAST);

      // Create content panel (will be populated dynamically)
      JPanel contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setBackground(Color.WHITE);

      // Add to the event details panel
      eventDetailsPanel.add(headerPanel, BorderLayout.NORTH);
      eventDetailsPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    /**
     * Update the calendar view after calendar or event changes.
     */
    private void updateCalendarView() {
      monthCalendarPanel.refreshCalendar();
    }

    /**
     * Shows the event details for a specific date.
     *
     * @param date The selected date
     */
    public void showEventsForDate(LocalDate date) {
      // Get the content panel and clear it
      JPanel contentPanel = (JPanel) ((JScrollPane) eventDetailsPanel.getComponent(1)).getViewport().getView();
      contentPanel.removeAll();

      // Update header with selected date
      JLabel dateLabel = (JLabel) ((JPanel) eventDetailsPanel.getComponent(0)).getComponent(0);
      dateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")));

      // Get events for the selected date
      List<CalendarEvent> dateEvents = events.getOrDefault(date, new ArrayList<>());

      if (dateEvents.isEmpty()) {
        JLabel noEventsLabel = new JLabel("No events scheduled");
        noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(noEventsLabel);
      } else {
        for (CalendarEvent event : dateEvents) {
          // Create an event panel
          JPanel eventPanel = createEventPanel(event);
          eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
          contentPanel.add(eventPanel);
          contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
      }

      // Add a button to create a new event for this date
      JButton addEventButton = new JButton("+ Add Event");
      addEventButton.setAlignmentX(Component.LEFT_ALIGNMENT);
      addEventButton.addActionListener(e -> showCreateEventDialog(date));

      contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      contentPanel.add(addEventButton);

      // Show the panel
      eventDetailsPanel.setVisible(true);
      contentPanel.revalidate();
      contentPanel.repaint();
    }

    /**
     * Creates a panel for displaying an event in the details view.
     *
     * @param event The calendar event
     * @return A panel containing the event details
     */
    private JPanel createEventPanel(CalendarEvent event) {
      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());
      panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 3, 0, 0, event.getColor()),
          BorderFactory.createEmptyBorder(5, 5, 5, 5)
      ));
      panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

      // Create time and title panel
      JPanel infoPanel = new JPanel();
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
      infoPanel.setBackground(Color.WHITE);

      // Time info
      String timeInfo = event.getStartTime() + " - " + event.getEndTime();
      JLabel timeLabel = new JLabel(timeInfo);
      timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
      timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Title
      JLabel titleLabel = new JLabel(event.getTitle());
      titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
      titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Calendar and location info
      JLabel calendarLabel = new JLabel(event.getCalendarName());
      calendarLabel.setFont(new Font("Arial", Font.PLAIN, 11));
      calendarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Add to info panel
      infoPanel.add(timeLabel);
      infoPanel.add(titleLabel);
      if (event.getLocation() != null && !event.getLocation().isEmpty()) {
        JLabel locationLabel = new JLabel(event.getLocation());
        locationLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(locationLabel);
      }
      infoPanel.add(calendarLabel);

      // Edit button
      JButton editButton = new JButton("Edit");
      editButton.setFont(new Font("Arial", Font.PLAIN, 12));
      editButton.addActionListener(e -> showEditEventDialog(event));

      // Add components to main panel
      panel.add(infoPanel, BorderLayout.CENTER);
      panel.add(editButton, BorderLayout.EAST);

      return panel;
    }

    /**
     * Shows a dialog to create a new event.
     */
    private void showCreateEventDialog() {
      showCreateEventDialog(LocalDate.now());
    }

    /**
     * Shows a dialog to create a new event on a specific date.
     *
     * @param date The date for the new event
     */
    private void showCreateEventDialog(LocalDate date) {
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

      formPanel.add(new JLabel("Date:"));
      JTextField dateField = new JTextField(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      formPanel.add(dateField);

      formPanel.add(new JLabel("Start Time:"));
      JTextField startTimeField = new JTextField("09:00");
      formPanel.add(startTimeField);

      formPanel.add(new JLabel("End Time:"));
      JTextField endTimeField = new JTextField("10:00");
      formPanel.add(endTimeField);

      formPanel.add(new JLabel("Description:"));
      JTextField descriptionField = new JTextField();
      formPanel.add(descriptionField);

      formPanel.add(new JLabel("Location:"));
      JTextField locationField = new JTextField();
      formPanel.add(locationField);

      formPanel.add(new JLabel("Calendar:"));
      JComboBox<String> calendarComboBox = new JComboBox<>(calendarNames.toArray(new String[0]));
      calendarComboBox.setSelectedItem(activeCalendar);
      formPanel.add(calendarComboBox);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);

      // Add action listeners
      cancelButton.addActionListener(e -> createDialog.dispose());

      saveButton.addActionListener(e -> {
        try {
          // Parse the date
          LocalDate eventDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

          // Get selected calendar
          String calendarName = (String) calendarComboBox.getSelectedItem();

          // Create the event
          CalendarEvent newEvent = new CalendarEvent(
              eventNameField.getText(),
              descriptionField.getText(),
              locationField.getText(),
              eventDate,
              startTimeField.getText(),
              endTimeField.getText(),
              calendarColors.get(calendarName),
              calendarName
          );

          // Add to events map
          List<CalendarEvent> dateEvents = events.getOrDefault(eventDate, new ArrayList<>());
          dateEvents.add(newEvent);
          events.put(eventDate, dateEvents);

          // Update the calendar view
          updateCalendarView();

          // Show event details if the date matches the current selection
          if (monthCalendarPanel.getSelectedDate().equals(eventDate)) {
            showEventsForDate(eventDate);
          }

          createDialog.dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(createDialog,
              "Invalid date format. Please use yyyy-MM-dd format.",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
        }
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      createDialog.add(mainPanel);

      createDialog.setVisible(true);
    }

    /**
     * Shows a dialog to edit an existing event.
     *
     * @param event The event to edit
     */
    private void showEditEventDialog(CalendarEvent event) {
      JDialog editDialog = new JDialog(this, "Edit Event", true);
      editDialog.setSize(400, 450);
      editDialog.setLocationRelativeTo(this);

      // Main panel with padding
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      // Create form panel with GridLayout
      JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

      // Add form fields with existing values
      formPanel.add(new JLabel("Event Name:"));
      JTextField eventNameField = new JTextField(event.getTitle());
      formPanel.add(eventNameField);

      formPanel.add(new JLabel("Date:"));
      JTextField dateField = new JTextField(event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      formPanel.add(dateField);

      formPanel.add(new JLabel("Start Time:"));
      JTextField startTimeField = new JTextField(event.getStartTime());
      formPanel.add(startTimeField);

      formPanel.add(new JLabel("End Time:"));
      JTextField endTimeField = new JTextField(event.getEndTime());
      formPanel.add(endTimeField);

      formPanel.add(new JLabel("Description:"));
      JTextField descriptionField = new JTextField(event.getDescription());
      formPanel.add(descriptionField);

      formPanel.add(new JLabel("Location:"));
      JTextField locationField = new JTextField(event.getLocation());
      formPanel.add(locationField);

      formPanel.add(new JLabel("Calendar:"));
      JComboBox<String> calendarComboBox = new JComboBox<>(calendarNames.toArray(new String[0]));
      calendarComboBox.setSelectedItem(event.getCalendarName());
      formPanel.add(calendarComboBox);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton deleteButton = new JButton("Delete");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(deleteButton);
      buttonPanel.add(saveButton);

      // Add action listeners
      cancelButton.addActionListener(e -> editDialog.dispose());

      deleteButton.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(editDialog,
            "Are you sure you want to delete this event?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
          // Remove the event
          List<CalendarEvent> dateEvents = events.get(event.getDate());
          if (dateEvents != null) {
            dateEvents.remove(event);
            if (dateEvents.isEmpty()) {
              events.remove(event.getDate());
            }
          }

          // Update views
          updateCalendarView();
          showEventsForDate(event.getDate());

          editDialog.dispose();
        }
      });

      saveButton.addActionListener(e -> {
        try {
          // Get the original date to remove from old list if date changed
          LocalDate originalDate = event.getDate();

          // Parse the new date
          LocalDate newDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

          // Get selected calendar
          String calendarName = (String) calendarComboBox.getSelectedItem();

          // Update the event
          event.setTitle(eventNameField.getText());
          event.setDescription(descriptionField.getText());
          event.setLocation(locationField.getText());
          event.setDate(newDate);
          event.setStartTime(startTimeField.getText());
          event.setEndTime(endTimeField.getText());
          event.setCalendarName(calendarName);
          event.setColor(calendarColors.get(calendarName));

          // If the date changed, need to move to new date in map
          if (!originalDate.equals(newDate)) {
            // Remove from old date
            List<CalendarEvent> originalDateEvents = events.get(originalDate);
            if (originalDateEvents != null) {
              originalDateEvents.remove(event);
              if (originalDateEvents.isEmpty()) {
                events.remove(originalDate);
              }
            }

            // Add to new date
            List<CalendarEvent> newDateEvents = events.getOrDefault(newDate, new ArrayList<>());
            newDateEvents.add(event);
            events.put(newDate, newDateEvents);
          }

          // Update the calendar view
          updateCalendarView();

          // Refresh event details panel
          showEventsForDate(newDate);

          editDialog.dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(editDialog,
              "Invalid date format. Please use yyyy-MM-dd format.",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
        }
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      editDialog.add(mainPanel);

      editDialog.setVisible(true);
    }

    /**
     * Shows a dialog to create a new calendar.
     */
    private void showCreateCalendarDialog() {
      JDialog createCalendarDialog = new JDialog(this, "Create Calendar", true);
      createCalendarDialog.setSize(350, 200);
      createCalendarDialog.setLocationRelativeTo(this);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

      formPanel.add(new JLabel("Calendar Name:"));
      JTextField nameField = new JTextField();
      formPanel.add(nameField);

      formPanel.add(new JLabel("Calendar Color:"));
      JButton colorButton = new JButton("Select Color");
      colorButton.setBackground(Color.BLUE);
      colorButton.setForeground(Color.WHITE);

      final Color[] selectedColor = {Color.BLUE};

      colorButton.addActionListener(e -> {
        Color color = JColorChooser.showDialog(createCalendarDialog, "Choose Calendar Color", colorButton.getBackground());
        if (color != null) {
          selectedColor[0] = color;
          colorButton.setBackground(color);
          // Set text color based on background brightness for readability
          colorButton.setForeground(getBrightness(color) > 128 ? Color.BLACK : Color.WHITE);
        }
      });

      formPanel.add(colorButton);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton cancelButton = new JButton("Cancel");
      JButton saveButton = new JButton("Save");

      buttonPanel.add(cancelButton);
      buttonPanel.add(saveButton);

      cancelButton.addActionListener(e -> createCalendarDialog.dispose());

      saveButton.addActionListener(e -> {
        String calendarName = nameField.getText().trim();

        if (calendarName.isEmpty()) {
          JOptionPane.showMessageDialog(createCalendarDialog,
              "Please enter a calendar name.",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (calendarNames.contains(calendarName)) {
          JOptionPane.showMessageDialog(createCalendarDialog,
              "A calendar with this name already exists.",
              "Input Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        // Add the new calendar
        calendarNames.add(calendarName);
        calendarColors.put(calendarName, selectedColor[0]);

        // Refresh the calendar list
        refreshCalendarList();

        createCalendarDialog.dispose();
      });

      // Add components to dialog
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      createCalendarDialog.add(mainPanel);

      createCalendarDialog.setVisible(true);
    }

    /**
     * Calculates the brightness of a color (for determining text color).
     *
     * @param color The color to evaluate
     * @return Brightness value (0-255)
     */
    private int getBrightness(Color color) {
      return (int) Math.sqrt(
          color.getRed() * color.getRed() * 0.241 +
              color.getGreen() * color.getGreen() * 0.691 +
              color.getBlue() * color.getBlue() * 0.068
      );
    }
  }

  /**
   * A custom panel for displaying a month view calendar.
   */
  static class MonthCalendarPanel extends JPanel {

    private final Color headerBackground = new Color(240, 240, 240);
    private final Color todayBackground = new Color(220, 240, 255);
    private final Color selectedBackground = new Color(230, 230, 255);
    private final Color dayBackground = Color.WHITE;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    private LocalDate today;
    private CalendarFrame parentFrame;

    /**
     * Constructor initializes the calendar with the current month.
     */
    public MonthCalendarPanel(CalendarFrame parentFrame) {
      this.currentYearMonth = YearMonth.now();
      this.selectedDate = LocalDate.now();
      this.today = LocalDate.now();
      this.parentFrame = parentFrame;

      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder());
      setBackground(Color.WHITE);

      buildCalendar();
    }

    /**
     * Gets the currently selected date.
     *
     * @return The selected date
     */
    public LocalDate getSelectedDate() {
      return selectedDate;
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
      if (date.equals(selectedDate)) {
        dayPanel.setBackground(selectedBackground);
      } else if (date.equals(today)) {
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

      // Add any events for this day
      List<CalendarEvent> dateEvents = parentFrame.events.getOrDefault(date, new ArrayList<>());
      for (CalendarEvent event : dateEvents) {
        JPanel eventIndicator = new JPanel();
        eventIndicator.setBackground(event.getColor());
        eventIndicator.setPreferredSize(new Dimension(eventsPanel.getWidth(), 8));
        eventIndicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        eventsPanel.add(eventIndicator);

        // Add a small gap between event indicators
        eventsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
      }

      dayPanel.add(dayLabel, BorderLayout.NORTH);
      dayPanel.add(eventsPanel, BorderLayout.CENTER);

      // Add click listener to select day
      dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          selectedDate = date;
          parentFrame.showEventsForDate(date);
          buildCalendar(); // Rebuild to update selection
        }
      });

      return dayPanel;
    }

    /**
     * Refreshes the calendar view.
     */
    public void refreshCalendar() {
      buildCalendar();
    }
  }
}
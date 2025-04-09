package calendarapp.view.impl;

import java.awt.*;
import java.io.File;
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
import calendarapp.model.dto.CalendarResponseDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.utils.Constants;
import calendarapp.view.GUIView;

/**
 * A JFrame-based GUI view for displaying and managing calendars and events.
 * Includes navigation, calendar panel, event creation, and sidebar for calendar selection.
 */
public class GUIJFrameView extends JFrame implements GUIView {
  private final Map<String, Color> calendarColors = new HashMap<>();
  private final Random random = new Random();
  // Main panel components
  private final JPanel mainPanel;
  // Header components
  private JPanel headerPanel;
  // Sidebar components
  private JPanel sidebarPanel;
  private JButton createCalendarButton;
  private JPanel calendarListPanel;
  private ButtonGroup calendarGroup;
  private JButton exportButton;
  private JButton importButton;
  // Content components
  private JPanel contentPanel;
  private JPanel navigationPanel;
  private JPanel calendarPanel;
  private JButton prevButton;
  private JButton nextButton;
  private JLabel dateLabel;
  // Details panel components
  private JPanel detailsPanel;
  private JPanel detailsContentPanel;
  private JLabel detailsDateLabel;
  private JButton createEventButton;
  private JButton findEventsButton;
  private List<CalendarResponseDTO> calendarList = new ArrayList<>();
  private String activeCalendar;
  private LocalDate currentDate = LocalDate.now();
  private LocalDate selectedDate = LocalDate.now();

  private Features controller;

  /**
   * Constructs and initializes the main GUI window for the calendar application.
   */
  public GUIJFrameView() {
    super(Constants.APP_TITLE);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 750);
    setLocationRelativeTo(null);
    mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);
    createComponents();
    assembleUI();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features controller) {
    this.controller = controller;
    createCalendarButton.addActionListener(e -> controller.createCalendar());
    prevButton.addActionListener(e -> controller.navigateToPrevious());
    nextButton.addActionListener(e -> controller.navigateToNext());
    createEventButton.addActionListener(e -> controller.createEvent());
    findEventsButton.addActionListener(e -> controller.findEvents());
    exportButton.addActionListener(e -> controller.exportCalendar());
    importButton.addActionListener(e -> controller.importCalendar());
  }

  @Override
  public void updateEvents(List<EventsResponseDTO> events) {
    updateCalendarView();
    updateDetailsPanel(events);
  }

  @Override
  public void updateCalendarList(List<CalendarResponseDTO> calendar) {
    this.calendarList = calendar;
    for (CalendarResponseDTO dto : calendarList) {
      if (!calendarColors.containsKey(dto.getName())) {
        calendarColors.put(dto.getName(), generateRandomColor());
      }
    }
    refreshCalendarList();
  }

  @Override
  public void setActiveCalendar(String calendarName) {
    this.activeCalendar = calendarName;
    for (Component comp : calendarListPanel.getComponents()) {
      if (comp instanceof JPanel) {
        for (Component inner : ((JPanel) comp).getComponents()) {
          if (inner instanceof JRadioButton) {
            JRadioButton rb = (JRadioButton) inner;
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
    JOptionPane.showMessageDialog(this, message, Constants.SUCCESS_DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showError(String errorMessage) {
    JOptionPane.showMessageDialog(this, errorMessage, Constants.ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public Map<String, String> showCreateEventForm() {
    EventDialog dialog = new EventDialog(this, selectedDate);
    detailsDateLabel.setText(formatDateForView(currentDate));
    return dialog.showDialog();
  }

  @Override
  public Map<String, String> showEditEventForm(EventsResponseDTO event) {
    EventDialog dialog = new EventDialog(this, selectedDate, event);
    return dialog.showDialog();
  }

  @Override
  public Map<String, String> showCreateCalendarForm() {
    CalendarFormDialog dialog = new CalendarFormDialog(this);
    return dialog.showDialog();
  }

  @Override
  public LocalDate getCurrentDate() {
    return currentDate;
  }

  @Override
  public void navigateToPrevious(LocalDate date) {
    currentDate = date;
    dateLabel.setText(formatDateForView(currentDate));
    detailsDateLabel.setText(formatDateForView(currentDate));
    updateCalendarView();
  }

  @Override
  public void navigateToNext(LocalDate date) {
    currentDate = date;
    dateLabel.setText(formatDateForView(currentDate));
    detailsDateLabel.setText(formatDateForView(currentDate));
    updateCalendarView();
  }

  @Override
  public Map<String, String> findEvents() {
    FindEventsFormDialog dialog = new FindEventsFormDialog(this);
    detailsDateLabel.setText(Constants.FIND_EVENTS_RESULT);
    return dialog.showDialog();
  }

  @Override
  public Map<String, String> showExportCalendarForm() {
    ExportCalendarFormDialog dialog = new ExportCalendarFormDialog(this);
    return dialog.showDialog();
  }

  @Override
  public Map<String, String> showImportCalendarDialog() {
    ImportCalendarDialog dialog = new ImportCalendarDialog(this);
    return dialog.showDialog();
  }

  /**
   * Creates and configures all UI components including header, sidebar, calendar grid, and details panel.
   */
  private void createComponents() {
    createHeaderPanel();
    createSidebarPanel();
    createContentPanel();
    createDetailsPanel();
  }

  /**
   * Creates the header panel of the application
   */
  private void createHeaderPanel() {
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
    headerPanel.setBackground(Color.WHITE);
    // Additional header components can be added here
  }

  /**
   * Creates the sidebar panel with calendar controls and calendar listing
   */
  private void createSidebarPanel() {
    sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setPreferredSize(new Dimension(300, 0));
    sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    sidebarPanel.setBackground(Color.WHITE);

    JPanel calendarHeaderPanel = new JPanel();
    calendarHeaderPanel.setLayout(new BoxLayout(calendarHeaderPanel, BoxLayout.X_AXIS));
    calendarHeaderPanel.setBackground(Color.WHITE);
    calendarHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel calendarLabel = new JLabel(Constants.MY_CALENDARS_LABEL);
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 18));
    calendarLabel.setPreferredSize(new Dimension(120, 30));

    createCalendarButton = new JButton(Constants.CREATE_BUTTON);
    createCalendarButton.setBorderPainted(false);
    createCalendarButton.setContentAreaFilled(false);
    createCalendarButton.setFocusPainted(false);
    createCalendarButton.setPreferredSize(new Dimension(60, 30));

    calendarHeaderPanel.add(calendarLabel);
    calendarHeaderPanel.add(Box.createHorizontalGlue());
    calendarHeaderPanel.add(createCalendarButton);

    calendarListPanel = new JPanel();
    calendarListPanel.setLayout(new BoxLayout(calendarListPanel, BoxLayout.Y_AXIS));
    calendarListPanel.setBackground(Color.WHITE);
    calendarListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    calendarGroup = new ButtonGroup();

    JPanel calendarFooterPanel = new JPanel();
    calendarFooterPanel.setBackground(Color.WHITE);
    calendarFooterPanel.setLayout(new BoxLayout(calendarFooterPanel, BoxLayout.Y_AXIS));

    exportButton = new JButton(Constants.EXPORT_CALENDAR_BUTTON);
    exportButton.setPreferredSize(new Dimension(200, 30));
    exportButton.setFont(new Font("Arial", Font.BOLD, 14));

    importButton = new JButton(Constants.IMPORT_CALENDAR_BUTTON);
    importButton.setPreferredSize(new Dimension(200, 30));
    importButton.setFont(new Font("Arial", Font.BOLD, 14));

    calendarFooterPanel.add(exportButton);
    calendarFooterPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    calendarFooterPanel.add(importButton);

    sidebarPanel.add(calendarHeaderPanel);
    sidebarPanel.add(calendarListPanel);
    sidebarPanel.add(Box.createVerticalGlue());
    sidebarPanel.add(calendarFooterPanel);
  }

  /**
   * Creates the main content panel with calendar view
   */
  private void createContentPanel() {
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE),
        BorderFactory.createMatteBorder(10, 10, 10, 10, Color.WHITE)));

    // Navigation panel
    navigationPanel = new JPanel();
    navigationPanel.setBackground(Color.WHITE);
    prevButton = new JButton(Constants.PREV_BUTTON);
    nextButton = new JButton(Constants.NEXT_BUTTON);
    dateLabel = new JLabel(formatDateForView(currentDate));
    dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
    dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
    navigationPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, Color.WHITE));
    navigationPanel.add(prevButton);
    navigationPanel.add(dateLabel);
    navigationPanel.add(nextButton);

    // Calendar panel
    calendarPanel = new JPanel();
    calendarPanel.setBackground(Color.WHITE);
    calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    contentPanel.add(navigationPanel, BorderLayout.NORTH);
    contentPanel.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);
  }

  /**
   * Creates the details panel for showing events
   */
  private void createDetailsPanel() {
    detailsPanel = new JPanel(new BorderLayout());
    detailsPanel.setPreferredSize(new Dimension(330, 0));
    detailsPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    detailsPanel.setBackground(Color.WHITE);

    JPanel detailsHeaderContainer = new JPanel();
    detailsHeaderContainer.setLayout(new BoxLayout(detailsHeaderContainer, BoxLayout.Y_AXIS));
    detailsHeaderContainer.setBackground(Color.WHITE);

    JPanel eventButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    eventButtonsPanel.setBackground(Color.WHITE);
    createEventButton = new JButton(Constants.CREATE_EVENT_BUTTON);
    createEventButton.setFont(new Font("Arial", Font.BOLD, 14));
    findEventsButton = new JButton(Constants.FIND_EVENTS_BUTTON);
    findEventsButton.setFont(new Font("Arial", Font.BOLD, 14));
    eventButtonsPanel.add(createEventButton);
    eventButtonsPanel.add(findEventsButton);

    JPanel headerLabelPanel = new JPanel(new BorderLayout());
    headerLabelPanel.setBackground(Color.WHITE);
    detailsDateLabel = new JLabel(formatDateForView(currentDate));
    detailsDateLabel.setFont(new Font("Arial", Font.BOLD, 16));
    detailsDateLabel.setBorder(BorderFactory.createMatteBorder(20, 0, 10, 0, Color.WHITE));
    headerLabelPanel.add(detailsDateLabel, BorderLayout.WEST);

    detailsHeaderContainer.add(eventButtonsPanel, BorderLayout.NORTH);
    detailsHeaderContainer.add(headerLabelPanel, BorderLayout.SOUTH);

    detailsPanel.add(detailsHeaderContainer, BorderLayout.NORTH);

    detailsContentPanel = new JPanel();
    detailsContentPanel.setLayout(new BoxLayout(detailsContentPanel, BoxLayout.Y_AXIS));
    detailsContentPanel.setBackground(Color.WHITE);
    detailsPanel.add(new JScrollPane(detailsContentPanel), BorderLayout.CENTER);
  }

  /**
   * Assembles the main UI layout by placing major panels into the main frame.
   */
  private void assembleUI() {
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(detailsPanel, BorderLayout.EAST);
    updateCalendarList(calendarList);
    updateCalendarView();
  }

  /**
   * Rebuilds the list of calendar radio buttons in the sidebar and updates the calendar view.
   */
  private void refreshCalendarList() {
    calendarListPanel.removeAll();
    calendarGroup = new ButtonGroup();
    for (CalendarResponseDTO calendar : calendarList) {
      String name = calendar.getName();
      JRadioButton radioButton = new JRadioButton(name + "\t(" + calendar.getZoneId().getId() +
          ")");
      radioButton.setBackground(Color.WHITE);
      radioButton.setSelected(name.equals(activeCalendar));
      radioButton.setFont(new Font("Arial", Font.PLAIN, 13));
      JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      radioPanel.setBackground(Color.WHITE);
      JPanel colorIndicator = new JPanel();
      colorIndicator.setPreferredSize(new Dimension(12, 12));
      colorIndicator.setBackground(calendarColors.getOrDefault(name, Color.GRAY));
      colorIndicator.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
      radioPanel.add(colorIndicator);
      radioPanel.add(Box.createRigidArea(new Dimension(5, 0)));
      radioPanel.add(radioButton);
      radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
          radioPanel.getPreferredSize().height));
      calendarGroup.add(radioButton);
      radioButton.setActionCommand(name);
      radioButton.addActionListener(e -> {
        if (radioButton.isSelected()) {
          int response = JOptionPane.showConfirmDialog(
              GUIJFrameView.this,
              Constants.CALENDAR_CHANGE_MESSAGE + radioButton.getText() + "\"?",
              Constants.CALENDAR_CHANGE_TITLE,
              JOptionPane.YES_NO_OPTION);
          if (response == JOptionPane.YES_OPTION) {
            controller.setActiveCalendar(radioButton.getText().split("\t")[0]);
          } else {
            setActiveCalendar(activeCalendar);
          }
        }
      });
      calendarListPanel.add(radioPanel);
    }
    calendarListPanel.revalidate();
    calendarListPanel.repaint();
    updateCalendarView();
  }

  /**
   * Refreshes the calendar view by clearing and redrawing the calendar grid.
   */
  private void updateCalendarView() {
    calendarPanel.removeAll();
    setupMonthView();
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  /**
   * Formats the given date into a display string for UI headers.
   *
   * @param date the date to format
   * @return formatted date string
   */
  private String formatDateForView(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern(Constants.MONTH_YEAR_FORMAT));
  }

  /**
   * Builds the visual calendar grid for the current month, including day headers and clickable day buttons.
   */
  private void setupMonthView() {
    calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));
    calendarPanel.setBackground(calendarColors.getOrDefault(activeCalendar, Color.GRAY));
    YearMonth yearMonth = YearMonth.from(currentDate);
    LocalDate firstOfMonth = yearMonth.atDay(1);

    for (String day : Constants.DAYS_OF_WEEK) {
      JPanel headerCell = new JPanel(new BorderLayout());
      headerCell.setBackground(Color.LIGHT_GRAY);
      headerCell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
      headerCell.add(dayLabel, BorderLayout.CENTER);
      calendarPanel.add(headerCell);
    }

    int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue() % 7;
    for (int i = 0; i < dayOfWeekValue; i++) {
      JPanel emptyCell = new JPanel();
      emptyCell.setBackground(calendarColors.getOrDefault(activeCalendar, Color.GRAY));
      calendarPanel.add(emptyCell);
    }

    int daysInMonth = yearMonth.lengthOfMonth();
    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate date = yearMonth.atDay(day);
      // Create a JButton for each day cell
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.setBackground(Color.WHITE);
      dayButton.setOpaque(true);
      dayButton.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(Color.DARK_GRAY),
          new EmptyBorder(3, 3, 3, 3)
      ));
      final LocalDate selDay = date;
      dayButton.addActionListener(e -> {
        selectedDate = selDay;
        detailsDateLabel.setText(selDay.format(DateTimeFormatter.ofPattern(Constants.DAY_MONTH_FORMAT)));
        String formattedDate = selDay.format(DateTimeFormatter.ofPattern(Constants.ISO_DATE_FORMAT));
        controller.loadEvents(null, null, formattedDate);
      });
      calendarPanel.add(dayButton);
    }
  }

  /**
   * Updates the event details panel with the list of events for the selected date.
   *
   * @param events the list of events to display
   */
  private void updateDetailsPanel(List<EventsResponseDTO> events) {
    detailsContentPanel.removeAll();
    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel(Constants.NO_EVENTS_LABEL);
      noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      detailsContentPanel.add(noEventsLabel);
    } else {
      for (EventsResponseDTO event : events) {
        JPanel eventPanel = createEventPanel(event);
        eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        detailsContentPanel.add(eventPanel);
        detailsContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      }
    }
    detailsContentPanel.revalidate();
    detailsContentPanel.repaint();
  }

  /**
   * Creates a panel displaying a single event's details along with an edit button.
   *
   * @param event the event to render in the panel
   * @return a JPanel representing the event
   */
  private JPanel createEventPanel(EventsResponseDTO event) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);
    JLabel timeLabel =
        new JLabel(formatter.format(event.getStartTime()) + " to " + formatter.format(event.getEndTime()));
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    JLabel titleLabel = new JLabel(event.getEventName());
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    infoPanel.add(titleLabel);
    infoPanel.add(timeLabel);
    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      JLabel locationLabel = new JLabel(Constants.LOCATION_LABEL + event.getLocation());
      locationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
      infoPanel.add(locationLabel);
    }
    JButton editButton = new JButton(Constants.EDIT_BUTTON);
    editButton.setFont(new Font("Arial", Font.PLAIN, 12));
    editButton.addActionListener(e -> controller.editEvent(event));
    panel.add(infoPanel, BorderLayout.CENTER);
    panel.add(editButton, BorderLayout.EAST);
    return panel;
  }

  /**
   * Generates a random soft color used to visually identify calendars.
   *
   * @return a new Color instance
   */
  private Color generateRandomColor() {
    return new Color(180 + random.nextInt(51), 180 + random.nextInt(51),
        180 + random.nextInt(51));
  }
}
package calendarapp.view.impl;

import java.awt.*;
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

public class GUIJFrameView extends JFrame implements GUIView {
  // Main panel components
  private JPanel mainPanel;

  // Header components
  private JPanel headerPanel;
  private JLabel titleLabel;

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

  // Data structures and state
  private List<String> calendarNames = new ArrayList<>();
  private final Map<String, Color> calendarColors = new HashMap<>();
  private String activeCalendar = "Personal";
  private String currentViewType = "month"; // Default view
  private LocalDate currentDate = LocalDate.now();
  private LocalDate selectedDate = LocalDate.now();
  private final Random random = new Random();

  // Features instance provided by the controller
  private Features controller;

  public GUIJFrameView() {
    super("Calendar");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(900, 600);
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

    // Register create buttons
    createEventButton.addActionListener(e -> controller.showCreateEventForm());
    createCalendarButton.addActionListener(e -> controller.showCreateCalendarForm());

    // Register navigation buttons
    prevButton.addActionListener(e -> controller.navigateToPrevious());
    nextButton.addActionListener(e -> controller.navigateToNext());

    // Close details panel button
    closeDetailsButton.addActionListener(e -> detailsPanel.setVisible(false));
  }

  private void createComponents() {
    // Header panel
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    headerPanel.setBackground(Color.WHITE);
    titleLabel = new JLabel("Calendar");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    JPanel viewTypePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    viewTypePanel.setBackground(Color.WHITE);
    // (Additional view type buttons could be added here if needed.)
    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(viewTypePanel, BorderLayout.EAST);

    // Sidebar panel
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
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    sidebarPanel.add(createCalendarPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    sidebarPanel.add(createEventPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    sidebarPanel.add(calendarHeaderPanel);
    sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    sidebarPanel.add(calendarListPanel);
    expandButton.addActionListener(e -> {
      boolean isVisible = calendarListPanel.isVisible();
      calendarListPanel.setVisible(!isVisible);
      expandButton.setText(isVisible ? "v" : "^");
    });

    // Content panel
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    contentPanel.setBackground(Color.WHITE);
    navigationPanel = new JPanel(new BorderLayout());
    navigationPanel.setBackground(Color.WHITE);
    navigationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    prevButton = new JButton("<");
    nextButton = new JButton(">");
    dateLabel = new JLabel(formatDateForView(currentDate));
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

    // Details panel
    detailsPanel = new JPanel(new BorderLayout());
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
    detailsPanel.setVisible(false);
  }

  private void assembleUI() {
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(detailsPanel, BorderLayout.EAST);
    updateCalendarList(calendarNames);

    JPanel navigationWrapper = new JPanel(new BorderLayout());
    navigationWrapper.setBackground(calendarColors.getOrDefault(activeCalendar, Color.GRAY));
    navigationWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
    navigationWrapper.add(navigationPanel, BorderLayout.CENTER);
    contentPanel.add(navigationWrapper, BorderLayout.NORTH);

    JScrollPane calendarScrollPane = new JScrollPane(calendarPanel);
    contentPanel.add(calendarScrollPane, BorderLayout.CENTER);
    updateCalendarView();
  }


  @Override
  public void updateEvents(List<PrintEventsResponseDTO> events) {
    updateCalendarView();
    if (detailsPanel.isVisible()) {
      updateDetailsPanel(events);
    }
  }

  @Override
  public void updateCalendarList(List<String> calendarNames) {
    this.calendarNames = calendarNames;
    for (String name : calendarNames) {
      if (!calendarColors.containsKey(name)) {
        calendarColors.put(name, generateRandomColor());
      }
    }
    refreshCalendarList();
  }

  private void refreshCalendarList() {
    calendarListPanel.removeAll();
    calendarGroup = new ButtonGroup();
    for (String name : calendarNames) {
      JRadioButton radioButton = new JRadioButton(name);
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
      radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, radioPanel.getPreferredSize().height));
      calendarGroup.add(radioButton);
      radioButton.setActionCommand(name);
      radioButton.addActionListener(e -> {
        if (radioButton.isSelected()) {
          controller.setActiveCalendar(radioButton.getText());
        }
      });
      calendarListPanel.add(radioPanel);
    }
    calendarListPanel.revalidate();
    calendarListPanel.repaint();
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
    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showError(String errorMessage) {
    JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void showStatus(String dateTime, String status) {
    String message = "Status for " + dateTime + ": " + status;
    JOptionPane.showMessageDialog(this, message, "Availability Status",
        JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showCreateEventForm() {
    // Use the separate EventFormDialog class
    EventFormDialog dialog = new EventFormDialog(this, controller, "Create Event", null,
        selectedDate);
    dialog.setVisible(true);
  }

  @Override
  public void showEditEventForm(PrintEventsResponseDTO event) {
    EventFormDialog dialog = new EventFormDialog(this, controller, "Edit Event", event,
        selectedDate);
    dialog.setVisible(true);
  }

  @Override
  public void showCreateCalendarForm() {
    // Use the separate CalendarFormDialog class
    CalendarFormDialog dialog = new CalendarFormDialog(this, controller);
    updateCalendarView();
    dialog.setVisible(true);
  }

  private void updateCalendarView() {
    calendarPanel.removeAll();
    setupMonthView();
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  private String formatDateForView(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
  }

  @Override
  public void navigateToPrevious() {
    currentDate = currentDate.minusMonths(1);
    dateLabel.setText(formatDateForView(currentDate));
    updateCalendarView();
    String startDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String endDate = currentDate.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    controller.loadEvents(startDate, endDate);
  }

  @Override
  public void navigateToNext() {
    currentDate = currentDate.plusMonths(1);
    dateLabel.setText(formatDateForView(currentDate));
    updateCalendarView();
    String startDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String endDate = currentDate.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    controller.loadEvents(startDate, endDate);
  }

  private void setupMonthView() {
    // Set the background color based on the active calendar.
    calendarPanel.setBackground(Color.WHITE);
    calendarPanel.setLayout(new GridLayout(0, 7));
    YearMonth yearMonth = YearMonth.from(currentDate);
    LocalDate firstOfMonth = yearMonth.atDay(1);
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
    int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue() % 7;
    for (int i = 0; i < dayOfWeekValue; i++) {
      JPanel emptyPanel = new JPanel();
      emptyPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      calendarPanel.add(emptyPanel);
    }
    int daysInMonth = yearMonth.lengthOfMonth();
    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate date = yearMonth.atDay(day);
      JPanel dayPanel = new JPanel(new BorderLayout());
      dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      JLabel dayLabel = new JLabel(String.valueOf(day));
      dayLabel.setBorder(new EmptyBorder(3, 3, 3, 3));
      JPanel eventsPanel = new JPanel();
      eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
      dayPanel.add(dayLabel, BorderLayout.NORTH);
      dayPanel.add(eventsPanel, BorderLayout.CENTER);
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

  private void showEventsForDate(LocalDate date) {
    detailsDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")));
    String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    controller.loadEvents(formattedDate, formattedDate);
    detailsPanel.setVisible(true);
  }

  private void updateDetailsPanel(List<PrintEventsResponseDTO> events) {
    detailsContentPanel.removeAll();
    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events scheduled");
      noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      detailsContentPanel.add(noEventsLabel);
    } else {
      for (PrintEventsResponseDTO event : events) {
        JPanel eventPanel = createEventPanel(event);
        eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        detailsContentPanel.add(eventPanel);
        detailsContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      }
    }
    JButton addEventButton = new JButton("+ Add Event");
    addEventButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    addEventButton.addActionListener(e -> showCreateEventForm());
    detailsContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    detailsContentPanel.add(addEventButton);
    detailsContentPanel.revalidate();
    detailsContentPanel.repaint();
  }

  private JPanel createEventPanel(PrintEventsResponseDTO event) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    JLabel timeLabel = new JLabel(event.getStartTime() + " - " + event.getEndTime());
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    JLabel titleLabel = new JLabel(event.getEventName());
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    infoPanel.add(timeLabel);
    infoPanel.add(titleLabel);
    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      JLabel locationLabel = new JLabel(event.getLocation());
      locationLabel.setFont(new Font("Arial", Font.ITALIC, 11));
      infoPanel.add(locationLabel);
    }
    JButton editButton = new JButton("Edit");
    editButton.setFont(new Font("Arial", Font.PLAIN, 12));
    editButton.addActionListener(e -> showEditEventForm(event));
    panel.add(infoPanel, BorderLayout.CENTER);
    panel.add(editButton, BorderLayout.EAST);
    return panel;
  }

  private Color generateRandomColor() {
    return new Color(random.nextInt(256), random.nextInt(256),
        random.nextInt(256), 150);
  }

  @Override
  public LocalDate getCurrentDate() {
    return currentDate;
  }

  @Override
  public String getCurrentViewType() {
    return currentViewType;
  }
}

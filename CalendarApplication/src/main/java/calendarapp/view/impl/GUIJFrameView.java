package calendarapp.view.impl;

import java.awt.*;
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
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

public class GUIJFrameView extends JFrame implements GUIView {
  private final Map<String, Color> calendarColors = new HashMap<>();
  private final Random random = new Random();
  // Main panel components
  private JPanel mainPanel;
  // Header components
  private JPanel headerPanel;
  // Sidebar components
  private JPanel sidebarPanel;
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
  private JPanel detailsContentPanel;
  private JLabel detailsDateLabel;
  private JButton createEventButton;
  private JButton findEventsButton;
  // Data structures and state
  private List<String> calendarNames = new ArrayList<>();
  private String activeCalendar;
  private LocalDate currentDate = LocalDate.now();
  private LocalDate selectedDate = LocalDate.now();

  private Features controller;

  public GUIJFrameView() {
    super("Calendar");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 700);
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
  }

  @Override
  public void updateEvents(List<EventsResponseDTO> events) {
    updateCalendarView();
    updateDetailsPanel(events);
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
  public Map<String, String> showCreateEventForm() {
    CreateEventDialog dialog = new CreateEventDialog(this, selectedDate);
    return dialog.showDialog();
  }

  @Override
  public void showEditEventForm(EventsResponseDTO event) {
//    EventFormDialog dialog = new EventFormDialog(this, controller, "Edit Event", event,
//        selectedDate);
//    dialog.setVisible(true);
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
    detailsDateLabel.setText("Find Events Result");
    return dialog.showDialog();
  }

  private void createComponents() {
    // Header panel
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
    headerPanel.setBackground(Color.WHITE);
    // (Additional header components can be added here.)

    // Sidebar panel
    sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setPreferredSize(new Dimension(220, 0));
    sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    sidebarPanel.setBackground(Color.WHITE);

    JPanel calendarHeaderPanel = new JPanel();
    calendarHeaderPanel.setLayout(new BoxLayout(calendarHeaderPanel, BoxLayout.X_AXIS));
    calendarHeaderPanel.setBackground(Color.WHITE);
    calendarHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    JLabel calendarLabel = new JLabel("My Calendars");
    calendarLabel.setFont(new Font("Arial", Font.BOLD, 18));
    calendarLabel.setPreferredSize(new Dimension(120, 30));

    createCalendarButton = new JButton("+");
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
    sidebarPanel.add(calendarHeaderPanel);
    sidebarPanel.add(calendarListPanel);

    // Content panel
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE),
        BorderFactory.createMatteBorder(10, 10, 10, 10, Color.WHITE)));
    navigationPanel = new JPanel();
    navigationPanel.setBackground(Color.WHITE);
    prevButton = new JButton("<");
    nextButton = new JButton(">");
    dateLabel = new JLabel(formatDateForView(currentDate));
    dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
    dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
    navigationPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, Color.WHITE));
    navigationPanel.add(prevButton);
    navigationPanel.add(dateLabel);
    navigationPanel.add(nextButton);
    calendarPanel = new JPanel();
    calendarPanel.setBackground(Color.WHITE);
    // Add some padding around the calendar grid.
    calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    contentPanel.add(navigationPanel, BorderLayout.NORTH);
    contentPanel.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);

    // Details panel
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
    createEventButton = new JButton("Create Event");
    createEventButton.setFont(new Font("Arial", Font.BOLD, 14));
    findEventsButton = new JButton("Find Events");
    findEventsButton.setFont(new Font("Arial", Font.BOLD, 14));
    eventButtonsPanel.add(createEventButton);
    eventButtonsPanel.add(findEventsButton);

    JPanel headerLabelPanel = new JPanel(new BorderLayout());
    headerLabelPanel.setBackground(Color.WHITE);
    detailsDateLabel = new JLabel("Events");
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

  private void assembleUI() {
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(detailsPanel, BorderLayout.EAST);
    updateCalendarList(calendarNames);
    updateCalendarView();
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
      radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
          radioPanel.getPreferredSize().height));
      calendarGroup.add(radioButton);
      radioButton.setActionCommand(name);
      radioButton.addActionListener(e -> {
        if (radioButton.isSelected()) {
          int response = JOptionPane.showConfirmDialog(
              GUIJFrameView.this,
              "Do you want to change the calendar to \"" + radioButton.getText() + "\"?",
              "Confirm Calendar Change",
              JOptionPane.YES_NO_OPTION);
          if (response == JOptionPane.YES_OPTION) {
            controller.setActiveCalendar(radioButton.getText());
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

  private void updateCalendarView() {
    calendarPanel.removeAll();
    setupMonthView();
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  private String formatDateForView(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
  }

  private void setupMonthView() {
    calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));
    calendarPanel.setBackground(calendarColors.getOrDefault(activeCalendar, Color.GRAY));
    YearMonth yearMonth = YearMonth.from(currentDate);
    LocalDate firstOfMonth = yearMonth.atDay(1);
    String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    for (String day : daysOfWeek) {
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
        detailsDateLabel.setText(selDay.format(DateTimeFormatter.ofPattern("EEEE, MMM d")));
        String formattedDate = selDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        controller.loadEvents(null, null, formattedDate);
      });
      calendarPanel.add(dayButton);
    }
  }

  private void updateDetailsPanel(List<EventsResponseDTO> events) {
    detailsContentPanel.removeAll();
    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events found.");
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

  private JPanel createEventPanel(EventsResponseDTO event) {
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
    return new Color(180 + random.nextInt(51), 180 + random.nextInt(51),
        180 + random.nextInt(51));
  }
}

package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import view.IViewEvent;
import view.IViewRecurringEvent;
import view.IViewRecurringSequenceEvent;
import view.IViewRecurringUntilEvent;
import view.ViewRecurringSequenceEvent;
import view.ViewRecurringUntilEvent;

/**
 * This is an implentation of the events list panel which lists events
 * on a given date. This component allows selection of specific events
 * which render dialogs that enable editing.
 */
public class EventsListPanel extends AbstractComponent implements IEventsListPanel {

  private List<IViewEvent> events;
  private ActionListener featureActionListener;
  private IEventForm<IViewEvent> singleEventForm;
  private IRecurringEventForm recurringEventForm;
  private String selectedEventId;

  /**
   * A constructor for an events list panel
   * instance which accepts a feature action
   * listener used to send fefature action
   * events to the controller.
   * @param featureActionListener the feature action listener.
   */
  public EventsListPanel(
      ActionListener featureActionListener
  ) {
    this.featureActionListener = featureActionListener;
    this.events = new ArrayList<IViewEvent>();
    this.setBackground(Color.LIGHT_GRAY);
  }

  @Override
  protected void render() {
    if (this.events.isEmpty()) {
      this.setLayout(new GridBagLayout());
      this.add(new JLabel("No Events Today"));
    }
    else {
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.setMinimumSize(new Dimension(this.getParent().getWidth(), this.getParent().getHeight()));
      for (IViewEvent event: this.events) {
        String eventLabel = event.getSubject();
        if (event.isAllDayEvent() != null && event.isAllDayEvent()) {
          eventLabel += ": All Day";
        }
        else {
          eventLabel += " " + event.getStartTimeString()  + "-" + event.getEndTimeString();
        }
        JButton eventButton = new JButton(eventLabel);
        eventButton.setBackground(Color.WHITE);
        eventButton.setMinimumSize(new Dimension(this.getParent().getWidth(), 45));
        eventButton.setMaximumSize(new Dimension(this.getParent().getWidth(), 45));
        this.add(eventButton);
        eventButton.addActionListener(e -> {
          this.showViewEventFrame(event);
        });
      }
    }
  }

  private void showViewEventFrame(IViewEvent event) {
    selectedEventId = event.getId();
    JFrame viewEventFrame = new JFrame(event.getSubject());

    if (event instanceof IViewRecurringEvent) {
      viewEventFrame.setSize(500, 500);
      this.renderRecurringEventView(event);
      viewEventFrame.add((JPanel) recurringEventForm);
    }
    else {
      viewEventFrame.setSize(500, 350);
      this.renderSingleEventView(event);
      viewEventFrame.add((JPanel) singleEventForm);
    }
    viewEventFrame.setResizable(false);
    viewEventFrame.setLocationRelativeTo(this.getParent());
    viewEventFrame.setVisible(true);
  }

  private void renderSingleEventView(IViewEvent event) {
    singleEventForm = new SingleEventForm("edit-single", "Edit", featureActionListener, null, null);
    setSingleFormFields(singleEventForm, event);
  }

  private void renderRecurringEventView(IViewEvent event) {
    recurringEventForm = new RecurringEventForm(
        "edit-recurring-single",
        "Edit",
        featureActionListener, 
        null, 
        null, 
        true);
    setRecurringFormFields(recurringEventForm, event);
  }

  private void setSingleFormFields(IEventForm<IViewEvent> form, IViewEvent event) {
    form.setDescription(defaultStringIfNull(event.getDescription()));
    form.setLocation(defaultStringIfNull(event.getLocation()));
    form.setSubject(defaultStringIfNull(event.getSubject()));
    form.setStartDate(event.getStartDateString());
    form.setStartTime(event.getStartTimeString());
    form.setEndDate(event.getEndDateString());
    form.setEndTime(event.getEndTimeString());
    if (defaultBooleanIfNull(event.isAllDayEvent())) {
      form.clickIsAllDayCheckbox();
    }
    if (defaultBooleanIfNull(event.isPrivate())) {
      form.clickIsPrivateCheckbox();
    }
  }

  private void setRecurringFormFields(IRecurringEventForm form, IViewEvent event) {
    form.setDescription(defaultStringIfNull(event.getDescription()));
    form.setLocation(defaultStringIfNull(event.getLocation()));
    form.setSubject(defaultStringIfNull(event.getSubject()));
    form.setStartDate(event.getStartDateString());
    form.setStartTime(event.getStartTimeString());
    form.setEndDate(event.getEndDateString());
    form.setEndTime(event.getEndTimeString());
    if (defaultBooleanIfNull(event.isAllDayEvent())) {
      form.clickIsAllDayCheckbox();
    }
    if (defaultBooleanIfNull(event.isPrivate())) {
      form.clickIsPrivateCheckbox();
    }
    if (event instanceof ViewRecurringSequenceEvent) {
      IViewRecurringSequenceEvent casted = (ViewRecurringSequenceEvent) event;
      form.setWeekdaysCheckboxes(casted.getWeekdaysString());
      form.setAfterString(casted.getSequenceSizeString());
    }
    else {
      IViewRecurringUntilEvent casted = (ViewRecurringUntilEvent) event;
      form.setWeekdaysCheckboxes(casted.getWeekdaysString());
      form.setOnDateString(casted.getOnDateString());
      form.setOnTimeString(casted.getOnTimeString());
    }
  }

  @Override
  public String getSelectedEventId() {
    return this.selectedEventId;
  }

  @Override
  public IViewEvent getEditSingleEvent() {
    return this.singleEventForm.getEventModel();
  }

  @Override
  public IViewRecurringEvent getEditRecurringEvent() {
    return this.recurringEventForm.getEventModel();
  }

  @Override
  public void setEvents(List<IViewEvent> events) {
    this.events = events;
  }

  private String defaultStringIfNull(String str) {
    if (str == null) {
      return "";
    }
    return str;
  }

  private Boolean defaultBooleanIfNull(Boolean boolValue) {
    if (boolValue == null) {
      return false;
    }
    return boolValue;
  }

}

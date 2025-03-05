package controller;

import java.io.IOException;
import java.util.Scanner;

import model.ICalendarApplication;

public class HeadlessCalendarController implements ICalendarController {

  private Readable in;
  private Appendable out;

  public HeadlessCalendarController(Readable in, Appendable out) {
    this.in = in;
    this.out = out;
  }

  @Override
  public void go(ICalendarApplication calendarApplication) {
    String line;
    Scanner scanner = new Scanner(in);
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      System.out.println(line);
    }
  }
}

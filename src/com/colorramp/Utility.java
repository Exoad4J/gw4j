package com.colorramp;

import java.util.Calendar;

public class Utility {
  private Utility() {
  }

  public static String millisTimeToString(long timeStamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeStamp);

    int mYear = calendar.get(Calendar.YEAR);
    int mMonth = calendar.get(Calendar.MONTH);
    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    return String.format("%d-%d-%d", mYear, mMonth, mDay);
  }
}

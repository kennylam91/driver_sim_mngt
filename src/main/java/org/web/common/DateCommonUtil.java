package org.web.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCommonUtil {
  public static String dateToString(Date date) {
    SimpleDateFormat format = getSimpleDateFormat();
    return format.format(date);
  }

  public static Date stringToDate(String str) {
    SimpleDateFormat format = getSimpleDateFormat();
    try {
      return format.parse(str);
    } catch (ParseException e) {
      return null;
    }
  }

  private static SimpleDateFormat getSimpleDateFormat() {
    return new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ssZ");
  }
}

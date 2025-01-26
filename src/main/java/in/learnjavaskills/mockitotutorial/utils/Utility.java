package in.learnjavaskills.mockitotutorial.utils;

import in.learnjavaskills.mockitotutorial.enums.WeekDays;

import java.time.LocalDate;
import java.util.Calendar;

public class Utility
{
    public static WeekDays getTodayDay()
    {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return WeekDays.getWeekDay(day);
    }

    public static WeekDays getDayFromDate(LocalDate localDate)
    {
        int day = localDate.getDayOfWeek().getValue();
        return switch (day) {
            case 1, 2, 3, 4, 5, 6 -> WeekDays.getWeekDay(day+1);
            case 7 -> WeekDays.SUNDAY;
            default -> WeekDays.UNKNOWN;
        };
    }
}

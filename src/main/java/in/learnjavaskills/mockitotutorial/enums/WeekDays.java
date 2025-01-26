package in.learnjavaskills.mockitotutorial.enums;

public enum WeekDays
{
    SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7), UNKNOWN(0);

    private int day;

    WeekDays(int day)
    {
        this.day = day;
    }

    public static WeekDays getWeekDay(int day)
    {
        for (WeekDays weekDays : WeekDays.values())
            if (weekDays.day == day)
                return weekDays;
        return WeekDays.UNKNOWN;
    }
}

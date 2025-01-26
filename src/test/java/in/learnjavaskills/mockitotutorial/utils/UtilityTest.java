package in.learnjavaskills.mockitotutorial.utils;

import in.learnjavaskills.mockitotutorial.enums.WeekDays;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest
{

    @Test
    void mockGetTodayDay()
    {
        // testing actual day without mocking
        assertEquals(WeekDays.TUESDAY, Utility.getTodayDay());

        try (MockedStatic<Utility> utilityMockStatic = Mockito.mockStatic(Utility.class))
        {
            // mocking the getTodayDay method to return the day of week as sunday instead of today's day
            utilityMockStatic.when(Utility :: getTodayDay)
                    .thenReturn(WeekDays.SUNDAY);

            assertEquals(WeekDays.SUNDAY, Utility.getTodayDay());

            // verify getTodayDay method invoke at least one time
            utilityMockStatic.verify(Utility :: getTodayDay);
        }

        // testing is mock release after the try-with resource scope
        assertEquals(WeekDays.TUESDAY, Utility.getTodayDay());
    }


    @Test
    void mockGetDayFromDate()
    {
        LocalDate localDate = LocalDate.now();
        // testing getDayFromDate without mocking
        assertEquals(WeekDays.TUESDAY, Utility.getDayFromDate(localDate));

        // let's mock the Utility.getDayFromDate to return Sunday.
        try(MockedStatic<Utility> utilityMockStatic = Mockito.mockStatic(Utility.class))
        {
            utilityMockStatic.when(()-> Utility.getDayFromDate(ArgumentMatchers.any()))
                    .thenReturn(WeekDays.SUNDAY);

            assertEquals(WeekDays.SUNDAY, Utility.getDayFromDate(localDate));

            // verify mock has been called
            utilityMockStatic.verify( ()-> Utility.getDayFromDate(localDate));
        }

        // check if mock has been release by invoking getDayFromDate outside try-with scope
        assertEquals(WeekDays.TUESDAY, Utility.getDayFromDate(localDate));
    }

}
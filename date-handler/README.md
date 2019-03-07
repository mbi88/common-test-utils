[![Build Status](https://travis-ci.org/mbi88/date-handler.svg?branch=master)](https://travis-ci.org/mbi88/date-handler)
[![codecov](https://codecov.io/gh/mbi88/date-handler/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/date-handler)


## About
Based on
 - <a href="http://www.joda.org/joda-time/">joda-time</a>.

Different operations with date time:
- get current date time
- get count of days between to dates
- add some period of time to passed date
- subtract some period of time from passed date
- get day of week 
- etc

Pass time zone to constructor to get needed date time. Default time zone - UTC.

Add some period of time to passed date. Time period should be in format e.g.: "1y2M3d4h5m6s"
Where:
* 1y - one year.
* 2M - two months.
* 3d - three days.
* 4h - four hours.
* 5m - 5 minutes.
* 6s - 6 seconds.

Example: passed date time = "2017-01-01T12:00:00", formula = "3d5m", method will return "2017-01-04T12:05:00".


## Example

```java
import com.mbi.DateHandler;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.util.TimeZone;

public class DateHandlerTest {

    @Test
    public void testName() {
        DateHandler dateHandler = new DateHandler(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Kiev")));

        // Current time is 2018-03-20T11:34:18. Method will return: 2022-05-20T13:34:18
        System.out.println(dateHandler.plus("4y2M2h"));
        
        // Returns 365
        System.out.println(dateHandler.daysBetweenDates("2017-01-01", "2018-01-01"));

        // Returns Tuesday
        System.out.println(dateHandler.getDayOfWeek("2019-01-01"));
    }
}
```

## See also
- <a href="https://github.com/JodaOrg/joda-time">Joda-time github</a>
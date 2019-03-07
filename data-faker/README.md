[![Build Status](https://travis-ci.com/mbi88/data-faker.svg?branch=master)](https://travis-ci.com/mbi88/data-faker)
[![](https://jitpack.io/v/mbi88/data-faker.svg)](https://jitpack.io/#mbi88/data-faker)
[![codecov](https://codecov.io/gh/mbi88/data-faker/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/data-faker)


## About
Set update parameter in object to get it replaced with corresponding data.

Supported update parameters: 
- UID _uid_

- DATE _current date in format yyyy-MM-dd_

- DATETIME _current datetime in format yyyy-MM-dd'T'HH:mm:ss_

- CALLER _caller method name_

- NUMBER _random number_

Example:
```json
{
  "field1": "Today is {$date}.",
  "field2": "Numbers: {$number}, {$number;2}"
}
```

Returns:
```json
{
  "field1": "Today is 2019-02-13.",
  "field2": "Numbers: 1550061663279, 85"
}
```

## Usage
Add it in your root `build.gradle` at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency:

```
dependencies {
	        implementation 'com.github.mbi88:data-faker:master-SNAPSHOT'
	}
```

## Example
```java
import com.mbi.Faker;
import com.mbi.JsonFaker;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FakerTest {

    private final Faker jsonFaker = new JsonFaker();
    private final String date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());

    @Test
    public void testFaker() {
        JSONObject json = new JSONObject();
        json.put("field", "Today is {$DATE}.");

        JSONObject result = jsonFaker.fakeData(json);

        assertEquals(result.getString("field"), String.format("Today is %s.", date));
    }
}
```
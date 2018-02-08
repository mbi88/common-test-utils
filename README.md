# API TESTS

API tests

### Preconditions

- install jdk `http://www.oracle.com/technetwork/java/javase/downloads/index.html`
- install gradle `https://gradle.org/install/`

### Before run

Set system environment variables: 
- **API__AUTH__SECRETKEY** _secret key for token generation_
- **API__AUTH__BASE64** _if secret base64 encoded_
- **API__ENDPOINT** _API url_

**_url example:_** https://qa1-api.userreport.com/survey/

**_note:_** do not forget to add "/" to the end of the url

It is possible to override variables values if another environment should be tested:
set up **DEFAULT_ENVIRONMENT** in `src/main/java/app/Configuration.java`


### Run

**Run full test suite from terminal:**
```
gradle clean test
```

Run excluding some test groups
```
gradle clean test -P exclude=group_name
```

Specific run
```
gradle test --tests org.gradle.SomeTest.someSpecificFeature
gradle test --tests all.in.specific.package\*
gradle test --tests \*SomeSpecificTest
```

Run a suite
```
gradle clean test -P suite=access
```

Available test suites path:
```
src/main/resources/suites/
```
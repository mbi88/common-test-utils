SET API__AUTH__SECRETKEY=asdasd==
SET API__AUTH__BASE64=true

SET API__ENDPOINT=http://localhost:5025/apiname/

gradle clean test -P exclude=long

# rate-limiting
rate-limiting app with bucket4j

Required:
JDK 17

Spring boot v3.2.3

bucket4j-core v8.7.1

To start:
1. run "mvn spring-boot:run"
2. The app will run on Port 9090
3. Test with GET request http://localhost:8080/api/items?numTokens=10
4.
5. numTokens must be a number an more than 0

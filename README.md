# SEC
## Requirements
* Maven https://maven.apache.org/install.html
* Java jdk 1.8
<!-- * Portugal's CC authentication program https://www.autenticacao.gov.pt/cc-aplicacao
     * A Citizen Card and a Card Reader -->
     
## Setup
On *root project's directory*, run the following command:
```
mvn clean compile install -DskipTests
```
Next, on *KeyStoreCreator's directory*, run the following command:
```
mvn exec:java
```

Next, on *server's directory*, run the following command:
```
mvn exec:java
```
After, on *client's directory*, run the following command:
```
mvn exec:java
```

If you want to run the tests in order to see if the program is running correctly, you only need to run
On *root project's directory*, run the following command:
```
mvn clean compile install
```

# SEC
## Requirements
* Maven https://maven.apache.org/install.html
* Java jdk 1.8
<!-- * Portugal's CC authentication program https://www.autenticacao.gov.pt/cc-aplicacao
     * A Citizen Card and a Card Reader -->
     
## Setup
On *root project's directory*, run the following command:
```
mvn clean compile install
```
Next, on *server's directory*, run the following command:
```
mvn exec:java
```
After, on *client's directory*, run the following command:
```
mvn exec:java
```

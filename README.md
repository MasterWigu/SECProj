# SEC
## Requirements
* Maven https://maven.apache.org/install.html
* Java jdk 1.8
<!-- * Portugal's CC authentication program https://www.autenticacao.gov.pt/cc-aplicacao
     * A Citizen Card and a Card Reader -->
     
## Setup
On *root project's directory*, run the following command:
```
mvn clean install -DskipTests
```

\
Next, on *KeyStoreCreator's directory*, run the following command, this will create the KeyStores for max 4 servers and 4 clients:
```
mvn exec:java
```

\
Next, on *server's directory*, run the following command, where {id} is the id of the server (1..4):
```
mvn exec:java -Dsv.i={1} [-Dsv.kPass={2}]
```
The ```-Dsv.kPass={2}``` is optional and {2} denotes the keystore Password. For usage with the automatically created KeyStores there is no need to explicitly declare the password since the default is already inserted in the pom file.

\
After, on *client's directory*, run the following command, where  {id} is the id of the keyStore the client will use (1..4):
```
mvn exec:java -Dcl.i={1} [-Dcl.f={2}] [-Dsv.kPass={3}]
```
The ```-Dcl.f={2}``` is optional and {2} denotes the number of faults tolerated. The default is 0.\
The ```-Dcl.kPass={3}``` is optional and {3} denotes the keystore Password. For usage with the automatically created KeyStores there is no need to explicitly declare the password since the default is already inserted in the pom file.

\
If you want to run the tests in order to see if the program is running correctly, you only need to run on *root project's directory*, the following command:
```
mvn clean test
```

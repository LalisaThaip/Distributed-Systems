# Calculator RMI Project
## Overview
This project implements a Remote Calculator using Java RMI, allowing multiple clients to connect and perform operations on a shared stack.

## Features:

- Push and pop integer values
- Perform operations: MIN, MAX, GCD, LCM
- Delayed pop operation
- Single and multiple client support
- JUnit tests for automated verification#

## Project Structure
```
assignment1/
│
├─ src/
│  ├─ main/java/
│  │   ├─ CalculatorServer.java
│  │   ├─ CalculatorClient.java
│  │   ├─ CalculatorImplementation.java
│  │   └─ Calculator.java
│  └─ test/java/
│      └─ CalculatorTest.java
│
├─ pom.xml
└─ README.md
```

## Prerequisites 
- Java 17 or later
- Maven (for JUnit testing)
- Terminal/command line access

## Steps to Run 
### 1. Compile Java Files

Navigate to the source folder:
```bash
cd assignment1/src/main/java
```

Compile all java files 
```bash
javac *.java
```

### 2. Start the RMI Registry

Open a new terminal and start RMI registry on port 1099:
```bash
rmiregistry 1099 &
```
Note: Ensure no other process is using port 1099. 
Use if needed to clear port 1099:
```bash
lsof -i :1099
kill -9 <PID>
```

### 3. Launch the Calculator Server

In a new terminal:
```bash
cd assignment1/src/main/java
java CalculatorServer
```
You should see in terminal:
Calculator Server running ...

### 4. Run the Client

Open another terminal and run:
```bash
cd assignment1/src/main/java
java CalculatorClient
```
Client provides a menu to:

1. Push a value (Add an integer to the stack)
2. Pop a value (Remove and show the top number from the stack)
3. Calculate MIN (Find the minimum value on the stack)
4. Calculate MAX (Find the maximum value on the stack)
5. Calculate GCD (Compute the Greatest Common Divisor of all numbers on the stack)
6. Calculate LCM (Compute the Least Common Multiple of all numbers on the stack)
7. Delay pop operation for a specified number of milliseconds
8. Exit the client server (Close the calculator application)

### 5. Simulate Multiple Clients

Open multiple terminals and run the client in each
```bash
cd assignment1/src/main/java
java CalculatorClient
```
Note: All clients interact with the same shared stack on the server. Always ensure the server is running first 

### 6. Run JUnit Tests

There are 2 methods for testing the code to choose from: Using Makefile and manually typing Maven commands.

#### Testing method 1: Maven

Navigate to the project root 
```bash
cd assignment1
```
Run tests with Maven:
```bash
mvn test
```
Note: please do not edit the pom.xml file as it is used for testing

Tests included:
- Single client push/pop and operations
- Multiple client concurrency

To rebuild tests cleanly:
```bash
mvn clean test
```


#### Testing method 2: Makefile 

Start the server in one terminal:
``` bash
make compile   # compile all Java files
make server    # start the Calculator RMI server
```
Open a new terminal and run the tests:
```bash
make test
```
This will run all JUnits in the CalculatorTest class
- The output will show results for:
- Single-client push/pop and operations
- Multi-client concurrent push/pop
- Multi-client shared stack operation

##### Cleaning
To remove compiled files:
```bash 
make clean
```
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
Note: Ensure no other process is using port 1099. Use lsof -i :1099 and kill -9 <PID> if needed.

### 3. Launch the Calculator Server
In a new terminal:
```bash
cd assignment1/src/main/java
java CalculatorServer
```
You should see in terinal:
Calculator Server running ...

### 4. Run the Client Calculator Server
Open another terminal and run:
```bash
java CalculatorClient
```
Client provides a menu to:

1. Push a value
2. Pop a value
3. Calculate MIN
4. Calculate MAX
5. Calculate GCD
6. Calculate LCM
7. Delay pop operation for a certain ms
8. Exit the client server

### 5. Simulate Multiple Clients
Open multiple terminals and run the client in each
```bash
java CalculatorClient
```
Note: All clients interact with the same shared stack on the server. Always ensure the server is running first 

### 6. Run JUnit Tests
Navigate to the project root 
```bash
cd assignment1
```
Run tests with Maven:
```bash
mvn test
```

Tests included:
- Single client push/pop and operations
- Multiple client concurrency

To rebuild tests cleanly:
```bash
mvn clean test
```
# Makefile for Calculator RMI Project

# Directories
SRC_DIR=src/main/java
TEST_DIR=src/test/java
OUT_DIR=out
TARGET_DIR=target

# Java & Maven
JAVAC=javac
JAVA=java
MVN=mvn

# RMI registry port
RMI_PORT=1099

# Main classes
SERVER_CLASS=CalculatorServer
CLIENT_CLASS=CalculatorClient
TEST_CLASS=CalculatorTest

# ============================
# Default target: compile all
# ============================
all: compile

# Compile main source files
compile:
	@echo "Compiling main Java files..."
	$(JAVAC) -d $(OUT_DIR) $(SRC_DIR)/*.java

# Compile and run tests via Maven
test:
	@echo "Running tests..."
	$(MVN) test

# Start RMI registry in background
rmiregistry:
	@echo "Starting RMI registry on port $(RMI_PORT)..."
	rmiregistry $(RMI_PORT) &

# Run the server
server: compile
	@echo "Starting Calculator Server..."
	$(JAVA) -cp $(OUT_DIR) $(SERVER_CLASS)

# Run the client
client: compile
	@echo "Starting Calculator Client..."
	$(JAVA) -cp $(OUT_DIR) $(CLIENT_CLASS)

# Clean compiled files
clean:
	@echo "Cleaning compiled files..."
	rm -rf $(OUT_DIR) $(TARGET_DIR)


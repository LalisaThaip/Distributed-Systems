# Makefile for Distributed Systems project
# Project path
PROJECT_DIR = /Users/lalisa/Desktop/ds_repo2/Distributed-Systems
# Maven command
MVN = mvn
# Java source files
SRC_DIR = $(PROJECT_DIR)/src/main/java/assignment2
TEST_DIR = $(PROJECT_DIR)/src/test/java/assignment2
# Output directories and files
TARGET_DIR = $(PROJECT_DIR)/target
WEATHER_DATA = $(PROJECT_DIR)/weather_data.json
TRANSACTION_LOG = $(PROJECT_DIR)/transaction.log
# Ports for integration testing
SERVER_PORT = 4567

# Default target
all: compile

# Clean project: remove target directory and generated files
clean:
	@echo "Cleaning project..."
	@cd $(PROJECT_DIR) && $(MVN) clean
	@rm -f $(WEATHER_DATA) $(TRANSACTION_LOG)

# Compile Java source files
compile:
	@echo "Compiling project..."
	@cd $(PROJECT_DIR) && $(MVN) clean compile

# Run unit tests
test: compile
	@echo "Running unit tests..."
	@cd $(PROJECT_DIR) && $(MVN) test

# Run AggregationServer
run-server:
	@echo "Starting AggregationServer on port $(SERVER_PORT)..."
	@cd $(PROJECT_DIR) && $(MVN) exec:java -Dexec.mainClass="assignment2.AggregationServer" -Dexec.args="$(SERVER_PORT)" &

# Run ContentServer
run-content:
	@echo "Running ContentServer..."
	@cd $(PROJECT_DIR) && $(MVN) exec:java -Dexec.mainClass="assignment2.ContentServer" -Dexec.args="http://localhost:$(SERVER_PORT) weather.txt"

# Run GETClient (all stations)
run-get-all:
	@echo "Running GETClient for all stations..."
	@cd $(PROJECT_DIR) && $(MVN) exec:java -Dexec.mainClass="assignment2.GETClient" -Dexec.args="http://localhost:$(SERVER_PORT)"

# Run GETClient (specific station)
run-get-station:
	@echo "Running GETClient for station1..."
	@cd $(PROJECT_DIR) && $(MVN) exec:java -Dexec.mainClass="assignment2.GETClient" -Dexec.args="http://localhost:$(SERVER_PORT) station1"

# Run integration test (sequential)
integration-test: compile
	@echo "Running integration test..."
	@cd $(PROJECT_DIR) && $(MVN) exec:java -Dexec.mainClass="assignment2.AggregationServer" -Dexec.args="$(SERVER_PORT)" & \
	 SERVER_PID=$$!; \
	 sleep 2; \
	 $(MVN) exec:java -Dexec.mainClass="assignment2.ContentServer" -Dexec.args="http://localhost:$(SERVER_PORT) weather.txt"; \
	 $(MVN) exec:java -Dexec.mainClass="assignment2.GETClient" -Dexec.args="http://localhost:$(SERVER_PORT)"; \
	 $(MVN) exec:java -Dexec.mainClass="assignment2.GETClient" -Dexec.args="http://localhost:$(SERVER_PORT) station1"; \
	 kill $$SERVER_PID

# Stop any running server on the specified port
stop-server:
	@echo "Stopping server on port $(SERVER_PORT)..."
	@lsof -i :$(SERVER_PORT) | grep LISTEN | awk '{print $$2}' | xargs -I {} kill -9 {} || true

# Phony targets to avoid conflicts with files
.PHONY: all clean compile test run-server run-content run-get-all run-get-station integration-test stop-server
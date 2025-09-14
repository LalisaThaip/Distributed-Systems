# Buildfile for Distributed Systems Assignment
# Base directory for the project
BASE_PATH = /Users/lalisa/Desktop/ds_repo2/Distributed-Systems
# Maven executable
MAVEN = mvn
# Source and output paths
JAVA_SRC = $(BASE_PATH)/src/main/java
OUTPUT_DIR = $(BASE_PATH)/target
# Generated files
DATA_JSON = $(BASE_PATH)/weather_data.json
LOG_FILE = $(BASE_PATH)/transaction.log
# Default input file for ContentServer
INPUT_FILE = $(BASE_PATH)/Weather.json
# Allow override with FILE variable (e.g., make run-content-server FILE=other.txt)
FILE_PATH ?= $(INPUT_FILE)
# Server port
PORT = 4567
# Class names with package
SERVER_CLASS = assignment2.AggregationServer
CONTENT_CLASS = assignment2.ContentServer
GET_CLIENT_CLASS = assignment2.GETClient

# Default target: compile the project
build: compile

# Compile all Java source files
compile:
	@echo "Building project..."
	@cd $(BASE_PATH) && $(MAVEN) clean compile

# Start AggregationServer on port 4567
run-agg-server: compile
	@echo "Launching AggregationServer on port $(PORT)..."
	@cd $(BASE_PATH) && $(MAVEN) exec:java -Dexec.mainClass="$(SERVER_CLASS)" -Dexec.args="$(PORT)" &

# Start ContentServer with specified file
run-content-server: compile
	@echo "Launching ContentServer with $(FILE_PATH)..."
	@if [ ! -f "$(FILE_PATH)" ]; then echo "Error: File $(FILE_PATH) not found"; exit 1; fi
	@cd $(BASE_PATH) && $(MAVEN) exec:java -Dexec.mainClass="$(CONTENT_CLASS)" -Dexec.args="http://localhost:$(PORT) $(FILE_PATH)"

# Start GETClient to fetch all stations
run-get-client: compile
	@echo "Launching GETClient for all stations..."
	@cd $(BASE_PATH) && $(MAVEN) exec:java -Dexec.mainClass="$(GET_CLIENT_CLASS)" -Dexec.args="http://localhost:$(PORT)"

# Start GETClient for a specific station (station1)
run-get-station: compile
	@echo "Launching GETClient for station1..."
	@cd $(BASE_PATH) && $(MAVEN) exec:java -Dexec.mainClass="$(GET_CLIENT_CLASS)" -Dexec.args="http://localhost:$(PORT) station1"

# Run unit tests
test-project: compile
	@echo "Executing unit tests..."
	@cd $(BASE_PATH) && $(MAVEN) test

# Clean project: remove output directory and generated files
clean-project:
	@echo "Cleaning project..."
	@cd $(BASE_PATH) && $(MAVEN) clean
	@rm -f $(DATA_JSON) $(LOG_FILE)

# Stop any running server on the specified port
stop-server:
	@echo "Terminating server on port $(PORT)..."
	@lsof -i :$(PORT) | grep LISTEN | awk '{print $$2}' | xargs -I {} kill -9 {} || true

# Phony targets to prevent conflicts with file names
.PHONY: build compile run-agg-server run-content-server run-get-client run-get-station test-project clean-project stop-server
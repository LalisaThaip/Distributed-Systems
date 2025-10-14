# Makefile for Paxos Election Project

SRC_DIR=src/main/java
TEST_DIR=src/test/java
OUT_DIR=target
MVN=mvn

# Default target
all: compile test

compile:
	@echo "🔧 Compiling Java source with Maven..."
	$(MVN) clean compile

test:
	@echo "🧪 Running Maven tests..."
	$(MVN) test

run-tests:
	@echo "🚀 Running distributed test scenarios..."
	chmod +x run_tests.sh
	./run_tests.sh

clean:
	@echo "🧹 Cleaning build artifacts..."
	$(MVN) clean
	rm -rf $(OUT_DIR)

.PHONY: all compile test run-tests clean

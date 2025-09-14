MVN = mvn
SRC_DIR = src/main/java
TARGET_DIR = target
AGGREGATION_SERVER_CLASS = AggregationServer
CONTENT_SERVER_CLASS = ContentServer
CLIENT_CLASS = Client

JAVAC_SRC = $(shell find src/main/java -name "*.java")
JAVAC_OUT = target/classes

all: compile

compile:
    $(MVN) compile

agg_server: compile
    $(MVN) exec:java -Dexec.mainClass="AggregationServer" -Dexec.args="4567"

content_server: compile
    $(MVN) exec:java -Dexec.mainClass="contentServer.ContentServer" -Dexec.args="http://localhost:4567 data/input/$(FILE)"

client: compile
    $(MVN) exec:java -Dexec.mainClass="client.Client" -Dexec.args="http://localhost:4567"

test: compile
    $(MVN) test

clean:
    $(MVN) clean
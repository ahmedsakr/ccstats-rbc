SOURCE_DIR=				src/com/ccstats
OBJECT_DIR=				bin
LIBRARIES=				lib/commons-codec-1.11.jar:lib/json-simple-1.1.1.jar:lib/jsoup-1.11.2.jar

MAIN_CLASS=				com/ccstats/test/Test
STATEMENT_PATH=			~/Desktop/ahmed-running-statement.ccs

run: compile
	java -cp bin:${LIBRARIES} ${MAIN_CLASS} ${STATEMENT_PATH} ${STATEMENT_PASSWORD}

compile:
	mkdir -p bin
	javac -d bin -cp ${LIBRARIES} `find ${SOURCE_DIR} -name "*.java"` 

clean:
	rm -rf bin com *.html package-list script.js stylesheet.css

docs:
	javadoc `find ${SOURCE_DIR} -name "*.java"`

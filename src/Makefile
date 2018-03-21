JAVA := $(wildcard */*/*/*.java */*/*.java */*.java *.java)
CLASS := ${JAVA:.java=.class}

%.class: %.java
	javac $<

default: $(CLASS)
	
clean:
	del /F /Q $(subst /,\,$(CLASS))

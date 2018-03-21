JAVA := $(shell dir /b/s *.java)
CLASS := ${JAVA:.java=.class}

%.class: %.java
	javac $<

default: $(CLASS)

find:
	echo $(JAVA)

clean:
	del /F /Q $(subst /,\,$(CLASS))

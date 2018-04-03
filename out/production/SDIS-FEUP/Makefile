JAVA :=
ifeq ($(OS),Windows_NT)
	JAVA += $(shell dir /b/s *.java)
else
	JAVA += $(shell find -name "*.java")
endif

CLASS := ${JAVA:.java=.class}

%.class: %.java
	javac $<

default: $(CLASS)

clean:
ifeq ($(OS),Windows_NT)
	del /F /Q $(subst /,\,$(CLASS))
else
	rm -rf $(CLASS)
endif

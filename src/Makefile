JDKPATH = /opt/sun-jdk-1.5.0.18/
LIBPATH = ../../lib/bufmgrAssign.jar
CLASSPATH = $(JDKPATH)/lib/classes.zip:.:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

bm:BufMgr.java
	$(JAVAC) BufMgr.java

together:*.java
	$(JAVAC) *.java

xx:
	jar tf $(LIBPATH)

clean:
	\rm -f *.class *~ \#* core 

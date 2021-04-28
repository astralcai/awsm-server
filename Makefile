.PHONY: clean compile start

clean:
	rm -rf *.class
	rm -rf *.out
	rm -rf data

compile:
	javac Server.java

start:
	nohup java Server 9000 &

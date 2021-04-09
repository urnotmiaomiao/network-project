
This folder contains 2 subfolders:
1. bin: contains *.class files
2. src: contains *.java files

Compile:
javac path/proj1/source_code/*.java -d path/proj1/bin


How to run it:
0. Switch working directory into proj
          eg: cd acn/proj/FogNode
1. If "bin" folder is empty, you need to compile it first. run the command above to complie source codes.
          eg: javac path/proj/src/*.java -d path/proj/bin
2. Revise launcher.sh file. Change $PROJDIR into proj directory, $CONFIGLOCAL. If you put .class files into ./bin, you do need change $BINDIR. Do not change $PROG.
3. Run launcher.sh file.
          eg: ./launcher.sh
4. Then you will see the output.
5. Revise cleanup.sh file and run it.
         eg: ./cleanup.sh 


If there is any question, please contact me txl180004@utdallas.edu
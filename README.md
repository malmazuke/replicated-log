--- README ---

How to compile from terminal:
• from the base directory, create a bin/ directory, then type the following:
javac -d bin/ src/replicatedlog/Replica.java src/replicatedlog/Event.java src/replicatedlog/ReplicatedLog.java src/replicatedlog/Transmission.java

How to run from terminal:
• from the base directory, type the following:
java -classpath bin/ replicatedlog/ReplicatedLog
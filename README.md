# Deadwood - Project
This game was as a class project. Coded using
Linux and Windows 11, and built using `oracle's openjdk 23.1`. You 
can play a small rather odd game about being an actor in a spaghetti
western themed movie set with 2-8 friends.

## Build and Run Instructions

### Linux
use the `cd` command to enter the directory that contains `src/, xml/, readme, out, and deadwood-Project.iml` next
use the command: `javac -d out -sourcepath src src/mothman/Deadwood.java`
then : `java -cp out mothman.Deadwood arg1` putting the player count instead of
`arg1`

### Windows 11 Platform

Assuming that you use the `Windows Terminal`, aka `Windows Powershell`, *not* `cmd`. You 
can navigate to the project directory, and use this command to compile
the necessary java code.

```javac -d out (Get-ChildItem -Recurse src\*.java)```

Then once you have successfully compiled all the classes, which should 
appear in the out folder. We can run the game from the project directory
still using the following:

```java -cp out mothman.Deadwood [PlayerCount]```

Where player count is going to be between 2-8 players.

### Intellij IDEA
Alternatively you could just load the entire project into the Intellij IDEA
IDE using the oracle openjdk 23.1, which was the primary development environment.

Then just have the IDE build the program using an application Run/Debug configuration.
Having the desired player count be environment variable.


## Code Style
- ClassNamePascals
- MethodsFollowPascal()
- PublicMembersArePascal
- _privateMembersAreCamels
- CONSTANTS_ARE_ALL_CAPS
- _PRIVATE_CONSTANTS
- parametersLikeCamels

### Exceptions to the Rules
- Override toString(), a built-in function

### How To Compile On Linux
- use the cd command to enter the directory that contains src/, xml/, readme, out, and deadwood-Project.iml
- use the command: javac -d out -sourcepath src src/mothman/Deadwood.java
- then : java -cp out mothman.Deadwood arg1

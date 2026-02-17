# mothman.Deadwood-Project


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

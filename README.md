# LogicVis

[![Build Status](https://travis-ci.org/orenjina/LogicVis.svg?branch=master)](https://travis-ci.org/orenjina/LogicVis)

See user manual here:
[User Manual](/User%20Manual.pdf)

<h2>Building from Source</h2>

Required software: Maven, JDK 11 (JDK 11 is only required to build from source. Java 8 suffices if only trying to run the jar executable.)

Run `mvn clean install` from the terminal in the project repository.
To launch the tool after building it, run `mvn exec:java`.

See the User Manual for more details.

<h2>Restrictions</h2>

This section notes a few input restrictions of the program. If these restrictions are violated, the program behaves unpredictably:
- Do not use any of these variable names in our input method:    ROOT, curDEPTH, depTH, callFromLAST, returnVALUE   
- Do not put any uncompilable code in our input method

<h2>Bug List</h2>

This list presents known bugs that have not been fixed yet:
- If we have multiple nodes that are on different levels pointing to the same nodes as a child, the arrows will overlap with one another. We have yet to figure out a way to solve it. This may require going through the entire tree once before starting to draw out individual nodes, so they can be located properly.
- We also have a problem dealing the the case when there is statement right after if statement “if (a) return b;” which is a valid java code. We can solve this by adding { } around “return b”. So “if (a) {return b;}”

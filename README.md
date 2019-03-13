# LogicVis

[![Build Status](https://travis-ci.org/orenjina/LogicVis.svg?branch=master)](https://travis-ci.org/orenjina/LogicVis)

See user manual here:
[User Manual](/User%20Manual.pdf)

For initial results, do any of the following:
1. To see the results from our script, run the following command in the terminal at the root directory: ./scripts/initial_results.sh

The program will first build and test, listing out its test results.

Then, you should be able to see string representation print out of a tree in the terminal (See [testCode](/src/main/resources/testCode.java) for the original code of the tree). 

In addition, there will be a pop-up that represents the basic representation of the UI we plan to make. Users can put in any standalone method and acquire a control flow graph as an output when they press the "Let's do it" button. The graph currently does not handle recursion separately and has visual clutter occasionally, but is representative of the code that was put in.

2. Directly check the scripts in the scripts folder and run each command independently.

3. *Be aware that in the input box, you need to add "{ }" around the code in "if statement". For example, instead of writing "if (a) return 0;", write "if (a) { return 0; }". We are currently working on solving this problem.
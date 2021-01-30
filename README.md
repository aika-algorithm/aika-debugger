# aika-visualization
A visualization component for the aika neural network based on the graph-stream project.


Hint: There is a scaling issue with some Java Versions on high resolution displays. A workaround
is to add the following option during startup. -Dsun.java2d.uiScale=100%


Stepping Modes:
'a' : stop before processing an activation
'l' : stop before processing a link
'v' : stop before each visitor step
'e' : stop after the current operation


Phases:
0 : Induction
    Creates a new untrained neuron from a template activation.
1 : Link-Induction
2 : Link-Linking
3 : Placeholder
4 : Link and Propagate
5 : Prepare Feedback Loop Update
6 : Positive Feedback Loop Update
7 : Link-Final Linking
8 : Softmax
9 : Counting
10 : Link-Counting
11 : Link-Shadow Factor
12 : SelfGradient
13 : Information-Gain Gradient
14 : Placeholder
15 : Propagate Gradients
16 : Link-Update Weight
17 : Update Synapse Input Links
18 : Link-Template
19 : Template-INPUT
20 : Template-OUTPUT
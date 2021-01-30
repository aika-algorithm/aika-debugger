# A visual debugger for the Aika Neural Network

**Hint:** There is a scaling issue with some Java Versions on high resolution displays. A workaround
is to add the following option during the startup. -Dsun.java2d.uiScale=100%


## Stepping Modes:
'a' : stop before processing an activation
'l' : stop before processing a link
'v' : stop before each visitor step
'e' : stop after the current operation


## Phases:
### What are the phases?
The phases are atomic processing steps either related to an activation, or a link that are added to the queue for 
future processing.

### List of all available phases:
0. **Induction:** Creates a new untrained neuron from a template activation.
1. **Link-Induction:** Creates a new untrained synapse from a template link.
2. **Link-Linking:** Uses the visitor to link neighbouring links to the same output activation.
3. **SumUpLink:** Uses the input activation value, and the synapse weight to update the net value of the output activation.
4. **Link and Propagate:** The job of the linking phase is to propagate information through the network by creating the required 
activations and links. Each activation and each link have a corresponding neuron or synapse respectively. Depending on the data set in the 
document, a neuron might have several activations associated with it. During propagation an input activation 
causes the creating of a link in one or more output synapses and the creation of an output activation. Initially the value 
of the input activation and the weight of the synapse might not suffice to activate the output activation. That might 
change later on as more input links are added to the activation. New input links are added by the closeCycle method. This 
method is called by the visitor which follows the links in the activation network to check that both input and output 
activation of a new link refer to the same object in the input data set.
5. **Prepare Feedback Loop Update:** Check if there are positive recurrent links that have not been activated and thus need to be updated.
6. **Positive Feedback Loop Update:** During the initial linking process all positive recurrent synapses are assumed to be active. 
If that is not the case, updates of the affected activations are required.
7. **Link-Positive Feedback Loop Update:**
8. **Determine Branch Probability:** If there are multiple mutually exclusive branches, then the softmax function will be used, to 
assign a probability to each branch.
9. **Counting:** Counts the number of activations a particular neuron has encountered.
10. **Link-Counting:** Counts the number of input or output activations a particular synapse has encountered. The four 
different cases are counted separately.
11. **Link-Shadow Factor:** Avoid that synapses which access the same source information generate twice the gradient.
12. **Entropy Gradient:** Computes the gradient of the entropy function for this activation.
13. **Information-Gain Gradient:** Computes the gradient of the information gain function for this activation.
14. **PropagateGradient:** Propagate the gradient backwards through the network.
15. **Propagate Gradients:** Propagates the gradient of this activation backwards to all its input links.
16. **Link-Update Weight:** Use the link gradient to update the synapse weight.
17. **Update Synapse Input Links:** Determines which input synapses of this activations neuron should be linked to the 
input neuron. Connecting a synapse to its input neuron is not necessary if the synapse weight is weak. That is the case 
if the synapse is incapable to completely suppress the activation of this neuron.
18. **Link-Template:** Uses the Template Network defined in the class *network.aika.neuron.Templates* to induce new 
template activations and links.
19. **Template-INPUT:** Uses the Template Network defined in the class *network.aika.neuron.Templates* to induce new template activations and links.
20. **Template-OUTPUT:**
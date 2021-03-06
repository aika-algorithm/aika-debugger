# A visual debugger for the Aika Neural Network

**Hints:** 
* A good starting point is the GradientTest.
* The debugger uses a fork of the gs-ui-swing library which contains a modified edge drawing function. https://github.com/aika-algorithm/gs-ui-swing.git


## Stepping Modes:
* 'a' : stop before processing an activation
* 'l' : stop before processing a link
* 'v' : stop before each visitor step
* 'e' : stop after the current operation
* 'r' : run mode

(Zoom: Mouse Wheel)


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

## Activation Console Frame
**New / Before / After** (*Phase*) Debugger stopping point relative to the current queue entry.

### Activation Phase
**Activation**  

* Id: *Activation Id*
* Label: *Neuron Label*
* Value: *Activation Value*
* f(net)': *Derivative of the activation function*
* net\[initial\]: *Initial net value where all positive recurrent synapses are assumed to be active*
* net\[final\]: *The final net value*
* Gradient: *The current gradient, which still needs to be propagated.*
* Gradient Sum: *The sum of all gradient computed during training.*
* Branch-Probability: *The probability of the mutually exclusive branches induced by negative recurrent synapses.*
* Fired: \[0,0\] *The exact point in time when this activation has been fired.*
* Norm: *Normalization factor taking the 'age' of a neuron into account.*
* Reference: *The ground input information referenced by this activation. Usually specified as char range.*

**Neuron**
* Id: *Neuron Id*
* Label: *Neuron Label*
* Type: *Neuron Type (Class name)*
* Is Input Neuron: *Input Neuron*
* Bias: 0.0 *The initial bias, where all positive recurrent synapses are assumed to be active*
* Bias (final): *The final bias*
* Frequency: *The counted activation frequency of this neuron.*
* N: *The number of instances in the sample space.* 
* LastPos: *The last time this sample space has been updated.*
* P(POS): *The activation probability of this neuron.*
* P(NEG): *The inverse activation probability of this neuron.*
* Surprisal(POS): *The [Surprisal](https://en.wikipedia.org/wiki/Information_content) originates from the derivative of the entropy function.*
* Surprisal(NEG): *The [Surprisal](https://en.wikipedia.org/wiki/Information_content) originates from the derivative of the entropy function.*

*Note that the Probabilities P(POS) and P(NEG). The reason for that is that are basically an upper bound on the 
probability, which lead to a conservative estimate for the corresponding surprisal values.*


### Link Phase
**New / Before / After** (*Phase*) Debugger stopping point relative to the current queue entry.

**Link**

* Input-Value: *The activation value of the input activation*
* Output-Value: *The activation value of the output activation*
* Output-net\[initial\]: *The initial net value  of the output activation*
* Output-net\[final\]: *The final net value  of the output activation*
* IsSelfRef: *True if this link closes a recurrent loop.*
* Gradient: *The gradient, that is propagated backwards through this link.*
* f(net)': *The derivative of the activation function of the output activation.*
* f(net - (xi * wi))': *This term is used for two purposes. Firstly, it limits the influence that weak synapses have on neighbouring 
synapses of the same output neuron. Secondly, it bootstraps the training of a weak synapse.*


**Synapse**

* Type: *Synapse Type (Class name)*
* Weight: *The weight of the synapse.*
* Frequency(POS, POS): *The number of instances where both input and output neuron have been activated simultaneously.*
* Frequency(POS, NEG): *The number of instances where only the input neuron has been activated.*
* Frequency(NEG, POS): *The number of instances where only the output neuron has been activated.*
* Frequency(NEG, NEG): *The number of instances where neither the input, nor the output neuron have been activated.*
* N: *The total number of trainings instances in the sample space of this synapse.*
* LastPos: X
* P(POS, POS): *The probability computed from the frequency (POS, POS)*
* P(POS, NEG): *The probability computed from the frequency (POS, NEG)*
* P(NEG, POS): *The probability computed from the frequency (NEG, POS)*
* P(NEG, NEG): *The probability computed from the frequency (NEG, NEG)*
* Surprisal(POS, POS): *The surprisal computed from the probability (POS, POS)*
* Surprisal(POS, NEG): *The surprisal computed from the probability (POS, NEG)*
* Surprisal(NEG, POS): *The surprisal computed from the probability (NEG, POS)*
* Surprisal(NEG, NEG): *The surprisal computed from the probability (NEG, NEG)*

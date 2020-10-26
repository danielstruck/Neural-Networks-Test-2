package augmentedNetworks;

import java.util.ArrayList;

import activationFunctions.NeuralProcess;
import network.*;

public class AugmentedInputLayer extends NeuralLayer{
	
	
	public AugmentedInputLayer(int numInputNeurons, NeuralProcess process) {
		super(numInputNeurons, numInputNeurons, process);
	}
	public AugmentedInputLayer(ArrayList<Neuron> neurons) {
		super(neurons);
	}
	public AugmentedInputLayer(NeuralLayer toClone) {
		super(toClone);
	}
	
	
	@Override
	public double[] calculateOutputs(double[] inputs, double bias) {
		return inputs;
	}
}

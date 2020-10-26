package augmentedNetworks;

import java.util.ArrayList;

import activationFunctions.NeuralProcess;
import network.NeuralLayer;
import network.NeuralNetwork;
import network.Neuron;

public class AugmentedNeuralLayer extends NeuralLayer{
	private NeuralLayer previousLayer;
	

	public AugmentedNeuralLayer(NeuralLayer previousLayer, int size, NeuralProcess process) {
		super(previousLayer.getNeurons().size(), size, process);
		this.previousLayer = previousLayer;
	}
	public AugmentedNeuralLayer(ArrayList<Neuron> neurons) {
		super(neurons);
	}
	public AugmentedNeuralLayer(NeuralLayer toClone) {
		super(toClone);
	}
	
	
	public NeuralLayer getPreviousLayer() {
		return previousLayer;
	}
	
	
	@Override
	public double[] calculateOutputs(double[] inputs, double bias) {
		inputs = getPreviousLayer().calculateOutputs(inputs, bias);
		
		return super.calculateOutputs(inputs, bias);
	}
	
	
	public AugmentedNeuralLayer breedWith(NeuralLayer other, int numInputs) {
		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		
		int largestLayerSize = Math.max(super.getNeurons().size(), other.getNeurons().size());
		for (int i = 0; i < largestLayerSize; i++) {
			AugmentedNeuron toAdd;
			AugmentedNeuron thisCurrentNeuron;
			AugmentedNeuron otherCurrentNeuron;
			
			if (i < super.getNeurons().size())
				thisCurrentNeuron = new AugmentedNeuron(super.getNeurons().get(i));
			else
				thisCurrentNeuron = new AugmentedNeuron(numInputs, NeuralProcess.random());
			
			if (i < other.getNeurons().size())
				otherCurrentNeuron = new AugmentedNeuron(other.getNeurons().get(i));
			else 
				otherCurrentNeuron = new AugmentedNeuron(numInputs, NeuralProcess.random());
			
			// mutate to add a random layer
			if (Math.random() < NeuralNetwork.MUTATION_RATE) {
				i--;
				toAdd = new AugmentedNeuron(numInputs, NeuralProcess.random());
			}
			else {
				toAdd = thisCurrentNeuron.breedWith(otherCurrentNeuron);
			}
			
			neurons.add(toAdd);
		}
		
		return new AugmentedNeuralLayer(neurons);
	}
}

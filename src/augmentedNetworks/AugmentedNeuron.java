package augmentedNetworks;

import java.util.ArrayList;

import activationFunctions.NeuralProcess;
import network.NeuralNetwork;
import network.Neuron;

public class AugmentedNeuron extends Neuron {
	public AugmentedNeuron(int inputNeurons, NeuralProcess process) {
		super(inputNeurons, process);
	}
	public AugmentedNeuron(NeuralProcess process, double biasWeight, double... weights) {
		super(process, biasWeight, weights);
	}
	public AugmentedNeuron(Neuron toClone) {
		super(toClone);
	}
	
	
	public AugmentedNeuron breedWith(Neuron other) {
		double[] newWeights = new double[Math.max(other.getWeights().length, other.getWeights().length) + 1];
		double newBiasWeight;
		NeuralProcess newProcess;
		
		for (int i = 0; i < newWeights.length; i++) {
			double toAdd;
			double thisCurrentWeight;
			double otherCurrentWeight;
			
			if (i < super.getWeights().length)
				thisCurrentWeight = super.getWeights()[i];
			else
				thisCurrentWeight = Neuron.randomWeight();
			
			if (i < other.getWeights().length)
				otherCurrentWeight = other.getWeights()[i];
			else 
				otherCurrentWeight = Neuron.randomWeight();
			
			// mutate to add a random layer
			if (Math.random() < NeuralNetwork.MUTATION_RATE) {
				toAdd = Neuron.randomWeight();
			}
			else if (Math.random() < .5){
				toAdd = thisCurrentWeight;
			}
			else {
				toAdd = otherCurrentWeight;
			}
			
			newWeights[i] = toAdd;
		}

		if (Math.random() < NeuralNetwork.MUTATION_RATE)
			newBiasWeight = Neuron.randomWeight();
		else if (Math.random() < .5)
			newBiasWeight = super.getBiasWeight();
		else
			newBiasWeight = other.getBiasWeight();

		if (Math.random() < NeuralNetwork.MUTATION_RATE)
			newProcess = NeuralProcess.random();
		else if (Math.random() < .5)
			newProcess = super.getProcess();
		else
			newProcess = other.getProcess();
		
		return new AugmentedNeuron(newProcess, newBiasWeight, newWeights);
	}
}

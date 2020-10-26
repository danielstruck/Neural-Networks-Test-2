package network;

import activationFunctions.NeuralProcess;

public class Neuron {
	private double[] weights;
	private double biasWeight;
	private NeuralProcess process;
	
	public Neuron(int inputNeurons, NeuralProcess process) {
		weights = new double[inputNeurons];
		
		for (int i = 0; i < weights.length; i++)
			weights[i] = Neuron.randomWeight();
		
		biasWeight = Neuron.randomWeight();
		
		this.process = process;
	}
	public Neuron(NeuralProcess process, double biasWeight, double ...weights) {
		this.process = process;
		this.biasWeight = biasWeight;
		this.weights = weights;
	}
	public Neuron(Neuron toClone) {
		weights = new double[toClone.getWeights().length];
		
		for (int i = 0; i < toClone.getWeights().length; i++)
			weights[i] = toClone.getWeights()[i];
	}
	
	
	public double calculateOutput(double[] inputs, double bias) {
		if (inputs.length != weights.length)
			System.err.println("[Neuron] inputs and weights are different lengths: " + inputs.length + ", " + weights.length);
		
		double output = 0;
		
		for (int i = 0; i < weights.length; i++)
			output += (inputs[i] * weights[i]);
		
		output += bias * biasWeight;
		
		output = process.processOutput(output);
		
		return output;
	}
	
	public double[] getWeights() {
		return weights;
	}
	
	public static double randomWeight() {
		return Math.random() * 2 - 1;
	}
	
	
	public double getBiasWeight() {
		return biasWeight;
	}
	
	public NeuralProcess getProcess() {
		return process;
	}
	
	
	public Neuron breedWith(Neuron otherNeuron) {
		NeuralProcess newProcess = (Math.random()<.5)?getProcess():otherNeuron.getProcess();
		double[] newWeights = new double[getWeights().length];
		double newBiasWeight;
		
		for (int i = 0; i < getWeights().length; i++) {
			if (Math.random() < NeuralNetwork.MUTATION_RATE)
				newWeights[i] = Neuron.randomWeight();
			else if (Math.random() < .5)
				newWeights[i] = getWeights()[i];
			else
				newWeights[i] = otherNeuron.getWeights()[i];
		}
		
		if (Math.random() < NeuralNetwork.MUTATION_RATE)
			newBiasWeight = Neuron.randomWeight();
		else if (Math.random() < .5)
			newBiasWeight = getBiasWeight();
		else
			newBiasWeight = otherNeuron.getBiasWeight();
			
		return new Neuron(newProcess, newBiasWeight, newWeights);
	}
}

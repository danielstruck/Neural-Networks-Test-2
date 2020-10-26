package network;
import java.util.ArrayList;

import activationFunctions.NeuralProcess;

public class NeuralLayer {
	private ArrayList<Neuron> neurons;
	
	
	public NeuralLayer(int numInputNeurons, int size, NeuralProcess process) {
		neurons = new ArrayList<Neuron>(size);
		for (int i = 0; i < size; i++)
			neurons.add(new Neuron(numInputNeurons, process));
	}
	public NeuralLayer(ArrayList<Neuron> neurons) {
		this.neurons = neurons;
	}
	public NeuralLayer(NeuralLayer toClone) {
		neurons = new ArrayList<Neuron>(toClone.getNeurons().size());
		
		for (Neuron n: toClone.getNeurons())
			neurons.add(new Neuron(n));
	}
	
	
	public double[] calculateOutputs(double[] inputs, double bias) {
		double[] outputs = new double[neurons.size()];
		
		for (int i = 0; i < neurons.size(); i++)
			outputs[i] = neurons.get(i).calculateOutput(inputs, bias);
		
		return outputs;
	}
	
	
	public ArrayList<Neuron> getNeurons() {
		return neurons;
	}
	
	
	public NeuralLayer breedWith(NeuralLayer otherLayer) {
		ArrayList<Neuron> newNeurons = new ArrayList<Neuron>(getNeurons().size());
		
		for (int i = 0; i < getNeurons().size(); i++)
			newNeurons.add(getNeurons().get(i).breedWith(otherLayer.getNeurons().get(i)));
		
		return new NeuralLayer(newNeurons);
	}
}

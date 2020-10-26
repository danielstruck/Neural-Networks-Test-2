package network;
import java.util.ArrayList;

import activationFunctions.NeuralProcess;import augmentedNetworks.AugmentedInputLayer;
import augmentedNetworks.AugmentedNeuralLayer;
import augmentedNetworks.AugmentedNeuralNetwork;

public class NeuralNetwork {
	public static double MUTATION_RATE = .02;// move to NeuralNetwork class
	
	
	private ArrayList<NeuralLayer> layers;
	private double[] lastOutputs;
	private double bias;
	
	
	public NeuralNetwork(int numInputs, int[] layerSizes, NeuralProcess ...neuralProcesses) {
		if (layerSizes.length != neuralProcesses.length)
			System.err.println("[NeuralNetwork] Number of layers and neural processes do not match");
		this.layers = new ArrayList<NeuralLayer>(layerSizes.length);
		
		// concatenate numInputs with layerSizes
		int[] layerInputs = new int[layerSizes.length+1];
		layerInputs[0] = numInputs;
		for (int i = 1; i < layerInputs.length-1; i++)
			layerInputs[i] = layerSizes[i-1];
		
		for (int i = 0; i < neuralProcesses.length; i++)
			this.layers.add(new NeuralLayer(layerInputs[i], layerSizes[i], neuralProcesses[i]));
		
		setup();
	}
	public NeuralNetwork(ArrayList<NeuralLayer> layers) {
		this.layers = layers;
		
		setup();
	}
	public NeuralNetwork(NeuralNetwork toClone) {
		layers = new ArrayList<NeuralLayer>(toClone.getLayers().size());
		
		for (NeuralLayer l: toClone.getLayers())
			layers.add(new NeuralLayer(l));
		
		bias = toClone.getBias();
	}
	private void setup() {
		bias = Math.random();
	}
	
	
	public void setBias(double newBias) {
		bias = newBias;
	}
	public double getBias() {
		return bias;
	}
	
	
	public ArrayList<NeuralLayer> getLayers() {
		return layers;
	}
	public void setLayers(ArrayList<NeuralLayer> newLayers) {
		layers = newLayers;
	}
	
	public ArrayList<Neuron> getNeurons() {
		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		
		for (NeuralLayer l: layers)
			neurons.addAll(l.getNeurons());
		
		return neurons;
	}
	
	
	public double[] calculateOutputs(double[] inputs){
		double[] outputs = inputs;
		
		for (int i = 0; i < layers.size(); i++)
			outputs = layers.get(i).calculateOutputs(outputs, bias);
		
		lastOutputs = outputs;
		
		return outputs;
	}
	
	
	public double calculateError(final double[] truth) {
		if (lastOutputs == null) {
			System.err.println("Network has not been run - no outputs calculated");
			return -1;
		}
		if (truth.length != lastOutputs.length)
			System.err.println("[NeuralNetwork] Truth and result are no the same length");
		
		double errorTotal = 0;
		for (int i = 0; i < lastOutputs.length; i++)
			errorTotal += Math.abs(truth[i] - lastOutputs[i]);
		
		return errorTotal;
	}
	public double calculateErrorMagnifyBelow(final double[] truth, double errThreshhold, double errWeight) {
		if (truth.length != lastOutputs.length)
			System.err.println("[NeuralNetwork] Truth and result are no the same length");
		
		double errorTotal = 0;
		for (int i = 0; i < lastOutputs.length; i++) {
			double err = Math.abs(truth[i] - lastOutputs[i]);
			if (err < errThreshhold)
				err *= errWeight;
			errorTotal += err;
		}
		
		return errorTotal;
	}
	public double calculateErrorMagnifyAbove(final double[] truth, double errThreshhold, double errWeight) {
		if (truth.length != lastOutputs.length)
			System.err.println("[NeuralNetwork] Truth and result are no the same length");
		
		double errorTotal = 0;
		for (int i = 0; i < lastOutputs.length; i++) {
			double err = Math.abs(truth[i] - lastOutputs[i]);
			if (err > errThreshhold)
				err *= errWeight;
			errorTotal += err;
		}
		
		return errorTotal;
	}
	
	
	public NeuralNetwork breedWith(NeuralNetwork otherNetwork) {
		ArrayList<NeuralLayer> newLayers = new ArrayList<NeuralLayer>(getLayers().size());
		
		for (int i = 0; i < getLayers().size(); i++)
			newLayers.add(getLayers().get(i).breedWith(otherNetwork.getLayers().get(i)));
		
		return new NeuralNetwork(newLayers);
	}


	public static void setMutationRate(double newRate) {
		MUTATION_RATE = newRate;
	}
}

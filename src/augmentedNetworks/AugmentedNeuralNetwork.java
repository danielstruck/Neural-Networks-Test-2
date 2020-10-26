package augmentedNetworks;

import java.util.ArrayList;

import activationFunctions.NeuralProcess;
import network.NeuralLayer;
import network.NeuralNetwork;
import network.Neuron;

public class AugmentedNeuralNetwork extends NeuralNetwork {
	
	
	public AugmentedNeuralNetwork(int numInputs, int numOutputs) {
		this(new ArrayList<NeuralLayer>());
		ArrayList<NeuralLayer> layers = new ArrayList<NeuralLayer>();
		
		AugmentedInputLayer inputLayer = new AugmentedInputLayer(numInputs, NeuralProcess.random());
		layers.add(inputLayer);
		
		int index;
		int avgInOut = (numInputs + numOutputs) / 2;
		for (index = 0; Math.random() < .5; index++) {
			int neuronCount = (int)((2 * Math.random()) * avgInOut);
			layers.add(new AugmentedNeuralLayer(layers.get(index), neuronCount, NeuralProcess.random()));
		}
		
		AugmentedNeuralLayer outputLayer = new AugmentedNeuralLayer(layers.get(index), numOutputs, NeuralProcess.random());
		layers.add(outputLayer);
		
		super.setLayers(layers);
	}
	public AugmentedNeuralNetwork(ArrayList<NeuralLayer> layers) {
		super(layers);
	}
	public AugmentedNeuralNetwork(NeuralNetwork toClone) {
		super(toClone);
	}
	
	
	public NeuralLayer getInputLayer() {
		return super.getLayers().get(0);
	}
	public AugmentedNeuralLayer getOutputLayer() {
		return (AugmentedNeuralLayer) super.getLayers().get(getLayers().size()-1);
	}
	
	
	@Override
	public double[] calculateOutputs(double[] inputs) {
		return getOutputLayer().calculateOutputs(inputs, super.getBias());
	}
	
	
	public AugmentedNeuralNetwork breedWith(NeuralNetwork other) {
		ArrayList<NeuralLayer> layers = new ArrayList<NeuralLayer>();
		layers.add(getInputLayer());
		
		int avgInOut = (getInputLayer().getNeurons().size() + getOutputLayer().getNeurons().size()) / 2;
		int largestNetworkSize = Math.max(super.getLayers().size(), other.getLayers().size());
		for (int i = 1; i < largestNetworkSize-1; i++) {
			NeuralLayer toAdd;
			
			NeuralLayer previousLayer = layers.get(i-1);
			int randomSize = (int) Neuron.randomWeight() + 3;
			// mutate to add a random layer
			if (Math.random() < NeuralNetwork.MUTATION_RATE) {
				i--;
				toAdd = new AugmentedNeuralLayer(previousLayer, randomSize, NeuralProcess.random());
			}
			else {
				AugmentedNeuralLayer thisCurrentLayer;
				NeuralLayer otherCurrentLayer;
				
				if (i < super.getLayers().size()-1)
					thisCurrentLayer = new AugmentedNeuralLayer(super.getLayers().get(i));
				else
					thisCurrentLayer = new AugmentedNeuralLayer(previousLayer, randomSize, NeuralProcess.random());
				
				if (i < other.getLayers().size()-1)
					otherCurrentLayer = other.getLayers().get(i);
				else 
					otherCurrentLayer = new AugmentedNeuralLayer(previousLayer, randomSize, NeuralProcess.random());
				
				toAdd = thisCurrentLayer.breedWith(otherCurrentLayer, previousLayer.getNeurons().size());
			}
			
			if (Math.random() > NeuralNetwork.MUTATION_RATE)
				layers.add(toAdd);
		}
		
		layers.add(getOutputLayer());
		
		return new AugmentedNeuralNetwork(layers);
	}
}

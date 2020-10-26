package genetic;

import java.util.ArrayList;
import augmentedNetworks.*;
import network.*;

public class AugmentedGeneration extends Generation{
	public AugmentedGeneration(int numberOfNetworks, int numInputs, int numOutputs) {
		this(new ArrayList<NeuralNetwork>());

		ArrayList<NeuralNetwork> networks = new ArrayList<NeuralNetwork>();
		for (int i = 0; i < numberOfNetworks; i++)
			networks.add(new AugmentedNeuralNetwork(numInputs, numOutputs));
		
		super.setNetworks(networks);
	}
	public AugmentedGeneration(ArrayList<NeuralNetwork> networks) {
		super(networks);
	}
	public AugmentedGeneration(Generation toCopy) {
		super(new ArrayList<NeuralNetwork>(toCopy.getNetworks()));
	}
	
	
	@Override
	public AugmentedGeneration breedNewGeneration(ArrayList<NeuralNetwork> networks, int populationSize) {
		ArrayList<NeuralNetwork> subList = new ArrayList<NeuralNetwork>(networks.subList(1, networks.size()));
		return new AugmentedGeneration(super.breedNewGeneration(subList, populationSize));
	}
}

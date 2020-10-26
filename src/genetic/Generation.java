package genetic;
import java.util.ArrayList;
import java.util.Collections;

import activationFunctions.NeuralProcess;
import network.NeuralNetwork;

public class Generation {
	private ArrayList<NeuralNetwork> networks;
	
	
	public Generation(int numberOfNetworks, int numInputs, int[] layerSizes, NeuralProcess ...processes) {
		networks = new ArrayList<NeuralNetwork>(numberOfNetworks);
		for (int i = 0; i < numberOfNetworks; i++)
			networks.add(new NeuralNetwork(numInputs, layerSizes, processes));
	}
	public Generation(ArrayList<NeuralNetwork> networks) {
		this.networks = networks;
	}
	
	
	public ArrayList<NeuralNetwork> runGeneration(double[] inputs, double[] truth, int numberBest) {
		if (numberBest > networks.size())
			System.err.println("[Generation] Requested best population exceeds generation size.");
		
		for (int i = 0; i < getNetworks().size(); i++)
			getNetworks().get(i).calculateOutputs(inputs);
		
		sortNetworks(truth);
		ArrayList<NeuralNetwork> bestNetworks = new ArrayList<NeuralNetwork>(numberBest);
		for (int i = 0; i < numberBest; i++)
			bestNetworks.add(networks.get(i));
		
		return bestNetworks;
	}
	
	
	public ArrayList<NeuralNetwork> getNetworks() {
		return networks;
	}
	public void setNetworks(ArrayList<NeuralNetwork> newNetworks) {
		networks = newNetworks;
	}
	
	
	public ArrayList<NeuralNetwork> sortNetworks(double[] truth) {
		getNetworks().sort((e1, e2) -> {
			double err1 = 1000 * Math.abs(e1.calculateError(truth));
			double err2 = 1000 * Math.abs(e2.calculateError(truth));
			return (int)(err1 - err2);
		});
		
		return getNetworks();
	}
	/* This method sorts networks based on their error */
	public ArrayList<NeuralNetwork> sortNetworksWithError(double[] errors) {
		if (errors.length != getNetworks().size()) {
			System.err.println("[Generation] Could not calculate error: error length and network count are not equal");
			return null;
		}
		
		class ExternalErrorNetwork implements Comparable<ExternalErrorNetwork>{
			NeuralNetwork network;
			double error;
			
			public ExternalErrorNetwork(NeuralNetwork network, double error) {
				this.network = network;
				this.error = error;
			}

			@Override
			public int compareTo(ExternalErrorNetwork other) {
				return (int) (1000*(error - other.error));
			}
		}
		ArrayList<ExternalErrorNetwork> errorNets = new ArrayList<ExternalErrorNetwork>(getNetworks().size());
		for (int i = 0; i < errors.length; i++)
			errorNets.add(new ExternalErrorNetwork(getNetworks().get(i), errors[i]));
		
		Collections.sort(errorNets);
		
		getNetworks().clear();
		for (int i = 0; i < errors.length; i++)
			getNetworks().add(errorNets.get(i).network);
		
		return getNetworks();
	}
	
	
	public Generation breedNewGeneration(ArrayList<NeuralNetwork> networks, int populationSize) {
		if (networks.size() == 0) {
			System.err.println("[Generation] Attempting to breed new generation with no networks.");
			return null;
		}
		ArrayList<NeuralNetwork> newNetworks = new ArrayList<NeuralNetwork>(populationSize);
		
		
		while (networks != null) {
			int numBest = networks.size();
			for (int i = 0; i < numBest; i++) {
				for (int j = i; j < numBest; j++) {
					newNetworks.add(breed(networks.get(i), networks.get(j)));
					if (newNetworks.size() >= populationSize)
						return new Generation(newNetworks);
				}
			}
		}
		
		System.err.println("[Generation] Breeding failed: network list is null.");
		return null;
	}
	public NeuralNetwork breed(NeuralNetwork n1, NeuralNetwork n2) {
		return n1.breedWith(n2);
	}
}

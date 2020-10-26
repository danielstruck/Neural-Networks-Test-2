package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import activationFunctions.NeuralProcess;
import augmentedNetworks.AugmentedNeuralNetwork;
import games.*;
import genetic.*;
import network.*;

public class Runner {
	public static boolean ECHO = false;
	

	public Runner() {
		final double[] inputs 				= {-1, -1, 
												1, 1};
		//										solid	diagonal	vertical	horizontal
		final double[] truth 				= {	0,		0,			0,			1};
		final int numberOfNetworks 			= 5;
		final int[] layerSizes 				= {4, 4, 8, 4};
		final NeuralProcess[] processes 	= {NeuralProcess.tanh,
											   NeuralProcess.tanh,
											   NeuralProcess.ReLU,
											   NeuralProcess.linear};
		
//		observeLearning(inputs, truth, numberOfNetworks, layerSizes, processes);
//		bulkLearn(inputs, truth, numberOfNetworks, layerSizes, processes, 100);
//		quickTestErrors(inputs, truth, numberOfNetworks, layerSizes, processes);
		
		final int gameNetworkCount = 10000;
		final int gameGenerationCount = 1000000;
		
		Generation gen;
		gen = playGames(Games.tictactoe, false, gameNetworkCount, gameGenerationCount);
		System.out.println("\n\n\n\n\n\n**********Human Game**********");
		Runner.ECHO = true;
		for (int i = 0; i < Games.longestNetworks.length; i++) {
			System.out.println("*********************\nNetwork " + (i+1) + "\n*********************");
			Games.tictactoe.runGame(Games.longestNetworks[i], new HumanNeuralNetwork(9, 9));
		}
	}

	
	public static void main(String[] args) {new Runner();}
	
	
	public static ArrayList<NeuralNetwork> processGeneration(Generation gen, double[] inputs, double[] truth) {
		ArrayList<NeuralNetwork> best = gen.runGeneration(inputs, truth, gen.getNetworks().size()/2);
		StringBuilder b = new StringBuilder();

		b.append("=====BEST=====\n");
		b.append(getAllNetworkInfo(best, truth));
		b.append("=====ALL======\n");
		b.append(getAllNetworkInfo(gen.getNetworks(), truth));

		System.out.println(b.toString() + "\n");

		return best;
	}
	public static StringBuilder getNeuronInfo(Neuron neuron) {
		StringBuilder b = new StringBuilder("[" + String.format("%-3d", neuron.getWeights().length) + "]");

		for (double d : neuron.getWeights()) {
			if (d != 0)
				b.append(String.format(" %6.2f ", d));
			else
				b.append(" ###### ");
		}

		return b;
	}
	public static StringBuilder getLayerInfo(NeuralLayer layer) {
		StringBuilder b = new StringBuilder();
		
		if (layer == null)
			return b;
		
		if (layer.getNeurons().get(0).getProcess() == null)
			System.err.println("[Runner] process is null");
		
		b.append(" Size:" + layer.getNeurons().size());
		b.append(" (" + layer.getNeurons().get(0).getProcess().getName() + ")");
		
		for (int i = 0; i < layer.getNeurons().size(); i++) {
			b.append("\n\t\tNeuron: ");
			b.append(getNeuronInfo(layer.getNeurons().get(i)));
		}
		b.append('\n');

		return b;
	}
	public static StringBuilder getNetworkInfo(NeuralNetwork network, double[] truth) {
		StringBuilder b = new StringBuilder();
		
		b.append(" (err: ");
		if (truth.length > 0)
			b.append(String.format("%10f", network.calculateError(truth)));
		else
			b.append(String.format("%10s", "?.??"));
		b.append(") | ");
		
		b.append("Layer count: " + network.getLayers().size() + " | ");
		b.append("Bias: " + network.getBias());

		return b;
	}
	public static StringBuilder getNetworkDetails(NeuralNetwork network, double[] truth) {
		StringBuilder b = getNetworkInfo(network, truth);
		
		b.append('\n');
		for (int i = 0; i < network.getLayers().size(); i++) {
			b.append("\tLayer " + (i+1));
			b.append(getLayerInfo(network.getLayers().get(i)));
		}
		b.append('\n');
		
		return b;
	}
	public static StringBuilder getAllNetworkInfo(ArrayList<NeuralNetwork> networks, double[] truth) {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < networks.size(); i++) {
			b.append("Network " + String.format("%3d", i + 1));
			b.append(getNetworkInfo(networks.get(i), truth));
			b.append('\n');
		}

		return b;
	}
	public static NeuralNetwork compareNetworks(NeuralNetwork net1, NeuralNetwork net2) {
		NeuralNetwork differenceNetwork;
		
		ArrayList<NeuralLayer> layers = new ArrayList<NeuralLayer>();
		
		int zeros = 0;
		int weightCount = 0;
		
		for (int layerIndex = 0; layerIndex < net1.getLayers().size(); layerIndex++) {
			NeuralLayer currentLayer1 = net1.getLayers().get(layerIndex);
			NeuralLayer currentLayer2 = net2.getLayers().get(layerIndex);
			
			ArrayList<Neuron> neurons = new ArrayList<Neuron>();
			
			for (int neuronIndex = 0; neuronIndex < currentLayer1.getNeurons().size(); neuronIndex++) {
				Neuron currentNeuron1 = currentLayer1.getNeurons().get(neuronIndex);
				Neuron currentNeuron2 = currentLayer2.getNeurons().get(neuronIndex);
				
				double[] weights = new double[currentNeuron1.getWeights().length];
				
				for (int weightIndex = 0; weightIndex < currentNeuron1.getWeights().length; weightIndex++) {
					double currentWeight1 = currentNeuron1.getWeights()[weightIndex];
					double currentWeight2 = currentNeuron2.getWeights()[weightIndex];
					weights[weightIndex] = Math.abs(currentWeight1 - currentWeight2);
					if (weights[weightIndex] == 0)
						zeros++;
					weightCount++;
				}
				
				neurons.add(new Neuron(currentNeuron1.getProcess(), 0, weights));
			}
			
			layers.add(new NeuralLayer(neurons));
		}
		
		differenceNetwork = new NeuralNetwork(layers);
		
		System.out.println(String.format("%.2f", ((double)zeros/weightCount)*100.0) + "% similar (" + zeros + "/" + weightCount + ")");
		
		return differenceNetwork;
	}
	
	
	public void bulkLearn(double[] inputs, double[] truth, int numberOfNetworks
							, int[] layerSizes, NeuralProcess[] processes
							, int numGenerations) {
		
		ArrayList<NeuralNetwork> best = new ArrayList<NeuralNetwork>();
		Generation gen = null;
		
		for (int i = 0; i < numGenerations; i++) {
			gen = new Generation(numberOfNetworks, inputs.length, layerSizes, processes);
			best.addAll(gen.runGeneration(inputs, truth, 1));
			System.out.println("Completed " + (i+1));
		}
		
		Collections.sort(best, (e1, e2) -> {
			return (int) (1000*(e1.calculateError(truth) - e2.calculateError(truth)));
		});
		
		
		for (int i = 0; i < 10; i++) {
			NeuralNetwork n = best.get(i);
			StringBuilder b = new StringBuilder();
			for (double d: n.calculateOutputs(inputs))
				b.append("out: " + d + "\n");
			b.append(getNetworkDetails(n, truth) + "\n------------\n");
			System.out.println(b.toString());
		}
	}
	public void observeLearning(double[] inputs, double[] truth, int numberOfNetworks
									, int[] layerSizes, NeuralProcess[] processes) {
		ArrayList<NeuralNetwork> best = null;
		Generation gen = null;
		Scanner sc = new Scanner(System.in);
		int timesRun = 0;
		int runBackorder = 1;
		double smallestError = Double.MAX_VALUE;
		NeuralNetwork smallestNet = null;
		String userIn = "";
		do {
			if (gen == null)
				gen = new Generation(numberOfNetworks, inputs.length, layerSizes, processes);
			else
				gen = gen.breedNewGeneration(best, numberOfNetworks);

			best = processGeneration(gen, inputs, truth);

			double err = gen.getNetworks().get(0).calculateError(truth);
			if (err < smallestError) {
				smallestError = err;
				smallestNet = gen.getNetworks().get(0);
			}
			System.out.println("Smallest error: " + smallestError);

			timesRun++;
			runBackorder--;
			while (!userIn.equals("stop") && runBackorder == 0) {
				System.out.print("(" + timesRun + ") Command: ");
				userIn = sc.nextLine().toLowerCase();
				int index;
				switch (userIn) {
					case "continue":
						System.out.print("Run amount: ");
						runBackorder = sc.nextInt();
						sc.nextLine();
						break;
					case "stop":
						break;
					case "networks":
						// prompt for network number
						System.out.print("Display network #");
						index = sc.nextInt() - 1;
						// display network
						if (index >= 0 && index < gen.getNetworks().size())
							System.out.println(getNetworkDetails(gen.getNetworks().get(index), truth));
						else
							System.out.println("Index exceeds generation size");
						sc.nextLine();
						break;
					case "smallest network":
						if (smallestNet != null)
							System.out.println(getNetworkDetails(smallestNet, truth));
						else
							System.out.println("Smallest network does not exist");
						break;
					case "input":
						// prompt for network number. If number is 0, use smallestNet network.
						System.out.print("Run network # ");
						index = sc.nextInt() - 1;
						if (index >= -1 && index < gen.getNetworks().size()) {
							NeuralNetwork network;
							if (index == -1)
								network = smallestNet;
							else
								network = gen.getNetworks().get(index);
							// prompt for values to input to selected network
							double[] userNetIn = new double[network.getLayers().get(0).getNeurons().size()];
							System.out.println(userNetIn.length + " inputs");
							for (int i = 0; i < userNetIn.length; i++) {
								System.out.print("Input " + (i+1) + ": ");
								userNetIn[i] = sc.nextDouble();
							}
							// display network's output
							StringBuilder b = new StringBuilder();
							for (double d: smallestNet.calculateOutputs(userNetIn))
								b.append(d + " ");
							b.append('\n');
							for (double d: smallestNet.calculateOutputs(userNetIn))
								b.append((d>.5)?"T ":"F ");
							System.out.println(b.toString());
						}
						else
							System.out.println("Index exceeds generation size");
						sc.nextLine();
						break;
					case "breed":
						// Prompt for two network numbers 
						System.out.print("First network number: ");
						int netIndex1 = sc.nextInt() - 1;
						System.out.print("Second network number: ");
						int netIndex2 = sc.nextInt() - 1;
						if (netIndex1 >= -1 && netIndex1 < gen.getNetworks().size()
								&& netIndex2 >= -1 && netIndex2 < gen.getNetworks().size()) {
							// breed them together
							NeuralNetwork net1;
							NeuralNetwork net2 = gen.getNetworks().get(netIndex2);
							
							if (netIndex1 == -1)
								net1 = smallestNet;
							else
								net1 = gen.getNetworks().get(netIndex1);
							if (netIndex2 == -1)
								net2 = smallestNet;
							else
								net2 = gen.getNetworks().get(netIndex2);
							
							NeuralNetwork bredNetwork = net1.breedWith(net2);
							// Display bred network
							System.out.println("Bred network" + getNetworkInfo(bredNetwork, truth));
						}
						else
							System.out.println("Index exceeds generation size");
						break;
					case "set network value":
						// prompt for network number, layer number, neuron number, weight number, and new weight and
						//   set the corresponding neuron's values to it
						System.err.println("Unimplemented");
						break;
					default:
						System.out.println("Invalid command: " + userIn);
				}
			}
			System.out.println("\n============================\n");
		} while (!userIn.equals("stop"));

		System.out.println("Smallest error = " + smallestError);
		System.out.println(getNetworkInfo(smallestNet, truth));
	}
	public void quickTestErrors(double[] inputs, double[] truth, int numberOfNetworks
									, int[] layerSizes, NeuralProcess[] processes) {
		Generation gen = new Generation(numberOfNetworks, inputs.length, layerSizes, processes);
		
		gen.runGeneration(inputs, truth, numberOfNetworks);
		gen.sortNetworks(truth);
		
		for (NeuralNetwork n: gen.getNetworks())
			System.out.println(getNetworkInfo(n, truth));
	}
	
	
	public Generation playGames(Games game, boolean humanControl, int numberOfNetworks, int numberOfGenerations){
		int numInputs;
		int numOutputs;
		
		switch (game) {
			case flappyBird:
				if (humanControl) {
					numInputs = 2;
					numOutputs = 1;
					game.runGame(new HumanNeuralNetwork(numInputs, numOutputs));
				}
				else
					return game.run(numberOfNetworks, numberOfGenerations);
				break;
			case tictactoe:
				if (humanControl) {
					numInputs = 9;
					numOutputs = 9;
					game.runGame(new HumanNeuralNetwork(numInputs, numOutputs),
							new HumanNeuralNetwork(numInputs, numOutputs));
				}
				else
					return game.run(numberOfNetworks, numberOfGenerations);
				break;
			default:
				System.err.println("Game not found");
		}
		return null;
	}
	
	
	

	public static void print(Object s) {
		if (Runner.ECHO)
			System.out.print(s);
	}
	public static void println(Object s) {
		Runner.print(s + "\n");
	}
}

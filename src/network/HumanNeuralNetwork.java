package network;

import java.util.Scanner;

import activationFunctions.NeuralProcess;

public class HumanNeuralNetwork extends NeuralNetwork{
	private int numOutputs;
	
	public HumanNeuralNetwork(int numInputs, int numOutputs) {
		super(numInputs, new int[]{numOutputs}, new NeuralProcess[1]);
		this.numOutputs = numOutputs;
	}
	public HumanNeuralNetwork(NeuralNetwork toClone) {
		super(toClone);
	}
	
	
	@Override
	public double[] calculateOutputs(double[] inputs) {
		double[] outputs;
		
		StringBuilder b = new StringBuilder();
		System.err.println("<Human Interface Start>");
		
		b.append("inputs:");
		for (int i = 0; i < inputs.length; i++)
			b.append(" " + inputs[i]);
		b.append('\n');
		
		outputs = new double[numOutputs];
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		b.append(numOutputs + " outputs");
		System.out.println(b.toString());
		for (int i = 0; i < numOutputs; i++) {
			System.out.print("Output #" + (i+1) + ": ");
			outputs[i] = sc.nextDouble();
		}
		System.err.println("<Human Interface End>");
		
		return outputs;
	}
}

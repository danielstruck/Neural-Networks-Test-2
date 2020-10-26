package games;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import activationFunctions.*;
import augmentedNetworks.*;
import genetic.*;
import main.Runner;
import network.*;

public enum Games {
	tictactoe {
		public final int numInputs = 9;
		public final int numOutputs = 9;
		
		private final int[] formatValues 	= {0,   1,  -1};
		private final char[] formatKeys		= {'X', 'O', '-'};
		
		private int genNum;
		private int longestGame;
		private Board longestGameBoard;
		
		
		public Generation run(int numberOfNetworks, int numGenerations) {
			if (numberOfNetworks % 2 != 0) {
				System.err.println("[tic tac toe] Number of networks odd - network count is now " + (numberOfNetworks + 1));
				numberOfNetworks++;
			}
			
			int[] layerSizes = {9, 8, 9};
			NeuralProcess[] processes = {NeuralProcess.cos,
										 NeuralProcess.ReLU,
										 NeuralProcess.abs};

			Generation gen = null;
//			AugmentedGeneration gen = null;
			ArrayList<NeuralNetwork> best = new ArrayList<NeuralNetwork>(numberOfNetworks/2);
			longestGame = 0;
			for (genNum = 0; genNum < numGenerations; genNum++) {
				if (Runner.ECHO)
					Runner.println("Generation #" + genNum);

				if (gen == null)
					gen = new Generation(numberOfNetworks, numInputs, layerSizes, processes);
				else
					gen = gen.breedNewGeneration(best, numberOfNetworks);
//				if (gen == null)
//					gen = new AugmentedGeneration(numberOfNetworks, numInputs, numOutputs);
//				else
//					gen = new AugmentedGeneration(gen.breedNewGeneration(best, numberOfNetworks));
				
				best.clear();
				NeuralNetwork[] networks = new NeuralNetwork[2];
				for (int i = 0; i+1 < numberOfNetworks; i+=2) {
					if (Runner.ECHO)
						Runner.println("Game #" + i/2);
//					System.err.println("[tic tac toe] Playing with HumanNeuralNetwork");
					networks[0] = gen.getNetworks().get(i);
					networks[1] = gen.getNetworks().get(i+1);//new HumanNeuralNetwork(numInputs, numOutputs);//
					
//					System.out.println(Runner.getNetworkDetails(networks[0], new double[0]).toString());
					
					best.addAll(runGame(networks));
//					System.out.println(Runner.getNetworkInfo(networks[0], new double[0]).toString());
//					System.out.println(Runner.getNetworkInfo(networks[1], new double[0]).toString());
				}
				
				printEndOfGeneration();
			}
			
			StringBuilder result = new StringBuilder("\n=========\n");
			result.append("Longest game: " + longestGame);
			result.append('\n');
			System.out.print(result);
			result = new StringBuilder();
			if (longestGame > 0)
				longestGameBoard.printFormat(formatValues, formatKeys);
			System.out.println(longestGameBoard.getTranscript());
			for (int i = 0; i < longestNetworks.length; i++) {
				result.append("Network #" + (i+1) + Runner.getNetworkDetails(longestNetworks[i], new double[0]));
				result.append('\n');
			}

			result.append("#############DIF NET#############");
			result.append('\n');
			NeuralNetwork difNet = Runner.compareNetworks(longestNetworks[0], longestNetworks[1]);
			result.append(Runner.getNetworkDetails(difNet, new double[0]));
			System.out.println(result.toString());
			
			return gen;
		}

		public ArrayList<NeuralNetwork> runGame(NeuralNetwork ...networks) {
			ArrayList<NeuralNetwork> winner = new ArrayList<NeuralNetwork>(1);
			
			if (networks.length != 2)
				System.err.println("[Tic Tac Toe] runGame() must be passed 2 networks");
			
			Board board = new Board(3, 3);
			board.fill(-1);
			int currentPlayer = 0;
			int turns = -1;
			int winType = -1;
			StringBuilder b = new StringBuilder();
			while (winType == -1 && ++turns < 9) {
				if (Runner.ECHO)
					board.printFormat(formatValues, formatKeys);
				b.append('\n');
				b.append("Player: " + (currentPlayer+1));
				
				Runner.println(b.toString());
				b = new StringBuilder();
				
				NeuralNetwork currentNetwork = networks[currentPlayer];
				
				double[] inputs = new double[board.cellCount()];
				for (int row = 0; row < board.rows(); row++) {
					for (int col = 0; col < board.cols(); col++) {
						if (board.getBoard()[row][col] == currentPlayer)
							inputs[row*board.cols()+col] = 1;
						else if (board.getBoard()[row][col] == 1-currentPlayer)
							inputs[row*board.cols()+col] = -1;
						else
							inputs[row*board.cols()+col] = 0;
					}
				}
				double[] outputs = currentNetwork.calculateOutputs(inputs);
				
				int largestIndex = 0;
				for (int i = 1; i < outputs.length; i++)
					if (outputs[i] > outputs[largestIndex])
						largestIndex = i;
				
				Point boardPoint = new Point(largestIndex%board.cols(), largestIndex/board.rows());
				b.append("Chose col " + boardPoint.x + " and row " + boardPoint.y + " from " + (largestIndex + 1));
				b.append('\n');
				
				// if selected cell is free
				if (board.valueAt(boardPoint) == -1) {
					winType = WIN_TYPE_VICTORY;
					board.setValue(boardPoint, currentPlayer);
					board.appendTranscriptLine(formatKeys[currentPlayer] + " r:" + boardPoint.y + " c:" + boardPoint.x);
					
					if (pointHasWon(board, boardPoint)) {
						board.appendTranscriptLine("Three in a row. Winner: " + formatKeys[currentPlayer] + ".");
						b.append("Three in a row. Winner: " + currentPlayer + ".");
						b.append('\n');
						winner.add(networks[currentPlayer]);
					}
					else
						currentPlayer = 1-currentPlayer;
				}
				// if selected cell is full
				else {
					// currentNetwork chose a full cell - is bad network
					winType = WIN_TYPE_DEFAULT;
					winner.add(networks[1-currentPlayer]);
					board.appendTranscriptLine("Chose full cell. Player " + formatKeys[1-currentPlayer] + " won.");
					
					b.append("Chose full cell. Player " + formatKeys[1-currentPlayer] + " won.");
					b.append('\n');
					
					b.append("\tout: ");
					for (double d: outputs)
						b.append(String.format("%.3f ", d));
					b.append('\n');
					
					b.append("\tChose: (" + boardPoint.x + "," + boardPoint.y + ")");
					b.append('\n');
				}
			}
			
			if (turns > longestGame) {
				longestGame = turns;
				longestGameBoard = board;
				longestNetworks[0] = networks[0];
				longestNetworks[1] = networks[1];
			}
			
			if (!Runner.ECHO){
				if(turns >= 9 || winType != WIN_TYPE_DEFAULT) {
					System.out.println("#" + genNum + "/" + turns + ": " + winType);
					board.printFormat(formatValues, formatKeys);
					System.out.println(board.getTranscript());
				}
			}
			else
				board.printFormat(formatValues, formatKeys);
			
			Runner.println(b.toString());
			
			return winner;
		}
		
		
		private boolean pointHasWon(Board board, Point point) {
			boolean diagnal = board.checkDiagnalsEqual(point);
			boolean row = board.checkRowsInColEqual(point.x);
			boolean col = board.checkColsInRowEqual(point.y);
//			System.err.println(diagnal + ", " + row + ", " + col);
			return diagnal || row || col;
		}
	},
	flappyBird {
		public final int numInputs = 2;
		
		private final int pipeOpeningSize 	= 3;
		private final int distBetweenPipes 	= 10;
		private final int pipeOpeningBound 	= 10;
		private final int initBirdHeight 	= pipeOpeningBound/2;
		
		private int highScore = 0;
		private int genNum;
		
		public Generation run(int numberOfNetworks, int numGenerations) {
			int[] layerSizes = {6, 1};
			NeuralProcess[] processes = {NeuralProcess.tanh, NeuralProcess.linear};
			
			Generation gen = null;
			ArrayList<NeuralNetwork> best = new ArrayList<NeuralNetwork>(numberOfNetworks/2);
			for (genNum = 0; genNum < numGenerations; genNum++) {
				Runner.println("Generation #" + (genNum+1));
				
				if (gen == null)
					gen = new Generation(numberOfNetworks, numInputs, layerSizes, processes);
				else
					gen = gen.breedNewGeneration(best, numberOfNetworks);
				
				NeuralNetwork[] networks = new NeuralNetwork[numberOfNetworks];
				for (int i = 0; i < gen.getNetworks().size(); i++)
					networks[i] = gen.getNetworks().get(i);
				
				best.clear();
				best.addAll(runGame(networks));
				if (best.size() < networks.length/2)
					best.addAll(new Generation(numberOfNetworks/2-best.size(), numInputs, layerSizes, processes).getNetworks());
				
				printEndOfGeneration();
			}
			
			return gen;
		}
		
		public ArrayList<NeuralNetwork> runGame(NeuralNetwork ...networks) {
			ArrayList<NeuralNetwork> winners = new ArrayList<NeuralNetwork>(networks.length/2);
			
			int pipeOpeningHeight = rand.nextInt(pipeOpeningBound);
			int nextPipeDist      = distBetweenPipes;
			int aliveBirds		  = networks.length;
			int[] birdHeights     = new int[aliveBirds];
			boolean[] aliveAt	  = new boolean[aliveBirds];
			
			// init arrays
			for (int i = 0; i < aliveBirds; i++) {
				birdHeights[i] = initBirdHeight;
				aliveAt[i] = true;
			}
			for (NeuralNetwork n: networks)
				winners.add(n);
			
			// play game
			StringBuilder sOut = new StringBuilder();
			int score = 0;
			while (aliveBirds > networks.length/2) {
				// display info
				sOut.append("Generation | Birds remaining | Opening at | Next pipe | Score | High score | ");
				sOut.append('\n');
				sOut.append(  String.format("%-10d | ", genNum+1)
							+ String.format("%-15d | ", aliveBirds)
							+ String.format("%3d : %-4d | ", (pipeOpeningHeight), (pipeOpeningHeight+pipeOpeningSize))
							+ String.format("%-9d | ", nextPipeDist)
							+ String.format("%-5d | ", score)
							+ String.format("%-10d | ",  highScore));
				sOut.append('\n');
				
				System.out.print(sOut.toString());
				
				printBirdsAndPipes(birdHeights, aliveAt, pipeOpeningHeight, nextPipeDist);
				
				sOut = new StringBuilder();
//new Scanner(System.in).nextLine();
				sOut.append("========================");
				sOut.append('\n');
				
				// make new pipe
				if (nextPipeDist == 0) {
					// move to next pipe
					nextPipeDist = distBetweenPipes;
					// generate opening for pipe
					pipeOpeningHeight = rand.nextInt(pipeOpeningBound);
					// increase score
					score++;
				}
				// move birds closer to pipe
				else
					nextPipeDist--;
				
				// loop through all birds
				for (int i = aliveAt.length-1; i >= 0; i--) {
					sOut.append("Bird #" + (i+1));
					if (aliveAt[i]) {
						sOut.append('\n');
						NeuralNetwork n = winners.get(i);
						
						// ask the birds whether they want to flap or not
						sOut.append("\t");
						// yes flap
						if (n.calculateOutputs(new double[]{nextPipeDist, birdHeights[i] - pipeOpeningHeight - (pipeOpeningSize + 1) / 2})[0] > .5) {
							birdHeights[i]++;
							sOut.append("flapped");
						}
						// no flap
						else {
							birdHeights[i]--;
							sOut.append("fell");
						}
						sOut.append(" (" + birdHeights[i] + ")");
						if (birdHeights[i] >= pipeOpeningHeight || birdHeights[i] < pipeOpeningHeight + pipeOpeningSize)
							sOut.append("<");
						sOut.append('\n');
						
						// check to see if each bird made it through the pipe
						if (nextPipeDist == 0) {
							sOut.append("\t");
							sOut.append("Bird at " + birdHeights[i] + " ");
							// bird has hit the pipe
							if (birdHeights[i] < pipeOpeningHeight || birdHeights[i] >= pipeOpeningHeight + pipeOpeningSize) {
								// bird is "dead"
								aliveAt[i] = false;
								aliveBirds--;
								winners.set(i, null);
								sOut.append("hit");
							}
							else
								sOut.append("avoided");
							sOut.append(" pole");
							sOut.append('\n');
						}
						else {
							if (birdHeights[i] < 0 || birdHeights[i] >= pipeOpeningHeight + pipeOpeningBound) {
								// bird is "dead"
								aliveAt[i] = false;
								aliveBirds--;
								winners.set(i, null);
							}
						}
					}
					else {
						sOut.append(" Dead\n");
					}
				}
			}
			if (score > highScore)
				highScore = score;
			
			for (int i = winners.size()-1; i >= 0; i--)
				if (winners.get(i) == null)
					winners.remove(i);
			
			sOut = new StringBuilder();
			sOut.append(aliveBirds + " alive");
			sOut.append('\n');
			sOut.append("Score: " + score);
			sOut.append('\n');
			sOut.append("High score: " + highScore);
			sOut.append('\n');
			
			
			System.out.println(sOut.toString());
			
			return winners;
		}
		
		private void printBirdsAndPipes(int[] heights, boolean[] aliveAt, int pipeOpeningHeight, int nextPipeDist) {
			StringBuilder b = new StringBuilder();
			HashMap<Integer, Integer> heightsCondensed = new HashMap<Integer, Integer>();
			for (int i = 0; i < heights.length; i++) {
				b.append("Bird " + String.format("%-4d", i+1) + "= " + heights[i]);
				if (aliveAt[i]) {
					if (heightsCondensed.containsKey(heights[i]))
						heightsCondensed.put(heights[i], heightsCondensed.get(heights[i]) + 1);
					else
						heightsCondensed.put(heights[i], 1);
				}
				else
					b.append("'");
				b.append("\n");
			}
			Runner.print(b.toString());
			
			b = new StringBuilder("----------------------\n");
			for (int i = pipeOpeningBound + pipeOpeningSize; i >= 0; i--) {
				if (heightsCondensed.containsKey(i))
					b.append(String.format("%5d", heightsCondensed.get(i)));
				else
					b.append("     ");
				
				if (!(i >= pipeOpeningHeight && i <= pipeOpeningHeight + pipeOpeningSize)) {
					for (int j = 0; j < nextPipeDist; j++)
						b.append(' ');
					b.append('|');
				}
				b.append("\n");
			}
			Runner.println(b.toString() + "----------------------");
		}
	};

	public static NeuralNetwork[] longestNetworks = new NeuralNetwork[2];
	public final int WIN_TYPE_DEFAULT	= 0,
			  		 WIN_TYPE_VICTORY	= 1;
	private final static Random rand = new Random();
	
	public abstract Generation run(int numberOfNetworks, int numGenerations);
	public abstract ArrayList<NeuralNetwork> runGame(NeuralNetwork ...networks);
	
	private static void printEndOfGeneration() {
		Runner.println("================================================");
		Runner.println("================================================");
	}
}

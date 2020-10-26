package games;

import java.awt.Point;

import main.Runner;

public class Board {
	private int[][] board;
	private StringBuilder transcript;
	private boolean shouldRecord;
	
	public Board(int rows, int cols) {
		board = new int[rows][cols];
		transcript = new StringBuilder();
		shouldRecord = false;
	}
	
	
	public void startRecording() {
		shouldRecord = true;
	}
	public void stopRecording() {
		shouldRecord = false;
	}
	
	
	public int[][] getBoard() {
		return board;
	}
	public int rows() {
		return board.length;
	}
	public int cols() {
		return board[0].length;
	}
	public int cellCount() {
		return rows() * cols();
	}
	
	
	public double[] asNetworkInput(int ...filter) {
		double[] netInputs = new double[rows()*cols()];
		
		for (int row = 0; row < rows(); row++) {
			for (int col = 0; col < cols(); col++) {
				boolean shouldAdd = filter.length == 0; // in case of no filters, default is shouldAdd = true
				for (int i: filter)
					if (getBoard()[row][col] == i)
						shouldAdd = true;
				
				if (shouldAdd)
					netInputs[row*cols()+col] = 1;
				else
					netInputs[row*cols()+col] = 0;
			}
		}
		
		return netInputs;
	}
	
	
	public int valueAt(Point pos) {
		return board[pos.y][pos.x];
	}
	public void moveValue(Point from, Point to) {
		board[to.y][to.x] = board[from.y][from.x];
		board[from.y][from.x] = 0;
		record("Moved (" + from.x + "," + from.y + ") to (" + to.x + "," + to.y + ")");
	}
	public void swapValues(Point from, Point to) {
		int temp = board[to.y][to.x];
		moveValue(from, to);
		board[from.y][from.x] = temp;

		record("Swapped (" + from.x + "," + from.y + ") and (" + to.x + "," + to.y + ")");
	}
	public void setValue(Point pos, int value) {
		board[pos.y][pos.x] = value;

		record("set (" + pos.x + "," + pos.y + ") to " + value);
	}
	public void fill(int value) {
		for (int row = 0; row < rows(); row++)
			for (int col = 0; col < cols(); col++)
				board[row][col] = value;

		record("Filled with " + value);
	}

	public boolean checkColsInRowEqual(int rowIndex) {
		for (int i = 1; i < cols(); i++)
			if (board[rowIndex][i] != board[rowIndex][0])
				return false;
		return true;
	}
	public boolean checkRowsInColEqual(int colIndex) {
		for (int i = 1; i < cols(); i++)
			if (board[i][colIndex] != board[0][colIndex])
				return false;
		return true;
	}
	public boolean checkDiagnalsEqual(Point point) {
		boolean downRight = point.x == point.y;				// ie: \
		boolean upRight = (point.x == (rows()-1) - point.y);// ie: /
		
		int diagnalLength = Math.min(rows(), cols());
		
		if (downRight) {
			for (int i = 1; i < diagnalLength; i++) {
				if (board[i][i] != board[0][0]) {
					return false;
				}
			}
		}
		else if (upRight) {
			for (int i = 1; i < diagnalLength; i++) {
				if (board[rows()-1-i][i] != board[rows()-1][0]) {
					return false;
				}
			}
		}
		else
			return false;
				
				
		return true;
	}
	
	
	public String getTranscript() {
		return transcript.toString();
	}
	public boolean record(String s) {
		if (shouldRecord)
			appendTranscriptLine(s);
		return shouldRecord;
	}
	public void appendTranscript(String s) {
		transcript.append(s);
	}
	public void appendTranscriptLine(String s) {
		appendTranscript(s + "\n");
	}
	
	public void print() {
		StringBuilder boarder = new StringBuilder();
		for (int i = 0; i < cols(); i++)
			boarder.append("----");
		
		StringBuilder b = new StringBuilder();
		b.append(boarder.toString());
		b.append('\n');
		for (int row = 0; row < rows(); row++) {
			for (int col = 0; col < cols(); col++) {
				b.append(String.format("%3d ", getBoard()[row][col]));
			}
			b.append('\n');
		}
		b.append(boarder.toString());
		b.append('\n');
		System.out.print(b.toString());
	}
	public void printFormat(int[] values, char[] keys) {
		StringBuilder boarder = new StringBuilder();
		for (int i = 0; i < cols(); i++)
			boarder.append("----");
		
		StringBuilder b = new StringBuilder();
		b.append(boarder.toString());
		b.append('\n');
		for (int[] row: board) {
			for (int i: row) {
				boolean hasChar = false;
				for (int j = 0; j < values.length; j++) {
					if (values[j] == i) {
						b.append(keys[j]);
						hasChar = true;
						break;
					}
				}
				if (!hasChar)
					b.append(String.format("%3d", i));
			}
			b.append('\n');
		}
		b.append(boarder.toString());
		b.append('\n');
		System.out.print(b.toString());
	}
}

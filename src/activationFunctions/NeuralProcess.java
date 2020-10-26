package activationFunctions;

public class NeuralProcess {
	public static final NeuralProcess
		tanh = new NeuralProcess("tanh", (in) -> {
			return 2/(1+Math.exp(-in))-1;
		}),
		arctan = new NeuralProcess("arctan", (in) -> {
			return Math.atan(in);
		}),
		ReLU = new NeuralProcess("ReLU", (in) -> {
			return (in < 0)?0:in;
		}),
		ELU = new NeuralProcess("ELU", (in) -> {
			return (in > 0)?in:Math.exp(in) - 1;
		}),
		linear = new NeuralProcess("linear", (in) -> {
			return in;
		}),
		binary = new NeuralProcess("binary", (in) -> {
			return in<0?0:1;
		}),
		sign = new NeuralProcess("sign", (in) -> {
			return in/Math.abs(in);
		}),
		softSign = new NeuralProcess("soft sign", (in) -> {
			return in/(1+Math.abs(in));
		}),
		gausian = new NeuralProcess("gausian", (in) -> {
			return Math.exp(-in*in);
		}),
		abs = new NeuralProcess("abs", (in) -> {
			return (in < 0)?-in:in;
		}),
		sin = new NeuralProcess("sin", (in) -> {
			return Math.sin(in);
		}),
		cos = new NeuralProcess("cos", (in) -> {
			return Math.cos(in);
		});
	public static final NeuralProcess[] list = {tanh, arctan, ReLU, ELU, linear, binary, sign, softSign, gausian, abs, sin, cos};
	
	
	private String name;
	
	
	private Processor process;
	public NeuralProcess(String name, Processor process) {
		this.process = process;
		this.name = name;
	}
	
	
	public double processOutput(double input) {
		return process.processOutput(input);
	}
	
	
	public NeuralProcess asInverseOutput() {
		return new NeuralProcess("Inverse" + getName(), process) {
			public double processOutput(double input) {
				return 1/processOutput(input);
			}
		};
	}
	public NeuralProcess asOppositeOutput() {
		return new NeuralProcess("Negative " + getName(), process) {
			public double processOutput(double input) {
				return -processOutput(input);
			}
		};
	}
	public NeuralProcess asOppositeInput() {
		return new NeuralProcess("X-Flipped " + getName(), process) {
			public double processOutput(double input) {
				return processOutput(-input);
			}
		};
	}
	public NeuralProcess withInput(NeuralProcess other) {
		return new NeuralProcess(getName() + " with " + other.getName(), process) {
			public double processOutput(double input) {
				return processOutput(other.processOutput(input));
			}
		};
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public static NeuralProcess random() {
		return NeuralProcess.list[(int) (Math.random() * NeuralProcess.list.length)];
	}
}
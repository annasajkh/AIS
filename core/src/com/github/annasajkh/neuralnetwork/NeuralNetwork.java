package com.github.annasajkh.neuralnetwork;

public class NeuralNetwork
{
	private Matrix[] network;
	private Matrix[] weights;
	private Matrix[] biases;
	private int inputSize;
	private int hiddenLayerSize;
	private int layerCount;
	private int outputSize;
	private float[] expectedOutput;
	private float learningRate = 0.01f;
	
	public static float sigmoid(float value)
	{
		return 1.0f / (1.0f + (float)Math.exp(-value));
	}

	private static Matrix sigmoid(Matrix matrix)
	{
		Matrix m = matrix.clone();
		for(int i = 0; i < m.rows; i++)
		{
			for(int j = 0; j < m.cols; j++)
			{
				m.array[i][j] = sigmoid(m.array[i][j]);
			}
		}
		return m;
	}

	private static Matrix dsigmoid(Matrix matrix)
	{
		Matrix m = matrix.clone();

		for(int i = 0; i < m.rows; i++)
		{
			for(int j = 0; j < m.cols; j++)
			{
				float value = m.array[i][j];
				m.array[i][j] = value * (1 - value);
			}
		}
		return m;
	}

	public NeuralNetwork(int inputSize, int hiddenLayerSize, int outputSize)
	{
		this(inputSize, hiddenLayerSize, outputSize, 1);
	}

	public NeuralNetwork(int inputSize, int hiddenLayerSize, int outputSize, int layerCount)
	{
		// make new array of matrix
		weights = new Matrix[1 + layerCount];
		biases = new Matrix[1 + layerCount];
		this.inputSize = inputSize;
		this.hiddenLayerSize = hiddenLayerSize;
		this.layerCount = layerCount;
		this.outputSize = outputSize;

		// make layers of hidden layer as many as the layer count
		Matrix hiddenLayers = new Matrix(1, hiddenLayerSize);

		// make the network with size of input + layerCount + output
		network = new Matrix[layerCount + 2];

		// make network index 0 the size of input cuz it's a input layer
		network[0] = new Matrix(1, inputSize);

		// fill network index 1 - ? with the hidden layer
		for(int i = 1; i < network.length - 1; i++)
		{
			network[i] = hiddenLayers;
		}

		// make network index last the size of output cuz it's a output layer
		network[network.length - 1] = new Matrix(1, outputSize);

		for(int i = 1; i < network.length; i++)
		{

			Matrix weight;
			Matrix bias;

			// make this if it's a hidden layer
			if(i > 1 && i != network.length - 1)
			{
				// weight from hidden to hidden
				weight = new Matrix(hiddenLayerSize, hiddenLayerSize);
			} 
			else
			{
				// make this is it's a output layer
				if(i == network.length - 1)
				{
					// weight from hidden to output
					weight = new Matrix(outputSize, hiddenLayerSize);
				}
				// make this is it's a input layer
				else
				{
					// weight from input to hidden
					weight = new Matrix(hiddenLayerSize, inputSize);
				}
			}

			// fill the matrix with random values
			weight.randomize();

			// set weight array to matrix
			weights[i - 1] = weight;

			// if is not output layer make new matrix size of hidden layer else make new
			// matrix size of output layer
			if(i != network.length - 1)
			{
				bias = new Matrix(hiddenLayerSize, 1);
			} 
			else
			{
				bias = new Matrix(outputSize, 1);
			}

			// randomize bias and set it to biases
			bias.randomize();
			biases[i - 1] = bias;
		}

	}

	public void setLearningRate(float learningRate)
	{
		this.learningRate = learningRate;
	}

	private void preprocess(int i, boolean train)
	{

		// matrix multiplacation between weight and before layer
		Matrix results = Matrix.multiply(weights[i - 1], network[i - 1]);

		// add biasses
		results.add(biases[i - 1]);

		// map it to sigmoid function
		results = sigmoid(results);

		// set current layer to the result
		network[i] = results;

		// if the current layer is the output and it's training then do the
		// backpropagation
		if(i == network.length - 1 && train)
		{
			backpropagation(results);
		}
	}

	public float[] process(float[] input)
	{
		// pass input to input layer
		network[0] = new Matrix(input);

		// feed forward the input from layer 1 so it can get layer 0
		for(int i = 1; i < network.length; i++)
		{
			preprocess(i, false);
		}

		// return the last layer aka the output
		return network[network.length - 1].toArray();
	}

	private Matrix[] getAllErrors(Matrix error)
	{
		// make array of matrix and set last index = error
		Matrix[] errors = new Matrix[weights.length];
		errors[errors.length - 1] = error;

		// calculate error on index i and pass it on index before it
		for(int i = errors.length - 1; i >= 1; i--)
		{
			errors[i - 1] = Matrix.multiply(Matrix.transpose(weights[i]), errors[i]);
		}

		return errors;
	}

	private void changingWeightsAndBiases(int index, Matrix errors, Matrix layer, Matrix afterLayer)
	{

		// calculate gradient and map it to dsigmoid function
		Matrix gradient = dsigmoid(layer);

		// multiply it by errors and learning rate
		gradient.scale(errors);
		gradient.scale(learningRate);

		// deltaWeight = gradient multiply afterLayer transposted
		Matrix deltaWeight = Matrix.multiply(gradient, Matrix.transpose(afterLayer));

		// adjust the weight by deltaWeight
		weights[index].add(deltaWeight);

		// adjust the bias by it's delta (it's just the gradient)
		biases[index].add(gradient);

	}

	private void backpropagation(Matrix output)
	{

		// calculate output error
		Matrix error = new Matrix(expectedOutput);
		error.sub(output);

		// get all errors
		Matrix[] errors = getAllErrors(error);

		// backpropagation
		for(int i = errors.length - 1; i >= 0; i--)
		{
			changingWeightsAndBiases(i, errors[i], network[i + 1], network[i]);
		}
	}

	public void train(float[] input, float[] expectedOutput)
	{
		// checking if expected output length is greater than output size
		if(expectedOutput.length > outputSize)
		{
			System.out.println("Error expected output is bigger than the output size");
			return;
		}

		// checking if input length is greater than input size
		if(expectedOutput.length > inputSize)
		{
			System.out.println("Error input is bigger than the input size");
			return;
		}

		this.expectedOutput = expectedOutput;

		// pass input to input layer
		network[0] = new Matrix(input);

		// feedforward
		for(int i = 1; i < network.length; i++)
		{
			preprocess(i, true);
		}

	}

	// mutate weights by chance between 0 - 1
	public NeuralNetwork mutateWeights(float chance)
	{
		NeuralNetwork neuralNetwork = clone();
		for(int i = 0; i < neuralNetwork.weights.length; i++)
		{
			neuralNetwork.weights[i].mutate(chance);
		}
		return neuralNetwork;
	}

	// mutate biases by chance between 0 - 1
	public NeuralNetwork mutateBiases(float chance)
	{
		NeuralNetwork neuralNetwork = clone();
		for(int i = 0; i < neuralNetwork.biases.length; i++)
		{
			neuralNetwork.biases[i].mutate(chance);
		}
		return neuralNetwork;
	}

	// mutates weights and biases by chance between 0 - 1
	public NeuralNetwork mutate(float chance)
	{
		NeuralNetwork neuralNetwork = clone();
		for(int i = 0; i < neuralNetwork.weights.length; i++)
		{
			neuralNetwork.weights[i].mutate(chance);
		}
		for(int i = 0; i < neuralNetwork.biases.length; i++)
		{
			neuralNetwork.biases[i].mutate(chance);
		}
		return neuralNetwork;
	}

	@Override
	public NeuralNetwork clone()
	{
		NeuralNetwork clone = new NeuralNetwork(inputSize, hiddenLayerSize, outputSize, layerCount);

		for(int i = 0; i < weights.length; i++)
		{
			clone.weights[i] = weights[i].clone();
		}
		for(int i = 0; i < biases.length; i++)
		{
			clone.biases[i] = biases[i].clone();
		}

		clone.learningRate = learningRate;
		return clone;
	}

}

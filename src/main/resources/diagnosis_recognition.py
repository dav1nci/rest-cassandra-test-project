import numpy as np
import random
import scipy.io as sc
import matplotlib.image as mpimg
from scipy.misc import imsave
from collections import deque
import operator
from PIL import Image
import struct
from array import array
import json
import http.client
import urllib.parse
import requests


class NeuralNetwork:
    "Neural Network Class"

    def __init__(self, enter_layer, exit_layer, hidd_lay, units_num_in_layer):
        self.hidd_lay = hidd_lay
        self.biases = [np.random.randn(units_num_in_layer, 1) for i in range(hidd_lay)]
        self.biases.append(np.random.randn(exit_layer, 1))

        self.weights = []
        self.weights.append(np.random.randn(units_num_in_layer, enter_layer))
        for i in range(hidd_lay - 1):
            self.weights.append(np.random.randn(units_num_in_layer, units_num_in_layer))
        self.weights.append(np.random.randn(10, units_num_in_layer))

    def recognite(self, test_data):
        test_results = [(np.argmax(self.feed_forward(x)), y) for (x, y) in  test_data]
        return sum(int(x == y) for x, y in test_results)

    def feed_forward(self, x):
        for b, w in zip(self.biases, self.weights):
            x = sigmoid(np.dot(w, x) + b)
        return x

    def stochastic_gradient_decent(self, trainig_data, mini_batch_size, epoch_number, learning_rate, test_data = None):
        for e in range(epoch_number):
            random.shuffle(trainig_data)
            mini_batches = [trainig_data[i : i + mini_batch_size] for i in range(0, len(trainig_data), mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, learning_rate)
            if test_data:
                print ("Epoch {0}: {1} / {2}".format(e, self.recognite(test_data), len(test_data)))

            else:
                print("Epoch {0} complete".format(e))

    def update_mini_batch(self, mini_batch, learning_rate):
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        for (x, y) in mini_batch:
            (delta_nabla_b, delta_nabla_w) = self.back_prop(x, y)
            nabla_b = [nb + dnb for (nb, dnb) in zip (nabla_b, delta_nabla_b)]
            nabla_w = [nw + dnw for (nw, dnw) in zip (nabla_w, delta_nabla_w)]
        self.weights = [w - (learning_rate / len(mini_batch)) * nw for (w, nw) in zip(self.weights, nabla_w)]
        self.biases = [b - (learning_rate / len(mini_batch)) * nb for (b, nb) in zip(self.biases, nabla_b)]

    def back_prop(self, x, y):
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        activation = x
        activations = [x]
        zs = []
        for (w, b) in zip(self.weights, self.biases):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)
        delta = (activations[-1] - y) * sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].T)
        for l in range(2, self.hidd_lay + 2):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l + 1].T, delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l - 1].T)
        return (nabla_b, nabla_w)

def vectorize_y(y):
    result = []
    for i in y:
        e = np.zeros((10, 1))
        e[i] = 1
        result.append(e)
    return result

def sigmoid_prime(x):
    return sigmoid(x) * (1 - sigmoid(x))

def sigmoid(x):
    return 1.0 / (1.0 + np.exp(-x))

if __name__ == "__main__":
    "Trainig neural Network"
    x = json.load(open('trainigSet.json'))
    x = np.asarray(x['x'])
    x = [np.reshape(i, (8, 1)) for i in x]
    y = []
    for i in range(10):
        analysis = [i] * int(len(x) / 10)
        y.extend(analysis)
    y_test = y[:50000]
    y = vectorize_y(np.asarray(y))
    #trainig_data = list(zip(np.asarray(x), y))
    nn = NeuralNetwork(enter_layer = len(x[0]), exit_layer = 10, hidd_lay = 1, units_num_in_layer = 5)
    nn.stochastic_gradient_decent(trainig_data = list(zip(np.asarray(x), y)), mini_batch_size = 10, epoch_number = 2, learning_rate = 1.0, test_data = list(zip(x[:50000], y_test)) )

    conn = http.client.HTTPConnection('localhost', 8080)
    conn.request("GET", "/patient/withNoDiagnose")
    response = conn.getresponse()
    a = response.read()
    conn.close()
    a = a.decode('utf-8')
    analysis = json.loads(a)
    print(analysis)
    research_data = []
    research_data.append(analysis['analysis']['leukocytes'] / 21e9)
    research_data.append(analysis['analysis']['erythrocytes'] / 5.2e12)
    research_data.append(analysis['analysis']['hemoglobin'] / 173.0)
    research_data.append(analysis['analysis']['hematocrit'] / 49.0)
    research_data.append(analysis['analysis']['erythrocytesMedian'] / 99.0)
    research_data.append(analysis['analysis']['hemoglobinInErythrocyte'] / 31.0)
    research_data.append(analysis['analysis']['hemoglobinAverageInErythrocyte'] / 380.0)
    research_data.append(analysis['analysis']['platelets'] / 600e9)

    research_data = np.reshape(np.asarray(research_data), (8, 1))
    res = nn.feed_forward(research_data)
    data = {'email' : analysis['key']['patientEmail'], 'diagnosis' : str(np.argmax(res))}
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    r = requests.post("http://localhost:8080/patient/analyseReady", data = json.dumps(data), headers = headers)

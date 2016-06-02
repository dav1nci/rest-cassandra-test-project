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


def sigm_fun(x):
    return 1.0 / (1.0 + np.exp((-1.0) * x))

def load(path_img, path_lbl):
    with open(path_lbl, 'rb') as file:
        magic, size = struct.unpack(">II", file.read(8))
        if magic != 2049:
            raise ValueError('Magic number mismatch, expected 2049,'
                             'got {}'.format(magic))

        labels = array("B", file.read())

    with open(path_img, 'rb') as file:
        magic, size, rows, cols = struct.unpack(">IIII", file.read(16))
        if magic != 2051:
            raise ValueError('Magic number mismatch, expected 2051,'
                             'got {}'.format(magic))

        image_data = array("B", file.read())

    images = []
    for i in range(size):
        images.append([0] * rows * cols)

    for i in range(size):
        images[i][:] = image_data[i * rows * cols:(i + 1) * rows * cols]

    return images, labels


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

#    def forward_prop(self):
#        self.a[0] = (self.x.popleft())
#        #print(type(self.a[0]))
#        # if self.complete == 1:
#        #     print(self.a[0])
#
#        #compute z for 1-st hidden layer
#        self.z[0] = (np.asarray([i + self.b[0][j] for (i, j) in zip(np.dot(self.a[0], self.w[0]), range(self.units_num_in_layer))]).flatten())
#        self.a[1] = (np.asarray([NeuralNetwork.sigm_fun(z) for z in self.z[0]]))
#
#        for l in range(1, self.hidd_lay):
#            self.z[l] = (np.asarray([i + self.b[l][j] for (i, j) in zip(np.dot(self.a[l], self.w[l]), range(self.units_num_in_layer))]).flatten())
#            self.a[l + 1] = (np.asarray([NeuralNetwork.sigm_fun(z) for z in self.z[l]]))
#
#        self.z[self.hidd_lay] = (np.asarray([i + self.b[-1][j] for (i, j) in zip(np.dot(self.a[self.hidd_lay], self.w[-1]), range(self.ny))]).flatten())
#        self.a[self.hidd_lay + 1] = (np.asarray([NeuralNetwork.sigm_fun(z) for z in self.z[-1]]))
#        
##         print('last a = ', self.a)
##         if self.complete == 1:
##             print('self.a = ', self.a)
#        return self.a[-1]
#
#
#    def back_prop(self):
#        y_vector = np.zeros(shape=(10))
#        y = self.y.popleft()
#        #print('index = ', y)
#        y_vector[y] = 1
#
#        #print('y_vector = ', y_vector)
#
#        self.err_last = np.asarray([self.sigmoid_deriv(z) * i for (i, z) in zip(np.subtract(self.a[-1], y_vector), self.z[-1])])
##        print('err_last = ', self.err_last)
##        print('a[-1] = ', self.a[-1])
#        #print('z[-1] = ', self.z[-1])
#        # if abs(self.err_last[0]) < 0.0001:
#        #     #print(self.err_last[0])
#        #     return
#            # print(abs(self.err_last[0]))
#        for l in range(self.hidd_lay, 0, -1):
#            f_z = [self.sigmoid_deriv(z) for z in self.z[l - 1]]
#            #print('z[l - 1] = ', self.z[l - 1])
#            # print("ANOTHER ITERATION")
#            # print('err_last type = ', type(self.err_last))
#            # print('err_last = ', self.err_last)
#            #print('err_last.shape = ', self.err_last.shape)
#            # print('w[l].shape = ', self.w[l].shape)
#            # print('w[l] = ', self.w[l])
#            dJdW = np.dot(np.asmatrix(self.a[l]).T, np.asmatrix(self.err_last))
#            dJdb = self.err_last
##            print('dJdW = ', dJdW)
##            print('a[l] = ', self.a[l])
#            self.err_last = np.multiply(np.dot(self.w[l], self.err_last), f_z) #new error
#            if len(self.err_last) == 1:
#                self.err_last = np.asarray(self.err_last).reshape(-1)
#                # print('err_pre_last type = ', type(self.err_last))
#                # print('err_pre_last = ', self.err_last)
#
#
#            # print('self.delta_b[l - 1]', self.delta_b[l - 1])
#            # print('dJdb = ', dJdb)
#            self.delta_w[l - 1] = np.add(self.delta_w[l - 1], dJdW)
#            self.delta_b[l] = np.add(self.delta_b[l], dJdb)
#            # print('delta_b = ', self.delta_b)
#            
##            print('w = ', self.w[l])
##            print('delta_w', self.delta_w[l - 1])
#            self.w[l] = np.subtract(self.w[l], (self.lerning_rate * np.add(((1. / self.m) * self.delta_w[l - 1]), self.lmbd * self.w[l])))
##            print('after adding w = ', self.w[l])
#
#            self.b[l] = np.subtract(self.b[l], (self.lerning_rate * (1. / self.m) * self.delta_b[l]))
#        self.counter += 1
#        #print(self.b)
#        # print('delta_w = ', self.delta_w)
#        # print('w = ', self.w)

    def recognite(self, test_data):
        test_results = [(np.argmax(self.feed_forward(x)), y) for (x, y) in  test_data]
        return sum(int(x == y) for x, y in test_results)

    def feed_forward(self, x):
        for b, w in zip(self.biases, self.weights):
            x = sigmoid(np.dot(w, x) + b)
        return x
        

    def stochastic_gradient_decent(self, trainig_data, test_data, mini_batch_size, epoch_number, learning_rate):
        for e in range(epoch_number):
            random.shuffle(trainig_data)
            mini_batches = [trainig_data[i : i + mini_batch_size] for i in range(0, len(trainig_data), mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, learning_rate)
            print ("Epoch {0}: {1} / {2}".format(e, self.recognite(test_data), len(test_data)))

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

ims, labels = load('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
test_ims, test_labels = load('t10k-images.idx3-ubyte', 't10k-labels.idx1-ubyte')

ims = np.asarray(ims) / 255
ims = [np.reshape(x, (784, 1)) for x in ims]
labels = np.asarray(labels)
test_ims = np.asarray(test_ims) / 255
test_ims = [np.reshape(x, (784, 1)) for x in test_ims]
test_labels = np.asarray(test_labels)

labels = vectorize_y(labels)
print('data loading complete')

nn = NeuralNetwork(len(ims[0]), 10, 2, 30)
nn.stochastic_gradient_decent(list(zip(ims, labels)), list(zip(test_ims, test_labels)), 10, 10, 3.0)

import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

class OutputElement:
    def __init__(self, nbrBat, nbrPV, lcoe):
        self.nbrBat = nbrBat
        self.nbrPV = nbrPV
        self.lcoe = lcoe

    def __repr__(self):
        return f"OutputElement(nbrBat={self.nbrBat}, nbrPV={self.nbrPV}, lcoe={self.lcoe})"

# Sample data
data = [
    OutputElement(5, 10, 0.12),
    OutputElement(3, 7, 0.15),
    OutputElement(6, 9, 0.11)
]

# Extracting data from the OutputElement instances
nbrBat = [e.nbrBat for e in data]
nbrPV = [e.nbrPV for e in data]
lcoe = [e.lcoe for e in data]

# Creating a 3D scatter plot
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

ax.scatter(nbrBat, nbrPV, lcoe, c='r', marker='o')

ax.set_xlabel('Number of Batteries')
ax.set_ylabel('Number of PV')
ax.set_zlabel('LCOE')

plt.show()

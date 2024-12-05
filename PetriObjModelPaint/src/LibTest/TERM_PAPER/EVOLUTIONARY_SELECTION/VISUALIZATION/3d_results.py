import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D


# Read and parse the data
def read_data(filename):
    with open(filename, 'r') as file:
        # Split the content into lines and convert to float values
        data = [[float(val.replace(',', '.')) for val in line.split()] for line in file.readlines()]
    return np.array(data)


# Prepare data for surface plot
def prepare_surface_data(data):
    # Create a grid of phase1 and phase3 values
    phase1_values = np.arange(10, 92)
    phase3_values = np.arange(10, 92)

    # Create a 2D grid to store metric values
    z_values = np.zeros((len(phase1_values), len(phase3_values)))

    # Flatten the input data
    flat_data = data.flatten()

    # Fill the z_values grid
    for i, phase1 in enumerate(phase1_values):
        for j, phase3 in enumerate(phase3_values):
            # Assuming the data represents metrics for different phase combinations
            index = i * len(phase3_values) + j
            if index < len(flat_data):
                z_values[i, j] = flat_data[index]

    return phase1_values, phase3_values, z_values


# Create 3D surface plot
def plot_3d_surface(phase1_values, phase3_values, z_values):
    # Create figure and 3D axes with specified size and DPI
    fig = plt.figure(figsize=(30, 30), dpi=400)
    ax = fig.add_subplot(111, projection='3d')

    # Create meshgrid for surface plot
    X, Y = np.meshgrid(phase3_values, phase1_values)

    # Plot the surface
    surf = ax.plot_surface(X, Y, z_values,
                           cmap='viridis',  # Color map
                           edgecolor='none',  # No edge color
                           alpha=0.8)  # Slight transparency

    # Customize the plot
    ax.set_xlabel('Phase 3', fontsize=20)
    ax.set_ylabel('Phase 1', fontsize=20)
    ax.set_zlabel('Fitness Function (max(L_i))', fontsize=20)
    ax.set_title('3D Surface Plot of Metrics', fontsize=24)

    # Add a color bar
    fig.colorbar(surf, shrink=0.6, aspect=10)

    # Adjust the viewing angle for best visualization
    ax.view_init(elev=30, azim=120)

    # Tight layout to use space efficiently
    plt.tight_layout()

    # Save the plot
    plt.savefig('3d_surface_plot.png', bbox_inches='tight')
    plt.close()


# Main execution
def main():
    # Read data from file
    data = read_data('paste.txt')

    # Prepare surface data
    phase1_values, phase3_values, z_values = prepare_surface_data(data)

    # Create 3D surface plot
    plot_3d_surface(phase1_values, phase3_values, z_values)

    print("3D surface plot has been saved as '3d_surface_plot.png'")


if __name__ == '__main__':
    main()

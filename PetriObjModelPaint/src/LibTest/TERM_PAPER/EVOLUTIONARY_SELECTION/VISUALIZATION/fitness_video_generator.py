import matplotlib.pyplot as plt
import numpy as np
import matplotlib.animation as animation

from visualization import CSV_FILE_PATH


def read_data(filename):
    """
    Читання даних з файлу.

    Args:
        filename (str): Шлях до файлу.

    Returns:
        np.ndarray: Дані.
    """

    with open(filename, "r") as file:
        data = [[float(val.replace(",", ".")) for val in line.split()] for line in file.readlines()]
    return np.array(data)

def prepare_surface_data(data):
    """
    Підготовка даних для побудови 3D поверхневого графіка.

    Args:
        data (np.ndarray): Дані.

    Returns:
        Tuple[np.ndarray, np.ndarray, np.ndarray]: Значення для осей та значення функції придатності.
    """

    phase1_values = np.arange(10, 92)
    phase3_values = np.arange(10, 92)
    z_values = np.zeros((len(phase1_values), len(phase3_values)))
    flat_data = data.flatten()

    for i, phase1 in enumerate(phase1_values):
        for j, phase3 in enumerate(phase3_values):
            index = i * len(phase3_values) + j
            if index < len(flat_data):
                z_values[i, j] = flat_data[index]

    return phase1_values, phase3_values, z_values

def read_fitness_data(filename):
    """
    Reads fitness data from a CSV file.

    Args:
        filename (str): Path to the CSV file.

    Returns:
        pandas.DataFrame: DataFrame containing the fitness data.
    """
    import pandas as pd
    try:
        df = pd.read_csv(filename)
        return df
    except FileNotFoundError:
        print(f"Error: CSV file not found at path: {filename}")
        return None

def plot_3d_surface_with_animation(phase1_values, phase3_values, z_values, fitness_df):
    """
    Ploats a 3D surface graph with animation of individuals from each generation.

    Args:
        phase1_values (np.ndarray): Values for axis 1.
        phase3_values (np.ndarray): Values for axis 3.
        z_values (np.ndarray): Fitness function values.
        fitness_df (pd.DataFrame): DataFrame with fitness data for each generation.
    """

    fig = plt.figure(figsize=(15, 15), dpi=200)
    ax = fig.add_subplot(111, projection='3d')

    X, Y = np.meshgrid(phase3_values, phase1_values)
    surf = ax.plot_surface(X, Y, z_values, cmap='viridis', edgecolor='none', alpha=0.6)

    ax.set_xlabel("Фаза 3", fontsize=10)
    ax.set_ylabel("Фаза 1", fontsize=10)
    ax.set_zlabel("Функція придатності (max(L_i))", fontsize=10)
    ax.set_title("3D Поверхневий графік метрик", fontsize=12)
    fig.colorbar(surf, shrink=0.6, aspect=10)
    ax.view_init(elev=30, azim=120)

    scat = ax.scatter([], [], [], s=100, c='gray', depthshade=True)
    best_scat = ax.scatter([], [], [], s=200, c='white', depthshade=True)
    gen_text = ax.text2D(0.05, 0.95, "", transform=ax.transAxes, fontsize=12)

    def animate(i):
        generation_data = fitness_df[fitness_df["Generation"] == i]
        phase1 = generation_data["1'st phase"].values
        phase3 = generation_data["3'rd phase"].values
        fitness = generation_data["Fitness"].values

        scat._offsets3d = (phase3, phase1, fitness)
        scat.set_color('gray')

        best_individual = generation_data.loc[generation_data["Fitness"].idxmin()]
        best_scat._offsets3d = ([best_individual["3'rd phase"]], [best_individual["1'st phase"]], [best_individual["Fitness"]])

        gen_text.set_text(f"Generation: {i}")

        return scat, best_scat, gen_text

    ani = animation.FuncAnimation(fig, animate, frames=fitness_df["Generation"].nunique(), interval=1000/15, blit=False)

    ani.save("3d_surface_animation.mp4", writer=animation.FFMpegWriter(fps=15))
    plt.close()
    print("3D surface animation saved as \"3d_surface_animation.mp4\"")

def main():
    """Головна функція для виконання прикладу."""

    data = read_data("paste.txt")
    phase1_values, phase3_values, z_values = prepare_surface_data(data)

    fitness_df = read_fitness_data(CSV_FILE_PATH)
    if fitness_df is not None:
      plot_3d_surface_with_animation(phase1_values, phase3_values, z_values, fitness_df)

if __name__ == "__main__":
    main()

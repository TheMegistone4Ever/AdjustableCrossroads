import cv2
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

from results_3d import prepare_surface_data, read_data
from visualization import CSV_FILE_PATH, load_and_validate_data


def create_surface_plot(ax, X, Y, z_values, min_z, max_z):
    """
    Створення поверхневого графіку.

    Args:
        ax (matplotlib.axes.Axes): 3D вісь для графіку.
        X (np.ndarray): Масив значень для осі X.
        Y (np.ndarray): Масив значень для осі Y.
        z_values (np.ndarray): Значення функції придатності.
        min_z (float): Мінімальне значення Z.
        max_z (float): Максимальне значення Z.
    """

    z_values_clipped = np.clip(z_values, min_z, max_z)
    ax.plot_surface(X, Y, z_values_clipped,
                    cmap='viridis',
                    edgecolor='none',
                    alpha=0.6)
    ax.set_zlim(min_z, max_z)


def plot_generation_data(ax, generation_data, best_individual, phase1_values, phase3_values, gen):
    """
    Відображення даних генерації на графіку.

    Args:
        ax (matplotlib.axes.Axes): 3D вісь для графіку.
        generation_data (pd.DataFrame): Дані поточної генерації.
        best_individual (pd.Series): Найкращий індивід генерації.
        phase1_values (np.ndarray): Значення фази 1.
        phase3_values (np.ndarray): Значення фази 3.
        gen (int): Номер генерації.
    """

    for collection in ax.collections[1:]:
        collection.remove()
    for text in ax.texts:
        text.remove()

    valid_data = generation_data[
        (phase1_values.min() <= generation_data["1'st phase"]) &
        (generation_data["1'st phase"] <= phase1_values.max()) &
        (phase3_values.min() <= generation_data["3'rd phase"]) &
        (generation_data["3'rd phase"] <= phase3_values.max())
        ]

    ax.scatter(
        valid_data["3'rd phase"],
        valid_data["1'st phase"],
        valid_data["Fitness"],
        c='gray',
        s=100,
        alpha=0.8
    )

    ax.scatter(
        best_individual["3'rd phase"],
        best_individual["1'st phase"],
        best_individual["Fitness"],
        c='red',
        s=200,
        alpha=0.9
    )

    ax.text2D(0.05, 0.95, f"Generation: {gen}",
              transform=ax.transAxes, fontsize=16)


def create_3d_surface_animation(phase1_values, phase3_values, z_values, fitness_df):
    """
    Створення 3D анімації поверхні придатності.

    Args:
        phase1_values (np.ndarray): Значення для осі 1.
        phase3_values (np.ndarray): Значення для осі 3.
        z_values (np.ndarray): Значення функції придатності.
        fitness_df (pd.DataFrame): DataFrame з даними придатності кожної генерації.
    """

    fig = plt.figure(figsize=(20, 20), dpi=200)
    ax = fig.add_subplot(111, projection='3d')

    ax.set_xlabel("Фаза 3", fontsize=10)
    ax.set_ylabel("Фаза 1", fontsize=10)
    ax.set_zlabel("Функція придатності (max(L_i))", fontsize=10)
    ax.set_title("3D Поверхневий графік метрик", fontsize=12)

    X, Y = np.meshgrid(phase3_values, phase1_values)

    min_z, max_z = fitness_df['Fitness'].min(), fitness_df['Fitness'].max()

    create_surface_plot(ax, X, Y, z_values, min_z, max_z)

    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    out = cv2.VideoWriter('3d_surface_animation_opencv.mp4', fourcc, 10.0, (1600, 1600))

    best_individuals = {}
    for gen in fitness_df['Generation'].unique():
        gen_data = fitness_df[fitness_df['Generation'] == gen]
        best_individuals[gen] = gen_data.loc[gen_data['Fitness'].idxmin()]

    for gen in fitness_df['Generation'].unique():
        generation_data = fitness_df[fitness_df["Generation"] == gen]
        best_individual = best_individuals[gen]

        plot_generation_data(ax, generation_data, best_individual,
                             phase1_values, phase3_values, gen)

        ax.view_init(elev=30, azim=120)

        plt.tight_layout()
        plt.draw()

        fig = plt.gcf()
        fig.canvas.draw()
        image = np.frombuffer(fig.canvas.tostring_rgb(), dtype=np.uint8)
        image = image.reshape(fig.canvas.get_width_height()[::-1] + (3,))

        image_bgr = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        image_resized = cv2.resize(image_bgr, (1600, 1600))

        out.write(image_resized)
        print(f"Processed generation {gen}")

    out.release()
    plt.close()
    print('3D surface animation saved as "3d_surface_animation_opencv.mp4"')


def main():
    """Головна функція для виконання прикладу."""

    data = read_data("paste.txt")
    phase1_values, phase3_values, z_values = prepare_surface_data(data)

    fitness_df = load_and_validate_data(CSV_FILE_PATH)
    if fitness_df is not None:
        create_3d_surface_animation(phase1_values, phase3_values, z_values, fitness_df)


if __name__ == "__main__":
    main()

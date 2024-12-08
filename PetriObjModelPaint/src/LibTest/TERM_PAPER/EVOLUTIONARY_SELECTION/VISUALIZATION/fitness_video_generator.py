import subprocess
from os import path, remove, rename

import cv2
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

from results_3d import prepare_surface_data, read_data
from visualization import CSV_FILE_PATH, load_and_validate_data


def convert_video_ffmpeg(file_path):
    """
    Перетворення відео за допомогою FFmpeg з кодеком H.265.

    Args:
        file_path (str): Шлях до вхідного відеофайлу.
    """

    def build_ffmpeg_command(input_file, output_file):
        return ["ffmpeg", "-i", input_file, "-vcodec", "libx265", "-vf", "scale=1600:1600", output_file, "-r", "10"]

    def execute_ffmpeg_command(command):
        return subprocess.run(command, capture_output=True, text=True)

    def handle_conversion_result(result_temp, original_file, temp_file):
        if result_temp.returncode == 0:
            remove(original_file)
            rename(temp_file, original_file)
            print(f"Успішно конвертовано відео: {original_file}")
        else:
            print("Помилка конвертації FFmpeg:")
            print(result_temp.stderr)

    try:
        base_name = path.splitext(file_path)[0]
        temp_output_file = f"{base_name}_temp.mp4"
        ffmpeg_command = build_ffmpeg_command(file_path, temp_output_file)
        result = execute_ffmpeg_command(ffmpeg_command)
        handle_conversion_result(result, file_path, temp_output_file)
    except Exception as e:
        print(f"Виникла помилка під час конвертації FFmpeg: {e}")


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
    ax.plot_surface(X, Y, z_values_clipped, cmap="viridis", edgecolor="none", alpha=.6)
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

    valid_data = generation_data[(generation_data["1'st phase"] >= phase1_values.min()) &
                                 (generation_data["1'st phase"] <= phase1_values.max()) &
                                 (generation_data["3'rd phase"] >= phase3_values.min()) &
                                 (generation_data["3'rd phase"] <= phase3_values.max())]

    ax.scatter(valid_data["3'rd phase"], valid_data["1'st phase"], valid_data.Fitness, c="k", s=100, alpha=.8)

    ax.scatter(best_individual["3'rd phase"], best_individual["1'st phase"], best_individual.Fitness, c="r", s=200,
               alpha=.9)

    ax.text2D(.05, .95, f"Generation: {gen}", transform=ax.transAxes, fontsize=24)


def create_3d_surface_animation(phase1_values, phase3_values, z_values, fitness_df, file_path):
    """
    Створення 3D анімації поверхні придатності.

    Args:
        phase1_values (np.ndarray): Значення для осі 1.
        phase3_values (np.ndarray): Значення для осі 3.
        z_values (np.ndarray): Значення функції придатності.
        fitness_df (pd.DataFrame): DataFrame з даними придатності кожної генерації.
        file_path: Шлях до файлу для збереження анімації.
    """

    fig = plt.figure(figsize=(16, 16), dpi=200)
    ax = fig.add_subplot(111, projection="3d")

    ax.set_xlabel("Фаза 3", fontsize=14)
    ax.set_ylabel("Фаза 1", fontsize=14)
    ax.set_zlabel("Функція придатності (max(L_i))", fontsize=14)
    ax.set_title("3D Поверхневий графік метрик", fontsize=20)

    X, Y = np.meshgrid(phase3_values, phase1_values)

    min_z, max_z = fitness_df.Fitness.min(), fitness_df.Fitness.max()

    create_surface_plot(ax, X, Y, z_values, min_z, max_z)

    fourcc = cv2.VideoWriter_fourcc(*"mp4v")
    out = cv2.VideoWriter(file_path, fourcc, 10., (1600, 1600))

    best_individuals = {}
    for gen in fitness_df.Generation.unique():
        gen_data = fitness_df[fitness_df.Generation == gen]
        best_individuals[gen] = gen_data.loc[gen_data.Fitness.idxmin()]

    for gen in fitness_df.Generation.unique():
        generation_data = fitness_df[fitness_df.Generation == gen]
        best_individual = best_individuals[gen]

        plot_generation_data(ax, generation_data, best_individual,
                             phase1_values, phase3_values, gen)

        ax.view_init(elev=30, azim=120)

        plt.tight_layout()
        plt.draw()

        fig = plt.gcf()
        fig.canvas.draw()
        image = np.frombuffer(fig.canvas.buffer_rgba(), dtype=np.uint8)
        image = image.reshape(fig.canvas.get_width_height()[::-1] + (4,))

        image_bgr = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        image_resized = cv2.resize(image_bgr, (1600, 1600))

        out.write(image_resized)
        print(f"Опрацьована генерація {gen + 1}")

    out.release()
    plt.close()
    print(f"3D поверхневу анімацію збережено як \"{file_path}\"")


def main():
    """Головна функція для виконання прикладу."""

    data = read_data("paste.txt")
    phase1_values, phase3_values, z_values = prepare_surface_data(data)

    fitness_df = load_and_validate_data(CSV_FILE_PATH)

    if fitness_df is not None:
        file_path = "3d_surface_anim.mp4"
        create_3d_surface_animation(phase1_values, phase3_values, z_values, fitness_df, file_path)
        convert_video_ffmpeg(file_path)


if __name__ == "__main__":
    main()

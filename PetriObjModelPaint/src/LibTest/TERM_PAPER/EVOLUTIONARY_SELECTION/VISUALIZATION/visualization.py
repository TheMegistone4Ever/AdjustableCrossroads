from typing import Optional

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

CSV_FILE_PATH = r"..\..\..\..\..\fitness_data.csv"
OUTPUT_FILENAME = "fitness_data_plots.png"
FIGURE_SIZE = (25, 25)
DPI = 500
COLORS = ["lightblue", "lightgreen", "lightyellow", "lightcoral"]


def load_and_validate_data(file_path: str) -> Optional[pd.DataFrame]:
    """
    Завантаження та перевірка даних з CSV-файлу.

    Args:
        file_path (str): Шлях до CSV-файлу.

    Returns:
        Optional[pd.DataFrame]: DataFrame з даними або None, якщо сталася помилка.
    """

    try:
        df = pd.read_csv(file_path)
        print("Дані успішно завантажені з CSV.")
        numeric_columns = ["Generation", "Individual", "Fitness", "1'st phase", "3'rd phase"]
        for col in numeric_columns:
            df[col] = pd.to_numeric(df[col], errors="raise")

        return df
    except FileNotFoundError:
        print(f"Помилка: CSV-файл не з найдено за шляхом: {file_path}")
    except ValueError as e:
        print(f"Помилка перетворення стовпця: {e}")

    return None


def plot_best_fitness_per_generation(df: pd.DataFrame, ax: Optional[plt.Axes] = None) -> None:
    """
    Побудова графіку найкращого (мінімального) значення фітнесу для кожного покоління.

    Args:
        df (pd.DataFrame): DataFrame з даними.
        ax (Optional[plt.Axes]): Вісь для малювання. Якщо None, створюється нова.
    """

    best_fitness_per_generation = df.groupby("Generation")["Fitness"].min()
    ax = ax or plt.subplots(1, 1, figsize=(10, 6), dpi=150)[1]
    ax.plot(best_fitness_per_generation.index, best_fitness_per_generation.values,
            linestyle="-", color="m", linewidth=2)
    ax.set_xlabel("Покоління")
    ax.set_ylabel("Найкращий фітнес (Мін. очікуючих машин)")
    ax.set_title("Найкращий фітнес за покоління")
    ax.grid(True)


def plot_fitness_boxplot_with_means(df: pd.DataFrame, ax: Optional[plt.Axes] = None) -> None:
    """
    Генерація боксплотів значень фітнесу для кожного покоління з підключеними середніми значеннями.

    Args:
        df (pd.DataFrame): DataFrame з даними.
        ax (Optional[plt.Axes]): Вісь для малювання. Якщо None, створюється нова.
    """

    generations = df["Generation"].unique()
    fitness_data_by_generation = [df[df["Generation"] == g]["Fitness"].values for g in generations]
    ax = ax or plt.subplots(1, 1, figsize=(12, 8), dpi=150)[1]
    box_plot = ax.boxplot(fitness_data_by_generation, positions=generations,
                          showfliers=False, widths=.6, patch_artist=True)
    means = [np.mean(fitness) for fitness in fitness_data_by_generation]
    mean_line, = ax.plot(generations, means, linestyle="--", color="m",
                         linewidth=2, label="Середній фітнес")

    for patch, color in zip(box_plot["boxes"], COLORS * (len(generations) // len(COLORS) + 1)):
        patch.set_facecolor(color)

    ax.set_xlabel("Покоління")
    ax.set_ylabel("Фітнес (Очікуючі машини)")
    ax.set_title("Розподіл фітнесу за покоління з середніми значеннями")
    ax.set_xticks(generations)
    ax.grid(axis="y")
    ax.legend(handles=[mean_line], loc="upper right")


def _safe_log_transformation(generations: np.ndarray) -> np.ndarray:
    """
    Безпечне логарифмічне перетворення для послідовності поколінь.

    Args:
        generations (np.ndarray): Масив поколінь.

    Returns:
        np.ndarray: Логарифмічно трансформовані покоління.
    """

    return np.log10(generations + max(1 - np.min(generations), 0) + 1)


def plot_best_fitness_log_scale(df: pd.DataFrame, ax: Optional[plt.Axes] = None,
                                is_boxplot: bool = False) -> None:
    """
    Побудова графіку фітнесу з логарифмічною шкалою для поколінь.

    Args:
        df (pd.DataFrame): DataFrame з даними.
        ax (Optional[plt.Axes]): Вісь для малювання.
        is_boxplot (bool): Чи є графік боксплотом.
    """

    generations = df["Generation"].unique()
    log_generations = _safe_log_transformation(generations)
    ax = ax or plt.subplots(1, 1, figsize=(12, 8), dpi=150)[1]

    if is_boxplot:
        fitness_data_by_generation = [df[df["Generation"] == g]["Fitness"].values for g in generations]
        box_plot = ax.boxplot(fitness_data_by_generation, positions=log_generations,
                              showfliers=False, widths=.6, patch_artist=True)
        means = [np.mean(fitness) for fitness in fitness_data_by_generation]
        mean_line, = ax.plot(log_generations, means, linestyle="--", color="m",
                             linewidth=2, label="Середній фітнес")

        for patch, color in zip(box_plot["boxes"], COLORS * (len(generations) // len(COLORS) + 1)):
            patch.set_facecolor(color)

        ax.legend(handles=[mean_line], loc="upper right")
        ax.set_title("Розподіл фітнесу за логарифмічними поколіннями з середніми значеннями")
    else:
        best_fitness_per_generation = df.groupby("Generation")["Fitness"].min()
        ax.plot(log_generations, best_fitness_per_generation.values,
                linestyle="-", color="m", linewidth=2)
        ax.set_title("Найкращий фітнес за логарифмічними поколіннями")

    ax.set_xlabel("Покоління")
    ax.set_ylabel("Фітнес (Очікуючі машини)")
    ax.set_xticks(log_generations)
    ax.set_xticklabels(generations, rotation=90)
    ax.grid(axis="y" if is_boxplot else "both")


def main():
    """Головна функція для завантаження даних, обробки та генерації графіків."""

    df = load_and_validate_data(CSV_FILE_PATH)
    if df is None:
        return

    fig, axes = plt.subplots(2, 2, figsize=FIGURE_SIZE, dpi=DPI)

    plot_functions = [
        plot_best_fitness_per_generation,
        plot_fitness_boxplot_with_means,
        lambda _df, _ax: plot_best_fitness_log_scale(_df, _ax, is_boxplot=False),
        lambda _df, _ax: plot_best_fitness_log_scale(_df, _ax, is_boxplot=True)
    ]

    for func, ax in zip(plot_functions, axes.ravel()):
        func(df, ax)

    plt.tight_layout()
    plt.savefig(OUTPUT_FILENAME)
    plt.show()


if __name__ == "__main__":
    main()

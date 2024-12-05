import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

CSV_FILE_PATH = r"..\..\..\..\..\fitness_data.csv"


def plot_best_fitness_per_generation(df, ax=None):
    """Plots the best (minimum) fitness value for each generation."""
    best_fitness_per_generation = df.groupby('Generation')['Fitness'].min()

    if ax is None:
        fig, ax = plt.subplots(1, 1, figsize=(10, 6), dpi=150)

    ax.plot(best_fitness_per_generation.index, best_fitness_per_generation.values, linestyle='-', color='m',
            linewidth=2)
    ax.set_xlabel("Generation")
    ax.set_ylabel("Best Fitness (Min Waiting Cars)")
    ax.set_title("Best Fitness per Generation")
    ax.grid(True)


def plot_fitness_boxplot_with_means(df, ax=None):
    """Generates box plots of fitness values for each generation and connects the means."""
    generations = df['Generation'].unique()
    fitness_data_by_generation = [df[df['Generation'] == g]['Fitness'].values for g in generations]

    if ax is None:
        fig, ax = plt.subplots(1, 1, figsize=(12, 8), dpi=150)

    box_plot = ax.boxplot(fitness_data_by_generation, positions=generations, showfliers=False, widths=.6,
                          patch_artist=True)

    means = [np.mean(fitness) for fitness in fitness_data_by_generation]
    mean_line, = ax.plot(generations, means, linestyle='--', color='m', linewidth=2, label='Mean Fitness')

    colors = ['lightblue', 'lightgreen', 'lightyellow', 'lightcoral']
    for patch, color in zip(box_plot['boxes'], colors * (len(generations) // len(colors) + 1)):
        patch.set_facecolor(color)

    ax.set_xlabel("Generation")
    ax.set_ylabel("Fitness (Waiting Cars)")
    ax.set_title("Fitness Distribution per Generation with Means")
    ax.set_xticks(generations)
    ax.grid(axis='y')
    ax.legend(handles=[mean_line], loc='upper right')


def plot_best_fitness_per_generation_log_scale(df, ax=None):
    """Plots the best fitness with log scale on the generation axis."""
    best_fitness_per_generation = df.groupby('Generation')['Fitness'].min()
    generations = best_fitness_per_generation.index.values

    if np.min(generations) <= 0:
        shift_amount = 1 - np.min(generations)
    else:
        shift_amount = 0

    log_generations = np.log10(generations + shift_amount + 1)

    if ax is None:
        fig, ax = plt.subplots(1, 1, figsize=(10, 6), dpi=150)

    ax.plot(log_generations, best_fitness_per_generation.values, linestyle='-', color='m', linewidth=2)
    ax.set_xlabel("Generation")
    ax.set_ylabel("Best Fitness (Min Waiting Cars)")
    ax.set_title("Best Fitness per Log Generation")
    ax.grid(True)

    ax.set_xticks(log_generations)
    ax.set_xticklabels(generations, rotation=90)


def plot_fitness_boxplot_with_means_log_scale(df, ax=None):
    """Generates box plots of fitness values with log scale on the generation axis and connects the means."""
    generations = df['Generation'].unique()
    fitness_data_by_generation = [df[df['Generation'] == g]['Fitness'].values for g in generations]

    if np.min(generations) <= 0:
        shift_amount = 1 - np.min(generations)
    else:
        shift_amount = 0

    log_generations = np.log10(generations + shift_amount + 1)

    if ax is None:
        fig, ax = plt.subplots(1, 1, figsize=(12, 8), dpi=150)

    box_plot = ax.boxplot(fitness_data_by_generation, positions=log_generations, showfliers=False, widths=.6,
                          patch_artist=True)

    means = [np.mean(fitness) for fitness in fitness_data_by_generation]
    mean_line, = ax.plot(log_generations, means, linestyle='--', color='m', linewidth=2, label='Mean Fitness')

    colors = ['lightblue', 'lightgreen', 'lightyellow', 'lightcoral']
    for patch, color in zip(box_plot['boxes'], colors * (len(generations) // len(colors) + 1)):
        patch.set_facecolor(color)

    ax.set_xlabel("Generation")
    ax.set_ylabel("Fitness (Waiting Cars)")
    ax.set_title("Fitness Distribution per Log Generation with Means")
    ax.set_xticks(log_generations)
    ax.set_xticklabels(generations, rotation=90)
    ax.grid(axis='y')
    ax.legend(handles=[mean_line], loc='upper right')


def main():
    """Main function to read CSV, process data, and generate plots."""

    try:
        df = pd.read_csv(CSV_FILE_PATH)
        print("Data loaded successfully from CSV.")

        for col in ['Generation', 'Individual', 'Fitness']:
            try:
                df[col] = pd.to_numeric(df[col])
            except (ValueError, TypeError) as e:
                print(f"Error converting '{col}' to numeric: {e}. Check CSV data.")
                return

        fig, axes = plt.subplots(2, 2, figsize=(25, 25), dpi=500)  # 2x2 grid

        plot_best_fitness_per_generation(df, axes[0, 0])
        plot_fitness_boxplot_with_means(df, axes[0, 1])
        plot_best_fitness_per_generation_log_scale(df, axes[1, 0])
        plot_fitness_boxplot_with_means_log_scale(df, axes[1, 1])

        plt.tight_layout()

        # save to file
        plt.savefig("fitness_data_plots.png")

        plt.show()

    except FileNotFoundError:
        print(f"Error: CSV file not found at path: {CSV_FILE_PATH}")
    except Exception as e:
        print(f"An error occurred: {e}")


if __name__ == "__main__":
    main()

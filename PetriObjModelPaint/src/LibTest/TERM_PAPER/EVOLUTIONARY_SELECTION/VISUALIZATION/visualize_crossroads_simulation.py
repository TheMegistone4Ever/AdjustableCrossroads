import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from matplotlib.ticker import MaxNLocator


def visualize_crossroads_simulation(csv_file_path):
    df = pd.read_csv(csv_file_path, sep=";", decimal=",")

    fig, axes = plt.subplots(4, 2, figsize=(60, 40))
    fig.suptitle("Статистика симуляції перехресть за ітераціями", fontsize=16)
    axes = axes.flatten()

    columns_to_plot = ["MEAN_QUEUE_1", "MEAN_QUEUE_2", "MEAN_QUEUE_3", "MEAN_QUEUE_4",
                       "MARK_1", "MARK_2", "MARK_3", "MARK_4", ]
    colors = plt.cm.tab10(np.linspace(0, 1, len(df.ITERATION.unique())))

    for i, col in enumerate(columns_to_plot):
        for j, it in enumerate(df.ITERATION.unique()):
            iteration_data = df[df.ITERATION == it]
            axes[i].plot(iteration_data.TIME, iteration_data[col], color=colors[j], alpha=.3, label=f"Ітерація №{it}")
        axes[i].set_title(f"{col} за час симуляції")
        axes[i].set_xlabel("Час симуляції")
        axes[i].set_ylabel(col)
        axes[i].xaxis.set_major_locator(MaxNLocator(nbins=50))
        axes[i].yaxis.set_major_locator(MaxNLocator(nbins=10))
        axes[i].legend()

    plt.tight_layout()
    filename = f"crossroads_simulation_statistics_time_{int(df.TIME.max())}_iterations_{df.ITERATION.max()}.png"
    plt.savefig(filename, dpi=150, bbox_inches="tight")
    plt.close()
    print(f"Статистику симуляції збережено як \"{filename}\"...")


if __name__ == "__main__":
    CSV_FILE_PATH = r"..\..\..\..\..\crossroads_simulation_data.csv"
    visualize_crossroads_simulation(CSV_FILE_PATH)

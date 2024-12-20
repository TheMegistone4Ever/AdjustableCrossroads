from matplotlib.pyplot import figure, savefig, close, tight_layout
from numpy import array, arange, zeros, meshgrid


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
    return array(data)


def prepare_surface_data(data):
    """
    Підготовка даних для побудови 3D поверхневого графіка.

    Args:
        data (np.ndarray): Дані.

    Returns:
        Tuple[np.ndarray, np.ndarray, np.ndarray]: Значення для осей та значення функції придатності.
    """

    phase1_values = arange(10, 91)
    phase3_values = arange(10, 91)
    z_values = zeros((len(phase1_values), len(phase3_values)))

    for i, phase1 in enumerate(phase1_values):
        for j, phase3 in enumerate(phase3_values):
            z_values[i, j] = data[i, j]

    return phase1_values, phase3_values, z_values


def plot_3d_surface(phase1_values, phase3_values, z_values):
    """
    Побудова 3D поверхневого графіка.

    Args:
        phase1_values (np.ndarray): Значення для осі 1.
        phase3_values (np.ndarray): Значення для осі 3.
        z_values (np.ndarray): Значення функції придатності.
    """

    fig = figure(figsize=(30, 30), dpi=150)
    ax = fig.add_subplot(111, projection="3d")

    X, Y = meshgrid(phase3_values, phase1_values)
    surf = ax.plot_surface(X, Y, z_values, cmap="viridis", edgecolor="none", alpha=.8)

    ax.set_xlabel("Фаза 3", fontsize=20)
    ax.set_ylabel("Фаза 1", fontsize=20)
    ax.set_zlabel("Функція придатності (max(L_i))", fontsize=20)
    ax.set_title("3D Поверхневий графік метрик", fontsize=24)
    fig.colorbar(surf, shrink=.6, aspect=10)
    ax.view_init(elev=30, azim=120)
    tight_layout()
    savefig("3d_surface_plot.png", bbox_inches="tight")
    close()


def main():
    """Головна функція для виконання прикладу."""

    data = read_data("paste.txt")
    phase1_values, phase3_values, z_values = prepare_surface_data(data)
    plot_3d_surface(phase1_values, phase3_values, z_values)
    print("3D поверхневий графік збережено як \"3d_surface_plot.png\"")


if __name__ == "__main__":
    main()

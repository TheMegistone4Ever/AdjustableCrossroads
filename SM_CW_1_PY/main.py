import matplotlib.pyplot as plt
from numpy import linspace, min, max, ceil

from digitgen import *

set_seed(1810)
size, samples_in_bin = 10_000, 250
total_bins = int(ceil(size / samples_in_bin))


def plot_distribution(data, density_func, F_X, F_X_inv, distribution_name, *params):
    bin_edges_theory, bin_edges = get_bin_edges(data, total_bins, F_X, F_X_inv, *params)

    x_min = min(data)
    x_max = max(data)
    df_space = linspace(x_min, x_max, size)

    plt.figure(figsize=(10, 6), dpi=200)
    plt.hist(data, bins=bin_edges, density=True, alpha=.6, color="b")
    plt.plot(df_space, density_func(df_space, *params), lw=3)

    for edge_theory, edge in zip(bin_edges_theory, bin_edges):
        plt.axvline(edge_theory, color="g", linestyle="-", lw=2, alpha=.4)
        plt.axvline(edge, color="r", linestyle="--", lw=1)

    chi_squared = chi_square_test(data, lambda v: density_func(v, *params), bin_edges)

    chi_square_stat, p_value, passed = chi_squared

    plt.title(f"{distribution_name} Distribution")
    plt.xlabel("Value")
    plt.ylabel("Density")

    test_result = f"Chi-square stat: {chi_square_stat:.2f}\np-value: {p_value:.8f}\nPassed: {passed}"
    print(f"{distribution_name} Distribution:")
    mu, std, variance = statistics(data)
    print(f"\tMean: {mu:.2f}\n\tStandard Deviation: {std:.2f}\n\tVariance: {variance:.2f}\n")
    print(f"Chi-Square Test:\n{test_result}\n")

    plt.text(.7, .8, test_result, transform=plt.gca().transAxes, fontsize=12, fontweight="bold",
             bbox=dict(facecolor="w", alpha=.8))

    plt.show()


gamma_values = [.5, 1, 2]
for gamma in gamma_values:
    x = exponential(size, gamma)
    plot_distribution(x, density_exponential, F_X_exponential, F_X_inv_exponential, "Exponential", gamma)

params_list = [(0, 1), (2, .5), (-1, 2)]
for mean, sigma in params_list:
    x = normal(size, mean, sigma)
    plot_distribution(x, density_normal, F_X_normal, F_X_inv_normal, "Normal", mean, sigma)

params_list = [(5 ** 13, 2 ** 31), (5 ** 12, 2 ** 15), (5 ** 11, 2 ** 7)]
for a, c in params_list:
    x = uniform(size, a, c)
    plot_distribution(x, density_uniform, F_X_uniform, F_X_inv_uniform, "Uniform")

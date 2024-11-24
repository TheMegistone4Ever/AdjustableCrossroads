from typing import Callable, Tuple

from numpy import array, histogram
from numpy.random import seed, uniform
from numpy.typing import NDArray


def psi(size: Tuple[int, int] = None, interval: Tuple[float, float] = (0., 1.)) -> float | NDArray:
    """Returns a random number in the given interval.

    :param interval: tuple, the interval of the random number
    :type interval: tuple
    :param size: int, the number of random numbers to generate
    :type size: int
    :return: float, the random number
    :rtype: float | NDArray
    """

    return uniform(*interval, size)


def set_seed(seed_value: int) -> None:
    """Sets the seed for the random number generator.

    :param seed_value: The seed value to set.
    :type seed_value: int
    """

    seed(seed_value)


def get_bin_edges(data: NDArray, total_bins: int, F_X: Callable, F_X_inv: Callable,
                  *params: Tuple[float, ...], min_size: int = 5) -> Tuple[NDArray, NDArray]:
    """
    Calculates the bin edges for a histogram based on the given data and distribution functions.
    Word theorems and proofs in a way that is straightforward to understand.

    :param data: The data to create the histogram from.
    :type data: NDArray
    :param total_bins: The number of bins to use.
    :type total_bins: int
    :param F_X: The cumulative distribution function.
    :type F_X: Callable
    :param F_X_inv: The inverse cumulative distribution function.
    :type F_X_inv: Callable
    :param params: The parameters for the distribution functions.
    :param min_size: The minimum number of counts in a bin.
    :type min_size: int
    :return: The theoretical bin edges and final bin edges after merging small counts.
    :rtype: Tuple[NDArray, NDArray]
    """

    sorted_data = sorted(data)
    interval_area = (F_X(sorted_data[-1], *params) - F_X(sorted_data[0], *params)) / total_bins
    bin_edges_theory = [sorted_data[0]]
    for i in range(total_bins):
        bin_edges_theory.append(F_X_inv(F_X(bin_edges_theory[-1], *params) + interval_area, *params))
    bin_edges_theory.append(sorted_data[-1])

    counts, edges = histogram(data, bins=total_bins)
    accumulated_count, bin_edges = 0, [edges[0]]
    for new_count, edge in zip(counts, edges[1:]):
        accumulated_count += new_count
        if accumulated_count >= min_size:
            bin_edges.append(edge)
            accumulated_count = 0
    if counts[-1] < min_size:
        bin_edges.pop(-1)
        bin_edges.append(edges[-1])

    return array(bin_edges_theory), array(bin_edges)


def statistics(data: NDArray) -> Tuple[float, float, float]:
    """Calculates the mean, standard deviation, and variance of the given data.

    :param data: The data to calculate the statistics for.
    :type data: NDArray
    :return: The mean, standard deviation, and variance of the data.
    :rtype: Tuple[float, float, float]
    """

    return data.mean(), data.std(), data.var()

from typing import Callable, Tuple

from numpy import histogram, array
from numpy.typing import NDArray
from scipy.stats import chi2


def chi_squared_statistic(observed: NDArray, expected: NDArray) -> float:
    """Calculates the chi-squared statistic for observed and expected frequencies.

    :param observed: The observed frequencies.
    :type observed: NDArray
    :param expected: The expected frequencies.
    :type expected: NDArray
    :return: The chi-squared statistic.
    :rtype: float
    """

    return float(((observed - expected) ** 2 / expected).sum())


def chi_square_p_value(chi_square: float, df: int) -> float:
    """Calculates the p-value from a chi-squared statistic and degrees of freedom.

    :param chi_square: The chi-squared statistic.
    :type chi_square: float
    :param df: The degrees of freedom.
    :type df: int
    :return: The p-value corresponding to the chi-squared statistic.
    :rtype: float
    """

    return float(1 - chi2.cdf(chi_square, df))


def chi_square_test(observed: NDArray, density_func: Callable,
                    bin_edges: NDArray, alpha: float = .05) -> Tuple[float, float, bool]:
    """Performs a chi-squared test and prints the result.

    :param observed: The observed frequencies.
    :type observed: NDArray
    :param density_func: The Probability Density Function (PDF) of the distribution.
    :type density_func: Callable
    :param bin_edges: The bin edges to use in the histogram.
    :type bin_edges: NDArray
    :param alpha: The significance level for the test.
    :type alpha: float, optional
    :return: The chi-squared statistic, p-value, and whether the null hypothesis is accepted.
    :rtype: Tuple[float, float, bool]
    """

    bin_widths = array([bin_edges[i + 1] - bin_edges[i] for i in range(len(bin_edges) - 1)])
    bin_midpoints = array([(bin_edges[i + 1] + bin_edges[i]) / 2 for i in range(len(bin_edges) - 1)])
    expected_prob = density_func(bin_midpoints) * bin_widths
    expected_freq = len(observed) * expected_prob

    observed_freq = histogram(observed, bins=bin_edges)[0]
    chi_square_stat = chi_squared_statistic(observed_freq, expected_freq)
    p_value = chi_square_p_value(chi_square_stat, len(bin_edges) - 1)

    return chi_square_stat, p_value, p_value > alpha

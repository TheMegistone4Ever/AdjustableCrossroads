from typing import Tuple, Any

from numpy import exp, sqrt, pi, dtype, ndarray, array
from scipy.special import erfinv, erf

from .utils import psi, NDArray


def mu(size: Tuple[int, int]) -> NDArray:
    """
    Generate random numbers from uniform distribution.

    :param size: The number of random numbers to generate.
    :type size: int
    :return: Random numbers from uniform distribution.
    :rtype: NDArray
    """

    return psi(size).sum(axis=1) - 6


def normal(size: int, mean: float, sigma: float) -> NDArray:
    """
    Generate random numbers from normal distribution.

    :param size: The number of random numbers to generate.
    :type size: int
    :param mean: The mean of the normal distribution.
    :type mean: float
    :param sigma: The standard deviation of the normal distribution.
    :type sigma: float
    :return: Random numbers from normal distribution.
    :rtype: NDArray
    """

    return sigma * mu((size, 12)) + mean


def density_normal(x: NDArray, mean: float, sigma: float) -> float | NDArray:
    """
    Calculate the probability density function of normal distribution.

    :param x: The value at which to calculate the probability density function.
    :type x: float
    :param mean: The mean of the normal distribution.
    :type mean: float
    :param sigma: The standard deviation of the normal distribution.
    :type sigma: float
    :return: The probability density function of normal distribution.
    :rtype: float
    """

    return exp(-((x - mean) ** 2) / (2 * sigma ** 2)) / (sigma * sqrt(2 * pi))


def F_X_normal(x: float, mean: float, sigma: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the cumulative distribution function of normal distribution.

    :param x: The value at which to calculate the cumulative distribution function.
    :type x: float
    :param mean: The mean of the normal distribution.
    :type mean: float
    :param sigma: The standard deviation of the normal distribution.
    :type sigma: float
    :return: The cumulative distribution function of normal distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return array(.5 * (1 + erf((x - mean) / (sigma * (2 ** .5)))))


def F_X_inv_normal(x: float, mean: float, sigma: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the inverse cumulative distribution function of normal distribution.

    :param x: The value at which to calculate the inverse cumulative distribution function.
    :type x: float
    :param mean: The mean of the normal distribution.
    :type mean: float
    :param sigma: The standard deviation of the normal distribution.
    :type sigma: float
    :return: The inverse cumulative distribution function of normal distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return array(mean + sigma * (2 ** .5) * erfinv(2 * x - 1))

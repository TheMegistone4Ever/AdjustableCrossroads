from typing import Any, Tuple

from numpy import log, exp, ndarray, dtype

from .utils import psi, NDArray


def exponential(size: int | Tuple[int, int], gamma: float) -> NDArray:
    """
    Generate random numbers from exponential distribution.

    :param size: The number of random numbers to generate.
    :type size: int
    :param gamma: The rate parameter.
    :type gamma: float
    :return: Random numbers from exponential distribution.
    :rtype: NDArray
    """

    return - 1 / gamma * log(psi(size))


def density_exponential(x: ndarray[Any, dtype[Any]], gamma: float) -> float:
    """
    Calculate the probability density function of exponential distribution.

    :param x: The value at which to calculate the probability density function.
    :type x: float
    :param gamma: The rate parameter.
    :type gamma: float
    :return: The probability density function of exponential distribution.
    :rtype: float
    """

    return gamma * exp(-gamma * x)


def F_X_exponential(x: float, gamma: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the cumulative distribution function of exponential distribution.

    :param x: The value at which to calculate the cumulative distribution function.
    :type x: float
    :param gamma: The rate parameter.
    :type gamma: float
    :return: The cumulative distribution function of exponential distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return 1 - exp(-gamma * x)


def F_X_inv_exponential(x: float, gamma: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the inverse cumulative distribution function of exponential distribution.

    :param x: The value at which to calculate the inverse cumulative distribution function.
    :type x: float
    :param gamma: The rate parameter.
    :type gamma: float
    :return: The inverse cumulative distribution function of exponential distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return - log(1 - x) / gamma

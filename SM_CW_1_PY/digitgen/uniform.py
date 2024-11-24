from typing import Any

from numpy import array, where, ndarray, dtype

from .utils import psi


def uniform(size: int, a: int, c: int) -> ndarray[Any, dtype[Any]]:
    """
    Generate random numbers from uniform distribution.

    :param size: The number of random numbers to generate.
    :type size: int
    :param a: The multiplier.
    :type a: int
    :param c: The modulus.
    :type c: int
    :return: Random numbers from uniform distribution.
    :rtype: NDArray
    """

    z = psi(interval=(0, c))
    return array([(z := (a * z) % c) / c for _ in range(size)])


def density_uniform(x: ndarray[Any, dtype[Any]]) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the probability density function of uniform distribution.

    :param x: The value at which to calculate the probability density function.
    :type x: float
    :return: The probability density function of uniform distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return where((0. <= x) & (x <= 1.), 1., 0.)


def F_X_uniform(x: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the cumulative distribution function of uniform distribution.

    :param x: The value at which to calculate the cumulative distribution function.
    :type x: float
    :return: The cumulative distribution function of uniform distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return where(x < 0., 0., where(x > 1., 1., x))


def F_X_inv_uniform(x: float) -> ndarray[Any, dtype[Any]]:
    """
    Calculate the inverse cumulative distribution function of uniform distribution.

    :param x: The value at which to calculate the inverse cumulative distribution function.
    :type x: float
    :return: The inverse cumulative distribution function of uniform distribution.
    :rtype: ndarray[Any, dtype[Any]]
    """

    return where(x < 0., 0., where(x > 1., 1., x))

import random
import time


def chance(probability):
    if not 0 <= probability <= 1:
        raise ValueError("Probability must be a double between 0 and 1.")
    return random.random() < probability


def wait_random_duration(min_seconds, max_seconds):
    if min_seconds > max_seconds:
        raise ValueError("min_seconds must not be greater than max_seconds.")
    duration = random.uniform(min_seconds, max_seconds)
    time.sleep(duration)

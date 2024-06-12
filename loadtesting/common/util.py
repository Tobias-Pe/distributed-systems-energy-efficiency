import random
import time
from faker import Faker

fake = Faker()

users = {}
feeds = {}
posts = {}
categories = []


def wait_random_duration(min_seconds, max_seconds):
    if min_seconds > max_seconds:
        raise ValueError("min_seconds must not be greater than max_seconds.")
    duration = random.uniform(min_seconds, max_seconds)
    time.sleep(duration)


def if_no_user_exists_wait():
    for _ in range(10):
        if not users:
            time.sleep(2)
        else:
            return
    raise Exception("No users present after waiting.")

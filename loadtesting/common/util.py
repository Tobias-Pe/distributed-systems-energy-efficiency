import random
import time
from faker import Faker

fake = Faker()

users = {}

posts = {}


def wait_random_duration(min_seconds, max_seconds):
    if min_seconds > max_seconds:
        raise ValueError("min_seconds must not be greater than max_seconds.")
    duration = random.uniform(min_seconds, max_seconds)
    time.sleep(duration)


def if_no_user_exists_wait():
    while len(users) == 0:
        print("No users present. Waiting...")
        time.sleep(2)
        return

import random
import time
from faker import Faker

fake = Faker()

users = {}
feeds = {}
post_ids = set()
categories = set()


def wait_random_duration(min_seconds, max_seconds):
    if min_seconds > max_seconds:
        raise ValueError("min_seconds must not be greater than max_seconds.")
    duration = random.uniform(min_seconds, max_seconds)
    time.sleep(duration)


def if_no_user_exists_wait(caller):
    for _ in range(5):
        if not users:
            time.sleep(1)
        else:
            return
    print("No users present after waiting. Interrupting my task set...")
    caller.interrupt()

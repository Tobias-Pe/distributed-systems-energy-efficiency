import random
from locust import HttpUser, task, between, TaskSet
from faker import Faker
import time

fake = Faker()

users = {}

posts = {}


def chance(probability):
    if not 0 <= probability <= 1:
        raise ValueError("Probability must be a double between 0 and 1.")
    return random.random() < probability


def wait_random_duration(min_seconds, max_seconds):
    if min_seconds > max_seconds:
        raise ValueError("min_seconds must not be greater than max_seconds.")
    duration = random.uniform(min_seconds, max_seconds)
    time.sleep(duration)


class UserActions(TaskSet):
    def handle_user_respone(self, response):
        if response.status_code >= 500:
            response.failure(response.text)
        elif response.status_code == 201:
            users[response.json().get("id")] = response.json().get("username")
        response.success()

    @task
    def create_user(self):
        with self.client.post("/userservice/users", json={"username": fake.user_name()}, name="/userservice/users",
                              catch_response=True) as response:
            self.handle_user_respone(response)

    def if_no_user_exists_create(self):
        if len(users) == 0:
            self.create_user()

    @task
    def edit_profile(self):
        self.if_no_user_exists_create()
        user_id = random.choice(list(users.keys()))
        with self.client.put(f"/userservice/users/{user_id}", json={"username": fake.user_name()},
                             name="/userservice/users/{id}",
                             catch_response=True) as response:
            self.handle_user_respone(response)

    @task(8)
    def follow(self):
        self.if_no_user_exists_create()
        follower_id = random.choice(list(users.keys()))
        to_be_subscribed_username = random.choice(list(users.values()))
        self.client.put(f"/userservice/users/{follower_id}/follow?toBeFollowedUsername={to_be_subscribed_username}",
                        name="/userservice/users/{id}/follow")

    @task
    def get_followers(self):
        self.if_no_user_exists_create()
        user_id = random.choice(list(users.keys()))
        self.client.get(f"/userservice/users/{user_id}/followers", name="/userservice/users/{id}/followers")

    @task
    def get_user(self):
        self.if_no_user_exists_create()
        user_id = random.choice(list(users.keys()))
        self.client.get(f"/userservice/users/{user_id}", name="/userservice/users/{id}")

    @task(2)
    def search_user(self):
        letters = ''.join(fake.random_letters(fake.random_int(1, 4, 1)))
        if chance(0.33):
            letters += "%"
        elif chance(0.33):
            letters = "%" + letters
        else:
            letters = "%" + letters + "%"
        page = 0
        self.search_user_paginated(letters, page)

    def search_user_paginated(self, query, page):
        with self.client.post(f"/userservice/users/search?page={page}&query={query}", name="/userservice/users/search",
                              catch_response=True) as response:
            if response.json().get("totalPages") > page and chance(0.5):
                response.success()
                wait_random_duration(0.5, 5)
                self.search_user_paginated(query, page + 1)


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = [UserActions]

from locust import HttpUser, task, between, events
from random_username.generate import generate_username
import random

users = {}


class UserserviceUser(HttpUser):
    wait_time = between(0.5, 5)

    @task
    def create_user(self):
        with self.client.post("/users", json={"username": generate_username(1)[0]}, catch_response=True) as response:
            if response.status_code >= 500:
                response.failure(response.text)
            elif response.status_code == 201:
                users[response.json().get("id")] = response.json().get("username")

    @task
    def follow(self):
        if len(users) <= 1:
            self.create_user()
        follower_id = random.choice(list(users.keys()))
        to_be_subscribed_username = random.choice(list(users.values()))
        self.client.post(f"/users/{follower_id}/follow?toBeFollowedUsername={to_be_subscribed_username}")

    @task
    def get_followers(self):
        if len(users) == 0:
            self.create_user()
        user_id = random.choice(list(users.keys()))
        self.client.get(f"/users/{user_id}/followers")

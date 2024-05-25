import random

from locust import TaskSet, task
from common.util import chance, wait_random_duration, users, fake


def handle_user_respone(response):
    if response.status_code >= 500:
        response.failure(response.text)
    elif response.status_code == 201:
        users[response.json().get("id")] = response.json().get("username")
    response.success()


class UserActions(TaskSet):

    @task
    def create_user(self):
        with self.client.post("/userservice/users", json={"username": fake.user_name()}, name="/userservice/users",
                              catch_response=True) as response:
            handle_user_respone(response)

    def if_no_user_exists_create(self):
        if len(users) == 0:
            print("No users present, creating one before continuing...")
            self.create_user()

    @task
    def edit_profile(self):
        self.if_no_user_exists_create()
        user_id = random.choice(list(users.keys()))
        with self.client.put(f"/userservice/users/{user_id}", json={"username": fake.user_name()},
                             name="/userservice/users/{id}",
                             catch_response=True) as response:
            handle_user_respone(response)

    @task(8)
    def follow(self):
        self.if_no_user_exists_create()
        follower_id = random.choice(list(users.keys()))
        to_be_subscribed_username = random.choice(list(users.values()))
        with self.client.put(f"/userservice/users/{follower_id}/follow?toBeFollowedUsername={to_be_subscribed_username}",
                        name="/userservice/users/{id}/follow", catch_response=True) as response:
            if response.status_code >= 500:
                response.failure(response.text)
                return
            response.success()

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
        choice = random.choice(["start", "end", "both"])
        if choice == "start":
            letters += "%"
        elif choice == "end":
            letters = "%" + letters
        else:
            letters = "%" + letters + "%"
        page = 0
        self.search_user_paginated(letters, page)

    def search_user_paginated(self, query, page):
        with self.client.post(f"/userservice/users/search?page={page}&query={query}", name="/userservice/users/search",
                              catch_response=True) as response:
            if response.status_code >= 400:
                response.failure(response.text)
                return
            response.success()
            if response.json().get("totalPages") > page and chance(0.5):
                wait_random_duration(0.5, 5)
                self.search_user_paginated(query, page + 1)

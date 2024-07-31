import random

from locust import TaskSet, task

from common.util import if_no_user_exists_wait, users, feeds, fake


class FeedActions(TaskSet):

    @task
    def get_users_feed(self):
        if_no_user_exists_wait(self)
        user_id = random.choice(list(users.keys()))
        with self.client.get(f"/feedservice/feeds/{user_id}",
                        name="/feedservice/feeds/{user_id}", catch_response=True) as response:
            if response.status_code != 200:
                response.failure(response.text)
                return
            response.success()
            feeds[user_id] = [post['id'] for post in response.json().get('content', [])]
        self.interrupt()

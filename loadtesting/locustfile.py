from locust import HttpUser, between, LoadTestShape

from common.feedservice import FeedActions
from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 30, PostActions: 30, FeedActions: 15, NotificationActions: 10}

duration = 10

stages = [
    {"duration": duration*1, "users": 100, "spawn_rate":50},
    {"duration": duration*2, "users": 200, "spawn_rate":50},
    {"duration": duration*3, "users": 400, "spawn_rate":50}
]

class StagedLoadShape(LoadTestShape):
    def tick(self):
        run_time = self.get_run_time()

        for stage in stages:
            if run_time < stage["duration"]:
                # user count, spawn rate
                tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        return None

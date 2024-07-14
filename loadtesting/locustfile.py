from locust import HttpUser, between, LoadTestShape
import locust.stats
from common.feedservice import FeedActions
from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 20, PostActions: 30, FeedActions: 15, NotificationActions: 10}

minutes = 30

stages = [
    {"duration": 60 * minutes, "users": 100, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 200, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 300, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 500, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 800, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 1300, "spawn_rate": 2}
]

cumulative_duration = 0
for stage in stages:
    cumulative_duration += stage["duration"]
    stage["cumulative_duration"] = cumulative_duration


class StagedLoadShape(LoadTestShape):
    def tick(self):
        run_time = self.get_run_time()

        for stage in stages:
            if run_time < stage["cumulative_duration"]:
                # user count, spawn rate
                tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        return None

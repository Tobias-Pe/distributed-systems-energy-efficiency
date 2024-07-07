from locust import HttpUser, between, LoadTestShape
import locust.stats
from common.feedservice import FeedActions
from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 30, PostActions: 30, FeedActions: 15, NotificationActions: 10}


multiplier = 5

stages = [
    {"duration": 60 * multiplier, "users": 100, "spawn_rate": 5},
    {"duration": 40 * multiplier, "users": 200, "spawn_rate": 20},
    {"duration": 40 * multiplier, "users": 300, "spawn_rate": 20},
    {"duration": 60 * multiplier, "users": 500, "spawn_rate": 20},
    {"duration": 80 * multiplier, "users": 800, "spawn_rate": 20},
    {"duration": 80 * multiplier, "users": 1300, "spawn_rate": 50},
    {"duration": 100 * multiplier, "users": 2100, "spawn_rate": 50}
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

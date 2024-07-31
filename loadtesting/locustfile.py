from locust import HttpUser, between, LoadTestShape
import locust.stats
from common.feedservice import FeedActions
from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 20, PostActions: 30, FeedActions: 15, NotificationActions: 10}

minutes = 20

stages = [
    {"duration": 60 * minutes, "users": 100, "spawn_rate": 2},
    {"duration": 60 * minutes, "users": 200, "spawn_rate": 1},
    {"duration": 60 * minutes, "users": 300, "spawn_rate": 1},
    {"duration": 60 * minutes, "users": 400, "spawn_rate": 1},
    {"duration": 60 * minutes, "users": 500, "spawn_rate": 1}
]

cumulative_duration = 0
for stage in stages:
    cumulative_duration += stage["duration"]
    stage["cumulative_duration"] = cumulative_duration

current_stage = None

class StagedLoadShape(LoadTestShape):
    def tick(self):
        global current_stage  # Declare current_stage as global to modify it
        run_time = self.get_run_time()

        for stage in stages:
            if run_time < stage["cumulative_duration"]:
                if current_stage != stage:
                    current_stage = stage
                    print(f"New stage: {stage}")
                # user count, spawn rate
                tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        return None

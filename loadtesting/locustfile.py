from locust import HttpUser, between

from common.feedservice import FeedActions
from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 30, PostActions: 30, FeedActions: 15, NotificationActions: 10}

from locust import HttpUser, between

from common.notificationservice import NotificationActions
from common.postservice import PostActions
from common.userservice import UserActions


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = {UserActions: 3, PostActions: 3, NotificationActions: 1}

from locust import HttpUser, between

from common.userservice import UserActions

users = {}

posts = {}


class GatewayUser(HttpUser):
    wait_time = between(0.5, 5)
    tasks = [UserActions]

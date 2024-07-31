import random

from locust import TaskSet, task

from common.util import if_no_user_exists_wait, users, fake


class NotificationActions(TaskSet):

    @task
    def count_notifications(self):
        if_no_user_exists_wait(self)
        user_id = random.choice(list(users.keys()))
        self.client.post(f"/notificationservice/notifications/count?userId={user_id}",
                         name="/notificationservice/notifications/count")
        self.interrupt()

    @task
    def get_notifications(self):
        if_no_user_exists_wait(self)
        user_id = random.choice(list(users.keys()))
        with self.client.get(f"/notificationservice/notifications?userId={user_id}",
                             name="/notificationservice/notifications", catch_response=True) as response:
            if not response.ok:
                response.failure(response.text)
                return
            response.success()

            # next task would be to change the read status of one of these notifications
            notifications = response.json()
            if notifications is None or len(notifications) == 0:
                return
            notification_id = random.choice(notifications).get("id")
            self.change_notification_read_status(notification_id)
        self.interrupt()

    def change_notification_read_status(self, notification_id):
        self.client.post(f"/notificationservice/notifications/{notification_id}?isRead={fake.boolean()}",
                         name="/notificationservice/notifications/{id}")

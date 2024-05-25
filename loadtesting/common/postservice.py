import os
import random

from locust import TaskSet, task
from common.util import chance, wait_random_duration, posts, users, fake
from faker_file.providers.png_file import GraphicPngFileProvider
from faker_file.providers.jpeg_file import GraphicJpegFileProvider

fake.add_provider(GraphicPngFileProvider)
fake.add_provider(GraphicJpegFileProvider)


class PostActions(TaskSet):

    @task
    def create_post(self):
        if len(users) == 0:
            print("No users present")
            return
        user_id = random.choice(list(users.keys()))
        category_parameters, multipart_file, text = self.generate_post_data()
        self.client.post(f"/postservice/posts?userId={user_id}&text={text}{category_parameters}",
                         files={"image": multipart_file},
                         name="/postservice/posts")

    def generate_post_data(self):
        words = fake.words(nb=fake.random_int(1, 6, 1), unique=True)
        category_parameters = ""
        for word in words:
            category_parameters += f"&categories={word}"
        text = fake.text(max_nb_chars=500)
        width_height_size = (fake.random_int(200, 10000, 100), fake.random_int(200, 10000, 100))
        if chance(0.5):
            multipart_file = self.create_jpg(width_height_size)
        else:
            multipart_file = self.create_png(width_height_size)
        return category_parameters, multipart_file, text

    def create_jpg(self, size):
        image_file = fake.graphic_jpeg_file(raw=True, size=size)
        multipart_file = (fake.file_name(category="image", extension="jpg"), image_file, "image/jpeg")
        return multipart_file

    def create_png(self, size):
        image_file = fake.graphic_png_file(raw=True, size=size)
        multipart_file = (fake.file_name(category="image", extension="png"), image_file, "image/png")
        return multipart_file

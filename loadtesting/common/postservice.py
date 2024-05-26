import random

from faker_file.providers.jpeg_file import GraphicJpegFileProvider
from faker_file.providers.png_file import GraphicPngFileProvider
from locust import TaskSet, task

from common import util
from common.util import if_no_user_exists_wait, posts, users, categories, fake, wait_random_duration

fake.add_provider(GraphicPngFileProvider)
fake.add_provider(GraphicJpegFileProvider)


class PostActions(TaskSet):

    @task
    def create_post(self):
        if_no_user_exists_wait()

        user_id = random.choice(list(users.keys()))
        category_parameters, multipart_file, text = self.generate_post_data()
        with self.client.post(f"/postservice/posts?userId={user_id}&text={text}{category_parameters}",
                              files={"image": multipart_file},
                              name="/postservice/posts", catch_response=True) as response:
            if response.status_code != 201:
                response.failure(response.text)
            else:
                posts[response.json().get("id")] = response.json()
                response.success()

    def generate_post_data(self):
        words = fake.words(nb=fake.random_int(1, 6, 1), unique=True)
        category_parameters = ""
        # keep track of all categories
        util.categories.extend(words)
        for word in words:
            category_parameters += f"&categories={word}"
        text = fake.text(max_nb_chars=500)
        width_height_size = (fake.random_int(200, 8000, 100), fake.random_int(200, 8000, 100))
        if fake.boolean(50):
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

    def if_no_post_exists_create(self):
        if len(posts) == 0:
            self.create_post()

    @task(5)
    def like_post(self):
        self.if_no_post_exists_create()
        post_id = random.choice(list(posts.keys()))
        self.client.post(f"/postservice/posts/{post_id}/like", name="/postservice/posts/{id}/like")

    @task
    def get_post(self):
        self.if_no_post_exists_create()
        post_id = random.choice(list(posts.keys()))
        self.client.get(f"/postservice/posts/{post_id}", name="/postservice/posts/{id}")

    @task
    def get_post_image(self):
        self.if_no_post_exists_create()
        post_id = random.choice(list(posts.keys()))
        self.client.get(f"/postservice/posts/{post_id}/image", name="/postservice/posts/{id}/image")

    @task
    def comment_post(self):
        # abort if no user is present
        if len(users) == 0:
            print("No users present")
            return

        self.if_no_post_exists_create()
        post_id = random.choice(list(posts.keys()))
        user_id = random.choice(list(users.keys()))
        text = fake.text(max_nb_chars=200)
        self.client.post(f"/postservice/posts/{post_id}/comments",
                         json={"userId": user_id, "text": text},
                         name="/postservice/posts/{id}/comments")

    @task
    def get_categories(self):
        with self.client.get(f"/postservice/categories",
                             name="/postservice/categories", catch_response=True) as response:
            if not response.ok:
                response.failure(response.text)
                return
            response.success()
            categories = response.json().get("content")
            if categories is None or len(categories) == 0:
                return
            category = random.choice(categories).get("name")
            self.get_posts_by_category(category)

    def get_posts_by_category(self, category):
        self.client.get(f"/postservice/posts?category={category}", name="/postservice/posts")

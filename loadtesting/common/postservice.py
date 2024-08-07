import random

from faker_file.providers.jpeg_file import GraphicJpegFileProvider
from faker_file.providers.png_file import GraphicPngFileProvider
from locust import TaskSet, task

from common import util
from common.util import if_no_user_exists_wait, post_ids, users, feeds, categories, fake, wait_random_duration

fake.add_provider(GraphicPngFileProvider)
fake.add_provider(GraphicJpegFileProvider)


class PostActions(TaskSet):

    @task
    def create_post(self):
        if_no_user_exists_wait(self)

        user_id = random.choice(list(users.keys()))
        category_parameters, multipart_file, text = self.generate_post_data()
        with self.client.post(f"/postservice/posts?userId={user_id}&text={text}{category_parameters}",
                              files={"image": multipart_file},
                              name="/postservice/posts", catch_response=True) as response:
            if response.status_code != 201:
                response.failure(response.text)
            else:
                post_ids.add(response.json().get("id"))
                response.success()
        self.interrupt()

    def generate_post_data(self):
        words = fake.words(nb=fake.random_int(1, 6, 1), unique=True)
        category_parameters = ""
        # keep track of all categories
        util.categories.update(words)
        for word in words:
            category_parameters += f"&categories={word}"
        text = fake.text(max_nb_chars=500)
        width_height_size = (fake.random_int(200, 6000, 100), fake.random_int(200, 6000, 100))
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
        if len(post_ids) == 0:
            self.create_post()

    @task(5)
    def like_post(self):
        post_id, user_id = self.get_post_and_user_pair()
        self.client.post(f"/postservice/posts/{post_id}/like?userId={user_id}", name="/postservice/posts/{id}/like")
        self.interrupt()

    @task
    def get_post(self):
        self.if_no_post_exists_create()
        post_id = random.choice(tuple(post_ids))
        self.client.get(f"/postservice/posts/{post_id}", name="/postservice/posts/{id}")
        self.interrupt()

    @task
    def get_post_image(self):
        self.if_no_post_exists_create()
        post_id = random.choice(tuple(post_ids))
        self.client.get(f"/postservice/posts/{post_id}/image", name="/postservice/posts/{id}/image")
        self.interrupt()

    @task
    def comment_post(self):
        post_id, user_id = self.get_post_and_user_pair()
        text = fake.text(max_nb_chars=200)
        self.client.post(f"/postservice/posts/{post_id}/comments",
                         json={"userId": user_id, "text": text},
                         name="/postservice/posts/{id}/comments")
        self.interrupt()

    def get_post_and_user_pair(self):
        if len(feeds) > 0 and fake.boolean(50):
            user_id, feed_post_ids = random.choice(list(feeds.items()))
            if len(feed_post_ids)>0:
                post_id = random.choice(feed_post_ids)
                return post_id, user_id
        if_no_user_exists_wait(self)
        self.if_no_post_exists_create()
        post_id = random.choice(tuple(post_ids))
        user_id = random.choice(list(users.keys()))
        return post_id, user_id

    @task
    def get_categories(self):
        self.get_categories_paginated(0)
        self.interrupt()

    def get_categories_paginated(self, page):
        with self.client.get(f"/postservice/categories",
                             name="/postservice/categories", catch_response=True) as response:
            if not response.ok:
                response.failure(response.text)
                return
            response.success()
            # if there is a next page query it with a change of 50%
            if response.json().get("totalPages") > page + 1 and fake.boolean(50):
                wait_random_duration(0.5, 5)
                self.get_categories_paginated(page + 1)

    @task
    def get_posts(self):
        user_id = None
        if users and fake.boolean(50):
            user_id = random.choice(list(users.keys()))

        category = None
        if categories and fake.boolean(50):
            category = random.choice(tuple(categories))

        self.get_posts_paginated_filtered(0, category=category, user_id=user_id)
        self.interrupt()

    def get_posts_paginated_filtered(self, page, category=None, user_id=None):
        params = f"?page={page}"
        if category is not None:
            params += f"&category={category}"
        if user_id is not None:
            params += f"&userId={user_id}"
        with self.client.get(f"/postservice/posts?category={category}", name="/postservice/posts",
                             catch_response=True) as response:
            if not response.ok:
                response.failure(response.text)
                return
            response.success()
            # if there is a next page query it with a change of 50%
            if response.json().get("totalPages") > page + 1 and fake.boolean(50):
                wait_random_duration(0.5, 5)
                self.get_posts_paginated_filtered(page + 1, category, user_id)

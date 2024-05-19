package edu.hm.peslalz.thesis.postservice;

import edu.hm.peslalz.thesis.postservice.controller.CategoryController;
import edu.hm.peslalz.thesis.postservice.controller.CommentController;
import edu.hm.peslalz.thesis.postservice.controller.PostController;
import edu.hm.peslalz.thesis.postservice.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
class PostserviceApplicationTests {

    @Autowired
    private PostController postController;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private CommentController commentController;

    @Test
    void scenario() {
        Post postFirst = postController.createPost(new PostRequest(1, "MyFirstPost", Set.of("beginnings", "blog")));
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        postController.likePost(postFirst.getId());
        postController.commentPost(postFirst.getId(), new CommentRequest(2, "What a first post! Wow :)"));
        postFirst = postController.getPost(postFirst.getId());
        Assertions.assertThat(postFirst).isNotNull();
        Assertions.assertThat(postFirst.getComments()).hasSize(1);
        Assertions.assertThat(postFirst.getLikes()).isEqualTo(1);
        assertThrows(ResponseStatusException.class, () -> commentController.likeComment(12345));
        assertThrows(ResponseStatusException.class, () -> postController.getPostsByCategory("12345"));
        assertThrows(ResponseStatusException.class, () -> postController.getPost(12345));
        Comment comment = commentController.likeComment(postFirst.getComments().stream().findFirst().get().getId());
        Assertions.assertThat(comment.getLikes()).isEqualTo(1);

        postController.createPost(new PostRequest(1, "NewzFromMyLaif", Set.of("life", "blog")));
        List<Category> categories = categoryController.getCategories(0).getContent();
        Assertions.assertThat(categories).hasSize(3);
        Set<Post> blogPosts = categories.stream().filter(category -> "blog".equals(category.getName())).findFirst().get().getPosts();
        Assertions.assertThat(blogPosts).hasSize(2);
    }

    @Test
    void parallelLikes() {
        Post postFirst = postController.createPost(new PostRequest(1, "MyFirstPost", Set.of("beginnings", "blog")));
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        Post finalPostFirst = postFirst;
        IntStream.range(0, 20).parallel().forEach(i -> postController.likePost(finalPostFirst.getId()));
        postFirst = postController.getPost(postFirst.getId());
        Assertions.assertThat(postFirst.getLikes()).isEqualTo(20);
    }

}

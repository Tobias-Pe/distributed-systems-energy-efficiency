package edu.hm.peslalz.thesis.postservice;

import edu.hm.peslalz.thesis.postservice.client.UserClient;
import edu.hm.peslalz.thesis.postservice.controller.CategoryController;
import edu.hm.peslalz.thesis.postservice.controller.CommentController;
import edu.hm.peslalz.thesis.postservice.controller.PostController;
import edu.hm.peslalz.thesis.postservice.entity.*;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class PostserviceApplicationTests {

    @Autowired
    private PostController postController;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private CommentController commentController;

    @MockBean
    private UserClient userClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void scenario() throws IOException {
        Mockito.when(userClient.getUserAccount(ArgumentMatchers.anyInt())).thenReturn(ResponseEntity.ok().build());
        File file = ResourceUtils.getFile("classpath:ExampleImage.png");
        byte[] imageBytes = Files.readAllBytes(file.toPath());
        Post postFirst = postController.createPost(new PostRequest(1, "MyFirstPost", Set.of("beginnings", "blog")), new MockMultipartFile(file.getName(), file.getName(), "image/png", imageBytes));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("notifications"), any(String.class));
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        Assertions.assertThat(Objects.requireNonNull(postController.getPostImage(postFirst.getId()).getBody()).getContentAsByteArray()).isEqualTo(imageBytes);
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

        postController.createPost(new PostRequest(1, "NewzFromMyLaif", Set.of("life", "blog")), null);
        List<Category> categories = categoryController.getCategories(0).getContent();
        Assertions.assertThat(categories).hasSize(3);
        Set<Post> blogPosts = categories.stream().filter(category -> "blog".equals(category.getName())).findFirst().get().getPosts();
        Assertions.assertThat(blogPosts).hasSize(2);
    }

    @Test
    void scenarioUserNotFound() {
        Mockito.when(userClient.getUserAccount(ArgumentMatchers.anyInt())).thenThrow(FeignException.class);
        PostRequest postRequest = new PostRequest(1, null, null);
        assertThrows(ResponseStatusException.class, () -> postController.createPost(postRequest, null));
    }

    @Test
    void parallelLikes() {
        Post postFirst = postController.createPost(new PostRequest(1, "MyFirstPost", Set.of("beginnings", "blog")), null);
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        Post finalPostFirst = postFirst;
        IntStream.range(0, 20).parallel().forEach(i -> postController.likePost(finalPostFirst.getId()));
        postFirst = postController.getPost(postFirst.getId());
        Assertions.assertThat(postFirst.getLikes()).isEqualTo(20);
    }

}

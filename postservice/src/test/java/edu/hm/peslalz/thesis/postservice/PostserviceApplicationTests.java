package edu.hm.peslalz.thesis.postservice;

import edu.hm.peslalz.thesis.postservice.controller.CategoryController;
import edu.hm.peslalz.thesis.postservice.controller.CommentController;
import edu.hm.peslalz.thesis.postservice.controller.PostController;
import edu.hm.peslalz.thesis.postservice.entity.*;
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
    private RabbitTemplate rabbitTemplate;

    @Test
    void scenario() throws Exception {
        Mockito.when(rabbitTemplate.convertSendAndReceive(any(String.class), any(String.class), any(Integer.class))).thenReturn(true);
        File file = ResourceUtils.getFile("classpath:ExampleImage.png");
        byte[] imageBytes = Files.readAllBytes(file.toPath());
        Post postFirst = postController.createPost(1, "MyFirstPost", Set.of("beginnings", "blog"), new MockMultipartFile(file.getName(), file.getName(), "image/png", imageBytes)).call();
        verify(rabbitTemplate, times(1)).convertAndSend(eq("postservice.direct"), eq("post"),any(String.class));
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        Assertions.assertThat(Objects.requireNonNull(postController.getPostImage(postFirst.getId()).call().getBody()).getContentAsByteArray()).isEqualTo(imageBytes);
        postController.likePost(postFirst.getId(), 1).call();
        postController.commentPost(postFirst.getId(), new CommentRequest(2, "What a first post! Wow :)")).call();
        postFirst = postController.getPost(postFirst.getId()).call();
        Assertions.assertThat(postFirst).isNotNull();
        Assertions.assertThat(postFirst.getComments()).hasSize(1);
        Assertions.assertThat(postFirst.getLikes()).isEqualTo(1);
        assertThrows(ResponseStatusException.class, () -> commentController.likeComment(12345).call());
        assertThrows(ResponseStatusException.class, () -> postController.getPost(12345).call());
        Assertions.assertThat(postController.getPosts("blog", null, 0, 50).call()).hasSize(1);
        Assertions.assertThat(postController.getPosts(null, 1, 0, 50).call()).hasSize(1);
        Comment comment = commentController.likeComment(postFirst.getComments().stream().findFirst().get().getId()).call();
        Assertions.assertThat(comment.getLikes()).isEqualTo(1);

        postController.createPost(1, "NewzFromMyLaif", Set.of("life", "blog"), null).call();
        List<String> categories = categoryController.getCategories(0).call().getContent();
        Assertions.assertThat(categories).hasSize(3);
    }

    @Test
    void scenarioUserNotFound() {
        Mockito.when(rabbitTemplate.convertSendAndReceive(any(String.class), any(String.class), any(Integer.class))).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> postController.createPost(1, null, null, null).call());
    }

    //@Test
    void parallelLikes() throws Exception {
        Mockito.when(rabbitTemplate.convertSendAndReceive(any(String.class), any(String.class), any(Integer.class))).thenReturn(true);
        Post postFirst = postController.createPost(1, "MyFirstPost", Set.of("beginnings", "blog"), null).call();
        Assertions.assertThat(postFirst.getCategories()).hasSize(2);
        Post finalPostFirst = postFirst;
        IntStream.range(0, 20).parallel().forEach(i -> {
            try {
                postController.likePost(finalPostFirst.getId(),1).call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        postFirst = postController.getPost(postFirst.getId()).call();
        Assertions.assertThat(postFirst.getLikes()).isEqualTo(20);
    }

}

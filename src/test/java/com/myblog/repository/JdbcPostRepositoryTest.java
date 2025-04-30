package com.myblog.repository;

import com.myblog.configuration.DataSourceConfiguration;
import com.myblog.model.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcPostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcPostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        // Добавление тестовых данных
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_1', 'text_1', 'tags_1', 'image_path_1', 1)");
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_2', 'text_2', 'tags_2', 'image_path_2', 2)");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllPosts() {
        assertEquals(2, postRepository.findPosts("").size());
        jdbcTemplate.execute("INSERT INTO posts (id, title, text, tags, image_path, likes_count) VALUES (3, 'title_3', 'text_3', 'tags_3', 'image_path_3', 3)");
        assertEquals(3, postRepository.findPosts("").size());
        assertEquals(1, postRepository.findPosts("tags_2").size());
    }

    @Test
    void getPostById() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);
        assertNotNull(post);
    }

    @Test
    void addPost() {
        assertEquals(2, postRepository.findPosts("").size());
        Post post = new Post(null, "title_3", "text_3", "tags_3", "image_path_3", 3);
        postRepository.addPost(post);
        assertEquals(3, postRepository.findPosts("").size());
    }

    @Test
    void likePost() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);
        Integer likesCount = post.getLikesCount();

        postRepository.likePost(postId, true);
        post = postRepository.getPostById(postId);
        assertEquals(likesCount + 1, post.getLikesCount());

        postRepository.likePost(postId, false);
        post = postRepository.getPostById(postId);
        assertEquals(likesCount, post.getLikesCount());
    }

    @Test
    void editPost() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);
        post.setTitle("changedTitle");
        post.setText("changedText");
        post.setTags("changedTags");
        post.setImagePath("changedImagePath");
        postRepository.editPost(post);

        post = postRepository.getPostById(postId);
        assertEquals("changedTitle", post.getTitle());
        assertEquals("changedText", post.getText());
        assertEquals("changedTags", post.getTags());
        assertEquals("changedImagePath", post.getImagePath());
    }

    @Test
    void addComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);
        Integer commentsCount = post.getComments().size();
        postRepository.addComment(postId, "postComment");
        post = postRepository.getPostById(postId);
        assertEquals(commentsCount+1, post.getComments().size());
    }

    @Test
    void editComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);

        assertEquals(0, post.getComments().size());

        postRepository.addComment(postId, "postComment");
        post = postRepository.getPostById(postId);
        assertEquals(1, post.getComments().size());
        assertEquals("postComment", post.getComments().getFirst().getText());

        postRepository.editComment(postId, post.getComments().getFirst().getId(), "changedPostComment");
        post = postRepository.getPostById(postId);
        assertEquals(1, post.getComments().size());
        assertEquals("changedPostComment", post.getComments().getFirst().getText());
    }

    @Test
    void deleteComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();

        postRepository.addComment(postId, "postComment");
        Post post = postRepository.getPostById(postId);
        assertEquals(1, post.getComments().size());

        postRepository.deleteComment(postId, post.getComments().getFirst().getId());
        post = postRepository.getPostById(postId);
        assertEquals(0, post.getComments().size());
    }

    @Test
    void deletePost() {
        assertEquals(2, postRepository.findPosts("").size());
        Long postId = postRepository.findPosts("").getFirst().getId();
        postRepository.deletePost(postId);
        assertEquals(1, postRepository.findPosts("").size());
    }
}
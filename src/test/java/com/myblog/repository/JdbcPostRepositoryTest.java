package com.myblog.repository;

import com.myblog.configuration.TestDataSourceConfiguration;
import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {TestDataSourceConfiguration.class, JdbcPostRepository.class})
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
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_1', 'text_1', 'tag1 tag2 tags3', 'image_path_1', 1)");
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_2', 'text_2', 'tag2 tag3 tags4', 'image_path_2', 2)");
    }

    @Test
    void findPosts() {
        assertEquals(0, postRepository.findPosts("tag1_no").size());
        assertEquals(2, postRepository.findPosts("").size());
        assertEquals(1, postRepository.findPosts("tag1").size());
        assertEquals(2, postRepository.findPosts("tag2").size());
    }

    @Test
    void getPostById() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Post post = postRepository.getPostById(postId);
        assertNotNull(post);
    }

    @Test
    void getCommentsByPostId() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        assertEquals(0, postRepository.getCommentsByPostId(postId).size());
        jdbcTemplate.execute("INSERT INTO comments (post_id, text) VALUES (" + postId + ", 'text_comment')");
        assertEquals(1, postRepository.getCommentsByPostId(postId).size());
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

        Post changedPost = postRepository.getPostById(postId);
        assertEquals("changedTitle", changedPost.getTitle());
        assertEquals("changedText", changedPost.getText());
        assertEquals("changedTags", changedPost.getTags());
        assertEquals("changedImagePath", changedPost.getImagePath());
    }

    @Test
    void addComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();

        assertEquals(0, postRepository.getCommentsByPostId(postId).size());

        Comment comment = new Comment(null, postId, "postComment");
        postRepository.addComment(comment);
        assertEquals(1, postRepository.getCommentsByPostId(postId).size());
    }

    @Test
    void editComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();

        Comment comment = new Comment(null, postId, "postComment");
        postRepository.addComment(comment);
        assertEquals("postComment", postRepository.getCommentsByPostId(postId).getFirst().text());

        Long commentId = postRepository.getCommentsByPostId(postId).getFirst().id();
        Comment changedPostComment = new Comment(commentId, null, "changedPostComment") ;
        postRepository.editComment(changedPostComment);
        assertEquals("changedPostComment", postRepository.getCommentsByPostId(postId).getFirst().text());
    }

    @Test
    void deleteComment() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Comment comment = new Comment(null, postId, "postComment");
        postRepository.addComment(comment);
        assertEquals(1, postRepository.getCommentsByPostId(postId).size());

        postRepository.deleteComment(postRepository.getCommentsByPostId(postId).getFirst().id());
        assertEquals(0, postRepository.getCommentsByPostId(postId).size());
    }

    @Test
    void deleteCommentsByPostId() {
        Long postId = postRepository.findPosts("").getFirst().getId();
        Comment comment_1 = new Comment(null, postId, "postComment_1");
        postRepository.addComment(comment_1);
        Comment comment_2 = new Comment(null, postId, "postComment_2");
        postRepository.addComment(comment_2);
        assertEquals(2, postRepository.getCommentsByPostId(postId).size());

        postRepository.deleteCommentsByPostId(postId);
        assertEquals(0, postRepository.getCommentsByPostId(postId).size());
    }

    @Test
    void deletePost() {
        assertEquals(2, postRepository.findPosts("").size());
        Long postId = postRepository.findPosts("").getFirst().getId();
        postRepository.deletePost(postId);
        assertEquals(1, postRepository.findPosts("").size());
    }
}
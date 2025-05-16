package com.myblog.repository;

import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class JdbcPostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        postRepository.save(new Post(null, "title_1", "text_1", "tag1 tag2 tags3", "image_path_1", 1));
        postRepository.save(new Post(null, "title_2", "text_2", "tag2 tag3 tags4", "image_path_2", 2));
    }

    @Test
    void findPosts() {
        assertEquals(0, postRepository.findByTagsLike("%tag1_no%").size());
        assertEquals(2, postRepository.findByTagsLike("%").size());
        assertEquals(1, postRepository.findByTagsLike("%tag1%").size());
        assertEquals(2, postRepository.findByTagsLike("%tag2%").size());
    }

    @Test
    void getPostById() {
        Long postId = postRepository.findAll().getFirst().getId();
        Post post = postRepository.findById(postId).orElse(null);
        assertNotNull(post);
    }

    @Test
    void getCommentsByPostId() {
        Long postId = postRepository.findAll().getFirst().getId();
        assertEquals(0, commentRepository.findByPostId(postId).size());
        commentRepository.save(new Comment(null, postId, "text_comment"));
        assertEquals(1, commentRepository.findByPostId(postId).size());
    }

    @Test
    void addPost() {
        assertEquals(2, postRepository.findAll().size());
        Post post = new Post(null, "title_3", "text_3", "tags_3", "image_path_3", 3);
        postRepository.save(post);
        assertEquals(3, postRepository.findAll().size());
    }

    @Test
    void likePost() {
        Long postId = postRepository.findAll().getFirst().getId();
        Post post = postRepository.findById(postId).orElse(null);
        Integer likesCount = post.getLikesCount();

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
        post = postRepository.findById(postId).orElse(null);
        assertEquals(likesCount + 1, post.getLikesCount());


        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);
        post = postRepository.findById(postId).orElse(null);
        assertEquals(likesCount, post.getLikesCount());
    }

    @Test
    void editPost() {
        Long postId = postRepository.findAll().getFirst().getId();
        Post post = postRepository.findById(postId).orElse(null);
        post.setTitle("changedTitle");
        post.setText("changedText");
        post.setTags("changedTags");
        post.setImagePath("changedImagePath");
        postRepository.save(post);

        Post changedPost = postRepository.findById(postId).orElse(null);
        assertEquals("changedTitle", changedPost.getTitle());
        assertEquals("changedText", changedPost.getText());
        assertEquals("changedTags", changedPost.getTags());
        assertEquals("changedImagePath", changedPost.getImagePath());
    }

    @Test
    void addComment() {
        Long postId = postRepository.findAll().getFirst().getId();

        assertEquals(0, commentRepository.findByPostId(postId).size());

        Comment comment = new Comment(null, postId, "postComment");
        commentRepository.save(comment);
        assertEquals(1, commentRepository.findByPostId(postId).size());
    }

    @Test
    void editComment() {
        Long postId = postRepository.findAll().getFirst().getId();

        Comment comment = new Comment(null, postId, "postComment");
        commentRepository.save(comment);
        assertEquals("postComment", commentRepository.findByPostId(postId).getFirst().getText());

        Long commentId = commentRepository.findByPostId(postId).getFirst().getId();
        Comment changedPostComment = new Comment(commentId, postId, "changedPostComment") ;
        commentRepository.save(changedPostComment);
        assertEquals("changedPostComment", commentRepository.findByPostId(postId).getFirst().getText());
    }

    @Test
    void deleteComment() {
        Long postId = postRepository.findAll().getFirst().getId();
        Comment comment = new Comment(null, postId, "postComment");
        commentRepository.save(comment);
        assertEquals(1, commentRepository.findByPostId(postId).size());

        commentRepository.deleteById(commentRepository.findByPostId(postId).getFirst().getId());
        assertEquals(0, commentRepository.findByPostId(postId).size());
    }

    @Test
    void deleteCommentsByPostId() {
        Long postId = postRepository.findAll().getFirst().getId();
        Comment comment_1 = new Comment(null, postId, "postComment_1");
        commentRepository.save(comment_1);
        Comment comment_2 = new Comment(null, postId, "postComment_2");
        commentRepository.save(comment_2);
        assertEquals(2, commentRepository.findByPostId(postId).size());

        commentRepository.deleteAll(commentRepository.findByPostId(postId));
        assertEquals(0, commentRepository.findByPostId(postId).size());
    }

    @Test
    void deletePost() {
        assertEquals(2, postRepository.findAll().size());
        Long postId = postRepository.findAll().getFirst().getId();
        postRepository.deleteById(postId);
        assertEquals(1, postRepository.findAll().size());
    }
}
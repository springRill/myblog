package com.myblog.repository;

import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findPosts(String search) {
        return jdbcTemplate.query(
                "select id, title, text, tags, image_path, likes_count from posts where tags LIKE ? order by id", new Object[]{"%" + search + "%"},
                (rs, rowNum) ->  new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getString("tags"),
                        rs.getString("image_path"),
                        rs.getInt("likes_count")
                ));
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = jdbcTemplate.query(
                "select id, title, text, tags, image_path, likes_count from posts where id = " + postId,
                (rs, rowNum) ->  new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getString("tags"),
                        rs.getString("image_path"),
                        rs.getInt("likes_count")
                )).getFirst();
        return post;
    }

    public List<Comment> getCommentsByPostId(Long postId){
        List<Comment> commentList = jdbcTemplate.query(
                "select id, post_id, text from comments where post_id = " + postId,
                (rs, rowNum) ->  new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text")
                ));

        return commentList;
    }

    @Override
    public Long addPost(Post post) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sql = "insert into posts(title, text, tags, image_path, likes_count) values(?, ?, ?, ?, ?);";
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getText());
            preparedStatement.setString(3, post.getTags());
            preparedStatement.setString(4, post.getImagePath());
            preparedStatement.setInt(5, post.getLikesCount());
            return preparedStatement;
        }, generatedKeyHolder);
        return (Long)generatedKeyHolder.getKeys().get("id");
    }

    @Override
    public void likePost(Long id, boolean like) {
        String sql;
        if(like){
            sql = "update posts set likes_count = likes_count + 1 where id = ?";
        }else {
            sql = "update posts set likes_count = likes_count - 1 where id = ?";
        }
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void editPost(Post post) {
        jdbcTemplate.update("update posts set title = ?, text = ?, tags = ?, image_path = ? where id = ?",
                post.getTitle(), post.getText(), post.getTags(), post.getImagePath(), post.getId());
    }

    @Override
    public void addComment(Comment comment) {
        jdbcTemplate.update("insert into comments(post_id, text) values(?, ?)",
                comment.postId(), comment.text());
    }

    @Override
    public void editComment(Comment comment) {
        jdbcTemplate.update("update comments set text = ? where id = ?",
                comment.text(), comment.id());
    }

    @Override
    public void deleteComment(Long commentId) {
        jdbcTemplate.update("delete from comments where id = ?", commentId);
    }

    @Override
    public void deleteCommentsByPostId(Long postId) {
        jdbcTemplate.update("delete from comments where post_id = ?", postId);
    }

    @Override
    public void deletePost(Long postId) {
        jdbcTemplate.update("delete from posts where id = ?", postId);
    }
}

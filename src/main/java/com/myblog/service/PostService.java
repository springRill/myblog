package com.myblog.service;

import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import com.myblog.dto.CommentDto;
import com.myblog.dto.PostDto;
import com.myblog.mapper.CommentMapper;
import com.myblog.mapper.PostMapper;
import com.myblog.repository.CommentRepository;
import com.myblog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, PostMapper postMapper, CommentMapper commentMapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    public List<PostDto> findPosts(String search) {
        List<Post> postList = postRepository.findByTagsLike("%" + search + "%");
        List<PostDto> postDtoList = postList.stream()
                .map(postMapper::toPostDto)
                .peek(postDto -> {
                    List<Comment> commentList = commentRepository.findByPostId(postDto.getId());
                    postDto.setComments(commentList.stream().map(commentMapper::toCommentDto).toList());
                })
                .collect(Collectors.toList());
        return postDtoList;
    }

    public PostDto getPostById(Long postId) {
        PostDto postDto = postMapper.toPostDto(postRepository.findById(postId).orElseThrow());
        List<Comment> commentList = commentRepository.findByPostId(postId);
        postDto.setComments(commentList.stream().map(commentMapper::toCommentDto).toList());
        return postDto;
    }

    public Long addPost(PostDto postDto) {
        Post post = postMapper.toPost(postDto);
        post.setTags(post.getTags().trim().replaceAll("\\s{2,}", " "));
        return postRepository.save(post).getId();
    }

    public void likePost(Long postId, boolean like) {
        Post post = postRepository.findById(postId).orElseThrow();
        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else {
            post.setLikesCount(post.getLikesCount() - 1);
        }
        postRepository.save(post);
    }

    public void editPost(PostDto postDto) {
        Post post = postMapper.toPost(postDto);
        post.setTags(post.getTags().trim().replaceAll("\\s{2,}", " "));
        post.setLikesCount(postRepository.findById(post.getId()).orElseThrow().getLikesCount());
        postRepository.save(post);
    }

    public void addComment(Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(postId, commentDto);
        commentRepository.save(comment);
    }

    public void editComment(Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(postId, commentDto);
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void deletePost(Long postId) {
        commentRepository.deleteAll(commentRepository.findByPostId(postId));
        postRepository.deleteById(postId);
    }

}

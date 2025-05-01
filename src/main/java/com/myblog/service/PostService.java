package com.myblog.service;

import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import com.myblog.dto.CommentDto;
import com.myblog.dto.PostDto;
import com.myblog.mapper.CommentMapper;
import com.myblog.mapper.PostMapper;
import com.myblog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper, CommentMapper commentMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    public List<PostDto> findPosts(String search) {
        List<Post> postList = postRepository.findPosts(search);
        List<PostDto> postDtoList = postList.stream()
                .map(postMapper::toPostDto)
                .peek(postDto -> {
                    List<Comment> commentList = postRepository.getCommentsByPostId(postDto.getId());
                    postDto.setComments(commentList.stream().map(commentMapper::toCommentDto).toList());
                })
                .collect(Collectors.toList());
        return postDtoList;
    }

    public PostDto getPostById(Long postId) {
        PostDto postDto = postMapper.toPostDto(postRepository.getPostById(postId));
        List<Comment> commentList = postRepository.getCommentsByPostId(postId);
        postDto.setComments(commentList.stream().map(commentMapper::toCommentDto).toList());
        return postDto;
    }

    public Long addPost(PostDto postDto) {
        Post post = postMapper.toPost(postDto);
        post.setTags(post.getTags().trim().replaceAll("\\s{2,}", " "));
        return this.postRepository.addPost(post);
    }

    public void likePost(Long id, boolean like) {
        this.postRepository.likePost(id, like);
    }

    public void editPost(PostDto postDto) {
        Post post = postMapper.toPost(postDto);
        post.setTags(post.getTags().trim().replaceAll("\\s{2,}", " "));
        this.postRepository.editPost(post);
    }

    public void addComment(Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(postId, commentDto);
        this.postRepository.addComment(comment);
    }

    public void editComment(Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(postId, commentDto);
        this.postRepository.editComment(comment);
    }

    public void deleteComment(Long commentId) {
        this.postRepository.deleteComment(commentId);
    }

    public void deletePost(Long postId) {
        this.postRepository.deleteCommentsByPostId(postId);
        this.postRepository.deletePost(postId);
    }

}

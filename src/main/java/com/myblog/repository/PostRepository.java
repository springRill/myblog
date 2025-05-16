package com.myblog.repository;

import com.myblog.domain.Post;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends ListCrudRepository<Post, Long> {

    List<Post> findByTagsLikeOrderByIdAsc(String pattern);
}

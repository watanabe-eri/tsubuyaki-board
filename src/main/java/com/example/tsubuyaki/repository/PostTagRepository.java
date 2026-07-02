package com.example.tsubuyaki.repository;

import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.domain.PostTagId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {

    @EntityGraph(attributePaths = "post")
    List<PostTag> findByTagNameOrderByPostCreatedAtDesc(String name);
}

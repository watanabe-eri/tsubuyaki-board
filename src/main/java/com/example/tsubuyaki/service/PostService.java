package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> latest() {
        List<Post> posts = repository.findTop50ByOrderByCreatedAtDesc();
        if (posts == null) {
            return Collections.emptyList();
        }
        return posts;
    }

    public List<Post> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return latest();
        }
        return repository.findTop50ByBodyContainingOrderByCreatedAtDesc(keyword);
    }

    @Transactional
    public void save(String author, String body) {
        save(author, body, "blue");
    }

    @Transactional
    public void save(String author, String body, String avatarColor) {
        String color = (avatarColor == null || avatarColor.isBlank()) ? "blue" : avatarColor;
        repository.save(new Post(author, body, Instant.now(), color));
    }

    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }
}

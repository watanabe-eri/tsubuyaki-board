package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.domain.PostTagId;
import com.example.tsubuyaki.domain.Tag;
import com.example.tsubuyaki.repository.PostTagRepository;
import com.example.tsubuyaki.repository.PostRepository;
import com.example.tsubuyaki.repository.TagRepository;
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

    private final TagRepository tagRepository;

    private final PostTagRepository postTagRepository;

    private final TagExtractor tagExtractor;

    public PostService(PostRepository repository, TagRepository tagRepository,
                       PostTagRepository postTagRepository, TagExtractor tagExtractor) {
        this.repository = repository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.tagExtractor = tagExtractor;
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
        Post savedPost = repository.save(new Post(author, body, Instant.now(), color));
        List<String> tagNames = tagExtractor.extract(body);
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            PostTagId postTagId = new PostTagId(savedPost.getId(), tag.getId());
            if (!postTagRepository.existsById(postTagId)) {
                postTagRepository.save(new PostTag(savedPost, tag));
            }
        }
    }

    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }
}

package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.repository.PostTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TagService {

    private final PostTagRepository postTagRepository;

    private final TagExtractor tagExtractor;

    public TagService(PostTagRepository postTagRepository, TagExtractor tagExtractor) {
        this.postTagRepository = postTagRepository;
        this.tagExtractor = tagExtractor;
    }

    public List<Post> findPostsByTagName(String name) {
        return postTagRepository.findByTagNameOrderByPostCreatedAtDesc(name).stream()
                .map(PostTag::getPost)
                .toList();
    }

    public Map<Long, List<String>> extractTagsByPostId(List<Post> posts) {
        Map<Long, List<String>> tagsByPostId = new LinkedHashMap<>();
        for (Post post : posts) {
            tagsByPostId.put(post.getId(), tagExtractor.extract(post.getBody()));
        }
        return tagsByPostId;
    }
}

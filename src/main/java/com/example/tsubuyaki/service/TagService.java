package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.repository.PostTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagService {

    private final PostTagRepository postTagRepository;

    public TagService(PostTagRepository postTagRepository) {
        this.postTagRepository = postTagRepository;
    }

    public List<Post> findPostsByTagName(String name) {
        return postTagRepository.findByTag_NameOrderByPost_CreatedAtDesc(name).stream()
                .map(PostTag::getPost)
                .toList();
    }
}

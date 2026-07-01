package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Like;
import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.repository.LikeRepository;
import com.example.tsubuyaki.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void toggle(Long postId, String clientHash) {
        Optional<Like> existingLike = likeRepository.findByPostIdAndClientHash(postId, clientHash);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return;
        }
        Post post = postRepository.getReferenceById(postId);
        likeRepository.save(new Like(post, clientHash, Instant.now()));
    }

    public long countByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLiked(Long postId, String clientHash) {
        return likeRepository.existsByPostIdAndClientHash(postId, clientHash);
    }
}

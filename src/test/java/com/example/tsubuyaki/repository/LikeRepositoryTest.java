package com.example.tsubuyaki.repository;

import com.example.tsubuyaki.domain.Like;
import com.example.tsubuyaki.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("h2")
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("いいね検索_postIdとclientHashが一致する場合_いいねを取得できる")
    void いいね検索_postIdとclientHashが一致する場合_いいねを取得できる() {
        Post post = postRepository.save(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        Like like = likeRepository.save(new Like(post, "client-1", Instant.parse("2026-05-23T10:05:00Z")));

        Optional<Like> foundLike = likeRepository.findByPostIdAndClientHash(post.getId(), "client-1");

        assertThat(foundLike).contains(like);
    }

    @Test
    @DisplayName("いいね済み判定_postIdとclientHashが一致する場合_trueを返す")
    void いいね済み判定_postIdとclientHashが一致する場合_trueを返す() {
        Post post = postRepository.save(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        likeRepository.save(new Like(post, "client-1", Instant.parse("2026-05-23T10:05:00Z")));

        boolean liked = likeRepository.existsByPostIdAndClientHash(post.getId(), "client-1");

        assertThat(liked).isTrue();
    }

    @Test
    @DisplayName("いいね数カウント_postIdが一致するいいね数を返す")
    void いいね数カウント_postIdが一致するいいね数を返す() {
        Post targetPost = postRepository.save(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        Post otherPost = postRepository.save(new Post("bob", "other", Instant.parse("2026-05-23T10:01:00Z")));
        likeRepository.save(new Like(targetPost, "client-1", Instant.parse("2026-05-23T10:05:00Z")));
        likeRepository.save(new Like(targetPost, "client-2", Instant.parse("2026-05-23T10:06:00Z")));
        likeRepository.save(new Like(otherPost, "client-1", Instant.parse("2026-05-23T10:07:00Z")));

        long likeCount = likeRepository.countByPostId(targetPost.getId());

        assertThat(likeCount).isEqualTo(2);
    }
}

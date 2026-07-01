package com.example.tsubuyaki.repository;

import com.example.tsubuyaki.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("h2")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("投稿一覧_51件以上の投稿がある場合_新着50件しか表示されないこと")
    void 投稿一覧_51件以上の投稿がある場合_新着50件しか表示されないこと() {
        Instant base = Instant.parse("2026-05-23T00:00:00Z");
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            posts.add(new Post("user-" + i, "body-" + i, base.plusSeconds(i)));
        }
        postRepository.saveAll(posts);

        List<Post> latestPosts = postRepository.findTop50ByOrderByCreatedAtDesc();

        assertThat(latestPosts).hasSize(50);
        assertThat(latestPosts).extracting(Post::getAuthor)
                .startsWith("user-59", "user-58", "user-57")
                .endsWith("user-12", "user-11", "user-10");
    }

    @Test
    @DisplayName("投稿検索_本文にキーワードを含む場合_新着順で返す")
    void 投稿検索_本文にキーワードを含む場合_新着順で返す() {
        postRepository.save(new Post("alice", "Spring Boot のメモ", Instant.parse("2026-05-23T10:00:00Z")));
        postRepository.save(new Post("bob", "Oracle のメモ", Instant.parse("2026-05-23T10:05:00Z")));
        postRepository.save(new Post("carol", "Spring Data JPA のメモ", Instant.parse("2026-05-23T10:10:00Z")));

        List<Post> posts = postRepository.findTop50ByBodyContainingOrderByCreatedAtDesc("Spring");

        assertThat(posts).extracting(Post::getAuthor)
                .containsExactly("carol", "alice");
    }
}

package com.example.tsubuyaki.repository;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.domain.PostTagId;
import com.example.tsubuyaki.domain.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("h2")
class PostTagRepositoryTest {

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("投稿タグ登録_save実行時_複合主キーで取得できる")
    void 投稿タグ登録_save実行時_複合主キーで取得できる() {
        Post post = postRepository.save(new Post("alice", "Spring のメモ #spring",
                Instant.parse("2026-05-23T10:00:00Z")));
        Tag tag = tagRepository.save(new Tag("spring"));
        PostTag postTag = postTagRepository.save(new PostTag(post, tag));

        PostTagId id = new PostTagId(post.getId(), tag.getId());

        assertThat(postTagRepository.findById(id)).contains(postTag);
    }

    @Test
    @DisplayName("タグ別投稿一覧_タグ名が一致する場合_投稿作成日時の新着順で返す")
    void タグ別投稿一覧_タグ名が一致する場合_投稿作成日時の新着順で返す() {
        Tag spring = tagRepository.save(new Tag("spring"));
        Tag oracle = tagRepository.save(new Tag("oracle"));
        Post oldPost = postRepository.save(new Post("alice", "Spring のメモ #spring",
                Instant.parse("2026-05-23T10:00:00Z")));
        Post otherPost = postRepository.save(new Post("bob", "Oracle のメモ #oracle",
                Instant.parse("2026-05-23T10:05:00Z")));
        Post newPost = postRepository.save(new Post("carol", "Spring Data JPA のメモ #spring",
                Instant.parse("2026-05-23T10:10:00Z")));
        postTagRepository.save(new PostTag(oldPost, spring));
        postTagRepository.save(new PostTag(otherPost, oracle));
        postTagRepository.save(new PostTag(newPost, spring));

        List<PostTag> postTags = postTagRepository.findByTagNameOrderByPostCreatedAtDesc("spring");

        assertThat(postTags).extracting(postTag -> postTag.getPost().getAuthor())
                .containsExactly("carol", "alice");
    }
}

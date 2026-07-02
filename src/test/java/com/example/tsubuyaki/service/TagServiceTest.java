package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.domain.Tag;
import com.example.tsubuyaki.repository.PostTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private TagExtractor tagExtractor;

    @InjectMocks
    private TagService tagService;

    @Test
    @DisplayName("タグ別投稿一覧_タグ名が一致する場合_PostTagからPostを取り出して返す")
    void タグ別投稿一覧_タグ名が一致する場合_PostTagからPostを取り出して返す() {
        Tag tag = new Tag("spring");
        Post latestPost = new Post("alice", "Spring Data JPA #spring", Instant.parse("2026-05-23T10:10:00Z"));
        Post oldPost = new Post("bob", "Spring Boot #spring", Instant.parse("2026-05-23T10:00:00Z"));
        given(postTagRepository.findByTagNameOrderByPostCreatedAtDesc("spring"))
                .willReturn(List.of(new PostTag(latestPost, tag), new PostTag(oldPost, tag)));

        List<Post> posts = tagService.findPostsByTagName("spring");

        assertThat(posts).containsExactly(latestPost, oldPost);
        verify(postTagRepository).findByTagNameOrderByPostCreatedAtDesc("spring");
    }

    @Test
    @DisplayName("タグ別投稿一覧_タグ名に一致する投稿がない場合_空配列を返す")
    void タグ別投稿一覧_タグ名に一致する投稿がない場合_空配列を返す() {
        given(postTagRepository.findByTagNameOrderByPostCreatedAtDesc("unknown")).willReturn(List.of());

        List<Post> posts = tagService.findPostsByTagName("unknown");

        assertThat(posts).isEmpty();
        verify(postTagRepository).findByTagNameOrderByPostCreatedAtDesc("unknown");
    }

    @Test
    @DisplayName("タグ表示用一覧_投稿一覧を受け取った場合_PostIdごとのタグ名一覧を返す")
    void タグ表示用一覧_投稿一覧を受け取った場合_PostIdごとのタグ名一覧を返す() {
        Post post = new Post("alice", "Spring Boot #java #spring", Instant.parse("2026-05-23T10:15:00Z"));
        org.springframework.test.util.ReflectionTestUtils.setField(post, "id", 1L);
        given(tagExtractor.extract("Spring Boot #java #spring")).willReturn(List.of("java", "spring"));

        Map<Long, List<String>> tagsByPostId = tagService.extractTagsByPostId(List.of(post));

        assertThat(tagsByPostId).containsEntry(1L, List.of("java", "spring"));
        verify(tagExtractor).extract("Spring Boot #java #spring");
    }
}

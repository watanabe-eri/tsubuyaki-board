package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("投稿一覧_latest実行時_Repositoryの新着50件を返す")
    void 投稿一覧_latest実行時_Repositoryの新着50件を返す() {
        List<Post> posts = List.of(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(posts);

        List<Post> latestPosts = postService.latest();

        assertThat(latestPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿作成_save実行時_投稿者本文作成日時を持つPostを保存する")
    void 投稿作成_save実行時_投稿者本文作成日時を持つPostを保存する() {
        Instant before = Instant.now();

        postService.save("alice", "共有事項があります");

        Instant after = Instant.now();
        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAuthor()).isEqualTo("alice");
        assertThat(savedPost.getBody()).isEqualTo("共有事項があります");
        assertThat(savedPost.getAvatarColor()).isEqualTo("blue");
        assertThat(savedPost.getCreatedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("投稿作成_save実行時_指定したアバター色を持つPostを保存する")
    void 投稿作成_save実行時_指定したアバター色を持つPostを保存する() {
        postService.save("alice", "共有事項があります", "purple");

        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAvatarColor()).isEqualTo("purple");
    }

    @Test
    @DisplayName("投稿詳細_findById実行時_Repositoryの検索結果を返す")
    void 投稿詳細_findById実行時_Repositoryの検索結果を返す() {
        Post post = new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z"));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        Optional<Post> foundPost = postService.findById(1L);

        assertThat(foundPost).contains(post);
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("投稿一覧_search実行時_キーワード空なら新着一覧を返す")
    void 投稿一覧_search実行時_キーワード空なら新着一覧を返す() {
        List<Post> posts = List.of(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(posts);

        List<Post> foundPosts = postService.search("   ");

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿一覧_search実行時_キーワードありなら本文検索結果を返す")
    void 投稿一覧_search実行時_キーワードありなら本文検索結果を返す() {
        List<Post> posts = List.of(new Post("alice", "Spring", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByBodyContainingOrderByCreatedAtDesc("Spring")).willReturn(posts);

        List<Post> foundPosts = postService.search("Spring");

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByBodyContainingOrderByCreatedAtDesc("Spring");
    }
}

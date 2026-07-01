package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Like;
import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.repository.LikeRepository;
import com.example.tsubuyaki.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("いいね切替_未いいねの場合_いいねを保存する")
    void いいね切替_未いいねの場合_いいねを保存する() {
        Post post = new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z"));
        given(likeRepository.findByPostIdAndClientHash(1L, "client-1")).willReturn(Optional.empty());
        given(postRepository.getReferenceById(1L)).willReturn(post);

        likeService.toggle(1L, "client-1");

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like savedLike = captor.getValue();
        assertThat(savedLike.getPost()).isEqualTo(post);
        assertThat(savedLike.getClientHash()).isEqualTo("client-1");
        assertThat(savedLike.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("いいね切替_いいね済みの場合_いいねを削除する")
    void いいね切替_いいね済みの場合_いいねを削除する() {
        Post post = new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z"));
        Like like = new Like(post, "client-1", Instant.parse("2026-05-23T10:05:00Z"));
        given(likeRepository.findByPostIdAndClientHash(1L, "client-1")).willReturn(Optional.of(like));

        likeService.toggle(1L, "client-1");

        verify(likeRepository).delete(like);
        verify(likeRepository, never()).save(org.mockito.ArgumentMatchers.any(Like.class));
    }

    @Test
    @DisplayName("いいね数_countByPostId実行時_Repositoryの件数を返す")
    void いいね数_countByPostId実行時_Repositoryの件数を返す() {
        given(likeRepository.countByPostId(1L)).willReturn(3L);

        long likeCount = likeService.countByPostId(1L);

        assertThat(likeCount).isEqualTo(3);
        verify(likeRepository).countByPostId(1L);
    }

    @Test
    @DisplayName("いいね済み判定_isLiked実行時_Repositoryの判定結果を返す")
    void いいね済み判定_isLiked実行時_Repositoryの判定結果を返す() {
        given(likeRepository.existsByPostIdAndClientHash(1L, "client-1")).willReturn(true);

        boolean liked = likeService.isLiked(1L, "client-1");

        assertThat(liked).isTrue();
        verify(likeRepository).existsByPostIdAndClientHash(1L, "client-1");
    }
}

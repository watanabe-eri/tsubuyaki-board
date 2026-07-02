package com.example.tsubuyaki.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    private static final Instant POST_CREATED_AT = Instant.parse("2026-05-23T10:00:00Z");
    private static final Instant LIKE_CREATED_AT = Instant.parse("2026-05-23T10:05:00Z");

    @Test
    @DisplayName("いいね_JPA用コンストラクタ_生成できる")
    void いいね_JPA用コンストラクタ_生成できる() {
        Like like = new Like();

        assertThat(like.getId()).isNull();
    }

    @Test
    @DisplayName("いいね_コンストラクタ_投稿クライアントハッシュ作成日時を保持する")
    void いいね_コンストラクタ_投稿クライアントハッシュ作成日時を保持する() {
        Post post = new Post("alice", "hello", POST_CREATED_AT);

        Like like = new Like(post, "client-1", LIKE_CREATED_AT);

        assertThat(like.getPost()).isEqualTo(post);
        assertThat(like.getClientHash()).isEqualTo("client-1");
        assertThat(like.getCreatedAt()).isEqualTo(LIKE_CREATED_AT);
    }

    @Test
    @DisplayName("いいね_getId_未永続化ならnullを返す")
    void いいね_getId_未永続化ならnullを返す() {
        Like like = new Like(new Post("alice", "hello", POST_CREATED_AT), "client-1", LIKE_CREATED_AT);

        assertThat(like.getId()).isNull();
    }

    @Test
    @DisplayName("いいね_equals_同じidのいいねならtrueを返す")
    void いいね_equals_同じidのいいねならtrueを返す() {
        Like like = new Like(new Post("alice", "hello", POST_CREATED_AT), "client-1", LIKE_CREATED_AT);
        Like sameIdLike = new Like(new Post("bob", "different", POST_CREATED_AT), "client-2", LIKE_CREATED_AT);
        ReflectionTestUtils.setField(like, "id", 1L);
        ReflectionTestUtils.setField(sameIdLike, "id", 1L);

        assertThat(like).isEqualTo(sameIdLike);
    }

    @Test
    @DisplayName("いいね_equals_異なるidのいいねならfalseを返す")
    void いいね_equals_異なるidのいいねならfalseを返す() {
        Like like = new Like(new Post("alice", "hello", POST_CREATED_AT), "client-1", LIKE_CREATED_AT);
        Like otherLike = new Like(new Post("bob", "different", POST_CREATED_AT), "client-2", LIKE_CREATED_AT);
        ReflectionTestUtils.setField(like, "id", 1L);
        ReflectionTestUtils.setField(otherLike, "id", 2L);

        assertThat(like).isNotEqualTo(otherLike);
    }

    @Test
    @DisplayName("いいね_equals_別型ならfalseを返す")
    void いいね_equals_別型ならfalseを返す() {
        Like like = new Like(new Post("alice", "hello", POST_CREATED_AT), "client-1", LIKE_CREATED_AT);
        ReflectionTestUtils.setField(like, "id", 1L);

        assertThat(like).isNotEqualTo("1");
    }

    @Test
    @DisplayName("いいね_hashCode_idに基づく値を返す")
    void いいね_hashCode_idに基づく値を返す() {
        Like like = new Like(new Post("alice", "hello", POST_CREATED_AT), "client-1", LIKE_CREATED_AT);
        Like sameIdLike = new Like(new Post("bob", "different", POST_CREATED_AT), "client-2", LIKE_CREATED_AT);
        ReflectionTestUtils.setField(like, "id", 1L);
        ReflectionTestUtils.setField(sameIdLike, "id", 1L);

        assertThat(like).hasSameHashCodeAs(sameIdLike);
    }
}

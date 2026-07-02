package com.example.tsubuyaki.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    private static final Instant CREATED_AT = Instant.parse("2026-05-23T10:00:00Z");

    @Test
    @DisplayName("投稿_コンストラクタ_投稿者本文作成日時を保持する")
    void 投稿_コンストラクタ_投稿者本文作成日時を保持する() {
        Post post = new Post("alice", "hello", CREATED_AT);

        assertThat(post.getId()).isNull();
        assertThat(post.getAuthor()).isEqualTo("alice");
        assertThat(post.getBody()).isEqualTo("hello");
        assertThat(post.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    @DisplayName("投稿_JPA用コンストラクタ_生成できる")
    void 投稿_JPA用コンストラクタ_生成できる() {
        Post post = new Post();

        assertThat(post.getId()).isNull();
    }

    @Test
    @DisplayName("投稿_3引数コンストラクタ_アバター色の既定値blueを返す")
    void 投稿_3引数コンストラクタ_アバター色の既定値blueを返す() {
        Post post = new Post("alice", "hello", CREATED_AT);

        assertThat(post.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿_アバター色がnullのとき_blueを返す")
    void 投稿_アバター色がnullのとき_blueを返す() {
        Post post = new Post("alice", "hello", CREATED_AT, null);

        assertThat(post.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿_アバター色が空白のとき_blueを返す")
    void 投稿_アバター色が空白のとき_blueを返す() {
        Post post = new Post("alice", "hello", CREATED_AT, "   ");

        assertThat(post.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿_equals_同じidの投稿ならtrueを返す")
    void 投稿_equals_同じidの投稿ならtrueを返す() {
        Post post = new Post("alice", "hello", CREATED_AT);
        Post sameIdPost = new Post("bob", "different", CREATED_AT.plusSeconds(60));
        ReflectionTestUtils.setField(post, "id", 1L);
        ReflectionTestUtils.setField(sameIdPost, "id", 1L);

        assertThat(post).isEqualTo(sameIdPost);
    }

    @Test
    @DisplayName("投稿_equals_異なるidの投稿ならfalseを返す")
    void 投稿_equals_異なるidの投稿ならfalseを返す() {
        Post post = new Post("alice", "hello", CREATED_AT);
        Post otherPost = new Post("bob", "different", CREATED_AT.plusSeconds(60));
        ReflectionTestUtils.setField(post, "id", 1L);
        ReflectionTestUtils.setField(otherPost, "id", 2L);

        assertThat(post).isNotEqualTo(otherPost);
    }

    @Test
    @DisplayName("投稿_equals_別型ならfalseを返す")
    void 投稿_equals_別型ならfalseを返す() {
        Post post = new Post("alice", "hello", CREATED_AT);
        ReflectionTestUtils.setField(post, "id", 1L);

        assertThat(post).isNotEqualTo("1");
    }

    @Test
    @DisplayName("投稿_hashCode_idに基づく値を返す")
    void 投稿_hashCode_idに基づく値を返す() {
        Post post = new Post("alice", "hello", CREATED_AT);
        Post sameIdPost = new Post("bob", "different", CREATED_AT.plusSeconds(60));
        ReflectionTestUtils.setField(post, "id", 1L);
        ReflectionTestUtils.setField(sameIdPost, "id", 1L);

        assertThat(post).hasSameHashCodeAs(sameIdPost);
    }
}

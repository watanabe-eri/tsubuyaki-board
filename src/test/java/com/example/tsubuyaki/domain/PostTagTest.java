package com.example.tsubuyaki.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostTagTest {

    private static final Instant CREATED_AT = Instant.parse("2026-05-23T10:00:00Z");

    @Test
    @DisplayName("投稿タグ_コンストラクタ_PostとTagと複合IDを保持する")
    void 投稿タグ_コンストラクタ_PostとTagと複合IDを保持する() {
        Post post = postWithId(1L);
        Tag tag = tagWithId(2L);

        PostTag postTag = new PostTag(post, tag);

        assertThat(postTag.getId()).isEqualTo(new PostTagId(1L, 2L));
        assertThat(postTag.getPost()).isEqualTo(post);
        assertThat(postTag.getTag()).isEqualTo(tag);
    }

    @Test
    @DisplayName("投稿タグ_JPA用コンストラクタ_生成できる")
    void 投稿タグ_JPA用コンストラクタ_生成できる() {
        PostTag postTag = new PostTag();

        assertThat(postTag.getId()).isNull();
    }

    @Test
    @DisplayName("投稿タグ_equals_同一インスタンスならtrueを返す")
    void 投稿タグ_equals_同一インスタンスならtrueを返す() {
        PostTag postTag = new PostTag(postWithId(1L), tagWithId(2L));

        assertThat(postTag).isEqualTo(postTag);
    }

    @Test
    @DisplayName("投稿タグ_equals_同じidならtrueを返す")
    void 投稿タグ_equals_同じidならtrueを返す() {
        PostTag postTag = new PostTag(postWithId(1L), tagWithId(2L));
        PostTag sameIdPostTag = new PostTag(postWithId(1L), tagWithId(2L));

        assertThat(postTag).isEqualTo(sameIdPostTag);
    }

    @Test
    @DisplayName("投稿タグ_equals_異なるidならfalseを返す")
    void 投稿タグ_equals_異なるidならfalseを返す() {
        PostTag postTag = new PostTag(postWithId(1L), tagWithId(2L));
        PostTag otherPostTag = new PostTag(postWithId(1L), tagWithId(3L));

        assertThat(postTag).isNotEqualTo(otherPostTag);
    }

    @Test
    @DisplayName("投稿タグ_equals_別型ならfalseを返す")
    void 投稿タグ_equals_別型ならfalseを返す() {
        PostTag postTag = new PostTag(postWithId(1L), tagWithId(2L));

        assertThat(postTag).isNotEqualTo("1");
    }

    @Test
    @DisplayName("投稿タグ_hashCode_idに基づく値を返す")
    void 投稿タグ_hashCode_idに基づく値を返す() {
        PostTag postTag = new PostTag(postWithId(1L), tagWithId(2L));
        PostTag sameIdPostTag = new PostTag(postWithId(1L), tagWithId(2L));

        assertThat(postTag).hasSameHashCodeAs(sameIdPostTag);
    }

    private Post postWithId(Long id) {
        Post post = new Post("alice", "hello", CREATED_AT);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private Tag tagWithId(Long id) {
        Tag tag = new Tag("java");
        ReflectionTestUtils.setField(tag, "id", id);
        return tag;
    }
}

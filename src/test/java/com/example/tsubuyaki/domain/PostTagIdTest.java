package com.example.tsubuyaki.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTagIdTest {

    @Test
    @DisplayName("投稿タグID_コンストラクタ_postIdとtagIdを保持する")
    void 投稿タグID_コンストラクタ_postIdとtagIdを保持する() {
        PostTagId postTagId = new PostTagId(1L, 2L);

        assertThat(postTagId.getPostId()).isEqualTo(1L);
        assertThat(postTagId.getTagId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("投稿タグID_JPA用コンストラクタ_生成できる")
    void 投稿タグID_JPA用コンストラクタ_生成できる() {
        PostTagId postTagId = new PostTagId();

        assertThat(postTagId.getPostId()).isNull();
        assertThat(postTagId.getTagId()).isNull();
    }

    @Test
    @DisplayName("投稿タグID_equals_同一インスタンスならtrueを返す")
    void 投稿タグID_equals_同一インスタンスならtrueを返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);

        assertThat(postTagId).isEqualTo(postTagId);
    }

    @Test
    @DisplayName("投稿タグID_equals_同じpostIdとtagIdならtrueを返す")
    void 投稿タグID_equals_同じpostIdとtagIdならtrueを返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);
        PostTagId samePostTagId = new PostTagId(1L, 2L);

        assertThat(postTagId).isEqualTo(samePostTagId);
    }

    @Test
    @DisplayName("投稿タグID_equals_postIdが異なるならfalseを返す")
    void 投稿タグID_equals_postIdが異なるならfalseを返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);
        PostTagId otherPostTagId = new PostTagId(3L, 2L);

        assertThat(postTagId).isNotEqualTo(otherPostTagId);
    }

    @Test
    @DisplayName("投稿タグID_equals_tagIdが異なるならfalseを返す")
    void 投稿タグID_equals_tagIdが異なるならfalseを返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);
        PostTagId otherPostTagId = new PostTagId(1L, 3L);

        assertThat(postTagId).isNotEqualTo(otherPostTagId);
    }

    @Test
    @DisplayName("投稿タグID_equals_別型ならfalseを返す")
    void 投稿タグID_equals_別型ならfalseを返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);

        assertThat(postTagId).isNotEqualTo("1");
    }

    @Test
    @DisplayName("投稿タグID_hashCode_postIdとtagIdに基づく値を返す")
    void 投稿タグID_hashCode_postIdとtagIdに基づく値を返す() {
        PostTagId postTagId = new PostTagId(1L, 2L);
        PostTagId samePostTagId = new PostTagId(1L, 2L);

        assertThat(postTagId).hasSameHashCodeAs(samePostTagId);
    }
}

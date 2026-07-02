package com.example.tsubuyaki.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {

    @Test
    @DisplayName("タグ_コンストラクタ_タグ名を保持する")
    void タグ_コンストラクタ_タグ名を保持する() {
        Tag tag = new Tag("java");

        assertThat(tag.getId()).isNull();
        assertThat(tag.getName()).isEqualTo("java");
    }

    @Test
    @DisplayName("タグ_JPA用コンストラクタ_生成できる")
    void タグ_JPA用コンストラクタ_生成できる() {
        Tag tag = new Tag();

        assertThat(tag.getId()).isNull();
    }

    @Test
    @DisplayName("タグ_equals_同一インスタンスならtrueを返す")
    void タグ_equals_同一インスタンスならtrueを返す() {
        Tag tag = new Tag("java");

        assertThat(tag).isEqualTo(tag);
    }

    @Test
    @DisplayName("タグ_equals_同じidならtrueを返す")
    void タグ_equals_同じidならtrueを返す() {
        Tag tag = new Tag("java");
        Tag sameIdTag = new Tag("spring");
        ReflectionTestUtils.setField(tag, "id", 1L);
        ReflectionTestUtils.setField(sameIdTag, "id", 1L);

        assertThat(tag).isEqualTo(sameIdTag);
    }

    @Test
    @DisplayName("タグ_equals_異なるidならfalseを返す")
    void タグ_equals_異なるidならfalseを返す() {
        Tag tag = new Tag("java");
        Tag otherTag = new Tag("spring");
        ReflectionTestUtils.setField(tag, "id", 1L);
        ReflectionTestUtils.setField(otherTag, "id", 2L);

        assertThat(tag).isNotEqualTo(otherTag);
    }

    @Test
    @DisplayName("タグ_equals_別型ならfalseを返す")
    void タグ_equals_別型ならfalseを返す() {
        Tag tag = new Tag("java");
        ReflectionTestUtils.setField(tag, "id", 1L);

        assertThat(tag).isNotEqualTo("1");
    }

    @Test
    @DisplayName("タグ_hashCode_idに基づく値を返す")
    void タグ_hashCode_idに基づく値を返す() {
        Tag tag = new Tag("java");
        Tag sameIdTag = new Tag("spring");
        ReflectionTestUtils.setField(tag, "id", 1L);
        ReflectionTestUtils.setField(sameIdTag, "id", 1L);

        assertThat(tag).hasSameHashCodeAs(sameIdTag);
    }
}

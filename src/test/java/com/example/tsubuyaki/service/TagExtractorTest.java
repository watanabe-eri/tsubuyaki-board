package com.example.tsubuyaki.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TagExtractorTest {

    private final TagExtractor tagExtractor = new TagExtractor();

    @Test
    @DisplayName("タグ抽出_本文に複数タグがある場合_シャープを除いたタグ名を初出順で返す")
    void タグ抽出_本文に複数タグがある場合_シャープを除いたタグ名を初出順で返す() {
        List<String> tags = tagExtractor.extract("今日は #spring と #研修2026 のメモを書く");

        assertThat(tags).containsExactly("spring", "研修2026");
    }

    @Test
    @DisplayName("タグ抽出_同じタグが複数回ある場合_重複を除去する")
    void タグ抽出_同じタグが複数回ある場合_重複を除去する() {
        List<String> tags = tagExtractor.extract("#spring の話。もう一度 #spring と #java");

        assertThat(tags).containsExactly("spring", "java");
    }

    @Test
    @DisplayName("タグ抽出_タグがない場合_空配列を返す")
    void タグ抽出_タグがない場合_空配列を返す() {
        List<String> tags = tagExtractor.extract("今日はタグなしの投稿です #");

        assertThat(tags).isEmpty();
    }

    @Test
    @DisplayName("タグ抽出_本文がnullの場合_空配列を返す")
    void タグ抽出_本文がnullの場合_空配列を返す() {
        List<String> tags = tagExtractor.extract(null);

        assertThat(tags).isEmpty();
    }

    @Test
    @DisplayName("タグ抽出_タグの後ろに句読点がある場合_句読点を含めない")
    void タグ抽出_タグの後ろに句読点がある場合_句読点を含めない() {
        List<String> tags = tagExtractor.extract("#spring、#java. #spring_boot!");

        assertThat(tags).containsExactly("spring", "java", "spring_boot");
    }
}

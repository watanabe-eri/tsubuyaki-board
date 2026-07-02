package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Test
    @DisplayName("タグ別投稿一覧_GET_tags_name_Serviceから取得した投稿一覧とタグ名をModelに追加する")
    void タグ別投稿一覧_GET_tags_name_Serviceから取得した投稿一覧とタグ名をModelに追加する() throws Exception {
        Post post = new Post("alice", "Spring Boot のメモ #spring", Instant.parse("2026-05-23T10:15:00Z"));
        org.springframework.test.util.ReflectionTestUtils.setField(post, "id", 1L);
        given(tagService.findPostsByTagName("spring")).willReturn(List.of(post));
        given(tagService.extractTagsByPostId(List.of(post))).willReturn(Map.of(1L, List.of("spring")));

        mockMvc.perform(get("/tags/spring"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attribute("posts", List.of(post)))
                .andExpect(model().attribute("tagName", "spring"))
                .andExpect(model().attribute("tagsByPostId", Map.of(1L, List.of("spring"))))
                .andExpect(content().string(containsString("Spring Boot のメモ #spring")));

        verify(tagService).findPostsByTagName("spring");
        verify(tagService).extractTagsByPostId(List.of(post));
    }
}

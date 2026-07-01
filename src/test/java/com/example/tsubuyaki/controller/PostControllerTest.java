package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.service.PostService;
import com.example.tsubuyaki.web.dto.PostForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("投稿フォーム_GET_posts_new_ModelにPostFormを追加しposts_formを返す")
    void 投稿フォーム_GET_posts_new_ModelにPostFormを追加しposts_formを返す() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/form"))
                .andExpect(model().attributeExists("postForm"))
                .andExpect(model().attribute("postForm", org.hamcrest.Matchers.instanceOf(PostForm.class)));
    }

    @Test
    @DisplayName("投稿作成_入力値が正常な場合_Serviceを呼び出しpostsへリダイレクトする")
    void 投稿作成_入力値が正常な場合_Serviceを呼び出しpostsへリダイレクトする() throws Exception {
        mockMvc.perform(post("/posts")
                        .param("author", "alice")
                        .param("body", "共有事項があります"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).save("alice", "共有事項があります");
    }

    @Test
    @DisplayName("投稿作成_入力値が不正な場合_posts_formを再表示しServiceを呼び出さない")
    void 投稿作成_入力値が不正な場合_posts_formを再表示しServiceを呼び出さない() throws Exception {
        mockMvc.perform(post("/posts")
                        .param("author", "   ")
                        .param("body", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/form"))
                .andExpect(model().attributeHasFieldErrors("postForm", "author", "body"));

        verify(postService, never()).save(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("投稿詳細_存在するIDの場合_posts_detailを表示しModelにpostを追加する")
    void 投稿詳細_存在するIDの場合_posts_detailを表示しModelにpostを追加する() throws Exception {
        Post post = new Post("alice", "共有事項があります", Instant.parse("2026-05-23T10:15:00Z"));
        given(postService.findById(1L)).willReturn(Optional.of(post));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attribute("post", post))
                .andExpect(content().string(containsString("alice")))
                .andExpect(content().string(containsString("共有事項があります")));
    }

    @Test
    @DisplayName("投稿詳細_存在しないIDの場合_404を返す")
    void 投稿詳細_存在しないIDの場合_404を返す() throws Exception {
        given(postService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("投稿一覧_0件の場合_まだ投稿はありませんを表示できていること")
    void 投稿一覧_0件の場合_まだ投稿はありませんを表示できていること() throws Exception {
        given(postService.latest()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attribute("posts", Collections.emptyList()))
                .andExpect(content().string(containsString("まだ投稿はありません")));
    }

    @Test
    @DisplayName("投稿一覧_更新ボタンがある場合_押すとpostsにリクエストすること")
    void 投稿一覧_更新ボタンがある場合_押すとpostsにリクエストすること() throws Exception {
        given(postService.latest()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/posts\" method=\"get\"")))
                .andExpect(content().string(containsString("<button type=\"submit\">更新</button>")));
    }

    @Test
    @DisplayName("投稿一覧_投稿がある場合_投稿者内容投稿日の順に表示できていること")
    void 投稿一覧_投稿がある場合_投稿者内容投稿日の順に表示できていること() throws Exception {
        Post post = new Post("alice", "共有事項があります", Instant.parse("2026-05-23T10:15:00Z"));
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postService.latest()).willReturn(List.of(post));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/posts/1\"")))
                .andExpect(content().string(containsString("詳細を見る")))
                .andExpect(result -> {
                    String html = result.getResponse().getContentAsString();
                    int authorIndex = html.indexOf("alice");
                    int bodyIndex = html.indexOf("共有事項があります");
                    int createdAtIndex = html.indexOf("datetime=\"2026-05-23T10:15:00Z\"");

                    assertThat(authorIndex).isNotNegative();
                    assertThat(bodyIndex).isGreaterThan(authorIndex);
                    assertThat(createdAtIndex).isGreaterThan(bodyIndex);
                });
    }
}

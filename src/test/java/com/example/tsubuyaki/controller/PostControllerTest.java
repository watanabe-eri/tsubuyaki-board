package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.service.LikeService;
import com.example.tsubuyaki.service.PostService;
import com.example.tsubuyaki.service.TagService;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockitoBean
    private LikeService likeService;

    @MockitoBean
    private TagService tagService;

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
                        .param("body", "共有事項があります")
                        .param("avatarColor", "blue"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).save("alice", "共有事項があります", "blue");
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
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("投稿詳細_存在するIDの場合_posts_detailを表示しModelにpostを追加する")
    void 投稿詳細_存在するIDの場合_posts_detailを表示しModelにpostを追加する() throws Exception {
        Post post = new Post("alice", "共有事項があります", Instant.parse("2026-05-23T10:15:00Z"));
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postService.findById(1L)).willReturn(Optional.of(post));
        given(likeService.countByPostId(1L)).willReturn(3L);
        given(likeService.isLiked(eq(1L), anyString())).willReturn(true);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attribute("post", post))
                .andExpect(model().attribute("likeCount", 3L))
                .andExpect(model().attribute("liked", true))
                .andExpect(content().string(containsString("alice")))
                .andExpect(content().string(containsString("共有事項があります")))
                .andExpect(content().string(containsString("3")))
                .andExpect(content().string(containsString("❤️")))
                .andExpect(content().string(containsString("いいね")));
    }

    @Test
    @DisplayName("投稿詳細_存在しないIDの場合_404を返す")
    void 投稿詳細_存在しないIDの場合_404を返す() throws Exception {
        given(postService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("いいね切替_POST_posts_id_likes_ClientHashを生成しServiceを呼び出して詳細へリダイレクトする")
    void いいね切替_POST_posts_id_likes_ClientHashを生成しServiceを呼び出して詳細へリダイレクトする() throws Exception {
        mockMvc.perform(post("/posts/1/likes")
                        .with(request -> {
                            request.setRemoteAddr("192.0.2.10");
                            return request;
                        })
                        .header("User-Agent", "JUnit"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/posts/1"));

        verify(likeService).toggle(org.mockito.ArgumentMatchers.eq(1L), anyString());
    }

    @Test
    @DisplayName("投稿一覧_0件の場合_まだ投稿はありませんを表示できていること")
    void 投稿一覧_0件の場合_まだ投稿はありませんを表示できていること() throws Exception {
        given(postService.search(null)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attribute("posts", Collections.emptyList()))
                .andExpect(content().string(containsString("まだ投稿はありません")));
    }

    @Test
    @DisplayName("投稿一覧_更新ボタンがある場合_押すとpostsにリクエストすること")
    void 投稿一覧_更新ボタンがある場合_押すとpostsにリクエストすること() throws Exception {
        given(postService.search(null)).willReturn(Collections.emptyList());

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
        given(postService.search(null)).willReturn(List.of(post));
        given(tagService.extractTagsByPostId(List.of(post))).willReturn(Map.of(1L, List.of()));

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

    @Test
    @DisplayName("投稿一覧_本文にタグがある場合_本文の下にタグリンクを表示する")
    void 投稿一覧_本文にタグがある場合_本文の下にタグリンクを表示する() throws Exception {
        Post post = new Post("alice", "Spring Boot のメモ #java #spring", Instant.parse("2026-05-23T10:15:00Z"));
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postService.search(null)).willReturn(List.of(post));
        given(tagService.extractTagsByPostId(List.of(post))).willReturn(Map.of(1L, List.of("java", "spring")));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tagsByPostId", Map.of(1L, List.of("java", "spring"))))
                .andExpect(content().string(containsString("href=\"/tags/java\"")))
                .andExpect(content().string(containsString(">#java</a>")))
                .andExpect(content().string(containsString("href=\"/tags/spring\"")))
                .andExpect(content().string(containsString(">#spring</a>")));
    }

    @Test
    @DisplayName("投稿一覧_q指定ありの場合_検索結果を一覧表示し入力値を保持する")
    void 投稿一覧_q指定ありの場合_検索結果を一覧表示し入力値を保持する() throws Exception {
        Post post = new Post("alice", "Spring Boot のメモ", Instant.parse("2026-05-23T10:15:00Z"));
        given(postService.search("Spring")).willReturn(List.of(post));
        given(tagService.extractTagsByPostId(List.of(post))).willReturn(Map.of());

        mockMvc.perform(get("/posts").param("q", "Spring"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attribute("q", "Spring"))
                .andExpect(model().attribute("posts", List.of(post)))
                .andExpect(content().string(containsString("name=\"q\"")))
                .andExpect(content().string(containsString("value=\"Spring\"")))
                .andExpect(content().string(containsString("Spring Boot のメモ")));
    }
}

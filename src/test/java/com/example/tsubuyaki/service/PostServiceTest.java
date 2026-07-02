package com.example.tsubuyaki.service;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.domain.PostTag;
import com.example.tsubuyaki.domain.PostTagId;
import com.example.tsubuyaki.domain.Tag;
import com.example.tsubuyaki.repository.PostTagRepository;
import com.example.tsubuyaki.repository.PostRepository;
import com.example.tsubuyaki.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private TagExtractor tagExtractor;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("投稿一覧_latest実行時_Repositoryの新着50件を返す")
    void 投稿一覧_latest実行時_Repositoryの新着50件を返す() {
        List<Post> posts = List.of(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(posts);

        List<Post> latestPosts = postService.latest();

        assertThat(latestPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿一覧_latest実行時_Repositoryがnullを返すと空配列を返す")
    void 投稿一覧_latest実行時_Repositoryがnullを返すと空配列を返す() {
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(null);

        List<Post> latestPosts = postService.latest();

        assertThat(latestPosts).isEmpty();
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿作成_save実行時_投稿者本文作成日時を持つPostを保存する")
    void 投稿作成_save実行時_投稿者本文作成日時を持つPostを保存する() {
        Instant before = Instant.now();

        postService.save("alice", "共有事項があります");

        Instant after = Instant.now();
        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAuthor()).isEqualTo("alice");
        assertThat(savedPost.getBody()).isEqualTo("共有事項があります");
        assertThat(savedPost.getAvatarColor()).isEqualTo("blue");
        assertThat(savedPost.getCreatedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("投稿作成_save実行時_指定したアバター色を持つPostを保存する")
    void 投稿作成_save実行時_指定したアバター色を持つPostを保存する() {
        postService.save("alice", "共有事項があります", "purple");

        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAvatarColor()).isEqualTo("purple");
    }

    @Test
    @DisplayName("投稿作成_save実行時_Post保存後に本文からタグ一覧を取得する")
    void 投稿作成_save実行時_Post保存後に本文からタグ一覧を取得する() {
        Post savedPost = new Post("alice", "共有事項があります #spring", Instant.parse("2026-05-23T10:00:00Z"));
        Tag tag = new Tag("spring");
        ReflectionTestUtils.setField(savedPost, "id", 1L);
        ReflectionTestUtils.setField(tag, "id", 10L);
        given(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).willReturn(savedPost);
        given(tagExtractor.extract("共有事項があります #spring")).willReturn(List.of("spring"));
        given(tagRepository.findByName("spring")).willReturn(Optional.of(tag));
        given(postTagRepository.existsById(new PostTagId(1L, 10L))).willReturn(false);

        postService.save("alice", "共有事項があります #spring", "purple");

        InOrder inOrder = inOrder(postRepository, tagExtractor);
        inOrder.verify(postRepository).save(org.mockito.ArgumentMatchers.any(Post.class));
        inOrder.verify(tagExtractor).extract("共有事項があります #spring");
    }

    @Test
    @DisplayName("投稿作成_save実行時_既存タグがある場合_PostTagを保存する")
    void 投稿作成_save実行時_既存タグがある場合_PostTagを保存する() {
        Post savedPost = new Post("alice", "共有事項があります #spring", Instant.parse("2026-05-23T10:00:00Z"));
        Tag tag = new Tag("spring");
        ReflectionTestUtils.setField(savedPost, "id", 1L);
        ReflectionTestUtils.setField(tag, "id", 10L);
        given(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).willReturn(savedPost);
        given(tagExtractor.extract("共有事項があります #spring")).willReturn(List.of("spring"));
        given(tagRepository.findByName("spring")).willReturn(Optional.of(tag));
        given(postTagRepository.existsById(new PostTagId(1L, 10L))).willReturn(false);

        postService.save("alice", "共有事項があります #spring", "purple");

        org.mockito.ArgumentCaptor<PostTag> captor = org.mockito.ArgumentCaptor.forClass(PostTag.class);
        verify(postTagRepository).save(captor.capture());
        PostTag savedPostTag = captor.getValue();
        assertThat(savedPostTag.getPost()).isEqualTo(savedPost);
        assertThat(savedPostTag.getTag()).isEqualTo(tag);
        assertThat(savedPostTag.getId()).isEqualTo(new PostTagId(1L, 10L));
    }

    @Test
    @DisplayName("投稿作成_save実行時_未登録タグがある場合_Tagを作成してPostTagを保存する")
    void 投稿作成_save実行時_未登録タグがある場合_Tagを作成してPostTagを保存する() {
        Post savedPost = new Post("alice", "共有事項があります #spring", Instant.parse("2026-05-23T10:00:00Z"));
        Tag savedTag = new Tag("spring");
        ReflectionTestUtils.setField(savedPost, "id", 1L);
        ReflectionTestUtils.setField(savedTag, "id", 10L);
        given(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).willReturn(savedPost);
        given(tagExtractor.extract("共有事項があります #spring")).willReturn(List.of("spring"));
        given(tagRepository.findByName("spring")).willReturn(Optional.empty());
        given(tagRepository.save(org.mockito.ArgumentMatchers.any(Tag.class))).willReturn(savedTag);
        given(postTagRepository.existsById(new PostTagId(1L, 10L))).willReturn(false);

        postService.save("alice", "共有事項があります #spring", "purple");

        org.mockito.ArgumentCaptor<Tag> tagCaptor = org.mockito.ArgumentCaptor.forClass(Tag.class);
        org.mockito.ArgumentCaptor<PostTag> postTagCaptor = org.mockito.ArgumentCaptor.forClass(PostTag.class);
        verify(tagRepository).save(tagCaptor.capture());
        verify(postTagRepository).save(postTagCaptor.capture());
        assertThat(tagCaptor.getValue().getName()).isEqualTo("spring");
        assertThat(postTagCaptor.getValue().getPost()).isEqualTo(savedPost);
        assertThat(postTagCaptor.getValue().getTag()).isEqualTo(savedTag);
    }

    @Test
    @DisplayName("投稿作成_save実行時_同一投稿タグの関連が存在する場合_PostTagを保存しない")
    void 投稿作成_save実行時_同一投稿タグの関連が存在する場合_PostTagを保存しない() {
        Post savedPost = new Post("alice", "共有事項があります #spring", Instant.parse("2026-05-23T10:00:00Z"));
        Tag tag = new Tag("spring");
        ReflectionTestUtils.setField(savedPost, "id", 1L);
        ReflectionTestUtils.setField(tag, "id", 10L);
        given(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).willReturn(savedPost);
        given(tagExtractor.extract("共有事項があります #spring")).willReturn(List.of("spring"));
        given(tagRepository.findByName("spring")).willReturn(Optional.of(tag));
        given(postTagRepository.existsById(new PostTagId(1L, 10L))).willReturn(true);

        postService.save("alice", "共有事項があります #spring", "purple");

        verify(postTagRepository, never()).save(org.mockito.ArgumentMatchers.any(PostTag.class));
    }

    @Test
    @DisplayName("投稿作成_save実行時_アバター色がnullなら既定色を保存する")
    void 投稿作成_save実行時_アバター色がnullなら既定色を保存する() {
        postService.save("alice", "共有事項があります", null);

        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿作成_save実行時_アバター色が空白なら既定色を保存する")
    void 投稿作成_save実行時_アバター色が空白なら既定色を保存する() {
        postService.save("alice", "共有事項があります", "   ");

        org.mockito.ArgumentCaptor<Post> captor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();
        assertThat(savedPost.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿詳細_findById実行時_Repositoryの検索結果を返す")
    void 投稿詳細_findById実行時_Repositoryの検索結果を返す() {
        Post post = new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z"));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        Optional<Post> foundPost = postService.findById(1L);

        assertThat(foundPost).contains(post);
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("投稿一覧_search実行時_キーワード空なら新着一覧を返す")
    void 投稿一覧_search実行時_キーワード空なら新着一覧を返す() {
        List<Post> posts = List.of(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(posts);

        List<Post> foundPosts = postService.search("   ");

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿一覧_search実行時_キーワードnullなら新着一覧を返す")
    void 投稿一覧_search実行時_キーワードnullなら新着一覧を返す() {
        List<Post> posts = List.of(new Post("alice", "hello", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByOrderByCreatedAtDesc()).willReturn(posts);

        List<Post> foundPosts = postService.search(null);

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("投稿一覧_search実行時_キーワードありなら本文検索結果を返す")
    void 投稿一覧_search実行時_キーワードありなら本文検索結果を返す() {
        List<Post> posts = List.of(new Post("alice", "Spring", Instant.parse("2026-05-23T10:00:00Z")));
        given(postRepository.findTop50ByBodyContainingOrderByCreatedAtDesc("Spring")).willReturn(posts);

        List<Post> foundPosts = postService.search("Spring");

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findTop50ByBodyContainingOrderByCreatedAtDesc("Spring");
    }
}

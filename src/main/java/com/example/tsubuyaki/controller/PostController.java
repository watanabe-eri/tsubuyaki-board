package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.service.LikeService;
import com.example.tsubuyaki.service.PostService;
import com.example.tsubuyaki.service.TagService;
import com.example.tsubuyaki.web.dto.PostForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    private final PostService postService;

    private final LikeService likeService;

    private final TagService tagService;

    public PostController(PostService postService, LikeService likeService, TagService tagService) {
        this.postService = postService;
        this.likeService = likeService;
        this.tagService = tagService;
    }

    @GetMapping({ "/", "/posts" })
    public String list(@RequestParam(required = false) String q, Model model) {
        List<Post> posts = postService.search(q);
        model.addAttribute("posts", posts);
        model.addAttribute("q", q);
        model.addAttribute("tagsByPostId", tagsByPostId(posts));
        return "posts/list";
    }

    @GetMapping("/posts/new")
    public String newForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "posts/form";
    }

    @PostMapping("/posts")
    public String create(@Valid PostForm postForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "posts/form";
        }
        postService.save(postForm.getAuthor(), postForm.getBody(), postForm.getAvatarColor());
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("post", postService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("likeCount", likeService.countByPostId(id));
        model.addAttribute("liked", likeService.isLiked(id, clientHash(request)));
        return "posts/detail";
    }

    @PostMapping("/posts/{id}/likes")
    public String toggleLike(@PathVariable Long id, HttpServletRequest request) {
        likeService.toggle(id, clientHash(request));
        return "redirect:/posts/" + id;
    }

    private String clientHash(HttpServletRequest request) {
        String source = request.getRemoteAddr() + "|" + request.getHeader("User-Agent");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }

    private Map<Long, List<String>> tagsByPostId(List<Post> posts) {
        Map<Long, List<String>> tagsByPostId = tagService.extractTagsByPostId(posts);
        if (tagsByPostId == null) {
            return Map.of();
        }
        return new LinkedHashMap<>(tagsByPostId);
    }
}

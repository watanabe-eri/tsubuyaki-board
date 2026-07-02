package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.domain.Post;
import com.example.tsubuyaki.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags/{name}")
    public String listByTag(@PathVariable String name, Model model) {
        List<Post> posts = tagService.findPostsByTagName(name);
        model.addAttribute("posts", posts);
        model.addAttribute("tagName", name);
        model.addAttribute("tagsByPostId", tagsByPostId(posts));
        return "posts/list";
    }

    private Map<Long, List<String>> tagsByPostId(List<Post> posts) {
        Map<Long, List<String>> tagsByPostId = tagService.extractTagsByPostId(posts);
        return new LinkedHashMap<>(tagsByPostId);
    }
}

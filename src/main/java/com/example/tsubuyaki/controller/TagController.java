package com.example.tsubuyaki.controller;

import com.example.tsubuyaki.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags/{name}")
    public String listByTag(@PathVariable String name, Model model) {
        model.addAttribute("posts", tagService.findPostsByTagName(name));
        model.addAttribute("tagName", name);
        return "posts/list";
    }
}

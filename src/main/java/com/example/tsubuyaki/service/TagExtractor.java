package com.example.tsubuyaki.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TagExtractor {

    private static final Pattern TAG_PATTERN = Pattern.compile("#([\\p{L}\\p{N}_]+)");

    public List<String> extract(String body) {
        if (body == null || body.isBlank()) {
            return List.of();
        }

        Matcher matcher = TAG_PATTERN.matcher(body);
        Set<String> tags = new LinkedHashSet<>();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }
        return new ArrayList<>(tags);
    }
}

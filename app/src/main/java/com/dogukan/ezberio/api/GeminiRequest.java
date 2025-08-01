package com.dogukan.ezberio.api;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    public GeminiRequest(String promptText) {
        this.contents = Collections.singletonList(new Content(promptText));
    }

    public static class Content {
        public List<Part> parts;
        public String role = "user";

        public Content(String promptText) {
            this.parts = Collections.singletonList(new Part(promptText));
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
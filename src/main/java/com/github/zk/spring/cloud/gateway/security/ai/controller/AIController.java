package com.github.zk.spring.cloud.gateway.security.ai.controller;

import com.github.zk.spring.cloud.gateway.security.ai.pojo.AiRequest;
import com.github.zk.spring.cloud.gateway.security.ai.service.AIService;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/ai/chat")
    public Mono<Response> chat(@RequestBody AiRequest request) {
        return aiService.chat(request)
                .map(aiResponse -> Response.setOk(aiResponse))
                .onErrorResume(e -> Mono.just(Response.setError("AI处理失败: " + e.getMessage())));
    }
}

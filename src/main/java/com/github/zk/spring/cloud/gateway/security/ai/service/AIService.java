package com.github.zk.spring.cloud.gateway.security.ai.service;

import com.github.zk.spring.cloud.gateway.security.ai.pojo.AiRequest;
import com.github.zk.spring.cloud.gateway.security.ai.pojo.AiResponse;
import reactor.core.publisher.Mono;

public interface AIService {
    Mono<AiResponse> chat(AiRequest request);
}

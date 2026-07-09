package com.github.zk.spring.cloud.gateway.security.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.ai.config.AiProperties;
import com.github.zk.spring.cloud.gateway.security.ai.pojo.AiRequest;
import com.github.zk.spring.cloud.gateway.security.ai.pojo.AiResponse;
import com.github.zk.spring.cloud.gateway.security.ai.service.AIService;
import com.github.zk.spring.cloud.gateway.security.ai.service.AIToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);

    private final AiProperties aiProperties;
    private final AIToolExecutor toolExecutor;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIServiceImpl(AiProperties aiProperties, AIToolExecutor toolExecutor) {
        this.aiProperties = aiProperties;
        this.toolExecutor = toolExecutor;
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl(aiProperties.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey())
                .build();
    }

    @Override
    public Mono<AiResponse> chat(AiRequest request) {
        Map<String, Object> body = buildRequestBody(request);

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::processResponse)
                .onErrorResume(e -> {
                    log.error("AI service call failed", e);
                    return Mono.just(new AiResponse("❌ 调用AI服务失败: " + e.getMessage(), false));
                });
    }

    private Map<String, Object> buildRequestBody(AiRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel());

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", aiProperties.getSystemPrompt()));

        List<Map<String, String>> history = request.getHistory();
        if (history != null) {
            for (Map<String, String> msg : history) {
                messages.add(Map.of("role", msg.get("role"), "content", msg.get("content")));
            }
        }

        messages.add(Map.of("role", "user", "content", request.getMessage()));
        body.put("messages", messages);

        body.put("tools", toolExecutor.getToolDefinitions());
        body.put("tool_choice", "auto");

        return body;
    }

    private AiResponse processResponse(JsonNode response) {
        JsonNode choices = response.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            return new AiResponse("❌ AI返回格式异常", false);
        }

        JsonNode message = choices.get(0).get("message");
        if (message == null) {
            return new AiResponse("❌ AI返回消息为空", false);
        }

        JsonNode toolCalls = message.get("tool_calls");
        if (toolCalls != null && toolCalls.isArray() && toolCalls.size() > 0) {
            StringBuilder resultBuilder = new StringBuilder();
            for (JsonNode tc : toolCalls) {
                String funcName = tc.get("function").get("name").asText();
                String argsStr = tc.get("function").get("arguments").asText();
                try {
                    Map<String, Object> args = objectMapper.readValue(argsStr,
                            new TypeReference<Map<String, Object>>() {});
                    String result = toolExecutor.execute(funcName, args);
                    resultBuilder.append(result).append("\n");
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse tool arguments", e);
                    resultBuilder.append("❌ 解析工具参数失败: ").append(e.getMessage()).append("\n");
                }
            }
            return new AiResponse(resultBuilder.toString().trim(), true);
        }

        String content = message.has("content") && !message.get("content").isNull()
                ? message.get("content").asText() : "";
        return new AiResponse(content, false);
    }
}

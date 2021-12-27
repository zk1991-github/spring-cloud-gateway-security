package com.github.zk.spring.cloud.gateway.security.jackson2;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

/**
 * 微信认证token 序列化器
 *
 * @author zk
 * @date 2021/12/7 15:14
 */
class WeChatAuthenticationTokenDeserializer extends JsonDeserializer<WeChatAuthenticationToken> {
    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<List<GrantedAuthority>>() {
    };

    private static final TypeReference<Object> OBJECT = new TypeReference<Object>() {
    };

    @Override
    public WeChatAuthenticationToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = getPrincipal(mapper, principalNode);

        List<GrantedAuthority> authorities = mapper.readValue(readJsonNode(jsonNode, "authorities").traverse(mapper),
                GRANTED_AUTHORITY_LIST);
        WeChatAuthenticationToken token = (!authenticated)
                ? new WeChatAuthenticationToken(principal)
                : new WeChatAuthenticationToken(principal, authorities);
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        }
        else {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode)
            throws IOException, JsonParseException, JsonMappingException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse(mapper), Object.class);
        }
        return principalNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}

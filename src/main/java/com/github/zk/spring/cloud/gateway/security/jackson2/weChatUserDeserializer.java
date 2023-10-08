/*
 *
 *  * Copyright 2021-2023 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.zk.spring.cloud.gateway.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.userdetails.User;

/**
 * 微信序列化
 *
 * @author zk
 * @date 2021/12/7 15:35
 */
class weChatUserDeserializer extends JsonDeserializer<WeChatUserInfo> {

    private static final TypeReference<List<RoleInfo>> ROLE_INFO_LIST = new TypeReference<List<RoleInfo>>() {
    };

    /**
     * This method will create {@link User} object. It will ensure successful object
     * creation even if password key is null in serialized json, because credentials may
     * be removed from the {@link User} by invoking {@link User#eraseCredentials()}. In
     * that case there won't be any password key in serialized json.
     * @param jp the JsonParser
     * @param ctxt the DeserializationContext
     * @return the user
     * @throws IOException if a exception during IO occurs
     * @throws JsonProcessingException if an error during JSON processing occurs
     */
    @Override
    public WeChatUserInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);

        String openid = readJsonNode(jsonNode, "openid").asText();
        String nickName = readJsonNode(jsonNode, "nickName").asText();
        Integer gender = readJsonNode(jsonNode, "gender").asInt();
        String avatarUrl = readJsonNode(jsonNode, "avatarUrl").asText();
        String country = readJsonNode(jsonNode, "country").asText();
        String province = readJsonNode(jsonNode, "province").asText();
        String city = readJsonNode(jsonNode, "city").asText();
        List<RoleInfo> roles = mapper.convertValue(jsonNode.get("roles"),
                ROLE_INFO_LIST);

        WeChatUserInfo result = new WeChatUserInfo(openid, nickName, gender, avatarUrl, country, province, city);
        result.setRoles(roles);
        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}

/*
 *
 *  * Copyright 2021-2024 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * mysql转sqlite导入sql语句
 *
 * @author zhaokai
 * @since 4.3.6
 */
public class MysqlToSqliteUtils {

    private static final String MYSQL_PATH = "/database/security_mysql.sql";
    private static final String SQLITE_PATH = "/database/security_sqlite.sql";
    public static void main(String[] args) {
        //当前工程路径
        String projectPath = System.getProperty("user.dir");

        Path sqlitePath = Paths.get(projectPath + SQLITE_PATH);
        if (Files.exists(sqlitePath)) {
            try {
                Files.delete(sqlitePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path path = Paths.get(projectPath + MYSQL_PATH);
        try(Stream<String> lines = Files.lines(path)) {
            lines.map(line -> {
                if (line.contains("ENGINE")) {
                    line = line.substring(0, line.indexOf("ENGINE"));
                    line += ";";
                }
                if (line.contains("COMMENT")) {
                    line = line.substring(0, line.indexOf("COMMENT"));
                    line += ",";
                }
                line = line.replace("ON UPDATE CURRENT_TIMESTAMP", "");

                return line;
            }).forEach(line -> {
                try {
                    Files.write(sqlitePath, Collections.singletonList(line),StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}

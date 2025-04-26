/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.core.pojo.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/1/8 15:52
 */
@Data
public class MessagesResponseVO implements Serializable {
    private static final long serialVersionUID = -3080698054707568260L;

    /**
     * id (string) 消息 ID
     */
    private String id;
    /**
     * conversation_id (string) 会话 ID
     */
    @JsonAlias("conversation_id")
    private String conversationId;
    /**
     * inputs (object) 用户输入参数。
     */
    private Map<String, Object> inputs;
    /**
     * query (string) 用户输入 / 提问内容。
     */
    private String query;
    /**
     * message_files (array[object]) 消息文件
     */
    @JsonAlias("message_files")
    private List<MessageFile> messageFiles;

    /**
     * answer (string) 回答消息内容
     */
    private String answer;
    /**
     * created_at (timestamp) 创建时间
     */
    @JsonAlias("created_at")
    private Long createdAt;
    /**
     * feedback (object) 反馈信息
     */
    private Feedback feedback;


    @Data
    public static class MessageFile implements Serializable {

        private static final long serialVersionUID = -2636950943739066320L;

        /**
         * id (string) ID
         */
        private String id;
        /**
         * type (string) 文件类型，image 图片
         */
        private String type;
        /**
         * url (string) 预览图片地址
         */
        private String url;
        /**
         * belongs_to (string) 文件归属方，user 或 assistant
         */
        @JsonAlias("belongs_to")
        private String belongsTo;

        /**
         * agent_thoughts (array[object]) Agent思考内容（仅Agent模式下不为空）
         */
        @JsonAlias("agent_thoughts")
        private List<MessageFileAgentThought> agentThoughts;
    }

    @Data
    public static class MessageFileAgentThought implements Serializable {

        private static final long serialVersionUID = 2931496090550003613L;

        /**
         * id (string) agent_thought ID，每一轮Agent迭代都会有一个唯一的id
         */
        private String id;
        /**
         * message_id (string) 消息唯一ID
         */
        @JsonAlias("message_id")
        private String messageId;
        /**
         * position (int) agent_thought在消息中的位置，如第一轮迭代position为1
         */
        private Integer position;
        /**
         * thought (string) agent的思考内容
         */
        private String thought;
        /**
         * observation (string) 工具调用的返回结果
         */
        private String observation;
        /**
         * tool (string) 使用的工具列表，以 ; 分割多个工具
         */
        private String tool;
        /**
         * tool_input (string) 工具的输入，JSON格式的字符串(object)。如：{"dalle3": {"prompt": "a cute cat"}}
         */
        @JsonAlias("tool_input")
        private String toolInput;
        /**
         * created_at (int) 创建时间戳，如：1705395332
         */
        @JsonAlias("created_at")
        private Long createdAt;
        /**
         * message_files (array[string]) 当前agent_thought 关联的文件ID
         */
        @JsonAlias("message_files")
        private List<String> messageFiles;
        /**
         * conversation_id (string) 会话ID
         */
        @JsonAlias("conversation_id")
        private String conversationId;
    }

    @Data
    public static class Feedback implements Serializable {

        private static final long serialVersionUID = -8145494645071051252L;

        /**
         * 点赞 like / 点踩 dislike
         */
        private String rating;
    }
}

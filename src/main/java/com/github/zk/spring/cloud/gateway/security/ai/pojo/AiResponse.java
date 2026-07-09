package com.github.zk.spring.cloud.gateway.security.ai.pojo;

public class AiResponse {
    private String reply;
    private boolean actionPerformed;

    public AiResponse() {}

    public AiResponse(String reply, boolean actionPerformed) {
        this.reply = reply;
        this.actionPerformed = actionPerformed;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public boolean isActionPerformed() { return actionPerformed; }
    public void setActionPerformed(boolean actionPerformed) { this.actionPerformed = actionPerformed; }
}

package com.hl.java.vo;
//服务器与客户端传递信息的载体
import lombok.Data;
@Data
public class MessageVO {
    /**
     * 告知服务器要进行的动作。1 表示用户注册；2 表示私聊；3 建群 4  群聊
     */
    private String type;
    /**
     *告知要发送到服务器的具体内容
     */
    private String content;
    /**
     * 私聊告知服务器要将消息发送给那个用户
     */
    private String to;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

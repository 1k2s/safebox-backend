package com.senai.safebox.wapAPI.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name="message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Nullable
    private String recipient;

    @Column
    @Nullable
    private String content;

    @Column
    @Nullable
    private Boolean success;

    @Column
    @Nullable
    private String timestamp;


    // Construtor vazio necessário para o Jackson deserializar JSON em objetos(Se necessário). JPA precisa dele para criar instâncias ao buscar dados do banco.
    public MessageEntity() {}


    public MessageEntity(@Nullable String recipient, @Nullable String content, @Nullable Boolean success, @Nullable String timestamp) {
        this.recipient = recipient;
        this.content = content;
        this.success = success;
        this.timestamp = timestamp;
    }

    // Getters e Setters


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(@Nullable String recipient) {
        this.recipient = recipient;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public void setContent(@Nullable String content) {
        this.content = content;
    }

    @Nullable
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(@Nullable Boolean success) {
        this.success = success;
    }

    @Nullable
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@Nullable String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "message{" +
                "id='" + id + '\'' +
                ", recipient='" + recipient + '\'' +
                ", content='" + content + '\'' +
                ", success='" + success + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

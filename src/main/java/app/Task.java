package app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Task {

    String status;

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    UUID id;

    LocalDateTime timestamp;

    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Task(String status, LocalDateTime date) {
        this.status = status;
        this.timestamp = date;
    }

    public Task() {
        status = "created";
        timestamp = LocalDateTime.now();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setId(UUID id) {
        this.id = id;
    }



}

package app;



import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
public class Task {

    String status;

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    UUID id;

    ZonedDateTime timestamp;

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Task(String status, ZonedDateTime date) {
        this.status = status;
        this.timestamp = date;
    }

    public Task() {
        status = "created";
        Instant now = Instant.now();
        timestamp = now.atZone(ZoneId.of("Europe/Moscow"));
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

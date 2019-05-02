package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RestController
public class Controller
{
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/task")
    public ResponseEntity<?> create() {
        Task task = taskRepository.save(new Task());
        new Thread(() -> {
            task.setStatus("running");
            task.setTimestamp(LocalDateTime.now());
            taskRepository.save(task);
            try {
                TimeUnit.MINUTES.sleep(2);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            task.setTimestamp(LocalDateTime.now());
            task.setStatus("finished");
            taskRepository.save(task);
        }).start();
        return new ResponseEntity<>(task.Id(),HttpStatus.valueOf(202));
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        try{
            UUID uuid = UUID.fromString(id);
            Optional<Task> task = taskRepository.findById(uuid);
            if (task.isPresent())
                return new ResponseEntity<>(task.get(),HttpStatus.valueOf(200));
            return new ResponseEntity<>(HttpStatus.valueOf(404));
        } catch (IllegalArgumentException exception){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
    }
    



    @GetMapping("/")
    public String testHeroku() {
        return "Hello,world!";
    }

}

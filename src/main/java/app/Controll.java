package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.*;


@RestController
@EnableAsync
class Controller
{

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/task")
    public ResponseEntity<?> doSmth() {
        Task task = new Task();
        taskRepository.save(task);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.submit(() -> {
            Task us = taskRepository.findById(task.id).get();
            us.setStatus("running");
            us.setTimestamp(LocalDateTime.now());
            taskRepository.save(us);
            try {
                TimeUnit.MINUTES.sleep(2);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            us.setTimestamp(LocalDateTime.now());
            us.setStatus("finished");
            taskRepository.save(us);
        });
        return new ResponseEntity<>(task.id,HttpStatus.valueOf(202));
    }

    @GetMapping("/task")
    public ResponseEntity<?> done(@RequestParam("id") String id) {

        try{
            UUID uuid = UUID.fromString(id);
            Task task = taskRepository.findById(uuid).get();
            if (task == null)
                return new ResponseEntity<>(HttpStatus.valueOf(404));
            return new ResponseEntity<>(task,HttpStatus.valueOf(200));
        } catch (IllegalArgumentException exception){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
    }

}

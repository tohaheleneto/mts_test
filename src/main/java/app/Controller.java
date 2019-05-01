package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@RestController
@EnableAsync
public class Controller
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
            Optional<Task> task = taskRepository.findById(uuid);
            if (task.isPresent())
                return new ResponseEntity<>(task.get(),HttpStatus.valueOf(200));
            return new ResponseEntity<>(HttpStatus.valueOf(404));
        } catch (IllegalArgumentException exception){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
    }
    
    /*
        Method that start threads to managing of unfinished task if server restarted
    */
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        ArrayList<Task> tasksNeedToChangeStatus = new ArrayList<>();

        for (Task task : taskRepository.findAll())
        {
            if (task.status.equals("running")) {
                if (task.getTimestamp().until(LocalDateTime.now(), ChronoUnit.MINUTES) >= 2) {
                    task.setTimestamp(task.getTimestamp().plusMinutes(2));
                    task.setStatus("finished");
                    taskRepository.save(task);
                }
                else {
                    tasksNeedToChangeStatus.add(task);
                }
            }
        }
        ScheduledExecutorService service = Executors.newScheduledThreadPool(tasksNeedToChangeStatus.size());
        for (Task task : tasksNeedToChangeStatus)
        {
            service.submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(LocalDateTime.now().until(task.timestamp.plusMinutes(2), ChronoUnit.MILLIS));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                task.setTimestamp(task.getTimestamp().plusMinutes(2));
                task.setStatus("finished");
                taskRepository.save(task);
            });
        }
    }

    @GetMapping("/")
    public String testHeroku() {
        return "Hello,world!";
    }

}

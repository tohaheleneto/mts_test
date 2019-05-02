package app;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@EnableAsync
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    private TaskRepository taskRepository;

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
}
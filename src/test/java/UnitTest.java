import app.Main;
import app.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes={Main.class})
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UnitTest {

    @MockBean
    TaskRepository taskRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    public void test_postMapping() {
        try {
            mvc.perform(MockMvcRequestBuilders.post("/task")).andExpect(status().isAccepted());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getMapping_NotUUID() {
        try {
            mvc.perform(MockMvcRequestBuilders.get("/task").param("id","NOT UUID")).andExpect(status().is(400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getMappingNoUUIDInRepository() {
        try {
            UUID id = java.util.UUID.randomUUID();
            given(taskRepository.findById(id)).willReturn(Optional.empty());
            mvc.perform(MockMvcRequestBuilders.get("/task").param("id",id.toString())).andExpect(status().is(404));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

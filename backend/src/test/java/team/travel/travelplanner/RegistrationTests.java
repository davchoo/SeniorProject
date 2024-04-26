package team.travel.travelplanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.UserSignUpModel;
import team.travel.travelplanner.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase
public class RegistrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSignUpSuccess() throws Exception {
        UserSignUpModel userSignUpModel = new UserSignUpModel("John", "Doe", "johndoe", "#MagicPancakes123$%");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSignUpModel)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User is registered successfully on TripEase!"));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testSignUpUsernameExists() throws Exception {
        UserSignUpModel userSignUpModel = new UserSignUpModel("John", "Doe", "johndoe", "#MagicPancakes123$%");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSignUpModel)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username, johndoe, already exists"));
    }

    @Test
    public void testMissingFields() throws Exception {
        UserSignUpModel userSignUpModel = new UserSignUpModel("", "User", "user1", "#MagicPancakes123$%");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSignUpModel)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testInvalidPasswordFormat() throws Exception {
        UserSignUpModel userSignUpModel = new UserSignUpModel("Jane", "Doe", "janedoe", "abc123");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSignUpModel)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

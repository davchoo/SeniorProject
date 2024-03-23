package team.travel.travelplanner;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import team.travel.travelplanner.model.UserModel;
import team.travel.travelplanner.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSignUpSuccess() throws Exception {
        UserModel userModel = new UserModel("John", "Doe", "johndoe", "password123");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userModel)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User is registered successfully on TripEase!"));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testSignUpUsernameExists() throws Exception {
        UserModel userModel = new UserModel("John", "Doe", "johndoe", "password123");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userModel)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username, johndoe, already exists"));
    }

    @Test
    public void testMissingFields() throws Exception {
        UserModel userModel = new UserModel("", "User", "user1", "password123");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userModel)))
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

package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = HelpRequestController.class)
@Import(TestConfig.class)

public class HelpRequestControllerTests extends ControllerTestCase {

  @MockBean
  HelpRequestRepository helpRequestRepository;

  @MockBean
  UserRepository userRepository;

   // Authorization tests for /api/helprequest/admin/all

   @Test
   public void logged_out_users_cannot_get_all() throws Exception {
           mockMvc.perform(get("/api/helprequest/all"))
                           .andExpect(status().is(403)); // logged out users can't get all
   }

   @WithMockUser(roles = { "USER" })
   @Test
   public void logged_in_users_can_get_all() throws Exception {
           mockMvc.perform(get("/api/helprequest/all"))
                           .andExpect(status().is(200)); // logged
   }

  //  @Test
  //  public void logged_out_users_cannot_get_by_id() throws Exception {
  //          mockMvc.perform(get("/api/helprequest?id=7"))
  //                          .andExpect(status().is(403)); // logged out users can't get by id
  //  }

    // Authorization tests for /api/helprequest/post
        // (Perhaps should also have these for put and delete)

        // @Test
        // public void logged_out_users_cannot_post() throws Exception {
        //         mockMvc.perform(post("/api/helprequest/post"))
        //                         .andExpect(status().is(403));
        // }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/helprequest/post"))
                                .andExpect(status().is(403)); // only admins can post
        }
        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void admin_user_can_post_a_new_helprequest() throws Exception {
            // Arrange
            LocalDateTime requestTime = LocalDateTime.parse("2024-10-29T15:30:00");
            HelpRequest helpRequest = new HelpRequest();
            helpRequest.setRequesterEmail("test.user@example.com");
            helpRequest.setTeamId("Team001");
            helpRequest.setTableOrBreakoutRoom("Table 5");
            helpRequest.setRequestTime(requestTime);
            helpRequest.setExplanation("Need assistance with project setup and configuration.");
            helpRequest.setSolved(true);  // Explicitly set solved to true for testing
            
            // Mock the repository save method to return the helpRequest itself
            when(helpRequestRepository.save(any(HelpRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            MvcResult response = mockMvc.perform(
                    post("/api/helprequest/post")
                            .param("requesterEmail", "test.user@example.com")
                            .param("teamId", "Team001")
                            .param("tableOrBreakoutRoom", "Table 5")
                            .param("requestTime", "2024-10-29T15:30:00")
                            .param("explanation", "Need assistance with project setup and configuration.")
                            .param("solved", "true")  // Set solved to true in the request parameters
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();
            
            // Capture the argument passed to save and verify each field
            verify(helpRequestRepository, times(1)).save(any(HelpRequest.class));
            
            // Use an ArgumentCaptor to capture the exact HelpRequest object passed to save
            ArgumentCaptor<HelpRequest> captor = ArgumentCaptor.forClass(HelpRequest.class);
            verify(helpRequestRepository).save(captor.capture());
            HelpRequest savedRequest = captor.getValue();
            
            // Assert each field, including solved, is set correctly in the HelpRequest object
            assertEquals("test.user@example.com", savedRequest.getRequesterEmail());
            assertEquals("Team001", savedRequest.getTeamId());
            assertEquals("Table 5", savedRequest.getTableOrBreakoutRoom());
            assertEquals(requestTime, savedRequest.getRequestTime());
            assertEquals("Need assistance with project setup and configuration.", savedRequest.getExplanation());
            assertEquals(true, savedRequest.getSolved());  // Explicit check for the solved field set to true
            
            // Optionally, verify the JSON response if needed
            String expectedJson = mapper.writeValueAsString(helpRequest);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
        }
        

        


        @WithMockUser(roles = { "USER" })
@Test
public void logged_in_user_can_get_all_helprequests() throws Exception {
    // Arrange
    LocalDateTime requestTime1 = LocalDateTime.parse("2024-10-29T15:30:00");
    HelpRequest helpRequest1 = new HelpRequest();
    helpRequest1.setRequesterEmail("user1@example.com");
    helpRequest1.setTeamId("Team001");
    helpRequest1.setTableOrBreakoutRoom("Table 5");
    helpRequest1.setRequestTime(requestTime1);
    helpRequest1.setExplanation("Need help with project setup.");
    helpRequest1.setSolved(false);

    LocalDateTime requestTime2 = LocalDateTime.parse("2024-11-01T15:30:00");
    HelpRequest helpRequest2 = new HelpRequest();
    helpRequest2.setRequesterEmail("user2@example.com");
    helpRequest2.setTeamId("Team002");
    helpRequest2.setTableOrBreakoutRoom("Table 6");
    helpRequest2.setRequestTime(requestTime2);
    helpRequest2.setExplanation("Need assistance with debugging.");
    helpRequest2.setSolved(true);

    ArrayList<HelpRequest> helpRequests = new ArrayList<>(Arrays.asList(helpRequest1, helpRequest2));
    when(helpRequestRepository.findAll()).thenReturn(helpRequests);

    // Act
    MvcResult response = mockMvc.perform(get("/api/helprequest/all"))
            .andExpect(status().isOk())
            .andReturn();

    // Assert
    verify(helpRequestRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(helpRequests);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
}
        
}


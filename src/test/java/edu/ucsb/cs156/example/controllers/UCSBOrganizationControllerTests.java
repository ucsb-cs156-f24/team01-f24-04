package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
        
        @MockBean
        UCSBOrganizationsRepository ucsbOrganizationsRepository;

        @MockBean
        UserRepository userRepository;

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                        .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                        .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations?orgCode=csu"))
                        .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/ucsborganizations/post
        // (Perhaps should also have these for put and delete)
        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                        .andExpect(status().is(403));
        }

        // @WithMockUser(roles = { "USER" })
        // @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                        .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
                // arrange
                UCSBOrganizations csu = UCSBOrganizations.builder()
                        .orgCode("csu")
                        .orgTranslationShort("CSU")
                        .orgTranslation("UCSB Chinese Student Union")
                        .inactive(false)
                        .build();
                when(ucsbOrganizationsRepository.findById(eq("csu"))).thenReturn(Optional.of(csu));
                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=csu"))
                        .andExpect(status().isOk()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(eq("csu"));
                String expectedJson = mapper.writeValueAsString(csu);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
                // arrange
                when(ucsbOrganizationsRepository.findById(eq("apples"))).thenReturn(Optional.empty());
                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=apples"))
                        .andExpect(status().isNotFound()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(eq("apples"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganizations with id apples not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganizations() throws Exception {
        
                // arrange

                UCSBOrganizations csu = UCSBOrganizations.builder()
                        .orgCode("csu")
                        .orgTranslationShort("CSU")
                        .orgTranslation("UCSB Chinese Student Union")
                        .inactive(false)
                        .build();
                
                UCSBOrganizations kasa = UCSBOrganizations.builder()
                        .orgCode("kasa")
                        .orgTranslationShort("KASA")
                        .orgTranslation("UCSB Korean American Student Association")
                        .inactive(false)
                        .build();

                ArrayList<UCSBOrganizations> expectedOrganizations = new ArrayList<>();
                expectedOrganizations.addAll(Arrays.asList(csu, kasa));

                when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrganizations);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                        .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_org() throws Exception {
                // arrange

                UCSBOrganizations tasa = UCSBOrganizations.builder()
                        .orgCode("tasa")
                        .orgTranslationShort("TASA")
                        .orgTranslation("UCSB Taiwanese American Student Association")
                        .inactive(true)
                        .build();

                when(ucsbOrganizationsRepository.save(eq(tasa))).thenReturn(tasa);

                // act
                MvcResult response = mockMvc.perform(
                        post("/api/ucsborganizations/post?orgCode=tasa&orgTranslationShort=TASA&orgTranslation=UCSB Taiwanese American Student Association&inactive=true")
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).save(tasa);
                String expectedJson = mapper.writeValueAsString(tasa);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_orgs() throws Exception {
                // arrange
                UCSBOrganizations vsaOrig = UCSBOrganizations.builder()
                        .orgCode("vsa")
                        .orgTranslationShort("VSA")
                        .orgTranslation("UCSB Vietnamese Student Association")
                        .inactive(false)
                        .build();
                UCSBOrganizations vsaEdited = UCSBOrganizations.builder()
                        .orgCode("vs")
                        .orgTranslationShort("VS")
                        .orgTranslation("Vietnamese Student")
                        .inactive(true)
                        .build();
                
                String requestBody = mapper.writeValueAsString(vsaEdited);
                when(ucsbOrganizationsRepository.findById(eq("vsa"))).thenReturn(Optional.of(vsaOrig));
                // act
                MvcResult response = mockMvc.perform(
                        put("/api/ucsborganizations?orgCode=vsa")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("vsa");
                verify(ucsbOrganizationsRepository, times(1)).save(vsaEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_orgs_that_does_not_exist() throws Exception {
                // arrange
                UCSBOrganizations editedOrgs = UCSBOrganizations.builder()
                        .orgCode("nsu")
                        .orgTranslationShort("NSU")
                        .orgTranslation("UCSB Nikkei Student Union")
                        .inactive(false)
                        .build();
                String requestBody = mapper.writeValueAsString(editedOrgs);
                when(ucsbOrganizationsRepository.findById(eq("nsu"))).thenReturn(Optional.empty());
                // act
                MvcResult response = mockMvc.perform(
                        put("/api/ucsborganizations?orgCode=nsu")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("nsu");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id nsu not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_organizations() throws Exception {
                // arrange
                UCSBOrganizations csu = UCSBOrganizations.builder()
                        .orgCode("csu")
                        .orgTranslationShort("CSU")
                        .orgTranslation("UCSB Chinese Student Union")
                        .inactive(false)
                        .build();
                when(ucsbOrganizationsRepository.findById(eq("csu"))).thenReturn(Optional.of(csu));
                // act
                MvcResult response = mockMvc.perform(
                        delete("/api/ucsborganizations?orgCode=csu")
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("csu");
                verify(ucsbOrganizationsRepository, times(1)).delete(any());
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id csu deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organizations_and_gets_right_error_message()
                        throws Exception {
                // arrange
                when(ucsbOrganizationsRepository.findById(eq("jsa"))).thenReturn(Optional.empty());
                // act
                MvcResult response = mockMvc.perform(
                        delete("/api/ucsborganizations?orgCode=jsa")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();
                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("jsa");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id jsa not found", json.get("message"));
        }
}
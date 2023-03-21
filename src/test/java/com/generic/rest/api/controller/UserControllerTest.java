package com.generic.rest.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generic.rest.api.Constants.CONTROLLER;
import com.generic.rest.api.Constants.CONTROLLER.LOGIN;
import com.generic.rest.api.Constants.JWTAUTH;
import com.generic.rest.api.domain.Address;
import com.generic.rest.api.domain.Country;
import com.generic.rest.api.domain.Role;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.service.AddressService;
import com.generic.rest.api.service.CountryService;
import com.generic.rest.api.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SessionControllerTest {

	private static final int TOTAL_USERS = 10;
	private static final int ADMIN_NUMBER = 1;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserService userService;
     
	@Autowired
	private AddressService addressService;

	@Autowired
	private CountryService countryService;
     
	private ObjectMapper objectMapper = new ObjectMapper();
	private HttpHeaders authHeader = new HttpHeaders();
     
	List<User> usersDatabase;
     
	@BeforeEach
	void setup() throws JsonProcessingException, Exception {
		usersDatabase = new ArrayList<>();
		
		String basePassword = "test";
    	 
		for (int i = 1; i <= TOTAL_USERS; i++) {
			Country country = Country.builder()
        			 .name(i == ADMIN_NUMBER ? "Portugal" : "Brasil")
        			 .build();
        	 
        	 Address address = Address.builder()
        			 .street("Street Test " + i)
        			 .state("RJ")
        			 .streetNumber("Street " + i)
        			 .country(country)
        			 .build();
        	 
        	 User user = User.builder()
            		  	.name("User_Test_" + i)
            		  	.password(i + basePassword)
            		  	.email("test" + i + "@test.com")
            		  	.role(i == ADMIN_NUMBER ? Role.ADMIN : Role.USER)
            		  	.address(address)
                        .build();
        	 
        	 usersDatabase.add(userService.save(user));
    	 }
    	 
    	 Map<String, String> authData = new HashMap<>();
    	 authData.put(LOGIN.EMAIL_FIELD, usersDatabase.get(0).getEmail());
    	 authData.put(LOGIN.PASSWORD_FIELD, ADMIN_NUMBER + basePassword);
    	 
    	 MvcResult result = mvc.perform(post(LOGIN.PATH)
     			.contentType(MediaType.APPLICATION_JSON)
 				.content(objectMapper.writeValueAsString(authData)))
       			.andReturn();
    	 
    	 JSONParser parser = new JSONParser(result.getResponse().getContentAsString());
    	 Map<String, Object> authResponse = parser.parseObject();
    	 
    	 StringBuilder token = new StringBuilder(JWTAUTH.BEARER)
    			 .append(" ")
    			 .append(authResponse.get(JWTAUTH.TOKEN).toString());
    	 
    	 authHeader.add(JWTAUTH.AUTHORIZATION, token.toString());
     }

     @Test
     void postUser_CreatedResponse() throws Exception {
    	 Country country = Country.builder()
    			 .name("Brasil")
    			 .build();
    	 
    	 Address address = Address.builder()
    			 .street("Street Test 11")
    			 .state("RJ")
    			 .streetNumber("Street 11")
    			 .country(country)
    			 .build();
    	 
    	 User user = User.builder()
        		  	.name("User_Test_11")
        		  	.password("11test")
        		  	.email("test11@test.com")
        		  	.role(Role.ADMIN)
        		  	.address(address)
                    .build();

    	 mvc.perform(MockMvcRequestBuilders.post(new StringBuilder(CONTROLLER.USER.PATH).toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user))
			.headers(authHeader))
  			.andExpect(status().isCreated());
     }
     
     @Test
     void putUser_SuccessResponse() throws Exception {
    	 String newName = "User_Test_1_edited";
    	 String newState = "Lisboa";
    	 
    	 usersDatabase.get(0).getAddress().setState(newState);
    	 usersDatabase.get(0).setName(newName);
    	 
    	 mvc.perform(MockMvcRequestBuilders.put(new StringBuilder(CONTROLLER.USER.PATH)
       		  .append(CONTROLLER.PATH_SEPARATOR)
       		  .append(usersDatabase.get(0).getExternalId())
       		  .toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(usersDatabase.get(0)))
			.headers(authHeader))
  			.andExpect(status().isOk())
  			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newName))
    	 	.andExpect(MockMvcResultMatchers.jsonPath("$.address.state").value(newState));
     }
     
     @Test
     void getUserByExternalId_NotFound() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append(CONTROLLER.PATH_SEPARATOR)
        		  .append("1234")
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isNotFound());
     }

     @Test
     void getUserByExternalId_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append(CONTROLLER.PATH_SEPARATOR)
        		  .append(usersDatabase.get(0).getExternalId())
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(usersDatabase.get(0).getName()));
     }
     
     @Test
     void deleteUserByExternalId_NotFound() throws Exception {
          mvc.perform(MockMvcRequestBuilders.delete(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append(CONTROLLER.PATH_SEPARATOR)
        		  .append("1234")
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isNotFound());
     }

     @Test
     void deleteUserByExternalId_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.delete(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append(CONTROLLER.PATH_SEPARATOR)
        		  .append(usersDatabase.get(9).getExternalId())
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk());
          
          usersDatabase.remove(9);
     }
     
     @Test
     void getAllUsers_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH).toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0]").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9]").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[10]").doesNotExist());
     }
     
     @Test
     void getAllUsersSingleProjection_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append("?projection=[email]")
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].email").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].email").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[10]").doesNotExist());
     }
     
     @Test
     void getAllUsersNestedSingleProjection_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append("?projection=[address.country.name]")
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].address.country.name").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].address.country.name").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[10]").doesNotExist());
     }
     
     @Test
     void getAllUsersNestedProjections_Ok() throws Exception {
          mvc.perform(MockMvcRequestBuilders.get(new StringBuilder(CONTROLLER.USER.PATH)
        		  .append("?projection=[address.country.name,address.street,address.streetNumber]")
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].address.country.name").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].address.street").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].address.streetNumber").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].address.state").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].address.country.name").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].address.street").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].address.streetNumber").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].address.state").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[9].name").doesNotExist())
               .andExpect(MockMvcResultMatchers.jsonPath("$.records[10]").doesNotExist());
     }
     
     @AfterEach
     void clear() {
    	 for (int i = 0; i < usersDatabase.size(); i++) {
    		 
    		 try {
    			 countryService.delete(usersDatabase.get(i).getAddress().getCountry().getExternalId());
    		 } catch (NotFoundApiException e) {}
    		 
    		 addressService.delete(usersDatabase.get(i).getAddress().getExternalId());
    		 userService.delete(usersDatabase.get(i).getExternalId());
    	 }
     }

}
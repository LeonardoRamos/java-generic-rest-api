package com.generic.rest.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
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
import com.generic.rest.api.service.AddressService;
import com.generic.rest.api.service.CountryService;
import com.generic.rest.api.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SessionControllerTest {

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
     private User userCreated;
     
     @BeforeEach
     void setup() throws JsonProcessingException, Exception {
    	 String password = "1test";
    	 
    	 Country country = Country.builder()
    			 .name("Brasil")
    			 .build();
    	 
    	 Address address = Address.builder()
    			 .street("Street Test")
    			 .state("RJ")
    			 .streetNumber("Street 1")
    			 .country(country)
    			 .build();
    	 
    	 User user = User.builder()
        		  	.name("User_Test_1")
        		  	.password(password)
        		  	.email("test1@test.com")
        		  	.role(Role.ADMIN)
        		  	.address(address)
                    .build();

    	 userCreated = userService.save(user);
    	 
    	 Map<String, String> authData = new HashMap<>();
    	 authData.put(LOGIN.EMAIL_FIELD, user.getEmail());
    	 authData.put(LOGIN.PASSWORD_FIELD, password);
    	 
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
    			 .street("Street Test 2")
    			 .state("RJ")
    			 .streetNumber("Street 2")
    			 .country(country)
    			 .build();
    	 
    	 User user = User.builder()
        		  	.name("User_Test_2")
        		  	.password("2test")
        		  	.email("test2@test.com")
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
    	 String newState = "PI";
    	 
    	 userCreated.getAddress().setState(newState);
    	 userCreated.setName(newName);
    	 
    	 mvc.perform(MockMvcRequestBuilders.put(new StringBuilder(CONTROLLER.USER.PATH)
       		  .append(CONTROLLER.PATH_SEPARATOR)
       		  .append(userCreated.getExternalId())
       		  .toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userCreated))
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
        		  .append(userCreated.getExternalId())
        		  .toString())
    		   .headers(authHeader))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userCreated.getName()));
     }
     
     @AfterEach
     void clear() {
    	 countryService.delete(userCreated.getAddress().getCountry().getExternalId());
    	 addressService.delete(userCreated.getAddress().getExternalId());
    	 userService.delete(userCreated.getExternalId());
     }

}
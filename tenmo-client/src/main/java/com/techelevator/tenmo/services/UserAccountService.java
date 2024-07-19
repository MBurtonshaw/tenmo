package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.security.Principal;

public class UserAccountService {
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;
    private String BASE_URL = "http://localhost:8080/";

    private final

//    private final User user = new User();

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public BigDecimal getBalance(User user) {
        BigDecimal balance = new BigDecimal(0) ;
        try {
            int id = user.getId();

            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(BASE_URL + id +"/balance/" ,
                            HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
    public String whoAmI(Principal principal) {
        String username = principal.getName();
        return username;
    }

    public  User[] listUsers(){
        User[] user = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(BASE_URL + "",
                    HttpMethod.GET, makeAuthEntity(), User[].class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public Transfer transfer(){
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(BASE_URL + "Transfer",
                    HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }


    }



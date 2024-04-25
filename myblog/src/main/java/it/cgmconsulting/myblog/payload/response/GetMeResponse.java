package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GetMeResponse {

    private int id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String bio;

}

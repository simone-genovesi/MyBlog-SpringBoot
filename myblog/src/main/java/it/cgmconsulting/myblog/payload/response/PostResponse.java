package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostResponse {

    private String title;
    private String content;
    private String image;
    private LocalDate publicationDate;
    private short totComments;
    private String author;
}

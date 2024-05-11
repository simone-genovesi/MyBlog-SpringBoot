package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostDetailResponse {

    private int id;
    private String title;
    private String content;
    private String image;
    private LocalDate publicationDate;
    private short totComments = 0;
    private double average; // i voti vanno da 1 a 5, quindi una media a 0 indica che il post non è mai stato votato
    private String author;
    private List<CommentResponse> comments = new ArrayList<>();
    private Set<String> tagNames = new HashSet<>();

    public PostDetailResponse(int id, String title, String content, String image, LocalDate publicationDate,
                              short totComments, double average, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.publicationDate = publicationDate;
        this.totComments = totComments;
        this.average = average;
        this.author = author;
    }
}

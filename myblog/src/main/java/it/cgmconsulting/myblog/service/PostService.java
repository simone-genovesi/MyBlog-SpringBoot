package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.PostResponse;
import it.cgmconsulting.myblog.repository.PostRepository;
import it.cgmconsulting.myblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public String createPost(PostRequest request, UserDetails userDetails){
        Post post = new Post(request.getTitle(), request.getContent(), (User) userDetails);

        postRepository.save(post);
        return "New post having title " + post.getTitle() + " has been created";
    }

    public String editPost(int id, PostRequest request, UserDetails userDetails){
        User user = (User) userDetails;
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ADMIN"::equals);

        if( user.getId() == post.getUserId().getId() || isAdmin ){
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setUpdateAt(LocalDateTime.now());
            postRepository.save(post);

            return "Post edited successfully";
        }
        else return null;
    }

    public String publishPost(int id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
        post.setPublicationDate(LocalDate.now());
        postRepository.save(post);
        return "Post published successfully";
    }

    public String hidePost(int id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
        post.setPublicationDate(null);
        postRepository.save(post);
        return "Post has been hidden successfully";
    }

    public PostResponse getPost(int id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
        User user = userRepository.findById(post.getUserId().getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", id));
        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .totComments(post.getTotComments())
                .author(user.getUsername())
                .publicationDate(post.getPublicationDate())
                .build();
    }

    public List<PostResponse> getAllVisiblePosts(){
        List<Post> dbPosts = postRepository.findByPublicationDateIsNotNullAndPublicationDateBefore(LocalDate.now()
                                                                                                .plusDays(1));
        List<PostResponse> posts = new ArrayList<>();
        for (Post post : dbPosts){
            User user = userRepository.findById(post.getUserId().getId()).orElseThrow(
                    () -> new ResourceNotFoundException("User", "Id", post.getUserId().getId()));
            PostResponse postResponse = PostResponse.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .image(post.getImage())
                    .totComments(post.getTotComments())
                    .author(user.getUsername())
                    .publicationDate(post.getPublicationDate())
                    .build();
            posts.add(postResponse);
        }
        return  posts;

//        VERSIONE CHATGPT
//        Map<Integer, User> userMap = new HashMap<>();
//        List<Post> dbPosts = postRepository.findByPublicationDateIsNotNullAndPublicationDateBefore(LocalDate.now()
//                .plusDays(1));
//        for (Post post : dbPosts) {
//            userMap.put(post.getUserId().getId(), post.getUserId());
//        }
//
//        List<PostResponse> posts = new ArrayList<>();
//        for (Post post : dbPosts) {
//            User user = userMap.get(post.getUserId().getId());
//            if (user == null) {
//                throw new ResourceNotFoundException("User", "Id", post.getUserId().getId());
//            }
//            PostResponse postResponse = PostResponse.builder()
//                    .title(post.getTitle())
//                    .content(post.getContent())
//                    .image(post.getImage())
//                    .totComments(post.getTotComments())
//                    .author(user.getUsername())
//                    .publicationDate(post.getPublicationDate())
//                    .build();
//            posts.add(postResponse);
//        }
//        return posts;
    }
}

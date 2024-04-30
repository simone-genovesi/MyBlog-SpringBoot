package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/v0/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable int id){
        return new ResponseEntity<>(postService.getPost(id), HttpStatus.OK);
    }

    @GetMapping("/v0/posts")
    public ResponseEntity<?> getAllVisiblePosts(){
        return new ResponseEntity<>(postService.getAllVisiblePosts(), HttpStatus.OK);
    }

    @PostMapping("/v1/posts")
    @PreAuthorize("hasAuthority('WRITER')")
    public ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody @Valid PostRequest request){
        return new ResponseEntity<>(postService.createPost(request, userDetails), HttpStatus.CREATED);
    }

    @PutMapping("/v1/posts")
    @PreAuthorize("hasAnyAuthority('WRITER', 'ADMIN')")
    public ResponseEntity<?> editPost(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam @Min(1) int id,
                                      @RequestBody @Valid PostRequest request) {
        String s = postService.editPost(id, request, userDetails);
        if(s != null)
            return new ResponseEntity<>(s, HttpStatus.OK);
        else return new ResponseEntity<>("You cannot edit this post", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/v1/posts/publish")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> publishPost(@RequestParam int id){
        return new ResponseEntity<>(postService.publishPost(id), HttpStatus.OK);
    }

    @PatchMapping("/v1/posts/hide")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> hidePost(@RequestParam int id){
        return new ResponseEntity<>(postService.hidePost(id), HttpStatus.OK);
    }

}

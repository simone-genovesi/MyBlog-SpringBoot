package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.Tag;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.*;
import it.cgmconsulting.myblog.repository.CommentRepository;
import it.cgmconsulting.myblog.repository.PostRepository;
import it.cgmconsulting.myblog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${application.comment.time}")
    private int timeToUpdate;

    @Value("${application.image.post.path}")
    private String path;

    private final PostRepository postRepository;
    private final TagService tagService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public String createPost(PostRequest request, UserDetails userDetails){
        String content = request.getContent();
        String overview = content.length() <= 255 ? content : content.substring(0,251).concat("...");
        Post post = new Post(request.getTitle(), content, (User) userDetails, overview);
        postRepository.save(post);
        return "New post having title " + post.getTitle() + " has been created";
    }

    public String editPost(int id, PostRequest request, UserDetails userDetails){
        User user = (User) userDetails;
        Post post = findPostById(id);
        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ADMIN"::equals);

        if( user.equals(post.getUserId()) || isAdmin ){
            String content = request.getContent();
            String overview = content.length() <= 255 ? content : content.substring(0,251).concat("...");
            post.setTitle(request.getTitle());
            post.setOverview(overview);
            post.setContent(request.getContent());
            post.setUpdatedAt(LocalDateTime.now());
            post.setPublicationDate(null);
            postRepository.save(post);

            return "Post edited successfully";
        }
        else return null;
    }

    @Transactional
    public String publishPost(int id, LocalDate publicationDate){
        Post post = findPostById(id);
        post.setPublicationDate(publicationDate);
        return "Publication post on " + publicationDate;
    }

    public PostDetailResponse getPost(int id){
        PostDetailResponse postDetailResponse = postRepository.getPostById(id, LocalDate.now(), path).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
        Set<String> tagNames = tagService.getTagNamesByPost(id);
        if(!tagNames.isEmpty())
            postDetailResponse.setTagNames(tagNames);
        List<CommentResponse> comments = commentRepository.getComments(id, LocalDateTime.now().minusSeconds(timeToUpdate));
        if(!comments.isEmpty())
            postDetailResponse.setComments(comments);
        return  postDetailResponse;
    }

    public List<PostResponse> getAllVisiblePosts(int pageNumber, int pageSize, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostResponse> posts = postRepository.getVisiblePosts(LocalDate.now(), pageable, path);
        List<PostResponse> postResponses = new ArrayList<>();
        if(posts.hasContent())
            postResponses = posts.getContent();
        return postResponses;
    }

    @Transactional
    public void addUpdateTagsToPost(UserDetails userDetails, int id, Set<String> tagNames){
        User user = (User) userDetails;
        Post post = findPostById(id);
        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ADMIN"::equals);

        if( user.equals(post.getUserId()) || isAdmin ){
           Set<Tag> tags = tagService.findAllByVisibleTrueAndTagNameIn(tagNames);
           post.setTags(tags);
        }
    }

    public Post findPostById(int id){
        return postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id));
    }

    public List<PostResponse> getAllVisiblePostsByTag(int pageNumber, int pageSize,
                                                      String sortBy, String direction, String tag) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostResponse> posts = postRepository.getVisiblePostsByTag(LocalDate.now(), pageable, tag, path);
        List<PostResponse> postResponses = new ArrayList<>();
        if(posts.hasContent())
            postResponses = posts.getContent();
        return postResponses;
    }

    public List<PostResponse> getAllVisiblePostsByAuthor(int pageNumber, int pageSize,
                                                         String sortBy, String direction, String username) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostResponse> posts = postRepository.getVisiblePostsByAuthor(LocalDate.now(), pageable, username, path);
        List<PostResponse> postResponses = new ArrayList<>();
        if(posts.hasContent())
            postResponses = posts.getContent();
        return postResponses;
    }

    public List<PostResponse> getAllVisiblePostsByKeyword(int pageNumber, int pageSize, String sortBy, String direction,
                                                          String keyword, boolean isCaseSensitive, boolean isExactMatch) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostKeywordResponse> posts = postRepository
                .getVisiblePostsByKeyword(LocalDate.now(), pageable, '%'+keyword+'%', path);

        Pattern pattern = null;

        if(!isCaseSensitive && !isExactMatch) {
            pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        } else if (!isCaseSensitive && isExactMatch){
            pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
        } else if (isCaseSensitive && !isExactMatch){
            pattern = Pattern.compile(keyword);
        } else if (isCaseSensitive && isExactMatch){
            pattern = Pattern.compile("\\b" + keyword + "\\b");
        }

        List<PostResponse> postResponses = new ArrayList<>();
        if(posts.hasContent()) {
            for(PostKeywordResponse p : posts.getContent()) {
                if ((pattern.matcher(p.getTitle().concat(" ").concat(p.getContent())).find()))
                    postResponses.add(new PostResponse(
                            p.getId(),
                            p.getTitle(),
                            p.getOverview(),
                            p.getImage(),
                            p.getTotComments(),
                            p.getAverage()
                    ));
            }
        }
        return postResponses;
    }

    @Transactional
    public String addRemoveBookmark(UserDetails userDetails, int postId) {

        User user = (User) userDetails;
        Post post = findPostById(postId);
        User u = userRepository.getUserWithPreferredPost(user.getId());
        String msg = "Post has beed added to preferred";
        if(u.getPreferredPosts().contains(post)){
            u.getPreferredPosts().remove(post);
            msg = "Post has been removed from preferred";

        }else {
            u.getPreferredPosts().add(post);
        }

        return msg;
    }

    public List<BookmarkResponse> getBookmarks(UserDetails userDetails) {
        User user = (User) userDetails;
        User u = userRepository.getUserWithPreferredPost(user.getId());
        List<BookmarkResponse> bookmarks = new ArrayList<>();
        for(Post p : u.getPreferredPosts()){
            if(p.getPublicationDate() != null && p.getPublicationDate().isBefore(LocalDate.now()))
                bookmarks.add(new BookmarkResponse(p.getId(), p.getTitle()));
            }
        return bookmarks;
    }

    public List<Post> getLastWeekPosts() {
        return postRepository.findByPublicationDateAfter(LocalDate.now().minusWeeks(1));
    }

    public List<Post> getLastMonthPosts() {
        return postRepository.findByPublicationDateAfter(LocalDate.now().minusMonths(1));
    }
}

package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Tag;
import it.cgmconsulting.myblog.service.TagService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class TagController {

    public final TagService tagService;

    @GetMapping("/v0/tags")
    public ResponseEntity<?> getAllVisibleTags(){
        List<Tag> tags = tagService.getAllVisibleTags();
        if(tags.isEmpty()){
            return new ResponseEntity<>(tags + " No tags found ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    // N = only not visibile
    // A = all
    @GetMapping("/v1/tags")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllTags(@RequestParam(defaultValue = "A") Character visible){
        List<Tag> tags = tagService.getAllTags(visible);
        if(tags.isEmpty()){
            return new ResponseEntity<>(tags + " No tags found ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/v0/tags/{id}")
    public ResponseEntity<?> getTag(@PathVariable @Min(1) @Max(32767) short id){
        return new ResponseEntity<>(tagService.getTag(id),HttpStatus.OK);
    }

    @PostMapping("/v1/tags")
    @PreAuthorize("hasAuthority('ADMIN')")
    public  ResponseEntity<?> createTag(
            @RequestParam
            @NotBlank(message = "The tag name contain at least one non whitespace character!")
            @Size(min = 4, max = 50) String tagName){
        Tag tag = tagService.createTag(tagName.toUpperCase().trim());
        if (tag != null){
            return new ResponseEntity<>(tag, HttpStatus.CREATED);
        }
        return  new ResponseEntity<>("The tag witch name " + tagName + " is alredy present", HttpStatus.CONFLICT);
    }

    @PutMapping("/v1/tags")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateTag(
            @RequestParam @NotBlank @Size(min = 4, max = 50) String tagName,
            @RequestParam @NotBlank @Size(min = 4, max = 50) String newTagName,
            @RequestParam boolean visible){
        Tag tag = tagService.updateTag(
                tagName.toUpperCase().trim(),
                newTagName.toUpperCase().trim(),
                visible);
        if (tag != null){
            return new ResponseEntity<>(tag, HttpStatus.CREATED);
        }
        return  new ResponseEntity<>("The tag witch name " + newTagName + " is alredy present", HttpStatus.CONFLICT);

    }

}
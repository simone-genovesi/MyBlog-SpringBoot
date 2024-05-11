package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Tag;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j  // fornisce un log
public class TagService {

    private final TagRepository tagRepository; // iniezione avventura con @RequiredArgsConstructor

    // lista di tag completa (visibili e no)
    public List<Tag> getAllTags(char visible){
        List<Tag> tags = new ArrayList<>();
        if(visible == 'A' || visible == 'a'){
            tags = tagRepository.findAllByOrderByTagName();
        } else if (visible == 'N' || visible == 'n') {
            tags = tagRepository.findByVisibleFalseOrderByTagName();
        }
        log.info("Tag list contains " + tags.size() + " elements");
        return tags;
    }

    //cercare uno specifico tag
    public Tag getTag(short id){
        return tagRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Tag", "Id", id));
    }

    //inserire un nuovo tag
    public Tag createTag(String tagName){
        //controllo che il valore già non esisti
        if (tagRepository.existsByTagName(tagName)){
            return null;
        }
        return tagRepository.save(new Tag(tagName));
    }

    //modificare un tag esistente
    @Transactional
    public Tag updateTag(String tagName, String newTagName, boolean visible){
        // trovo tag da modificare
        Tag tag = tagRepository.findByTagName(tagName).
                orElseThrow(() -> new ResourceNotFoundException("Tag", "tagName", tagName));
        // verifico che non esista un altro record che abbia lo stesso tang name di quello nuovo
        if(tagRepository.existsByTagNameAndIdNot(newTagName, tag.getId())){
            return null;
        }
        tag.setTagName(newTagName);
        tag.setVisible(visible);
        // con @Transactional si occupa lui di salvare solo se tutte le operazioni vanno a buon fine
        // in caso contrario tutte le operazioni vengono annullate
        // funziona solo su elementi già esistenti
        return tag;
    }

    public List<Tag> getAllVisibleTags(){
        return  tagRepository.findByVisibleTrueOrderByTagName();
    }

    public Set<String> getTagNamesByPost(int postId){
        return tagRepository.getTagNamesByPost(postId);
    }

    public Set<Tag> findAllByVisibleTrueAndTagNameIn(Set<String> tagNames){
        return tagRepository.findAllByVisibleTrueAndTagNameIn(tagNames);
    }
}

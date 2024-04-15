package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Avatar {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private AvatarId avatarId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, length = 10)
    private String filetype;

    @Lob
    @Column(nullable = false, columnDefinition = "BLOB") // Blob -> Binary Long OBject
    private byte[] data;
}
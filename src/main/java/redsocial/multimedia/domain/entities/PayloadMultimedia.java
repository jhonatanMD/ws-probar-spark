package redsocial.multimedia.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadMultimedia {
    private String multimediaId;
    private String connection;
}
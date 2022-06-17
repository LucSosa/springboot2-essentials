package academy.devdojo.springboot2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Anime {
    private Long id;
    private String name;
    /*
     *
     * Caso o atributo for diferente pode ser realizado conforme o exemplo abaixo
     * @JsonProperty("name")
     * private String nameCharacter;
     *
     */
}

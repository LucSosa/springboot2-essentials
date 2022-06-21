package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@DisplayName("Test for Anime Controller")
@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;
    @Mock
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidUpdateAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("list returns list of anime inside page object when sucessful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of anime when sucessful")
    void listAll_ReturnsListOfAnime_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.listAll().getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findbyId returns anime when sucessful")
    void findById_ReturnsAnime_WhenSucessful() {
        Long expectedName = AnimeCreator.createValidAnime().getId();

        Anime anime = animeController.findById(1).getBody();

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns list of anime when sucessful")
    void findByName_ReturnsListOfAnime_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when sucessful")
    void save_ReturnsAnime_WhenSucessful() {
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace returns anime when sucessful")
    void replace_UpdateAnime_WhenSucessful() {
        Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

        Assertions.assertThat(entity).isNotNull();

        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes returns anime when sucessful")
    void delete_RemovesAnime_WhenSucessful() {
        Assertions.assertThatCode(() -> animeController.delete(1))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.delete(1);

        Assertions.assertThat(entity).isNotNull();

        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
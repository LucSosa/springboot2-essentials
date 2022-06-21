package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DisplayName("Test for Anime Service")
@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;
    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("listAll returns list of anime inside page object when sucessful")
    void listAll_ReturnsListOfAnimeInsidePageObject_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeService.listAll(PageRequest.of(1,1));

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAllNonPageable returns list of anime when sucessful")
    void listAllNonPageable_ReturnsListOfAnime_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeService.listAllNonPageable();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when sucessful")
    void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSucessful() {
        Long expectedName = AnimeCreator.createValidAnime().getId();

        Anime anime = animeService.findByIdOrThrowBadRequestException(1);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException returns anime is not found")
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1));
    }

    @Test
    @DisplayName("findByName returns list of anime when sucessful")
    void findByName_ReturnsListOfAnime_WhenSucessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeService.findByName("anime");

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animeService.findByName("anime");

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when sucessful")
    void save_ReturnsAnime_WhenSucessful() {
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace returns anime when sucessful")
    void replace_UpdateAnime_WhenSucessful() {
        Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete removes returns anime when sucessful")
    void delete_RemovesAnime_WhenSucessful() {
        Assertions.assertThatCode(() -> animeService.delete(1))
                .doesNotThrowAnyException();
    }
}
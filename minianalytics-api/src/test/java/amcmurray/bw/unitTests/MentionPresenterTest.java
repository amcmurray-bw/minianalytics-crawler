package amcmurray.bw.unitTests;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import amcmurray.bw.MentionPresenter;
import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionDTO;
import amcmurray.bw.twitterdomainobjects.MentionType;

@RunWith(SpringRunner.class)
public class MentionPresenterTest {

    private MentionPresenter mentionPresenter = new MentionPresenter();

    private final Date testDate = Date.from(Instant.now());

    private final String expectedId = "123";
    private final int expectedQueryId = 456;
    private final MentionType expectedMentionType = MentionType.TWITTER;
    private final String expectedAuthor = "testAuthor";
    private final String expectedText = "mocktext";
    private final String expectedTestDate = ZonedDateTime.ofInstant(
            testDate.toInstant(), ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss z Z"));
    private final String expectedLanguage = "en";
    private final int expectedFavourites = 0;


    private final Mention mention = new Mention(
            expectedId, expectedQueryId, expectedMentionType,
            expectedAuthor, expectedText, testDate,
            expectedLanguage, expectedFavourites);

    private final MentionDTO mentionDto = new MentionDTO(
            expectedId, expectedQueryId, expectedMentionType,
            expectedAuthor, expectedText, expectedTestDate,
            expectedLanguage, expectedFavourites);

    private final List<Mention> listOfMentions = Arrays.asList(mention,
            mention, mention, mention, mention);
    private final List<MentionDTO> expectedListOfDTOs = Arrays.asList(mentionDto,
            mentionDto, mentionDto, mentionDto, mentionDto);

    @Test
    public void toDTOs_convertsMentionObjectToDTO() {

        List<MentionDTO> listOfMentionsToDTOs = mentionPresenter.toDTOs(listOfMentions);

        assertEquals(expectedListOfDTOs, listOfMentionsToDTOs);
    }

}

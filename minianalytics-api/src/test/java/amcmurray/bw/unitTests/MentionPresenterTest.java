package amcmurray.bw.unitTests;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
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

    private Mention mention = new Mention(
            "123", 456, MentionType.TWITTER,
            "mocktext", testDate,
            "en", 0);

    private MentionDTO mentionDto = new MentionDTO(
            "123", 456, MentionType.TWITTER,
            "mocktext", ZonedDateTime.ofInstant(
            testDate.toInstant(), ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("dd:MM:YYYY HH:mm:ss z Z")),
            "en", 0);

    private final List<Mention> listOfMentions = Arrays.asList(mention, mention, mention, mention, mention);
    private final List<MentionDTO> expectedListOfDTOs = Arrays.asList(mentionDto, mentionDto, mentionDto, mentionDto, mentionDto);

    @Test
    public void toDTOs_convertsMentionObjectToDTO() {

        List<MentionDTO> listOfMentionsToDTOs = mentionPresenter.toDTOs(listOfMentions);

        Assert.assertEquals(expectedListOfDTOs, listOfMentionsToDTOs);
    }

    @Test
    public void toDTOs_propertiesAreAsExpected() {

        List<MentionDTO> listOfMentionsToDTOs = mentionPresenter.toDTOs(listOfMentions);

        MentionDTO mentionDT0 = listOfMentionsToDTOs.get(0);

        assert mention.getId().equals(mentionDT0.getId());
        assert mention.getQueryId() == (mentionDT0.getQueryId());
        assert mention.getMentionType().equals(mentionDT0.getMentionType());
        assert mention.getText().equals(mentionDT0.getText());
        assert mention.getLanguageCode().equals(mentionDT0.getLanguageCode());
        assert mention.getFavouriteCount() == (mentionDT0.getFavouriteCount());

        assert ZonedDateTime.ofInstant(
                mention.getCreatedAt().toInstant(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("dd:MM:YYYY HH:mm:ss z Z"))
                .equals(mentionDT0.getDateCreated());
    }
}

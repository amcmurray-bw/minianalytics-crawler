package amcmurray.bw;

import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import amcmurray.bw.twitterdomainobjects.Mention;
import amcmurray.bw.twitterdomainobjects.MentionDTO;
import amcmurray.bw.twitterdomainobjects.MentionType;

@RunWith(MockitoJUnitRunner.class)
public class MentionPresenterTest {

    @Mock
    private MentionPresenter mentionPresenter;

    private Mention mention = new Mention(
            "123", 456, MentionType.TWITTER,
            "mocktext", Date.from(Instant.now()),
            "en", 0);

    private final List<Mention> allMentions = Arrays.asList(mention, mention, mention, mention, mention);


    @Test
    public void toDTOs_convertsMentionObjectToDTO() {

        when(mentionPresenter.toDTOs(allMentions)).thenCallRealMethod();

        List<MentionDTO> mentionDTOList = mentionPresenter.toDTOs(allMentions);

        Assert.assertNotNull(mentionDTOList);
        Assert.assertEquals(MentionDTO.class, mentionDTOList.get(0).getClass());

    }

    @Test
    public void toDTOs_propertiesAreAsExpected() {

        when(mentionPresenter.toDTOs(allMentions)).thenCallRealMethod();

        List<MentionDTO> mentionDTOList = mentionPresenter.toDTOs(allMentions);

        MentionDTO mentionDT0 = mentionDTOList.get(0);

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

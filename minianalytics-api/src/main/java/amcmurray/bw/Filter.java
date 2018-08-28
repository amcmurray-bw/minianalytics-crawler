package amcmurray.bw;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class Filter {

    private String languageCode;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:SS")
    private Date startDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:SS")
    private Date endDate;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

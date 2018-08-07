package ru.rsmu.facadeEispo.actions;

import org.springframework.util.StringUtils;

import java.util.Calendar;

/**
 * @author leonid.
 */
public class SearchForm {
    private String lastName = "";
    private String caseNumber = "";
    private String snilsNumber = "";

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getSnilsNumber() {
        return snilsNumber;
    }

    public void setSnilsNumber( String snilsNumber ) {
        this.snilsNumber = snilsNumber;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty( lastName ) && StringUtils.isEmpty( caseNumber ) && StringUtils.isEmpty( snilsNumber );
    }

    public boolean isEmptyName() {
        return StringUtils.isEmpty( lastName );
    }

    public boolean isEmptyCase() {
        return StringUtils.isEmpty( caseNumber ) || ! caseNumber.matches( "\\d+" );
    }

    public boolean isEmptySnils() {
        return StringUtils.isEmpty( snilsNumber ) || !snilsNumber.matches( "\\d+" );
    }

    public Long getCase() {
        if ( !isEmptyCase() ) {
            try {
                Long shortNumber = Long.parseLong( caseNumber );
                Calendar date = Calendar.getInstance();
                return shortNumber + date.get( Calendar.YEAR ) * 100000L;
            } catch (NumberFormatException e) {
                //
            }
        }
        return 0L;
    }

    public Long getSnils() {
        if ( !isEmptySnils() ) {
            try {
                return Long.parseLong( snilsNumber );
            } catch (NumberFormatException e) {
                //
            }
        }
        return 0L;
    }
}

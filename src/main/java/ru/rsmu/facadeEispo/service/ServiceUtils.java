package ru.rsmu.facadeEispo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leonid.
 */
public class ServiceUtils {
    protected static Logger logger = LoggerFactory.getLogger( ServiceUtils.class );

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat( "yyyy" );

    public static Long parseSnils( String value ) {
        Long snils = null;
        try {
            snils = Long.decode( value.replaceAll( "^0+", "" ) );
        } catch (NumberFormatException e) {
            logger.error( String.format( "Wrong snils: %s", value ));
        }
        return snils;
    }

    public static Date parseDate( String value ) {
        Date birthDate;
        try {
            birthDate = DATE_FORMAT.parse( value );
        } catch (ParseException e) {
            birthDate = null;
        }
        return birthDate;
    }

}

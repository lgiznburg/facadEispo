package ru.rsmu.facadeEispo.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.facadeEispo.dao.OidDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.oid.OrganizationInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static  String getCellValue( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            String value;
            switch ( cell.getCellType() ) {
                case STRING:
                    value = cell.getRichStringCellValue().getString().trim();
                    break;
                case NUMERIC:
                    double cellDouble = Math.abs( cell.getNumericCellValue() );
                    double floorDouble = Math.floor( cellDouble );
                    if ( cellDouble == floorDouble ) {
                        value = Long.toString( Math.round( cell.getNumericCellValue() ) );
                    }
                    else {
                        value = Double.toString( cell.getNumericCellValue() );
                    }
                    break;
                case FORMULA:
                    try {
                        value = cell.getRichStringCellValue().toString().trim();
                    } catch (Exception e) { // if formula does not return string we get exception
                        try {               // lets try number
                            value = Double.toString( cell.getNumericCellValue() );
                        } catch (Exception e1) {
                            value = "";
                        }
                    }
                default:
                    return null;
            }
            return value;
        }
        return null;
    }

    public static  Long getCellNumber( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            Long value = null;
            switch ( cell.getCellType()  ) {
                case NUMERIC:
                    value = Math.round( cell.getNumericCellValue() );
                    break;
                case STRING:
                    String strValue = cell.getRichStringCellValue().getString().trim();
                    if ( strValue.matches( "\\d+" ) ) {
                        value = Long.parseLong( strValue );
                    }
            }
            return value;
        }
        return null;
    }

    public static String getEntrantInfo( Entrant entrant, OidDao oidDao, boolean shortName ) {
        OrganizationInfo orgForUs = oidDao.getOrgInfo( entrant.getExamInfo().getOrganization() );

        String readableType = entrant.getExamInfo().getType().replaceAll( "ординатура", "тестирование (ординатура)" );
        readableType = readableType.replaceAll( "аккредитация", "1-й этап аккредитации" );

        StringBuilder result = new StringBuilder();
        result.append( "Тип: " ).append( readableType )
                .append( ", Организация: " ).append( shortName ? orgForUs.getShortName() : orgForUs.getFullName() )
                .append( ", Год: " ).append( entrant.getExamInfo().getYear() );
        return result.toString();
    }

    public static String getReadableErrorInfo( String errorInfo, OidDao oidDao, boolean shortName ) {
        String response = errorInfo;
        Pattern pattern = Pattern.compile( "(\\d+\\.)+\\d+" );
        Matcher matcher = pattern.matcher( response );
        while ( matcher.find() ) {
            OrganizationInfo orgForThem = oidDao.getOrgInfo( matcher.group() );
            if ( orgForThem != null ) {
                response = response.replaceAll( matcher.group(), "\"" + (shortName ? orgForThem.getShortName() : orgForThem.getFullName()) + "\"" );
            }
        }
        response = response.replaceAll( "ординатура", "тестирование (ординатура)" );
        response = response.replaceAll( "аккредитация", "1-й этап аккредитации" );
        return response;
    }
}

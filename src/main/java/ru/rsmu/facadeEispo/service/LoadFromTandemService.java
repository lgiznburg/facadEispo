package ru.rsmu.facadeEispo.service;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
@Component
public class LoadFromTandemService implements ExcelLayout {
    protected Logger logger = LoggerFactory.getLogger( LoadFromTandemService.class );

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );
    protected static final DateFormat YEAR_FORMAT = new SimpleDateFormat( "yyyy" );
    protected static final Locale RU_LOCALE = Locale.forLanguageTag( "ru_RU" );

    protected static final Pattern achievementsPattern = Pattern.compile( "\\d\\. ([^ ]+) . (\\d{1,3})" );
    protected static final Pattern specialityPattern = Pattern.compile( "\\d{2}\\.\\d{2}\\.\\d{2}" );

    @Autowired
    private EntrantDao entrantDao;

    public void readFromFile( InputStream file, boolean loginOnly ) throws IOException {


        //List<String> resultLog = new ArrayList<>();

        List<Entrant> entrants = new LinkedList<>();

        POIFSFileSystem fs = new POIFSFileSystem( file );
        HSSFWorkbook wb = new HSSFWorkbook( fs );

        if ( !loginOnly ) {
            HSSFSheet sheet = wb.getSheetAt( 0 );  // main page

            loadCommonInfo( sheet, entrants );

            sheet = wb.getSheetAt( 1 );  // requests page
            loadRequests( sheet, entrants, false );

/*
            sheet = wb.getSheetAt( 3 );   // score request  page
            loadScoreRequests( sheet, entrants );

            sheet = wb.getSheetAt( 2 );   // login request  page
            loadLoginRequests( sheet, entrants );
*/
            sheet = wb.getSheetAt( 2 );   // foreign request  page
            loadRequests( sheet, entrants, true );

        } else {

            HSSFSheet sheet = wb.getSheetAt( 0 );  // login request page
            loadDiplomaDate( sheet, entrants );
        }

        saveAllEntities( entrants );
    }

    private void loadScoreRequests( HSSFSheet sheet, List<Entrant> entrants ) {
        int rowN = 1;  // skip header

        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }

            Long snils = parseSnils( getCellValue( row, SNILS ) );
            Entrant entrant = findBySnils( entrants, snils );
            if ( entrant != null ) {
                if ( entrant.getExamInfo() != null ) {
                    entrant.getExamInfo().setSpeciality( getCellValue( row, SC_SPECIALITY ) );
                }
            } else {
                logger.error( "Can't find entrant by snils" );
            }

            rowN++;
        } while ( true );
    }

    private Entrant findBySnils( List<Entrant> entrants, Long snils ) {
        for ( Entrant entrant : entrants ) {
            if ( entrant.getSnilsNumber() != null && entrant.getSnilsNumber().equals( snils ) ) {
                return entrant;
            }
        }
        return null;
    }

    private void loadRequests( HSSFSheet sheet, List<Entrant> entrants, boolean foreigners ) {
        int rowN = 1;  // skip header

        List<Request> requests = new ArrayList<>();
        Entrant current = null;

        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }
            Long caseNumber = getCellNumber( row, R_CASE_NUMBER );

            String firstName = getCellValue( row, R_FIRST_NAME );
            String middleName = getCellValue( row, R_MIDDLE_NAME );
            String lastName = getCellValue( row, R_LAST_NAME );
            Date birthDate;
            try {
                birthDate = DATE_FORMAT.parse( getCellValue( row, R_BIRTH_DATE ) );
            } catch (ParseException e) {
                birthDate = null;
            }
            Entrant entrant = findByCaseNumber( entrants, caseNumber );
            if ( entrant == null ) {
                entrant = findByName( entrants, firstName, middleName, lastName, birthDate );
            }
            if ( entrant != null ) {
                if ( current == null ) {  // new one
                    current = entrant;
                    if ( current.getId() > 0 ) {
                        requests.addAll( current.getRequests() );
                    }
                }
                if ( current != entrant ) {  // old one and switching to new one
                    if ( current.getId() > 0 && requests.size() > 0 ) {
                        finalizeRequests( requests );
                        requests.clear();
                    }
                    current = entrant;
                    if ( current.getId() > 0 ) {
                        requests.addAll( current.getRequests() );
                    }
                }
                Long snils = parseSnils( getCellValue( row, SNILS ) );
                if ( snils == null || foreigners) {
                    if ( entrant.getCitizenship() == null || StringUtils.isEmpty( entrant.getCitizenship() ) ) {
                        entrant.setCitizenship( getCellValue( row, R_CITIZENSHIP ) );
                    }
                    entrant.setStatus( EntrantStatus.FOREIGNER );
                }
                else if ( entrant.getSnilsNumber() == null ) {
                    // not yet initialized or updated
                    entrant.setSnilsNumber( snils );
                    entrant.setCitizenship( getCellValue( row, R_CITIZENSHIP ) );
                    if ( entrant.getCitizenship().length() < 3 ) {
                        entrant.setCitizenship( "0" + entrant.getCitizenship() );
                    }
                    if ( entrant.getStatus() ==  EntrantStatus.FOREIGNER ) {
                        entrant.setStatus( EntrantStatus.UPDATED );
                    }

                } else {
                    if ( !snils.equals( entrant.getSnilsNumber() ) ) {
                        entrant.setSnilsNumber( snils );
                        if ( entrant.getStatus() == EntrantStatus.SUBMITTED) {
                            // если он передан, вероятно его надо отозвать и переподать
                            entrant.setStatus( EntrantStatus.UPDATED );
                        }
                    }
                }
                entrant.setBirthDate( birthDate );

                ExamInfo examInfo = new ExamInfo( entrant );
                examInfo.setOrganization( getCellValue( row, R_EXAM_ORG ) );
                examInfo.setType( getCellValue( row, R_EXAM_TYPE ) );
                examInfo.setYear( getCellValue( row, R_EXAM_YEAR ) );
                entrant.getExamInfo().setSpeciality( getCellValue( row, R_BASE_SPECIALITY ) );
                Date scheduledDate;
                try {
                    scheduledDate = DATE_FORMAT.parse( getCellValue( row, R_EXAM_DATE ) );
                } catch (ParseException e) {
                    scheduledDate = null;
                }
                entrant.getExamInfo().setScheduledDate( scheduledDate );
                entrant.getExamInfo().setAchievements( parseAchievements( getCellValue( row, R_ACHIEVEMENTS ) ) );

                if ( !entrant.getExamInfo().equalsByName( examInfo ) ) {
                    entrant.getExamInfo().update( examInfo );
                    entrant.getExamInfo().setScore( 0 );
                    entrant.getExamInfo().setResponse( "" );
                    if ( entrant.getStatus() == EntrantStatus.SUBMITTED ) {
                        entrant.setStatus( EntrantStatus.UPDATED );
                        entrant.getRequests().forEach( re -> {
                            if ( re.getStatus() == RequestStatus.CONFIRMED ) {
                                re.setStatus( RequestStatus.REFRESHING );
                            }
                            else if ( re.getStatus() == RequestStatus.REJECTED ) {
                                re.setStatus( RequestStatus.NEW );
                            }
                        } );
                    }
                }

                Request request = new Request( entrant );
                request.setFinancing( getCellValue( row, R_FINANCING ) );
                request.setTargetRequest( getCellValue( row, R_TARGET ) );
                request.setSpeciality( getCellValue( row, R_SPECIALITY ) );
                Date applicationDate;
                try {
                    applicationDate = DATE_FORMAT.parse( getCellValue( row, R_APPLICATION_DATE ) );
                } catch (ParseException e) {
                    applicationDate = null;
                }
                request.setApplicationDate( applicationDate );

                Request existedRequest = entrant.findRequestByName( request );
                if ( existedRequest == null ) {
                    entrant.getRequests().add( request );
                    if ( entrant.getStatus() == EntrantStatus.SUBMITTED ) {
                        entrant.setStatus( EntrantStatus.UPDATED );
                    }
                }
                else if ( requests.size() > 0 ) {  //existed entrant. remove updated request from the list
                    String requestState = getCellValue( row, R_REQUEST_STATUS );
                    if ( "0".equals( requestState ) ) {  // забор документов
                       switch ( existedRequest.getStatus() ) {
                           case CONFIRMED:
                           case REFRESHING:
                               existedRequest.setStatus( RequestStatus.RETIRED );
                               entrant.setStatus( EntrantStatus.UPDATED );
                               break;
                           case NEW:
                           case REJECTED:
                               existedRequest.setStatus( RequestStatus.TERMINATED );
                               break;
                       }
                    }
                        /*    && existedRequest.getStatus() == RequestStatus.CONFIRMED ) {
                    }*/
                    requests.removeIf( r2 -> existedRequest.getId() == r2.getId() );
                }

            } else {
                logger.error( "Can't match request to entrant" );
            }

            rowN++;
        } while ( true );
        if ( current != null && current.getId() > 0 && requests.size() > 0 ) {
            finalizeRequests( requests );
            requests.clear();
        }
    }

    private void finalizeRequests( List<Request> requests ) {
        for ( Request request : requests ) {
            if ( request.getStatus() != null ) {
                switch ( request.getStatus() ) {
                    case CONFIRMED:
                    case REFRESHING:
                        request.setStatus( RequestStatus.RETIRED );
                        break;
                    case REJECTED:
                    case NEW:
                        request.setStatus( RequestStatus.TERMINATED );
                        break;
                }
            }
        }
        entrantDao.saveAllEntities( requests );
        Entrant entrant = entrantDao.findEntity( Entrant.class, requests.get( 0 ).getEntrant().getId() );
        boolean updated = false;
        for ( Request request : entrant.getRequests() ) {
            if ( request.getStatus() != RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.TERMINATED ) {
                updated = true;
                break;
            }
        }
        EntrantStatus status = entrant.getStatus();
        if ( status == EntrantStatus.SUBMITTED && updated ) {
            entrant.setStatus( EntrantStatus.UPDATED );
            entrantDao.saveEntity( entrant );
        }
    }

    private Entrant findByName( List<Entrant> entrants, String firstName, String middleName, String lastName, Date birthDate ) {
        for ( Entrant entrant : entrants ) {
            if ( (firstName != null && firstName.equalsIgnoreCase( entrant.getFirstName() )) &&
                    (middleName != null && middleName.equalsIgnoreCase( entrant.getMiddleName() )) &&
                    (lastName != null && lastName.equalsIgnoreCase( entrant.getLastName() )) &&
                    ( birthDate != null && birthDate.equals( entrant.getBirthDate() )) ) {
                return entrant;
            }

        }
        return null;
    }

    private Entrant findByCaseNumber( List<Entrant> entrants, Long caseNumber ) {
        for ( Entrant entrant : entrants ) {
            if ( entrant.getCaseNumber().equals( caseNumber ) ) {
                return entrant;
            }
        }
        return null;
    }

    private void loadCommonInfo( HSSFSheet sheet, List<Entrant> entrants ) {

        Set<Long> cases = new HashSet<>();

        int rowN = 1;  // skip header
        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }

            Long caseNumber = getCellNumber( row, CASE_NUMBER );
            if ( cases.contains( caseNumber ) ) {
                rowN++;
                continue;
            }
            cases.add( caseNumber );

            Entrant entrant = entrantDao.findEntrantByCaseNumber( caseNumber );
            if ( entrant == null ) {
                entrant = new Entrant( caseNumber );
            }
            entrant.setEmail( getCellValue( row, EMAIL ) );
            entrant.setPhone( getCellValue( row, PHONE ) );

            String firstName = getCellValue( row, FIRST_NAME );
            String middleName = getCellValue( row, MIDDLE_NAME );
            String lastName = getCellValue( row, LAST_NAME );
            Date diplomaDate = ServiceUtils.parseDate( getCellValue( row, DIPLOMA_DATE ) );
            if ( entrant.getStatus() == EntrantStatus.SUBMITTED ) {
                if ( (firstName != null && !firstName.equalsIgnoreCase( entrant.getFirstName() )) ||
                        (middleName != null && !middleName.equalsIgnoreCase( entrant.getMiddleName() )) ||
                        (lastName != null && !lastName.equalsIgnoreCase( entrant.getLastName() )) /*||
                        ( birthDate != null && !birthDate.equals( entrant.getBirthDate() ))*/ ) {
                    entrant.setStatus( EntrantStatus.UPDATED );
//                    if ( entrant.getDeception() != null ) {
//                        entrantDao.deleteEntity( entrant.getDeception() );
//                        entrant.setDeception( null );
//                    }
                }
            }
            if ( firstName != null )  entrant.setFirstName( firstName );
            if ( middleName != null ) entrant.setMiddleName( middleName );
            if ( lastName != null ) entrant.setLastName( lastName );
            //if ( birthDate != null ) entrant.setBirthDate( birthDate );
            //entrantDao.saveEntity( entrant );
            entrant.setDiplomaIssueDate( diplomaDate );

            entrants.add( entrant );

            rowN++;
        } while ( true );
    }

    private void loadDiplomaDate( HSSFSheet sheet, List<Entrant> entrants ) {

        Set<Long> cases = new HashSet<>();

        int rowN = 1;  // skip header
        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }

            Long caseNumber = getCellNumber( row, CASE_NUMBER );
            if ( cases.contains( caseNumber ) ) {
                rowN++;
                continue;
            }
            cases.add( caseNumber );

            Entrant entrant = entrantDao.findEntrantByCaseNumber( caseNumber );
            if ( entrant == null ) {
                rowN++;
                continue;
            }
            Date diplomaDate = ServiceUtils.parseDate( getCellValue( row, DIPLOMA_DATE ) );
            entrant.setDiplomaIssueDate( diplomaDate );

            entrants.add( entrant );

            rowN++;
        } while ( true );
    }

    private String getCellValue( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            String value;
            switch ( cell.getCellType() ) {
                case STRING:
                    value = cell.getRichStringCellValue().getString().trim();
                    break;
                case NUMERIC:
                    value = Long.toString( Math.round( cell.getNumericCellValue() ) );
                    break;
                default:
                    return null;
            }
            return value;
        }
        return null;
    }

    private Long getCellNumber( Row row, short cellN ) {
        Cell cell = row.getCell( cellN );
        if ( cell != null ) {
            Long value;
            switch ( cell.getCellType() ) {
                case NUMERIC:
                    value = Math.round( cell.getNumericCellValue() );
                    break;
                default:
                    return null;
            }
            return value;
        }
        return null;
    }

    private void saveAllEntities( List<Entrant> entrants ) {
        for ( Entrant entrant : entrants ) {
            entrantDao.saveEntity( entrant.getExamInfo() );
            entrantDao.saveEntity( entrant );
            entrantDao.saveAllEntities( entrant.getRequests() );
        }
    }

    private Long parseSnils( String value ) {
        Long snils = null;
        try {
            if ( value != null ) {
                snils = Long.decode( value.replaceAll( "^0+", "" ) );
            }
        } catch (NumberFormatException e) {
            logger.error( "Wrong snils" );
        }
        return snils;
    }

    private void loadLoginRequests( HSSFSheet sheet, List<Entrant> entrants ) {
        loadLoginRequests( sheet, entrants, false );
    }

    private void loadLoginRequests( HSSFSheet sheet, List<Entrant> entrants, boolean loadFormDB ) {
        int rowN = 1;  // skip header

        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }

            Long snils = parseSnils( getCellValue( row, L_SNILS ) );
            Entrant entrant = findBySnils( entrants, snils );
            if ( loadFormDB ) {
                entrant = entrantDao.findEntrantBySnilsNumber( snils );
            }
            if ( entrant != null && entrant.getExamInfo() != null ) {
                if ( loadFormDB ) {
                    entrants.add( entrant );
                }
                Date scheduledDate;
                try {
                    scheduledDate = DATE_FORMAT.parse( getCellValue( row, L_SCHEDULED ) );
                } catch (ParseException e) {
                    scheduledDate = null;
                }
                entrant.getExamInfo().setScheduledDate( scheduledDate );
            } else {
                logger.error( "Can't find entrant by snils" );
            }

            rowN++;
        } while ( true );
    }


    public void loadScoresAndAchievements( InputStream file ) throws IOException {

        List<Entrant> entrants = entrantDao.findAllEntities( Entrant.class );
        Map<Long, Entrant> entrantMap = new HashMap<>();
        for ( Entrant entrant : entrants ) {
            entrantMap.put( entrant.getCaseNumber(), entrant );
        }

        POIFSFileSystem fs = new POIFSFileSystem( file );
        HSSFWorkbook wb = new HSSFWorkbook( fs );

        HSSFSheet sheet = wb.getSheetAt( 0 );  // main page

        Iterator<Row> rowIt = sheet.rowIterator();

        while ( rowIt.hasNext() ){
            Row row = rowIt.next();
            // check if row is valid
            if ( row.getCell( A_CASE ) == null ) {
                continue;
            }

            Long caseNumb = parseSnils( getCellValue( row, A_CASE ) );  //parse long value
            Entrant entrant = entrantMap.get( caseNumb );
            if ( entrant != null && entrant.getExamInfo() != null ) {
                Long score = getCellNumber( row, A_SCORE );
                entrant.getExamInfo().setTotalScore( score != null ? score.intValue() : 0 );
                entrant.getExamInfo().setAchievements( parseAchievements( getCellValue( row, A_ACHIEVEMENTS ) ) );
            } else {
                logger.error( "Can't find entrant by case number" );
            }

        }

        saveAllEntities( entrants );
    }

    private String parseAchievements( String cellValue ) {
        if ( !StringUtils.isBlank( cellValue ) && !cellValue.equals( "0" ) ) {
            StringBuilder builder = new StringBuilder(  );
            Matcher matcher = achievementsPattern.matcher( cellValue );
            while ( matcher.find() ) {
                String name = matcher.group(1);
                String score = matcher.group(2);
                String orderCode = "";
                if ( name.contains( "СтпРФ" ) ) {
                    orderCode = "а";
                } else if ( name.contains( "Отл" ) ) {
                    orderCode = "б";
                } else if ( name.contains( "Статья" ) ) {
                    orderCode = "в";
                } else if ( name.contains( "СтжСПО" ) ) {
                    orderCode = "г1";
                } else if ( name.contains( "СтжВО<1,5" ) ) {
                    orderCode = "г2";
                } else if ( name.contains( "СтжВО1,5++" ) ) {
                    orderCode = "г3";
                } else if ( name.contains( "СтжСел" ) ) {
                    orderCode = "д";
                } else if ( name.contains( "ВСО\"Я-проф\"" ) ) {
                    orderCode = "е";
                } else if ( name.contains( "Вол." ) ) {
                    orderCode = "ж";
                } else if ( name.contains( "Иные" ) ) {
                    orderCode = "к-8";
                } else if ( name.contains( "ВолCOVID" ) ) {
                    orderCode = "з";
                } else if ( name.contains( "СтжCOVID" ) ) {
                    orderCode = "и";
                }
                if ( orderCode.length() > 0 ) {
                    if ( builder.length() > 0  ) {
                        builder.append( "," );
                    }
                    builder.append( orderCode ).append( "-" ).append( score );
                }
            }
            return builder.toString();
        }
        return "";
    }

    public void loadEnrollmentOrder( InputStream file ) throws IOException {


        POIFSFileSystem fs = new POIFSFileSystem( file );
        HSSFWorkbook wb = new HSSFWorkbook( fs );

        HSSFSheet sheet = wb.getSheetAt( 0 );  // main page

        int rowN = 1;  // skip header

        do {
            HSSFRow row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                break;
            }

            Long caseNumb = parseSnils( getCellValue( row, CASE_NUMBER ) );  //parse long value
            Entrant entrant = entrantDao.findEntrantByCaseNumber( caseNumb );
            if ( entrant != null && entrant.getExamInfo() != null ) {
                String specLong = getCellValue( row, SPECIALITY );
                String speciality = "";
                if ( specLong != null ) {
                    Matcher matcher = specialityPattern.matcher( specLong );
                    if ( matcher.find() ) {
                        speciality = matcher.group();
                    }
                }
                String targeting = getCellValue( row, TARGETING );
                String compensation = getCellValue( row, COMPENSATION_TYPE );
                if ( compensation == null || !compensation.equalsIgnoreCase( "бюджет" ) ) {
                    compensation = "договор";
                }
                Request toCompare = new Request();
                toCompare.setFinancing( compensation );
                toCompare.setSpeciality( speciality );
                toCompare.setTargetRequest( targeting );
                for ( Request request : entrant.getRequests() ) {
                    if ( request.equalsByName( toCompare ) ) {
                        request.setEnrollment( true );
                        request.setEnrollmentOrder( getCellValue( row, ORDER_NUMBER ) );
                        request.setEnrollmentOrderDate( ServiceUtils.parseDate( getCellValue( row, ORDER_DATE ) ) );
                        entrantDao.saveEntity( request );
                        break;
                    }
                }
            } else {
                logger.error( "Can't find entrant by case number" );
            }

            rowN++;
        } while ( true );

    }


}

package ru.rsmu.facadeEispo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.*;

import java.io.*;
import java.util.*;

/**
 * @author leonid.
 */
@Component
public class LoadApplicationResponseService {

    private static final String HEADER_APPLICATION = "snils;oid;compaignId;specialty;applicationDate;financingType;targetReception;status;errorInfo";
    private static final String HEADER_SCORES = "snils;oid;testResultType;testResultOrganization;testResultYear;specialty;result;status;errorInfo";
    private static final String HEADER_WITHDRAWAL = "snils;oid;compaignId;specialty;financingType;targetReception;applicationDate;status;errorInfo";
    private static final String HEADER_LOGINS = "snils;oid;specialty;date;login;password;status;errorInfo";
    private static final String HEADER_FINAL_REPORT = "snils;oid;compaignId;specialty;applicationDate;financingType;targetReception;status;errorInfo;finalReport";
    private static final String HEADER_FOREIGNERS_SCORES = "snils;oid;dateOfBirth;surname;name;patronymic;certNumber;docNumber;testResultType;testResultOrganization;testResultYear;specialty;result;additionalInfo;status;errorInfo";

    public enum ResponseType {
        UNDEFINED(""),
        APPLICATION( HEADER_APPLICATION ),
        SCORES( HEADER_SCORES ),
        WITHDRAWAL( HEADER_WITHDRAWAL ),
        LOGINS( HEADER_LOGINS ),
        FINAL_REPORT( HEADER_FINAL_REPORT ),
        FOREIGNERS_SCORES( HEADER_FOREIGNERS_SCORES )
        ;

        private String header;

        ResponseType( String header ) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }

        public static ResponseType findByHeader( String header ) {
            for ( ResponseType type : ResponseType.values() ) {
                if ( type.getHeader().equals( header ) ) {  // what is at the begin of the line?
                    return type;
                }
            }
            return UNDEFINED;
        }
    }

    @Autowired
    private EntrantDao entrantDao;

    public ResponseType loadResponse( InputStream fileInputStream, boolean isFinalResponse ) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

        Map<Long,Entrant> entrantMap = new HashMap<>();

        ResponseType type = null;
        //Read File Line By Line
        for ( String strLine; (strLine = br.readLine()) != null; ) {
            if ( type == null ) {
                // what is at the begin of the line?
                if ( !strLine.startsWith( "snils" ) ) strLine = strLine.substring( 1 );
                type = ResponseType.findByHeader( strLine );
                continue;
            }
            switch ( type ) {
                case APPLICATION:
                    if ( isFinalResponse ) {
                        parseFinalReport( strLine, entrantMap );
                    } else {
                        parseApplication( strLine, entrantMap );
                    }
                    break;
                case SCORES:
                    parseScores( strLine, entrantMap );
                    break;
                case WITHDRAWAL:
                    parseWithdrawal( strLine, entrantMap );
                    finalizeWithdrawal( entrantMap.values() );
                    break;
                case LOGINS:
                    parseLogins( strLine, entrantMap );
                    break;
                case FINAL_REPORT:
                    parseFinalReport( strLine, entrantMap );
                    break;
                case FOREIGNERS_SCORES:
                    parseForeignersScores( strLine, entrantMap );
                    break;
                case UNDEFINED:
                    return type;
            }
        }
        return type;
    }

    private void parseApplication( String strLine, Map<Long, Entrant> entrantMap ) {

        Entrant entrant = null;
        //Read File Line By Line
        String[] cells = strLine.split( ";" );
        if ( cells.length < 8 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            return;
        }
        if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }
        Request r1 = new Request();
        r1.setSpeciality( cells[3] );
        r1.setApplicationDate( ServiceUtils.parseDate( cells[4] ) );
        r1.setFinancing( cells[5] );
        r1.setTargetRequest( cells[6] );

        for ( Request request : entrant.getRequests() ) {
            if ( request.equalsByName( r1 ) ) {
                EispoResponse response = request.getResponse()==null ? new EispoResponse() : request.getResponse();
                response.setRequest( request );
                String comment = cells.length >= 9 ? cells[8] : "";
                /*if ( comment.length() > 251 ) {
                    comment = comment.substring( 0, 250 );
                }*/
                response.setResponse( comment );

                if ( cells[7].startsWith( "не" ) ) {
                    request.setStatus( RequestStatus.REJECTED );
                    response.setSuccess( false );
                }
                else {
                    request.setStatus( RequestStatus.CONFIRMED );
                    response.setSuccess( true );
                }
                response.setDate( new Date() );
                entrantDao.saveEntity( response );
                request.setResponse( response );
                entrantDao.saveEntity( request );
            }
        }
        entrant.setStatus( EntrantStatus.SUBMITTED );
        entrantDao.saveEntity( entrant );
    }

    private void parseScores( String strLine, Map<Long, Entrant> entrantMap ) {
        Entrant entrant = null;
        String[] cells = strLine.split( ";" );
        if ( cells.length < 8 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            return;
        }
        if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }

        String comment = cells.length >= 9 ? cells[8] : "";
        if ( comment.length() > 251 ) {
            comment = comment.substring( 0, 250 );
        }
        Integer score = 0;
        try {
            score = Integer.parseInt( cells[6] );
        } catch (NumberFormatException e) {
            score = null;
        }

        if ( cells[7].startsWith( "не" ) ) {
            entrant.getExamInfo().setScore( null );
        } else {
            entrant.getExamInfo().setScore( score );
        }
        entrant.getExamInfo().setResponse( comment );

        entrantDao.saveEntity( entrant.getExamInfo() );
    }

    private void parseWithdrawal( String strLine, Map<Long, Entrant> entrantMap ) {
        Entrant entrant = null;
        //Read File Line By Line
        String[] cells = strLine.split( ";" );
        if ( cells.length < 8 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            return;
        }
        if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }

        Request r1 = new Request();
        r1.setSpeciality( cells[3] );
        r1.setApplicationDate( ServiceUtils.parseDate( cells[6] ) );
        r1.setFinancing( cells[4] );
        r1.setTargetRequest( cells[5] );

        for ( Request request : entrant.getRequests() ) {
            if ( request.equalsByName( r1 ) ) {

                if ( !cells[7].startsWith( "не" ) ) {
                    if ( request.getStatus() == RequestStatus.RETIRED ) {
                        request.setStatus( RequestStatus.TERMINATED );
                    }
                    else if ( request.getStatus() == RequestStatus.REFRESHING ) {
                        request.setStatus( RequestStatus.NEW );
                    }
                }
                entrantDao.saveEntity( request );
            }
        }
    }

    private void finalizeWithdrawal( Collection<Entrant> entrants ) {

        for ( Entrant entrant1 : entrants ){
            boolean terminated = true;
            for ( Request request : entrant1.getRequests() ) {
                if ( request.getStatus() != RequestStatus.TERMINATED ) {
                    terminated = false;
                    break;
                }
            }
            if ( terminated ) {
                entrant1.setStatus( EntrantStatus.RETIRED );
                entrantDao.saveEntity( entrant1 );
            }
        }
    }

    private void parseLogins( String strLine, Map<Long, Entrant> entrantMap ) {
        Entrant entrant = null;
        //Read File Line By Line
        String[] cells = strLine.split( ";" );
        if ( cells.length < 7 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            return;
        }
        if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }
        LoginInfo loginInfo = entrantDao.findLoginInfo( entrant );
        if ( loginInfo == null ) {
            loginInfo = new LoginInfo();
            loginInfo.setEntrant( entrant );
        }

        String comment = cells.length >= 8 ? cells[7] : "";
        if ( comment.length() > 251 ) {
            comment = comment.substring( 0, 250 );
        }

        loginInfo.setLogin( cells[4] );
        loginInfo.setPassword( cells[5] );
        if ( cells[6].startsWith( "не" ) ) {
            loginInfo.setSuccess( false );
            loginInfo.setInfo( comment );
        } else {
            loginInfo.setSuccess( true );
            loginInfo.setInfo( "" );
        }

        entrantDao.saveEntity( loginInfo );
    }

    private void parseFinalReport( String strLine, Map<Long, Entrant> entrantMap ) {
        //"snils;oid;compaignId;specialty;applicationDate;financingType;targetReception;status;errorInfo"
        //  0     1    2          3          4                5             6             7       8
        Entrant entrant = null;
        //Read File Line By Line
        String[] cells = strLine.split( ";" );
        if ( cells.length < 8 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            return;
        }
        if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }
        Request r1 = new Request();
        r1.setSpeciality( cells[3] );
        r1.setApplicationDate( ServiceUtils.parseDate( cells[4] ) );
        r1.setFinancing( cells[5] );
        r1.setTargetRequest( cells[6] );

        for ( Request request : entrant.getRequests() ) {
            if ( request.equalsByName( r1 ) ) {
                EnrollmentResponse response = request.getEnrollmentResponse();
                if ( response == null ) {
                    response = new EnrollmentResponse();
                    request.setEnrollmentResponse( response );
                }

                String comment = cells.length >= 9 ? cells[8] : "";
/*
                if ( comment.length() > 251 ) {
                    comment = comment.substring( 0, 250 );
                }
*/
                response.setSuccess( !cells[7].startsWith( "не" ) );
                response.setResponse( comment );
                boolean doSave = response.getId() == 0;
                entrantDao.saveEntity( response );
                if ( doSave ) entrantDao.saveEntity( request );
            }
        }

    }

    private void parseForeignersScores( String strLine, Map<Long, Entrant> entrantMap ) {
        //snils;oid;dateOfBirth;surname;name;patronymic;certNumber;docNumber;testResultType;testResultOrganization;testResultYear;specialty;result;additionalInfo;status;errorInfo
        //  0    1      2         3       4     5           6         7         8                  9                    10             11     12       13           14     15
        Entrant entrant = null;
        String[] cells = strLine.split( ";" );
        if ( cells.length < 15 ) {
            return;
        }
        Long snils = ServiceUtils.parseSnils( cells[0] );
        if ( snils == null ) {
            entrant = entrantDao.findEntrantByName( cells[3], cells[4], cells[5] );
            if ( entrant == null ) {
                return;
            }
        }
        else if ( (entrant = entrantMap.get( snils )) == null ) {
            entrant = entrantDao.findEntrantBySnilsNumber( snils );
            if ( entrant == null ) {
                return;
            }
            entrantMap.put( snils, entrant );
        }

        String comment = cells.length >= 16 ? cells[15] : "";
        Integer score = 0;
        try {
            score = Integer.parseInt( cells[12] );
        } catch (NumberFormatException e) {
            score = null;
        }

        if ( cells[14].startsWith( "не" ) ) {
            entrant.getExamInfo().setScore( null );
        } else {
            entrant.getExamInfo().setScore( score );
        }
        entrant.getExamInfo().setResponse( comment );

        entrantDao.saveEntity( entrant.getExamInfo() );
    }
}

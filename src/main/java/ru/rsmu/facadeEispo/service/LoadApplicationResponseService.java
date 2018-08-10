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

    @Autowired
    private EntrantDao entrantDao;

    public void loadResponse( InputStream fileInputStream ) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

        boolean skipHeader = true;
        Entrant entrant = null;
        //Read File Line By Line
        for (String strLine;(strLine = br.readLine()) != null;)   {
            if ( skipHeader ) {
                skipHeader = false;
                continue;
            }
            String[] cells = strLine.split( ";" );
            if ( cells.length < 8 ) {
                continue;
            }
            Long snils = ServiceUtils.parseSnils( cells[0] );
            if ( snils == null ) {
                continue;
            }
            if ( entrant == null || !snils.equals( entrant.getSnilsNumber() ) ) {
                entrant = entrantDao.findEntrantBySnilsNumber( snils );
            }
            if ( entrant == null ) {
                continue;
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
                    if ( comment.length() > 251 ) {
                        comment = comment.substring( 0, 250 );
                    }
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
    }

    public void loadScores( InputStream inputStream ) throws IOException {
        //snils;oid;testResultType;testResultOrganization;testResultYear;specialty;result;status;errorInfo
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        boolean skipHeader = true;
        Entrant entrant = null;
        //Read File Line By Line
        for (String strLine;(strLine = br.readLine()) != null;) {
            if ( skipHeader ) {
                skipHeader = false;
                continue;
            }
            String[] cells = strLine.split( ";" );
            if ( cells.length < 8 ) {
                continue;
            }
            Long snils = ServiceUtils.parseSnils( cells[0] );
            if ( snils == null ) {
                continue;
            }
            if ( entrant == null || !snils.equals( entrant.getSnilsNumber() ) ) {
                entrant = entrantDao.findEntrantBySnilsNumber( snils );
            }
            if ( entrant == null ) {
                continue;
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
    }

    public void loadWithdrawal( InputStream inputStream ) throws IOException {
        //snils; oid; compaignId; specialty; financingType; targetReception; applicationDate;  status; errorInfo
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        boolean skipHeader = true;
        Entrant entrant = null;
        Set<Entrant> entrants = new HashSet<>();
        //Read File Line By Line
        for (String strLine;(strLine = br.readLine()) != null;) {
            if ( skipHeader ) {
                skipHeader = false;
                continue;
            }
            String[] cells = strLine.split( ";" );
            if ( cells.length < 8 ) {
                continue;
            }
            Long snils = ServiceUtils.parseSnils( cells[0] );
            if ( snils == null ) {
                continue;
            }
            if ( entrant == null || !snils.equals( entrant.getSnilsNumber() ) ) {
                entrant = entrantDao.findEntrantBySnilsNumber( snils );
                if ( entrant != null && !entrants.contains( entrant ) ) {
                    entrants.add( entrant );
                }
            }
            if ( entrant == null ) {
                continue;
            }

            Request r1 = new Request();
            r1.setSpeciality( cells[3] );
            r1.setApplicationDate( ServiceUtils.parseDate( cells[6] ) );
            r1.setFinancing( cells[4] );
            r1.setTargetRequest( cells[5] );

            for ( Request request : entrant.getRequests() ) {
                if ( request.equalsByName( r1 ) ) {

                    if ( !cells[7].startsWith( "не" ) ) {
                        request.setStatus( RequestStatus.TERMINATED );
                    }
                    entrantDao.saveEntity( request );
                }
            }
        }
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

}

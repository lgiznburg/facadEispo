package ru.rsmu.facadeEispo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.*;

import java.io.*;
import java.util.Date;

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
}

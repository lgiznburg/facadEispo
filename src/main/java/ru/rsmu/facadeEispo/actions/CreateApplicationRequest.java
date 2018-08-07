package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.dao.RequestDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.Request;
import ru.rsmu.facadeEispo.model.RequestStatus;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Controller
public class CreateApplicationRequest  {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );

    @Autowired
    private EntrantDao entrantDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private StoredPropertyService propertyService;

    @RequestMapping(value = "/createApplicationRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;surname;name;patronymic;oid;compaignId;dateOfBirth;citizenship;specialty;financingType;applicationDate;targetReception;testResultType;testResultOrganization;testResultYear\n" );

        List<Request> requests = requestDao.findRequestsForExport();
        for( Request request : requests ) {
            if ( !request.getEntrant().isValid() ) {
                continue;
            }
            if ( request.getStatus() == RequestStatus.RETIRED || request.getStatus() == RequestStatus.TERMINATED ) {
                continue;
            }
            result.append( String.format( "%011d", request.getEntrant().getSnilsNumber() ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( request.getEntrant().getDeception().getLastName() ).append( ";" )
                        .append( request.getEntrant().getDeception().getFirstName() ).append( ";" )
                        .append( request.getEntrant().getDeception().getMiddleName() ).append( ";" );
            } else {
                result.append( request.getEntrant().getLastName() ).append( ";" )
                        .append( request.getEntrant().getFirstName() ).append( ";" )
                        .append( request.getEntrant().getMiddleName() ).append( ";" );
            }

            result.append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( DATE_FORMAT.format( request.getEntrant().getDeception().getBirthDate() ) ).append( ";" );
            } else {
                result.append( DATE_FORMAT.format( request.getEntrant().getBirthDate() ) ).append( ";" );
            }

            result.append( request.getEntrant().getCitizenship() ).append( ";" )
                    .append( request.getSpeciality() ).append( ";" )
                    .append( request.getFinancing() ).append( ";" )
                    .append( DATE_FORMAT.format( request.getApplicationDate() ) ).append( ";" )
                    .append( request.getTargetRequest() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getType() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getOrganization() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getYear() ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_applications_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createWithdrawalRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createWithdrwalCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;compaignId;dateOfBirth;specialty;financingType;targetReception;applicationDate;initiative\n" );

        List<Request> requests = requestDao.findWithdrawalRequests();
        for( Request request : requests ) {
            if ( !request.getEntrant().isValid() ) {
                continue;
            }
            result.append( String.format( "%011d", request.getEntrant().getSnilsNumber() ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( DATE_FORMAT.format( request.getEntrant().getDeception().getBirthDate() ) ).append( ";" );
            } else {
                result.append( DATE_FORMAT.format( request.getEntrant().getBirthDate() ) ).append( ";" );
            }

            result.append( request.getSpeciality() ).append( ";" )
                    .append( request.getFinancing() ).append( ";" )
                    .append( request.getTargetRequest() ).append( ";" )
                    .append( DATE_FORMAT.format( request.getApplicationDate() ) ).append( ";" )
                    .append( "1" ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_withdrawal_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createScoresRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createScoresCsvRequest(@RequestParam(value = "type",required = false) String type) {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;dateOfBirth;testResultType;testResultYear;testResultOrganization;specialty\n" );

        List<Entrant> entrants = entrantDao.findEntrantsForScores();
        for( Entrant entrant : entrants ) {
            if ( !entrant.isValid() ) {
                continue;
            }
            if ( "ординатура".equalsIgnoreCase( entrant.getExamInfo().getType() ) ) {
                // ординатура
                if ( type == null || !type.equals( "test" ) ) {
                    continue; //запрошена аккредитация
                }
            }
            else {  // аккредитация
                if ( type != null && type.equals( "test" ) ) {
                    continue; //запрошено тестирование
                }
            }
            boolean rejected = false;
            for ( Request request : entrant.getRequests() ) {
                if ( request.getStatus() != RequestStatus.CONFIRMED ) {
                    rejected = true;
                    break;
                }
            }
            if ( rejected ) continue;


            result.append( String.format( "%011d", entrant.getSnilsNumber() ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" );

            /*if ( entrant.getEntrant().getDeception() != null ) {
                result.append( DATE_FORMAT.format( entrant.getEntrant().getDeception().getBirthDate() ) ).append( ";" );
            } else {*/
                result.append( DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" );
            //}

            result.append( entrant.getExamInfo().getType() ).append( ";" )
                    .append( entrant.getExamInfo().getYear() ).append( ";" )
                    .append( entrant.getExamInfo().getOrganization() ).append( ";" )
                    .append( entrant.getExamInfo().getSpeciality() ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_scores_request_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }



}

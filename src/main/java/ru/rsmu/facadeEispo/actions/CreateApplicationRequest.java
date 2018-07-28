package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.dao.RequestDao;
import ru.rsmu.facadeEispo.model.Request;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/createApplicationRequest.htm")
public class CreateApplicationRequest  {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );

    @Autowired
    private EntrantDao entrantDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private StoredPropertyService propertyService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> createCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;surname;name;patronymic;oid;compaignId;dateOfBirth;citizenship;specialty;financingType;applicationDate;targetReception;testResultType;testResultOrganization;testResultYear\n" );

        List<Request> requests = requestDao.findRequestsForExport();
        for( Request request : requests ) {
            if ( !request.getEntrant().isValid() ) {
                continue;
            }
            result.append( String.format( "%011d", request.getEntrant().getSnilsNumber() )).append( ";" )
                    .append( request.getEntrant().getLastName() ).append( ";" )
                    .append( request.getEntrant().getFirstName() ).append( ";" )
                    .append( request.getEntrant().getMiddleName() ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" )
                    .append( DATE_FORMAT.format( request.getEntrant().getBirthDate() ) ).append( ";" )
                    .append( request.getEntrant().getCitizenship() ).append( ";" )
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
}

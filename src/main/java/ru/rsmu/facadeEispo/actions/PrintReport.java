package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Controller
public class PrintReport {
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );

    @Autowired
    private EntrantDao entrantDao;

    @RequestMapping(value = "/printErrors.htm")
    public String showPrintError( ModelMap modelMap ) {
        List<Entrant> entrants = entrantDao.findEntrantsWithError();
        modelMap.addAttribute( "entrants", entrants );
        return "/blocks/PrintErrors";
    }

    @RequestMapping(value = "/createCsvErrors.htm")
    public ResponseEntity<String> createCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "Номер дела;ФИО;Данные в РНИМУ;Описание ошибки ЕИСПО\n" );

        List<Entrant> entrants = entrantDao.findEntrantsWithError();
        for( Entrant entrant : entrants ) {
            result.append( entrant.getCaseNumber() ).append( ";" )
            .append( entrant.getLastName() ).append( " " )
            .append( entrant.getFirstName() ).append( " " )
            .append( entrant.getMiddleName() ).append( ";" )
            .append( "Тип " ).append( entrant.getExamInfo().getType() )
            .append( " Организация " ).append( entrant.getExamInfo().getOrganization() )
            .append( " Год " ).append( entrant.getExamInfo().getYear() ).append( ";" )
            .append( "\"" ).append( entrant.getRequests().get( 0 ).getResponse().getResponse() ).append( "\"\n" );


        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_errors_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

}

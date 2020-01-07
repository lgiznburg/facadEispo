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
import ru.rsmu.facadeEispo.model.LoginInfo;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.service.ServiceUtils;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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

    @Autowired
    private StoredPropertyService propertyService;

    @RequestMapping(value = "/printErrors.htm")
    public String showPrintError( ModelMap modelMap ) {
        List<Entrant> entrants = entrantDao.findEntrantsWithError();

        Collections.sort( entrants, new Comparator<Entrant>() {
            @Override
            public int compare( Entrant o1, Entrant o2 ) {
                return o1.getLastName().compareTo( o2.getLastName() );
            }
        } );

        modelMap.addAttribute( "entrants", entrants );
        return "/blocks/PrintErrors";
    }

    @RequestMapping(value = "/createCsvErrors.htm")
    public ResponseEntity<String> createCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        boolean extended = propertyService.getPropertyAsInt( StoredPropertyName.REPORT_EXTENDED_ERROR_CSV ) > 0;
        result.append( "Номер дела;" );
        if ( extended ) { result.append( "СНИЛС;" ); }
        result.append( "ФИО;" );
        if ( extended ) { result.append( "Дата рожд.;" ); }
        result.append( "Данные в РНИМУ;Описание ошибки ЕИСПО\n" );

        List<Entrant> entrants = entrantDao.findEntrantsWithError();

        Collections.sort( entrants, new Comparator<Entrant>() {
            @Override
            public int compare( Entrant o1, Entrant o2 ) {
                return o1.getLastName().compareTo( o2.getLastName() );
            }
        } );

        for( Entrant entrant : entrants ) {
            String response = entrant.getRequests().get( 0 ).getResponse().getResponse();
            /*if ( response.lastIndexOf( " В заявлении" ) > 0 ) {
                response = response.substring( 0, response.lastIndexOf( " В заявлении" ) );
                response += " Есть еще одно заявление в другую организацию с такими же данными";
            }*/
            result.append( entrant.getCaseNumber() ).append( ";" );
            if ( extended ) { result.append( entrant.getSnilsNumber() ).append( ";" ); }
            result.append( entrant.getLastName() ).append( " " )
                    .append( entrant.getFirstName() ).append( " " )
                    .append( entrant.getMiddleName() ).append( ";" );
            if ( extended ) { result.append( ServiceUtils.DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" ); }
            result.append( "Тип " ).append( entrant.getExamInfo().getType() )
                    .append( " Организация " ).append( entrant.getExamInfo().getOrganization() )
                    .append( " Год " ).append( entrant.getExamInfo().getYear() ).append( ";" )
                    .append( "\"" ).append( response ).append( "\"\n" );


        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_errors_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createCsvScores.htm")
    public ResponseEntity<String> createCsvScores() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "Номер дела;ФИО;Тип;Организация;Год;Балл\n" );

        List<Entrant> entrants = entrantDao.findEntrantsWithScore();

        Collections.sort( entrants, new Comparator<Entrant>() {
            @Override
            public int compare( Entrant o1, Entrant o2 ) {
                return o1.getLastName().compareTo( o2.getLastName() );
            }
        } );

        for( Entrant entrant : entrants ) {
            result.append( entrant.getCaseNumber() ).append( ";" );
            result.append( entrant.getLastName() ).append( " " )
                    .append( entrant.getFirstName() ).append( " " )
                    .append( entrant.getMiddleName() ).append( ";" );
            result.append( entrant.getExamInfo().getType() ).append( ";" )
                    .append( entrant.getExamInfo().getOrganization() ).append( ";" )
                    .append( entrant.getExamInfo().getYear() ).append( ";" )
                    .append( entrant.getExamInfo().getScore() ).append( "\n" );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_scores_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createCsvLogins.htm")
    public ResponseEntity<String> createCsvLogins() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "Номер дела;ФИО;Подпись;Логин;Пароль\n" );

        List<LoginInfo> loginInfos = entrantDao.findAllLoginInfo( false );
        loginInfos.sort( new Comparator<LoginInfo>() {
            @Override
            public int compare( LoginInfo o1, LoginInfo o2 ) {
                int compare1 = o1.getEntrant().getExamInfo().getScheduledDate().compareTo( o2.getEntrant().getExamInfo().getScheduledDate() );
                return compare1 != 0 ? compare1 : o1.getEntrant().getLastName().compareToIgnoreCase( o2.getEntrant().getLastName() );
            }
        } );
        Date date = new Date();
        for( LoginInfo loginInfo : loginInfos ) {
            Date examDate = loginInfo.getEntrant().getExamInfo().getScheduledDate();
            if ( !examDate.equals( date ) ) {
                date = examDate;
                result.append( "\n" ).append( DATE_FORMAT.format( examDate ) ).append( "\n\n" );
                result.append( "Номер дела;ФИО;Подпись;Логин;Пароль\n" );

            }
            result.append( loginInfo.getEntrant().getCaseNumber() ).append( ";" )
            .append( loginInfo.getEntrant().getLastName() ).append( " " )
            .append( loginInfo.getEntrant().getFirstName() ).append( " " )
            .append( loginInfo.getEntrant().getMiddleName() ).append( ";;" );
            if ( loginInfo.isSuccess() ) {
                result.append( loginInfo.getLogin() ).append( ";" )
                        .append( loginInfo.getPassword() ).append( "\n" );
            } else {
                result.append( "нет допуска;" )
                        .append( loginInfo.getInfo() ).append( "\n" );
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"login_info_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createCsvScoresError.htm")
    public ResponseEntity<String> createCsvScoresError() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "№ дела;ФИО;Способ получения ВИ;Ошибка\n" );

        List<Entrant> entrants = entrantDao.findScoresErrors();


        for( Entrant entrant : entrants ) {
            result.append( entrant.getCaseNumber() ).append( ";" );
            result.append( entrant.getLastName() ).append( " " )
                    .append( entrant.getFirstName() ).append( " " )
                    .append( entrant.getMiddleName() ).append( ";" );
            result.append( "Тип " ).append( entrant.getExamInfo().getType() ).append( " " )
                    .append( "Год " ).append( entrant.getExamInfo().getYear() ).append( " " )
                    .append( "Организация " ).append( entrant.getExamInfo().getOrganization() ).append( ";" )
                    .append( entrant.getExamInfo().getResponse() ).append( "\n" );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"scores_error_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }


}

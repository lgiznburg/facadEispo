package ru.rsmu.facadeEispo.actions;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.dao.OidDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.LoginInfo;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.model.oid.OrganizationInfo;
import ru.rsmu.facadeEispo.service.ServiceUtils;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private OidDao oidDao;

    @Autowired
    public void setOidDao( OidDao oidDao ) {
        this.oidDao = oidDao;
    }

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
    public ResponseEntity<byte[]> createCsvRequest() throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet( "Ошибки ординаторов");
        Row header = sheet.createRow( 0 );
        //header
        boolean extended = propertyService.getPropertyAsInt( StoredPropertyName.REPORT_EXTENDED_ERROR_CSV ) > 0;

        int cellN = 0;
        Cell cell = header.createCell( cellN++ );
        cell.setCellValue( "Номер дела" );
        if ( extended ) {
            cell = header.createCell( cellN++ );
            cell.setCellValue( "СНИЛС" );
        }
        cell = header.createCell( cellN++ );
        cell.setCellValue( "ФИО" );
        if ( extended ) {
            cell = header.createCell( cellN++ );
            cell.setCellValue( "Дата рожд." );
        }
        cell = header.createCell( cellN++ );
        cell.setCellValue( "Данные в РНИМУ" );
        cell = header.createCell( cellN++ );
        cell.setCellValue( "Описание ошибки ЕИСПО" );

        List<Entrant> entrants = entrantDao.findEntrantsWithError();

        entrants.sort( new Comparator<Entrant>() {
            @Override
            public int compare( Entrant o1, Entrant o2 ) {
                return o1.getLastName().compareTo( o2.getLastName() );
            }
        } );

        int rowN = 1;
        for( Entrant entrant : entrants ) {
            Row row = sheet.createRow( rowN++ );
            cellN = 0;
            cell = row.createCell( cellN++ );
            cell.setCellValue( entrant.getCaseNumber() );


            String response = entrant.getRequests().get( 0 ).getResponse().getResponse();
            /*if ( response.lastIndexOf( " В заявлении" ) > 0 ) {
                response = response.substring( 0, response.lastIndexOf( " В заявлении" ) );
                response += " Есть еще одно заявление в другую организацию с такими же данными";
            }*/
            if ( extended ) {
                cell = row.createCell( cellN++ );
                cell.setCellValue( entrant.getSnilsNumber() );
            }
            StringBuilder result = new StringBuilder();
            result.append( entrant.getLastName() ).append( " " )
                    .append( entrant.getFirstName() ).append( " " )
                    .append( entrant.getMiddleName() );
            cell = row.createCell( cellN++ );
            cell.setCellValue( result.toString() );
            if ( extended ) {
                cell = row.createCell( cellN++ );
                cell.setCellValue( ServiceUtils.DATE_FORMAT.format( entrant.getBirthDate() ) );
                result.append( ServiceUtils.DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" );
            }

            cell = row.createCell( cellN++ );
            cell.setCellValue( ServiceUtils.getEntrantInfo( entrant, oidDao ) );
            cell = row.createCell( cellN++ );
            cell.setCellValue( ServiceUtils.getReadableErrorInfo( response, oidDao ) );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ) );
        String attachment = String.format("attachment; filename=\"entrant_errors_%s.xlsx\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        headers.set("Expires", "0");
        headers.set("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        headers.set("Pragma", "public");
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        wb.write( document );
        return new ResponseEntity<byte[]>(document.toByteArray(), headers, HttpStatus.OK );

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

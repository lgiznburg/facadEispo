package ru.rsmu.facadeEispo.actions;

import org.apache.commons.mail.EmailException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.oid.OrganizationInfo;
import ru.rsmu.facadeEispo.service.EmailService;
import ru.rsmu.facadeEispo.service.EmailType;
import ru.rsmu.facadeEispo.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/mailingToList.htm")
public class MailingToList extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( MailingToList.class );

    private EntrantDao entrantDao;

    private EmailService emailService;

    @Autowired
    public void setEntrantDao( EntrantDao entrantDao ) {
        this.entrantDao = entrantDao;
    }

    @Autowired
    public void setEmailService( EmailService emailService ) {
        this.emailService = emailService;
    }

    public MailingToList() {
        setTitle( "Send email to entrants" );
        setContent( "/WEB-INF/pages/blocks/MailingToList.jsp" );
    }

    @ModelAttribute("emailsList")
    public EmailType[] getEmailTypes() { return EmailType.values(); }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap ) {
        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String sendToList( ModelMap modelMap,
                              @RequestParam("emailType") String emailTypeName,
                              @RequestParam("incomingList") String incomingList ) {
        List<String> errors = new ArrayList<>();
        int sent = 0;

        EmailType type = EmailType.valueOf( emailTypeName );

        String[] cases = incomingList.split( "[ .,;:-]" );
        for ( String caseNumberStr : cases ) {
            try {
                long caseNumber = Long.parseLong( caseNumberStr );
                Entrant entrant = entrantDao.findEntrantByCaseNumber( caseNumber );
                if ( entrant != null ) {
                    Map<String,Object> model = new HashMap<>();
                    model.put( "user", entrant );
                    emailService.sendEmail( entrant, type, model );
                    sent++;
                }
                else {
                    errors.add( String.format( "Not found: %s", caseNumberStr ) );
                }
            } catch (NumberFormatException e) {
                errors.add( String.format( " Error with: %s", caseNumberStr ) );
            }
        }
        errors.add( 0, String.format( "Отправлено %d писем", sent ) );
        modelMap.put( "errors", errors );
        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST, params = "fromFile")
    public String sendFromFile( HttpServletRequest request, ModelMap modelMap ,
                                @RequestParam("subject") String subject,
                                @RequestParam("bodyTemplate") String bodyTemplate) {
        List<String> errors = new ArrayList<>();
        int sent = 0;

        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "sendToFile" ) && !multipart.getFile( "sendToFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "sendToFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xlsx?" ) ) {
                        SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy" );
                        Workbook wb;
                        if ( file.getOriginalFilename().matches(".*\\.xls") ) {
                            POIFSFileSystem fs = new POIFSFileSystem( file.getInputStream() );
                            wb = new HSSFWorkbook( fs );
                        }
                        else {
                            wb = new XSSFWorkbook( file.getInputStream() );
                        }
                        Sheet sheet = wb.getSheetAt( 0 );
                        for ( Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
                            Row row = rowIterator.next();
                            Long caseNumber = ServiceUtils.getCellNumber( row, (short) 0 );
                            if ( caseNumber == null  ) {
                                continue;  // skip rows without ID
                            }
                            Entrant entrant = entrantDao.findEntrantByCaseNumber( caseNumber );
                            if ( entrant != null ) {
                                Map<String,Object> model = new HashMap<>();
                                model.put( "user", entrant );
                                try {
                                    emailService.sendEmail( entrant, subject, bodyTemplate, model );
                                    sent++;
                                } catch (EmailException e) {
                                    //e.printStackTrace();
                                    errors.add( String.format( " Error with: %s, Exception: %s", caseNumber, e.getMessage() )  );
                                }
                            }
                            else {
                                errors.add( String.format( "Not found: %d", caseNumber ) );
                            }
                        }
                    }
                }
            }
        } catch ( IOException e ) {
            errors.add( "Can't upload information from Excel file: "+ e.getMessage() );
        }


        errors.add( 0, String.format( "Отправлено %d писем", sent ) );
        modelMap.put( "errors", errors );
        return buildModel( modelMap );
    }
}

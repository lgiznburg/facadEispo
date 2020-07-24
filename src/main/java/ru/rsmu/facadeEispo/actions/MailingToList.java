package ru.rsmu.facadeEispo.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.service.EmailService;
import ru.rsmu.facadeEispo.service.EmailType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}

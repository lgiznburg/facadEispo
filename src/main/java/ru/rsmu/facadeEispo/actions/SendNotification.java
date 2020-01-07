package ru.rsmu.facadeEispo.actions;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.service.EmailService;
import ru.rsmu.facadeEispo.service.EmailType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
@Controller
public class SendNotification {

    @Autowired
    private EntrantDao entrantDao;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/sendNotifications.htm")
    public String sendNotifications() {
        List<Entrant> entrants = entrantDao.findEntrantsWithError();
        for ( Entrant entrant : entrants ) {
            if ( StringUtils.isEmpty( entrant.getEmail() ) ) continue;
            Map<String, Object> model = new HashMap<>();
            String errorMsg = entrant.getRequests().get( 0 ).getResponse().getResponse();
            if ( errorMsg.contains( "поступающего не совпадает" ) ) {
                // person info errors
                model.put( "user", entrant );
                model.put( "df", new DateTool() );
                emailService.sendEmail( entrant, EmailType.PERSON_INFO_COMPLAIN_NOTIFICATION, model );
            } else {
                // errors in request
                model.put( "user", entrant );
                emailService.sendEmail( entrant, EmailType.ERROR_NOTIFICATION, model );
            }
        }
        return "redirect:/home.htm?variant=error";
    }
}

package ru.rsmu.facadeEispo.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.editor.DateTimeEditor;
import ru.rsmu.facadeEispo.model.Deception;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.EntrantStatus;
import ru.rsmu.facadeEispo.model.RequestStatus;

import java.util.Date;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/editEntrant.htm")
public class EditEntrant extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( EditEntrant.class );

    @Autowired
    private EntrantDao entrantDao;

    public EditEntrant() {
        setTitle( "Edit entrant" );
        setContent( "/WEB-INF/pages/blocks/EditEntrant.jsp" );
    }

    @ModelAttribute("entrant")
    public Entrant getEntrant( @RequestParam(value = "id") Long id ) {
        Entrant entrant = entrantDao.findEntity( Entrant.class, id );
        if ( entrant.getDeception() == null ) {
            entrant.setDeception( new Deception() );
            entrant.getDeception().setFirstName( entrant.getFirstName() );
            entrant.getDeception().setMiddleName( entrant.getMiddleName() );
            entrant.getDeception().setLastName( entrant.getLastName() );
            entrant.getDeception().setBirthDate( entrant.getBirthDate() );
        }
        return entrant;
    }

    @ModelAttribute("entrantStatuses")
    public EntrantStatus[] getEntrantStatuses() {
        return EntrantStatus.values();
    }

    @ModelAttribute("requestStatuses")
    public RequestStatus[] getRequestStatuses() {
        return RequestStatus.values();
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap, @ModelAttribute("entrant") Entrant entrant ) {
        if ( entrant == null )
            return "redirect:/home.htm";

        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST)
    public String updateEntrant( ModelMap modelMap, @ModelAttribute("entrant") Entrant entrant ) {

        if ( entrant.getDeception().getId() == 0 &&
                entrant.getDeception().getFirstName().equalsIgnoreCase( entrant.getFirstName() ) &&
                entrant.getDeception().getMiddleName().equalsIgnoreCase( entrant.getMiddleName() ) &&
                entrant.getDeception().getLastName().equalsIgnoreCase( entrant.getLastName() ) &&
                entrant.getDeception().getBirthDate().equals( entrant.getBirthDate() ) ) {
            entrant.setDeception( null );
        } else {
            entrant.getDeception().setEntrant( entrant );
            entrantDao.saveEntity( entrant.getDeception() );
        }

        entrantDao.saveEntity( entrant );

        return buildModel( modelMap );
    }

    @InitBinder
    public void initBinder( WebDataBinder binder ) {
        binder.registerCustomEditor( Date.class, new DateTimeEditor() );
    }
}

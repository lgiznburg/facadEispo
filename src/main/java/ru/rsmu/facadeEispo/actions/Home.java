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

import java.util.Collections;
import java.util.List;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/home.htm")
public class Home extends BaseController{
    protected Logger logger = LoggerFactory.getLogger( Home.class );

    @Autowired
    private EntrantDao entrantDao;

    public Home() {
        setTitle( "List of loaded entrants" );
        setContent( "/WEB-INF/pages/blocks/Home.jsp" );
    }

    @ModelAttribute("entrants")
    public List<Entrant> getEntrants( @RequestParam(value = "variant", required = false) String variant) {
        if ( variant == null ) {
            return entrantDao.findNewEntrants();
        } else if ( variant.equalsIgnoreCase( "error" ) ) {
            return entrantDao.findEntrantsWithError();
        }
        return entrantDao.findNewEntrants();
    }

    @ModelAttribute("showType")
    public String getShowType( @RequestParam(value = "variant", required = false) String variant ) {
        if ( variant != null ) {
            if( variant.equalsIgnoreCase( "error" ) ) {
                return "error";
            }
        }
        return "new";
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap ) {
        return buildModel( modelMap );
    }
}

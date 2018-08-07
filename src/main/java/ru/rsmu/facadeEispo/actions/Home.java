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
import java.util.Comparator;
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

    private static final Comparator<Entrant> comparator = new Comparator<Entrant>() {
        @Override
        public int compare( Entrant o1, Entrant o2 ) {
            return o1.getLastName().compareTo( o2.getLastName() );
        }
    };

    public Home() {
        setTitle( "List of loaded entrants" );
        setContent( "/WEB-INF/pages/blocks/Home.jsp" );
    }

    @ModelAttribute("entrants")
    public List<Entrant> getEntrants( @RequestParam(value = "variant", required = false) String variant) {
        List<Entrant> entrants = null;
        if ( variant == null ) {
            entrants = entrantDao.findNewEntrants();
        } else if ( variant.equalsIgnoreCase( "error" ) ) {
            entrants = entrantDao.findEntrantsWithError();
        } else if ( variant.equalsIgnoreCase( "search" ) ) {
            entrants = Collections.emptyList();
        }
        if ( entrants == null ) {
            entrants = entrantDao.findNewEntrants();
        }
        Collections.sort( entrants, comparator );

        return entrants;
    }

    @ModelAttribute("searchForm")
    public SearchForm getSearchForm() {
        return new SearchForm();
    }

    @ModelAttribute("showType")
    public String getShowType( @RequestParam(value = "variant", required = false) String variant ) {
        if ( variant != null ) {
            if( variant.equalsIgnoreCase( "error" ) ) {
                return "error";
            }
            if( variant.equalsIgnoreCase( "search" ) ) {
                return "search";
            }
        }
        return "new";
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap ) {
        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String doSearch( ModelMap modelMap,
                            @ModelAttribute("searchForm") SearchForm searchForm ) {
        if ( !searchForm.isEmpty() ) {
            List<Entrant> entrants = entrantDao.findBySearch( searchForm );
            Collections.sort( entrants, comparator );
            modelMap.addAttribute( "entrants", entrants );
        }
        return buildModel( modelMap );
    }
}

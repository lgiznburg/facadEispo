package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rsmu.facadeEispo.dao.StoredPropertyDao;
import ru.rsmu.facadeEispo.model.StoredProperty;
import ru.rsmu.facadeEispo.model.StoredPropertyName;

import java.util.*;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/storedProperties.htm")
public class StoredProperties extends BaseController {

    @Autowired
    private StoredPropertyDao storedPropertyDao;

    public StoredProperties() {
        setTitle( "Stored Properties" );
        setContent( "/WEB-INF/pages/blocks/StoredProperties.jsp" );
    }

    @ModelAttribute("storedProperties")
    public List<StoredProperty> getProperties() {
        Map<StoredPropertyName,StoredProperty> properties = new HashMap<StoredPropertyName, StoredProperty>();
        for ( StoredProperty property : storedPropertyDao.findAll() ) {
            properties.put( property.getPropertyName(), property );
        }
        for ( StoredPropertyName names : StoredPropertyName.values() ) {
            if ( !properties.containsKey( names ) ) {
                properties.put( names, new StoredProperty( names, names.getDefaultValue() ) );
            }
        }
        List<StoredProperty> result = new LinkedList<StoredProperty>( properties.values() );
        Collections.sort( result );

        return result;
    }

    @RequestMapping()
    public String showPage( ModelMap model ) {
        return buildModel( model );
    }

}

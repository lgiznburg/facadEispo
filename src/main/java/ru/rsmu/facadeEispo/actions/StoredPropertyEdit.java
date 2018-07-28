package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rsmu.facadeEispo.dao.StoredPropertyDao;
import ru.rsmu.facadeEispo.model.StoredProperty;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import javax.validation.Valid;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/storedPropertyEdit.htm")
public class StoredPropertyEdit extends BaseController {

    @Autowired
    private StoredPropertyService storedPropertyService;

    @Autowired
    private StoredPropertyDao storedPropertyDao;


    public StoredPropertyEdit() {
        setTitle( "Edit Strored Property" );
        setContent( "/WEB-INF/pages/blocks/StoredPropertyEdit.jsp" );
    }

    @ModelAttribute("storedProperty")
    public StoredProperty getProperty( @RequestParam("propertyName")StoredPropertyName propertyName ) {
        StoredProperty property = storedPropertyDao.findByName( propertyName );
        return property != null ? property : new StoredProperty( propertyName, propertyName.getDefaultValue() );
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD} )
    public String showPage( ModelMap model,
                            @RequestParam("propertyName")StoredPropertyName propertyName ) {
        if ( ! propertyName.isEditable() ) {
            return "redirect:/storedProperties.htm";
        }
        return buildModel( model );
    }

    @RequestMapping(method = RequestMethod.POST)
    public String saveProperty( ModelMap model,
                                @ModelAttribute("storedProperty") @Valid StoredProperty property,
                                BindingResult errors ) {
        if ( errors.hasErrors() ) {
            return buildModel( model );
        }
        storedPropertyService.saveProperty( property );
        return "redirect:/storedProperties.htm";
    }
}

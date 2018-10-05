package ru.rsmu.facadeEispo.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rsmu.facadeEispo.service.LoadFromTandemService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/loadFormTandem.htm")
public class LoadFromTandem  extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( LoadFromTandem.class );

    @Autowired
    private LoadFromTandemService loadFromTandemService;

    public LoadFromTandem() {
        setTitle( "Load File from Tandem" );
        setContent( "/WEB-INF/pages/blocks/LoadFromTandem.jsp" );
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap model ) {
        return buildModel( model );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String onSubmitPage( HttpServletRequest request, ModelMap model,
                                @RequestParam(value = "loadTestDate", required = false) Integer loadTestDate ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xls" ) ) {
                        boolean loginOnly = loadTestDate != null && loadTestDate > 0;
                        /*List<String> messages =*/ loadFromTandemService.readFromFile( file.getInputStream(), loginOnly );
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        return "redirect:/home.htm"/*buildModel( model )*/;
    }

    @RequestMapping( method = RequestMethod.POST, params = "achievements")
    public String onGettingAchievements( HttpServletRequest request, ModelMap model,
                                @RequestParam(value = "loadTestDate", required = false) Integer loadTestDate ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xls" ) ) {
                        boolean loginOnly = loadTestDate != null && loadTestDate > 0;
                        /*List<String> messages =*/ loadFromTandemService.loadScoresAndAchievements( file.getInputStream() );
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        return "redirect:/home.htm"/*buildModel( model )*/;
    }

    @RequestMapping( method = RequestMethod.POST, params = "enrollment")
    public String onGettingEnrollment( HttpServletRequest request, ModelMap model,
                                         @RequestParam(value = "loadTestDate", required = false) Integer loadTestDate ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xls" ) ) {
                        boolean loginOnly = loadTestDate != null && loadTestDate > 0;
                        /*List<String> messages =*/ loadFromTandemService.loadEnrollmentOrder( file.getInputStream() );
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        return "redirect:/home.htm"/*buildModel( model )*/;
    }
}

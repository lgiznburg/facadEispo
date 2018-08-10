package ru.rsmu.facadeEispo.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rsmu.facadeEispo.service.LoadApplicationResponseService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/loadApplicationResponse.htm")
public class LoadApplicationResponse extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( LoadApplicationResponse.class );

    @Autowired
    private LoadApplicationResponseService responseService;

    public LoadApplicationResponse() {
        setTitle( "Load Application Response" );
        setContent( "/WEB-INF/pages/blocks/LoadApplicationResponse.jsp" );
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap model ) {
        return buildModel( model );
    }

    @RequestMapping( method = RequestMethod.POST, params = "response")
    public String onSubmitPage( HttpServletRequest request, ModelMap model ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.csv" ) ) {
                        /*List<String> messages =*/ responseService.loadResponse( file.getInputStream());
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload application response CSV file", e );
        }
        return "redirect:/home.htm?variant=error";
    }

    @RequestMapping( method = RequestMethod.POST, params = "scores")
    public String onUpdateScores( HttpServletRequest request, ModelMap model ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.csv" ) ) {
                        /*List<String> messages =*/ responseService.loadScores( file.getInputStream() );
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload application response CSV file", e );
        }
        return "redirect:/home.htm?variant=scores";
    }

    @RequestMapping( method = RequestMethod.POST, params = "withdrawal")
    public String onUpdateWithdrawal( HttpServletRequest request, ModelMap model ) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.csv" ) ) {
                        /*List<String> messages =*/ responseService.loadWithdrawal( file.getInputStream() );
                        //model.put( "messages", messages );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload application response CSV file", e );
        }
        return "redirect:/home.htm";
    }


}

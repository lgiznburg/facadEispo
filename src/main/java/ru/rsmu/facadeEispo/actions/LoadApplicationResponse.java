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
    public String onSubmitPage( HttpServletRequest request, ModelMap model,
                                @RequestParam(value = "finalResponse", required = false) Integer finalResponse) {
        LoadApplicationResponseService.ResponseType type = LoadApplicationResponseService.ResponseType.UNDEFINED;
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.csv" ) ) {
                        boolean isFinalResponse = finalResponse != null && finalResponse > 0;
                        type = responseService.loadResponse( file.getInputStream(), isFinalResponse );
                    }
                }
            }
        } catch (IOException e) {
            logger.error( "Can't upload application response CSV file", e );
        }
        switch ( type ) {
            case APPLICATION:
                return "redirect:/home.htm?variant=error";
            case SCORES:
                return "redirect:/home.htm?variant=scores";


        }
        return "redirect:/home.htm";
    }


}

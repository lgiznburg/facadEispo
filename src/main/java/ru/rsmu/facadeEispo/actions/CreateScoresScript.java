package ru.rsmu.facadeEispo.actions;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/createScoresScript.htm")
public class CreateScoresScript extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( CreateScoresScript.class );


    @Autowired
    private EntrantDao entrantDao;

    public CreateScoresScript() {
        setTitle( "Create Scores Script" );
        setContent( "/WEB-INF/pages/blocks/CreateScoresScript.jsp" );
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap ) {
        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String createScript( HttpServletRequest request, ModelMap model,
                                @RequestParam(value = "tabNumber") Integer tabNumber ) {

        List<Entrant> entrants = entrantDao.findEntrantsWithScore();
        Map<Long,Entrant> entrantMap = new HashMap<>();
        for ( Entrant entrant : entrants ) {
            entrantMap.put( entrant.getCaseNumber(), entrant );
        }

        List<String> script = new LinkedList<>();
        script.add( "WAIT(3)" );


        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xls" ) ) {
                        POIFSFileSystem fs = new POIFSFileSystem( file.getInputStream() );
                        HSSFWorkbook wb = new HSSFWorkbook( fs );
                        HSSFSheet sheet = wb.getSheetAt( 0 );  // main page
                        int rowN = 1;  //start from second line
                        do {
                            HSSFRow row = sheet.getRow( rowN++ );
                            // check if row is valid
                            if ( row == null || row.getCell( (short) 0 ) == null ) {
                                break;
                            }

                            Long caseNumber = getCellNumber( row, (short)0 );

                            Entrant entrant = null;
                            if ( caseNumber != null ) {
                                entrant = entrantMap.get( caseNumber );
                            }
                            script.add( String.format( "KEYSTRING(\"%s\")", entrant != null ? entrant.getExamInfo().getScore().toString() : "" ) );

                            for ( int i = 0; i < tabNumber; i++ ) {
                                script.add( "KEYPRESS(#TAB)" );
                            }


                        } while ( true );

                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        script.add( "HALT" );

        model.addAttribute( "script", script );
        return buildModel( model );
    }

    private Long getCellNumber( HSSFRow row, short cellN ) {
        HSSFCell cell = row.getCell( cellN );
        if ( cell != null ) {
            Long value;
            switch ( cell.getCellType() ) {
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = Math.round( cell.getNumericCellValue() );
                    break;
                default:
                    return null;
            }
            return value;
        }
        return null;
    }

}

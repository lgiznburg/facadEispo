package ru.rsmu.facadeEispo.actions;

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
import ru.rsmu.facadeEispo.model.Request;
import ru.rsmu.facadeEispo.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author leonid.
 */
@Controller
@RequestMapping("/checkPersons.htm")
public class CheckPersons extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( CheckPersons.class );

    @Autowired
    private EntrantDao entrantDao;

    public CheckPersons() {
        setTitle( "Load File from Tandem" );
        setContent( "/WEB-INF/pages/blocks/CheckPersons.jsp" );
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap model ) {
        return buildModel( model );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String onSubmitPage( HttpServletRequest request, ModelMap model) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "studentsFile" ) && !multipart.getFile( "studentsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "studentsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xls" ) ) {

                        Set<Entrant> entrants = new HashSet<>();

                        POIFSFileSystem fs = new POIFSFileSystem( file.getInputStream() );
                        HSSFWorkbook wb = new HSSFWorkbook( fs );
                        HSSFSheet sheet = wb.getSheetAt( 0 );  // main page
                        int rowN = 1;  // skip header


                        do {
                            HSSFRow row = sheet.getRow( rowN );
                            // check if row is valid
                            if ( row == null || row.getCell( (short) 0 ) == null ) {
                                break;
                            }
                            String lastN = ServiceUtils.getCellValue( row, (short)0 );
                            String firstN = ServiceUtils.getCellValue( row, (short)1 );
                            String middleN = ServiceUtils.getCellValue( row, (short)2 );
                            SearchForm form = new SearchForm();
                            form.setLastName( lastN );

                            entrants.addAll( entrantDao.findBySearch( form ) );
                            rowN++;

                        } while ( true );
                        List<Entrant> sorted = new LinkedList<>();
                        sorted.addAll( entrants );
                        Collections.sort( sorted, new Comparator<Entrant>() {
                            @Override
                            public int compare( Entrant o1, Entrant o2 ) {
                                return o1.getFirstName().compareTo( o2.getFirstName() );
                            }
                        } );
                        model.addAttribute( "entrants", sorted );
                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        return buildModel( model );
    }


}

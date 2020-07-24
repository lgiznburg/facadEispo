package ru.rsmu.facadeEispo.actions;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rsmu.facadeEispo.dao.OidDao;
import ru.rsmu.facadeEispo.model.oid.OrganizationInfo;
import ru.rsmu.facadeEispo.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/loadOids.htm")
public class LoadOids extends BaseController {
    protected Logger logger = LoggerFactory.getLogger( LoadOids.class );

    private OidDao oidDao;

    @Autowired
    public void setOidDao( OidDao oidDao ) {
        this.oidDao = oidDao;
    }

    public LoadOids() {
        setTitle( "Load organization IDs" );
        setContent( "/WEB-INF/pages/blocks/LoadOids.jsp" );
    }

    @RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showPage( ModelMap modelMap ) {
        return buildModel( modelMap );
    }

    @RequestMapping( method = RequestMethod.POST )
    public String loadOids( HttpServletRequest request, ModelMap model) {
        try {
            if ( request instanceof MultipartHttpServletRequest ) {
                MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
                if ( multipart.getFileMap().containsKey( "oidsFile" ) && !multipart.getFile( "oidsFile" ).isEmpty() ) {
                    MultipartFile file = multipart.getFile( "oidsFile" );
                    if ( file.getOriginalFilename().matches( ".*\\.xlsx?" ) ) {
                        SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy" );
                        Workbook wb;
                        if ( file.getOriginalFilename().matches(".*\\.xls") ) {
                            POIFSFileSystem fs = new POIFSFileSystem( file.getInputStream() );
                            wb = new HSSFWorkbook( fs );
                        }
                        else {
                            wb = new XSSFWorkbook( file.getInputStream() );
                        }
                        Sheet sheet = wb.getSheetAt( 0 );
                        for ( Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
                            Row row = rowIterator.next();
                            if ( ServiceUtils.getCellNumber( row, (short) 0 ) == null  ) {
                                continue;  // skip rows without ID
                            }
                            OrganizationInfo oid = new OrganizationInfo();
                            oid.setFullName( ServiceUtils.getCellValue( row, (short) 1 ) );
                            oid.setShortName( ServiceUtils.getCellValue( row, (short) 2 ) );

                            String start = ServiceUtils.getCellValue( row, (short) 3 );
                            Date startDate = null;
                            try {
                                startDate = df.parse( start );
                            } catch (ParseException e) {
                                continue;
                            }
                            oid.setNameChangeDate( startDate );
                            String end = ServiceUtils.getCellValue( row, (short) 4 );
                            Date endDate = null;
                            try {
                                if ( end != null ) {
                                    endDate = df.parse( end );
                                }
                            } catch (ParseException e) {
                                //
                            }
                            oid.setNameEndDate( endDate );
                            oid.setOid( ServiceUtils.getCellValue( row, (short) 5 ) );
                            Long subject = ServiceUtils.getCellNumber( row, (short) 6 );
                            oid.setSubjectCode( subject != null? subject.intValue() : 0 );

                            oidDao.saveEntity( oid );
                        }

                    }
                }
            }
        } catch ( IOException e ) {
            logger.error( "Can't upload information from Excel file", e );
        }
        return "redirect:/home.htm"/*buildModel( model )*/;

    }

}

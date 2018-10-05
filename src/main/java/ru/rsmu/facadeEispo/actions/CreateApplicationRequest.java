package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.dao.RequestDao;
import ru.rsmu.facadeEispo.model.*;
import ru.rsmu.facadeEispo.service.ServiceUtils;
import ru.rsmu.facadeEispo.service.StoredPropertyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author leonid.
 */
@Controller
public class CreateApplicationRequest  {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );

    @Autowired
    private EntrantDao entrantDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private StoredPropertyService propertyService;

    @RequestMapping(value = "/createApplicationRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;surname;name;patronymic;oid;compaignId;dateOfBirth;citizenship;specialty;financingType;applicationDate;targetReception;testResultType;testResultOrganization;testResultYear\n" );

        List<Request> requests = requestDao.findRequestsForExport();
        for( Request request : requests ) {
            if ( !request.getEntrant().isValid() ) {
                continue;
            }
            if ( request.getStatus() == RequestStatus.RETIRED || request.getStatus() == RequestStatus.TERMINATED ) {
                continue;
            }
            result.append( String.format( "%011d", request.getEntrant().getSnilsNumber() ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( request.getEntrant().getDeception().getLastName() ).append( ";" )
                        .append( request.getEntrant().getDeception().getFirstName() ).append( ";" )
                        .append( request.getEntrant().getDeception().getMiddleName() ).append( ";" );
            } else {
                result.append( request.getEntrant().getLastName() ).append( ";" )
                        .append( request.getEntrant().getFirstName() ).append( ";" )
                        .append( request.getEntrant().getMiddleName() ).append( ";" );
            }

            result.append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( DATE_FORMAT.format( request.getEntrant().getDeception().getBirthDate() ) ).append( ";" );
            } else {
                result.append( DATE_FORMAT.format( request.getEntrant().getBirthDate() ) ).append( ";" );
            }

            result.append( request.getEntrant().getCitizenship() ).append( ";" )
                    .append( request.getSpeciality() ).append( ";" )
                    .append( request.getFinancing() ).append( ";" )
                    .append( DATE_FORMAT.format( request.getApplicationDate() ) ).append( ";" )
                    .append( request.getTargetRequest() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getType() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getOrganization() ).append( ";" )
                    .append( request.getEntrant().getExamInfo().getYear() ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_applications_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createWithdrawalRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createWithdrwalCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;compaignId;dateOfBirth;specialty;financingType;targetReception;applicationDate;initiative\n" );

        List<Request> requests = requestDao.findWithdrawalRequests();
        for( Request request : requests ) {
            if ( !request.getEntrant().isValid() ) {
                continue;
            }
            result.append( String.format( "%011d", request.getEntrant().getSnilsNumber() ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" );

            if ( request.getEntrant().getDeception() != null ) {
                result.append( DATE_FORMAT.format( request.getEntrant().getDeception().getBirthDate() ) ).append( ";" );
            } else {
                result.append( DATE_FORMAT.format( request.getEntrant().getBirthDate() ) ).append( ";" );
            }

            result.append( request.getSpeciality() ).append( ";" )
                    .append( request.getFinancing() ).append( ";" )
                    .append( request.getTargetRequest() ).append( ";" )
                    .append( DATE_FORMAT.format( request.getApplicationDate() ) ).append( ";" )
                    .append( "1" ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_withdrawal_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createScoresRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createScoresCsvRequest(@RequestParam(value = "type",required = false) String type) {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;dateOfBirth;testResultType;testResultYear;testResultOrganization;specialty\n" );

        List<Entrant> entrants = entrantDao.findEntrantsForScores();
        for( Entrant entrant : entrants ) {
            if ( !entrant.isValid() ) {
                continue;
            }
            if ( "ординатура".equalsIgnoreCase( entrant.getExamInfo().getType() ) ) {
                // имеем - ординатура
                if ( type == null || !type.equals( "test" ) ) {
                    continue; //запрошена аккредитация
                }
                if ( entrant.getExamInfo().getOrganization().equals( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ) &&
                        entrant.getExamInfo().getYear().equals( ServiceUtils.YEAR_FORMAT.format( new Date() ) ) &&
                        entrant.getExamInfo().getScheduledDate() != null &&
                        entrant.getExamInfo().getScheduledDate().after( new Date(  ) ) ) {
                    continue;  //our testing - it does not take place yet
                }
            }
            else {  // имеем - аккредитация
                if ( type != null && type.equals( "test" ) ) {
                    continue; //запрошено тестирование
                }
            }
            if ( entrant.getStatus() == EntrantStatus.SUBMITTED ) {  // only SUBMITTED and ENFORCED selected
                boolean rejected = false;
                for ( Request request : entrant.getRequests() ) {
                    if ( request.getStatus() != RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.TERMINATED ) {
                        rejected = true;
                        break;
                    }
                }
                if ( rejected ) continue;
            }


            result.append( String.format( "%011d", entrant.getSnilsNumber() ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" );

            if ( entrant.getDeception() != null ) {
                result.append( DATE_FORMAT.format( entrant.getDeception().getBirthDate() ) ).append( ";" );
            } else {
                result.append( DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" );
            }

            result.append( entrant.getExamInfo().getType() ).append( ";" )
                    .append( entrant.getExamInfo().getYear() ).append( ";" )
                    .append( entrant.getExamInfo().getOrganization() ).append( ";" )
                    .append( entrant.getExamInfo().getSpeciality() ).append( "\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_scores_request_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

    @RequestMapping(value = "/createLoginRequest.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createLoginCsvRequest() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;dateOfBirth;specialty;date;attemptType;retryReason\n" );

        List<Entrant> entrants = entrantDao.findEntrantsForLogin(
                propertyService.getProperty( StoredPropertyName.SYSTEM_OID ),
                ServiceUtils.YEAR_FORMAT.format( new Date() ));
        List<LoginInfo> loginInfos = entrantDao.findAllLoginInfo();
        Set<Long> alreadyRequested = new HashSet<>();
        for ( LoginInfo loginInfo : loginInfos ) {
            if ( loginInfo.isSuccess() ) {
                alreadyRequested.add( loginInfo.getEntrant().getId() );
            }
        }
        for( Entrant entrant : entrants ) {
            if ( !entrant.isValid() ) {
                continue;
            }
            if ( alreadyRequested.contains( entrant.getId() ) ) { continue; }

            boolean rejected = false;
            switch ( entrant.getStatus() ) {
                case SUBMITTED:
                    for ( Request request : entrant.getRequests() ) {
                        if ( request.getStatus() != RequestStatus.CONFIRMED ) {
                            rejected = true;
                            break;
                        }
                    }
                    break;
                case RETIRED:
                    rejected = true;
            }
            if ( rejected ) continue;
            if ( entrant.getExamInfo().getScheduledDate() == null ) {
                continue;
            }


            result.append( String.format( "%011d", entrant.getSnilsNumber() ) ).append( ";" )
                    .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" );

            result.append( DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" );

            result.append( entrant.getExamInfo().getSpeciality() ).append( ";" )
                    .append( DATE_FORMAT.format( entrant.getExamInfo().getScheduledDate() ) ).append( ";" )
                    .append( "1;\n" );

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"entrant_login_request_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }


    @RequestMapping(value = "/createFinalResults.htm", method = RequestMethod.GET)
    public ResponseEntity<String> createFinalResults() {
        StringBuilder result = new StringBuilder();
        //header
        result.append( "snils;oid;compaignId;dateOfBirth;specialty;financingType;targetReception;applicationDate;amountScore;testResult;individualAchievements;applicationStatus;admissionOrderNumber;admissionOrderDate;regulationsParagraph;diplomaIssueDate;diplomaSpecialty\n" );

        List<Entrant> entrants = entrantDao.findEnrollments();
        for( Entrant entrant : entrants ) {

            for ( Request request : entrant.getRequests() ) {
                EnrollmentResponse enrollmentResponse = request.getEnrollmentResponse();
                if ( enrollmentResponse != null && enrollmentResponse.isSuccess() ) {
                    continue;  // skip success
                }
                if ( entrant.getStatus() == EntrantStatus.ENFORCED && !entrant.isEnrollment() ) {
                    continue;
                }
                if ( request.getStatus() == RequestStatus.RETIRED || request.getStatus() == RequestStatus.TERMINATED ) {
                    continue;
                }
                int forceStatus = 0;
                if ( entrant.getExamInfo().getScore() == null || entrant.getExamInfo().getScore() == 0 ) {
                    forceStatus = 4;
                }

                result.append( String.format( "%011d", entrant.getSnilsNumber() ) ).append( ";" )
                        .append( propertyService.getProperty( StoredPropertyName.SYSTEM_OID ) ).append( ";" )
                        .append( propertyService.getProperty( StoredPropertyName.SYSTEM_CAMPAIGN_ID ) ).append( ";" );

                result.append( DATE_FORMAT.format( entrant.getBirthDate() ) ).append( ";" );

                result.append( request.getSpeciality() ).append( ";" )
                        .append( request.getFinancing() ).append( ";" )
                        .append( request.getTargetRequest() ).append( ";" )
                        .append( DATE_FORMAT.format( request.getApplicationDate() ) ).append( ";" );

                if ( forceStatus != 4 ) {
                    result.append( entrant.getExamInfo().getTotalScore() ).append( ";" )
                            .append( entrant.getExamInfo().getScore() == null ? 0 : entrant.getExamInfo().getScore() ).append( ";" )
                            .append( entrant.getExamInfo().getAchievements().replace( "г1", "г" ) ).append( ";" );
                } else {
                    result.append( "0;;;" );
                }

                if ( request.isEnrollment() ) {
                    result.append( "1;" )
                            .append( request.getEnrollmentOrder() ).append( ";" )
                            .append( DATE_FORMAT.format( request.getEnrollmentOrderDate() ) ).append( ";" );
                }
                else {
                    if ( request.getStatus() == RequestStatus.CONFIRMED ||
                            (entrant.getStatus() == EntrantStatus.ENFORCED && request.getStatus() == RequestStatus.REJECTED ) ) {
                        result.append( "2;;;" );
                    }
                    else if ( request.getStatus() == RequestStatus.REJECTED || forceStatus == 4 ) {
                        result.append( "4;;;" );
                    } else {
                        result.append( "3;;;" );
                    }
                }
                if ( !entrant.getCitizenship().equals( "643" ) ) {
                    if ( enrollmentResponse != null && enrollmentResponse.getResponse().equals( "Неверно указан пункт порядка приема" )) {
                        result.append( "63" );
                    } else {
                        result.append( "66" );
                    }
                }
                result.append( ";" )
                        .append( entrant.getDiplomaIssueDate() != null ? DATE_FORMAT.format( entrant.getDiplomaIssueDate()) : ""  ).append( ";" )
                        .append( entrant.getExamInfo().getSpeciality() ).append( "\n" );
            }


        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType( "text/csv; charset=utf-8" ) );
        String attachment = String.format("attachment; filename=\"enrollment_results_%s.csv\"", DATE_FORMAT.format( new Date() ) );
        headers.set( "Content-Disposition", attachment );
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK );

    }

}

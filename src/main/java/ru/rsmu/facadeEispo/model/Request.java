package ru.rsmu.facadeEispo.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name = "requests")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class Request implements Serializable {
    private static final long serialVersionUID = 6561641650310694003L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "entrant_id")
    private Entrant entrant;

    @Column
    private String speciality;

    @Column(name = "target_request")
    private String targetRequest;

    @Column
    private String financing;

    @Column(name = "application_date")
    @Temporal( TemporalType.DATE )
    private Date applicationDate;

    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @OneToOne
    @JoinColumn(name = "response_id")
    private EispoResponse response;

    @Column
    private boolean enrollment;

    @Column(name = "enrollment_order")
    private String enrollmentOrder;

    @Column(name = "enrollment_order_date")
    @Temporal( TemporalType.DATE )
    private Date enrollmentOrderDate;

    @ManyToOne
    @JoinColumn(name = "rinal_response_id")
    private EnrollmentResponse enrollmentResponse;

    public Request() {
    }

    public Request( Entrant entrant ) {
        this.entrant = entrant;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Entrant getEntrant() {
        return entrant;
    }

    public void setEntrant( Entrant entrant ) {
        this.entrant = entrant;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality( String speciality ) {
        this.speciality = speciality;
    }

    public String getTargetRequest() {
        return targetRequest;
    }

    public void setTargetRequest( String targetRequest ) {
        this.targetRequest = targetRequest;
    }

    public String getFinancing() {
        return financing;
    }

    public void setFinancing( String financing ) {
        this.financing = financing;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate( Date applicationDate ) {
        this.applicationDate = applicationDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus( RequestStatus status ) {
        this.status = status;
    }

    public EispoResponse getResponse() {
        return response;
    }

    public void setResponse( EispoResponse response ) {
        this.response = response;
    }

    public boolean isEnrollment() {
        return enrollment;
    }

    public void setEnrollment( boolean enrollment ) {
        this.enrollment = enrollment;
    }

    public String getEnrollmentOrder() {
        return enrollmentOrder;
    }

    public void setEnrollmentOrder( String enrollmentOrder ) {
        this.enrollmentOrder = enrollmentOrder;
    }

    public Date getEnrollmentOrderDate() {
        return enrollmentOrderDate;
    }

    public void setEnrollmentOrderDate( Date enrollmentOrderDate ) {
        this.enrollmentOrderDate = enrollmentOrderDate;
    }

    public EnrollmentResponse getEnrollmentResponse() {
        return enrollmentResponse;
    }

    public void setEnrollmentResponse( EnrollmentResponse enrollmentResponse ) {
        this.enrollmentResponse = enrollmentResponse;
    }

    public boolean equalsByName( Request another ) {
        boolean equals = true;
        if ( equals && !(( speciality == null && another.getSpeciality() == null ) || ( speciality != null && speciality.equalsIgnoreCase( another.getSpeciality() ))) ) {
            equals = false;
        }
        if ( equals && !(( targetRequest == null && another.getTargetRequest() == null ) || ( targetRequest != null && targetRequest.equalsIgnoreCase( another.getTargetRequest() ))) ) {
            equals = false;
        }
        if ( equals && !(( financing == null && another.getFinancing() == null ) || ( financing != null && financing.equalsIgnoreCase( another.getFinancing() ))) ) {
            equals = false;
        }
        return equals;
    }

    public boolean isValid() {

        return !StringUtils.isEmpty( speciality ) && !StringUtils.isEmpty( targetRequest ) && !StringUtils.isEmpty( financing ) &&
                applicationDate != null;
    }
}

package ru.rsmu.facadeEispo.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "entrants")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class Entrant implements Serializable {
    private static final long serialVersionUID = 3851774298385784905L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "case_number", unique = true )
    private Long caseNumber;

    @Column(name = "snils_number")
    private Long snilsNumber = null;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    @Temporal( TemporalType.DATE )
    private Date birthDate;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String citizenship;

    @Column(name = "diploma_date")
    @Temporal( TemporalType.DATE )
    private Date diplomaIssueDate;

    @OneToMany(mappedBy = "entrant")
    private List<Request> requests;

    @ManyToOne
    @JoinColumn(name = "deception_id")
    private Deception deception;

    @OneToOne(/*fetch = FetchType.EAGER*/)
    @JoinColumn(name = "exam_id")
    private ExamInfo examInfo;

    @Column
    @Enumerated(EnumType.STRING)
    private EntrantStatus status;

    public Entrant() {
        examInfo = new ExamInfo( this );
    }

    public Entrant( Long caseNumber ) {
        this.caseNumber = caseNumber;
        status = EntrantStatus.NEW;
        requests = new ArrayList<>( );
        examInfo = new ExamInfo( this );
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( Long caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public Long getSnilsNumber() {
        return snilsNumber;
    }

    public void setSnilsNumber( Long snilsNumber ) {
        this.snilsNumber = snilsNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate( Date birthDate ) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone( String phone ) {
        this.phone = phone;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship( String citizenship ) {
        this.citizenship = citizenship;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests( List<Request> requests ) {
        this.requests = requests;
    }

    public Deception getDeception() {
        return deception;
    }

    public void setDeception( Deception deception ) {
        this.deception = deception;
    }

    public EntrantStatus getStatus() {
        return status;
    }

    public void setStatus( EntrantStatus status ) {
        this.status = status;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo( ExamInfo examInfo ) {
        this.examInfo = examInfo;
    }

    public Date getDiplomaIssueDate() {
        return diplomaIssueDate;
    }

    public void setDiplomaIssueDate( Date diplomaIssueDate ) {
        this.diplomaIssueDate = diplomaIssueDate;
    }

    public boolean equalsByName( Entrant another ) {
        boolean equals = true;
        if ( equals && !(( firstName == null && another.getFirstName() == null ) || ( firstName != null && firstName.equalsIgnoreCase( another.getFirstName() ))) ) {
            equals = false;
        }
        if ( equals && !(( lastName == null && another.getLastName() == null ) || ( lastName != null && lastName.equalsIgnoreCase( another.getLastName() ))) ) {
            equals = false;
        }
        if ( equals && !(( middleName == null && another.getMiddleName() == null ) || ( middleName != null && middleName.equalsIgnoreCase( another.getMiddleName() ))) ) {
            equals = false;
        }
        if ( equals && !(( birthDate == null && another.getBirthDate() == null ) || ( birthDate != null && birthDate.equals( another.getBirthDate() ))) ) {
            equals = false;
        }
        return equals;
    }

    public Request findRequestByName( Request request ) {
        for ( Request myRequest : requests ) {
            if ( myRequest.equalsByName( request ) ) {
                return myRequest;
            }
        }
        return null;
    }

    public boolean isValid() {
        boolean valid = true;

        for ( Request request : requests ) {
            valid = valid && request.isValid();
        }
        return valid && examInfo.isValid() &&
                birthDate != null && snilsNumber != null && snilsNumber != 0 &&
                !StringUtils.isEmpty( citizenship );
    }

    public boolean isEnrollment() {
        if ( requests != null ) {
            for ( Request request : requests ) {
                if ( request.isEnrollment() ) return true;
            }
        }
        return false;
    }

    public boolean isSnilsValid() {
        if ( snilsNumber == null ) {
            return false;
        }
        long numbers = snilsNumber / 100;
        if ( numbers <= 1001998L ) return true;

        String strSnils = String.format( "%011d", snilsNumber );
        String strNumbers = strSnils.substring( 0, 9 );
        String strCheckSum = strSnils.substring( 9 );
        long calculation = 0;
        for ( int i = 0; i < 9; i++ ) {
            calculation += ( 9 - i ) * Long.parseLong( Character.toString(strNumbers.charAt( i )) );
        }
        calculation = calculation % 101;
        calculation = calculation < 100 ? calculation : 0;
        return strCheckSum.equals( String.format( "%02d", calculation ) );
    }

    public boolean isBudgetOrRussian() {
        if ( citizenship.equals( "643" ) ) {
            return true;
        }
        for ( Request request : requests ) {
            if ( request.getFinancing().equals( "бюджет" ) ) return true;
        }
        return false;
    }

    @Transient
    public String getErrorInfo() {
        Request request  = requests.stream().filter( re -> !re.getResponse().isSuccess() )
                .findAny()
                .orElse( null );
        return request != null ? request.getResponse().getResponse() : "";
    }
}

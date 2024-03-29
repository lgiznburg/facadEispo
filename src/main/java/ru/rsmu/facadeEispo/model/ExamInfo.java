package ru.rsmu.facadeEispo.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "exam_info")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class ExamInfo implements Serializable {
    private static final long serialVersionUID = 1002972230654061527L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "examInfo")
    private Entrant entrant;

    @Column
    private String speciality;

    @Column
    private String type;

    @Column
    private String organization;

    @Column
    private String year;

    @Column
    private Integer score = 0;

    @Column
    private String response;

    @Column(name = "scheduled_date")
    @Temporal( TemporalType.DATE )
    private Date scheduledDate;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column
    private String achievements;

    public ExamInfo() {
    }

    public ExamInfo( Entrant entrant ) {
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

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization( String organization ) {
        this.organization = organization;
    }

    public String getYear() {
        return year;
    }

    public void setYear( String year ) {
        this.year = year;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore( Integer score ) {
        this.score = score;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse( String response ) {
        this.response = response;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate( Date scheduledDate ) {
        this.scheduledDate = scheduledDate;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore( Integer totalScore ) {
        this.totalScore = totalScore;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements( String achievements ) {
        this.achievements = achievements;
    }

    public boolean equalsByName( ExamInfo another ) {
        boolean equals = true;
        if ( equals && !(( type == null && another.getType() == null ) || ( type != null && type.equalsIgnoreCase( another.getType() ))) ) {
            equals = false;
        }
        if ( equals && !(( organization == null && another.getOrganization() == null ) || ( organization != null && organization.equalsIgnoreCase( another.getOrganization() ))) ) {
            equals = false;
        }
        if ( equals && !(( year == null && another.getYear() == null ) || ( year != null && year.equalsIgnoreCase( another.getYear() ))) ) {
            equals = false;
        }
        return equals;
    }

    public void update( ExamInfo examInfo ) {
        type = examInfo.getType();
        organization = examInfo.getOrganization();
        year = examInfo.getYear();
    }

    public boolean isValid() {
        return !StringUtils.isEmpty( type ) && !StringUtils.isEmpty( year ) && !StringUtils.isEmpty( organization )
                && organization.matches( "1\\.2\\.643\\.5\\.1\\.13\\.13\\.12\\.4(\\.\\d+){2}" );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ExamInfo) ) return false;
        ExamInfo examInfo = (ExamInfo) o;
        return id == examInfo.id && entrant.equals( examInfo.entrant ) && Objects.equals( speciality, examInfo.speciality ) && Objects.equals( type, examInfo.type ) && Objects.equals( organization, examInfo.organization ) && Objects.equals( year, examInfo.year ) && Objects.equals( score, examInfo.score ) && Objects.equals( response, examInfo.response ) && Objects.equals( scheduledDate, examInfo.scheduledDate ) && Objects.equals( totalScore, examInfo.totalScore ) && Objects.equals( achievements, examInfo.achievements );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, entrant, speciality, type, organization, year, score, response, scheduledDate, totalScore, achievements );
    }
}

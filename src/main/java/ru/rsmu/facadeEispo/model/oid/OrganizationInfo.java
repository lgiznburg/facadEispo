package ru.rsmu.facadeEispo.model.oid;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table( name = "oid_info" )
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class OrganizationInfo implements Serializable, Comparable<OrganizationInfo> {
    private static final long serialVersionUID = 60939805395975986L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "full_name", columnDefinition = "text" )
    private String fullName;

    @Column( name = "short_name", columnDefinition = "text")
    private String shortName;

    @Column( name = "name_change_date" )
    @Temporal( TemporalType.DATE )
    private Date nameChangeDate;

    @Column( name = "name_end_date" )
    @Temporal( TemporalType.DATE )
    private Date nameEndDate;

    @Column
    private String oid;

    @Column( name = "subject_code" )
    private int subjectCode;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    public Date getNameChangeDate() {
        return nameChangeDate;
    }

    public void setNameChangeDate( Date nameChangeDate ) {
        this.nameChangeDate = nameChangeDate;
    }

    public Date getNameEndDate() {
        return nameEndDate;
    }

    public void setNameEndDate( Date nameEndDate ) {
        this.nameEndDate = nameEndDate;
    }

    public String getOid() {
        return oid;
    }

    public void setOid( String oid ) {
        this.oid = oid;
    }

    public int getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode( int subjectCode ) {
        this.subjectCode = subjectCode;
    }

    @Transient
    public int[] getLastOidDigits() {
        String[] parts = oid.split( "\\." );
        int[] result = new int[2];
        int size = parts.length;
        result[0] = Integer.parseInt( parts[size-2] );
        result[1] = Integer.parseInt( parts[size-1] );
        return result;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof OrganizationInfo) ) return false;
        OrganizationInfo that = (OrganizationInfo) o;
        return id.equals( that.id ) &&
                fullName.equals( that.fullName ) &&
                shortName.equals( that.shortName ) &&
                nameChangeDate.equals( that.nameChangeDate ) &&
                Objects.equals( nameEndDate, that.nameEndDate ) &&
                oid.equals( that.oid );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, fullName, shortName, nameChangeDate, nameEndDate, oid );
    }

    @Override
    public int compareTo( OrganizationInfo o ) {
        int[] codes = getLastOidDigits();
        int[] o_codes = o.getLastOidDigits();
        return codes[0] - o_codes[0] == 0 ? codes[1] - o_codes[1] : codes[0] - o_codes[0];
        //return oid.compareTo( o.getOid() );
    }
}

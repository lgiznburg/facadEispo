package ru.rsmu.facadeEispo.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "enrollment_responses")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class EnrollmentResponse implements Serializable {
    private static final long serialVersionUID = -5059354113686366383L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private boolean success = false;

    @Column( name = "response", columnDefinition = "text")
    private String response;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess( boolean success ) {
        this.success = success;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse( String response ) {
        this.response = response;
    }
}

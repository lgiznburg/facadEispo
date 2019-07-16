package ru.rsmu.facadeEispo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name = "responses")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class EispoResponse implements Serializable {
    private static final long serialVersionUID = 2050604278021223482L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "response")
    private Request request;

    @Column
    private boolean success;

    @Column(name = "full_response")
    private String response;

    @Column
    private Date date;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest( Request request ) {
        this.request = request;
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

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }
}

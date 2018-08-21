package ru.rsmu.facadeEispo.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "login_info")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class LoginInfo implements Serializable {

    private static final long serialVersionUID = -2925197476965140283L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "entrant_id")
    private Entrant entrant;

    @Column
    private String login;

    @Column
    private String password;

    @Column
    private boolean success;

    @Column
    private String info;

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

    public String getLogin() {
        return login;
    }

    public void setLogin( String login ) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess( boolean success ) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
    }
}

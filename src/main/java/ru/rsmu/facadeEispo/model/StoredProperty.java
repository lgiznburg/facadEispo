package ru.rsmu.facadeEispo.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "stored_properties")
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class StoredProperty implements Serializable, Comparable<StoredProperty> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "property_name")
    @Enumerated(EnumType.STRING)
    private StoredPropertyName propertyName;

    @Column
    private String value;

    public StoredProperty() {
    }

    public StoredProperty( StoredPropertyName propertyName, String value ) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public StoredPropertyName getPropertyName() {
        return propertyName;
    }

    public void setPropertyName( StoredPropertyName propertyName ) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public int compareTo( StoredProperty o ) {
        int compare = this.getPropertyName().getGroupName().compareTo( o.getPropertyName().getGroupName());
        if ( compare != 0 ) return compare;
        compare = this.getPropertyName().getName().compareTo( o.getPropertyName().getName() );
        return compare != 0 ? compare : this.getValue().compareTo( o.getValue() );
    }
}

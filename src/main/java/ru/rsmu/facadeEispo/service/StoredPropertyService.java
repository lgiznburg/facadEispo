package ru.rsmu.facadeEispo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rsmu.facadeEispo.dao.StoredPropertyDao;
import ru.rsmu.facadeEispo.model.StoredProperty;
import ru.rsmu.facadeEispo.model.StoredPropertyName;
import ru.rsmu.facadeEispo.model.StoredPropertyType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author leonid.
 */
@Service
public class StoredPropertyService {

    static final private SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
    static final private SimpleDateFormat timeFormat = new SimpleDateFormat( "hh:mm" );

    @Autowired
    private StoredPropertyDao storedPropertyDao;

    private HashMap<StoredPropertyName, String> properties = new HashMap<>();


    public void buildPropertiesMap() {
        List<StoredProperty> storedProperties = storedPropertyDao.findAll();
        for ( StoredProperty property : storedProperties ) {
            properties.put( property.getPropertyName(), property.getValue() );
        }
    }

    public String getProperty( StoredPropertyName propertyName ) {
        if ( properties.isEmpty() ) {
            buildPropertiesMap();
        }
        String propertyValue = properties.get( propertyName );
        return propertyValue != null ? propertyValue : propertyName.getDefaultValue();
    }

    public Integer getPropertyAsInt( StoredPropertyName propertyName ) {
        final String prop = this.getProperty( propertyName );
        try {
            return Integer.parseInt( prop );
        } catch (NumberFormatException e) {
            try {  //protection against foolish user
                return Integer.parseInt( propertyName.getDefaultValue() );
            } catch (NumberFormatException e2) {
                return null; // this is fatal
            }
        }
    }

    public Long getPropertyAsLong( StoredPropertyName propertyName ) {
        final String prop = this.getProperty( propertyName );
        try {
            return Long.parseLong( prop );
        } catch (NumberFormatException e) {
            try {  //protection against foolish user
                return Long.parseLong( propertyName.getDefaultValue() );
            } catch (NumberFormatException e2) {
                return null; // this is fatal
            }
        }
    }


    public void saveProperty( StoredProperty property ) {
        storedPropertyDao.saveEntity( property );
        properties.put( property.getPropertyName(), property.getValue() );
    }

    public Date getPropertyAsDate( StoredPropertyName name ) {
        if ( !name.getType().equals( StoredPropertyType.DATE )
                && !name.getType().equals( StoredPropertyType.TIME ) ) {
            return null;
        }
        String value = getProperty( name );
        Date result = null;
        try {
            switch ( name.getType() ) {
                case DATE :
                    result = dateFormat.parse( value );
                    break;
                case TIME:
                    result = timeFormat.parse( value );
                    break;
                default:
            }
        } catch (ParseException e) {
            //nothing, return null
        }

        return result;
    }

/*
    public <T> T getProperty( StoredPropertyName name, Class<T> tClass) {
        String stringProperty = getProperty( name );
        if ( tClass.equals( String.class ) ) {
            return (T) stringProperty;
        }
        if ( tClass.isAssignableFrom( Date.class ) ) {

        }
        if ( tClass.isAssignableFrom( Integer.class ) ) {

        }
        if ( tClass.isAssignableFrom( Long.class ) ) {
            try {
                return (T) Long.decode( stringProperty );
            } catch (NumberFormatException e) {
                try {  //protection against foolish user
                    return Long.parseLong( propertyName.getDefaultValue() );
                } catch (NumberFormatException e2) {
                    return null; // this is fatal
                }
            }

        }

    }
*/
}

package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.rsmu.facadeEispo.model.StoredProperty;
import ru.rsmu.facadeEispo.model.StoredPropertyName;

import java.util.List;

/**
 * @author leonid.
 */
@Repository
public class StoredPropertyDao extends CommonDao {

    public List<StoredProperty> findAll() {
        return findAllEntities( StoredProperty.class );
    }

    public StoredProperty findByName( StoredPropertyName propertyName ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( StoredProperty.class )
                .add( Restrictions.eq( "propertyName", propertyName ) )
                .setMaxResults( 1 );
        return (StoredProperty) criteria.uniqueResult();
    }
}

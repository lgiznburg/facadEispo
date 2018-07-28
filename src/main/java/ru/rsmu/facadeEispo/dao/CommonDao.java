package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author leonid.
 */
@Repository
public abstract class CommonDao extends HibernateDaoSupport {

    @Autowired
    public void setPersistentResource( SessionFactory sessionFactory ) {
        super.setSessionFactory( sessionFactory );
    }

    public void saveEntity( Object object) {
        try {
            getHibernateTemplate().saveOrUpdate( object );
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public <T> T findEntity( Class<T> entityClass, long id  ) {
        return getHibernateTemplate().get( entityClass, id );
    }


    public void deleteEntity( Object object ) {
        getHibernateTemplate().delete( object );
    }

    public  <T> List<T> findAllEntities( Class<T> entityClass ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( entityClass );
        return  criteria.list();
    }

    public void saveAllEntities( List objects ) {
        for ( Object object : objects ) {
            getHibernateTemplate().saveOrUpdate( object );
        }
    }


}

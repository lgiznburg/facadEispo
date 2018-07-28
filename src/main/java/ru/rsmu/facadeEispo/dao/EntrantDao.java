package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.EntrantStatus;
import ru.rsmu.facadeEispo.model.RequestStatus;

import java.util.List;

/**
 * @author leonid.
 */
@Repository
public class EntrantDao extends CommonDao {


    public Entrant findEntrantByCaseNumber( Long caseNumber ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.eq( "caseNumber", caseNumber ) )
                .setMaxResults( 1 );

        return (Entrant) criteria.uniqueResult();
    }

    public Entrant findEntrantBySnilsNumber( Long snils ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.eq( "snilsNumber", snils ) )
                .setMaxResults( 1 );

        return (Entrant) criteria.uniqueResult();
    }

    public List<Entrant> findNewEntrants() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.disjunction()
                                .add( Restrictions.eq( "status", EntrantStatus.NEW ) )
                                .add( Restrictions.eq( "status", EntrantStatus.UPDATED ) )
                );
        return criteria.list();
    }

    public List<Entrant> findEntrantsWithError() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.eq( "status", EntrantStatus.SUBMITTED ) )
                .createAlias( "requests", "request" )
                .add( Restrictions.eq( "request.status", RequestStatus.REJECTED ) )
                .setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );

        return criteria.list();
    }
}

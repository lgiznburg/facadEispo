package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.rsmu.facadeEispo.actions.SearchForm;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.EntrantStatus;
import ru.rsmu.facadeEispo.model.LoginInfo;
import ru.rsmu.facadeEispo.model.RequestStatus;

import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Repository
@SuppressWarnings( "unchecked" )
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
                .addOrder( Order.asc( "lastName" ) )
                .setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );

        return criteria.list();
    }

    public List<Entrant> findEntrantsForScores() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.disjunction()
                        .add( Restrictions.eq( "status", EntrantStatus.SUBMITTED ) )
                        .add( Restrictions.eq( "status", EntrantStatus.ENFORCED ) )
                )
                .createAlias( "examInfo", "examInfo" )
                .add( Restrictions.or( Restrictions.eq( "examInfo.score", 0 ), Restrictions.isNull( "examInfo.score" ) ) );

        return criteria.list();
    }

    public List<Entrant> findEntrantsWithScore() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.or( Restrictions.eq( "status", EntrantStatus.SUBMITTED ), Restrictions.eq( "status", EntrantStatus.ENFORCED ) ) )
                .createAlias( "examInfo", "examInfo" )
                .add( Restrictions.gt( "examInfo.score", 0 ) );

        return criteria.list();
    }

    public List<Entrant> findBySearch( SearchForm searchForm ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class );
        if ( !searchForm.isEmptyName() ) {
            criteria.add( Restrictions.like( "lastName", "%" + searchForm.getLastName() + "%" ) );
        }
        if ( searchForm.getCase() > 0 ) {
            criteria.add( Restrictions.eq( "caseNumber", searchForm.getCase() ) );
        }
        if ( searchForm.getSnils() > 0 ) {
            criteria.add( Restrictions.eq( "snilsNumber", searchForm.getSnils() ) );
        }

        return criteria.list();
    }

    public List<Entrant> findEntrantsForLogin( String ourOID, String year ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.disjunction()
                                .add( Restrictions.eq( "status", EntrantStatus.SUBMITTED ) )
                                .add( Restrictions.eq( "status", EntrantStatus.ENFORCED ) )
                )
                .createAlias( "examInfo", "examInfo" )
                .add( Restrictions.eq( "examInfo.type", "ординатура" ) )
                .add( Restrictions.eq( "examInfo.organization", ourOID ) )
                .add( Restrictions.eq( "examInfo.year", year ) );
        return criteria.list();
    }

    public LoginInfo findLoginInfo( Entrant entrant ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( LoginInfo.class )
                .add( Restrictions.eq( "entrant", entrant ) )
                .setMaxResults( 1 );
        return (LoginInfo) criteria.uniqueResult();
    }

    public List<LoginInfo> findAllLoginInfo() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( LoginInfo.class )
                .add( Restrictions.eq( "success", true ) );
        return criteria.list();
    }

    public List<LoginInfo> findAllLoginInfo( boolean successOnly ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( LoginInfo.class )
                .createAlias( "entrant", "entrant" )
                .createAlias( "entrant.examInfo", "examInfo" )
                .add( Restrictions.gt( "examInfo.scheduledDate", new Date() ) );

        if ( successOnly ) {
            criteria.add( Restrictions.eq( "success", true ) );
        }
        return criteria.list();
    }


    public List<Entrant> findScoresErrors() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Entrant.class )
                .add( Restrictions.or( Restrictions.eq( "status", EntrantStatus.SUBMITTED ), Restrictions.eq( "status", EntrantStatus.ENFORCED ) ) )
                .createAlias( "examInfo", "examInfo" )
                .add( Restrictions.isNull( "examInfo.score" ) );

        return criteria.list();
    }
}

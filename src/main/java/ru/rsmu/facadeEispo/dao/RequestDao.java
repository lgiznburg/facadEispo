package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.rsmu.facadeEispo.model.EntrantStatus;
import ru.rsmu.facadeEispo.model.Request;
import ru.rsmu.facadeEispo.model.RequestStatus;

import java.util.List;

/**
 * @author leonid.
 */
@Repository
public class RequestDao extends CommonDao {


    public List<Request> findRequestsForExport() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Request.class )
                .createAlias( "entrant", "entrant" )
                .add( Restrictions.disjunction()
                                .add( Restrictions.eq( "status", RequestStatus.NEW ) )
                                .add( Restrictions.eq( "entrant.status", EntrantStatus.NEW ) )
                                .add( Restrictions.eq( "entrant.status", EntrantStatus.UPDATED ) )
                );

        return criteria.list();
    }

    public List<Request> findWithdrawalRequests() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( Request.class )
                .createAlias( "entrant", "entrant" )
                .add(  Restrictions.eq( "status", RequestStatus.RETIRED ) );

        return criteria.list();
    }
}

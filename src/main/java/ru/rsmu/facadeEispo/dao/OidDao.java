package ru.rsmu.facadeEispo.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.rsmu.facadeEispo.model.oid.OrganizationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
@Repository
public class OidDao extends CommonDao {

    public int deleteAllOid() {
        Query query = getSessionFactory().getCurrentSession().createQuery( "DELETE FROM OrganizationInfo" );
        return query.executeUpdate();
    }

    @SuppressWarnings( "unchecked" )
    public List<OrganizationInfo> findFor( String textToSearch ) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria( OrganizationInfo.class );
        if ( textToSearch.trim().matches( "[\\d.]+" ) && textToSearch.trim().length() > 2 ) {
            criteria.add( Restrictions.like( "oid", "%"+ textToSearch.trim() + "%" ) );
        } else {
            criteria.add( Restrictions.or(
                    Restrictions.like( "fullName", "%"+ textToSearch.trim() + "%" ),
                    Restrictions.like( "shortName", "%"+ textToSearch.trim() + "%" )
            ) );
        }
        criteria.addOrder( Order.asc( "oid" ) )
                .setMaxResults( 100 );
        return criteria.list();
    }

    public OrganizationInfo getOrgInfo( String textToSearch ) {
        List<OrganizationInfo> results = findFor( textToSearch );
        OrganizationInfo infoResult = null;
        for ( OrganizationInfo info : results ) {
            if ( infoResult == null ) {
                infoResult = info ;
            }
            else if ( infoResult.getNameEndDate() != null &&
                    ( info.getNameEndDate() == null || info.getNameEndDate().after( infoResult.getNameEndDate() )) ) {
                infoResult = info ;
            }
        }
        return infoResult;
    }
}

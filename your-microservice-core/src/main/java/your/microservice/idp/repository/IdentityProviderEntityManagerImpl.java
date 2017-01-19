package your.microservice.idp.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.base.YourEntityTokenHistory;
import your.microservice.idp.model.types.YourEntityTokenStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jaschenk
 * Date: 1/16/17
 * Time: 8:54 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional(readOnly = true)
public class IdentityProviderEntityManagerImpl implements IdentityProviderEntityManager {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public YourEntityTokenHistory createTokenHistory(YourEntityTokenHistory yourEntityTokenHistory) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public YourEntityTokenHistory readTokenHistory(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<YourEntityTokenHistory> readTokenHistoryBySubject(String subject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<YourEntityTokenHistory> readCurrentExpiredTokenHistory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<YourEntityTokenHistory> readTokenHistory(Map<String, Object> queryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer updateTokenHistoryStatus(String jti, YourEntityTokenStatus status) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer incrementTokenHistoryUsage(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer deleteTokenHistory(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer deleteTokenHistoryBySubject(String subject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public YourEntity findByEmail(String email) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityEmailAddress"),email));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();

    }

    @Override
    public List<YourEntity> findAll() {


        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);

        return  entityManager.createQuery(criteriaQuery).getResultList();

    }

}

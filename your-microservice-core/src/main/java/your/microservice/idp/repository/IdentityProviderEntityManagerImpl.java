package your.microservice.idp.repository;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.base.YourEntityOrganization;
import your.microservice.idp.model.base.YourEntityRole;
import your.microservice.idp.model.base.YourEntityTokenHistory;
import your.microservice.idp.model.types.YourEntityTokenStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.time.Instant;
import java.util.Date;
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
@Transactional
public class IdentityProviderEntityManagerImpl implements IdentityProviderEntityManager {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdentityProviderEntityManagerImpl.class);


    /**
     * Entity Manager
     */
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public YourEntityTokenHistory createTokenHistory(YourEntityTokenHistory yourEntityTokenHistory) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public YourEntityTokenHistory readTokenHistory(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readTokenHistoryBySubject(String subject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readCurrentExpiredTokenHistory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readTokenHistory(Map<String, Object> queryParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public Integer updateTokenHistoryStatus(String jti, YourEntityTokenStatus status) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public Integer incrementTokenHistoryUsage(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public Integer deleteTokenHistory(String jti) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional
    public Integer deleteTokenHistoryBySubject(String subject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public YourEntity findYourEntityById(Long entityId) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityId"),entityId));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();

    }

    @Override
    @Transactional(readOnly = true)
    public YourEntity findYourEntityByEmail(String email) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityEmailAddress"),email));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();

    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntity> findAllYourEntities() {


        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);

        return  entityManager.createQuery(criteriaQuery).getResultList();

    }

    @Override
    @Transactional(readOnly = true)
    public YourEntityOrganization findYourEntityOrganizationById(Long entityOrgId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityOrgId"),entityOrgId));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityOrganization findYourEntityOrganizationByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("name"),name));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityOrganization> findAllYourEntityOrganizations() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);

        return  entityManager.createQuery(criteriaQuery).getResultList();
    }



    @Override
    @Transactional(readOnly = true)
    public YourEntityRole findYourEntityRoleById(Long entityRoleId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityRoleId"),entityRoleId));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityRole findYourEntityRoleByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("name"),name));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityRole> findAllYourEntityRoles() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);

        return  entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional
    public void saveYourEntityRole(YourEntityRole yourEntityRole) {
        try {
        yourEntityRole.setCreatedByDate(Date.from(Instant.now()));
        yourEntityRole.setCreatedByIdentifier("SYSTEM");
        yourEntityRole.setUpdatedByDate(Date.from(Instant.now()));
        yourEntityRole.setUpdatedByIdentifier("SYSTEM");
        entityManager.persist(yourEntityRole);
        entityManager.flush();
        } catch(Exception e) {
           LOGGER.error("Exception Saving YourEntityRole: {}",e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public void saveYourEntity(YourEntity yourEntity) {
        yourEntity.setUpdatedByDate(Date.from(Instant.now()));
        yourEntity.setUpdatedByIdentifier("SYSTEM");
        entityManager.persist(yourEntity);
        entityManager.flush();
    }

    @Override
    @Transactional
    public void saveYourEntityOrganization(YourEntityOrganization yourEntityOrganization) {
        yourEntityOrganization.setUpdatedByDate(Date.from(Instant.now()));
        yourEntityOrganization.setUpdatedByIdentifier("SYSTEM");
        entityManager.persist(yourEntityOrganization);
        entityManager.flush();
    }
}

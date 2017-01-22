package your.microservice.idp.repository;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import your.microservice.idp.model.base.*;
import your.microservice.idp.model.types.YourEntityTokenStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import java.time.Instant;
import java.util.ArrayList;
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
        try {
            entityManager.persist(yourEntityTokenHistory);
            entityManager.flush();
        } catch(Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}",e.getMessage(), yourEntityTokenHistory, e);
            return null;
        }
        return readTokenHistory(yourEntityTokenHistory.getJti());
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityTokenHistory readTokenHistory(String jti) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
        final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("jti"),jti));

        return  entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readTokenHistoryBySubject(String subject) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
        final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("subject"),subject));

        return  entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readCurrentExpiredTokenHistory() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
        final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);

        // Create Date path and parameter expressions:
        Expression<Date> expiration = yourEntityRoot.get("expiration");

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.lessThanOrEqualTo(expiration, criteriaBuilder.currentTimestamp()));

        return  entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional
    public Boolean updateTokenHistoryStatus(String jti, YourEntityTokenStatus status) {
        try
        {
            entityManager.createNativeQuery("UPDATE YourEntityTokenHistory SET status = ? WHERE jti = ?", YourEntityTokenHistory.class)
                    .setParameter(1, status.name())
                    .setParameter(2, jti)
                    .executeUpdate();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean incrementTokenHistoryUsage(String jti) {

        try
        {
            entityManager.createNativeQuery("UPDATE YourEntityTokenHistory SET usageCount = usageCount + 1 WHERE jti = ? AND expiration > ?", YourEntityTokenHistory.class)
                    .setParameter(1, jti)
                    .setParameter(2, Date.from(Instant.now()))
                    .executeUpdate();
            entityManager.flush();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
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
    @Transactional
    public void createEventHistory(YourEntityEventHistory yourEntityEventHistory) {
        try {
            entityManager.persist(yourEntityEventHistory);
            entityManager.flush();
        } catch(Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}",e.getMessage(), yourEntityEventHistory, e);
        }
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
            if (yourEntityRole.getEntityRoleId() == null) {
                yourEntityRole.setCreatedByDate(Date.from(Instant.now()));
                yourEntityRole.setCreatedByIdentifier("SYSTEM");
            }
            yourEntityRole.setUpdatedByDate(Date.from(Instant.now()));
            yourEntityRole.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntityRole);
            entityManager.flush();
        } catch(Exception e) {
           LOGGER.error("Exception Saving YourEntityRole: {} {}",e.getMessage(), yourEntityRole, e);
        }
    }


    @Override
    @Transactional
    public void saveYourEntity(YourEntity yourEntity) {
        try {
            if (yourEntity.getEntityId() == null) {
                yourEntity.setCreatedByDate(Date.from(Instant.now()));
                yourEntity.setCreatedByIdentifier("SYSTEM");
            }
            yourEntity.setUpdatedByDate(Date.from(Instant.now()));
            yourEntity.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntity);
            entityManager.flush();
        } catch(Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}",e.getMessage(), yourEntity, e);
        }

    }

    @Override
    @Transactional
    public void saveYourEntityOrganization(YourEntityOrganization yourEntityOrganization) {
        try {
            if (yourEntityOrganization.getEntityOrgId() == null) {
                yourEntityOrganization.setCreatedByDate(Date.from(Instant.now()));
                yourEntityOrganization.setCreatedByIdentifier("SYSTEM");
            }
            yourEntityOrganization.setUpdatedByDate(Date.from(Instant.now()));
            yourEntityOrganization.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntityOrganization);
            entityManager.flush();
        } catch(Exception e) {
            LOGGER.error("Exception Saving YourEntityOrganization: {} {}",e.getMessage(), yourEntityOrganization, e);
        }
    }
}

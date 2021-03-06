package your.microservice.core.security.idp.repository;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import your.microservice.core.security.idp.model.base.*;
import your.microservice.core.security.idp.model.types.YourEntityTokenStatus;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

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
        } catch (Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}", e.getMessage(), yourEntityTokenHistory, e);
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
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("jti"), jti));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readTokenHistoryBySubject(String subject) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
        final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("subject"), subject));

        return entityManager.createQuery(criteriaQuery).getResultList();
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

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityTokenHistory> readCurrentNonExpiredTokenHistory() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
        final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);

        // Create Date path and parameter expressions:
        Expression<Date> expiration = yourEntityRoot.get("expiration");

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.greaterThan(expiration, criteriaBuilder.currentTimestamp()));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    @Override
    @Transactional
    public Integer updateTokenHistoryStatus(String jti, YourEntityTokenStatus status) {
        try {
            Integer count =
                    entityManager.createNativeQuery("UPDATE YourEntityTokenHistory SET status = ? WHERE jti = ?", YourEntityTokenHistory.class)
                    .setParameter(1, status.name())
                    .setParameter(2, jti)
                    .executeUpdate();

            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    @Transactional
    public Integer incrementTokenHistoryUsage(String jti) {

        try {
            Integer count = entityManager.createNativeQuery("UPDATE YourEntityTokenHistory SET usageCount = usageCount + 1 WHERE jti = ? AND expiration > ?", YourEntityTokenHistory.class)
                    .setParameter(1, jti)
                    .setParameter(2, Date.from(Instant.now()))
                    .executeUpdate();
            entityManager.flush();

            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * deleteTokenHistory
     *
     * @return Integer Number of Elements deleted or Zero.
     */
    @Override
    @Transactional
    public Integer deleteTokenHistory() {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaDelete<YourEntityTokenHistory> delete = criteriaBuilder.createCriteriaDelete(YourEntityTokenHistory.class);
            final Root<YourEntityTokenHistory> yourEntityRoot = delete.from(YourEntityTokenHistory.class);

            // Create Date path and parameter expressions:
            Expression<Date> expiration = yourEntityRoot.get("expiration");
            delete.where(criteriaBuilder.lessThanOrEqualTo(expiration, criteriaBuilder.currentTimestamp()));

            Integer count = entityManager.createQuery(delete).executeUpdate();
            entityManager.flush();
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteTokenHistory: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * purgeTokenHistory
     *
     * @return Integer Number of Elements deleted or Zero.
     */
    @Override
    @Transactional
    public Integer purgeTokenHistory() {
        Integer count = 0;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<YourEntityTokenHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityTokenHistory.class);
            final Root<YourEntityTokenHistory> yourEntityRoot = criteriaQuery.from(YourEntityTokenHistory.class);
            /**
             * Select All Entities...
             */
            criteriaQuery.select(yourEntityRoot);
            List<YourEntityTokenHistory> results = entityManager.createQuery(criteriaQuery).getResultList();
            if (results != null && results.size() > 0) {
                for(YourEntityTokenHistory entity : results) {
                    count += deleteTokenHistory(entity.getJti());
              }
            }
              return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteTokenHistory: {}", e.getMessage(), e);
            return count;
        }
    }

    @Override
    @Transactional
    public Integer deleteTokenHistory(String jti) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaDelete<YourEntityTokenHistory> delete = criteriaBuilder.createCriteriaDelete(YourEntityTokenHistory.class);
            final Root<YourEntityTokenHistory> yourEntityRoot = delete.from(YourEntityTokenHistory.class);

            // Create Date path and parameter expressions:
            Expression<String> jtiExpression = yourEntityRoot.get("jti");
            delete.where(criteriaBuilder.equal(jtiExpression, jti));

            Integer count = entityManager.createQuery(delete).executeUpdate();
            entityManager.flush();
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteTokenHistory using JTI:[{}] {}", jti, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public Integer deleteTokenHistoryBySubject(String subject) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaDelete<YourEntityTokenHistory> delete = criteriaBuilder.createCriteriaDelete(YourEntityTokenHistory.class);
            final Root<YourEntityTokenHistory> yourEntityRoot = delete.from(YourEntityTokenHistory.class);

            // Create Date path and parameter expressions:
            Expression<Date> subjectExpression = yourEntityRoot.get("subject");
            delete.where(criteriaBuilder.equal(subjectExpression, subject));

            Integer count = entityManager.createQuery(delete).executeUpdate();
            entityManager.flush();
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteTokenHistory using SUBJECT:[{}] {}", subject, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public void createEventHistory(YourEntityEventHistory yourEntityEventHistory) {
        if (yourEntityEventHistory == null) {
            return;
        }
        try {
            yourEntityEventHistory.setCreatedByDate(Date.from(Instant.now()));
            entityManager.persist(yourEntityEventHistory);
            entityManager.flush();
        } catch (Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}", e.getMessage(), yourEntityEventHistory, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityEventHistory> findAllYourEntityEventHistory(Long entityId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityEventHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityEventHistory.class);
        final Root<YourEntityEventHistory> yourEntityRoot = criteriaQuery.from(YourEntityEventHistory.class);
        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("yourEntity"), entityId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityEventHistory> findAllYourEntityEventHistory() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityEventHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityEventHistory.class);
        final Root<YourEntityEventHistory> yourEntityRoot = criteriaQuery.from(YourEntityEventHistory.class);
        criteriaQuery.select(yourEntityRoot);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * deleteEventHistory
     * Will delete all of Event History, used by Admin Only.
     *
     * @return Integer Count of Objects Deleted or Zero.
     */
    @Override
    @Transactional
    public Integer deleteEventHistory() {
        Integer count = 0;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<YourEntityEventHistory> criteriaQuery = criteriaBuilder.createQuery(YourEntityEventHistory.class);
            final Root<YourEntityEventHistory> yourEntityRoot = criteriaQuery.from(YourEntityEventHistory.class);
            /**
             * Select All Entities...
             */
            criteriaQuery.select(yourEntityRoot);
            List<YourEntityEventHistory> results = entityManager.createQuery(criteriaQuery).getResultList();
            if (results != null && results.size() > 0) {
                for(YourEntityEventHistory entity : results) {

                    entityManager.remove(entity);
                    count++;
                }
                entityManager.flush();
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteTokenHistory: {}", e.getMessage(), e);
            return count;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public YourEntity findYourEntityById(Long entityId) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityId"), entityId));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public YourEntity findYourEntityByEmail(String email) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityEmailAddress"), email));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntity> findAllYourEntities() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
        final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

        criteriaQuery.select(yourEntityRoot);

        return entityManager.createQuery(criteriaQuery).getResultList();

    }

    @Override
    @Transactional
    public void saveYourEntity(YourEntity yourEntity) {
        if (yourEntity == null) {
            return;
        }
        try {
            if (yourEntity.getEntityId() == null) {
                yourEntity.setCreatedByDate(Date.from(Instant.now()));
                yourEntity.setCreatedByIdentifier("SYSTEM");
            }
            yourEntity.setUpdatedByDate(Date.from(Instant.now()));
            yourEntity.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntity);
            entityManager.flush();
        } catch (Exception e) {
            LOGGER.error("Exception Saving YourEntity: {} {}", e.getMessage(), yourEntity, e);
        }

    }

    /**
     * deleteYourEntityById
     * Will delete Graph of the YourEntity Object
     *
     * @param entityId Identity of Entity to be Deleted.
     * @return Integer Count of Objects Deleted or Zero.
     */
    @Override
    @Transactional
    public Integer deleteYourEntityById(Long entityId) {
        Integer count = 0;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<YourEntity> criteriaQuery = criteriaBuilder.createQuery(YourEntity.class);
            final Root<YourEntity> yourEntityRoot = criteriaQuery.from(YourEntity.class);

            criteriaQuery.select(yourEntityRoot);
            criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityId"), entityId));
            YourEntity yourEntity = entityManager.createQuery(criteriaQuery).getSingleResult();
            if (yourEntity != null) {
                    entityManager.remove(yourEntity);
                    entityManager.flush();
                    count++;
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteYourEntityById: {}", e.getMessage(), e);
            return count;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public YourEntityOrganization findYourEntityOrganizationById(Long entityOrgId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityOrgId"), entityOrgId));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityOrganization findYourEntityOrganizationByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("name"), name));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityOrganization> findAllYourEntityOrganizations() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
        final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

        criteriaQuery.select(yourEntityRoot);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * deleteYourEntityOrganizationById
     * Will delete Graph of the YourEntity Object
     *
     * @param entityOrgId Identity of Entity to be Deleted.
     * @return Integer Count of Objects Deleted or Zero.
     */
    @Override
    @Transactional
    public Integer deleteYourEntityOrganizationById(Long entityOrgId) {
        Integer count = 0;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<YourEntityOrganization> criteriaQuery = criteriaBuilder.createQuery(YourEntityOrganization.class);
            final Root<YourEntityOrganization> yourEntityRoot = criteriaQuery.from(YourEntityOrganization.class);

            criteriaQuery.select(yourEntityRoot);
            criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityOrgId"), entityOrgId));
            YourEntityOrganization yourEntity = entityManager.createQuery(criteriaQuery).getSingleResult();
            if (yourEntity != null) {
                entityManager.remove(yourEntity);
                entityManager.flush();
                count++;
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteYourEntittOrganizationById: {}", e.getMessage(), e);
            return count;
        }
    }

    @Override
    @Transactional
    public void saveYourEntityOrganization(YourEntityOrganization yourEntityOrganization) {
        if (yourEntityOrganization == null) {
            return;
        }
        try {
            if (yourEntityOrganization.getEntityOrgId() == null) {
                yourEntityOrganization.setCreatedByDate(Date.from(Instant.now()));
                yourEntityOrganization.setCreatedByIdentifier("SYSTEM");
            }
            yourEntityOrganization.setUpdatedByDate(Date.from(Instant.now()));
            yourEntityOrganization.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntityOrganization);
            entityManager.flush();
        } catch (Exception e) {
            LOGGER.error("Exception Saving YourEntityOrganization: {} {}", e.getMessage(), yourEntityOrganization, e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityRole findYourEntityRoleById(Long entityRoleId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityRoleId"), entityRoleId));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    @Transactional(readOnly = true)
    public YourEntityRole findYourEntityRoleByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);
        criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("name"), name));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YourEntityRole> findAllYourEntityRoles() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
        final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

        criteriaQuery.select(yourEntityRoot);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional
    public void saveYourEntityRole(YourEntityRole yourEntityRole) {
        if (yourEntityRole == null) {
            return;
        }
        try {
            if (yourEntityRole.getEntityRoleId() == null) {
                yourEntityRole.setCreatedByDate(Date.from(Instant.now()));
                yourEntityRole.setCreatedByIdentifier("SYSTEM");
            }
            yourEntityRole.setUpdatedByDate(Date.from(Instant.now()));
            yourEntityRole.setUpdatedByIdentifier("SYSTEM");
            entityManager.persist(yourEntityRole);
            entityManager.flush();
        } catch (Exception e) {
            LOGGER.error("Exception Saving YourEntityRole: {} {}", e.getMessage(), yourEntityRole, e);
        }
    }

    /**
     * deleteYourEntityRoleById
     * Will delete Graph of the YourEntity Object
     *
     * @param entityRoleId Identity of Entity to be Deleted.
     * @return Integer Count of Objects Deleted or Zero.
     */
    @Override
    @Transactional
    public Integer deleteYourEntityRoleById(Long entityRoleId) {
        Integer count = 0;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<YourEntityRole> criteriaQuery = criteriaBuilder.createQuery(YourEntityRole.class);
            final Root<YourEntityRole> yourEntityRoot = criteriaQuery.from(YourEntityRole.class);

            criteriaQuery.select(yourEntityRoot);
            criteriaQuery.where(criteriaBuilder.equal(yourEntityRoot.get("entityRoleId"), entityRoleId));
            YourEntityRole yourEntity = entityManager.createQuery(criteriaQuery).getSingleResult();
            if (yourEntity != null) {
                entityManager.remove(yourEntity);
                entityManager.flush();
                count++;
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("Exception encountered attempting to deleteYourEntityRoleById: {}", e.getMessage(), e);
            return count;
        }
    }

}

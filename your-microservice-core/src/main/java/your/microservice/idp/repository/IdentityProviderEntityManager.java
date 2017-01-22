package your.microservice.idp.repository;

import your.microservice.idp.model.base.*;
import your.microservice.idp.model.types.YourEntityTokenStatus;

import java.util.List;
import java.util.Map;

/**
 * IdentityProviderEntityManager
 *
 * @author jeff.a.schenk@gmail.com on 4/6/16.
 */
public interface IdentityProviderEntityManager {
    /**
     * createTokenHistory
     *
     * @param yourEntityTokenHistory Entity Object to be Persisted.
     * @return YourEntityTokenHistory Entity Object Persisted.
     */
    YourEntityTokenHistory createTokenHistory(YourEntityTokenHistory yourEntityTokenHistory);

    /**
     * readTokenHistory
     *
     * @param jti Distinct Token Identifier Token to Read.
     * @return YourEntityTokenHistory Entity Object Found.
     */
    YourEntityTokenHistory readTokenHistory(String jti);

    /**
     * readTokenHistoryBySubject
     *
     * @param subject used to search for all Token Occurrences.
     * @return List of Token History Entities.
     */
    List<YourEntityTokenHistory> readTokenHistoryBySubject(String subject);

    /**
     * readCurrentExpiredTokenHistory
     *
     * @return List of Token History Entities which have Expired.
     */
    List<YourEntityTokenHistory> readCurrentExpiredTokenHistory();

    /**
     * updateTokenHistoryStatus
     *
     * @param jti Entity Object whose status is to be updated.
     * @param status Status to be set on JTI.
     * @return Boolean Indicates if JTI was Updated or not.
     */
    Boolean updateTokenHistoryStatus(String jti, YourEntityTokenStatus status);

    /**
     * incrementTokenHistoryUsage
     * If the resultant Integer is not 1, then the Token is either been revoked, has been
     * removed from the Store or in another state other than Actove.
     *
     * @param jti Distinct Token Identifier Token to Increment Usage.
     * @return Boolean Indicates if JTI Count was Updated or not, if not, kill the token.
     */
    Boolean incrementTokenHistoryUsage(String jti);

    /**
     * deleteToken
     *
     * @param jti Token Identifier to use to Physically Delete Token Entity.
     * @return Integer number of Tokens Deleted, normally this is one for this method call.
     */
    Integer deleteTokenHistory(String jti);

    /**
     * deleteTokenHistoryBySubject
     *
     * @param subject Token Subject to use to Physically Delete Token Entities.
     * @return Integer number of Tokens Deleted.
     */
    Integer deleteTokenHistoryBySubject(String subject);

    /**
     * createEventHistory
     *
     * @param yourEntityEventHistory
     */
    void createEventHistory(YourEntityEventHistory yourEntityEventHistory);


    YourEntity findYourEntityById(Long entityId);

    YourEntity findYourEntityByEmail(String email);

    void saveYourEntity(YourEntity yourEntity);

    List<YourEntity> findAllYourEntities();



    YourEntityOrganization findYourEntityOrganizationById(Long entityOrgId);

    YourEntityOrganization findYourEntityOrganizationByName(String name);

    List<YourEntityOrganization> findAllYourEntityOrganizations();

    void saveYourEntityOrganization(YourEntityOrganization yourEntityOrganization);



    List<YourEntityRole> findAllYourEntityRoles();

    YourEntityRole findYourEntityRoleById(Long entityRoleId);

    YourEntityRole findYourEntityRoleByName(String name);

    void saveYourEntityRole(YourEntityRole yourEntityRole);

}

package your.microservice.idp.repository;

import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.base.YourEntityOrganization;
import your.microservice.idp.model.base.YourEntityTokenHistory;
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
     * readTokenHistory
     *
     * @param queryParameters Named Value Parameters for Query.
     * @return List of YourEntityTokenHistory Entities.
     */
    List<YourEntityTokenHistory> readTokenHistory(Map<String, Object> queryParameters);

    /**
     * updateTokenHistoryStatus
     *
     * @param jti Entity Object whose status is to be updated.
     * @param status Status to be set on JTI.
     * @return Integer Number of Entities Updated, normally one.
     */
    Integer updateTokenHistoryStatus(String jti, YourEntityTokenStatus status);

    /**
     * incrementTokenHistoryUsage
     * If the resultant Integer is not 1, then the Token is either been revoked, has been
     * removed from the Store or in another state other than Actove.
     *
     * @param jti Distinct Token Identifier Token to Increment Usage.
     * @return Integer Number of Entities Updated, normally one, if zero, kill the token.
     */
    Integer incrementTokenHistoryUsage(String jti);

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


    YourEntity findYourEntityByEmail(String email);

    List<YourEntity> findAllYourEntities();

    YourEntityOrganization findYourEntityOrganizationByName(String name);

    List<YourEntityOrganization> findAllYourEntityOrganizations();

}

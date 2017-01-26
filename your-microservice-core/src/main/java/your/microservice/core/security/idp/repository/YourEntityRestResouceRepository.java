package your.microservice.core.security.idp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import your.microservice.core.security.idp.model.base.YourEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jaschenk
 * Date: 1/16/17
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */
@RepositoryRestResource(collectionResourceRel = "yourEntity", path = "yourEntity")
public interface YourEntityRestResouceRepository extends PagingAndSortingRepository<YourEntity, Long> {

        List<YourEntity> findByEmail(@Param("email") String email);

}

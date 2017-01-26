package your.microservice.core.security.idp.service.impl;

import your.microservice.core.security.idp.service.SecurityService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Override
    public Boolean hasProtectedAccess() {
        return SecurityContextHolder.getContext().getAuthentication().
                getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    @Override
    public Boolean hasUserAccess() {
        return SecurityContextHolder.getContext().getAuthentication().
                getAuthorities().contains(new SimpleGrantedAuthority("USER"));
    }

}

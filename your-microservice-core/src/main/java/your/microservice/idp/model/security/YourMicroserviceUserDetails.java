package your.microservice.idp.model.security;

import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.service.authority.YourMicroservicePendingUserAuthority;
import your.microservice.idp.service.authority.YourMicroserviceUserAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * YourMicroserviceUserDetails
 */
public class YourMicroserviceUserDetails implements UserDetails {

    /*Spring Security fields*/
    private final Collection<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final boolean accountNonPending;

    /**
     * username
     * This will contain the 'Primary Email Address' of the Entity within the
     * Your Microservice Eco-System.
     */
    private final String username;
    /**
     * password
     * This will contain the 'Credentials' of the Entity,
     * can go null if after Authentication.  The contents of this property is the
     * Encoded Password from the Store.
     * <p>
     * Use the Class' shredCredentials which
     * will in-turn nullify the Password to clear memory constructs
     * containing the credential information in any form.
     */
    private String password;
    /**
     * Principal's Identifier.
     */
    private final Long principalID;

    /**
     * Default Constructor
     *
     * @param yourEntity Entity found based upon Lookup.
     */
    public YourMicroserviceUserDetails(YourEntity yourEntity) {
        /**
         * Check for a Null Entity.
         */
        if (yourEntity == null) {
            this.authorities = new ArrayList<>();
            this.username = null;
            this.password = null;
            this.principalID = null;
            this.accountNonExpired = false;
            this.accountNonLocked = false;
            this.credentialsNonExpired = false;
            this.enabled = false;
            this.accountNonPending = false;
        } else {
            /**
             * Instantiate a User Details Object from the Persisted Person
             * object that was previously obtained.
             */
            this.username = yourEntity.getEntityEmailAddress().toLowerCase();
            this.password = yourEntity.getCredentials();
            this.principalID = yourEntity.getEntityId();

            if (yourEntity.getStatus().equals(YourEntityStatus.ACTIVE)) {
                this.accountNonLocked = true;
                this.accountNonPending = true;
                this.credentialsNonExpired = true;
                this.accountNonExpired = true;
            } else {
                this.accountNonLocked = false;
                this.accountNonPending = false;
                this.credentialsNonExpired = false;
                this.accountNonExpired = true;
            }

            this.enabled = this.accountNonExpired && this.accountNonLocked;

            /**
             * Establish the Granted Authorities...
             * TODO Lookup Roles for the Entity ...
             */
            ArrayList<GrantedAuthority> grantedAuthorites = new ArrayList<>();
            grantedAuthorites.add(
                    accountNonPending
                            ? new YourMicroserviceUserAuthority()
                            : new YourMicroservicePendingUserAuthority()
            );

            this.authorities = grantedAuthorites;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public Long getPrincipalID() {
        return this.principalID;
    }

    public void shredCredentials() {
        this.password = null;
    }
}

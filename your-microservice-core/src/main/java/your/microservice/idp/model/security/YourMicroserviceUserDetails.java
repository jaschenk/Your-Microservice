package your.microservice.idp.model.security;

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
     * This will contain the 'Primary Email Address' of the Person Entity within the
     * Your Microservice Eco-System.
     */
    private final String username;
    /**
     * password
     * This will contain the 'Credentials' of the person Entity,
     * can go null if after Authentication.  The contents of this property is the
     * Encoded Password from the Store.
     * <p>
     * Use the Class' shredCredentials which
     * will in-turn nullify the Password to clear memory constructs
     * containing the credential information in any form.
     */
    private String password;
    /**
     * Orient DB @RID Identifier for the Person Entity.
     */
    private final Object rid;
    /**
     * Principal's Identifier.
     */
    private final String principalUUID;

    public YourMicroserviceUserDetails(Object person) {
        /**
         * If by chance we received a Null Person ODocument Object,
         * simply instantiate a Nullified User Details Object.
         */
        if (person == null) {
            this.authorities = new ArrayList<>();
            this.accountNonExpired = false;
            this.accountNonLocked = false;
            this.credentialsNonExpired = false;
            this.enabled = false;
            this.accountNonPending = false;
            this.username = null;
            this.password = null;
            this.rid = null;
            this.principalUUID = null;
        } else {
            /**
             * Instantiate a User Details Object from the Persisted Person
             * object that was previously obtained.
             */
            this.rid = null; // person.getIdentity();
            this.username = null; //((String) person.field("primaryemail")).toLowerCase();
            this.password = null; // person.field("password");
            this.principalUUID = null; //person.field("uuid");

            //String objStateString = person.field("objState");
            //ObjectState objState = (objStateString == null)
            //        ? ObjectState.PENDING
            //        : ObjectState.valueOf(objStateString);

            this.accountNonLocked = true;
            this.accountNonPending = true;
            this.credentialsNonExpired = true;
            this.accountNonExpired = true;

            // TODO :: Determine if the Account is Expired or Not...

            this.enabled = this.accountNonExpired && this.accountNonLocked;

            /**
             * Establish the Granted Authorities...
             */
            ArrayList<GrantedAuthority> grantedAuthorites = new ArrayList<>();

            grantedAuthorites.add(
                    accountNonPending
                            ? new YourMicroserviceUserAuthority()
                            : new YourMicroservicePendingUserAuthority()
            );

            // TODO: this could still probably be a better check
            //String superadminEmail = System.getProperty("superadmin_email");
            //if (superadminEmail != null && superadminEmail.equalsIgnoreCase(this.username)) {
            //    grantedAuthorites.add(new YourMicroserviceSuperAdminAuthority());
            //}

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

    public Object getRid() {
        return this.rid;
    }

    public String getPrincipalUUID() {
        return this.principalUUID;
    }

    public void shredCredentials() {
        this.password = null;
    }
}

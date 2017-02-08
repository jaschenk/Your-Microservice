package your.microservice.core.security.idp.jwt;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.JWTClaimsVerifier;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;
import your.microservice.core.system.ShutdownManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.util.*;

/**
 * YourMicroserviceToken Component
 * <p>
 * Your Microservice Standards JWT Generation:
 * <p>
 * “iss” : Issuer
 * Static Constant: “Your Microservice”
 * <p>
 * “sub” : Subject
 * Depending upon type of Token will contain:
 * Primary Email Address which initial Authentication occurred to obtain Access Token.
 * <p>
 * “aud” : Audience
 * The IdP determine the source device the request has been originated and sets the applicable Audience.
 * Audience Unknown
 * Audience Mobile
 * Audience Tablet
 * Audience WEB
 * Audience Bot
 * <p>
 * “exp” : Expiration Date and Time of the Token, when Token will no longer be valid.
 * <p>
 * “nbf” : Not Used Before Date and Time.  Token can not be used before this Date and Time.
 * <p>
 * “iat” : Issued at Date and Time.
 * <p>
 * “jti” : Token Identifier, Generated Random UUID.  Used for Identity Store Persistence of Token Store.
 * <p>
 * “your.microservice” : Your Microservice Manifest
 * <p>
 * Standard Claim Names Defined here: http://www.iana.org/assignments/jwt/jwt.xhtml
 *
 * @author jeff.a.schenk@gmail.com
 */
@Component
public class YourMicroserviceToken_nimbus_Impl implements YourMicroserviceToken {
    /**
     * Message Constants
     */
    protected static final String V_MESSAGE_NO_TOKEN_SUPPLIED = "No Token Supplied to Verify.";
    protected static final String V_MESSAGE_UNABLE_TO_PARSE_TOKEN = "Unable to Parse Token.";
    protected static final String V_MESSAGE_UNABLE_TO_DECRYPT_TOKEN = "Unable to Decrypt Token.";
    protected static final String V_MESSAGE_TOKEN_SIGNATURE_INVALID = "Token Signature Invalid.";
    protected static final String V_MESSAGE_INVALID_TOKEN = "Invalid Token, failed Verification.";
    protected static final String V_MESSAGE_INVALID_TOKEN_YOUR_MICROSERVICE_MANIFEST
            = "Invalid Token, Your Microservice Manifest failed Verification.";

    /**
     * Default Keystore File Name.
     */
    private static final String TEMPORARY_KEYSTORE_FILENAME_PREFIX =
            ".yourMicroservice-IdP";

    /**
     * KEYSTORE_FILE_NAME_PATH
     *
     * KeyStore File Name Path, full Path of
     * KeyStore.  Parent Directory Must exist,
     * if KeyStore if to be Created.
     */
    @Value("${your.microservice.security.keystore.filename}")
    private String KEYSTORE_FILE_NAME_PATH;
    private File resolvedKeystoreFile;

    /**
     * KEYSTORE_CREDENTIALS
     *
     * KeyStore Credentials to access KeyStore
     * Entries.
     */
    @Value("${your.microservice.security.keystore.credentials}")
    private String KEYSTORE_CREDENTIALS;

    /**
     * KEYSTORE_ENTRY_NAME
     *
     * KeyStore Entry Name to be obtained.
     */
    @Value("${your.microservice.security.keystore.entry.name}")
    private String KEYSTORE_ENTRY_NAME;

    /**
     * TOKEN_EXPIRATION_IN_SECONDS
     *
     * Expiration of Tokens in Seconds.
     * Example specified as '14400' equates to 4 Hours.
     */
    @Value("${your.microservice.security.token.expiration}")
    private Long TOKEN_EXPIRATION_IN_SECONDS = 14400L;

    /**
     * Object Mapper for Marshaling the Your Microservice Manifest
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Our Internal Secret Key, obtain from KeyStore.
     * If KeyStore, does not exist, the SecretKey will be generated.
     */
    private SecretKey secretKey;
    /**
     * Our JWT Signer based upon our SecretKey.
     */
    private JWSSigner jwsSigner;

    /**
     * Shutdown Manager
     */
    @Autowired
    private ShutdownManager shutdownManager;

    /**
     * Post Construction Initialization.
     * Here we established our SecretKey and JWS Signer.
     */
    @PostConstruct
    public synchronized void initialization() {
        /**
         * Initialize
         */
        LOGGER.info("{}YourMicroserviceToken Component Initialization Commencing...",
                LOGGING_HEADER);
        /**
         * Resolve the Keystore Location based upon our Provided Properties.
         */
        if(!resolveKeystoreFile()) {
                String message = "Unable to Initialize the YourMicroserviceToken Implementation, " +
                        "No KeyStore File Path Specified or was invalid! Update value in property: 'your.microservice.security.keystore', unable to continue...";
                LOGGER.error("{}{}", LOGGING_HEADER, message);
                shutdownManager.initiateShutdown(
                        LOGGING_HEADER+message, 9);
                return;
        }
        /**
         * Check for proper runtime properties.
         */
        if (KEYSTORE_CREDENTIALS == null || KEYSTORE_CREDENTIALS.isEmpty()) {
            String message = "Unable to Initialize the YourMicroserviceToken Implementation, " +
                    "No KeyStore Credentials Specified, specify property: 'your.microservice.security.keystore.credentials', unable to continue...";
            LOGGER.error("{}{}", LOGGING_HEADER, message);
            shutdownManager.initiateShutdown(
                    LOGGING_HEADER+message, 9);
            return;
        }
        if (KEYSTORE_ENTRY_NAME == null || KEYSTORE_ENTRY_NAME.isEmpty()) {
            String message = "Unable to Initialize the YourMicroserviceToken Implementation, " +
                    "No KeyStore Entry Name Specified, specify property: 'your.microservice.security.keystore.entry.name', unable to continue...";
            LOGGER.error("{}{}", LOGGING_HEADER, message);
            shutdownManager.initiateShutdown(
                    LOGGING_HEADER+message, 9);
            return;
        }
        /**
         * Obtain SecretKey and establish our JWT Signer.
         */
        secretKey = obtainSecretKey();
        jwsSigner = obtainJWSSigner(secretKey);
        if (secretKey != null && jwsSigner != null) {
            LOGGER.info("{}YourMicroserviceToken Component Initialization Successful.",
                    LOGGING_HEADER);
        } else {
            LOGGER.error("{}YourMicroserviceToken Component Initialization has Failed!",
                    LOGGING_HEADER);
            /**
             * Shutdown the Application Container, we can not run without this Component
             * correctly initialized.
             */
            shutdownManager.initiateShutdown(
                    LOGGING_HEADER+"Unable to properly Initialize the YourMicroserviceToken Implementation, " +
                            "please review Logs, unable to continue...", 9);
        }
    }

    /**
     * Pre-Destroy TearDown of Container.
     * Here we Destroy our SecretKey and JWS Signer.
     */
    @PreDestroy
    public void shutdown() {
        /**
         * Shutdown Component
         */
        LOGGER.info("{}YourMicroserviceToken Component Shutdown commencing...",
                LOGGING_HEADER);
        secretKey = null;
        jwsSigner = null;
        LOGGER.info("{}YourMicroserviceToken Component Shutdown Completed, ready for Bean Removal.",
                LOGGING_HEADER);
    }

    /**
     * obtainSecretKey
     *
     * @return SecretKey Obtained or Generated from keyStore.
     */
    private SecretKey obtainSecretKey() {
        /**
         * Generate our Entry Credentials based upon our KeyStore Credentials.
         */
        final String KEYSTORE_ENTRY_CREDENTIALS =
                generatedKeyStoreEntryCredentials(KEYSTORE_CREDENTIALS);
        /**
         * Access KeyStore.
         */
        try {
            /**
             * Check our Entry Credentials were generated.
             */
            if (KEYSTORE_ENTRY_CREDENTIALS == null || KEYSTORE_ENTRY_CREDENTIALS.isEmpty()) {
                LOGGER.error("{}KeyStore Entry Credentials were not Generated, Unable to Generate SecretKey!",
                        LOGGING_HEADER);
                throw new YourMicroserviceTokenInitializationException("Unable to Generate Entry Credentials.");
            }
            /**
             * Access KeyStore.
             */
            KeyStore keyStore = accessKeyStore(resolvedKeystoreFile, KEYSTORE_CREDENTIALS);
            KeyStore.PasswordProtection keyPassword =
                    new KeyStore.PasswordProtection(KEYSTORE_ENTRY_CREDENTIALS.toCharArray());
            /**
             * Does our Key exist?
             * Attempt to obtain our Key Entry from the specified KeyStore.
             */
            KeyStore.Entry entry = null;
            try {
                entry = keyStore.getEntry(KEYSTORE_ENTRY_NAME, keyPassword);
            } catch (UnrecoverableKeyException uke) {
                LOGGER.error("{}Unable to Obtain KeyStore Entry due to KeyStore Entry Password not correct!",
                        LOGGING_HEADER);
                throw new YourMicroserviceTokenInitializationException("Unable to Access KeyStore.");
            }
            SecretKey keyFound = null;
            if (entry != null) {
                keyFound = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
                LOGGER.info("{}Found Established Key from specified KeyStore: '{}'",
                        LOGGING_HEADER, resolvedKeystoreFile.getAbsolutePath());
            } else {
                /**
                 * Generate a new Secret Key for AES Encryption
                 */
                SecretKey secretKey = generateSecretKey();
                if (secretKey == null) {
                    LOGGER.error("{}Unable to Obtain to generate a Secret Key, failing runtime support.",
                            LOGGING_HEADER);
                    throw new YourMicroserviceTokenInitializationException("Unable to Access KeyStore.");
                }
                /**
                 * Store the Secret Key for Subsequent Bootstraps, oh and yes, this Key needs to be shared
                 * across all instances.  At least point to the same or at least a copy of the
                 * initially created KeyStore.
                 */
                LOGGER.info("{}Storing Secret Key to specified KeyStore: '{}'.",
                        LOGGING_HEADER, resolvedKeystoreFile.getAbsolutePath());
                KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(secretKey);
                keyStore.setEntry(KEYSTORE_ENTRY_NAME, keyStoreEntry, keyPassword);
                keyStore.store(new FileOutputStream(resolvedKeystoreFile), KEYSTORE_CREDENTIALS.toCharArray());
                /**
                 * retrieve the stored key back
                 */
                entry = keyStore.getEntry(KEYSTORE_ENTRY_NAME, keyPassword);
                keyFound = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            }
            /**
             * Return Key, if null we need to abort.
             */
            return keyFound;
        } catch (Exception e) {
            LOGGER.error("{}Exception Obtaining SecretKey for Signing and Encryption Use.",
                    LOGGING_HEADER);
            return null;
        }
    }

    /**
     * obtainJWSSigner
     * Obtain JWT Signer based upon our SecretKey
     *
     * @param secretKey Reference
     * @return JWSSigner Signer to be used for Signing and Validating Signatures.
     */
    private JWSSigner obtainJWSSigner(SecretKey secretKey) {
        if (secretKey == null) {
            LOGGER.error("{}Unable to use specified Secret Key, very Bad!", LOGGING_HEADER);
            return null;
        }
        try {
            /**
             * Create HMMAC Signer.
             */
            return new MACSigner(secretKey.getEncoded());
        } catch (KeyLengthException kle) {
            LOGGER.error("{}Signing Secret Key Failed with Key Length Exception, very Bad, {}",
                    LOGGING_HEADER, kle.getMessage());
            return null;
        }
    }

    /**
     * generateSecretKey
     * Generate Secret Key which will be used based upon the KeyStore which the Runtime is established.
     *
     * @return SecretKey Generated Secret Key, which will be Stored in the KeyStore.
     */
    private SecretKey generateSecretKey() {
        try {
            /**
             *  Generate 256-bit AES key for HMAC as well as encryption
             *  This requires the Java 8 Unlimited Crypto Policies be in Place, otherwise this will Fail!
             */
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            LOGGER.info("{}Using Provider: '{}' Algorithm: '{}'",
                    LOGGING_HEADER, keyGen.getProvider().getName(), keyGen.getAlgorithm());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException nsae) {
            LOGGER.error("{}No Such Algorithm Exception: {}", LOGGING_HEADER, nsae.getMessage());
            return null;
        }
    }

    private boolean resolveKeystoreFile() {
        boolean resolved = false;
        /**
         * Determine if the File NAme Path has been specified for our KeyStore.
         * If it has not, assume a test, and attempt to establish a Test Keystore Filename,
         * which if established, can only be used once for the duration of the runtime instance.
         */
        if (KEYSTORE_FILE_NAME_PATH == null || KEYSTORE_FILE_NAME_PATH.isEmpty()) {
            /**
             * Establish a Test Temporary Keystore for this runtime instance's existence.
             */
            try{
                File temp = File.createTempFile(TEMPORARY_KEYSTORE_FILENAME_PREFIX, ".keystore");
                /**
                 * Remove it for now ...
                 */
                if (temp.exists()) {
                    temp.delete();
                }
                KEYSTORE_FILE_NAME_PATH = temp.getAbsolutePath();
                LOGGER.info("{}Temporary Keystore;[{}] has been created for this Runtime Instance.",
                        LOGGING_HEADER, KEYSTORE_FILE_NAME_PATH);
                LOGGER.info("{}If you would like to reuse this Temporary Keystore, " +
                        "copy the Keystore File and assign the Keystore Property with the appropriate File Path Value.",
                        LOGGING_HEADER);
            } catch(IOException e){
                LOGGER.error("{}Attempting Creation of Temporary Keystore Failed: {}",
                        LOGGING_HEADER, e.getMessage(),e);
               return false;
            }
            /**
             * Recursively re-attempt the resolution.
             */
            if (resolvedKeystoreFile != null) {
                /**
                 * We already attempted resolution twice, now abandon further attempts...
                 */
                return resolved;
            }
            return resolveKeystoreFile();
        }
        /**
         * Attempt to resolve the Keystore File Path
         */
            resolvedKeystoreFile = new File(KEYSTORE_FILE_NAME_PATH);
            if (resolvedKeystoreFile.exists()&&resolvedKeystoreFile.isFile()&&resolvedKeystoreFile.canRead()&&
                    resolvedKeystoreFile.canWrite()) {
              return true;
            } else if (!resolvedKeystoreFile.exists()) {
               resolved = true;
                LOGGER.info("{}Using Keystore:[{}] has been created for this Runtime Instance.",
                        LOGGING_HEADER, resolvedKeystoreFile.getAbsolutePath());
            } else {
                /**
                 * Here we have a permission issue, either a Directory, or we can not Read or Write to the
                 * specified Keystore, we have to fail, as we do not want to assume anything and run the risk
                 * of running an unsecured Microservice.
                 */
                String message = "Unable to Initialize the YourMicroserviceToken Implementation, " +
                        "No KeyStore File Path Specified, specify property: 'your.microservice.security.keystore', unable to continue...";
                LOGGER.error("{}{}", LOGGING_HEADER, message);
            }
        /**
         * return Resolution
         */
        return resolved;
    }

    /**
     * accessKeyStore
     * Access KeyStore, or if not available, create the KeyStore.
     *
     * @param file Object of KeyStore file.
     * @param pw Credentials for KeyStore.
     * @return KeyStore Obtained KeyStore.
     * @throws Exception Thrown when issue accessing or creating a new KeyStore.
     */
    private KeyStore accessKeyStore(File file, String pw) throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JCEKS");
        /**
         * Check if KeyStore File Exists?
         */
        if (file.exists()) {
            /**
             * KeyStore file already exists => load it
             */
            keyStore.load(new FileInputStream(file), pw.toCharArray());
        } else {
            if (!file.getParentFile().exists()) {
                String message = "Unable to Access KeyStore Path to Create KeyStore!";
                LOGGER.warn("{}{} Path:[{}]", LOGGING_HEADER, message, file.getAbsolutePath());
                throw new YourMicroserviceTokenInitializationException(message);
            }
            /**
             * KeyStore file not created yet => create it
             */
            keyStore.load(null, null);
            keyStore.store(new FileOutputStream(file), pw.toCharArray());
        }
        /**
         * Return KeyStore.
         */
        return keyStore;
    }

    /**
     * generatedKeyStoreEntryCredentials
     * Generate the KeyStore entry Credentials based upon the KeyStore credentials.
     * @param KEYSTORE_CREDENTIALS KeyStore Credentials.
     * @return String contains Generated KeyStore Entry Credentials.
     */
    private String generatedKeyStoreEntryCredentials(final String KEYSTORE_CREDENTIALS) {
        if (KEYSTORE_CREDENTIALS == null || KEYSTORE_CREDENTIALS.isEmpty()) {
            return null;
        }
        int hashcode = KEYSTORE_CREDENTIALS.hashCode();
        int mask = 255;
        int extra = hashcode & mask;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%010d", hashcode));
        sb.append(String.format("%03d", extra));
        return Base64.getEncoder().encodeToString(sb.toString().getBytes());
    }

    /**
     * transformAudienceType, Helper Method to generate the Audience Mnemonic based upon
     * the Device Type.
     * @param device End Consumer Device Type.
     * @return String representing deduced Audience.
     */
    @Override
    public String transformAudienceType(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    /**
     * getUsernameFromToken
     *
     * @param token JWT
     * @return String containing Authenticated UserName, in our case it is the
     * Primary Email Address.  This is always the claims 'sub' or 'subject'.
     */
    @Override
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Map<String, Object> claims = getClaimsFromToken(token);
            username = (String) claims.get(CLAIM_NAME_SUBJECT);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * getIdentifierFromToken
     *
     * @param token JWT
     * @return String containing 'jti' or the JWT Token Identifier.
     */
    @Override
    public String getIdentifierFromToken(String token) {
        String jti;
        try {
            final Map<String, Object> claims = getClaimsFromToken(token);
            jti = (String) claims.get(CLAIM_NAME_TOKEN_IDENTIFIER);
        } catch (Exception e) {
            jti = null;
        }
        return jti;
    }

    /**
     * getIssuedDateFromToken
     *
     * @param token JWT
     * @return Date which represents 'iat' Issued At.
     */
    @Override
    public Date getIssuedDateFromToken(String token) {
        Date created;
        try {
            final Map<String, Object> claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_NAME_ISSUED_AT));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * getExpirationDateFromToken
     *
     * @param token JWT
     * @return Date which represents 'exp' Expiration.
     */
    @Override
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Map<String, Object> claims = getClaimsFromToken(token);
            expiration = new Date((Long) claims.get(CLAIM_NAME_EXPIRATION));
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * getAudienceFromToken
     *
     * @param token JWT
     * @return String which represents the Audience claim or 'aud'.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAudienceFromToken(String token) {
        List<String> audience;
        try {
            final Map<String, Object> claims = getClaimsFromToken(token);
            audience = (List) claims.get(CLAIM_NAME_AUDIENCE);
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    /**
     * getClaimsFromToken
     * <p>
     * Private helper method to obtain all Claims for specified JWT.
     *
     * @param token JWT
     * @return Claims parsed from JWT.
     */
    protected Map<String, Object> getClaimsFromToken(String token) {
        try {
            JWTClaimsSet jwtClaimsSet = verifyToken(token);
            if (jwtClaimsSet != null) {
                return jwtClaimsSet.getClaims();
            }
        } catch (Exception e) {
            LOGGER.error("{}Exception Processing Token:'{}', '{}'.", LOGGING_HEADER, token, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Generate Current Date, protected Helper Method.
     *
     * @return Date representing now.
     */
    protected Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * generateExpirationDate, protected Helper Method.
     *
     * @return Date representing now + Expiration.
     */
    protected Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_IN_SECONDS * 1000);
    }

    /**
     * isTokenExpired, protected Helper Method.
     *
     * @param token JWT
     * @return Boolean indicating if specified Token is Expired or not.
     */
    protected Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(generateCurrentDate());
    }

    /**
     * generateToken
     *
     * @param subject Current User Details Object
     * @param device  Current Device Type User is performing Request from.
     * @return String Representing the constructed JWT for the specified Claims.
     */
    @Override
    public String generateToken(String subject, String device) {
        /**
         * Create Your Microservice Token Standards Claims.
         */
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_NAME_SUBJECT, subject.toLowerCase());
        List<String> audience = new ArrayList<>();
        audience.add(device);
        claims.put(CLAIM_NAME_AUDIENCE, audience);
        /**
         * Return the Generated Token.
         */
        return generateToken(claims);
    }

    /**
     * refreshToken
     * Construct a new JWT for the applicable User per the Parsed incoming JWT.
     *
     * @param token JWT
     * @return String representing new JWT for applicable Authenticated Subject.
     */
    @Override
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            /**
             * Obtain the Existing Claims and use to pass
             * to generate Token per Claims.  All Claims will be overwritten
             * with exception of the following:
             * 'sub' : Subject
             * 'aud' : Audience
             */
            final Map<String, Object> claims = getClaimsFromToken(token);
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * generateToken, protected Helper Method.
     * <p>
     * Generate Token always, performs in this Order:
     * + Generate Raw Token
     * + Sign
     * + Encrypt
     *
     * @param claims Current Claims
     * @return String Representing the constructed JWT for the specified Claims.
     */
    @SuppressWarnings("unchecked")
    @Override
    public String generateToken(Map<String, Object> claims) {
        /**
         * initialize
         */
        if (claims == null) {
            claims = new HashMap<>();
        }
        /**
         * Create Audience
         */
        List<String> audience = (List) claims.get(CLAIM_NAME_AUDIENCE);
        /**
         * Create Manifest
         */
        YourMicroserviceManifest yourMicroserviceManifest = createDefaultManifest();

        /**
         * Calculate a not Before Use Date, of now less 2 minutes.
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        Date notBeforeDate = calendar.getTime();
        /**
         * Prepare JWT Claims Set.
         */
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(((String) claims.get(CLAIM_NAME_SUBJECT)).toLowerCase())
                .audience(audience)
                .jwtID(UUID.randomUUID().toString())
                .issuer(YOUR_ORGANIZATION_ISSUER)
                .issueTime(generateCurrentDate())
                .expirationTime(generateExpirationDate())
                .notBeforeTime(notBeforeDate)
                .claim(CLAIM_NAME_YOUR_MICROSERVICE, yourMicroserviceManifest)
                // Add Additional Claims Here ...
                .build();
        /**
         * Sign the JWT and apply a Wrapper to construct a JWE.
         */
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        try {
            /**
             * Apply the HMAC
             */
            signedJWT.sign(jwsSigner);
            /**
             * Create JWE object with signed JWT as payload
             */
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));
            /**
             * Encrypt
             */
            jweObject.encrypt(new DirectEncrypter(secretKey.getEncoded()));
            /**
             * Return the Generated Token.
             */
            return jweObject.serialize();
        } catch (JOSEException josee) {
            LOGGER.error("{}Generating Encrypted Signed Token Failed with Exception, very Bad, {}",
                    LOGGING_HEADER, josee.getMessage());
            return null;
        }

    }

    /**
     * createDefaultManifest
     *
     * @return YourMicroserviceManifest
     */
    protected YourMicroserviceManifest createDefaultManifest() {
        /**
         * Create our Manifest
         */
        Map<String, Object> manifestData = new HashMap<>();
        manifestData.put("COUNTRIES_ALLOWED", new String[]{"us", "ca"});
        manifestData.put("REGIONS_ALLOWED", "All");
        return createManifest(manifestData);
    }

    /**
     * createManifest
     *
     * @param manifestData Your Microservice Manifest Map.
     * @return YourMicroserviceManifest
     */
    protected YourMicroserviceManifest createManifest(Map<String, Object> manifestData) {
        return new YourMicroserviceManifest(UUID.randomUUID().toString(), manifestData);
    }

    /**
     * verifyToken
     * Performs the JWT validation for an incoming Token against User Details Obtained.
     *
     * @param token JWT to be Validated
     * @return JWTClaimsSet if not null, indicates Token was Verified.
     * @throws YourMicroserviceInvalidTokenException If Token does not Validate per Your Microservice Standards.
     */
    @Override
    public JWTClaimsSet verifyToken(String token) throws YourMicroserviceInvalidTokenException {
        if (token == null || token.isEmpty()) {
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_NO_TOKEN_SUPPLIED);
        }
        /**
         * Phase One of Validation:
         * Parse the JWE Token String into a JWEObject.
         */
        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(token);
        } catch (ParseException pe) {
            LOGGER.warn("{}Unable to Validate Token due to Parsing Exception: '{}', Denying Access.",
                    LOGGING_HEADER, pe.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_UNABLE_TO_PARSE_TOKEN, pe);
        }
        /**
         * Phase Two of Validation:
         * Decrypt the JWE
         */
        try {
            jweObject.decrypt(new DirectDecrypter(secretKey.getEncoded()));
        } catch (KeyLengthException kle) {
            LOGGER.warn("{}Unable to Validate Token due to Decryption Failure: '{}', Denying Access.",
                    LOGGING_HEADER, kle.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_UNABLE_TO_DECRYPT_TOKEN, kle);
        } catch (JOSEException je) {
            LOGGER.warn("{}Unable to Validate Token due to JOSE Failure: '{}', Denying Access.",
                    LOGGING_HEADER, je.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_UNABLE_TO_DECRYPT_TOKEN, je);
        }
        /**
         * Phase Three of Validation:
         * Extract the Signature from the JWE Decrypted Payload.
         */
        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
        if (signedJWT == null) {
            LOGGER.warn("{}Unable to Validate Token, Payload was not Signed, Denying Access",
                    LOGGING_HEADER);
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_TOKEN_SIGNATURE_INVALID);
        }
        /**
         * Phase Four of Validation:
         * Validate the HMAC Signature from JWE Decrypted Payload.
         */
        try {
            if (!signedJWT.verify(new MACVerifier(secretKey.getEncoded()))) {
                LOGGER.warn("{}Token did not Validate, Denying Access.", LOGGING_HEADER);
                throw new YourMicroserviceInvalidTokenException(V_MESSAGE_TOKEN_SIGNATURE_INVALID);
            }
        } catch (JOSEException je) {
            LOGGER.warn("{}Unable to Validate Token due to JOSE Failure: '{}', Denying Access.", LOGGING_HEADER, je.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_TOKEN_SIGNATURE_INVALID, je);
        }
        /**
         * Phase Five of Validation:
         * Verify Payload from JWE Decrypted Payload.
         */
        JWTClaimsSet claimsSet;
        try {
            JWTClaimsVerifier verifier = getYourMicroserviceClaimsVerifier();
            claimsSet = signedJWT.getJWTClaimsSet();
            verifier.verify(claimsSet);
        } catch (ParseException pe) {
            LOGGER.warn("{}Token did not Verify:'{}', Denying Access.",
                    LOGGING_HEADER, pe.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN, pe);
        } catch (BadJWTException je) {
            LOGGER.warn("{}Bad JWT:'{}', Denying Access.",
                    LOGGING_HEADER, je.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN, je);
        }
        /**
         * Read back our Manifest to perform any additional Validation....
         * Instead of walking the JSONObject, which maybe faster, we rehydrate the manifest into
         * it's POJO form.
         */
        try {
            String yms = ((JSONObject) claimsSet.getClaim("yms")).toJSONString();
            YourMicroserviceManifest yourMicroserviceManifest = MAPPER.readValue(yms, YourMicroserviceManifest.class);
            /**
             * Iterate over All Keys.
             */
            boolean MANIFEST_ID_FOUND = false;
            boolean REGIONS_ALLOWED_FOUND = false;
            boolean COUNTRIES_ALLOWED_FOUND = false;
            if (isUUIDValid(yourMicroserviceManifest.getId())) {
                MANIFEST_ID_FOUND = true;
            }
            for (String key : yourMicroserviceManifest.getData().keySet()) {
                if (key.equalsIgnoreCase("REGIONS_ALLOWED")) {
                    REGIONS_ALLOWED_FOUND = true;
                } else if (key.equalsIgnoreCase("COUNTRIES_ALLOWED")) {
                    COUNTRIES_ALLOWED_FOUND = true;
                }
            }
            /**
             * Ensure what Keys we need are available, if not, not a valid Token.
             */
            if (MANIFEST_ID_FOUND && REGIONS_ALLOWED_FOUND && COUNTRIES_ALLOWED_FOUND) {
                /**
                 * Token has been Verified and Deduced to be Valid, Allow Access.
                 */
                return claimsSet;
            } else {
                LOGGER.warn("{}Token did not Verify Your Microservice Manifest:'{}', Denying Access.",
                        LOGGING_HEADER);
                throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN_YOUR_MICROSERVICE_MANIFEST);
            }
        } catch (JsonMappingException jme) {
            LOGGER.warn("{}Token did not Verify Your Microservice Manifest:'{}', Denying Access.",
                    LOGGING_HEADER, jme.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN_YOUR_MICROSERVICE_MANIFEST, jme);
        } catch (JsonParseException jpe) {
            LOGGER.warn("{}Token did not Verify Your Microservice Manifest:'{}', Denying Access.",
                    LOGGING_HEADER, jpe.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN_YOUR_MICROSERVICE_MANIFEST, jpe);
        } catch (IOException ioe) {
            LOGGER.warn("{}Token did not Verify Your Microservice Manifest:'{}', Denying Access.",
                    LOGGING_HEADER, ioe.getMessage());
            throw new YourMicroserviceInvalidTokenException(V_MESSAGE_INVALID_TOKEN_YOUR_MICROSERVICE_MANIFEST, ioe);
        }
    }

    /**
     * parseJWT
     * Parse JWT and  Display Token Information in Logs.
     *
     * @param jwt Token to be Parsed
     */
    @Override
    public void parseAndDumpJWT(final String jwt) throws Exception {
        /**
         * This line will throw an exception if it is not a signed JWS (as expected)
         */
        JWTClaimsSet claimsSet = verifyToken(jwt);
        if (claimsSet == null) {
            LOGGER.error("{}No Claims Set returned, Invalid Token.",LOGGING_HEADER);
            return;
        }
        LOGGER.info("{} Dumping JWT: '{}'", LOGGING_HEADER, jwt);
        for(String claimKey : claimsSet.getClaims().keySet()) {
            LOGGER.info("{} ++ Claim '{}' = '{}'", LOGGING_HEADER,
                    claimKey, claimsSet.getClaims().get(claimKey));
        }
    }

    /**
     * getYourMicroserviceClaimsVerifier
     * Obtains our Standard Claims Verifier.
     *
     * @return JWTClaimsVerifier Claims Verifier to be performed against a Claims Set.
     */
    protected JWTClaimsVerifier getYourMicroserviceClaimsVerifier() {
        /**
         * Default JWT claims verifier. This class is thread-safe.
         *
         * Performs the following checks:
         *
         * + If an expiration time (exp) claim is present, makes sure it is ahead of the current time, else the JWT claims set is rejected.
         * + If a not-before-time (nbf) claim is present, makes sure it is before the current time, else the JWT claims set is rejected.
         *  This class may be extended to perform additional checks.
         */
        return new DefaultJWTClaimsVerifier() {
            @Override
            public void verify(JWTClaimsSet claimsSet)
                    throws BadJWTException {
                /**
                 * Verify the Expiration of the Token and Not Before Use.
                 */
                super.verify(claimsSet);
                /**
                 * Ensure Correct Issuer is from our own Eco-System.
                 */
                String issuer = claimsSet.getIssuer();
                if (issuer == null || !issuer.equals(YourMicroserviceToken.YOUR_ORGANIZATION_ISSUER)) {
                    throw new BadJWTException("Invalid Token issuer");
                }
                /**
                 * Ensure Subject Specified.
                 */
                String subject = claimsSet.getSubject();
                if (subject == null || subject.isEmpty()) {
                    throw new BadJWTException("Invalid Token Subject");
                }
                /**
                 * Ensure Subject Specified.
                 */
                String jti = claimsSet.getJWTID();
                if (!isUUIDValid(jti)) {
                    throw new BadJWTException("Invalid Token Identifier");
                }
                /**
                 * Validate Audience, we need at least Once Specified.
                 */
                if (claimsSet.getAudience() == null || claimsSet.getAudience().isEmpty()) {
                    throw new BadJWTException("Invalid Audience");
                }
                /**
                 * Ensure Your Microservice was Specified.
                 */
                JSONObject yms = (JSONObject) claimsSet.getClaim(CLAIM_NAME_YOUR_MICROSERVICE);
                if (yms == null || yms.isEmpty()) {
                    throw new BadJWTException("Invalid Your Microservice Claim");
                }
                /**
                 * Add Additional Claims Verification Here if and when Applicable...
                 */
            }
        };
    }

    /**
     * Validate a Your Microservice Generated UUID.
     * @param uuid To be Validated.
     * @return boolean inidcator if UUID if valid or not.
     */
    protected boolean isUUIDValid(final String uuid) {
        if (uuid == null || uuid.trim().isEmpty() || uuid.trim().length() != 36) {
            return false;
        }
        try{
            UUID.fromString(uuid.trim());
            return true;
        } catch (IllegalArgumentException exception) {
            LOGGER.error("Error in Validating UUID:'{}', {}", uuid, exception.getMessage());
            return false;
        }
    }

    /**
     * Unused Private Method...
     *
     protected Boolean ignoreTokenExpiration(String token) {
     String audience = getAudienceFromToken(token);
     return AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience);
     }
     **/

    /**
     * UnUsed Public Method.....
     public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
     final Date created = getCreatedDateFromToken(token);
     return !(isCreatedBeforeLastPasswordReset(created, lastPasswordReset)) &&
     !(isTokenExpired(token)) || ignoreTokenExpiration(token);
     }
     **/

    /**
     * // TODO :: This needs to be Addressed....
     * protected Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
     * return lastPasswordReset != null && created.before(lastPasswordReset);
     * }
     */
}

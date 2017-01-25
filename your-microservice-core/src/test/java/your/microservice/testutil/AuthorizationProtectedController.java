package your.microservice.testutil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/{server}/{version}/test")
public class AuthorizationProtectedController {

    /**
     * This is an example of some different kinds of granular restriction for endpoints. You can use the built-in SPEL expressions
     * in @PreAuthorize such as 'hasRole()' to determine if a user has access. However, if you require logic beyond the methods
     * Spring provides then you can encapsulate it in a security and register it as a bean to use it within the annotation as
     * demonstrated below with 'securityService'.
     **/
    @RequestMapping(value = "/protected", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getDaHoney() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", 42);
        resultMap.put("somethingElse", "Another Protected Value");
        return ResponseEntity.ok(resultMap);
    }

    @RequestMapping(value = "/useraccess", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserAccessibleResource() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", 42);
        resultMap.put("somethingElse", "Another User Accessible Value");
        return ResponseEntity.ok(resultMap);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get() {
        return ResponseEntity.ok("OK");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> post() {
        return ResponseEntity.ok("OK");
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> put() {
        return ResponseEntity.ok("OK");
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete() {
        return ResponseEntity.ok("OK");
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    @ResponseBody
    public String resolveAccessDeniedExceptions() {
        return "forbidden";
    }


}

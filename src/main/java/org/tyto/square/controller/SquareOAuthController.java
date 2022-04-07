package org.tyto.square.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.tyto.square.service.SquareService;
import org.tyto.square.util.FrontendUtil;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://connect.squareupsandbox.com", "https://connect.squareupsandbox.com"})
@RestController
@RequestMapping("/square/oauth")
public class SquareOAuthController {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final SquareService squareService;
    private final FrontendUtil frontendUtil;

    @Autowired
    public SquareOAuthController(SquareService squareService, FrontendUtil frontendUtil) {
        this.squareService = squareService;
        this.frontendUtil = frontendUtil;
    }

    private ResponseEntity<?> buildRedirectResponse(String location) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).header(HttpHeaders.LOCATION, location).build();
    }

    @GetMapping("/start")
    public ResponseEntity<?> startOAuthFlow() {
        String redirectUrl = squareService.startSquareOAuthFlow();
        return buildRedirectResponse(redirectUrl);
    }

    @GetMapping("/finish")
    public Mono<ResponseEntity<?>> finishOAuthFlow(@Nullable @RequestParam("code") String code,
                                                   @Nullable @RequestParam("state") Long stateId,
                                                   @Nullable @RequestParam("error") String error,
                                                   @Nullable @RequestParam("error_description") String errorDescription) {
        if (code == null || stateId == null) {
            log.error("Authorization failed for Square, error: '{}' reason: '{}}'", error, errorDescription);
            return Mono.just(buildRedirectResponse(frontendUtil.getFailureUri()));
        }

        return squareService.finishSquareOAuthFlow(code, stateId)
                .onErrorResume(err -> {
                    log.error("Failed approval due to: " + err.getMessage());
                    return Mono.just(false);
                })
                .map(authSuccess -> {
                    if (authSuccess) {
                        return buildRedirectResponse(frontendUtil.getSuccessUri());
                    } else {
                        return buildRedirectResponse(frontendUtil.getFailureUri());
                    }
                });
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthorizationStatus() {
        boolean authorized = squareService.checkIfAuthorized();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("authorized", authorized);
        return ResponseEntity.ok(root);
    }

    @GetMapping("/revoke")
    public Mono<ResponseEntity<?>> revokeAccessToSquare() {
        return squareService.revokeAccessToken()
                .map(accessRevoked -> {
                    ObjectNode root = MAPPER.createObjectNode();
                    root.put("revoked", accessRevoked);
                    return ResponseEntity.ok(root);
                });
    }
}

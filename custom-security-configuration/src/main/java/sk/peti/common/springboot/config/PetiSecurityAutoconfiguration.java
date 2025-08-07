package sk.peti.common.springboot.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@AutoConfiguration
@EnableWebSecurity
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Profile("!dev")
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerJwtConfiguration.OAuth2SecurityFilterChainConfiguration")
public class PetiSecurityAutoconfiguration {

    private final String uriClaimRoles;
    private final String jwtIssuerUri;

    public PetiSecurityAutoconfiguration(
            Environment environment,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String jwtIssuerUri) {
        var applicationName = Optional.ofNullable(environment.getProperty("APPLICATION_NAME")).orElseThrow();
        this.uriClaimRoles = environment.getProperty(applicationName + ".request.mapping.uris.jwt.claim-roles");
        this.jwtIssuerUri = jwtIssuerUri;
    }

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http, final JwtDecoder jwtDecoder) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.requestMatchers("/v1/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new JwtToAuthenticationTokenConverter(uriClaimRoles);
    }

    @Bean
    @Profile("!test")
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(jwtIssuerUri);
    }
}

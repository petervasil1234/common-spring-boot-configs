package sk.peti.common.springboot.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

record JwtToAuthenticationTokenConverter(String uriClaimRoles) implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(final Jwt jwt) {
        final Collection<GrantedAuthority> authorities = new ArrayList<>();

        final List<String> permissions = jwt.getClaimAsStringList("permissions");
        if (permissions != null) {
            authorities.addAll(
                    permissions.stream().map(SimpleGrantedAuthority::new).toList());
        }

        final List<String> roles = jwt.getClaimAsStringList(uriClaimRoles);
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList());
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}

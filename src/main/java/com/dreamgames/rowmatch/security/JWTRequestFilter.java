package com.dreamgames.rowmatch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class JWTRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JWTUtils jwtUtils;

	@Autowired
	CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain
	) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		String username = null;
		String jwt = null;

		if (
			authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
		) {
			jwt = authorizationHeader.substring(7);
			username = jwtUtils.extractUsername(jwt);
		}

		if (
			username != null &&
			SecurityContextHolder.getContext().getAuthentication() == null
		) {
			UserDetails userDetails =
				this.userDetailsService.loadUserByUsername(username);
			if (jwtUtils.validateToken(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
        chain.doFilter(request, response);
	}
}

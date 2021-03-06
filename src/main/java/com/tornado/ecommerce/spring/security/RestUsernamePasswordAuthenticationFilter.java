package com.tornado.ecommerce.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tornado.ecommerce.common.exception.EcommerceException;
import com.tornado.ecommerce.common.utils.Encryptor;
import com.tornado.ecommerce.model.entity.User;

public class RestUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String token = obtainToken(request);
		User extractUserFromToken = Encryptor.getInstance().decrypt(token);
		String username = extractUserFromToken.getLoginName();
		String password = extractUserFromToken.getFirstName();

		if (username == null || password == null) {
			throw new EcommerceException("token does not correct");
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.customAuthenticationProvider.authenticate(authRequest);
	}

	protected String obtainToken(HttpServletRequest request) {
		String token = request.getHeader("Token");
		if(token == null)
			throw new EcommerceException("token does not exist");
		return token;
	}

/*	@Override
	protected String obtainPassword(HttpServletRequest request) {
		return request.getHeader("j_password");
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		return request.getHeader("j_username");
	}
*/
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Authentication auth = attemptAuthentication(httpRequest, httpResponse);
		SecurityContextHolder.getContext().setAuthentication(auth);

		chain.doFilter(request, response);

	}
	
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }

}

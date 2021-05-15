package io.swagger.config.server;

import io.swagger.config.authentication.UniqueAuthenticationKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(ServerSecurityConfig.class)
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AuthServerOAuth2Config.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder oauthClientPasswordEncoder;

//    @Autowired
//    private TokenStore tokenStore;
//    @Autowired
//    private TokenEndpoint tokenEndpoint;

//    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
//    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();
//    private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();

    @Bean
    public TokenStore tokenStore() {
        JdbcTokenStore tokenStore = new JdbcTokenStore(dataSource);
        tokenStore.setAuthenticationKeyGenerator(new UniqueAuthenticationKeyGenerator());
        return tokenStore;
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(oauthClientPasswordEncoder);
//                .addTokenEndpointAuthenticationFilter(new CustomTokenFilter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager).userDetailsService(userDetailsService);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds(0);

        endpoints.tokenServices( tokenServices );
    }

//    public class CustomTokenFilter implements Filter {
//
//        @Override
//        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//            final HttpServletRequest request = (HttpServletRequest) req;
//            final HttpServletResponse response = (HttpServletResponse) res;
//
//            try {
//
//                Authentication authentication = tokenExtractor.extract(request);
//
//                if (authentication == null) {
//                    if (isAuthenticated()) {
////                            if (debug) {
//                        logger.debug("Clearing security context.");
////                            }
//                        SecurityContextHolder.clearContext();
//                    }
////                        if (debug) {
//                    logger.debug("No token in request, will continue chain.");
////                        }
//                }
//                else {
//                    request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());
//                    if (authentication instanceof AbstractAuthenticationToken) {
//                        AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
//                        needsDetails.setDetails(authenticationDetailsSource.buildDetails(request));
//                    }
//                    Authentication authResult = authenticationManager.authenticate(authentication);
//
////                        if (debug) {
//                    logger.debug("Authentication success: " + authResult);
////                        }
//
////                        eventPublisher.publishAuthenticationSuccess(authResult);
//                    SecurityContextHolder.getContext().setAuthentication(authResult);
//
//                }
//            }
//            catch (OAuth2Exception failed) {
//                SecurityContextHolder.clearContext();
//
////                    if (debug) {
//                logger.debug("Authentication request failed: " + failed);
////                    }
////                    eventPublisher.publishAuthenticationFailure(new BadCredentialsException(failed.getMessage(), failed),
////                            new PreAuthenticatedAuthenticationToken("access-token", "N/A"));
//
//                authenticationEntryPoint.commence(request, response,
//                        new InsufficientAuthenticationException(failed.getMessage(), failed));
//
//                return;
//            }
//
//            chain.doFilter(request, response);
//        }
//
//        private boolean isAuthenticated() {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//                return false;
//            }
//            return true;
//        }
//    }
}

package com.icodesoft.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2DeviceCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.ObjectUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationConfig {
    /**
     * jwk set缓存前缀
     */
    public static final String AUTHORIZATION_JWS_PREFIX_KEY = "authorization_jws";

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        // 新建设备码 OAuth2DeviceCodeAuthenticationConverter 和 OAuth2DeviceCodeAuthenticationProvider OAuth2DeviceCodeAuthenticationToken
//

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
                                .authorizationEndpoint(authorizationEndpointCustomizer -> authorizationEndpointCustomizer.consentPage(CUSTOM_CONSENT_PAGE_URI))
//                                // 设置设备码用户验证url(自定义用户验证页)
//                                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
//                                        deviceAuthorizationEndpoint.verificationUri("/activate")
//                                )
//                                // 设置验证设备码用户确认页面
//                                .deviceVerificationEndpoint(deviceVerificationEndpoint ->
//                                        deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
//                                )
//                                .clientAuthentication(clientAuthentication ->
//                                        // 客户端认证添加设备码的converter和provider
//                                        clientAuthentication
//                                                .authenticationConverter(OAuth2DeviceCodeAuthenticationConverter)
//                                                .authenticationProvider(new OAuth2DeviceCodeAuthenticationProvider())
//                                )
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )


                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // 使用JWT处理接收到的access token
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // client id
                .clientId("testclient")
                // client secret
//                .clientSecret(passwordEncoder.encode("123456"))
                .clientSecret("{noop}secret2")
                // 客户端基于请求头的认证
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                //客户端获取授权时支持的授权类型
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 授权码模式回调地址
                .redirectUri("http://localhost:8080/index")
                .redirectUri("https://www.baidu.com")
                // OPENID的scope时返回IdToken
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                // setting client and require auth consent
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).requireProofKey(true).build())
                .tokenSettings(TokenSettings.builder()
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofSeconds(300))
                        .authorizationCodeTimeToLive(Duration.ofSeconds(300))
                        .reuseRefreshTokens(true)
                        .refreshTokenTimeToLive(Duration.ofSeconds(3600))
                        .accessTokenTimeToLive(Duration.ofSeconds(300))
                        .build())
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        // init client
        RegisteredClient client = registeredClientRepository.findByClientId(registeredClient.getClientId());
        if (client == null) {
            registeredClientRepository.save(registeredClient);
        }
        // device code client
//        RegisteredClient deviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                .clientId("device-message-client")
//                // public client
//                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
//                // device code grant
//                .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                // customer scope
//                .scope("message.read")
//                .scope("message.write")
//                .build();
//        RegisteredClient byClientId = registeredClientRepository.findByClientId(deviceClient.getClientId());
//        if (byClientId == null) {
//            registeredClientRepository.save(deviceClient);
//        }

//        // PKCE client
//        RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                .clientId("pkce-client")
//                // 公共客户端
//                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
//                // 授权码模式，因为是扩展授权码流程，所以流程还是授权码的流程，改变的只是参数
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                // 授权码模式回调地址，oauth2.1已改为精准匹配，不能只设置域名，并且屏蔽了localhost，本机使用127.0.0.1访问
//                .redirectUri("http://localhost:8080/index")
//                .clientSettings(ClientSettings.builder().requireProofKey(Boolean.TRUE).build())
//                // 自定scope
//                .scope("message.read")
//                .scope("message.write")
//                .build();
//        RegisteredClient findPkceClient = registeredClientRepository.findByClientId(pkceClient.getClientId());
//        if (findPkceClient == null) {
//            registeredClientRepository.save(pkceClient);
//        }

        return registeredClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // get jwkset from reids
//        String jwkSetCache = redisOperator.get(RedisConstants.AUTHORIZATION_JWS_PREFIX_KEY);
//        if (!ObjectUtils.isEmpty(jwkSetCache)) {
//            JWKSet jwkSet = JWKSet.parse(jwkSetCache);
//            return new ImmutableJWKSet<>(jwkSet);
//        }

        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
//        // convert json to string
//        String jwkSetString = jwkSet.toString(Boolean.FALSE);
//        // save to redis
//        redisOperator.set(RedisConstants.AUTHORIZATION_JWS_PREFIX_KEY, jwkSetString);

        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}

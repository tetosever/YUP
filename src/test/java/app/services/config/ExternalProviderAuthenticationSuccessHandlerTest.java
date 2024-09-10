package app.services.config;

import app.YUP.model.ExternalUser;
import app.YUP.service.ExternalUserService;
import app.services.model.ExternalProviderUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalProviderAuthenticationSuccessHandlerTest {

    @Mock
    private ExternalUserService externalUserService;

    @InjectMocks
    private ExternalProviderAuthenticationSuccessHandler successHandler;

    @Mock
    private Authentication authentication;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ExternalProviderUser externalProviderUser;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        OAuth2User oAuth2User = mock(OAuth2User.class);
        ExternalProviderUser providerUser = new ExternalProviderUser(oAuth2User, "google");

        ExternalUser expectedUser = new ExternalUser();
        expectedUser.setEmail("john.doe@example.com");

        when(authentication.getPrincipal()).thenReturn(externalProviderUser);
    }

    @Test
    void onAuthenticationSuccess_CreatesExternalUserAndRedirects() throws Exception {
        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(externalUserService).createExternalUser(response, externalProviderUser);
    }
}
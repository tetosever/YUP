package app.services.service;

import app.services.model.ExternalProviderUser;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class ExternalProviderUserService extends DefaultOAuth2UserService {

    /**
     * Overrides the loadUser method from DefaultOAuth2UserService to create an instance of ExternalProviderUser.
     * This class extends DefaultOAuth2UserService and is used to handle the user information returned from external providers.
     *
     * @param oAuth2UserRequest The OAuth2UserRequest object containing information about the user and the client.
     * @return An instance of OAuth2User representing the user information.
     * @throws OAuth2AuthenticationException If an error occurs during the user information retrieval process.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        return new ExternalProviderUser(super.loadUser(oAuth2UserRequest),
                oAuth2UserRequest.getClientRegistration().getRegistrationId());
    }
}

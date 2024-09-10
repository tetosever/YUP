package app.services.config;

import app.YUP.service.ExternalUserService;
import app.services.model.ExternalProviderUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExternalProviderAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ExternalUserService externalUserService;

    /**
     * This method is called when the user successfully authenticates with an external provider.
     * It retrieves the {@link ExternalProviderUser} from the authentication object, creates a new user in the system,
     * and sets the default target URL to "/event/all".
     *
     * @param request The {@link HttpServletRequest} object representing the incoming request.
     * @param response The {@link HttpServletResponse} object representing the response to be sent.
     * @param authentication The {@link Authentication} object representing the authenticated user.
     * @throws IOException If an input or output error occurs.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        ExternalProviderUser externalProviderUser = (ExternalProviderUser) authentication.getPrincipal();
        externalUserService.createExternalUser(response, externalProviderUser);
        setDefaultTargetUrl("/event/all");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

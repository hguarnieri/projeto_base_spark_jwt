package authentication;

import dao.UserDAO;
import model.User;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;

public class UsernameAndPasswordValidator implements UsernamePasswordAuthenticator {

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        final HttpProfile profile = new HttpProfile();
        UserDAO dao = new UserDAO();

        if (credentials == null) {
            throwsException("No credential");
        }
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            throwsException("Username cannot be blank");
        }
        if (CommonHelper.isBlank(password)) {
            throwsException("Password cannot be blank");
        }

        if (!dao.authenticate(username, password)) {
            throwsException("Username : '" + username + "' does not match password");
        } else {
            // Obtem os dados do usuário
            User user = dao.getUser(username);

            // Utiliza os dados no perfil
            profile.setId(user.getUsername());
            profile.addAttribute(CommonProfile.USERNAME, username);
            profile.addRoles(user.getRoles());

            // Define o perfil para a sessão
            credentials.setUserProfile(profile);
        }
    }

    protected void throwsException(final String message) {
        throw new CredentialsException(message);
    }
}
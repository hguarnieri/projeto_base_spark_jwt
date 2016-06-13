package authentication;

import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import spark.TemplateEngine;

public class ConfigFactory implements org.pac4j.core.config.ConfigFactory {

    private final String salt;

    private final TemplateEngine templateEngine;

    public ConfigFactory(final String salt, final TemplateEngine templateEngine) {
        this.salt = salt;
        this.templateEngine = templateEngine;
    }

    @Override
    public Config build() {
        // REST authent with JWT for a token passed in the header
        HeaderClient headerClient = new HeaderClient(new CustomJwtAuthenticator(salt));
        headerClient.setHeaderName("Authorization");
        headerClient.setPrefixHeader("Bearer ");

        // Basic auth
        final DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new UsernameAndPasswordValidator());
        final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient(new UsernameAndPasswordValidator());

        final Clients clients = new Clients("http://localhost:8080/callback", headerClient, directBasicAuthClient, indirectBasicAuthClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("user", new RequireAnyRoleAuthorizer("ROLE_USER"));
        config.setHttpActionAdapter(new HttpActionAdapter(templateEngine));
        return config;
    }
}
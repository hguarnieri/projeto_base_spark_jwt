import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.sparkjava.ApplicationLogoutRoute;
import org.pac4j.sparkjava.RequiresAuthenticationFilter;
import org.pac4j.sparkjava.SparkWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private final static String JWT_SALT = "12345678901234567890123456789012";

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private final static MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

    public static void main(String[] args) {
        // Define o diretório padrão de arquivos estáticos
        staticFileLocation(".");
        // Define a porta que o servidor irá rodar
        port(8080);

        // Cria um arquivo de configuração
        final Config config = new DemoConfigFactory(JWT_SALT, templateEngine).build();

        // Protege as páginas
        before("/rest-jwt", new RequiresAuthenticationFilter(config, "HeaderClient"));
        before("/jwt", new RequiresAuthenticationFilter(config, "DirectBasicAuthClient"));

        // Cria os caminhos
        get("/basicauth", Main::protectedIndex, templateEngine);
        get("/jwt", Main::jwt, templateEngine);
        get("/rest-jwt", Main::protectedIndex, templateEngine);
        get("/logout", new ApplicationLogoutRoute(config));

        // Mostra exceções
        exception(Exception.class, (e, request, response) -> {
            logger.error("Unexpected exception", e);
            response.body(templateEngine.render(new ModelAndView(new HashMap<>(), "error500.mustache")));
        });
    }

    private static ModelAndView protectedIndex(final Request request, final Response response) {
        final Map map = new HashMap();
        map.put("profile", getUserProfile(request, response));
        return new ModelAndView(map, "protectedIndex.mustache");
    }

    private static UserProfile getUserProfile(final Request request, final Response response) {
        final SparkWebContext context = new SparkWebContext(request, response);
        final ProfileManager manager = new ProfileManager(context);
        return manager.get(true);
    }

    private static ModelAndView jwt(final Request request, final Response response) {
        final UserProfile profile = getUserProfile(request, response);
        JwtGenerator generator = new JwtGenerator(JWT_SALT);
        String token = "";
        if (profile != null) {
            token = generator.generate(profile);
        }
        final Map map = new HashMap();
        map.put("token", token);
        return new ModelAndView(map, "jwt.mustache");
    }
}

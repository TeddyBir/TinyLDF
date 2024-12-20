package foo;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(name = "QuadQuery", urlPatterns = { "/quads" })
public class QuadQuery extends HttpServlet {

    // Méthode pour récupérer des quads avec des filtres de type sujet, prédicat, objet, et graphe
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Récupérer les paramètres de la requête pour le filtre
        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String graph = request.getParameter("graph");
        String pageToken = request.getParameter("pageToken");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query("Quad");
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);

        // Appliquer des filtres si les paramètres sont fournis
        if (subject != null) {
            query.setFilter(new Query.FilterPredicate("subject", Query.FilterOperator.EQUAL, subject));
        }
        if (predicate != null) {
            query.setFilter(new Query.FilterPredicate("predicate", Query.FilterOperator.EQUAL, predicate));
        }
        if (object != null) {
            query.setFilter(new Query.FilterPredicate("object", Query.FilterOperator.EQUAL, object));
        }
        if (graph != null) {
            query.setFilter(new Query.FilterPredicate("graph", Query.FilterOperator.EQUAL, graph));
        }

        // Appliquer un curseur pour la pagination si un pageToken est passé
        if (pageToken != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(pageToken));
        }

        PreparedQuery pq = datastore.prepare(query);
        List<Entity> results = pq.asList(fetchOptions);

        // Construire la réponse JSON
        StringBuilder jsonResponse = new StringBuilder("[");
        for (Entity entity : results) {
            String subjectResult = (String) entity.getProperty("subject");
            String predicateResult = (String) entity.getProperty("predicate");
            String objectResult = (String) entity.getProperty("object");
            String graphResult = (String) entity.getProperty("graph");

            jsonResponse.append(String.format("{\"subject\": \"%s\", \"predicate\": \"%s\", \"object\": \"%s\", \"graph\": \"%s\"},",
                    subjectResult, predicateResult, objectResult, graphResult));
        }

        if (results.size() > 0) {
            jsonResponse.deleteCharAt(jsonResponse.length() - 1);  // Supprimer la virgule finale
        }

        // Ajouter le curseur pour la pagination suivante
        String nextPageToken = null;
        if (results.size() > 0) {
            nextPageToken = results.get(results.size() - 1).getKey().getName();
        }

        jsonResponse.append("{\"nextPageToken\": \"" + nextPageToken + "\"}");

        // Répondre avec les résultats
        response.getWriter().print(jsonResponse.toString());
    }

    // Méthode pour insérer un nouveau quad dans Datastore
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Vérifier si l'utilisateur est authentifié avant d'insérer
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user == null) {
            response.getWriter().print("{\"error\": \"User is not authenticated.\"}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Récupérer les paramètres de la requête pour le quad
        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String graph = request.getParameter("graph");

        if (subject == null || predicate == null || object == null || graph == null) {
            response.getWriter().print("{\"error\": \"Missing required fields\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Créer un nouvel Entity pour le quad
        Entity quadEntity = new Entity("Quad");
        quadEntity.setProperty("subject", subject);
        quadEntity.setProperty("predicate", predicate);
        quadEntity.setProperty("object", object);
        quadEntity.setProperty("graph", graph);

        // Ajouter un identifiant pour l'utilisateur
        quadEntity.setProperty("user", user.getEmail());

        // Enregistrer le quad dans Datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(quadEntity);

        // Répondre avec un message de succès
        response.getWriter().print("{\"status\": \"Quad inserted successfully\"}");
    }
}

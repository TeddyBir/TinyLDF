package main.java;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;

@WebServlet(name = "QuadQuery", urlPatterns = { "/quads" })
public class QuadQuery extends HttpServlet {

    // La méthode doGet pour rechercher les quads par sujet, prédicat, ou objet
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String graph = request.getParameter("graph");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Créer une requête pour trouver des quads dans Datastore
        Query query = new Query("Quad");

        // Appliquer les filtres si les paramètres sont fournis
        if (subject != null) {
            query.setFilter(new FilterPredicate("subject", FilterOperator.EQUAL, subject));
        }
        if (predicate != null) {
            query.setFilter(new FilterPredicate("predicate", FilterOperator.EQUAL, predicate));
        }
        if (object != null) {
            query.setFilter(new FilterPredicate("object", FilterOperator.EQUAL, object));
        }
        if (graph != null) {
            query.setFilter(new FilterPredicate("graph", FilterOperator.EQUAL, graph));
        }

        PreparedQuery pq = datastore.prepare(query);
        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());

        // Construire la réponse JSON
        StringBuilder jsonResponse = new StringBuilder("[");
        for (Entity entity : results) {
            Quad quad = new Quad(
                (String) entity.getProperty("subject"),
                (String) entity.getProperty("predicate"),
                (String) entity.getProperty("object"),
                (String) entity.getProperty("graph")
            );
            jsonResponse.append(quad.toJson()).append(",");
        }

        if (results.size() > 0) {
            jsonResponse.deleteCharAt(jsonResponse.length() - 1);  // Supprimer la virgule finale
        }

        jsonResponse.append("]");
        
        response.getWriter().print(jsonResponse.toString());
    }

    // La méthode doPost pour insérer un nouveau quad
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Récupérer les données du quad depuis la requête
        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String graph = request.getParameter("graph");

        if (subject == null || predicate == null || object == null || graph == null) {
            response.getWriter().print("{\"error\": \"Missing required fields\"}");
            return;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Créer un nouvel Entity pour un quad
        Entity quadEntity = new Entity("Quad");
        quadEntity.setProperty("subject", subject);
        quadEntity.setProperty("predicate", predicate);
        quadEntity.setProperty("object", object);
        quadEntity.setProperty("graph", graph);

        // Enregistrer le quad dans Datastore
        datastore.put(quadEntity);

        // Répondre à la demande avec un message de succès
        response.getWriter().print("{\"status\": \"Quad inserted successfully\"}");
    }
}


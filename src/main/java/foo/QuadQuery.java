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
    // Method for handling GET requests and returning the list of quads in JSON format
    @Override
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // Retrieve the query parameters
    String subject = request.getParameter("subject");
    String predicate = request.getParameter("predicate");
    String object = request.getParameter("object");
    String graph = request.getParameter("graph");
    String pageToken = request.getParameter("pageToken");

    // Prepare Datastore service and query
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Quad");
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);

    // Apply filters based on query parameters
    if (subject != null && !subject.isEmpty()) {
        query.setFilter(new Query.FilterPredicate("subject", Query.FilterOperator.EQUAL, subject));
    }
    if (predicate != null && !predicate.isEmpty()) {
        query.setFilter(new Query.FilterPredicate("predicate", Query.FilterOperator.EQUAL, predicate));
    }
    if (object != null && !object.isEmpty()) {
        query.setFilter(new Query.FilterPredicate("object", Query.FilterOperator.EQUAL, object));
    }
    if (graph != null && !graph.isEmpty()) {
        query.setFilter(new Query.FilterPredicate("graph", Query.FilterOperator.EQUAL, graph));
    }

    // Apply pagination if pageToken is provided
    if (pageToken != null) {
        fetchOptions.startCursor(Cursor.fromWebSafeString(pageToken));
    }

    // Prepare the query and fetch results
    PreparedQuery pq = datastore.prepare(query);
    List<Entity> results = pq.asList(fetchOptions);

    // Build the JSON response
    StringBuilder jsonResponse = new StringBuilder("{\"results\": [");

    for (Entity entity : results) {
        String subjectResult = (String) entity.getProperty("subject");
        String predicateResult = (String) entity.getProperty("predicate");
        String objectResult = (String) entity.getProperty("object");
        String graphResult = (String) entity.getProperty("graph");

        jsonResponse.append(String.format("{\"subject\": \"%s\", \"predicate\": \"%s\", \"object\": \"%s\", \"graph\": \"%s\"},",
                subjectResult, predicateResult, objectResult, graphResult));
    }

    // Remove the trailing comma, if there are results
    if (results.size() > 0) {
        jsonResponse.deleteCharAt(jsonResponse.length() - 1);  // Remove the trailing comma
    }

    // Add nextPageToken for pagination (if any)
    String nextPageToken = (results.size() > 0) ? results.get(results.size() - 1).getKey().getName() : null;

    // Close the JSON array and append the nextPageToken object properly inside the results object
    jsonResponse.append("],");
    jsonResponse.append("\"nextPageToken\": \"" + (nextPageToken != null ? nextPageToken : "") + "\"");

    // Close the response object
    jsonResponse.append("}");

    // Output the final JSON response
    response.getWriter().print(jsonResponse.toString());
}

    

    // Méthode pour insérer un nouveau quad dans Datastore
    @Override
public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // Retrieve the parameters for the quad
    String subject = request.getParameter("subject");
    String predicate = request.getParameter("predicate");
    String object = request.getParameter("object");
    String graph = request.getParameter("graph");

    // Check if any required parameters are missing
    if (subject == null || predicate == null || object == null || graph == null) {
        response.getWriter().print("{\"error\": \"Missing required fields\"}");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
    }

    // Create a new entity for the quad
    Entity quadEntity = new Entity("Quad");
    quadEntity.setProperty("subject", subject);
    quadEntity.setProperty("predicate", predicate);
    quadEntity.setProperty("object", object);
    quadEntity.setProperty("graph", graph);

    // Store the quad in Datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(quadEntity);

    // Respond with a success message as a JSON object
    response.getWriter().print("{\"status\": \"Quad inserted successfully\"}");
}


}

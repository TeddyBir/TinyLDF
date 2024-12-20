package foo;

import com.google.appengine.api.datastore.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/datastoreTest")
public class DatastoreTestServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // Créer une instance de DatastoreService
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Créer une entité "Quad"
        Entity quadEntity = new Entity("Quad");

        // Ajouter des propriétés à l'entité
        quadEntity.setProperty("subject", "http://example.org/subject");
        quadEntity.setProperty("predicate", "http://example.org/predicate");
        quadEntity.setProperty("object", "http://example.org/object");
        quadEntity.setProperty("graph", "http://example.org/graph");

        // Enregistrer l'entité dans Datastore
        datastore.put(quadEntity);
        response.getWriter().println("Entity has been inserted into Datastore");

        // Récupérer l'entité de Datastore
        Key quadKey = quadEntity.getKey();
        try {
            Entity retrievedEntity = datastore.get(quadKey);
            response.getWriter().println("Retrieved entity: " + retrievedEntity.getProperty("subject"));
        } catch (EntityNotFoundException e) {
            response.getWriter().println("Entity not found: " + e.getMessage());
        }
    }
}

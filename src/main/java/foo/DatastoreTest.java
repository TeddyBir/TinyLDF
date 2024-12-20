package foo;

import com.google.appengine.api.datastore.*;

public class DatastoreTest {
    public static void main(String[] args) {
        // Configurer l'utilisation de l'émulateur
        System.setProperty("datastore.emulator.host", "localhost:8081");

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
        System.out.println("Entity has been inserted into Datastore");

        // Récupérer l'entité de Datastore
        Key quadKey = quadEntity.getKey();
        try {
            Entity retrievedEntity = datastore.get(quadKey);
            System.out.println("Retrieved entity: " + retrievedEntity.getProperty("subject"));
        } catch (EntityNotFoundException e) {
            System.err.println("Entity not found: " + e.getMessage());
        }
    }
}


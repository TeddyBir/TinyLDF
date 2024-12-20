package foo;

import com.google.appengine.api.datastore.*;

public class DatastoreListEntities {
    public static void main(String[] args) {
        // Créer une instance de Datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Créer une requête pour toutes les entités de type "Quad"
        Query query = new Query("Quad");

        // Exécuter la requête
        PreparedQuery preparedQuery = datastore.prepare(query);

        // Lister les entités récupérées
        for (Entity entity : preparedQuery.asIterable()) {
            // Afficher les informations des entités
            System.out.println("Entity ID: " + entity.getKey().getName());
            System.out.println("Subject: " + entity.getProperty("subject"));
            System.out.println("Predicate: " + entity.getProperty("predicate"));
            System.out.println("Object: " + entity.getProperty("object"));
            System.out.println("Graph: " + entity.getProperty("graph"));
            System.out.println("----------------------------");
        }
    }
}



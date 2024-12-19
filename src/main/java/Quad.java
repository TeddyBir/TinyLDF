package main.java;

public class Quad {
    private String subject;
    private String predicate;
    private String object;
    private String graph;

    public Quad(String subject, String predicate, String object, String graph) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.graph = graph;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    // Méthode pour convertir un Quad en JSON
    public String toJson() {
        return String.format("{\"subject\": \"%s\", \"predicate\": \"%s\", \"object\": \"%s\", \"graph\": \"%s\"}",
                subject, predicate, object, graph);
    }
}

package informatica.unical.spadafora.casanostra.model;

public class Category {

    // ===================== Variabili =====================

    private Integer id;
    private String nome;

    // ===================== Costruttore =====================

    public Category(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // ===================== Getters =====================

    public Integer getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }

    // ===================== Setters =====================

    public void setId(Integer Id) {
        this.id = Id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }


}
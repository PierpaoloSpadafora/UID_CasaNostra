package informatica.unical.spadafora.casanostra.model;

public class Transaction {

    // ===================== Variabili =====================

    private Integer transazione_ID;
    private String nome;
    private double importo;
    private String note;
    private String tipo;
    private String data;
    private String categoria;
    private String utente_ID;

    // ===================== Costruttore =====================

    public Transaction(Integer transazione_ID, String nome, double importo, String note, String tipo, String data, String categoria, String utente_ID) {
        this.transazione_ID = transazione_ID;
        this.nome = nome;
        this.importo = importo;
        this.note = note;
        this.tipo = tipo;
        this.data = data;
        this.categoria = categoria;
        this.utente_ID = utente_ID;
    }

    // ===================== Getters =====================

    public Integer getTransazione_ID() {
        return transazione_ID;
    }
    public String getNome() {
        return nome;
    }
    public double getImporto() {
        return importo;
    }
    public String getNote() {
        return note;
    }
    public String getTipo() {
        return tipo;
    }
    public String getData() {
        return data;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getUtente_ID() {
        return utente_ID;
    }

    // ===================== Setters =====================

    public void setTransazione_ID(Integer transazione_ID) {
        this.transazione_ID = transazione_ID;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setImporto(double importo) {
        this.importo = importo;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setUtente_ID(String utente_ID) {
        this.utente_ID = utente_ID;
    }



    @Override
    public String toString() {
        return "Transaction{" +
                "transazione_ID=" + transazione_ID +
                ", nome='" + nome + '\'' +
                ", importo=" + importo +
                ", note='" + note + '\'' +
                ", tipo='" + tipo + '\'' +
                ", data='" + data + '\'' +
                ", categoria='" + categoria + '\'' +
                ", utente_ID='" + utente_ID + '\'' +
                '}';
    }


}
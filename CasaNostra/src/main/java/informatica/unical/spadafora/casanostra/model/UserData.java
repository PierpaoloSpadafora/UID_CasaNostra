package informatica.unical.spadafora.casanostra.model;

import java.util.List;

public class UserData {

    // ===================== Variabili =====================

    private List<Transaction> transactions;
    private List<Category> categories;
    private Settings settings;

    // ===================== Costruttore =====================

    public UserData(List<Transaction> transactions, List<Category> categories, Settings settings) {
        this.transactions = transactions;
        this.categories = categories;
        this.settings = settings;
    }

    // ===================== Getters =====================

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Settings getSettings() {
        return settings;
    }

    // ===================== Setters =====================

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}

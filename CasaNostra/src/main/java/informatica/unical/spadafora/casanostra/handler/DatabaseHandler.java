package informatica.unical.spadafora.casanostra.handler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.GsonBuilder;
import informatica.unical.spadafora.casanostra.model.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;

public class DatabaseHandler {

    // ===================== Costanti per le query =====================
    private static final String CHECK_USER_Existence = "SELECT EXISTS (SELECT 1 FROM utenti WHERE username=?);";
    private static final String CHECK_PASSWORD = "SELECT password FROM utenti WHERE username=?;";
    private static final String SELECT_TRANSACTIONS = "SELECT * FROM transazione WHERE utente_ID=? AND data >= ? ORDER BY data ASC ;";
    private static final String SELECT_TRANSACTIONS_WITH_LIMIT = "SELECT * FROM transazione WHERE utente_ID=? AND data >= ? ORDER BY data ASC LIMIT ?;";
    private static final String SELECT_TRANSACTIONS_BETWEEN_TWO_DATES = "SELECT * FROM transazione WHERE utente_ID = ? AND data >= ? AND data <= ? ORDER BY data ASC;";
    private static final String INSERT_USER = "INSERT INTO utenti (username, password) VALUES (?, ?);";
    private static final String INSERT_TRANSACTION = "INSERT INTO transazione (nome, importo, note, tipo, data, categoria, utente_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_TRANSACTION = "UPDATE transazione SET nome=?, importo=?, note=?, tipo=?, data=?, categoria=? WHERE transazione_ID=?";
    private static final String DELETE_TRANSACTION = "DELETE FROM transazione WHERE transazione_ID = ?";
    private static final String CHECK_CATEGORY_Existence = "SELECT EXISTS (SELECT 1 FROM categorie WHERE nome=? AND utente_ID=?);";
    private static final String INSERT_CATEGORY = "INSERT INTO categorie (nome, utente_ID) VALUES (?,?)";
    private static final String COUNT_TRANSACTIONS_WITH_CATEGORY = "SELECT COUNT(*) FROM transazione WHERE categoria = ? AND utente_ID = ?";
    private static final String SELECT_CATEGORIES = "SELECT * FROM categorie WHERE utente_ID = ? AND nome LIKE ?";
    private static final String INSERT_SETTINGS = "INSERT INTO settings (theme, font, font_size, utente_ID) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_SETTINGS = "UPDATE settings SET theme=?, font=?, font_size=? WHERE utente_ID=?;";
    private static final String SELECT_SETTINGS = "SELECT * FROM settings WHERE utente_ID=?;";
    private static final String DELETE_USER = "DELETE FROM utenti WHERE username = ?";
    private static final String DELETE_TRANSACTIONS = "DELETE FROM transazione WHERE utente_ID = ?";
    private static final String DELETE_CATEGORIES = "DELETE FROM categorie WHERE utente_ID = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM categorie WHERE id = ?";
    private static final String DELETE_SETTINGS = "DELETE FROM settings WHERE utente_ID = ?";
    public static final String INSERT_SECURITY_QUESTION = "INSERT INTO domandaDiSicurezza (utente_ID, domanda1, domanda2, domanda3, risposta1, risposta2, risposta3) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String DELETE_SECURITY_QUESTION = "DELETE FROM domandaDiSicurezza WHERE utente_ID = ?";
    public static final String CHECK_SECURITY_QUESTION_Existence = "SELECT EXISTS (SELECT 1 FROM domandaDiSicurezza WHERE utente_ID = ? )";
    public static final String SELECT_SECURITY_QUESTION = "SELECT * FROM domandaDiSicurezza WHERE utente_ID = ?";
    public static final String UPDATE_SECURITY_QUESTION = "UPDATE domandaDiSicurezza SET domanda1=?, domanda2=?, domanda3=?, risposta1=?, risposta2=?, risposta3=? WHERE utente_ID=?";
    private static final String CHECK_SECURITY_ANSWERS = "SELECT risposta1, risposta2, risposta3 FROM domandaDiSicurezza WHERE utente_ID = ?";
    private static final String CHECK_SECURITY_QUESTIONS_EXIST = "SELECT COUNT(*) FROM domandaDiSicurezza WHERE utente_ID = ?";
    private static final String UPDATE_PASSWORD = "UPDATE utenti SET password = ? WHERE username = ?";


    // ===================== Variabili di istanza =====================

    private static Connection con;
    private User currentUser;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    // ===================== Costruttore di una classe Singleton =====================

    private static final DatabaseHandler instance = new DatabaseHandler();
    private DatabaseHandler() {}
    public static DatabaseHandler getInstance() {
        return instance;
    }


    // ===================== Gestione Utente =====================

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    public User getCurrentUser() {
        return currentUser;
    }

    // ===================== Utilità =====================

    private void logError(String message) {
        System.out.println(message);
    }

    // ===================== Gestione Database =====================

    public void openConnection() {
        try {
            String url = "jdbc:sqlite:CasaNostra.db";
            con = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            logError("Error while connecting to the database: " + e.getMessage());
        }
    }

    public void closeConnection() {

        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            logError("Error while closing the connection: " + e.getMessage());
        }
    }

    public void logout(){
        currentUser = null;
    }


    // ===================== Ottenere un oggetto di tipo ... da una query  =====================

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("transazione_ID"),
                rs.getString("nome"),
                rs.getDouble("importo"),
                rs.getString("note"),
                rs.getString("tipo"),
                rs.getString("data"),
                rs.getString("categoria"),
                rs.getString("utente_ID")
        );
    }
    private Category createCategoriaFromResultSet(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("nome")
        );
    }
    private Settings createSettingsFromResultSet(ResultSet rs) throws SQLException {
        return new Settings(
                rs.getString("theme"),
                rs.getString("font"),
                rs.getString("font_size")
        );
    }


    // ===================== Metodi "core" =====================

    public boolean utentePresenteInDBMethod(String username){
        try (PreparedStatement stmt = con.prepareStatement(CHECK_USER_Existence)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                // Se c'è un risultato e il valore è 1, l'utente esiste
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logError("Error while checking if the user exist in the database: " + e.getMessage());
            return false;
        }
    }
    public boolean checkPasswordMethod(String username, String plainPassword){
        try (PreparedStatement statement = con.prepareStatement(CHECK_PASSWORD)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return BCrypt.checkpw(plainPassword, hashedPassword);
                }
            }
        } catch (SQLException e) {
            logError("Error while verifying the password: " + e.getMessage());
        }
        return false;

    }
    public boolean inserisciUserMethod(String username, String plainPassword){

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        try (PreparedStatement stmt = con.prepareStatement(INSERT_USER)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            boolean result = stmt.executeUpdate() > 0;
            currentUser = new User(username);
            return result;
        } catch (SQLException e) {
            logError("Error while adding a new user: " + e.getMessage());
            return false;
        }
    }
    public boolean modificaPasswordMethod(String plainPassword){
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_PASSWORD)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, currentUser.username());
            boolean result = stmt.executeUpdate() > 0;
            currentUser = new User(currentUser.username());
            return result;
        } catch (SQLException e) {
            logError("Error while adding a new user: " + e.getMessage());
            return false;
        }
    }
    public boolean inserisciTransazioneMethod(Transaction t){
        try (PreparedStatement stmt = con.prepareStatement(INSERT_TRANSACTION)) {
            stmt.setString(1, t.getNome());
            stmt.setDouble(2, t.getImporto());
            stmt.setString(3, t.getNote());
            stmt.setString(4, t.getTipo());
            stmt.setString(5, t.getData());
            stmt.setString(6, t.getCategoria());
            stmt.setString(7, currentUser.username());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            logError("Error while adding a new transaction: " + e.getMessage());
            return false;
        }
    }
    public boolean modificaTransazioneMethod(Transaction t){
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_TRANSACTION)) {
            stmt.setString(1, t.getNome());
            stmt.setDouble(2, t.getImporto());
            stmt.setString(3, t.getNote());
            stmt.setString(4, t.getTipo());
            stmt.setString(5, t.getData());
            stmt.setString(6, t.getCategoria());
            stmt.setInt(7, t.getTransazione_ID());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            logError("Errore durante la modifica della transazione: " + e.getMessage());
            return false;
        }
    }
    public boolean rimuoviTransazioneMethod(int id){
        try (PreparedStatement stmt = con.prepareStatement(DELETE_TRANSACTION)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Errore durante la rimozione della transazione: " + e.getMessage());
            return false;
        }
    }
    public List<Transaction> trovaTransazioniTraDueDateMethod( String date1, String date2) {

        List<Transaction> transazioni = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(SELECT_TRANSACTIONS_BETWEEN_TWO_DATES)) {
            stmt.setString(1, currentUser.username());
            stmt.setString(2, date1);
            stmt.setString(3, date2);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transazioni.add(createTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logError("Errore durante la ricerca delle transazioni: " + e.getMessage());
        }
        return transazioni;
    }
    public List<Transaction> trovaTransazioniAvanzatoMethod(String transaction, String type, String category, String date) {
        List<Transaction> transazioni = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM transazione WHERE ");

        boolean asc_ord_data = false;

        List<String> conditions = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        conditions.add("utente_ID = ?");
        values.add(currentUser.username());

        if (category != null && !category.isEmpty() && !category.equals("All Categories")) {
            conditions.add("categoria = ?");
            values.add(category);
        }
        if (type != null && !type.isEmpty() && !type.equals("Both")){
            conditions.add("tipo = ?");
            values.add(type);
        }
        if (transaction != null && !transaction.isEmpty()) {
            conditions.add("nome LIKE ?");
            values.add("%" + transaction + "%");
        }
        if (date != null && !date.isEmpty()) {
            conditions.add("data >= ?");
            asc_ord_data = true;
            values.add(date); // Potrebbe essere necessario un parsing in un tipo data
        }

        query.append(String.join(" AND ", conditions));
        if(asc_ord_data){
            query.append(" ORDER BY data ASC");
        }
        else{
            query.append(" ORDER BY data DESC");
        }
        query.append(";");

        try (PreparedStatement stmt = con.prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1 , values.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transazioni.add(createTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logError("Errore durante la ricerca delle transazioni: " + e.getMessage());
        }

        return transazioni;
    }
    public boolean categoriaPresenteInDBMethod(String nomeCategoria){
        try (PreparedStatement stmt = con.prepareStatement(CHECK_CATEGORY_Existence)) {
            stmt.setString(1, nomeCategoria);
            stmt.setString(2, currentUser.username());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logError("Error while checking if the category exist in the database: " + e.getMessage());
            return false;
        }
    }
    public boolean aggiungiCategoriaMethod(String nomeCategoria) {

        if(categoriaPresenteInDB(nomeCategoria)){
            return false;
        }

        try (PreparedStatement stmt = con.prepareStatement(INSERT_CATEGORY)) {
            stmt.setString(1, nomeCategoria);
            stmt.setString(2, currentUser.username());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            logError("Errore durante l'aggiunta della categoria: " + e.getMessage());
            return false;
        }
    }
    public boolean assenzaDiTransazioniConCategoriaMethod(String nomeCategoria){
        try (PreparedStatement stmt = con.prepareStatement(COUNT_TRANSACTIONS_WITH_CATEGORY)) {
            stmt.setString(1, nomeCategoria);
            stmt.setString(2, currentUser.username());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }

        } catch (SQLException e) {
            logError("Error while checking if there are transactions with the category: " + e.getMessage());
        }
        return false;
    }
    public boolean rimuoviCategoriaMethod(int id, String nomeCategoria){

        if(!assenzaDiTransazioniConCategoria(nomeCategoria)){
            return false;
        }

        try (PreparedStatement stmt = con.prepareStatement(DELETE_CATEGORY)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Errore durante la rimozione della categoria: " + e.getMessage());
            return false;
        }
    }
    public List<Category> trovaCategorieMethod(String nomeCategoria) {
        List<Category> categorie = new ArrayList<>();

        if(nomeCategoria == null || nomeCategoria.isEmpty()){
            nomeCategoria = "%";
        }
        else{
            nomeCategoria = "%" + nomeCategoria + "%";
        }

        try (PreparedStatement stmt = con.prepareStatement(SELECT_CATEGORIES)) {
            stmt.setString(1, currentUser.username());
            stmt.setString(2, nomeCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorie.add(createCategoriaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logError("Errore durante la ricerca delle categorie: " + e.getMessage());
        }
        return categorie;
    }
    public boolean aggiungiSettingsMethod(Settings settings) {
        try (PreparedStatement stmt = con.prepareStatement(INSERT_SETTINGS)) {
            stmt.setString(1, settings.getTheme());
            stmt.setString(2, settings.getFont());
            stmt.setString(3, settings.getFontSize());
            stmt.setString(4, currentUser.username());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            logError("Errore durante l'aggiunta delle impostazioni: " + e.getMessage());
            return false;
        }
    }
    public boolean modificaSettingsMethod(Settings settings) {
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_SETTINGS)) {
            stmt.setString(1, settings.getTheme());
            stmt.setString(2, settings.getFont());
            stmt.setString(3, settings.getFontSize());
            stmt.setString(4, currentUser.username());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            logError("Errore durante la modifica delle impostazioni: " + e.getMessage());
            return false;
        }
    }
    public Settings trovaSettingsMethod() {
        Settings settings = null;
        try (PreparedStatement stmt = con.prepareStatement(SELECT_SETTINGS)) {
            stmt.setString(1, currentUser.username());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    settings = createSettingsFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logError("Errore durante la ricerca delle impostazioni: " + e.getMessage());
        }
        return settings;
    }
    public boolean salvaTransazioniMethod(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            inserisciTransazioneMethod(t); // oppure inserisciTransazione se vuoi la versione asincrona
        }
        return true;
    }
    public boolean salvaCategorieMethod(List<Category> categories) {
        for (Category c : categories) {
            aggiungiCategoriaMethod(c.getNome()); // oppure aggiungiCategoria se vuoi la versione asincrona
        }
        return true;
    }
    public boolean salvaSettingsMethod(Settings settings) {
        Settings currentSettings = trovaSettingsMethod(); // oppure trovaSettings se vuoi la versione asincrona
        if (currentSettings == null) {
            aggiungiSettingsMethod(settings); // oppure aggiungiSettings se vuoi la versione asincrona
        } else {
            modificaSettingsMethod(settings); // oppure modificaSettings se vuoi la versione asincrona
        }
        return true;
    }
    public boolean esportaUtenteInJsonMethod(String path) {
        String username = currentUser.username();
        String fileName = username + "_data.json";

        File selectedFile = new File(path + fileName);

        List<Transaction> transazioni = trovaTransazioniAvanzatoMethod(null, null, null, null );
        List<Category> categorie = trovaCategorie("");
        Settings settings = trovaSettings();

        // Crea un oggetto che contiene tutte queste informazioni
        UserData exportData = new UserData(transazioni, categorie, settings);

        // Converte l'oggetto in una stringa JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(exportData);

        // Salva la stringa JSON nel file
        try (FileWriter file = new FileWriter(selectedFile)) {
            file.write(json);
            return true;
        } catch (IOException e) {
            logError("Error while writing JSON file: " + e.getMessage());
            return false;
        }
    }
    public boolean importaUtenteDaJsonMethod(String path) {
        File selectedFile = new File(path);

        if (!selectedFile.exists()) {
            logError("The selected file does not exist: " + path);
            return false;
        }

        try (FileReader fileReader = new FileReader(selectedFile)) {
            Gson gson = new GsonBuilder().create();

            // Converte il file JSON in un oggetto UserData
            UserData importData = gson.fromJson(fileReader, UserData.class);

            // Salva le informazioni nel database
            salvaTransazioni(importData.getTransactions());
            salvaCategorie(importData.getCategories());
            salvaSettings(importData.getSettings());

            return true;

        } catch (IOException e) {
            logError("Error while reading JSON file: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminaTransazioniMethod() {
        try (PreparedStatement stmt = con.prepareStatement(DELETE_TRANSACTIONS)) {
            stmt.setString(1, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Error when changing settings: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminaCategorieMethod() {
        try (PreparedStatement stmt = con.prepareStatement(DELETE_CATEGORIES)) {
            stmt.setString(1, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante l'eliminazione delle categorie: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminaSettingsMethod(){
        try (PreparedStatement stmt = con.prepareStatement(DELETE_SETTINGS)) {
            stmt.setString(1, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante l'eliminazione delle impostazioni: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminaUtenteMethod() {
        try (PreparedStatement stmt = con.prepareStatement(DELETE_USER)) {
            stmt.setString(1, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante l'eliminazione dell'utente: " + e.getMessage());
            return false;
        }
    }
    public void eliminaAccount() {
        if(eliminaTransazioni()){
            if(eliminaCategorie() && eliminaSettings() && eliminaDomandeDiSicurezza()){
                eliminaUtente();
            }
        }
    }
    public boolean inserisciDomandeDiSicurezzaMethod(String domanda1, String domanda2, String domanda3, String risposta1, String risposta2, String risposta3) {
        try (PreparedStatement stmt = con.prepareStatement(INSERT_SECURITY_QUESTION)) {
            stmt.setString(1, currentUser.username());
            stmt.setString(2, domanda1);
            stmt.setString(3, domanda2);
            stmt.setString(4, domanda3);
            stmt.setString(5, BCrypt.hashpw(risposta1, BCrypt.gensalt(12)));
            stmt.setString(6, BCrypt.hashpw(risposta2, BCrypt.gensalt(12)));
            stmt.setString(7, BCrypt.hashpw(risposta3, BCrypt.gensalt(12)));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante l'inserimento delle domande di sicurezza: " + e.getMessage());
            return false;
        }
    }
    public boolean modificaDomandeDiSicurezzaMethod(String domanda1, String domanda2, String domanda3, String risposta1, String risposta2, String risposta3){
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_SECURITY_QUESTION)) {
            stmt.setString(1, domanda1);
            stmt.setString(2, domanda2);
            stmt.setString(3, domanda3);
            stmt.setString(4, BCrypt.hashpw(risposta1, BCrypt.gensalt(12)));
            stmt.setString(5, BCrypt.hashpw(risposta2, BCrypt.gensalt(12)));
            stmt.setString(6, BCrypt.hashpw(risposta3, BCrypt.gensalt(12)));
            stmt.setString(7, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante la modifica delle domande di sicurezza: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminaDomandeDiSicurezzaMethod(){
        try (PreparedStatement stmt = con.prepareStatement(DELETE_SECURITY_QUESTION)) {
            stmt.setString(1, currentUser.username());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logError("Errore durante l'eliminazione delle domande di sicurezza: " + e.getMessage());
            return false;
        }
    }
    public boolean verificaSeDomandeDiSicurezzaPresentiMethod(){
        try (PreparedStatement stmt = con.prepareStatement(CHECK_SECURITY_QUESTION_Existence)) {
            stmt.setString(1, currentUser.username());
            try (ResultSet rs = stmt.executeQuery()) {
                // Se c'è un risultato e il valore è 1, l'utente esiste
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logError("Error while checking if the user exist in the database: " + e.getMessage());
            return false;
        }
    }
    public List<String> trovaDomandeDiSicurezzaMethod(){
        List<String> domande = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(SELECT_SECURITY_QUESTION)) {
            stmt.setString(1, currentUser.username());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    domande.add(rs.getString("domanda1"));
                    domande.add(rs.getString("domanda2"));
                    domande.add(rs.getString("domanda3"));
                }
            }
        } catch (SQLException e) {
            logError("Errore durante la ricerca delle domande di sicurezza: " + e.getMessage());
        }
        return domande;
    }
    public boolean checkSecurityAnswersMethod(String plainAnswer1, String plainAnswer2, String plainAnswer3) {
        try (PreparedStatement statement = con.prepareStatement(CHECK_SECURITY_ANSWERS)) {
            statement.setString(1, currentUser.username());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String hashedAnswer1 = rs.getString("risposta1");
                    String hashedAnswer2 = rs.getString("risposta2");
                    String hashedAnswer3 = rs.getString("risposta3");

                    return BCrypt.checkpw(plainAnswer1, hashedAnswer1) &&
                            BCrypt.checkpw(plainAnswer2, hashedAnswer2) &&
                            BCrypt.checkpw(plainAnswer3, hashedAnswer3);
                }
            }
        } catch (SQLException e) {
            logError("Error while verifying the security answers: " + e.getMessage());
        }
        return false;
    }
    public boolean areSecurityQuestionsSetMethod(String username) {
        try (PreparedStatement statement = con.prepareStatement(CHECK_SECURITY_QUESTIONS_EXIST)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            logError("Error while checking if security questions are set: " + e.getMessage());
        }
        return false;
    }

    // ===================== Esecuzione Asincrona =====================

    public <T> T executeAsync(Callable<T> task, String errorMessage) {
        Future<T> futureResult = executorService.submit(task);
        try {
            return futureResult.get();
        } catch (Exception e) {
            logError(errorMessage + e.getMessage());
            return null;
        }
    }

    // ===================== Metodi Asincroni Wrapper =====================

    public boolean utentePresenteInDB(String username) {
        return executeAsync(() -> utentePresenteInDBMethod(username), "Error while executing utentePresenteInDB");
    }
    public boolean checkPassword(String username, String plainPassword) {
        return executeAsync(() -> checkPasswordMethod(username, plainPassword), "Error while executing checkPassword");
    }
    public boolean inserisciUser(String username, String plainPassword) {
        return executeAsync(() -> inserisciUserMethod(username, plainPassword), "Error while executing inserisciUser");
    }
    public boolean modificaPassword(String plainPassword) {
        return executeAsync(() -> modificaPasswordMethod(plainPassword), "Error while executing modificaPassword");
    }
    public boolean inserisciTransazione(Transaction t) {
        return executeAsync(() -> inserisciTransazioneMethod(t), "Error while executing inserisciTransazione");
    }
    public boolean modificaTransazione(Transaction t) {
        return executeAsync(() -> modificaTransazioneMethod(t), "Error while executing modificaTransazione");
    }
    public boolean rimuoviTransazione(int id) {
        return executeAsync(() -> rimuoviTransazioneMethod(id), "Error while executing rimuoviTransazione");
    }
    public List<Transaction> trovaTransazioniTraDueDate(String date1, String date2) {
        return executeAsync(() -> trovaTransazioniTraDueDateMethod(date1, date2), "Error while executing trovaTransazioni");
    }
    public List<Transaction> trovaTransazioniAvanzato(String category, String type, String date, String transaction) {
        return executeAsync(() -> trovaTransazioniAvanzatoMethod(category, type, date, transaction), "Error while executing trovaTransazioniAvanzato");
    }
    public boolean categoriaPresenteInDB(String nomeCategoria) {
        return executeAsync(() -> categoriaPresenteInDBMethod(nomeCategoria), "Error while executing categoriaPresenteInDB");
    }
    public boolean aggiungiCategoria(String nomeCategoria) {
        return executeAsync(() -> aggiungiCategoriaMethod(nomeCategoria), "Error while executing aggiungiCategoria");
    }
    public boolean assenzaDiTransazioniConCategoria(String nomeCategoria) {
        return executeAsync(() -> assenzaDiTransazioniConCategoriaMethod(nomeCategoria), "Error while executing assenzaDiTransazioniConCategoria");
    }
    public boolean rimuoviCategoria(int id, String nomeCategoria) {
        return executeAsync(() -> rimuoviCategoriaMethod(id, nomeCategoria), "Error while executing rimuoviCategoria");
    }
    public List<Category> trovaCategorie(String nomeCategoria) {
        return executeAsync(() -> trovaCategorieMethod(nomeCategoria), "Error while executing trovaCategoria");
    }
    public boolean aggiungiSettings(Settings settings) {
        return executeAsync(() -> aggiungiSettingsMethod(settings), "Error while executing aggiungiSettings");
    }
    public boolean modificaSettings(Settings settings) {
        return executeAsync(() -> modificaSettingsMethod(settings), "Error while executing modificaSettings");
    }
    public Settings trovaSettings() {
        return executeAsync(this::trovaSettingsMethod, "Error while executing trovaSettings");
    }
    public void salvaTransazioni(List<Transaction> transactions) {
        executeAsync(() -> salvaTransazioniMethod(transactions), "Error while executing salvaTransazioni");
    }
    public void salvaCategorie(List<Category> categories) {
        executeAsync(() -> salvaCategorieMethod(categories), "Error while executing salvaCategorie");
    }
    public void salvaSettings(Settings settings) {
        executeAsync(() -> salvaSettingsMethod(settings), "Error while executing salvaSettings");
    }
    public boolean esportaUtenteInJson(String path) {
        return executeAsync(() -> esportaUtenteInJsonMethod(path), "Error while executing esportaUtenteInJson");
    }
    public boolean importaUtenteDaJson(String path) {
        return executeAsync(() -> importaUtenteDaJsonMethod(path), "Error while running importaUtenteDaJson");
    }
    public boolean eliminaTransazioni() {
        return executeAsync(this::eliminaTransazioniMethod, "Error while executing eliminaTransazioni");
    }
    public boolean eliminaCategorie() {
        return executeAsync(this::eliminaCategorieMethod, "Error while executing eliminaCategorie");
    }
    public boolean eliminaSettings() {
        return executeAsync(this::eliminaSettingsMethod, "Error while executing eliminaSettings");
    }
    public void eliminaUtente() {
        executeAsync(this::eliminaUtenteMethod, "Error while executing eliminaUtente");
    }
    public boolean inserisciDomandeDiSicurezza(String domanda1, String domanda2, String domanda3, String risposta1, String risposta2, String risposta3) {
        return executeAsync(() -> inserisciDomandeDiSicurezzaMethod(domanda1, domanda2, domanda3, risposta1, risposta2, risposta3), "Error while executing inserisciDomandeDiSicurezza");
    }
    public boolean modificaDomandeDiSicurezza(String domanda1, String domanda2, String domanda3, String risposta1, String risposta2, String risposta3) {
        return executeAsync(() -> modificaDomandeDiSicurezzaMethod(domanda1, domanda2, domanda3, risposta1, risposta2, risposta3), "Error while executing modificaDomandeDiSicurezza");
    }
    public boolean eliminaDomandeDiSicurezza() {
        return executeAsync(this::eliminaDomandeDiSicurezzaMethod, "Error while executing eliminaDomandeDiSicurezza");
    }
    public boolean verificaSeDomandeDiSicurezzaPresenti() {
        return executeAsync(this::verificaSeDomandeDiSicurezzaPresentiMethod, "Error while executing verificaSeDomandeDiSicurezzaPresenti");
    }
    public List<String> trovaDomandeDiSicurezza() {
        return executeAsync(this::trovaDomandeDiSicurezzaMethod, "Error while executing trovaDomandeDiSicurezza");
    }
    public boolean checkSecurityAnswers(String plainAnswer1, String plainAnswer2, String plainAnswer3) {
        return executeAsync(() -> checkSecurityAnswersMethod(plainAnswer1, plainAnswer2, plainAnswer3), "Error while executing checkSecurityAnswers");
    }
    public boolean areSecurityQuestionsSet(String username) {
        return executeAsync(() -> areSecurityQuestionsSetMethod(username), "Error while executing areSecurityQuestionsSet");
    }





}
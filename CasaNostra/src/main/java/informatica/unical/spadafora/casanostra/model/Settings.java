package informatica.unical.spadafora.casanostra.model;

public class Settings {

    // ===================== Variabili =====================

    private String theme;
    private String font;
    private String font_size;

    // ===================== Costruttore =====================

    public Settings(String theme, String font, String font_size) {
        this.theme = theme;
        this.font = font;
        this.font_size = font_size;
    }

    // ===================== Getters =====================

    public String getTheme() {
        return theme;
    }
    public String getFont() {
        return font;
    }
    public String getFontSize() {
        return font_size;
    }

    // ===================== Setters =====================

    public void setTheme(String theme) {
        this.theme = theme;
    }
    public void setFont(String font) {
        this.font = font;
    }
    public void setFontSize(String font_size) {
        this.font_size = font_size;
    }

}
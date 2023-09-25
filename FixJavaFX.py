
# Questo script può essere lanciato da qualunque percorso (all'interno del pc in cui è presente il progetto)
# corregge tutte le "versioni errate" di JavaFX nei file FXML facendo scomparire i messaggi di errore

# RICORDATI DI CAMBIARE IL PATH ED EVENTUALMENTE ANCHE LE VERSIONI DI JavaFX

import os

def replace_string_in_files(directory, old_string, new_string):
    for subdir, _, files in os.walk(directory):
        for filename in files:
            if not filename.endswith('.fxml'):
                continue

            filepath = os.path.join(subdir, filename)

            with open(filepath, 'r') as file:
                filedata = file.read()

            filedata = filedata.replace(old_string, new_string)

            with open(filepath, 'w') as file:
                file.write(filedata)

#NECESSARI 4 SLASH O IL PATH NON VERRA' INTERPRETATO CORRETTAMENTE

directory = 'C:\\\\Users\\\\user\\\\Desktop\\\\nomeProgetto\\\\src\\\\main\\\\resources\\\\percorso\\\\per\\\\la\\\\view'

# Versione installata in cui vengono salvati i file da sceneBuilder
currentVersion = 'xmlns="http://javafx.com/javafx/19"'

# Versione corretta da Intellij per far scomparire i messaggi di errore
correctVersion = 'xmlns="http://javafx.com/javafx/17.0.6"'

replace_string_in_files(directory, currentVersion, correctVersion)

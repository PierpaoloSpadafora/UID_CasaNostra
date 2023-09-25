# This script can be run from any location (within the PC where the project is located)
# It fixes all "incorrect versions error" of JavaFX in FXML files, making all the related warnings disappear

# REMEMBER TO CHANGE THE PATH AND POSSIBLY THE JAVAFX VERSIONS AS WELL

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

# YOU NEED 4 SLASHES OR THE PATH WON'T BE INTERPRETED CORRECTLY

directory = 'C:\\\\Users\\\\user\\\\Desktop\\\\projectName\\\\src\\\\main\\\\resources\\\\path\\\\to\\\\view'

# Version installed where files are saved by SceneBuilder
currentVersion = 'xmlns="http://javafx.com/javafx/19"'

# Correct version according to IntelliJ to make error messages disappear
correctVersion = 'xmlns="http://javafx.com/javafx/17.0.6"'

replace_string_in_files(directory, currentVersion, correctVersion)

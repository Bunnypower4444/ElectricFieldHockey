# Removes unnecessary files from the a specified version's jar file, such as
# the Assets folder (which will be included in the zip), the documentation, other builds,
# and bash files (such as this one).

# UPDATE: i set the source class path for the Java project as the Source folder,
# so other files won't be included anymore (but this still is useful for renaming
# and replacing files i guess...)

# Used for generating release builds

read -p "Enter version: " version

if [[ -f "build/ElectricFieldHockey.jar" ]]; then
    echo "Renaming ElectricFieldHockey.jar to ElectricFieldHockey-v${version}.jar ..."
    mv -i build/ElectricFieldHockey.jar build/ElectricFieldHockey-v${version}.jar
fi

# echo "Cleaning jar for v${version}..."
# zip -d build/ElectricFieldHockey-v${version}.jar Assets/\* docs/\* build/\* *.command
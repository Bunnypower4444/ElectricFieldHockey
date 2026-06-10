# Removes unnecessary files from the a specified version's jar file, such as
# the Assets folder (which will be included in the zip), the documentation, other builds,
# and bash files (such as this one). This also renames any jar file without a version
# (aka one just generated through VS Code's Java Projects menu) to include the version.

# Used for generating release builds

read -p "Enter version: " version

if [[ -f "build/ElectricFieldHockey.jar" ]]; then
    echo "Renaming ElectricFieldHockey.jar to ElectricFieldHockey-v${version}.jar ..."
    mv -i build/ElectricFieldHockey.jar build/ElectricFieldHockey-v${version}.jar
fi

echo "Cleaning jar for v${version}..."
zip -d build/ElectricFieldHockey-v${version}.jar Assets/\* docs/\* build/\* *.command
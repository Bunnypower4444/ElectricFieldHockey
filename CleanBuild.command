# Removes unnecessary files from the a specified version's jar file, such as
# the Assets folder (which will be included in the zip), the documentation, other builds,
# and bash files (such as this one).
# Used for generating release builds

read -p "Enter version: " version
echo "Cleaning jar for v${version}..."
zip -d build/ElectricFieldHockey-v${version}.jar Assets/\* docs/\* build/\* *.command
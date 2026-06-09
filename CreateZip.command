# Creates a zip for the specified version, containing the assets folder and the
# version's jar file.
# Used for generating release builds

read -p "Enter version: " version
echo "Creating zip for v${version}..."
rm "build/ElectricFieldHockey-v${version}.zip"
zip -r "build/ElectricFieldHockey-v${version}.zip" Assets
zip -j "build/ElectricFieldHockey-v${version}.zip" "build/ElectricFieldHockey-v${version}.jar"
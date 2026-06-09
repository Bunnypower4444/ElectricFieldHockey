# creates an index.list file for each folder in the Assets folder

for sub_dir in Assets/*/; do
    echo $sub_dir
    cd $sub_dir
    ls -1 > index.list
    cd ../..
done
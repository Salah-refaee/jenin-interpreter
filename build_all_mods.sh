# a shortcut that builds every module in a project structured as:
# / project
# |-/ nativemods
# | |-/ SomeModule.java
# | |-/ AnotherModule.java
# |- ...jenin files + other stuff

# usage: ./build_all_mods.sh <path to project.jar> <path to project folder>

sources=$(find $2/nativemods -name "*.java")
for source in $sources; do
  echo "Building $source"
  javac -cp $1 $source
  if [ $? -ne 0 ]; then
    printf "\033[33mBuild failed for %s\033[0m\n" $source
  else
    printf "\033[32mBuild successful for %s\033[0m\n" $source
  fi
  # move all sucsessfully built .class files to the nativemods folder
done
mv $2/nativemods/*.class $2/nativemods/
tree $2/nativemods
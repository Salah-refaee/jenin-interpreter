# build modules for jenin

mkdir modules 2>/dev/null ## ignore if already exists
javac -cp "$1" -d modules "$2"
# $1; classpath for the project .jar
# #2: the target .java file
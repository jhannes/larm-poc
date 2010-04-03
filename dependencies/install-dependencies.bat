cmd.exe /c "mvn install:install-file -DgroupId=com.caucho -DartifactId=hessian -Dversion=4.0.3 -Dpackaging=jar -Dfile=%~dp0\hessian-4.0.3.jar"
cmd.exe /c "mvn install:install-file -DgroupId=com.caucho -DartifactId=hessian -Dversion=4.0.3 -Dpackaging=jar -Dclassifier=sources -Dfile=%~dp0\hessian-4.0.3-src.jar"


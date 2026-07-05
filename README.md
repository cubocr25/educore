*Requisitos
-JAVA JDK 21
-Apache Maven 3.9 o superior
-NetBeans 25 o superior (yo lo hice en IDE 30 porque solo ese me sirvio al hacer el fork)

*Compilar
bash
mvn clean compile

*pruebas
bash
mvn test

*Formato
bash
mvn fmt:check

*ejecutar proyecto
desde NetBeans
    Abrir proyecto
    Run Project
desde bash
mvn exec:java -Dexec.mainClass="edu.uam.educore.Main"

*Sistema
Gestion estudiantes
    registra estudiantes
    lista estudiantes
    busca por id
    actualiza info
    elimina estudiantes
Gestion Empleados
    registra empleado
    lista empleados
    busca por id
    actualiza info
    elimina empleados
Gestion Académica
    Edificios
    Aulas
    Secciones
    Asigna estudiantes a secciones

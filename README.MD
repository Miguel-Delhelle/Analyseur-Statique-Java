Analyseur de projet Java.
Projet de TP universitaire, étudiant
https://gitlab.com/miguel.delhelle-group/Analyseur-Projet-Java

Auteur Delhelle Miguel - Master 2 Informatique ico, université de montpellier.

Ceci est le résultat du premeir TP d'ingenierie logicielle.
C'est un projet SpringBoot - Maven l'application n'as pas de cli, ni d'interface "bureau".
Mais une application web qui s'ouvre dans le navigateur. 
Celle-ci a été réalisé rapidement, par intelligence artificelle générative.
L'idée était de ne consacrer que peu de ressource intellectuelles a sa réalisation pour ce premier TP. 
Pour la suite elle sera mis à jour pour devenir plus "authentique". 

L'idée de la petite appli web qui tourne, est conforme à ce qui se fait technologiquement à l'heure actuelle.
Beaucoup de solutions logicielles préfère éxécuter l'interface directement dans le navigateur, afin de profiter de la portabilité de celui-ci. 
Et également des librairies de développement frontend très avancée dans les écosystème javascript et php.


Joint à ce readme un .jar pour lancer l'application rapidement.

Le lancement se fait en double cliquant dessus ou via la commande "java -jar AstJava.jar" .
Cette commande fonctionne sur tout les OS tant que java est installé et est dans le Path.

L'application web s'ouvre sur le port 8080 par défaut. Vous êtes libre de modifier l'application properties pour le modifier vous mêmes. 

Quelques test unitaires ont été réalisé, un répertoire "webApp" montre tout le projet frontend.
Il a été fait rapidement et simplement avec typescript d'installé ainsi que graphviz pour pouvoir visualiser le .dot du graphe d'appel.
Le graphe d'appel n'est pas un graphe d'appel traditionnelle, mais augmenter. Il représente les classes. Et se concentre uniquement sur le projet analysé directement pas aux appels extérieurs.

Ce qui peut être une mauvaise chose, sur lequel nous reviendrons avec l'itération du TP2. 

Le programme prend en entrée un .zip ou un lien vers un dépot git. 

-----------------------------------------------------------------------------------
Potentielle vulnérabilités dans le programme.

Pour les analyser on clone des répertoires git, ou décompresse des .zip .
Les zip sont accepté jusqu'à 500MO. 
Celà se clone dans le /tmp du système (sur les linux).

Et les répertoires détruit après, cependant, une faille de sécurité à l'air d'exister à travers ce fonctionnement. 






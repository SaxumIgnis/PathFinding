Système de pathfinding
======================

3D complète mais lourde :

- prise en compte de la pente dans la vitesse de déplacement
- coefficient de vitesse locale
- arêtes pondérées (temps pour les traverser)
- déplacement le long des arrêtes (les WayPoints sont les sommets des triangles physiques)

Nécessité d'indiquer les numéros (*tag*) des triangles physiques pour les points d'arrivée et de départ

Pas encore de carte dynamique (porte à casser, echelles sur les murs etc), à voir dans une prochaine version

L'initialisation prend en entrée :
- Un tableau de polygones (geometry.Point[][]) qui doivent être orientés correctement (sens anti-horaire) et planaires
- un tableau de doubles (double[]) indiquant le coefficient de vitesse pour chaque polygone (1 pour normal)

Les trous de la carte bloquent les déplacements (pas de sortie de carte possible)

Le calcul d'un chemin (shortestWay) prend en entrée deux points localisés chacun dans un polygone (geometry.LocatedPoint, obtenable en appelant la méthode locate(int polygonTag) sur un Point) et renvoie un tableau de points (geometry.Point[]) correspondant à l'itinéraire

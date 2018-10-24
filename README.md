Système de pathfinding
======================

### Version "2D augmentée"

- carte homéomorphe à un plan (pas de ponts ou de tunnels)
- prise en compte de la pente dans la vitesse de déplacement
- "facteur de lag" RESEARCH_AREA de la classe ProcessedMap (plus il est grand, meilleur est le chemin trouvé mais plus ça prend de temps => voir s'il pourrait dépendre du nombre de points sur la carte)


Pas encore de carte dynamique (porte à casser, echelles sur les murs etc), à voir dans une prochaine version

L'initialisation prend en entrée (ProcessedMap):
- un tableau de points (geometry.Point[]) pour définir l'altitude à différents endroits de la carte (la carte physique est ensuite reconstituée avec une triangularisation de Delaunay)
- un tableau d'arêtes (geometry.Point[][]) qui indiquent les limites de obstacles physiques (ces arêtes seront forcées dans la triangularisation)

L'initialisation prend du temps (triangularisation + Dijkstra sur chaque point => n^3 au mieux) mais le calcul d'un chemin min en temps réel se fait en temps < log(n)

Le calcul d'un chemin (shortestWay) prend en entrée deux points (geometry.Point) et renvoie un tableau de points (geometry.Point[]) correspondant à l'itinéraire

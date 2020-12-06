# Algorithms - MST (Kruskal+union-find), StableMarriage, Coin Change
 Implement the Algorithms MST (Kruskal+union-find), StableMarriage, Coin Change.
 
 University Project.
 
There are two kinds of ants in the nest, red and black.
1. The place where each ant is is determined by (x,y) coordinates.
2. The black ants gather their food (seeds) at certain places from where the red ants
carry those seeds to their nest.
3. The red ants are equal to the black ants at numbers.
4. The red ants carry a bucket ech with different capacity of seeds.
5. The black ants carry seeds of lots of diiferent kinds.
6. Each kind of seed has a different weight.
7. Each black ant chooses which seeds it wants but only from 5 kinds of seeds.

At some point we have the following data: 

Caterogy Coordinations Other Info
0 (red) 0.123 0.8765 1567 (capacity of bucket)
1 (black) 0.821 0.5262 12 14 34 46 24 (weight of each kind of seed)
0 (red) 0.345 0.6534 1002 (capacity of bucket)
1 (black) 0.812 0.9134 42 44 24 26 14 (weight of each kind of seed)
...

We want to implemet a program that will do the following:

Α
The ants decide to not move from their place again, before they decide which is 
the shorter path (Minimum Spanning Tree) that will cover all ants. Find the 
path with the use of the algorithm of Kruskal+union-find.
You much create the function findMST() that exists in class IP_AEM.

B
The red ants must create couples with the black ants, so they can put the seeds 
into their buckets. The ants prefer to couple with ants that are closer to them and
have the opposite color. Create couples using the algorith of Stable Marriage
having the red ants do the proposing first. This will have to be implemented
with the function findStableMatchings() which will be in the class IP_AEM.

C
Say that we have a couple which is the 1st red ant with the 1st black ant. 
The red ant wants to check is it can full its bucket with the less possible
seeds. Check if there are such ways and which one is the better one with the
use of dynamic programming. This problem is the same as the Coin Change one. 


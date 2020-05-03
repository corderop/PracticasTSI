include "globals.mzn";

% Para cada tarea 1->Comienzo, 2->Final
% Filas: 1->A, 2->B, 3->C...
array[1..9,1..3] of var int: tiempos;
var int: t;

constraint forall(i in 1..9)( tiempos[i,1]>=0 /\ tiempos[i,2]>=0 /\ tiempos[i,3]>=1 /\ tiempos[i,3]<=3);

% Duración de cada una

% A
constraint (tiempos[1,3]==1 \/ tiempos[1,2]-tiempos[1,1]==4) \/ (tiempos[1,3]==2 \/ tiempos[1,2]-tiempos[1,1]==7) \/ (tiempos[1,3]==3 \/ tiempos[1,2]-tiempos[1,1]==10); 
constraint tiempos[2,2]-tiempos[2,1]==3; % B
constraint tiempos[3,2]-tiempos[3,1]==1; % C
constraint tiempos[4,2]-tiempos[4,1]==8; % D
constraint tiempos[5,2]-tiempos[5,1]==2; % E
constraint tiempos[6,2]-tiempos[6,1]==1; % F
constraint tiempos[7,2]-tiempos[7,1]==1; % G
constraint tiempos[8,2]-tiempos[8,1]==3; % H
constraint tiempos[9,2]-tiempos[9,1]==2; % I

% Restricciones de predecesores

% El tiempo de inicio de B tiene que ser cuando A haya acabado
constraint tiempos[2,1]>=tiempos[1,2];
% El tiempo de inicio de D tiene que ser cuando A haya acabado
constraint tiempos[4,1]>=tiempos[1,2];
% El tiempo de inicio de H tiene que ser cuando A haya acabado
constraint tiempos[8,1]>=tiempos[1,2];
% El tiempo de inicio de C tiene que ser cuando B haya acabado
constraint tiempos[3,1]>=tiempos[2,2];
% El tiempo de inicio de E tiene que ser cuando C y D haya acabado
constraint tiempos[5,1]>=tiempos[3,2] /\ tiempos[5,1]>=tiempos[4,2];
% El tiempo de inicio de E tiene que ser cuando C y D haya acabado
constraint tiempos[6,1]>=tiempos[3,2] /\ tiempos[6,1]>=tiempos[4,2];
% El tiempo de inicio de E tiene que ser cuando C y D haya acabado
constraint tiempos[7,1]>=tiempos[3,2] /\ tiempos[7,1]>=tiempos[4,2];
% El tiempo de inicio de I tiene que ser cuando F y H haya acabado
constraint tiempos[9,1]>=tiempos[6,2] /\ tiempos[9,1]>=tiempos[8,2];

constraint t==max([ tiempos[i,2] | i in 1..9 ]);

solve minimize t;

output["Días totales: \(t)\n" ++
       "A: Inicio: \(tiempos[1,1]) Final: \(tiempos[1,2])\n" ++
       "B: Inicio: \(tiempos[2,1]) Final: \(tiempos[2,2])\n" ++
       "C: Inicio: \(tiempos[3,1]) Final: \(tiempos[3,2])\n" ++
       "D: Inicio: \(tiempos[4,1]) Final: \(tiempos[4,2])\n" ++
       "E: Inicio: \(tiempos[5,1]) Final: \(tiempos[5,2])\n" ++
       "F: Inicio: \(tiempos[6,1]) Final: \(tiempos[6,2])\n" ++
       "G: Inicio: \(tiempos[7,1]) Final: \(tiempos[7,2])\n" ++
       "H: Inicio: \(tiempos[8,1]) Final: \(tiempos[8,2])\n" ++
       "I: Inicio: \(tiempos[9,1]) Final: \(tiempos[9,2])"];
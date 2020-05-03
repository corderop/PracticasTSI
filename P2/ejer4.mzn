include "globals.mzn";

% Columnas de la matriz: 1->Aula, 2->Hora
array[1..12,1..2] of var int: asig; % 1->IA-G1  2 ->IA-G2  3 ->IA-G3  4 ->IA-G4
                                    % 5->TSI-G1 6 ->TSI-G2 7 ->TSI-G3 8 ->TSI-G4
                                    % 9->FBD-G1 10->FBD-G2 11->FBD-G3 12->FBD-G4

% a. Existen cuatro aulas, disponibles entre las 9:00 y las 13:00.

% Para que los datos de la matriz sean correctos en cuanto a horas y nº de aulas
constraint forall( i in 1..12 )( asig[i,1] >=1 /\ asig[i,1] <=4 /\ asig[i,2] >=9 /\ asig[i,2] <=12 );

% h. Cada profesor tiene las restricciones de horarios definidas en la siguiente tabla.

% Horario no disponible del profesor 2
constraint asig[9,2]!=10 /\ asig[10,2]!=10;
% Horario no disponible del profesor 4
constraint asig[3,2]!=9 /\ asig[4,2]!=9;

% g. Cada profesor imparte docencia de un único grupo/asignatura en cada momento.

% Asignaturas del profesor 1 no se pueden impartir a la misma hora
constraint asig[1,2]!=asig[2,2] /\ asig[1,2]!=asig[5,2] /\ asig[1,2]!=asig[6,2] /\ asig[2,2]!=asig[5,2] /\ asig[2,2]!=asig[6,2] /\ asig[5,2]!=asig[6,2];
% Asignaturas del profesor 2 no se pueden impartir a la misma hora
constraint asig[9,2]!=asig[10,2];
% Asignaturas del profesor 3 no se pueden impartir a la misma hora
constraint asig[7,2]!=asig[8,2] /\ asig[7,2]!=asig[11,2] /\ asig[7,2]!=asig[12,2] /\ asig[8,2]!=asig[11,2] /\ asig[8,2]!=asig[12,2] /\ asig[11,2]!=asig[12,2];
% Asignaturas del profesor 4 no se pueden impartir a la misma hora
constraint asig[3,2]!=asig[4,2];

% e. Cada grupo recibe docencia de una única asignatura en cada momento.

% Grupo 1 con diferentes horas
constraint asig[1,2]!=asig[5,2] /\ asig[1,2]!=asig[9,2] /\ asig[5,2]!=asig[9,2];
% Grupo 2 con diferentes horas
constraint asig[2,2]!=asig[6,2] /\ asig[1,2]!=asig[10,2] /\ asig[6,2]!=asig[10,2];
% Grupo 3 con diferentes horas
constraint asig[3,2]!=asig[7,2] /\ asig[1,2]!=asig[11,2] /\ asig[7,2]!=asig[11,2];
% Grupo 4 con diferentes horas
constraint asig[4,2]!=asig[8,2] /\ asig[1,2]!=asig[12,2] /\ asig[8,2]!=asig[12,2];

% b. Cada aula sólo puede estar ocupada por un único profesor/a al mismo tiempo.
constraint forall(i,j in 1..12)( i==j \/ asig[i,2] != asig[j,2] \/ asig[i,1] != asig[j,1] );

solve satisfy;

output ["IA-G1 -> Aula \(asig[1,1]) Hora \(asig[1,2])\n" ++
        "IA-G2 -> Aula \(asig[2,1]) Hora \(asig[2,2])\n" ++
        "IA-G3 -> Aula \(asig[3,1]) Hora \(asig[3,2])\n" ++
        "IA-G4 -> Aula \(asig[4,1]) Hora \(asig[4,2])\n" ++
        "TSI-G1 -> Aula \(asig[5,1]) Hora \(asig[5,2])\n" ++
        "TSI-G2 -> Aula \(asig[6,1]) Hora \(asig[6,2])\n" ++
        "TSI-G3 -> Aula \(asig[7,1]) Hora \(asig[7,2])\n" ++
        "TSI-G4 -> Aula \(asig[8,1]) Hora \(asig[8,2])\n" ++
        "FBD-G1 -> Aula \(asig[9,1]) Hora \(asig[9,2])\n" ++
        "FBD-G2 -> Aula \(asig[10,1]) Hora \(asig[10,2])\n" ++
        "FBD-G3 -> Aula \(asig[11,1]) Hora \(asig[11,2])\n" ++
        "FBD-G4 -> Aula \(asig[12,1]) Hora \(asig[12,2])\n"]

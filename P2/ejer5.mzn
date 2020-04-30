include "globals.mzn";

% Columnas de la matriz: 1->Dia, 2->Hora
array[1..16,1..2] of var int: asig; % 1 ->A1-1, 2 ->A1-2, 3 ->A2-1, 4 ->A2-2
                                    % 5 ->A3-1, 6 ->A3-2, 7 ->A4-1, 8 ->A4-2
                                    % 9 ->A5-1, 10->A5-2, 11->A6-1, 12->A6-2
                                    % 13->A7-1, 14->A7-2, 15->A8,   16->A9

% Hay 5 días y las horas son de 8 a 14                         
constraint forall( i in 1..16 )( asig[i,1] >=1 /\ asig[i,1] <=5 /\ asig[i,2] >=8 /\ asig[i,2] <=13 );
                              
% Para los bloques de 2 horas no permito que tengan clase a las 10 porque solaparía con el recreo
% Tampoco a las 13 porque el aula cierra a las 14:00
constraint asig[1,2]!=10 /\ asig[2,2]!=10 /\ asig[1,2]!=13 /\ asig[2,2]!=13; % A1
constraint asig[5,2]!=10 /\ asig[6,2]!=10 /\ asig[5,2]!=13 /\ asig[6,2]!=13; % A3
constraint asig[7,2]!=10 /\ asig[8,2]!=10 /\ asig[7,2]!=13 /\ asig[8,2]!=13; % A4
constraint asig[9,2]!=10 /\ asig[10,2]!=10 /\ asig[9,2]!=13 /\ asig[10,2]!=13; % A5
constraint asig[15,2]!=10 /\ asig[15,2]!=13; % A8

% Los que son bloques de dos horas al asignarse un bloque 
constraint forall(i,j in 1..16)( i==j \/ i==3 \/ i==4 \/ i==11 \/ i==12 \/ i==13 \/ i==14 \/ i==16 \/ asig[i,1]!=asig[j,1] \/ asig[i,2]>asig[j,2] \/ abs(asig[i,2]-asig[j,2])>1 );
                            
% d. En cada día de la semana sólo se puede impartir, como máximo, UN bloque de cada asignatura.
% Comparo los bloques de las asignaturas que no tengan el mismo día asignado
constraint asig[1,1]!=asig[2,1];
constraint asig[3,1]!=asig[4,1];
constraint asig[5,1]!=asig[6,1];
constraint asig[7,1]!=asig[8,1];
constraint asig[9,1]!=asig[10,1];
constraint asig[11,1]!=asig[12,1];
constraint asig[13,1]!=asig[14,1];

% Profesores solo pueden impartir un bloque al día excepto el 4
% Comparo que los bloques de una asignatura de un profesor no coincida con los de otra asignatura del profesor
constraint asig[1,1]!=asig[5,1] /\ asig[1,1]!=asig[6,1] /\ asig[2,1]!=asig[5,1] /\ asig[2,1]!=asig[6,1];   % Profesor 1
constraint asig[7,1]!=asig[9,1] /\ asig[7,1]!=asig[10,1] /\ asig[8,1]!=asig[9,1] /\ asig[8,1]!=asig[10,1]; % Profesor 2
constraint asig[11,1]!=asig[16,1] /\ asig[12,1]!=asig[16,1]; % Profesor 3

% Tomo las 11 como recreo
constraint forall(i in 1..16)( asig[i,2]!=11 );

% El aula un mismo día solo puede estar ocupada a una hora por una clase
constraint forall(i,j in 1..16)( i==j \/ asig[i,2] != asig[j,2] \/ asig[i,1] != asig[j,1] );

solve satisfy;

output ["A1-1 -> Día \(asig[1,1]) Hora \(asig[1,2])\n" ++
        "A1-2 -> Día \(asig[2,1]) Hora \(asig[2,2])\n" ++
        "A2-1 -> Día \(asig[3,1]) Hora \(asig[3,2])\n" ++
        "A2-2 -> Día \(asig[4,1]) Hora \(asig[4,2])\n" ++
        "A3-1 -> Día \(asig[5,1]) Hora \(asig[5,2])\n" ++
        "A3-2 -> Día \(asig[6,1]) Hora \(asig[6,2])\n" ++
        "A4-1 -> Día \(asig[7,1]) Hora \(asig[7,2])\n" ++
        "A4-2 -> Día \(asig[8,1]) Hora \(asig[8,2])\n" ++
        "A5-1 -> Día \(asig[9,1]) Hora \(asig[9,2])\n" ++
        "A5-2 -> Día \(asig[10,1]) Hora \(asig[10,2])\n" ++
        "A6-1 -> Día \(asig[11,1]) Hora \(asig[11,2])\n" ++
        "A6-2 -> Día \(asig[12,1]) Hora \(asig[12,2])\n" ++
        "A7-1 -> Día \(asig[13,1]) Hora \(asig[13,2])\n" ++
        "A7-2 -> Día \(asig[14,1]) Hora \(asig[14,2])\n" ++
        "A8   -> Día \(asig[15,1]) Hora \(asig[15,2])\n" ++
        "A9   -> Día \(asig[16,1]) Hora \(asig[16,2])\n"];

include "globals.mzn";

array[1..4,9..12] of var 1..3: aula_asig;    % 1 - IA, 2 - TSI, 3 - FBD
array[1..4,9..12] of var 1..4: aula_grupo;
array[1..4,9..12] of var 1..4: aula_prof;

% Restricción para 1h semanal por asignatura
% Cada asignatura debe aparecer 4 veces ya que tiene 
% 4 grupos
% constraint count(aula_asig, 1) == 4;
% constraint count(aula_asig, 2) == 4;
% constraint count(aula_asig, 3) == 4;

% % Un grupo no puede tener el mismo horario a la vez en dos clases diferentes
constraint forall(i in 9..12 )( alldifferent( [aula_grupo[j,i] | j in 1..4] ));
% Lo mismo con los profesores
constraint forall(i in 9..12 )( alldifferent( [aula_prof[j,i] | j in 1..4] ));

% Eliminamos los horarios en los que los profesores no pueden dar prácticas
constraint count([aula_prof[i,10] | i in 1..4],2) == 0;
constraint count([aula_prof[i,9] | i in 1..4],4) == 0;

constraint forall(j in 9..12)( ( aula_prof[1,j]==1 /\ aula_grupo[1,j]==1 /\ aula_asig[1,j]==1 ) \/ % Profesor 1
                               ( aula_prof[1,j]==1 /\ aula_grupo[1,j]==2 /\ aula_asig[1,j]==1 ) \/
                               ( aula_prof[1,j]==1 /\ aula_grupo[1,j]==1 /\ aula_asig[1,j]==2 ) \/
                               ( aula_prof[1,j]==1 /\ aula_grupo[1,j]==2 /\ aula_asig[1,j]==2 ) \/
                               ( aula_prof[1,j]==2 /\ aula_grupo[1,j]==1 /\ aula_asig[1,j]==3 ) \/ % Profesor 2
                               ( aula_prof[1,j]==2 /\ aula_grupo[1,j]==2 /\ aula_asig[1,j]==3 ) \/
                               ( aula_prof[1,j]==3 /\ aula_grupo[1,j]==3 /\ aula_asig[1,j]==2 ) \/ % Profesor 3
                               ( aula_prof[1,j]==3 /\ aula_grupo[1,j]==4 /\ aula_asig[1,j]==2 ) \/
                               ( aula_prof[1,j]==3 /\ aula_grupo[1,j]==3 /\ aula_asig[1,j]==3 ) \/
                               ( aula_prof[1,j]==3 /\ aula_grupo[1,j]==4 /\ aula_asig[1,j]==3 ) \/
                               ( aula_prof[1,j]==4 /\ aula_grupo[1,j]==3 /\ aula_asig[1,j]==1 ) \/ % Profesor 4
                               ( aula_prof[1,j]==4 /\ aula_grupo[1,j]==4 /\ aula_asig[1,j]==1 ));
                               
constraint forall(j in 9..12)( ( aula_prof[2,j]==1 /\ aula_grupo[2,j]==1 /\ aula_asig[2,j]==1 ) \/ % Profesor 1
                               ( aula_prof[2,j]==1 /\ aula_grupo[2,j]==2 /\ aula_asig[2,j]==1 ) \/
                               ( aula_prof[2,j]==1 /\ aula_grupo[2,j]==1 /\ aula_asig[2,j]==2 ) \/
                               ( aula_prof[2,j]==1 /\ aula_grupo[2,j]==2 /\ aula_asig[2,j]==2 ) \/
                               ( aula_prof[2,j]==2 /\ aula_grupo[2,j]==1 /\ aula_asig[2,j]==3 ) \/ % Profesor 2
                               ( aula_prof[2,j]==2 /\ aula_grupo[2,j]==2 /\ aula_asig[2,j]==3 ) \/
                               ( aula_prof[2,j]==3 /\ aula_grupo[2,j]==3 /\ aula_asig[2,j]==2 ) \/ % Profesor 3
                               ( aula_prof[2,j]==3 /\ aula_grupo[2,j]==4 /\ aula_asig[2,j]==2 ) \/
                               ( aula_prof[2,j]==3 /\ aula_grupo[2,j]==3 /\ aula_asig[2,j]==3 ) \/
                               ( aula_prof[2,j]==3 /\ aula_grupo[2,j]==4 /\ aula_asig[2,j]==3 ) \/
                               ( aula_prof[2,j]==4 /\ aula_grupo[2,j]==3 /\ aula_asig[2,j]==1 ) \/ % Profesor 4
                               ( aula_prof[2,j]==4 /\ aula_grupo[2,j]==4 /\ aula_asig[2,j]==1 ));
                               
constraint forall(j in 9..12)( ( aula_prof[3,j]==1 /\ aula_grupo[3,j]==1 /\ aula_asig[3,j]==1 ) \/ % Profesor 1
                               ( aula_prof[3,j]==1 /\ aula_grupo[3,j]==2 /\ aula_asig[3,j]==1 ) \/
                               ( aula_prof[3,j]==1 /\ aula_grupo[3,j]==1 /\ aula_asig[3,j]==2 ) \/
                               ( aula_prof[3,j]==1 /\ aula_grupo[3,j]==2 /\ aula_asig[3,j]==2 ) \/
                               ( aula_prof[3,j]==2 /\ aula_grupo[3,j]==1 /\ aula_asig[3,j]==3 ) \/ % Profesor 2
                               ( aula_prof[3,j]==2 /\ aula_grupo[3,j]==2 /\ aula_asig[3,j]==3 ) \/
                               ( aula_prof[3,j]==3 /\ aula_grupo[3,j]==3 /\ aula_asig[3,j]==2 ) \/ % Profesor 3
                               ( aula_prof[3,j]==3 /\ aula_grupo[3,j]==4 /\ aula_asig[3,j]==2 ) \/
                               ( aula_prof[3,j]==3 /\ aula_grupo[3,j]==3 /\ aula_asig[3,j]==3 ) \/
                               ( aula_prof[3,j]==3 /\ aula_grupo[3,j]==4 /\ aula_asig[3,j]==3 ) \/
                               ( aula_prof[3,j]==4 /\ aula_grupo[3,j]==3 /\ aula_asig[3,j]==1 ) \/ % Profesor 4
                               ( aula_prof[3,j]==4 /\ aula_grupo[3,j]==4 /\ aula_asig[3,j]==1 ));
                               
constraint forall(j in 9..12)( ( aula_prof[4,j]==1 /\ aula_grupo[4,j]==1 /\ aula_asig[4,j]==1 ) \/ % Profesor 1
                               ( aula_prof[4,j]==1 /\ aula_grupo[4,j]==2 /\ aula_asig[4,j]==1 ) \/
                               ( aula_prof[4,j]==1 /\ aula_grupo[4,j]==1 /\ aula_asig[4,j]==2 ) \/
                               ( aula_prof[4,j]==1 /\ aula_grupo[4,j]==2 /\ aula_asig[4,j]==2 ) \/
                               ( aula_prof[4,j]==2 /\ aula_grupo[4,j]==1 /\ aula_asig[4,j]==3 ) \/ % Profesor 2
                               ( aula_prof[4,j]==2 /\ aula_grupo[4,j]==2 /\ aula_asig[4,j]==3 ) \/
                               ( aula_prof[4,j]==3 /\ aula_grupo[4,j]==3 /\ aula_asig[4,j]==2 ) \/ % Profesor 3
                               ( aula_prof[4,j]==3 /\ aula_grupo[4,j]==4 /\ aula_asig[4,j]==2 ) \/
                               ( aula_prof[4,j]==3 /\ aula_grupo[4,j]==3 /\ aula_asig[4,j]==3 ) \/
                               ( aula_prof[4,j]==3 /\ aula_grupo[4,j]==4 /\ aula_asig[4,j]==3 ) \/
                               ( aula_prof[4,j]==4 /\ aula_grupo[4,j]==3 /\ aula_asig[4,j]==1 ) \/ % Profesor 4
                               ( aula_prof[4,j]==4 /\ aula_grupo[4,j]==4 /\ aula_asig[4,j]==1 ));
                               
solve satisfy;

% output ["Aula 09:00 = \(aula[9])\n10:00 = \(aula[10])\n11:00 = \(aula[11])\n12:00 = \(aula[12])\n13:00 = \(aula[13])\n14:00 = \(aula[14])"];
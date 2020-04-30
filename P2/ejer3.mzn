include "globals.mzn";

% Defino las variables
% Defino un array para las asignaciones de cada hora
array[9..14] of var 1..6: aula;

% No puede haber un mismo profesor asignado a dos horas distintas
constraint all_different(aula);
% Aquí digo que para cada subvector con el horario de cada 
% profesor este debe aparecer 1 vez
constraint count(aula[11..14], 1)==1;
constraint count(aula[11..12], 2)==1;
constraint count(aula[10..13], 3)==1;
constraint count(aula[10..12], 4)==1;
constraint count(aula[11..12], 5)==1;
constraint count(aula[9..14], 6)==1;

output ["09:00 = \(aula[9])\n10:00 = \(aula[10])\n11:00 = \(aula[11])\n12:00 = \(aula[12])\n13:00 = \(aula[13])\n14:00 = \(aula[14])"];

solve satisfy;
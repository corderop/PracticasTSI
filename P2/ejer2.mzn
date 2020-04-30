include "globals.mzn";

% Definición de variables:
% Array que representa X de forma que cada posición es 
% un dígito de este
array[0..9] of var int: X;

% Compara que para cada posición el número que haya
% sea las veces que aparece este en el vector.
constraint forall(i in 0..9)(count(X, i) == X[i]);

% Modifico el output para que muestre el número en lugar
% del vector.
output ["\(X[i])" | i in 0..9];

solve satisfy;
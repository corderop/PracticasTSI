include "globals.mzn";

var int: tiempo;
array[1..9] of int: t = [7,3,1,8,2,1,1,3,2];
array[1..9,1..2] of var 1..9: orden;

% B tiene que tener delante a A
constraint forall(i,j in 1..9)( orden[i,1]!=1 \/ orden[j,1]!=2 \/ ( i<j )  );
% D tiene que tener delante a A
constraint forall(i,j in 1..9)( orden[i,1]!=1 \/ orden[j,1]!=4 \/ ( i<j )  );
% H tiene que tener delante a A
constraint forall(i,j in 1..9)( orden[i,1]!=1 \/ orden[j,1]!=8 \/ ( i<j )  );
% C tiene que tener delante a B
constraint forall(i,j in 1..9)( orden[i,1]!=2 \/ orden[j,1]!=3 \/ ( i<j )  );
% E tiene que tener delante a C
constraint forall(i,j in 1..9)( orden[i,1]!=3 \/ orden[j,1]!=5 \/ ( i<j )  );
% E tiene que tener delante a D
constraint forall(i,j in 1..9)( orden[i,1]!=4 \/ orden[j,1]!=5 \/ ( i<j )  );
% F tiene que tener delante a C
constraint forall(i,j in 1..9)( orden[i,1]!=3 \/ orden[j,1]!=6 \/ ( i<j )  );
% F tiene que tener delante a D
constraint forall(i,j in 1..9)( orden[i,1]!=4 \/ orden[j,1]!=6 \/ ( i<j )  );
% G tiene que tener delante a C
constraint forall(i,j in 1..9)( orden[i,1]!=3 \/ orden[j,1]!=7 \/ ( i<j )  );
% G tiene que tener delante a D
constraint forall(i,j in 1..9)( orden[i,1]!=4 \/ orden[j,1]!=7 \/ ( i<j )  );
% I tiene que tener delante a F
constraint forall(i,j in 1..9)( orden[i,1]!=6 \/ orden[j,1]!=9 \/ ( i<j )  );
% I tiene que tener delante a H
constraint forall(i,j in 1..9)( orden[i,1]!=8 \/ orden[j,1]!=9 \/ ( i<j )  );

constraint forall(j in 1..9)( ( j==1 /\ orden[j,2] == t[orden[j,1]] ) \/ ( j!=1 /\ orden[j,2] == ( max( [ orden[i,2] | i in 1..j-1] )+t[orden[j,1]] ) ));

constraint all_different([orden[i,1] | i in 1..9]);

solve satisfy;
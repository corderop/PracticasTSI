% Definición de variables:
var 0..9: T;  var 0..9: F;  var 0..9: N; var 0..9: A;
var 0..9: E;  var 0..9: D;  var 0..9: K;
var 0..9: S;  var 0..9: I;  var 0..9: R;

% Definición de restricciones:

% Defino que no pueden ser iguales los valores de las letras comparando todas las letras entre si
constraint T!= E /\ T!= S /\ T!= F /\ T!= D /\ T!= I /\ T!= N /\ T!= K /\ T!= R /\ T!= A ;
constraint E!= S /\ E!= F /\ E!= D /\ E!= I /\ E!= N /\ E!= K /\ E!= R /\ E!= A ;
constraint S!= F /\ S!= D /\ S!= I /\ S!= N /\ S!= K /\ S!= R /\ S!= A ;
constraint F!= D /\ F!= I /\ F!= N /\ F!= K /\ F!= R /\ F!= A ;
constraint D!= I /\ D!= N /\ D!= K /\ D!= R /\ D!= A ;
constraint I!= N /\ I!= K /\ I!= R /\ I!= A ;
constraint N!= K /\ N!= R /\ N!= A ;
constraint K!= R /\ K!= A ;
constraint R!= A ;

% Aquí me aseguro que según la posición de cada letra y su valor la suma sea correcta
constraint T*10000+E*1000+S*100+T*10+E + 
           F*10000+E*1000+S*100+T*10+E + 
           D*10000+E*1000+I*100+N*10+E 
           == 
           K*100000+R*10000+A*1000+F*100+T*10+E;

solve satisfy;
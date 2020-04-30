include "globals.mzn";

var int : pr;
var int : peso;
array[1..12] of var 0..1 : cogidos;
array[1..12,1..2] of var int : pp; % 1->Peso 2->Preferencia

constraint (cogidos[1]==0  /\ pp[1,1]==0  /\ pp[1,2]==0 )  \/ (cogidos[1]==1  /\ pp[1,1]==9   /\ pp[1,2]==150 ); %Mapa
constraint (cogidos[2]==0  /\ pp[2,1]==0  /\ pp[2,2]==0 )  \/ (cogidos[2]==1  /\ pp[2,1]==13  /\ pp[2,2]==35 );  %Compás
constraint (cogidos[3]==0  /\ pp[3,1]==0  /\ pp[3,2]==0 )  \/ (cogidos[3]==1  /\ pp[3,1]==153 /\ pp[3,2]==200 ); %Agua
constraint (cogidos[4]==0  /\ pp[4,1]==0  /\ pp[4,2]==0 )  \/ (cogidos[4]==1  /\ pp[4,1]==50  /\ pp[4,2]==160 ); %Sandwich
constraint (cogidos[5]==0  /\ pp[5,1]==0  /\ pp[5,2]==0 )  \/ (cogidos[5]==1  /\ pp[5,1]==15  /\ pp[5,2]==60 );  %Azúcar
constraint (cogidos[6]==0  /\ pp[6,1]==0  /\ pp[6,2]==0 )  \/ (cogidos[6]==1  /\ pp[6,1]==68  /\ pp[6,2]==45 );  %Lata
constraint (cogidos[7]==0  /\ pp[7,1]==0  /\ pp[7,2]==0 )  \/ (cogidos[7]==1  /\ pp[7,1]==27  /\ pp[7,2]==60 );  %Plátano
constraint (cogidos[8]==0  /\ pp[8,1]==0  /\ pp[8,2]==0 )  \/ (cogidos[8]==1  /\ pp[8,1]==39  /\ pp[8,2]==40 );  %Manzana
constraint (cogidos[9]==0  /\ pp[9,1]==0  /\ pp[9,2]==0 )  \/ (cogidos[9]==1  /\ pp[9,1]==23  /\ pp[9,2]==30 );  %Queso
constraint (cogidos[10]==0 /\ pp[10,1]==0 /\ pp[10,2]==0 ) \/ (cogidos[10]==1 /\ pp[10,1]==52 /\ pp[10,2]==10 ); %Cerveza
constraint (cogidos[11]==0 /\ pp[11,1]==0 /\ pp[11,2]==0 ) \/ (cogidos[11]==1 /\ pp[11,1]==11 /\ pp[11,2]==70 ); %Protector Solar
constraint (cogidos[12]==0 /\ pp[12,1]==0 /\ pp[12,2]==0 ) \/ (cogidos[12]==1 /\ pp[12,1]==32 /\ pp[12,2]==30 ); %Cámara

constraint peso==sum([ pp[i,1] | i in 1..12 ]); % Peso
constraint pr==sum([ pp[i,2] | i in 1..12 ]);   % Preferencia total

constraint peso <= 275;

solve maximize pr;
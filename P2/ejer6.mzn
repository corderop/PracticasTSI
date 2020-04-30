include "globals.mzn";

% Filas: 
% 1->Vasco
% 2->Catalan
% 3->Gallego
% 4->Navarro
% 5->Andaluz
array[1..5,1..5] of var 1..5: persona;
% Columnas:
% 1->Casa: 1 a 5 de izquierda a derecha
% 2->Color de la casa: 1->Rojo, 2->Verde, 3->Blanca, 4->Amarilla, 5->Azul
% 3->Animal: 1->Perro, 2->Caracoles, 3->Zorro, 4->Caballo, 5->Cebra
% 4->Profesión: 1->Pintor, 2->Escultor, 3->Diplomático, 4->Violinista, 5->Médico
% 5->Bebida: 1->Te, 2->Café, 3->Leche, 4->Zumo, 5->Agua

constraint forall(i in 1..5)( all_different( [persona[j,i] | j in 1..5 ] ));

% Vasco vive en la casa roja
constraint persona[1,2] == 1;

% Catalán tiene perro
constraint persona[2,3] == 1;

% Gallego es pintor
constraint persona[3,4] == 1;

% Navarro bebe te
constraint persona[4,5] == 1;

% Andaluz vive en la primera cada de la izquierda
constraint persona[5,1] == 1;

% El de la casa verde bebe café
constraint forall(i in 1..5)( ( persona[i,2]==2 /\ persona[i,5]==2 ) \/ ( persona[i,2]!=2 /\ persona[i,5]!=2 ) );

% La casa verde está al lado de la blanca y a su derecha
constraint forall(i,j in 1..5)( ( persona[i,2]==2 /\ persona[j,2]==3 /\ persona[i,1]-persona[j,1]==1 ) \/ persona[i,2]!=2 \/ persona[j,2]!=3 );

% El escultor cria caracoles
constraint forall(i in 1..5)( ( persona[i,4]==2 /\ persona[i,3]==2 ) \/ ( persona[i,4]!=2 /\ persona[i,3]!=2 ) );

% El diplomático vive en la casa amarilla
constraint forall(i in 1..5)( ( persona[i,2]==4 /\ persona[i,4]==3 ) \/ ( persona[i,2]!=4 /\ persona[i,4]!=3 ) );

% En la casa central se bebe leche
constraint forall(i in 1..5)( ( persona[i,1]==3 /\ persona[i,5]==3 ) \/ ( persona[i,1]!=3 /\ persona[i,5]!=3 ) );

% La casa del andaluz está al lado de la azul
constraint forall(i in 1..5)( ( persona[i,1]==2 /\ persona[i,2]==5 ) \/ ( persona[i,1]!=2 /\ persona[i,2]!=5 ) );

% El violinista bebe zumo
constraint forall(i in 1..5)( ( persona[i,4]==4 /\ persona[i,5]==4 ) \/ ( persona[i,4]!=4 /\ persona[i,5]!=4 ) );

% El zorro está en una casa de al lado de la del médico
constraint forall(i,j in 1..5)( ( persona[i,3]==3 /\ persona[j,4]==5 /\ abs(persona[i,1]-persona[j,1])==1 ) \/ persona[i,3]!=3 \/ persona[j,4]!=5 );

% El caballo está en una casa al lado de la del diplomático
constraint forall(i,j in 1..5)( ( persona[i,3]==4 /\ persona[j,4]==3 /\ abs(persona[i,1]-persona[j,1])==1 ) \/ persona[i,3]!=4 \/ persona[j,4]!=3 );

solve satisfy;

output ["(Respuesta a la pregunta en la memoria)\n" ++
        "Vasco:\n" ++
        "   Posición de casa: \(persona[1,1])\n" ++
        "   Color de la casa: \(persona[1,2])\n" ++
        "   Animal: \(persona[1,3])\n" ++
        "   Profesión: \(persona[1,4])\n" ++
        "   Bebida: \(persona[1,5])\n" ++
        "Catalán:\n" ++
        "   Posición de casa: \(persona[2,1])\n" ++
        "   Color de la casa: \(persona[2,2])\n" ++
        "   Animal: \(persona[2,3])\n" ++
        "   Profesión: \(persona[2,4])\n" ++
        "   Bebida: \(persona[2,5])\n" ++
        "Gallego:\n" ++
        "   Posición de casa: \(persona[3,1])\n" ++
        "   Color de la casa: \(persona[3,2])\n" ++
        "   Animal: \(persona[3,3])\n" ++
        "   Profesión: \(persona[3,4])\n" ++
        "   Bebida: \(persona[3,5])\n" ++
        "Navarro:\n"++
        "   Posición de casa: \(persona[4,1])\n" ++
        "   Color de la casa: \(persona[4,2])\n" ++
        "   Animal: \(persona[4,3])\n" ++
        "   Profesión: \(persona[4,4])\n" ++
        "   Bebida: \(persona[4,5])\n" ++
        "Andaluz:\n"++
        "   Posición de casa: \(persona[5,1])\n" ++
        "   Color de la casa: \(persona[5,2])\n" ++
        "   Animal: \(persona[5,3])\n" ++
        "   Profesión: \(persona[5,4])\n" ++
        "   Bebida: \(persona[5,5])\n"
];

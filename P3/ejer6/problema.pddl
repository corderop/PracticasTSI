(define (problem ejer6_p)
    (:domain ejer6)
    (:objects
        vce1 - unidad
        marine1 marine2 - unidad
        segador1 - unidad
        centrodemando1 barracon1 extractor1 bdi1 d1 - edificio
    )
    (:init
        (Gas Gas)
        (Segador Segador)
        
        ; Inicializo las funciones
        (= (cantidad Gas) 0)
        (= (cantidad Minerales) 0)

        (= (limite Gas) 100)
        (= (limite Minerales) 100)

        (= (coste_e Minerales CentroDeMando) 150)
        (= (coste_e Gas CentroDeMando) 50)

        (= (coste_e Minerales Barracones) 150)
        (= (coste_e Gas Barracones) 0)

        (= (coste_e Minerales Extractor) 75)
        (= (coste_e Gas Extractor) 0)

        (= (coste_e Minerales BahiaDeIngenieria) 125)
        (= (coste_e Gas BahiaDeIngenieria) 0)

        (= (coste_e Minerales Deposito) 75)
        (= (coste_e Gas Deposito) 25)

        (= (coste_u Minerales VCE) 50)
        (= (coste_u Gas VCE) 0)

        (= (coste_u Minerales Marine) 50)
        (= (coste_u Gas Marine) 0)

        (= (coste_u Minerales Segador) 50)
        (= (coste_u Gas Segador) 50)

        (= (coste_i Minerales ImpulsorSegador) 50)
        (= (coste_i Gas ImpulsorSegador) 200)

        ; Declaro los tipos
        (esTipo_u vce1 VCE)
        (esTipo_u marine1 Marine)
        (esTipo_u marine2 Marine)
        (esTipo_u segador1 Segador)
        (esTipo_e centrodemando1 CentroDeMando)
        (esTipo_e barracon1 Barracones)
        (esTipo_e extractor1 Extractor)
        (esTipo_e bdi1 BahiaDeIngenieria)
        (esTipo_e d1 Deposito)

        ; Declaro posiciones
        (en_un vce1 T3_1)
        (en_ed centrodemando1 T4_4)
        (en_ed bdi1 T5_4)
        (nodo_recurso Minerales T2_1)
        (nodo_recurso Minerales T2_5)
        (nodo_recurso Minerales T4_3)
        (nodo_recurso Gas T1_5)
        (nodo_recurso Gas T5_2)

        ; Lugar de reclutamiento para las unidades
        (lugar_reclutamiento VCE CentroDeMando)
        (lugar_reclutamiento Marine Barracones)
        (lugar_reclutamiento Segador Barracones)

        ; Ninguna unidad ocupada
        (libre vce1)

        ; Tablero
        (conectado T1_1 T1_2)
        (conectado T1_1 T2_1)
        (conectado T1_2 T1_1)
        (conectado T1_2 T1_3)
        (conectado T1_2 T2_2)
        (conectado T1_3 T2_3)
        (conectado T1_3 T1_2)
        (conectado T1_3 T1_4)
        (conectado T1_4 T2_4)
        (conectado T1_4 T1_3)
        (conectado T1_4 T1_5)
        (conectado T1_5 T1_4)
        (conectado T1_5 T2_5)

        (conectado T2_1 T1_1)
        (conectado T2_1 T2_2)
        (conectado T2_1 T3_1)
        (conectado T2_2 T1_2)
        (conectado T2_2 T2_3)
        (conectado T2_2 T3_2)
        (conectado T2_2 T2_1)
        (conectado T2_3 T1_3)
        (conectado T2_3 T3_3)
        (conectado T2_3 T2_2)
        (conectado T2_3 T2_4)
        (conectado T2_4 T1_4)
        (conectado T2_4 T2_3)
        (conectado T2_4 T3_4)
        (conectado T2_4 T2_5)
        (conectado T2_5 T3_5)
        (conectado T2_5 T1_5)
        (conectado T2_5 T2_4)
        
        (conectado T3_1 T2_1)
        (conectado T3_1 T3_2)
        (conectado T3_1 T4_1)
        (conectado T3_2 T2_2)
        (conectado T3_2 T3_3)
        (conectado T3_2 T4_2)
        (conectado T3_2 T3_1)
        (conectado T3_3 T2_3)
        (conectado T3_3 T4_3)
        (conectado T3_3 T3_2)
        (conectado T3_3 T3_4)
        (conectado T3_4 T2_4)
        (conectado T3_4 T4_4)
        (conectado T3_4 T3_5)
        (conectado T3_4 T3_3)
        (conectado T3_5 T2_5)
        (conectado T3_5 T3_4)
        (conectado T3_5 T4_5)
        
        (conectado T4_1 T3_1)
        (conectado T4_1 T5_1)
        (conectado T4_1 T4_2)
        (conectado T4_2 T3_2)
        (conectado T4_2 T4_1)
        (conectado T4_2 T5_2)
        (conectado T4_2 T4_3)
        (conectado T4_3 T3_3)
        (conectado T4_3 T4_4)
        (conectado T4_3 T5_3)
        (conectado T4_3 T4_2)
        (conectado T4_4 T3_4)
        (conectado T4_4 T5_4)
        (conectado T4_4 T4_3)
        (conectado T4_4 T4_5)
        (conectado T4_5 T5_5)
        (conectado T4_5 T3_5)
        (conectado T4_5 T4_4)
        
        (conectado T5_1 T4_1)
        (conectado T5_1 T5_2)
        (conectado T5_2 T4_2)
        (conectado T5_2 T5_3)
        (conectado T5_2 T5_1)
        (conectado T5_3 T4_3)
        (conectado T5_3 T5_4)
        (conectado T5_3 T5_2)
        (conectado T5_4 T4_4)
        (conectado T5_4 T5_3)
        (conectado T5_4 T5_5)
        (conectado T5_5 T4_5)
        (conectado T5_5 T5_4)

        ; Declaro las casillas vacias menos donde hay edificios
        (vacia T1_1)
        (vacia T1_2)
        (vacia T1_3)
        (vacia T1_4)
        (vacia T1_5)

        (vacia T2_1)
        (vacia T2_2)
        (vacia T2_3)
        (vacia T2_4)
        (vacia T2_5)

        (vacia T3_1)
        (vacia T3_2)
        (vacia T3_3)
        (vacia T3_4)
        (vacia T3_5)

        (vacia T4_1)
        (vacia T4_2)
        (vacia T4_3)
        (vacia T4_5)

        (vacia T5_1)
        (vacia T5_2)
        (vacia T5_3)
        (vacia T5_4)
        (vacia T5_5)
    )
    (:goal
        (and
            (en_un marine1 T5_1)
            (en_un marine2 T4_2)
            (en_un segador1 T4_2)
        )
    )
)
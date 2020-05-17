(define (domain ejer1)
    (:requirements :strips :typing :adl)
    (:types
        unidad edificio recurso casilla - object
        tipoUnidad tipoEdificio - object
    )

    (:constants 
        VCE - tipoUnidad
        CentroDeMando Barracones Extractor - tipoEdificio
        Minerales Gas - recurso
        T1_1 T1_2 T1_3 T1_4 T1_5 T2_1 T2_2 T2_3 T2_4 T2_5 T3_1 T3_2 T3_3 T3_4 T3_5 T4_1 T4_2 T4_3 T4_4 T4_5 T5_1 T5_2 T5_3 T5_4 T5_5 - casilla
    )

    (:predicates
        ; Identificar el tipo
        (esTipo_u ?u - unidad ?t - tipoUnidad)
        (esTipo_e ?e - edificio ?t - tipoEdificio)

        ; Indica la localización de una unidad o edificio
        (en_un ?u - unidad ?x - casilla)
        (en_ed ?e - edificio ?x - casilla)

        ; Indica en que casilla se encuentra un nodo recurso y de que tipo es
        (nodo_recurso ?r - recurso ?x - casilla)

        ; Indica dos casillas adyacentes
        (conectado ?x1 - casilla ?x2 - casilla)

        ; Ocupados
        (libre ?u - unidad)

        ; Indicar si un vce está extrayendo un recurso
        (extrayendo ?u - unidad ?r - recurso)

        ; Minerales para cada edificio
        (recurso_edificio ?r - recurso ?e - tipoEdificio)

        ; No hay ningun edificio en una casilla
        (vacia ?x - casilla)
    )
    
    (:action navegar
        :parameters (?u - unidad ?x1 - casilla ?x2 - casilla)
        :precondition
            (and
                (en_un ?u ?x1)
                (conectado ?x1 ?x2)
            )
        :effect
            (and
                (not (en_un ?u ?x1))
                (en_un ?u ?x2)
            )
    )

    (:action obtenerGas
        :parameters (?u - unidad ?x - casilla ?e - edificio)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (nodo_recurso Gas ?x)
                (en_ed ?e ?x)
                (esTipo_e ?e Extractor)
            )
        :effect
            (and
                (not (libre ?u))
                (extrayendo ?u Gas)
            )
    )

    (:action obtenerMineral
        :parameters (?u - unidad ?x - casilla)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (nodo_recurso Minerales ?x)
            )
        :effect
            (and
                (not (libre ?u))
                (extrayendo ?u Minerales)
            )
    )

    (:action construir
        :parameters (?u - unidad ?e - edificio ?t - tipoEdificio ?x - casilla ?u_ext - unidad)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (esTipo_e ?e ?t)
                (forall (?r - recurso)
                    (or
                        (not (recurso_edificio ?r ?t))
                        (extrayendo ?u_ext ?r)
                    )
                )
                (vacia ?x)
            )
        :effect
            (and
                (en_ed ?e ?x)
                (not (vacia ?x))
            )
    )

    (:action construirExtractor
        :parameters (?u - unidad ?e - edificio ?x - casilla ?u_ext - unidad ?r - recurso)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (esTipo_e ?e Extractor)
                (forall (?r - recurso)
                    (or
                        (not (recurso_edificio ?r Extractor))
                        (extrayendo ?u_ext ?r)
                    )
                )
                (nodo_recurso Gas ?x)
                (vacia ?x)
            )
        :effect
            (and
                (en_ed ?e ?x)
                (not (vacia ?x))
            )
    )
    
)
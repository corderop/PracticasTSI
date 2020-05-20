(define (domain ejer1)
    (:requirements :strips :typing :adl)
    (:types
        unidad edificio recurso casilla - object
        tipoUnidad tipoEdificio - object
    )

    (:constants 
        VCE Marine Segador - tipoUnidad
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

        ; Indicar que un recurso está disponible
        (disponible ?r - recurso)

        ; Recursos para cada edificio
        (recurso_edificio ?r - recurso ?e - tipoEdificio)
        ; Recursos para cada unidad
        (recurso_unidad ?r - recurso ?u - tipoUnidad)
        ; Donde se recluta cada unidad
        (lugar_reclutamiento ?u - tipoUnidad ?e - tipoEdificio)

        ; No hay ningun edificio en una casilla
        (vacia ?x - casilla)
    )
    
    (:action navegar
        :parameters (?u - unidad ?x1 - casilla ?x2 - casilla)
        :precondition
            (and
                (en_un ?u ?x1)
                (conectado ?x1 ?x2)
                (libre ?u)
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
                (en_ed ?e ?x)
                (esTipo_e ?e Extractor)
            )
        :effect
            (and
                (not (libre ?u))
                (extrayendo ?u Gas)
                (disponible Gas)
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
                (disponible Minerales)
            )
    )

    (:action construir
        :parameters (?u - unidad ?e - edificio ?t - tipoEdificio ?x - casilla)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (esTipo_e ?e ?t)
                (forall (?r - recurso)
                    (or
                        (not (recurso_edificio ?r ?t))
                        (disponible ?r)
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
        :parameters (?u - unidad ?e - edificio ?x - casilla)
        :precondition
            (and
                (en_un ?u ?x)
                (libre ?u)
                (esTipo_e ?e Extractor)
                (disponible Minerales)
                (nodo_recurso Gas ?x)
                (vacia ?x)
            )
        :effect
            (and
                (en_ed ?e ?x)
                (not (vacia ?x))
            )
    )

    (:action reclutar
        :parameters (?u - unidad ?tu - tipoUnidad  ?x - casilla ?e - edificio ?t - tipoEdificio)
        :precondition
            (and
                (esTipo_u ?u ?tu)
                (forall (?r - recurso)
                    (or
                        (not (recurso_unidad ?r ?tu))
                        (disponible ?r)
                    )
                )
                (en_ed ?e ?x)
                (esTipo_e ?e ?t)
                (lugar_reclutamiento ?tu ?t)
                ; No puede existir ya la unidad
                (not (exists (?x_aux - casilla) 
                    (en_un ?u ?x_aux)
                ))
            )
        :effect
            (and
                (en_un ?u ?x)
                (libre ?u)
            )
    )
    
)
(define (domain ejer6)
    (:requirements :strips :typing :adl :fluents)
    (:types
        unidad edificio recurso casilla investigacion - object
        tipoUnidad tipoEdificio - object
    )

    (:constants 
        VCE Marine Segador - tipoUnidad
        CentroDeMando Barracones Extractor BahiaDeIngenieria Deposito - tipoEdificio
        Minerales Gas - recurso
        ImpulsorSegador - investigacion
        T1_1 T1_2 T1_3 T1_4 T1_5 T2_1 T2_2 T2_3 T2_4 T2_5 T3_1 T3_2 T3_3 T3_4 T3_5 T4_1 T4_2 T4_3 T4_4 T4_5 T5_1 T5_2 T5_3 T5_4 T5_5 - casilla
    )

    (:functions
        (cantidad ?r - recurso)
        (limite ?r - recurso)
        (coste_e ?r - recurso ?i - tipoEdificio)
        (coste_u ?r - recurso ?u - tipoUnidad)
        (coste_i ?r - recurso ?u - investigacion)
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

        ; Donde se recluta cada unidad
        (lugar_reclutamiento ?u - tipoUnidad ?e - tipoEdificio)

        ; No hay ningun edificio en una casilla
        (vacia ?x - casilla)

        ; Comparar recursos
        (Gas ?r - recurso)
        ; (Minerales ?r - recurso)

        ; Comparar tipos de unidades
        (Segador ?tu)

        ; Cosa que están investigadas
        (investigado ?i - investigacion)
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

    (:action asignarNodoRecursos
        :parameters (?u - unidad ?r - recurso ?x - casilla)
        :precondition 
            (and 
                (en_un ?u ?x)
                (libre ?u)
                (nodo_recurso ?r ?x)
                ; Si es un nodo de gas debe de haber un extractor
                (or
                    (not (Gas ?r))
                    (exists (?e - edificio)
                        (and
                            (en_ed ?e ?x)
                            (esTipo_e ?e Extractor)
                        )
                    )
                )
            )
        :effect 
            (and 
                (not (libre ?u))
                (extrayendo ?u ?r)
            )
    )
    
    (:action recolectarRecursos
        :parameters (?u - unidad ?r - recurso)
        :precondition
            (and
                (extrayendo ?u ?r)
                ; No puede sobrepasar el límite
                (<
                    (cantidad ?r)
                    (limite ?r)
                )                
            )
        :effect
            (and
                ; Incrementar gas o minerales
                (increase (cantidad ?r) 10)
                (when (and (> (cantidad ?r) (limite ?r)))
                    (and
                        (assign (cantidad ?r) (limite ?r))
                    )
                )
            )
    )

    (:action desasignar
        :parameters (?u - unidad ?r - recurso)
        :precondition
            (and
                (not (libre ?u))
                (extrayendo ?u ?r)
            )
        :effect
            (and
                (libre ?u)
                (not (extrayendo ?u ?r))
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
                    (and
                        (>=
                            (cantidad ?r)
                            (coste_e ?r ?t)
                        )
                    )
                )
                ; Compruebo que no se construya un extractor 
                ; cuando no es un nodo de Gas y compruebo que si
                ; es un nodo de gas no se pueda construir otra cosa
                (or
                    (and
                        (esTipo_e ?e Extractor)
                        (nodo_recurso Gas ?x)
                    )
                    (and
                        (not (esTipo_e ?e Extractor))
                        (not (nodo_recurso Gas ?x))
                    )
                )
                (vacia ?x)
                ; No construye dos veces el mismo edificio
                (not (exists (?x_aux - casilla) 
                    (en_ed ?e ?x_aux) 
                ))
            )
        :effect
            (and
                (en_ed ?e ?x)
                (not (vacia ?x))
                (forall (?r - recurso)
                    (and
                        (decrease (cantidad ?r) (coste_e ?r ?t))
                    )
                )
                (when (and (esTipo_e ?e Deposito))
                    (and
                        (increase (limite Gas) 100)
                        (increase (limite Minerales) 100)
                    )
                )
            )
    )

    (:action reclutar
        :parameters (?u - unidad ?tu - tipoUnidad  ?x - casilla ?e - edificio ?t - tipoEdificio)
        :precondition
            (and
                (esTipo_u ?u ?tu)
                (forall (?r - recurso)
                    (and
                        (>=
                            (cantidad ?r)
                            (coste_u ?r ?tu)
                        )
                    )
                )
                ; Compruebo si se va a reclutar un segador si está investigado
                (or
                    (not (Segador ?tu))
                    (investigado ImpulsorSegador)
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
                (forall (?r - recurso)
                    (and
                        (decrease (cantidad ?r) (coste_u ?r ?tu))
                    )
                )
            )
    )

    (:action investigar
        :parameters ( ?i - investigacion ?x - casilla)
        :precondition
            (and
                ; No puede existir ya la investigacion
                (not (investigado ?i))
                (exists (?e - edificio ) 
                    (and
                        (en_ed ?e ?x)
                        (esTipo_e ?e BahiaDeIngenieria)
                    )
                )
                ; Compruebo que se tienen todos los recursos
                (forall (?r - recurso)
                    (and
                        (>=
                            (cantidad ?r)
                            (coste_i ?r ?i)
                        )
                    )
                )
            )
        :effect
            (and
                (investigado ?i)
                (forall (?r - recurso)
                    (and
                        (decrease (cantidad ?r) (coste_i ?r ?i))
                    )
                )
            )
    )
    
)
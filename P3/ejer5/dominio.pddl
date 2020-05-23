(define (domain ejer5)
    (:requirements :strips :typing :adl :fluents)
    (:types
        unidad edificio recurso casilla investigacion - object
        tipoUnidad tipoEdificio - object
    )

    (:constants 
        VCE Marine Segador - tipoUnidad
        CentroDeMando Barracones Extractor BahiaDeIngenieria - tipoEdificio
        Minerales Gas - recurso
        ImpulsorSegador - investigacion
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
        ; Recurso para una investigación
        (recurso_investigacion ?r - recurso ?i - investigacion)
        ; Donde se recluta cada unidad
        (lugar_reclutamiento ?u - tipoUnidad ?e - tipoEdificio)

        ; No hay ningun edificio en una casilla
        (vacia ?x - casilla)

        ; Comparar recursos
        (Gas ?r - recurso)

        ; Comparar tipos de unidades
        (Segador ?tu)

        ; Cosa que están investigadas
        (investigado ?i - investigacion)
    )
    
    (:action navegar
        :parameters (?u - unidad ?x1 - casilla ?x2 - casilla)
        :precondition
            (and
                ; Posición inicial de la unidad
                (en_un ?u ?x1)
                ; La posición inicial está conectada con la de destino
                (conectado ?x1 ?x2)
                ; La unidad debe estar libre
                (libre ?u)
            )
        :effect
            (and
                ; Se elimina la posición antigua
                (not (en_un ?u ?x1))
                ; Se añade la posición nueva
                (en_un ?u ?x2)
            )
    )

    (:action asignar
        :parameters (?u - unidad ?r - recurso ?x - casilla)
        :precondition
            (and
                ; Está en la misma casilla que el nodo de recurso
                (en_un ?u ?x)
                (nodo_recurso ?r ?x)
                ; Está libre la unidad
                (libre ?u)
                ; Compruebo que si es gas exista un extractor en el nodo
                (or
                    (not (Gas ?r)) ; Si se cumple el recurso necesario no es Gas
                    (exists (?e - edificio) ; Si el recurso es Gas tiene que haber un extractor en la posición
                        (and
                            (en_ed ?e ?x)
                            (esTipo_e ?e Extractor)
                        )
                    )
                )
                
            )
        :effect
            (and
                ; La unidad deja de estar libre
                (not (libre ?u))
                ; Pasa a estar extrayendo el recurso
                (extrayendo ?u ?r)
                ; Como está extrayendo el recurso este se puede usar
                (disponible ?r)
            )
    )

    (:action construir
        :parameters (?u - unidad ?e - edificio ?t - tipoEdificio ?x - casilla)
        :precondition
            (and
                ; Está en la posición que se va a construir el edificio
                (en_un ?u ?x)
                ; Está libre la unidad
                (libre ?u)
                ; Se esta extrayendo el recurso necesario para el tipo de edificio concreto
                (esTipo_e ?e ?t)
                ; Comprueba para todos los recursos cuales son los necesarios y si están disponibles
                (forall (?r - recurso)
                    (or
                        (not (recurso_edificio ?r ?t))
                        (disponible ?r)
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
                    (not (esTipo_e ?e Extractor))
                )
                ; No hay un edificio construido en esa posición.
                (vacia ?x)
                ; No construye dos veces el mismo edificio
                (not (exists (?x_aux - casilla) 
                    (en_ed ?e ?x_aux) 
                ))
            )
        :effect
            (and
                ; Se crea el edificio en la posición
                (en_ed ?e ?x)
                ; Pasa a no estar vacia la posición
                (not (vacia ?x))
            )
    )

    (:action reclutar
        :parameters (?u - unidad ?tu - tipoUnidad  ?x - casilla)
        :precondition
            (and
                ; Compara que tipo de unidad es
                (esTipo_u ?u ?tu)
                ; Comprueba para todos los recursos cuales son los necesarios y si están disponibles
                (forall (?r - recurso)
                    (or
                        (not (recurso_unidad ?r ?tu))
                        (disponible ?r)
                    )
                )
                ; Comprueba que la casilla donde la unidad sea creada haya un edificio del tipo 
                ; que la unidad necesita
                (exists (?e - edificio ?t - tipoEdificio)
                    (and
                        (en_ed ?e ?x)
                        (esTipo_e ?e ?t)
                        (lugar_reclutamiento ?tu ?t)
                    )
                )
                ; Compruebo que si se va a reclutar un segador ImpulsorSegador está investigado
                (or
                    (not (Segador ?tu))
                    (investigado ImpulsorSegador)
                )
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

    (:action investigar
        :parameters ( ?i - investigacion)
        :precondition
            (and
                ; Existe la bahía de ingeniería
                (exists (?e - edificio ?x - casilla) 
                    (and
                        (en_ed ?e ?x)
                        (esTipo_e ?e BahiaDeIngenieria)
                    )
                )
                ; Compruebo que se tienen todos los recursos necesarios
                (forall (?r - recurso)
                    (or
                        (not (recurso_investigacion ?r ?i))
                        (disponible ?r)
                    )
                )
                ; Compruebo que no se ha investigado ya
                (not (investigado ?i))
            )
        :effect
            (and
                (investigado ?i)
            )
    )
    
)
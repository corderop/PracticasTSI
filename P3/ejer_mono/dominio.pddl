(define (domain mono)
    (:requirements :strips :typing)
    (:types
        movible localizacion - object
        mono caja - movible
    )

    (:predicates
        (en ?obj - movible ?x - localizacion)
        (tienePlatano ?m - mono)
        (sobre ?m - mono ?c - caja)
        (platanoEn ?x - localizacion)
    )
    
    (:action cogerPlatanos
        :parameters (?m - mono ?c - caja ?x - localizacion)
        :precondition
            (and
                (platanoEn ?x)
                (en ?c ?x)
                (sobre ?m ?c)
            )
        :effect
            (and
                (tienePlatano ?m)
            )
    )

    (:action empujarCaja
        :parameters (?m - mono ?c - caja ?i - localizacion ?d - localizacion)
        :precondition
            (and
                (or
                    (en ?c ?i)
                )
                (sobre ?m ?c)
            )
        :effect
            (and
                (not (en ?c ?i))
                (en ?c ?d)
            )
    )

    (:action subirEnCaja
        :parameters (?m - mono ?c - caja ?x - localizacion)
        :precondition
            (and
                (en ?c ?x)
                (en ?m ?x)
            )
        :effect
            (and
                (sobre ?m ?c)
            )
    )

    (:action moverMono
        :parameters (?m - mono ?x1 - localizacion ?x2 - localizacion)
        :precondition
            (and
                (en ?m ?x1)
            )
        :effect
            (and
                (not (en ?m ?x1))
                (en ?m ?x2)
            )
    )
    
)
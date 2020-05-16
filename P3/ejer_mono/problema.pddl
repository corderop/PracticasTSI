(define (problem monosp1)
    (:domain mono)
    (:objects
        mono1 - mono
        caja1 - caja
        localizacion1 localizacion2 localizacion3 - localizacion
    )
    (:init
        (en caja1 localizacion3)
        (platanoEn localizacion2)
        (en mono1 localizacion1)
    )
    (:goal
        (and
            (tienePlatano mono1)
        )
    )
)
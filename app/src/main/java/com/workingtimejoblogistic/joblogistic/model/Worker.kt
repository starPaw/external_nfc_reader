package com.workingtimejoblogistic.joblogistic.model

import java.math.BigInteger


data class Worker (
val id :Int,
val created_at :BigInteger,
val Name :String,
val card_id :Int
)

data class WorkerTime (
val id :Int,
val created_at :Int,
val start :Int,
val stop :Int,
val status :String
)
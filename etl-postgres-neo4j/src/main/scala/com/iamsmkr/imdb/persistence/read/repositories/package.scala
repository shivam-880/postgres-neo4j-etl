package com.iamsmkr.imdb.persistence.read

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

package object repositories {

  object ColumnTypeImplicits {

    import slick.jdbc.PostgresProfile.api._

    implicit val listColumnType: JdbcType[List[String]] with BaseTypedType[List[String]] =
      MappedColumnType.base[List[String], String]({ r => r.mkString(",") }, { s => s.split(",").toList })

  }

}

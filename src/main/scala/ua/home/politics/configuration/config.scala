package ua.home.politics.configuration

import scala.collection.mutable

/**
  * Created by Maksym on 6/26/2016.
  */
private object ConfigurationStore {
  val entries = mutable.Map[String, AnyRef]()

  def put(key: String, value: AnyRef) {
    entries += ((key, value))
  }

  def get[A] =
    entries.values.find(_.isInstanceOf[A]).asInstanceOf[Option[A]]

}

trait Configurator {
  def configure[R <: AnyRef](f: => R) = {
    val obj = f
    ConfigurationStore.put(obj.getClass.getName, obj)
    obj
  }
}

trait ConfiguredContext {
  def getConfigured[A] : A = {
    ConfigurationStore.get[A].get
  }
}
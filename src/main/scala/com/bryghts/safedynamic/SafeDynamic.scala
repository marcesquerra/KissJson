package com.bryghts.safedynamic

import scala.language.dynamics

trait SafeDynamic[T]
{self =>

	class DynamicWrapper(in: SafeDynamic[T]) extends Dynamic
	{
		def selectDynamic(name: String): T =
			self.selectDynamic(name)

	}

	def selectDynamic(name: String): T

	def ? : DynamicWrapper = new DynamicWrapper(self)

}

trait HasApply[I, O] {
	def apply(i: I): O
}

trait ApplyAfterSafeDynamic[I, O, T <: HasApply[I, O]] extends SafeDynamic[T]
{self =>

	class ExtendedDynamicWrapper(in: ApplyAfterSafeDynamic[I, O, T] with SafeDynamic[T]) extends DynamicWrapper(in)
	{

		def applyDynamic(name: String)(i: I): O = selectDynamic(name).apply(i)

	}

	override def ? : ExtendedDynamicWrapper = new ExtendedDynamicWrapper(self)

}




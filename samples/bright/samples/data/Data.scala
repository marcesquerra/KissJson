package bright.samples
package data

import utils._


case class EMail(
		login:        String,
		domain:       String)                                                   extends ImprovedToString[EMail]

case class Person(
		name:         String,
		middlename:   Option[String],
		surname:      String,
		age:          Int,
		eMails:       Array[EMail])                                             extends ImprovedToString[Person]

case class Employee(
		self:         Person,
		ocupation:    String,
		subordinates: Option[List[Employee]] = None)                            extends ImprovedToString[Employee]

case class Company(
		name:         String,
		emails:       Array[EMail],
		staff:        List[Employee])                                           extends ImprovedToString[Company]


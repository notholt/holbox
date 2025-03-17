/**
 * This package contains all classes and methods specific to the hybrid system project development
 * such as settings, limits, operation envelope, etc.
 */

// This is to prevent the project XML file to repeat namespaces for every and each of the values given
@javax.xml.bind.annotation.XmlSchema (
      xmlns = { 
        @javax.xml.bind.annotation.XmlNs(prefix = "xsi", 
                   namespaceURI="http://www.w3.org/2001/XMLSchema-instance"),
        @javax.xml.bind.annotation.XmlNs(prefix="xs",
                   namespaceURI="http://www.w3.org/2001/XMLSchema")}
      )

package de.hsrt.holbox.project;

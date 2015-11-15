This article contains the planning for the Gentics Content Connector Version 2. It describes how the new version should look like and how it will be restructured.


# Core Elements #

## Configuration ##

## Datasources ##

FKA RequestProcessor
Note: Extract the doNavigation option into a standard abstract class that can be used by all request processors
with the option to overwrite the doNavigation function

### Readable ###

### Writeable ###

## Filters ##

FKA Transformer

## Renderers ##

FKA ContentRepository

## Scheduler ##

### Dynamic Update Checker ###

## Utils ##

## Servlets ##

## Portlets ##


# General restructuring #

  * all calls to finalize() should be removed / changed to destroy()/shutdown()/...
  * rendering of content should be pulled to a single position
  * Abstraction containers (RESTSimpleContainer, RESTBinaryContainer,...) should be redone.
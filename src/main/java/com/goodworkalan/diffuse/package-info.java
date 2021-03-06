/**
 * Creates a copy of an object graph with objects converted into maps of the object
 * properties and consisting solely of maps, lists and scalars where a scalar is a
 * primitive or string. Maps and lists are copied, arrays are converted into lists,
 * primitives objects and strings are left as is, and any other object that lacks a
 * custom conversion strategy is converted into a map by copying object's fields
 * and bean properties into a map. A diffused object graph is diffused because it
 * removes type informaiton and converts data into primitive, but univeral types.
 * <p>
 * This format is useful when you wish to serialize data in a format that can be
 * universally read or written. This is often the case when you have utilities that
 * are only interested in the objects data and not in the objects behavior. It is
 * often the natural order of things in languages like Javascript or Perl where
 * objects are really maps with methods.
 * <p>
 * For example, imagine you want to create a tree view of an object graph for
 * debugging purposes. The tree view would let you explore the contents of an
 * object graph, expanding object members as branches, and inspecting those
 * members, until you reach an atomic type such as a integer. Yes, Integer does
 * have properties in Java, but lets say that we're content to ignore those.
 * <p>
 * We could build the introspection into the tree viewer. Then the tree viewer
 * would have to interact with a special object model that would wrap an object and
 * present its fields and bean properties as name value pairs to be displayed in
 * our tree view.
 * <p>
 * Alternatively, we can use this library to diffuse the object, then have the tree
 * viewer display the contents of a tree that contains only two different container
 * types, maps and lists.
 * <p>
 * This is how diffuse is used in Notice. Notice is a logging API that creates
 * structured logging messages, that capture object graphs during operation. It
 * takes a snapshot of an object graph that can then be recorded by any Java
 * serialization tool that can understand Java maps and lists. Not need to wade
 * through a dozen different bean mapping strategis, simply convert to a diffused
 * object graph and the contents can be written in YAML, JSON or sent in a nicely
 * formatted email via SMTP.
 */
package com.goodworkalan.diffuse;


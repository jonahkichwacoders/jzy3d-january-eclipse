# jzy3d-january-eclipse
The JZY3D graphical library (https://github.com/jzy3d) and the latest JOGL/Gluegen libraries have been bundled in OSGi-ready plugins and fragments. A test project (org.dawnsci.jzy3d.to.january) is available to preview a diffraction image loaded using Eclipse January (https://github.com/eclipse/january) as a surface in a JZY3d chart shown inside of an Eclipse 4 part component.

The target platform needed to successfully run this project includes the following dependencies:
* Running Eclipse 4 platform plugins
* Eclipse January (https://github.com/eclipse/january)
* Apache Log4j (https://mvnrepository.com/artifact/log4j/log4j/1.2.17)
* Unit Of Measurement API (https://mvnrepository.com/artifact/javax.measure/unit-api/1.0)
* Apache Commons Maths3 (https://mvnrepository.com/artifact/org.apache.commons/commons-math3/3.6.1)

Once the TP set, this repository and the Eclipse January one can be pulled and the project should compile and run.

The steps followed to create the OSGi ready bundles were found here: https://wadeawalker.wordpress.com/2010/10/09/tutorial-a-cross-platform-workbench-program-using-java-opengl-and-eclipse/

The code used to build a surface given a double array data comes from : https://stackoverflow.com/questions/8338215/build-a-3d-surface-plot-using-xyz-coordinates-with-jzy3d

The [diffpattern.jpg](https://github.com/belkassaby/jzy3d-january-eclipse/blob/master/org.dawnsci.january.to.jzy3d/data/diffpattern.jpg) image is the "diffraction pattern of a circular aperture which disburses the light from a point source of light, such as a star, into a bull's-eye shaped image which is known as an Airy disk". (http://www.goldastro.com/goldfocus/resolving_power.php)

The [diffpattern2.jpg](https://github.com/belkassaby/jzy3d-january-eclipse/blob/master/org.dawnsci.january.to.jzy3d/data/diffpattern2.jpg) image  is a Selected Area Diffraction (SAD) pattern found on https://sites.google.com/site/selectedareadiffraction/

The native libraries (JOGL) and the JZY3d jars have their own licensing as specified in each project. The projects created in this repository are under the Eclipse License Version 1.0.


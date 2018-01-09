# jzy3d-january-eclipse
The JZY3D graphical library (https://github.com/jzy3d) and the latest released JOGL/Gluegen libraries have been bundled in OSGi-ready plugins and fragments using the Eclipse EBR project. A test project (org.dawnsci.jzy3d.to.january) is available to preview a diffraction image loaded using Eclipse January (https://github.com/eclipse/january) as a surface in a JZY3d chart shown inside of an Eclipse 4 part component.

The target platform needed to successfully run this project are in the example.tpd. *at time of writing bintray is not working, so download the EBR zip from https://www.dropbox.com/sh/zlyswbf6odkv3ao/AAC2bmy7gkapUYkZuv6jkQ6ta?dl=0*

Once the TP set the project should compile and run.

The code used to build a surface given a double array data comes from : https://stackoverflow.com/questions/8338215/build-a-3d-surface-plot-using-xyz-coordinates-with-jzy3d

The [diffpattern.jpg](https://github.com/belkassaby/jzy3d-january-eclipse/blob/master/org.dawnsci.january.to.jzy3d/data/diffpattern.jpg) image is the "diffraction pattern of a circular aperture which disburses the light from a point source of light, such as a star, into a bull's-eye shaped image which is known as an Airy disk". (http://www.goldastro.com/goldfocus/resolving_power.php)

The [diffpattern2.jpg](https://github.com/belkassaby/jzy3d-january-eclipse/blob/master/org.dawnsci.january.to.jzy3d/data/diffpattern2.jpg) image  is a Selected Area Diffraction (SAD) pattern found on https://sites.google.com/site/selectedareadiffraction/

The native libraries (JOGL) and the JZY3d jars have their own licensing as specified in each project. The projects created in this repository are under the Eclipse License Version 1.0.


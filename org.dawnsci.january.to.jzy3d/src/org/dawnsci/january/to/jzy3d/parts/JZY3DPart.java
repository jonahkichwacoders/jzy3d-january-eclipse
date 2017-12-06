/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package org.dawnsci.january.to.jzy3d.parts;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dawnsci.january.to.jzy3d.Activator;
import org.dawnsci.january.to.jzy3d.io.LazyLoadingJanuary;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.bridge.swt.Bridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRBG;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class JZY3DPart {

	private Composite parent;

	private String filename;

	@PostConstruct
	public void createComposite(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));

//		if (filename == null) {
//			Shell shell = Display.getDefault().getActiveShell();
//			FileDialog dialog = new FileDialog(shell);
//			filename = dialog.open();
//		}
		String bundleLocation = Activator.getContext().getBundle().getLocation();
		bundleLocation = bundleLocation.split("initial@reference:file:")[1];
		filename = bundleLocation + "data/diffpattern2.jpg";
		Shape surface = getSurfaceFromImage(filename);
		drawSurface(surface, parent);
	}

	/*
	 * @return surface given a file image
	 */
	private Shape getSurfaceFromImage(String fileName) {
		// get the lazy data from the image file
		ILazyDataset lazyData = LazyLoadingJanuary.createFromFile(new File(fileName));
		int[] shape = lazyData.getShape();
		SliceND ndSlice = new SliceND(shape, new int[] {0, 0}, new int[] {shape[0], shape[1]}, null);
		IDataset data = null;
		try {
			data = lazyData.getSlice(null, ndSlice);
		} catch (DatasetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// populate the data array
		double[][] distDataProp = new double[shape[0]][shape[1]];
		for (int i = 0; i < shape[0]; i++) {
			for (int j = 0; j < shape[1]; j++) {
				distDataProp[i][j] = data.getDouble(i, j);
			}
		}
		// create the list of polygons: found on
		// https://stackoverflow.com/questions/8338215/build-a-3d-surface-plot-using-xyz-coordinates-with-jzy3d
		List<Polygon> polygons = new ArrayList<Polygon>();
		for (int i = 0; i < distDataProp.length - 1; i++) {
			for (int j = 0; j < distDataProp[i].length - 1; j++) {
				Polygon polygon = new Polygon();
				polygon.add(new Point(new Coord3d(i, j, distDataProp[i][j])));
				polygon.add(new Point(new Coord3d(i, j + 1, distDataProp[i][j + 1])));
				polygon.add(new Point(new Coord3d(i + 1, j + 1, distDataProp[i + 1][j + 1])));
				polygon.add(new Point(new Coord3d(i + 1, j, distDataProp[i + 1][j])));
				polygons.add(polygon);
			}
		}
		// create a shape with the polygons
		return new Shape(polygons);
	}

	/**
	 * Draw a surface on a JZy3d chart inside of a SWT Composite
	 *
	 * @param surface
	 * @param parent
	 */
	private void drawSurface(Shape surface, Composite parent) {
		surface.setColorMapper(new ColorMapper(new ColorMapRBG(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);

		// Create a chart
		Chart chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
		chart.getScene().getGraph().add(surface);

		Settings.getInstance().setHardwareAccelerated(true);

		parent.setLayout(new FillLayout());
		chart.addKeyboardCameraController();
		chart.addKeyboardScreenshotController();
		chart.addMouseCameraController();
		Bridge.adapt(parent, (Component) chart.getCanvas());
	}

	@Focus
	public void setFocus() {
		parent.setFocus();
	}

}
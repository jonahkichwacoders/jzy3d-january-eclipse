/*******************************************************************************
 * Copyright (c) 2017 Baha El-Kassaby and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.dawnsci.january.to.jzy3d.parts;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dawnsci.january.to.jzy3d.Activator;
import org.dawnsci.january.to.jzy3d.io.LazyLoadingJanuary;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.bridge.swt.Bridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.swt.SWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRBG;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class JZY3DPart {
	/**
	 * Quality of rendering. See {@link Quality} for options.
	 */
	private static final Quality QUALITY = Quality.Intermediate;

	/**
	 * Turn on or off Hardware Acceleration
	 */
	private static final boolean HARDWARE_ACCELERATED = true;

	/**
	 * Use a diffraction pattern provided by a jpg in this bundle, or if false, use
	 * a Sin wave.
	 */
	private static final boolean USE_DIFFPATTERN = false;

	/**
	 * Use a SWTChartComponentFactory vs AWTChartComponentFactory as the drawing
	 * canvas.
	 */
	private static final boolean USE_SWT = true;

	private Composite parent;

	@PostConstruct
	public void createComposite(Composite parent) {
		this.parent = parent;
		parent.setLayout(new FillLayout());

		Shape surface;
		if (USE_DIFFPATTERN) {
			surface = createSurfaceDiffpattern();
		} else {
			surface = createSurfaceSin();
		}

		if (USE_SWT) {
			drawSurfaceSWTChartComponentFactory(surface, parent);
		} else {
			drawSurfaceAWTChartComponentFactory(surface, parent);
		}
	}

	private Shape createSurfaceDiffpattern() {
		Shape surface = null;
		URL url = FileLocator.find(Activator.getContext().getBundle(), new Path("data/diffpattern2.jpg"), null);
		try {
			url = FileLocator.toFileURL(url);
			String filename = URIUtil.toFile(URIUtil.toURI(url)).toString();
			surface = getSurfaceFromImage(filename);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return surface;
	}

	private Shape createSurfaceSin() {
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};

		// Define range and precision for the function to plot
		Range range = new Range(-3, 3);
		int steps = 80;

		// Create the object to represent the function over the given range.
		final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		return surface;
	}

	/*
	 * @return surface given a file image
	 */
	private Shape getSurfaceFromImage(String fileName) {
		// get the lazy data from the image file
		ILazyDataset lazyData = LazyLoadingJanuary.createFromFile(new File(fileName));
		int[] shape = lazyData.getShape();
		SliceND ndSlice = new SliceND(shape, new int[] { 0, 0 }, new int[] { shape[0], shape[1] }, null);
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
		Shape surface = new Shape(polygons);
		surface.setColorMapper(new ColorMapper(new ColorMapRBG(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		return surface;
	}

	private void drawSurfaceSWTChartComponentFactory(Shape surface, Composite parent) {
		Chart chart = SWTChartComponentFactory.chart(parent, QUALITY);
		chart.getScene().getGraph().add(surface);
		Settings.getInstance().setHardwareAccelerated(HARDWARE_ACCELERATED);
		ChartLauncher.openChart(chart);
	}

	private void drawSurfaceAWTChartComponentFactory(Shape surface, Composite parent) {
		Chart chart = AWTChartComponentFactory.chart(QUALITY, "awt");
		chart.getScene().getGraph().add(surface);
		Settings.getInstance().setHardwareAccelerated(HARDWARE_ACCELERATED);
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
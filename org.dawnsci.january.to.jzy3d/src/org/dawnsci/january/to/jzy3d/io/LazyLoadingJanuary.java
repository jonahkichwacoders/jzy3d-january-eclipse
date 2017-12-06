/*******************************************************************************
 * Copyright (c) 2017 Baha El-Kassaby and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.dawnsci.january.to.jzy3d.io;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.january.IMonitor;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.LazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.january.io.ILazyLoader;

public class LazyLoadingJanuary {

	/**
	 * Taken from example in January IO LazyLoadingExample
	 *
	 * @param file
	 * @return lazy dataset
	 */
	public static ILazyDataset createFromFile(final File file) {
		if (!file.canRead()) {
			throw new IllegalArgumentException("File '" + file + "' does not exist or is not readable");
		}
		try {
			BufferedImage image = ImageIO.read(file);

			Raster ras = image.getData();
			final int height = ras.getHeight();
			final int width = ras.getWidth();
			final int[] shape = new int[] {height, width};

			ILazyLoader loader = new ILazyLoader() {
				private static final long serialVersionUID = 8802943590647491871L;

				@Override
				public boolean isFileReadable() {
					return file.canRead();
				}

				@Override
				public IDataset getDataset(IMonitor mon, SliceND slice) throws IOException {
					BufferedImage image = ImageIO.read(file);

					Raster ras = image.getData();
					Dataset tmp = DatasetFactory.createFromObject(
							ras.getSamples(0, 0, width, height, 0, (int[]) null), height, width);
					return tmp.getSliceView(slice);
				}
			};
			LazyDataset lazy = new LazyDataset(file.getName(), Dataset.INT32, shape, loader);
			return lazy;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

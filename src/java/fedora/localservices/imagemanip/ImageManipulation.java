/**
 *	All image manipulations are handled by ImageJ, a Java API written for image
 *	processing:
 *
 *  Rasband, W.S., ImageJ, National Institutes of Health, Bethesda,
 *  Maryland, USA, http://rsb.info.nih.gov/ij/, 1997-2003.
 *
 *
 *
 *	The GifEncoder portion of ImageJ is copyrighted below:
 *
 *  Transparency handling and variable bit size courtesy of Jack Palevich.
 *
 *  Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 *  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *
 *  Visit the ACME Labs Java page for up-to-date versions of this and other
 *  fine Java utilities: http://www.acme.com/java/
 */
 
package fedora.localservices.imagemanip;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import ij.*;
import ij.io.*;
import ij.process.*;
import java.awt.*;


/**
 *	ImageManipulation is a Java servlet that takes a URL of an image as a param
 *	and based on other given parameters, can perform a variety of image related
 *	manipulations on the object. After the image is manipulated, it is then
 *	sent back as an image/type object to the calling parent, most often a
 *	browser or an HTML img tag.
 *
 *	@author Theodore Serbinski, tss24@cornell.edu
 *	@version 1.0
 *	@created April 2003
 */
public class ImageManipulation extends HttpServlet {

	// set extensions that are supported for writing back images, see outputImg()
	private String ext= "gif|jpg|jpeg";

	// set extensions that are supported for opening and converting to above formats
	private String convertFromExt= "gif|jpg|jpeg|tif|tiff";

	private int jpgQuality= 90;
	private String imgName= "";
	private String imgExt= "";


	/**
	 *	Method automatically called by browser to handle image manipulations.
	 *
	 *	@param	req	Browser request to servlet
	 *					res Response sent back to browser after image manipulation
	 *	@throws	IOException	If an input or output exception occurred
	 *					ServletException	If a servlet exception occurred
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		// collect all possible parameters for servlet
		String url= req.getParameter("url");
		String op= req.getParameter("op");
		String newWidth= req.getParameter("newWidth");
		String brightAmt= req.getParameter("brightAmt");
		String zoomAmt= req.getParameter("zoomAmt");
		String wmText= req.getParameter("wmText");
		String cropX= req.getParameter("cropX");
		String cropY= req.getParameter("cropY");
		String cropWidth= req.getParameter("cropWidth");
		String cropHeight= req.getParameter("cropHeight");
		String convertTo= req.getParameter("convertTo");

		// if there is a valid URL specifying an image with a valid extension
		if (checkImg(url,convertTo)) {

			// tell browser what type of image is being sent back
			res.setContentType("image/" + imgExt.toLowerCase());

			ServletOutputStream out= res.getOutputStream();
			BufferedOutputStream bout= new BufferedOutputStream(out);

			Opener o= new Opener();

			// some URLs may timeout, so see following web site for possible fixes:
			// http://forum.java.sun.com/thread.jsp?thread=327305&forum=4&message=1332329
			ImagePlus imp= o.openURL(url);

			// if the image was able to be opened
			if (imp != null) {
				ImageProcessor ip= imp.getProcessor();

				// causes scale() and resize() to do bilinear interpolation
				ip.setInterpolate(true);

				// if an operation was specified in the URL, call its respective function
				if (op != null) {
					if (op.equals("resize")) ip= resize(ip,newWidth);
					else if (op.equals("zoom")) ip= zoom(ip, zoomAmt);
					else if (op.equals("brightness")) ip= brightness(ip, brightAmt);
					else if (op.equals("watermark")) ip= watermark(ip,wmText);
					else if (op.equals("grayscale")) ip= grayscale(ip);
					else if (op.equals("crop")) ip= crop(ip,cropX,cropY,cropWidth,cropHeight);
				}

				// otherwise if we are converting an image to a GIF, we need to
				// reduce the colors in the original image to 256 to fit the GIF format
				else if (imgExt.equals("gif") && convertTo != null) {
					ip= ip.convertToRGB();
					ip= reduceColors(ip,256);
				}

				outputImg(ip,bout);
			}

			bout.close();
			out.close();
		}
	}


	/**
	 *	Verifies that a given URL and convertTo format are valid, with valid
	 *	extensions matching the ones defined at the top of the class.
	 *
	 *	@param	url The URL to validate
	 *					convertTo The extension to convert the image to
	 *	@return Boolean depending on if the URL and convertTo validate
	 */
	private boolean checkImg(String url, String convertTo) {
		boolean img= false;

		if (url != null) {

			// see if there is an image name given
			int nIndex= url.lastIndexOf("/");
			if (nIndex > 0)
				imgName= url.substring(nIndex+1);

			if (!imgName.equals("")) {

				// get extension to image name
				int eIndex= imgName.lastIndexOf(".");
				if (eIndex > 0) {
					imgExt= imgName.substring(eIndex+1);

					if (convertTo != null) {
						// if the extension is valid and the convertTo format is valid
						if (ext.indexOf(convertTo.toLowerCase()) > -1 && convertFromExt.indexOf(imgExt.toLowerCase()) > -1) {
							img= true;
							imgExt= convertTo;
						}
					}

					// if the extension is valid
					if (ext.indexOf(imgExt.toLowerCase()) > -1)
					  img= true;
				}
			}
		}

		return img;
	}


	/**
	 *	Writes out the image in the correct format.
	 *
	 *	@param	ip The image to write out
	 *					bout The buffered stream to write the image to
	 */
	private void outputImg(ImageProcessor ip, BufferedOutputStream bout) throws IOException {
		if (imgExt.equals("jpg") || imgExt.equals("jpeg")) {
			JpegEncoder je= new JpegEncoder(ip.createImage(), jpgQuality, bout);
			je.WriteHeaders(bout);
			je.WriteCompressedData(bout);
			je.WriteEOI(bout);
		}

		else if (imgExt.equals("gif")) {
			ImagePlus imp= new ImagePlus("temp",ip);
			FileInfo fi= imp.getFileInfo();
			byte pixels[]= (byte[])imp.getProcessor().getPixels();
			GifEncoder ge= new GifEncoder(fi.width,fi.height,pixels,fi.reds,fi.greens,fi.blues);
			ge.write(bout);
		}
	}


	/**
	 *	Reduces the amount of colors in a RGB image to a supplied number
	 *
	 *	@param	ip The image to reduce colors in
	 *					numColors Reduce the number of colors in an image to this amount
	 *	@return The image with reduced colors
	 */
	private ImageProcessor reduceColors(ImageProcessor ip, int numColors) {
		MedianCut mc= new MedianCut((int[])ip.getPixels(), ip.getWidth(), ip.getHeight());
		ip= mc.convertToByte(numColors);

		return ip;
	}


	/**
	 *	Resizes an image to the supplied new width in pixels. The height is
	 *	reduced proportionally to the new width.
	 *
	 *	@param	ip The image to resize
	 *				 	newWidth The width in pixels to resize the image to
	 *	@return The image resized
	 */
	private ImageProcessor resize(ImageProcessor ip, String newWidth) {
		if (newWidth != null) {
			try {
				int width= Integer.parseInt(newWidth);

				if (width < 0)
					return ip;

				int imgWidth= ip.getWidth();
				int imgHeight= ip.getHeight();

				// if the image is GIF need to convert it to RGB so colors aren't lost
				if (imgExt.equals("gif"))
					ip= ip.convertToRGB();

				ip= ip.resize(width, width*imgHeight/imgWidth);

				// if the image is GIF need to reduce colors down to 256 to write it out
				if (imgExt.equals("gif"))
					ip= reduceColors(ip, 256);
			}

			// no need to do anything with number format exception since the servlet
			// returns only images; just return the original image
			catch (NumberFormatException e) {}
		}

		return ip;
	}


	/**
	 *	Zooms either in or out of an image by a supplied amount. The zooming
	 *	occurs from the center of the image.
	 *
	 *	@param	ip The image to zoom
	 *					zoomAmt The amount to zoom the image.
	 *									0 < zoomAmt < 1 : zoom out
	 *									1 = zoomAmt     : original image
	 *									1 < zoomAmt			: zoom in
	 *	@return The image zoomed
	 */
	private ImageProcessor zoom(ImageProcessor ip, String zoomAmt) {
		if (zoomAmt != null) {
			try {
				float zoom= Float.parseFloat(zoomAmt);

				if (zoom < 0)
					return ip;

				// if the image is GIF need to convert it to RGB so colors aren't lost
				if (imgExt.equals("gif"))
					ip= ip.convertToRGB();

				ip.scale(zoom,zoom);

				// if the image is being zoomed out, trim the extra whitespace around the image
				if (zoom < 1) {
					int imgWidth= ip.getWidth();
					int imgHeight= ip.getHeight();

					// set a ROI around the image, minus the extra whitespace
					ip.setRoi((int)(Math.round(imgWidth/2-imgWidth*zoom/2)), (int)(Math.round(imgHeight/2-imgHeight*zoom/2)), (int)(Math.round(imgWidth*zoom)), (int)(Math.round(imgHeight*zoom)));
					ip= ip.crop();
				}

				// if the image is GIF need to reduce colors down to 256 to write it out
				if (imgExt.equals("gif"))
					ip= reduceColors(ip, 256);
			}

			// no need to do anything with number format exception since the servlet
			// returns only images; just return the original image
			catch (NumberFormatException e) {}
		}

		return ip;
	}


	/**
	 *	Adjusts the brightness of an image by a supplied amount.
	 *
	 *	@param	ip The image to adjust the brightness of
	 *					brightAmt The amount to adjust the brightness of the image by
	 *									0 <= brightAmt < 1 : darkens image
	 *									1 = brightAmt      : original image
	 *									1 < brightAmt			 : brightens image
	 *	@return The image with brightness levels adjusted
	 */
	private ImageProcessor brightness(ImageProcessor ip, String brightAmt) {
		if (brightAmt != null) {
			try {
				float bright= Float.parseFloat(brightAmt);

				if (bright < 0)
					return ip;

				// if the image is GIF need to convert it to RGB so colors aren't lost
				if (imgExt.equals("gif"))
					ip= ip.convertToRGB();

				ip.multiply(bright);

				// if the image is GIF need to reduce colors down to 256 to write it out
				if (imgExt.equals("gif"))
					ip= reduceColors(ip, 256);
			}

			// no need to do anything with number format exception since the servlet
			// returns only images; just return the original image
			catch (NumberFormatException e) {}
		}

		return ip;
	}


	/**
	 *	Adds a watermark to an image using the supplied text.
	 *
	 *	@param	ip The image to add a watermark to
	 *					watermarkText The text to write on the image
	 *	@return	The watermarked image
	 */
	private ImageProcessor watermark(ImageProcessor ip, String watermarkText) {
		if (watermarkText != null) {
			try {
				// if the image is GIF need to convert it to RGB so colors aren't lost
				if (imgExt.equals("gif"))
					ip= ip.convertToRGB();

				// set the font size to 3% of the image width or a minimum size of 10
				int fontSize= ip.getWidth()*3/100;
				if (fontSize < 10) fontSize= 10;

				ip.setFont(new Font("SansSerif", Font.BOLD, fontSize));

				// place text at bottom, center of image
				int x= ip.getWidth()/2 - ip.getStringWidth(watermarkText)/2;
				int y= ip.getHeight() - ip.getFontMetrics().getHeight();

				// create a rectangle around text that is lighter than the image
				ip.setRoi(x-5, y-ip.getFontMetrics().getHeight()-5, ip.getStringWidth(watermarkText) + 10, ip.getFontMetrics().getHeight() + 10);
				ip.add(85);

				// more colors:
				// http://java.sun.com/j2se/1.4.1/docs/api/java/awt/Color.html
				Color c= new Color(33,33,33);
				ip.setColor(c);
				ip.drawString(watermarkText, x, y);

				// if the image is GIF need to reduce colors down to 256 to write it out
				if (imgExt.equals("gif"))
					ip= reduceColors(ip,256);
			}

			// no need to do anything with number format exception since the servlet
			// returns only images; just return the original image
			catch (NumberFormatException e) {}
		}

		return ip;
	}


	/**
	 *	Converts an image to gray scale.
	 *
	 *	@param	ip The image to convert to grayscale
	 *	@return The image converted to grayscale
	 */
	private ImageProcessor grayscale(ImageProcessor ip) {
		ip= ip.convertToRGB();
		ip= ip.convertToByte(true);

		return ip;
	}


	/**
	 *	Crops an image with supplied starting point and ending point.
	 *
	 *	@param	ip The image to crop
	 *					cropX The starting x position; x=0 corresponds to left side of image
	 *					cropY	The starting y position; y=0 corresponds to top of image
	 *					cropWidth The width of the crop, starting from the above x
	 *					cropHeight The height of the crop, starting from the above y
	 *	@return The image cropped
	 */
	public ImageProcessor crop(ImageProcessor ip, String cropX, String cropY, String cropWidth, String cropHeight) {
		if ((cropX != null) && (cropY !=null)) {
			try {
				int x= Integer.parseInt(cropX);
				int y= Integer.parseInt(cropY);
				int width;
				int height;

				// if value for cropWidth is not given, just use the width of the image
				if (cropWidth != null)
					width= Integer.parseInt(cropWidth);
				else
					width= ip.getWidth();

				// if value for cropHeight is not given, just use the height of the image
				if (cropHeight != null)
					height= Integer.parseInt(cropHeight);
				else
					height= ip.getHeight();

				// if any value is negative, this causes ImageJ to explode, so just return
				if (x < 0 || y < 0 || width < 0 || height < 0)
					return ip;

				ip.setRoi(x,y,width,height);
				ip= ip.crop();
			}

			// no need to do anything with number format exception since the servlet
			// returns only images; just return the original image
			catch (NumberFormatException e) {}
		}

		return ip;
	}
}
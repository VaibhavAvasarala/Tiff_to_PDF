package com.so;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

/*author
 * Shrisowdhaman.S 
 * 25th Oct 2015
 */
public class Tiff2Pdf {
	
	public static final String FILE_SEPRATOR = System.getProperty("file.separator");
	public JProgressBar jb;
	public static String logpath = null;
	public String logmessage = null;
	
	public Tiff2Pdf() {
		
		try{
		 JFileChooser directory = new JFileChooser();
		 directory.setFileSelectionMode(1);
		 
		 int option = directory.showOpenDialog(null);
		 
		 if (option == 0) {
			
		      try {
		    	
		    	String sourceDir = directory.getSelectedFile().toString();
		        
		        //Get the source directory from JFileChooser
		        File fileSrc = new File(sourceDir);
		       
		        //Log Path 
		        logpath = sourceDir + "/log.txt";
		        logwriter("Process Started ");
		        
		        //Extensions to filter tif file
				String[] tif_exts = new String[] { "tif" };
		        
				//Used to list all directory with contain tif image
				List<File> files = (List<File>) FileUtils.listFiles(fileSrc, tif_exts, true);
				
				System.out.println(" File count  : " +files.size());

				//Load the progress bar screen
				final ProgressBarExample it = new ProgressBarExample(0,files.size());
			    JFrame frame = new JFrame("Tiff to PDF Conversion");
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.setSize(200, 200);
			    frame.setContentPane(it);
			    frame.setLocationRelativeTo(null);
			    frame.pack();
			    frame.setVisible(true);
			    //End progress bar screen
			    
				int count = 0;
				 
				//Loop start to fetch all tif to convert PDF
				
				for (File file : files) {
				 
					count +=1;					 
					
					String destFileName = file.getName().substring(0, file.getName().lastIndexOf("."))+".pdf";
					
					String srcFile = file.getCanonicalPath();
					String destFile = file.getParent()+FILE_SEPRATOR+destFileName;
					
					logmessage = count +". Source Path : "+ srcFile +"     Destignation Path : "+destFile;
					logwriter(logmessage);
					
					//Check whether File converted to PDF 
					File pdfFiles = new File(destFile);
					if(!pdfFiles.exists()){
						convertStart(srcFile,destFile);
					}
					
					
					final int percent = count;
				      try {
				        SwingUtilities.invokeLater(new Runnable() {
				        	public void run() {				        	  
				            it.updateBar(percent);
				        	}
				         });
				        java.lang.Thread.sleep(100);
				      } catch (InterruptedException e) {
				        System.out.println(e);
				        logwriter(e.toString());
				      }
					 
				}
				
				logwriter("Process Completed ");
				JOptionPane.showMessageDialog(null, "Completed");
				it.exit();
				
		      } catch (ArrayIndexOutOfBoundsException e) {
		    	  logwriter(e.toString());  
		          JOptionPane.showMessageDialog(null, " Please try again");
		      }
		  } else {
		      JOptionPane.showMessageDialog(null, "User Cancelled the Operation");
		  }
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public boolean convertStart(String sourcefile,String targetfile){			
        
        try {
        	
        	 float width = 1000;
             float height = 1500	;
             
             Rectangle rect = new Rectangle(width,height);
             
	         //PDF conversation starts here 
             Document document = new Document(rect);
	    	    
	    	 FileOutputStream fos = new FileOutputStream(targetfile);
	    	 PdfWriter writer = PdfWriter.getInstance(document, fos);	   
	    	 writer.open();
	    	    
	    	 document.open();
        	 Iterator readers=javax.imageio.ImageIO.getImageReadersBySuffix("tiff");
        	  
        	  if (readers.hasNext()) {
        		  
        		  File srcFile=new File(sourcefile);
        		  
        		  ImageInputStream iis=javax.imageio.ImageIO.createImageInputStream(srcFile);
      		      TIFFDecodeParam param=null;
      		      ImageDecoder dec=ImageCodec.createImageDecoder("tiff",srcFile,param);
    		      int pageCount=dec.getNumPages();
    		      ImageReader _imageReader=(ImageReader)(readers.next());
        		  
    		      if (_imageReader != null) {
    		    	  
    			      _imageReader.setInput(iis,true);
    			      
    			      for (int i=0; i < pageCount; i++) {
	    			    	//Read the source file (tiff)
	    		        	  BufferedImage srcImg = _imageReader.read(i);  
	    		        	  BufferedImage img2 = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), 
	    	        				  BufferedImage.TYPE_INT_RGB);
	    		        	  	    	        	    
	    		        	
	    	        		//Set the RGB values for converted image (jpg)
	    	        	    for(int y = 0; y < srcImg.getHeight(); y++) {
	    	        	      for(int x = 0; x < srcImg.getWidth(); x++) {
	    	        	        img2.setRGB(x, y, srcImg.getRGB(x, y));
	    	        	      }
	    	        	    }
	    	        	  /*  
	    	        	    String s = "C:/Users/shrisowdhaman/Desktop/TIFF/A-04-000001/sample"+i+".jpg";
	    	        	    ImageIO.write(img2, "jpg", new File(s));
	    	        	    */
	    	        	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	        	    ImageIO.write(img2, "jpg", baos);
	    	        	    baos.flush();
	    	
	    	        	    // Convert byteArrayoutputSteam to ByteArray
	    	        	    byte[] imageInByte = baos.toByteArray();
	    	        	    
	    	        	    
	    	        	    document.add(Image.getInstance(imageInByte));
	    	        	   
	    	        	    System.out.println("Page: " + (i + 1)  );
	    	        	    baos.close();
	    	        	    
    			      }//End of for loop
    			    //Close all open methods
  	        	    document.close();
  	        	    writer.close();
  	        	    fos.close();
  	        	   
    		      }else{
    		      
	        		  logwriter("Image is null for file :" + targetfile); 
	        	      System.out.println("image is null for file :" + targetfile);
	        	      return false;
	        	  }
        	  }
        	} catch (Exception e) {
        		System.out.println(e);
        		return false;
        	}
        
          return true;
	}
	
	static void logwriter(String strLog) throws IOException  {
		
	    FileWriter logwrit = new FileWriter(logpath, true);
	    PrintWriter Pwriter = new PrintWriter(logwrit);
	    Pwriter.println(strLog);
	    logwrit.close();
	    Pwriter.close();
	  }
	 
	public static void main(String[] args) {
		
		 new Tiff2Pdf();

	}

}

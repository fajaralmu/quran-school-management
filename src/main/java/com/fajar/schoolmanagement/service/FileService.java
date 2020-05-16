package com.fajar.schoolmanagement.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
	
	@Autowired
	private WebConfigService webAppConfiguration;
	private String uploadedImagePath;
	
	@PostConstruct
	public void init() {
		uploadedImagePath = webAppConfiguration.getUploadedImageRealPath();
	}
	
	public static void main(String[] args) {
		File file = new File("D:/Development/Files");
		System.out.println(file.toURI().toString());
	}
	
	/**
	 * write base64 image to disk
	 * @param imageCode
	 * @param base64Data
	 * @return
	 * @throws IOException
	 */
	public String writeImage(String imageCode, String base64Data) throws IOException {

			String[] imageData = base64Data.split(",");
			if(imageData == null || imageData.length<2) {
				return null;
			}  
			
			BufferedImage image = generateBufferedImage(imageData[1]);

			// get image extension
			String imageIdentity = imageData[0];
			String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
			
			//image name
			String imageName = UUID.randomUUID().toString();  
			String imageFileName = imageCode+"_"+imageName + "." + imageType;
			
			File outputfile = new File(uploadedImagePath +"/"+imageFileName);
			System.out.println("==========UPLOADED FILE: "+outputfile.getAbsolutePath());
			
			ImageIO.write(image, imageType, outputfile);
			System.out.println("==output file: " + outputfile.getAbsolutePath());
			 
			return imageFileName;
		}

	private BufferedImage generateBufferedImage(String imageString) throws IOException {
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();
		
		return image;
	}


}

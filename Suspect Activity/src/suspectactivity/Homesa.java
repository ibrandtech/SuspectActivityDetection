package suspectactivity;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

//import com.googlecode.javacv.OpenKinectFrameGrabber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Homesa extends JFrame {

	/******************************* DECLARATIONS **************************/

	private JPanel contentPane;
	JPanel vpanel = new JPanel();
	JButton btnstart = new JButton("START");
	JButton btnGroup = new JButton("GROUP");
	JButton btnAut = new JButton("AUTHENTICATION");
	JButton btnFire = new JButton("FIRE");
	JButton btnBag = new JButton("OBJECT");
	JButton btnStop = new JButton("STOP");
	
	JButton Capturebtn = new JButton("CAPTURE");
	JButton checkbtn = new JButton("CHECK");
	JButton newdbbtn = new JButton("NEW USER");
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH-mm-ss");
	Date date = new Date();
	
	private Timer t1;
	private Timer tf;
	private Timer to;
	
	boolean t1b=false;
	boolean tfb=false;
	boolean tob=false;
	private int Count=21;
	private int Countf=21;
	private int Counto=21;
	JLabel timerlbl = new JLabel("");
	JLabel noplbl = new JLabel("");
	JLabel firelbl = new JLabel("NO FIRE");
	JLabel whitelbl = new JLabel("");
	JLabel objtimerlbl = new JLabel("");
	
	int people=0;
	int Bags=0;
	int fire=0;
	
	
	
	int facecount=0;
	String match=null;
	boolean newbtn=false;
	boolean checkubtn=false;
	boolean firstf=true;
	boolean websourceb=false;
				

	/*************************** Thread Declarations ***************/

	private DaemonThread mythread = null;
	VideoCapture websource = null;
	Mat vframe = new Mat();
	Mat gray = new Mat();
	Mat op = new Mat();
	MatOfByte mem = new MatOfByte();
	MatOfRect FaceDetections = new MatOfRect();
	MatOfRect FullBodyDetection = new MatOfRect();
	MatOfRect upperBodyDetection = new MatOfRect();
	MatOfRect lowerBodyDetection = new MatOfRect();
	MatOfRect FireDetections = new MatOfRect();
	MatOfRect bagdetections=new MatOfRect();
	
	Mat backimg=Mat.zeros(100,100, CvType.CV_8UC1);
	Mat backgroundimg=new Mat();


	boolean layDown = false;
	boolean vis=true;
//	public boolean awisopen=false;
//	public boolean fireisopen=false;
	public boolean bagisopen=false;
//	public boolean autisopen=false;
	alertm alert= new alertm();
	
	 private final static String ACCOUNT_SID = "AC80ba84f5f90e834e8ee09c7715c86a7e"; 
	 private final static String AUTH_TOKEN = "2ba0a02670d5b56b98e604eeb95f0a6b"; 
	
	 
	 Mat dilateEle=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24,24));
		Mat erodeEle=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
	 
	int faces=0;
	int human=0;
	int whitep=0;
	private Rect rectcrop=null;
	private BufferedImage image_1, image_2;
    private boolean image_1_load_status = false, image_2_load_status = false;
	
	
	// haarcascade_fullbody.xml haarcascade_frontalface_alt.xml  travelbaghaar.xml firehaar.xml
	// haarcascade_upperbody.xml
	CascadeClassifier FaceDetection=new CascadeClassifier(
			Homesa.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1).replaceAll("%20"," "));
	
//	CascadeClassifier FullBody = new CascadeClassifier("haarcascade_fullbody.xml");
	CascadeClassifier FullBody =new CascadeClassifier(
			Homesa.class.getResource("haarcascade_fullbody.xml").getPath().substring(1).replace("%20", " "));
	CascadeClassifier upperBody = new CascadeClassifier(
			Homesa.class.getResource("haarcascade_upperbody.xml").getPath().substring(1).replace("%20", " "));
	
	CascadeClassifier lowerBody = new CascadeClassifier(
			Homesa.class.getResource("haarcascade_lowerbody.xml").getPath().substring(1).replace("%20", " "));
	CascadeClassifier FireDetection=new CascadeClassifier(
			Homesa.class.getResource("firehaar.xml").getPath().substring(1).replaceAll("%20"," "));
	
	CascadeClassifier BagDetection=new CascadeClassifier(
			Homesa.class.getResource("topten.xml").getPath().substring(1).replaceAll("%20"," "));
	private JTextField captxt;
	private JTextField chktxt;

	/********************************* camera coding ************************/

	class DaemonThread implements Runnable {
		protected volatile boolean runnable = false;

		@Override
		public void run() {
			synchronized (this) {
				while (runnable) {
					if (websource.grab()) {
						try {
							websource.retrieve(vframe);
																				
									if(!btnGroup.isEnabled())
										fullbodyfun();
									if(!btnAut.isEnabled())
										faceauthentication();
									if(!btnFire.isEnabled())
									fired();
									if(!btnBag.isEnabled())
									{
										/*******************BACKGROUND-SUBSTRACTION*****************/
										objectd();
										/******************HAARCASCADE********************/
//										bagd();
//										Imgcodecs.imencode(".bmp", vframe, mem);
									}
									else
									{
										Imgcodecs.imencode(".bmp", vframe, mem);
									}
							         Graphics g = vpanel.getGraphics();
//									Imgcodecs.imencode(".bmp", vframe, mem); //vframe=current frame, mem=memoryofbyte
									Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
									BufferedImage buff = (BufferedImage) im;
									g.drawImage(buff, 0, 0, buff.getWidth(), buff.getHeight(), 0, 0,
									buff.getWidth(), buff.getHeight(),null); 
									} catch (Exception ex) {
							System.out.println("camera Error");
						}
					}
				}
			}
		}
	}
	
/**********************************************************TIMER*******************************************/
	
	public void Timert()
	{
		Count=21;
		t1=new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				{
				
					if(people>0)
					{
						
					
					Count--;
					timerlbl.setText(String.valueOf(Count));
					noplbl.setText(String.valueOf(people));
//					objectlbl.setText(String.valueOf(Bags));
					}
					else
					{
						Count=21;
					}
					if(Count==0)
					{
							Count=21;
							playsound();
							   	
							alert.setVisible(true);
							alert.lblsusa.setText("Suspicious Activity Detected.......");
							alert.lblwar.setText("GROUP DETECTION ALERT...!!!");
							String alertmsg=alert.lblwar.getText();
							sendsms(alertmsg);
							
							String filename = "groupdb\\groupcap"+dateFormat.format(date)+".jpg";
							File file=new File(filename);
							Imgcodecs.imwrite(file.getPath(), vframe);
						    JOptionPane.showMessageDialog(null, "ScreenShoot Saved.....");
						
						
					}
					
					
				}
				
				
			}
			
		});
		
		t1.start();
		t1b=true;
		
	}
	
/*************************************************************PLAYSOUND*********************************/
	
	public static void playsound()
	{
		
		try
		{
			File alertsound=new File("sirentone.wav");
			Clip clip=AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(alertsound));
			clip.start();
			Thread.sleep(clip.getMicrosecondLength()/1000);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Sound Error..");
			
		}
		
	}
	
	/************************************FULLBODY-DETECTION**********************************/
	
	public void fullbodyfun()
	{	
		
		Mat grayframe=new Mat();
		
        Imgproc.cvtColor(this.vframe, grayframe, Imgproc.COLOR_BGR2GRAY);
       
        Imgproc.equalizeHist(grayframe, grayframe);   ///histogram equilization

		
		this.FullBody.detectMultiScale(grayframe, FullBodyDetection); // object of rectofbyte
		
		Rect[] humansarray=FullBodyDetection.toArray();
		people=humansarray.length;

				for (Rect recte : FullBodyDetection.toArray()) {
							Imgproc.rectangle(vframe, new Point(recte.x, recte.y),
							new Point(recte.x + recte.width, recte.y + recte.height),
							new Scalar(255, 0, 255),3);
				}

				
	}
	
	/*******************************************LAY-DOWN*******************************/
	
	public void laydown()
	{
		Mat grayframe=new Mat();
		// convert the frame in gray scale
        Imgproc.cvtColor(this.vframe, grayframe, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayframe, grayframe);
		
		this.upperBody.detectMultiScale(grayframe, upperBodyDetection);
		this.lowerBody.detectMultiScale(grayframe, lowerBodyDetection);
		Rect[] rectu=upperBodyDetection.toArray();
		for (int i = 0; i < rectu.length; i++) {
			Imgproc.rectangle(vframe, new Point(rectu[i].x, rectu[i].y),
			new Point(rectu[i].x + rectu[i].width, rectu[i].y + rectu[i].height),
			new Scalar(255, 0, 255),3);
			Mat ubody=grayframe.submat(rectu[i]);
			this.FaceDetection.detectMultiScale(ubody, FaceDetections);
			Rect[] rectf = FaceDetections.toArray();
			for (int j = 0; j < rectf.length; j++) {
				Imgproc.rectangle(vframe, new Point(rectf[j].x, rectf[j].y),
				new Point(rectf[j].x + rectf[j].width, rectf[j].y + rectf[j].height),
				new Scalar(0, 0, 255),3);
			}
		}
			Rect[] lowerBodyDetectionarray = lowerBodyDetection.toArray();
			for (int k = 0; k < lowerBodyDetectionarray.length; k++)
			{
			    Imgproc.rectangle(vframe, lowerBodyDetectionarray[k].tl(), lowerBodyDetectionarray[k].br(), new Scalar(255, 0,0), 3);
			
			}
			
			
	}
	/*******************************************FIRE-Detection**************************/
	public void fired()
	{
		Rect facef;
		Rect firef;
//		this.FireDetection.detectMultiScale(vframe, FireDetections,1.3, 20, 0,new Size(), new Size(200,200));
//		Rect[] fireDetectionarray=FireDetections.toArray();
//		fire=fireDetectionarray.length;
		this.upperBody.detectMultiScale(vframe, upperBodyDetection);
		Rect[] upperbodya=upperBodyDetection.toArray();
		Mat facecrop=new Mat();
		FaceDetection.detectMultiScale(vframe, FaceDetections);
		Rect[] faceDetectionarray=FaceDetections.toArray();
		int nfaces=faceDetectionarray.length;
		for(int f=0;f<faceDetectionarray.length;f++)
		{
			facecrop=vframe.submat(faceDetectionarray[f]);
			
		}
		if(nfaces>0)
		{
			this.FireDetection.detectMultiScale(facecrop, FireDetections,1.3, 20, 0,new Size(), new Size(200,200));
			Rect[] fireDetectionarray=FireDetections.toArray();
			fire=fireDetectionarray.length;
		for (int k = 0; k < fireDetectionarray.length; k++)
		{
		    	Imgproc.rectangle(vframe, fireDetectionarray[k].tl(), fireDetectionarray[k].br(), new Scalar(255, 0,0), 3);
		    
		}
		}
		else
		{
			this.FireDetection.detectMultiScale(vframe, FireDetections,1.3, 20, 0,new Size(), new Size(200,200));
			Rect[] fireDetectionwf=FireDetections.toArray();
			fire=fireDetectionwf.length;
			for (int k = 0; k < fireDetectionwf.length; k++)
			{
			    	Imgproc.rectangle(vframe, fireDetectionwf[k].tl(), fireDetectionwf[k].br(), new Scalar(255, 0,0), 3);
			    
			}
		}
		
		
	}
/******************************************BAG-DETECTION**************************************************************/
	
	public void bagd()
	{
		this.BagDetection.detectMultiScale(vframe, bagdetections,1.03,20,0,new Size(50,50),new Size(500,500));
		Rect[] bagDetectionarray=bagdetections.toArray();
		Bags=bagDetectionarray.length;
		if(Bags>0)
		{
			playsound();
			if(bagisopen==false)
			{				    	
			alert.setVisible(true);
			alert.lblsusa.setText("Suspicious Activity Detected.......");
			alert.lblwar.setText("BAG DETECTION ALERT...!!!");
			String alertmsg=alert.lblwar.getText();
			sendsms(alertmsg);
			String filename = "objdb\\objcap"+dateFormat.format(date)+".jpg";
			File file=new File(filename);
			Imgcodecs.imwrite(file.getPath(), vframe);
		    JOptionPane.showMessageDialog(null, "ScreenShoot Saved.....");
			bagisopen=true;
			 }
			else
			{
			alert.detection();	
			}
		}
		for (int k = 0; k < bagDetectionarray.length; k++)
		{
		    Imgproc.rectangle(vframe, bagDetectionarray[k].tl(), bagDetectionarray[k].br(), new Scalar(255, 0,0), 3);
		
		}
		
	}
	/*******************************************OBJECT************************************************/
	public void objectd()
	{
		 FaceDetection.detectMultiScale(vframe, FaceDetections);
 		Rect[] harray=FaceDetections.toArray();
 		human=harray.length;
		Imgproc.cvtColor(backgroundimg, backimg, Imgproc.COLOR_RGB2GRAY);
        Mat dst=new Mat();
        

               /*******************STATIC BACKGROUND*********************/
        
        Mat currimg=new Mat();
        Imgproc.cvtColor(vframe, currimg, Imgproc.COLOR_RGB2GRAY);
        Mat fgmask=Mat.zeros(currimg.rows(),currimg.cols(), CvType.CV_8UC1);                          
        Core.absdiff(backimg,currimg, fgmask);

        Imgproc.threshold(fgmask, fgmask, 80, 255, Imgproc.THRESH_BINARY);
        

       whitep= Core.countNonZero(fgmask);
       whitelbl.setText(String.valueOf(whitep));
       Imgproc.erode(fgmask, dst, erodeEle);
       Imgcodecs.imencode(".bmp", dst, mem);
	}
	
/***************************************FACE-AUTHENTICATION*************************************************/
	public void faceauthentication()
	{
	FaceDetection.detectMultiScale(vframe, FaceDetections);
	
	Rect[] faceDetectionarray=FaceDetections.toArray();
	faces=faceDetectionarray.length;
	
	
	for(Rect rect:FaceDetections.toArray())
	{
	Imgproc.rectangle(vframe,new Point(rect.x,rect.y),new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,0),2);
	rectcrop = new Rect(rect.x, rect.y, rect.width, rect.height);
	}
	
	}
	
	 public void compare_image(List<BufferedImage> images , BufferedImage img_2) throws IOException
	 {//Its called by above method compare_image()
		 List<BufferedImage> storeimages = images;
		 List<Mat> matimages = new ArrayList<Mat>();
		 for(int i=0;i<10;i++) 
		 {
		    Mat mat_1 = conv_Mat(storeimages.get(i));
		    matimages.add(mat_1);
		 }
	        Mat mat_2 = conv_Mat(img_2);

	        Mat hist_1 = new Mat();
	        Mat hist_2 = new Mat();

	        MatOfFloat ranges = new MatOfFloat(0f, 256f);
	        MatOfInt histSize = new MatOfInt(25);

	        Imgproc.calcHist(matimages, new MatOfInt(0),
	                new Mat(), hist_1, histSize, ranges);
	        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0),
	                new Mat(), hist_2, histSize, ranges);

	        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
	        Double d = new Double(res * 100);
	        System.out.println(d);

	        if(newbtn==true)
	        {
	        	checkdb(d.intValue());
	        
	        newbtn=false;
	        }
	        else if(checkubtn==true)
	        	{
	        	disp_percen(d.intValue());
	        	checkubtn=false;
	        	}

	    }
	 
	 void checkdb(int d) throws IOException
	 {
		 System.out.println("percentage="+d);

	        if (d >= 90) {
	        	
	        	//String uname=chktxt.getText();
	            JOptionPane.showMessageDialog(null, "Registered USER.....\nUser Name:"+match );

	        } else {
	        	
	        	Capturebtn.setEnabled(true);
				newdbbtn.setEnabled(false);	
	        

	        }
	 }

	    void disp_percen(int d) throws IOException {
	    	System.out.println("percentage="+d);

	        if (d >= 80) {
	        	
	        	String uname=chktxt.getText();
	            JOptionPane.showMessageDialog(null, "AUTHENTICATED USER.....\n" + "USER NAME: " + uname );

	        } else {
	        	
	        			//JOptionPane.showMessageDialog(null, "UNAUTHENTICATED USER\n" + "Similarity : " + d + " %");
	            playsound();	    	
				alert.setVisible(true);
				alert.lblsusa.setText("Suspicious Activity Detected.......");
				alert.lblwar.setText("Unauthenticated User Alert...!!!");
				String alertmsg=alert.lblwar.getText()+"\nUser Account:"+chktxt.getText();
				sendsms(alertmsg);
				
				String filename = "authenticationdb\\autcap"+dateFormat.format(date)+".jpg";
				File file=new File(filename);
				Imgcodecs.imwrite(file.getPath(), vframe);
			    JOptionPane.showMessageDialog(null, "ScreenShoot Saved.....");
				

	        }
	    }

	    private Mat conv_Mat(BufferedImage img) {
	        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
	        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
	        mat.put(0, 0, data);
	        Mat mat1 = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
	        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2HSV);

	        return mat1;
	    }
	    
	    private BufferedImage img_resize(BufferedImage img_temp) {  //Its called by above method img_resize()
	        BufferedImage dimg = new BufferedImage(180, 180, img_temp.getType());
	        Graphics2D g = dimg.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(img_temp, 0, 0, 179, 179, 0, 0, img_temp.getWidth(), img_temp.getHeight(), null);
	        g.dispose();
	        return dimg;
	    }
	
	
	
	/**************************************************SMS_FUNCTION***************************************/
	
	public void sendsms(String Alertm)
	{
		String alertsms=Alertm;
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN); 
        
        Message message = Message.creator(new PhoneNumber("+919763135272"),
                new PhoneNumber("+13074661213"), 
                alertsms).create();
		
        System.out.println(message.getSid()); 
	}
	
	/**
	 * Launch the application.
	 */
	
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Homesa frame = new Homesa();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Homesa() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1366, 768);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		vpanel.setBounds(409, 96, 636, 476);
		contentPane.add(vpanel);
		vpanel.setLayout(null);
		
		BackgroundSubtractorMOG2 mBGSub = Video
				.createBackgroundSubtractorMOG2();

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(28, 22, 357, 401);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Choose Activity Option");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
		lblNewLabel.setBounds(28, 11, 319, 29);
		panel.add(lblNewLabel);
		btnGroup.setEnabled(false);
		
		
		
		btnGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop();
				if(tob)
				to.stop();
				
				timerlbl.setText("");
				noplbl.setText("");
				firelbl.setText("NO FIRE");
				whitelbl.setText("");
				objtimerlbl.setText("");
				
				btnGroup.setEnabled(false);
				btnAut.setEnabled(true);
				btnBag.setEnabled(true);
				btnFire.setEnabled(true);
				
				Timert();
				
			}
		});
		btnGroup.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnGroup.setBounds(24, 111, 180, 34);
		panel.add(btnGroup);
		btnAut.setEnabled(false);
		
		
		
			
		

		
		btnAut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop();
				if(tob)
				to.stop();
				
				timerlbl.setText("");
				noplbl.setText("");
				firelbl.setText("NO FIRE");
				whitelbl.setText("");
				objtimerlbl.setText("");
				
				btnAut.setEnabled(false);
				
				btnGroup.setEnabled(true);
				btnBag.setEnabled(true);
				btnFire.setEnabled(true);
				newdbbtn.setEnabled(true);
			
				
				//Capturebtn.setEnabled(true);
				checkbtn.setEnabled(true);
				
			}
		});
		btnAut.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnAut.setBounds(24, 166, 218, 34);
		panel.add(btnAut);
		btnBag.setEnabled(false);

		
		btnBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop();
				if(tob)
				to.stop();
				
				timerlbl.setText("");
				noplbl.setText("");
				firelbl.setText("NO FIRE");
				whitelbl.setText("");
				objtimerlbl.setText("");

				websource.read(backgroundimg);
				
				to=new Timer(1000,new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						{
						if(whitep>10000)
						{
							if(human==0)
							{
							Counto--;
							objtimerlbl.setText(String.valueOf(Counto));
							}
							else
							{
								Counto=21;
							}
						}
						else
						{
							Counto=21;
						}
							if(Counto==0)
							{				
									Counto=21;
												    	
										alert.setVisible(true);
										alert.lblsusa.setText("Suspicious Activity Detected.......");
										alert.lblwar.setText("OBJECT DETECTION ALERT...!!!");
										String alertmsg=alert.lblwar.getText();
										sendsms(alertmsg);
										String filename = "objdb\\objcap"+dateFormat.format(date)+".jpg";
										File file=new File(filename);
										Imgcodecs.imwrite(file.getPath(), vframe);
									    JOptionPane.showMessageDialog(null, "ScreenShoot Saved.....");
										
								
							}
							
						}
						
						
					}
					
				});
            	to.start();
            	tob=true;
            	
            	btnBag.setEnabled(false);
				btnGroup.setEnabled(true);
				btnAut.setEnabled(true);
				btnFire.setEnabled(true);
			}
		});
		btnBag.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnBag.setBounds(24, 223, 180, 34);
		panel.add(btnBag);
		btnFire.setEnabled(false);

		
		btnFire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop();
				if(tob)
				to.stop();
				
				timerlbl.setText("");
				noplbl.setText("");
				firelbl.setText("NO FIRE");
				whitelbl.setText("");
				objtimerlbl.setText("");
				
				btnFire.setEnabled(false);
				
				btnGroup.setEnabled(true);
				btnAut.setEnabled(true);
				btnBag.setEnabled(true);
				
				tf=new Timer(1000,new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						{
						if(fire>0)
						{
							Countf--;
							firelbl.setText(String.valueOf(Countf));
							
						}
							if(Countf==0)
							{
													
//									firelbl.setText("0");
									Countf=21;
									playsound();
												    	
									alert.setVisible(true);
									alert.lblsusa.setText("Suspicious Activity Detected.......");
									alert.lblwar.setText("FIRE DETECTION ALERT...!!!");
									String alertmsg=alert.lblwar.getText();
									sendsms(alertmsg);
									
									
									String filename = "firedb\\firecap"+dateFormat.format(date)+".jpg";
									File file=new File(filename);
									Imgcodecs.imwrite(file.getPath(), vframe);
								    JOptionPane.showMessageDialog(null, "ScreenShoot Saved.....");
								
							}
							
						}
						
						
					}
					
				});
				
				tf.start();
				tfb=true;
				
			}
		});
		btnFire.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnFire.setBounds(24, 279, 180, 34);
		panel.add(btnFire);

		/*******************************STOP*************************************/
		btnStop.setEnabled(false);
		
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timerlbl.setText("");
				noplbl.setText("");
				firelbl.setText("NO FIRE");
				whitelbl.setText("");
				objtimerlbl.setText("");
				
				btnstart.setEnabled(true);
				mythread.runnable = false;
				websource.release();
				btnStop.setEnabled(false);
				btnGroup.setEnabled(false);
				btnAut.setEnabled(false);
				btnFire.setEnabled(false);
				btnBag.setEnabled(false);
				
				Capturebtn.setEnabled(false);
				checkbtn.setEnabled(false);
				newdbbtn.setEnabled(false);
				
				websourceb=false;
				
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop();
				if(tob)
				to.stop();
				
				
			}
		});
		btnStop.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnStop.setBounds(24, 343, 180, 34);
		panel.add(btnStop);
		
	/**************************************START************************************/
		
		btnstart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				websource = new VideoCapture(0);
				mythread = new DaemonThread();
				Thread th = new Thread(mythread);
				th.setDaemon(true);
				mythread.runnable = true;
				th.start();
				
				btnStop.setEnabled(true);
				btnstart.setEnabled(false);
				
				btnFire.setEnabled(true);
				btnBag.setEnabled(true);
				btnAut.setEnabled(true);
				btnGroup.setEnabled(true);
				
//				awisopen=false;
//				fireisopen=false;
				bagisopen=false;
//				autisopen=false;
				
				websourceb=true;
				
				
				
			}
		});
		btnstart.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnstart.setBounds(24, 51, 180, 34);
		panel.add(btnstart);

		JButton exitbtn = new JButton("");
		Image img = new ImageIcon(this.getClass().getResource("/exits.png")).getImage();
		exitbtn.setIcon(new ImageIcon(img));
		exitbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(t1b)
					t1.stop();
				if(tfb)
					tf.stop(); 
				if(tob)
				to.stop();
				
				
				if(websourceb) {
				mythread.runnable = false;
				websource.release();
				}
				System.exit(0);
				
			}
		});
		exitbtn.setBounds(1270, 11, 70, 72);
		contentPane.add(exitbtn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		panel_1.setBounds(28, 445, 357, 254);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		Capturebtn.setEnabled(false);
		
		
		Capturebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(faces==0)
				{
					JOptionPane.showMessageDialog(null, "NO FACE DETECTED");
				}
				else if(faces==1)
				{
					if(captxt.getText().equals(""))
					{
						JOptionPane.showMessageDialog(null, "Enter Name To SAVE.......!!!!!");               
					}
					else
					{
				Mat facecrop=new Mat(vframe,rectcrop);
			
				if(facecount<10)
				{
				Imgcodecs.imwrite("ADB\\"+captxt.getText()+"_"+facecount+".jpg", facecrop);
				facecount++;
				JOptionPane.showMessageDialog(null, "SAVED SUCCESSFULLY\nIMAGE="+facecount);
				
				}
				else
				{
				JOptionPane.showMessageDialog(null, "DATABASE SAVED SUCCESSFULLY");
				Capturebtn.setEnabled(false);
				newdbbtn.setEnabled(true);
				}
				}
				
			}
				else if(faces>1)
				{
					JOptionPane.showMessageDialog(null, "MORE THAN ONE FACE.......");
				}
			}
		});
		Capturebtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
		Capturebtn.setBounds(198, 117, 135, 34);
		panel_1.add(Capturebtn);
		checkbtn.setEnabled(false);
		
		
		checkbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkubtn=true;
				List<BufferedImage> images = new ArrayList<BufferedImage>();
				if(chktxt.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Enter Name To CHECK.......!!!!!");
				
	                
				}
				else
				{
					Mat newface=new Mat(vframe,rectcrop);
					   Imgcodecs.imwrite(".jpg", newface);

		                try {

		                    image_2 = ImageIO.read(new File(".jpg"));
		                    image_2_load_status = true;
		                    image_2 = img_resize(image_2);//this method created under below as img_resize

		                } catch (IOException e1) {

		                }

		                try {
		                	for(int i=0;i<10;i++)
		                	{
		                    image_1 = ImageIO.read(new File("ADB\\"+chktxt.getText()+"_"+i+".jpg"));
		                    image_1 = img_resize(image_1);
		                    images.add(image_1);
		                	}
		                    image_1_load_status = true;
		                    //this method created under below as img_resize

		                } catch (IOException e1) {
		                	JOptionPane.showMessageDialog(null, "NOT IN DATABASE!!!!!\n");
		                }

		                if (image_1_load_status && image_2_load_status) {

		                    try {
		                        compare_image(images, image_2);//this method created under below as compare_image
		                    } catch (IOException ex) {
		                        Logger.getLogger(Homesa.class.getName()).log(Level.SEVERE, null, ex);
		                    }

		                }}
			}
		});
		checkbtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
		checkbtn.setBounds(198, 192, 135, 34);
		panel_1.add(checkbtn);
		
		JLabel lblAthentication = new JLabel("AUTHENTICATION");
		lblAthentication.setForeground(Color.WHITE);
		lblAthentication.setFont(new Font("Times New Roman", Font.BOLD, 30));
		lblAthentication.setBounds(28, 11, 319, 29);
		panel_1.add(lblAthentication);
		
		captxt = new JTextField();
		captxt.setFont(new Font("Times New Roman", Font.BOLD, 14));
		captxt.setBounds(28, 76, 131, 29);
		panel_1.add(captxt);
		captxt.setColumns(10);
		
		chktxt = new JTextField();
		chktxt.setFont(new Font("Times New Roman", Font.BOLD, 14));
		chktxt.setColumns(10);
		chktxt.setBounds(28, 192, 131, 29);
		panel_1.add(chktxt);
		newdbbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newbtn=true;
				List<String> identifyname = new ArrayList<String>();
				try
				{
				//BufferedImage temp=ImageIO.read(new File("ADB\\"+captxt.getText()+"_1.jpg"));
				if(new File("ADB\\"+captxt.getText()+"_1.jpg").exists())
				{
				
					JOptionPane.showMessageDialog(null, "NAME ALREADY EXISTS IN DATABASE");
				}
				else
				{
					if(faces==0)
					{
						JOptionPane.showMessageDialog(null, "NO FACE DETECTED");
					}
					else if(faces==1)
					{
						if(captxt.getText().equals(""))
						{
							JOptionPane.showMessageDialog(null, "Enter Name To SAVE.......!!!!!");               
						}
						else
						{
							File folder = new File("ADB");
							File[] listOfFiles = folder.listFiles();
							//System.out.println(listOfFiles.length);
							if(listOfFiles.length==0)
							{
								Capturebtn.setEnabled(true);
								newdbbtn.setEnabled(false);
							}
							else
							{
					Mat facecrop=new Mat(vframe,rectcrop);
					Imgcodecs.imwrite(".jpg", facecrop);
					BufferedImage currentimg=ImageIO.read(new File(".jpg"));
					currentimg = img_resize(currentimg);
					
					
					
					    for (int l = 0; l < listOfFiles.length; l++) {
					      if (listOfFiles[l].isFile()) {
					        
					        String arrayname[]= listOfFiles[l].getName().split("_");
					        	identifyname.add(arrayname[0]);
					       	}
					      
					    }
		 List<String> listDistinct = identifyname.stream().distinct().collect(Collectors.toList());
//		 String collectDistinct = listDistinct.stream().collect(Collectors.joining(", "));
//		 System.out.println(collectDistinct); 
		 
		 List<BufferedImage> dbimages = new ArrayList<BufferedImage>();
					
					int dbsize=listDistinct.size();
					for(int dbl=0;dbl<dbsize;dbl++)
					{
						match=listDistinct.get(dbl);
						BufferedImage img;
					for(int i=0;i<10;i++)
                	{
						
                    img = ImageIO.read(new File("ADB\\"+listDistinct.get(dbl)+"_"+i+".jpg"));
                    img = img_resize(img);
                    dbimages.add(img);
                	}
					}
					 try {
	                        compare_image(dbimages, currentimg);//this method created under below as compare_image
	                    } catch (IOException ex) {
	                        Logger.getLogger(Homesa.class.getName()).log(Level.SEVERE, null, ex);
	                    }

						}
//					Capturebtn.setEnabled(true);
//					newdbbtn.setEnabled(false);
					
						}
						}
					else if(faces>1)
					{
						JOptionPane.showMessageDialog(null, "MORE THAN ONE FACE.......");
					}
					
				}
				}
				catch(Exception exp)
				{
					
				}
				}
		});
		
		
		newdbbtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
		newdbbtn.setEnabled(false);
		newdbbtn.setBounds(181, 72, 152, 34);
		panel_1.add(newdbbtn);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBackground(Color.GRAY);
		panel_2.setBounds(1055, 107, 272, 476);
		contentPane.add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 21, 252, 141);
		panel_2.add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblTimer = new JLabel("TIMER:");
		lblTimer.setBounds(23, 61, 123, 27);
		panel_3.add(lblTimer);
		lblTimer.setFont(new Font("Times New Roman", Font.BOLD, 22));
		timerlbl.setBounds(156, 61, 60, 27);
		panel_3.add(timerlbl);
		
		
		timerlbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		
		JLabel lblHumans = new JLabel("People:");
		lblHumans.setBounds(31, 99, 102, 27);
		panel_3.add(lblHumans);
		lblHumans.setFont(new Font("Times New Roman", Font.BOLD, 22));
		noplbl.setBounds(156, 99, 60, 27);
		panel_3.add(noplbl);
		
		
		noplbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		
		JLabel lblGroupDetection = new JLabel("GROUP DETECTION");
		lblGroupDetection.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblGroupDetection.setBounds(10, 11, 232, 27);
		panel_3.add(lblGroupDetection);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(10, 192, 252, 141);
		panel_2.add(panel_4);
		panel_4.setLayout(null);
		
		JLabel lblObjectDetection = new JLabel("OBJECT DETECTION");
		lblObjectDetection.setBounds(10, 11, 232, 27);
		panel_4.add(lblObjectDetection);
		lblObjectDetection.setFont(new Font("Times New Roman", Font.BOLD, 22));
		
		JLabel label = new JLabel("TIMER:");
		label.setFont(new Font("Times New Roman", Font.BOLD, 22));
		label.setBounds(10, 53, 94, 27);
		panel_4.add(label);
		
		
		objtimerlbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		objtimerlbl.setBounds(151, 49, 60, 27);
		panel_4.add(objtimerlbl);
		
		JLabel lblWhitePixels = new JLabel("White Pixels:");
		lblWhitePixels.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblWhitePixels.setBounds(10, 91, 126, 27);
		panel_4.add(lblWhitePixels);
		
		
		whitelbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		whitelbl.setBounds(146, 91, 96, 27);
		panel_4.add(whitelbl);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(10, 362, 252, 90);
		panel_2.add(panel_5);
		panel_5.setLayout(null);
		
		JLabel lblFireDetection = new JLabel("FIRE DETECTION");
		lblFireDetection.setBounds(29, 11, 213, 27);
		lblFireDetection.setFont(new Font("Times New Roman", Font.BOLD, 22));
		panel_5.add(lblFireDetection);
		firelbl.setBounds(114, 49, 102, 27);
		panel_5.add(firelbl);
		
		
		firelbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		
		JLabel lblFire = new JLabel("TIMER:");
		lblFire.setBounds(10, 49, 94, 27);
		panel_5.add(lblFire);
		lblFire.setFont(new Font("Times New Roman", Font.BOLD, 22));
	}
}

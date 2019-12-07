package suspectactivity;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
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
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


//import eblink.Home.DaemonThread;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
//import eblink.Home.DaemonThread;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;

public class backsub extends JFrame {

	private JPanel contentPane;
	
	private  DaemonThread mythread=null;
	private DaemonThreadb bthread=null;
	int count=0;
	private int Bcount=0;
	//boolean isClose=false;

	VideoCapture websource=null;
	VideoCapture bwebsource=null;
	
	private Timer tf;
	
	int Count=20;
	int whitep=0;
	alertm alertb= new alertm();
	
	JLabel whitelbl = new JLabel("White");
	MatOfPoint pointblack=new MatOfPoint();
	Mat frame1=new Mat();
	Mat frame2=Mat.zeros(100,100, CvType.CV_8UC1);
	Mat backimg=Mat.zeros(100,100, CvType.CV_8UC1);
	Mat backgroundimg=new Mat();
	MatOfByte mem=new MatOfByte();
	MatOfByte mem2=new MatOfByte();
	MatOfRect FaceDetections=new MatOfRect();
	CascadeClassifier FaceDetection=new CascadeClassifier(
			backsub.class.getResource("haarcascade_upperbody.xml").getPath().substring(1).replaceAll("%20"," "));
	

	
	JPanel videopanel = new JPanel();
	JPanel backpanel = new JPanel();
	
	JLabel difflbl = new JLabel("");
	
	JButton btnStart = new JButton("Start");
	JButton btnStop = new JButton("Stop");
	JLabel capimg = new JLabel("");
	JLabel blbl = new JLabel("");
	
	BackgroundSubtractorMOG2 mBGSub = Video
			.createBackgroundSubtractorMOG2();
	
	Mat dilateEle=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24,24));
	Mat erodeEle=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
	int faces=0;
	private final JLabel lblTimer = new JLabel("Timer:");
	private final JLabel tlbl = new JLabel("");
	boolean bagisopen=false;
	
	
	//CascadeClassifier FaceDetector = new CascadeClassifier(
			//JavaCam.class.getResource("haarcascade_eye.xml").getPath().substring(1).replace("%20", " "));
	
	
	class DaemonThread implements Runnable
    {
    protected volatile boolean runnable = false;

    @Override
    public  void run()
    {
        synchronized(this)
        {
            while(runnable)
            {
                if(websource.grab())
                {
		    	try
                        {
                            websource.retrieve(frame1);
                            FaceDetection.detectMultiScale(frame1, FaceDetections);
                    		Rect[] faceDetectionarray=FaceDetections.toArray();
                    		faces=faceDetectionarray.length;                 		
                    		for (int k = 0; k < faceDetectionarray.length; k++)
                    		{
                    		    Imgproc.rectangle(frame1, faceDetectionarray[k].tl(), faceDetectionarray[k].br(), new Scalar(255, 0,0), 3);
                    		
                    		}
                            Imgcodecs.imencode(".bmp", frame1, mem);
			                Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
             			    BufferedImage buff = (BufferedImage) im;
			                Graphics g=videopanel.getGraphics();

			    if (g.drawImage(buff, 0, 0, getWidth()-700, getHeight()-130, 0, 0, buff.getWidth(), buff.getHeight(),null))
			    {			    	
			    if(runnable == false)
                            {
			    	System.out.println("Going to wait");			    	
			    }
			 }
							
                        }
			 catch(Exception ex)
                         {
			    System.out.println("Error");
                         }
                }
            }
        }
     }
   }
	/*******************************BACK-GROUND*********************/
	class DaemonThreadb implements Runnable
    {
    protected volatile boolean runnable = false;

    @Override
    public  void run()
    {
        synchronized(this)
        {
            while(runnable)
            {
                if(bwebsource.grab())
                {
		    	try
                        {
		    				Imgproc.cvtColor(backgroundimg, backimg, Imgproc.COLOR_RGB2GRAY);
	    				
                            bwebsource.retrieve(frame2);
                            Mat dst=new Mat();
                            
 /**********************************Background Subtraction**********************************/                          
//                          Mat fgmask=new Mat();
//                          mBGSub.apply(frame2,fgmask);
//                          Imgproc.threshold(fgmask, fgmask, 80, 255, Imgproc.THRESH_BINARY);
//                          Imgproc.erode(fgmask, dst, erodeEle);
//                          Imgcodecs.imencode(".bmp", dst, mem2);
                       
 /*****************************STATIC BACKGROUND*************************************************/
                            
                            Mat currimg=new Mat();
                            Imgproc.cvtColor(frame2, currimg, Imgproc.COLOR_RGB2GRAY);
                            Mat fgmask=Mat.zeros(currimg.rows(),currimg.cols(), CvType.CV_8UC1);                          
                            Core.absdiff(backimg,currimg, fgmask);
//                            float diffpct = countNonZero(currimg-backimg) / (backgroundimg.rows() * frame2.cols());
                            
                            
                            Imgproc.threshold(fgmask, fgmask, 80, 255, Imgproc.THRESH_BINARY);

                           whitep= Core.countNonZero(fgmask);
                           whitelbl.setText(String.valueOf(whitep));
                            Imgproc.erode(fgmask, dst, erodeEle);
                            Imgcodecs.imencode(".bmp", dst, mem2);
                                                    
			                Image im = ImageIO.read(new ByteArrayInputStream(mem2.toArray()));
             			    BufferedImage buff = (BufferedImage) im;
			                Graphics g=backpanel.getGraphics();

			    if (g.drawImage(buff, 0, 0, getWidth()-700, getHeight()-130, 0, 0, buff.getWidth(), buff.getHeight(),null))
			    {			    	
			    if(runnable == false)
                            {
			    	System.out.println("Going to wait");			    	
			    }
			 }
							
                        }
			 catch(Exception ex)
                         {
			    System.out.println("Error");
                         }
                }
            }
        }
     }
   }
	
	
	/**********************************Background Capture**********************/
	public void backcap()
	{
	VideoCapture camera=new VideoCapture(1);
	if(!camera.isOpened())
	{
		System.out.println("ERROR....");	
	}
	else
	{
		
		while(true)
		{
			if(camera.read(backgroundimg))
			{
				System.out.println("ok");
				//backimg=backgroundimg;
				break;
			}
		}
	}
	camera.release();
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.load("C:\\opencv\\build\\x64\\vc14\\bin\\opencv_videoio_ffmpeg412_64.dll");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					backsub frame = new backsub();
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
	public backsub() {
		setTitle("WebCam");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 1300, 678);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		videopanel.setBackground(Color.GRAY);
		
		
		videopanel.setBounds(25, 21, 580, 539);
		contentPane.add(videopanel);
		btnStart.setFont(new Font("Times New Roman", Font.BOLD, 30));
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//websource=new VideoCapture("‪‪‪\\samples\\Cardio1.mp4");
				backcap();
				websource=new VideoCapture(1);
				bwebsource=new VideoCapture(1);
				mythread=new DaemonThread();
				Thread t=new Thread(mythread);
				t.setDaemon(true);
				mythread.runnable=true;
				t.start();
				
				bthread=new DaemonThreadb();
				Thread t1=new Thread(bthread);
				t1.setDaemon(true);
				bthread.runnable=true;
				t1.start();
				
				
				
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				
				tf=new Timer(1000,new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						{
					
						if(whitep>10000)
						{
//							if(faces==0)
//							{
							Count--;
							tlbl.setText(String.valueOf(Count));
//							}
//							else
//							{
//								Count=20;
//							}
						}
						else
						{
							Count=20;
						}
							if(Count==0)
							{
													
									Count=20;
									if(bagisopen==false)
										{				    	
										alertb.setVisible(true);
										alertb.lblsusa.setText("Suspicious Activity Detected.......");
										alertb.lblwar.setText("OBJECT DETECTION ALERT...!!!");
										bagisopen=true;
										 }
										else
										{
										alertb.detection();
										}
								
							}
							
						}
						
						
					}
					
				});
            	tf.start();
			}
		});
		
		btnStart.setBounds(65, 571, 143, 42);
		contentPane.add(btnStart);
		btnStop.setFont(new Font("Times New Roman", Font.BOLD, 30));
		
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mythread.runnable=false;
				bthread.runnable=false;
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
				websource.release();
				bwebsource.release();
			}
		});
		btnStop.setBounds(331, 571, 143, 42);
		contentPane.add(btnStop);
		
		
		blbl.setFont(new Font("Elephant", Font.BOLD, 22));
		blbl.setBounds(1170, 68, 81, 50);
		contentPane.add(blbl);
		
		
		backpanel.setBackground(Color.GRAY);
		backpanel.setBounds(655, 21, 580, 539);
		contentPane.add(backpanel);
		backpanel.add(capimg);
		
		
		difflbl.setFont(new Font("Times New Roman", Font.BOLD, 30));
		difflbl.setBounds(530, 571, 143, 42);
		contentPane.add(difflbl);
		lblTimer.setFont(new Font("Times New Roman", Font.BOLD, 30));
		lblTimer.setBounds(807, 571, 95, 42);
		
		contentPane.add(lblTimer);
		
		
		whitelbl.setFont(new Font("Times New Roman", Font.BOLD, 30));
		whitelbl.setBounds(1053, 571, 133, 42);
		contentPane.add(whitelbl);
		tlbl.setFont(new Font("Times New Roman", Font.BOLD, 30));
		tlbl.setBounds(912, 571, 95, 42);
		
		contentPane.add(tlbl);
	}
}

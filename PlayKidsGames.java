//This code can be able to let the user load "specific" games in .class files and be able to let the user choose which game 
//to play with by clicking on the GUI



import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;




import java.util.*;
public class PlayKidsGames	implements ListSelectionListener
{
	private JFrame programsWindow = new JFrame("Select a KidsGame");
	private JList<String> programsList  = new JList<String>();
	private Class<?> gameProgramClassObject; 
	private JPanel   gameProgram;
	Method[] gameMethods;
	String[] methodNames;
	TreeMap<String, Method> methodMap = new TreeMap<String, Method>();
	private JFrame        methodsWindow = new JFrame();        // window to show the method names
	private JList<String> methodsList   = new JList<String>(); // GUI object to show (and select!) method names.
	private JFrame gameWindow = new JFrame();  
	private int callNumber = 0;

	public PlayKidsGames() throws ClassNotFoundException // CONSTRUCTOR
	{

		Vector<String> programNamesToShow = new Vector<String>();
		programsWindow.getContentPane().add(programsList);//"Center" by default
		programsWindow.setSize(300,300);
		programsWindow.setLocation(0,0);
		programsWindow.setVisible(true);
		programsWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		programsList.setSelectionMode(0); // single select mode.
		programsList.addListSelectionListener(this);




		File localDirectory = new File(System.getProperty("user.dir"));
		//	System.out.println("Programs in the local directory " 
		//			+ localDirectory + " that are an KidGame are:");
		String[] listOfFiles = localDirectory.list();


		for (String fileName : listOfFiles) 
		{                                  
			if (fileName.endsWith(".class"))
			{
				// strip off the ending ".class"
				String className = fileName.substring(0,fileName.length()-6);
				// Load the Class object for the className we just found: 
				Class<?> classObject = Class.forName(className); // loads just the class object and saves reference to it.
				if (PlayKidsGames.class == classObject) continue; // we have found us!
				if (classObject.isInterface()) continue; // don't show interface class names.
				Class<?>[] interfaceList = classObject.getInterfaces();//implemented by this class
				for (Class<?> interfaceClassObject : interfaceList)
					if (interfaceClassObject.getName().equals("KidsGame")) programNamesToShow.add(className); 
			} // end of if
		} // end of for
		programsList.setListData(programNamesToShow);
	} // end of constructor()


	public static void main(String[] args) 
	{
		System.out.println("Guangsen Ji @2020");
		try {new PlayKidsGames();} // load and then call the constructor.
		catch(Exception e){System.out.println(e.getMessage());} // handle any problems the constructor had.
	}


	@Override
	public void valueChanged(ListSelectionEvent e)
	{	
		if (e.getSource() == programsList)	
		{


			if (programsList.getValueIsAdjusting()) return; // still selecting!
			String gameProgramName = programsList.getSelectedValue(); // because we declared the JList to hold Strings
			System.out.println(gameProgramName + " was just selected!");	

			try {
				gameProgramClassObject = Class.forName(gameProgramName);
			    gameProgram = (JPanel) gameProgramClassObject.newInstance(); // load&go!
				System.out.println("Methods of " + gameProgramClassObject.getName());
				gameMethods = gameProgramClassObject.getDeclaredMethods(); 
				for (Method method : gameMethods) // loop through methods array
				{
					String methodName = method.getName(); // get simple name
					System.out.println(methodName); 
					methodMap.put(methodName, method); // add the Method object to a keyed collection
					methodMap.remove("paint"); // We don't want to show the paint() method to the user.
					methodNames = methodMap.keySet().toArray(new String[0]); // get key list 		           
				} // end of Methods array loop
			}
			catch(Exception e1)
			{
				System.out.println(e1.getMessage());
			}

			// Now show the methodsWindow with the methodNames in it.
			methodsWindow.setTitle(gameProgramName);
			methodsWindow.getContentPane().add(methodsList); // "Center" default
			methodsList.setListData(methodNames);// put names in JList
			methodsList.setSelectionMode(0); // single select mode.
			methodsWindow.setSize(300,300);
			methodsWindow.setLocation(300,0); // x,y (next to programsWindow)
			methodsWindow.setVisible(true); // show 2nd window
			methodsWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			methodsList.addListSelectionListener(this);//methods JList: call me!
			programsList.removeListSelectionListener(this);//programs JList: DON'T call me!
			programsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			programsWindow.setVisible(false); // close 1st window!

			// Now show the (3nd) gameWindow with the game running in it.
			gameWindow.setTitle(gameProgramName);
			gameWindow.getContentPane().add(gameProgram);//"Center" default
			gameWindow.setSize(300,300);
			gameWindow.setLocation(600,0); // x,y (next to methodsWindow)
			gameWindow.setVisible(true); // show 3rd window
			gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			



		}   	
		if (e.getSource() == methodsList)	
		   {
		   if (methodsList.getValueIsAdjusting()) return; // still selecting!
		   String methodName = methodsList.getSelectedValue();
		   System.out.println(methodName + " method was selected! call #" + callNumber++);	
			   
		   // when the methodsList in the methodsWindow calls, branch to the selected method in the game program.
		   
		   Method methodObject = methodMap.get(methodName); // retrieve Method object associated with this name.        
			try
			{
				methodObject.invoke(gameProgram);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // the EXECUTABLE object is the parameter of the invoke() method.

		 
		   }
		




	}




} // end of class


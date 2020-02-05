import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class MyFirstGUI implements ActionListener
{

//decalre program "instane" variables 
JFrame window = new JFrame("This is my first GUI program");
JButton button = new JButton("PUSH ME QAQ ");
JTextField textField = new JTextField("Enter data here and press ENTER");
JTextArea textArea = new JTextArea("info presented to user here");
public MyFirstGUI(){
    System.out.println("MyFirstGUI @OFF-Uber 2020 ");
    window.getContentPane().add(button,"North"); //按钮
    window.getContentPane().add(textArea, "Center"); //用户反馈界面
    window.getContentPane().add(textField, "South"); //用户输入界面
    window.setSize(500,400); // default size is ICON size
    window.setLocation(500,0);//x:from left to right, y:down forn the top 
    button.setBackground(Color.red);
    textField.setBackground(Color.yellow);
    textArea.setFont(new Font("default", Font.BOLD, 20));
    window.setVisible(true);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    button.addActionListener(this); //give address of MyFirstGUI program to the button pogram
    textField.addActionListener(this);//call when ENTER,give address of MyFirstGUI program to the enter program
    
}
public void actionPerformed(ActionEvent ae)
{
    if(ae.getSource() == button)                        //return the address of the object that is calling
    {
        textArea.setText("Button is pushed");
    }
    if(ae.getSource() == textField)
    {   
        String input = textField.getText();
        textField.setText("");
        textArea.setText("Data entered was:"+input);
        
    }


}
public static void main(String[] args){
    MyFirstGUI mfg = new MyFirstGUI();
}


}

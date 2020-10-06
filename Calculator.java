import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Stack;
import java.util.HashMap;

class WinListener extends WindowAdapter
{
    public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }
}

public class Calculator extends JFrame implements ActionListener
{
    JPanel buttons,display;
    JLabel displayLabel;
    boolean flag=true;
    HashMap<Character,Integer> operations=new HashMap<>();

    private String simplify(String expression)
    {
        StringBuilder str=new StringBuilder();
        for(int i=0;i<expression.length();i++)
        {
            char ch=expression.charAt(i);
            if(Character.isLetter(ch))
            {
                int ind=expression.indexOf('(',i);
                int j=ind+1;
                int left=1;
                int right=0;
                while(left!=right)
                {
                    if(expression.charAt(j)=='(')
                        left++;
                    else if(expression.charAt(j)==')')
                        right++;

                    j++;
                }
                double anns=evaluate(expression.substring(ind,j));
                String func=expression.substring(i,ind);

                if(func.equals("sin"))
                {
                    anns=Math.sin(anns);
                }
                else if(func.equals("cos"))
                {
                    anns=Math.cos(anns);
                }
                else if(func.equals("tan"))
                {
                    anns=Math.tan(anns);
                }
                else if(func.equals("sec"))
                {
                    anns=1/Math.cos(anns);
                }
                else if(func.equals("cosine"))
                {
                    anns=1/Math.sin(anns);
                }
                else if(func.equals("cot"))
                {
                    anns=1/Math.tan(anns);
                }
                else if(func.equals("log"))
                {
                    anns=1/Math.log10(anns);
                }
                else if(func.equals("ln"))
                {
                    anns=1/Math.log(anns);
                }

                str.append((int)(anns));
                i=j-1;
            }
            else if(operations.containsKey(ch) || Character.isDigit(ch) || ch=='(' || ch==')')
            {
                str.append(ch);
            }
            else
            {
                return null;
            }
        }

        return str.toString();
    }
    public String toPostfix(String expression)
    {
        StringBuilder str=new StringBuilder();
        Stack<Character> stack=new Stack<>();
        stack.push('#');

        for(int i=0;i<expression.length();i++)
        {
            char ch=expression.charAt(i);
            if(Character.isDigit(ch) || ch=='.')
            {
                int j = i + 1;
                while (j<expression.length() && (Character.isDigit(expression.charAt(j))
                        || expression.charAt(j)=='.'))
                {
                    j++;
                }
                str.append(expression, i, j);
                str.append(',');
                i = j - 1;
            }
            else if(ch=='(' || ch=='^')
            {
                stack.push(ch);
            }
            else if(ch==')')
            {
                while(stack.peek()!='#' && stack.peek()!='(' )
                {
                    str.append(stack.pop());
                }
                stack.pop();
            }
            else
            {
                while(stack.peek()!='#' && hasHighPrecedence(stack.peek(),ch))
                {
                    str.append(stack.pop());
                }
                stack.push(ch);
            }
        }

        while(stack.peek()!='#')
        {
            str.append(stack.pop());
        }

        return str.toString();
    }

    public boolean hasHighPrecedence(char op1, char op2)
    {
        System.out.println(op1+" "+op2);
        if (op2 == '(' || op2 == ')')
            return false;
        if (operations.get(op1)<operations.get(op2))
            return false;
        else
            return true;
    }
    public int applyOp(char op,int b,int a)
    {
        if(op=='^')
        {
            return((int)(Math.pow(a,b)));
        }
        else if(op=='+')
        {
            return(a+b);
        }
        else if(op=='-')
        {
            return(a-b);
        }
        else if(op=='*')
        {
            return a*b;
        }
        else if(op=='/')
        {
            return(a/b);
        }

        return 0;
    }
    public int solvePostfix(String expression)
    {
        Stack<Integer> value= new Stack<>();

        for(int i=0;i<expression.length();i++)
        {
            char ch=expression.charAt(i);

            if(operations.containsKey(ch))
            {
                value.push(applyOp(ch,value.pop(),value.pop()));
            }
            else if(Character.isDigit(ch))
            {
                int j=i+1;
                while(j<expression.length() && !operations.containsKey(expression.charAt(j)) && expression.charAt(j)!=',')
                {
                    j++;
                }
                value.push(Integer.parseInt(expression.substring(i,j)));
                i=j-1;
            }
        }

        return value.peek();
    }

    public boolean isBalanced(String expression)
    {
        return true;
    }
    public int evaluate(String expression)
    {
        if(!isBalanced(expression))
            return Integer.MAX_VALUE;

        expression=simplify(expression);
        if(expression==null)
            return Integer.MAX_VALUE;

        expression=toPostfix(expression);
        return solvePostfix(expression);
    }
    public void actionPerformed(ActionEvent e)
    {
        JButton btn=(JButton)(e.getSource());
        if(flag)
        {
            flag=false;
            displayLabel.setText("");
        }

        String expression=displayLabel.getText();
        String btntext=btn.getText();
        if(btntext.equals("="))
        {
            int ans=(int)(evaluate(expression));
            if(ans==Integer.MAX_VALUE)
            {
                displayLabel.setText("Invalid Expression");
                flag=true;
            }
            else
                displayLabel.setText(ans+"");
        }
        else if(btntext.equals("clear"))
        {
            reset();
        }
        else if(btntext.equals("del"))
        {
            if(expression.length()>0)
                displayLabel.setText(expression.substring(0,expression.length()-1));
        }
        else if(btntext.equals("~"))
        {
            displayLabel.setText("-("+expression+")");
        }
        else
        {
            displayLabel.setText(expression+btntext);
        }

    }

    private void reset() {
        displayLabel.setText("Enter ur expression...");
        flag=true;
    }

    public Calculator()
    {
        super("Calculator");
        this.addWindowListener(new WinListener());
        this.setSize(450,250);
        
        operations.put('+',1);
        operations.put('-',1);
        operations.put('/',2);
        operations.put('*',2);
        operations.put('^',3);

        display=new JPanel();
        displayLabel=new JLabel("Enter ur expression...",SwingConstants.RIGHT);
        display.setBackground(Color.WHITE);
        displayLabel.setForeground(Color.GRAY);
        display.setAlignmentX(Component.RIGHT_ALIGNMENT);
        display.setAlignmentY(Component.CENTER_ALIGNMENT);
        display.add(displayLabel);

        buttons=new JPanel();
        buttons.setLayout(new GridLayout(6,5));

        String[] panel ={"sin","ln","clear","del","=",
                        "cos","log","(",")","^",
                        "tan","1","2","3","/",
                        "cosine","4","5","6","*",
                        "sec","7","8","9","-",
                        "cot","~","0",".","+"};

        for(int i=0;i<30;i++)
        {
            JButton btn=new JButton(panel[i]);
            btn.addActionListener(this);
            buttons.add(btn);
        }

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints=new GridBagConstraints();

        constraints.fill=GridBagConstraints.BOTH;
        constraints.weightx=0.1;
        constraints.weighty=0.1;
        constraints.gridx=0;
        constraints.gridy=0;
        this.add(display,constraints);


        constraints.gridx=0;
        constraints.gridy=1;
        this.add(buttons,constraints);


        this.setVisible(true);
    }

    public static void main(String args[])
    {
        new Calculator();
    }
}


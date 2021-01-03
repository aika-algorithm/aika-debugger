package network.aika.visualization;

import network.aika.text.Document;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AikaDebugger extends JPanel {

    JTabbedPane tabbedPane;

    Document doc;

    ActivationViewerManager actViewManager;

    public AikaDebugger(Document doc) {
        super(new GridLayout(1, 1));

        this.doc = doc;

        tabbedPane = new JTabbedPane();
//        ImageIcon icon = createImageIcon("images/middle.gif");

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == ' ') {
                    actViewManager.click();
                } else if(e.getKeyChar() == 'v') {
                    System.out.println("Visitor");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        tabbedPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Click");
           //     click();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        actViewManager = new ActivationViewerManager(doc);

        addTab(0, "Activations", KeyEvent.VK_A, actViewManager.getView());
    }

    public void addTab(int tabIndex, String label, int ke, JComponent panel) {
        tabbedPane.addTab(label, null, panel,
                "Does nothing");
        tabbedPane.setMnemonicAt(tabIndex, ke);
    }

    public static void createAndShowGUI(Document doc) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Aika Debugger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new AikaDebugger(doc), BorderLayout.CENTER);

        frame.setSize( 800, 600 );
        frame.setVisible(true);
    }
}

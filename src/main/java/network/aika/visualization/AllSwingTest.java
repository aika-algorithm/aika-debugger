package network.aika.visualization;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class AllSwingTest extends JFrame {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        AllSwingTest test = new AllSwingTest() ;
        test.run();
    }

    protected String styleSheetA = "graph {padding: 60px;} node {fill-color: green;}";
    protected String styleSheetB = "graph {padding: 60px;} node {fill-color: blue;}";

    public void run() throws InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        MultiGraph ga = new MultiGraph("mga");
        MultiGraph gb = new MultiGraph("mgb");
        SwingViewer va = new SwingViewer(ga, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        SwingViewer vb = new SwingViewer(gb, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

        ga.setAttribute("ui.antialias");
        ga.setAttribute("ui.quality");
        ga.setAttribute("ui.stylesheet", styleSheetA);
        gb.setAttribute("ui.antialias");
        gb.setAttribute("ui.quality");
        gb.setAttribute("ui.stylesheet", styleSheetB);

        va.enableAutoLayout();
        vb.enableAutoLayout();


        JTabbedPane tabbedPane = new JTabbedPane();
     //   ImageIcon icon = createImageIcon("images/middle.gif");



        DefaultView viewA = (DefaultView)va.addDefaultView(false, new SwingGraphRenderer());
        add(viewA, BorderLayout.CENTER);

        tabbedPane.addTab("Tab A", null, viewA,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


        DefaultView viewB = (DefaultView)vb.addDefaultView(false, new SwingGraphRenderer());
        add(viewB, BorderLayout.CENTER);

        tabbedPane.addTab("Tab B", null, viewB,
                "Does nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_1);

        add(tabbedPane);


        for(Graph g: new Graph[] {ga, gb}) {
            DorogovtsevMendesGenerator gen = new DorogovtsevMendesGenerator();
            gen.addSink(g);
            gen.begin();
            for (int i = 0; i < 100; i++)
                gen.nextEvents();
            gen.end();
            gen.removeSink(g);
        }

        setSize( 800, 600 );
        setVisible( true );

    }
}
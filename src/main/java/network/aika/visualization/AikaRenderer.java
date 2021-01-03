package network.aika.visualization;


import org.graphstream.ui.graphicGraph.*;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.swing.BackendJ2D;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing.renderer.SelectionRenderer;

import java.awt.*;

public class AikaRenderer extends SwingGraphRenderer {

    @Override
    public void open(GraphicGraph graph, Container drawingSurface) {
        if( this.graph == null ) {
            this.graph   = graph;
            this.backend = new AikaBackend();		// choose it according to some setting
            this.camera  = new AikaCamera(graph);
            graph.getStyleGroups().addListener(this);
            backend.open(drawingSurface);
        }
        else {
            throw new RuntimeException("renderer already open, use close() first");
        }
    }


    @Override
    public void render(Graphics2D g, int x, int y, int width, int height) {
        if(graph != null) {
            startFrame();

            // Verify this view is not closed, the Swing repaint mechanism may trigger 1 or 2
            // calls to this after being closed.
            if(backend == null)
                backend = new AikaBackend(); // TODO choose it according to some setting ...

            backend.prepareNewFrame(g);
            camera.setBackend(backend);

            StyleGroupSet sgs = graph.getStyleGroups();

            setupGraphics();
            graph.computeBounds();
            camera.setBounds(graph);
            camera.setViewport(x, y, width, height);
            getStyleRenderer(graph).render(backend, camera, width, height);
            renderBackLayer();

            camera.pushView(graph);
            sgs.shadows().forEach( s -> getStyleRenderer(s).renderShadow(backend, camera));


            sgs.getZIndex().forEach( groups -> {
                groups.forEach( group -> {
                    if(group.getType() != Selector.Type.GRAPH) {
                        getStyleRenderer(group).render(backend, camera);
                    }
                });
            });

            camera.popView();
            renderForeLayer();

            if( selection.getRenderer() == null )
                selection.setRenderer(new SelectionRenderer( selection, graph ));
            selection.getRenderer().render(backend, camera, width, height );

            endFrame();
        }
    }


}
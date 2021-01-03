package network.aika.visualization;


import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.*;

import org.graphstream.ui.view.camera.*;


public class AikaCamera extends DefaultCamera2D {


    public AikaCamera(GraphicGraph graph) {
        super(graph);
    }

    /**
     * Compute a transformation that pass from graph units (user space) to a pixel
     * units (device space) so that the view (zoom and center) requested by the user
     * is produced.
     *
     * @return The transformation modified.
     */
    public void userView() {
        double sx = 0.0;
        double sy = 0.0;
        double tx = 0.0;
        double ty = 0.0;
        double padXgu = paddingXgu() * 2;
        double padYgu = paddingYgu() * 2;
        double padXpx = paddingXpx() * 2;
        double padYpx = paddingYpx() * 2;
        double gw;
        if (gviewport != null)
            gw = gviewport[2] - gviewport[0];
        else
            gw = metrics.size.data[0];

        double gh;
        if (gviewport != null)
            gh = gviewport[3] - gviewport[1];
        else
            gh = metrics.size.data[1];

        if (padXpx > metrics.viewport[2])
            padXpx = metrics.viewport[2] / 10.0;
        if (padYpx > metrics.viewport[3])
            padYpx = metrics.viewport[3] / 10.0;

        sx = (metrics.viewport[2] - padXpx) / ((gw + padXgu) * zoom);
        sy = (metrics.viewport[3] - padYpx) / ((gh + padYgu) * zoom);

        tx = center.x;
        ty = center.y;

        if (sx > sy) // The least ratio.
            sx = sy;
        else
            sy = sx;

        bck.beginTransform();
        bck.setIdentity();
//        bck.translate(50, 50, 0); // 4. Place the whole result at the center
        bck.translate(metrics.viewport[2] / 2, metrics.viewport[3] / 2, 0); // 4. Place the whole result at the center
        // of the view port.
        if (rotation != 0)
           bck.rotate(rotation / (180.0 / Math.PI), 0, 0, 1); // 3. Eventually apply a rotation.
        bck.scale(sx, -sy, 0); // 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to
        // bottom-left).
        bck.translate(-tx, -ty, 0); // 1. Move the graph so that the give center is at (0,0).
        bck.endTransform();

        Point3 dst = bck.transform(0, 0, 0);

        metrics.ratioPx2Gu = sx;

        double w2 = (metrics.viewport[2] / sx) / 2f;
        double h2 = (metrics.viewport[3] / sx) / 2f;

        metrics.loVisible.set(center.x - w2, center.y - h2);
        metrics.hiVisible.set(center.x + w2, center.y + h2);
    }

}

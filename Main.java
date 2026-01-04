//Author: Steffen Herweg
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<List<Vec2>> loadedCurves;

            try {
                loadedCurves = VertLoader.load("riderr.vert"); 
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            JFrame frame = new JFrame("Project 1 - Discrete Curves");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            frame.setContentPane(new DrawPanel(loadedCurves));

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    static class DrawPanel extends JPanel {

        private final List<List<Vec2>> curves;

        DrawPanel(List<List<Vec2>> curves) {
            this.curves = curves;

            // curvature flow steps
            for (int i = 0; i < 10; i++) {
                applyCurvatureFlow(0.01);
            }

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;

            
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // boundingbox
            double minimumX = Double.POSITIVE_INFINITY;
            double maximumX = Double.NEGATIVE_INFINITY;
            double minimumY = Double.POSITIVE_INFINITY;
            double maximumY = Double.NEGATIVE_INFINITY;

            for (List<Vec2> curve : curves) {
                for (Vec2 point : curve) {
                    minimumX = Math.min(minimumX, point.x);
                    maximumX = Math.max(maximumX, point.x);
                    minimumY = Math.min(minimumY, point.y);
                    maximumY = Math.max(maximumY, point.y);
                }
            }

            double curveWidth = maximumX - minimumX;
            double curveHeight = maximumY - minimumY;

        
            if (curveWidth == 0) {
                curveWidth = 1;
            }
            if (curveHeight == 0) {
                curveHeight = 1;
            }

            //  scaling calculate
            double margin = 40;
            double scaleX = (getWidth() - 2 * margin) / curveWidth;
            double scaleY = (getHeight() - 2 * margin) / curveHeight;
            double scale = Math.min(scaleX, scaleY);

            // mid calculate
            double curveCenterX = (minimumX + maximumX) / 2.0;
            double curveCenterY = (minimumY + maximumY) / 2.0;

            // transform
            AffineTransform worldToScreenTransform = new AffineTransform();
            worldToScreenTransform.translate(getWidth() / 2.0, getHeight() / 2.0);
            worldToScreenTransform.scale(scale, -scale); // Y nach oben
            worldToScreenTransform.translate(-curveCenterX, -curveCenterY);

            // draw curve
            graphics2D.setColor(Color.BLACK);

            for (List<Vec2> curve : curves) {
                if (!curve.isEmpty()) {
                    Path2D curvePath = new Path2D.Double();

                    Vec2 firstPoint = curve.get(0);
                    Point2D firstPointOnScreen = worldToScreenTransform.transform(
                            new Point2D.Double(firstPoint.x, firstPoint.y), null);

                    curvePath.moveTo(firstPointOnScreen.getX(), firstPointOnScreen.getY());

                    for (int i = 1; i < curve.size(); i++) {
                        Vec2 currentPoint = curve.get(i);
                        Point2D currentPointOnScreen = worldToScreenTransform.transform(
                                new Point2D.Double(currentPoint.x, currentPoint.y), null);

                        curvePath.lineTo(currentPointOnScreen.getX(), currentPointOnScreen.getY());
                    }

                    curvePath.closePath(); // geschlossen: letzter Punkt -> erster Punkt
                    graphics2D.draw(curvePath);
                }
            }


            // tangent  normal   curvagure draw
            for (List<Vec2> curve : curves) {

                int numberOfPoints = curve.size();
                if (numberOfPoints < 3) {
                    continue; 
                }
                

                for (int pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {

                    int previousIndex = (pointIndex - 1 + numberOfPoints) % numberOfPoints;
                    int nextIndex = (pointIndex + 1) % numberOfPoints;

                    Vec2 previousPoint = curve.get(previousIndex);
                    Vec2 nextPoint = curve.get(nextIndex);
                    Vec2 currentPoint = curve.get(pointIndex);

                    // direction vector 
                    double tangentX = nextPoint.x - previousPoint.x;
                    double tangentY = nextPoint.y - previousPoint.y;

                    double tangentLength = Math.sqrt(tangentX * tangentX + tangentY * tangentY);
                    if (tangentLength == 0) {
                        continue;
                    }

                    // normieren 
                    tangentX = tangentX / tangentLength;
                    tangentY = tangentY / tangentLength;

                    // normal
                    double normalX = -tangentY;
                    double normalY = tangentX;

                    //  draw normal
                    double lengthNormal = 0.10;

                    Vec2 normalStart = new Vec2(
                            currentPoint.x - normalX * lengthNormal,
                            currentPoint.y - normalY * lengthNormal);

                    Vec2 normalEnd = new Vec2(
                            currentPoint.x + normalX * lengthNormal,
                            currentPoint.y + normalY * lengthNormal);

                    Point2D screenNormalStart = worldToScreenTransform.transform(
                            new Point2D.Double(normalStart.x, normalStart.y), null);

                    Point2D screenNormalEnd = worldToScreenTransform.transform(
                            new Point2D.Double(normalEnd.x, normalEnd.y), null);

                    graphics2D.setColor(Color.BLUE);
                    graphics2D.draw(new Line2D.Double(screenNormalStart, screenNormalEnd));

                    // draw tangent
                    double lengthTangent = 0.15;

                    Vec2 tangentStart = new Vec2(
                            currentPoint.x - tangentX * lengthTangent,
                            currentPoint.y - tangentY * lengthTangent);

                    Vec2 tangentEnd = new Vec2(
                            currentPoint.x + tangentX * lengthTangent,
                            currentPoint.y + tangentY * lengthTangent);

                    Point2D screenTangentStart = worldToScreenTransform.transform(
                            new Point2D.Double(tangentStart.x, tangentStart.y), null);

                    Point2D screenTangentEnd = worldToScreenTransform.transform(
                            new Point2D.Double(tangentEnd.x, tangentEnd.y), null);

                    graphics2D.setColor(new Color(255, 0, 0, 120));
                    graphics2D.draw(new Line2D.Double(screenTangentStart, screenTangentEnd));


                    
                    // curvature calculate 
                    
                    double edge1X = currentPoint.x - previousPoint.x;
                    double edge1Y = currentPoint.y - previousPoint.y;

                    
                    double edge2X = nextPoint.x - currentPoint.x;
                    double edge2Y = nextPoint.y - currentPoint.y;

                    double edge1Length = Math.sqrt(edge1X * edge1X + edge1Y * edge1Y);
                    double edge2Length = Math.sqrt(edge2X * edge2X + edge2Y * edge2Y);

                    if (edge1Length == 0) {
                        continue;
                    }
                    if (edge2Length == 0) {
                        continue;
                    }

                    double dotProduct = edge1X * edge2X + edge1Y * edge2Y;
                    double crossProduct = edge1X * edge2Y - edge1Y * edge2X;

                    double turningAngle = Math.atan2(crossProduct, dotProduct);

                    double averageEdgeLength = (edge1Length + edge2Length) / 2.0;

                    double curvature = turningAngle / averageEdgeLength;

                    // draw curvature
                    double curvatureDisplayScale = 0.15;
                    double curvatureVectorX = normalX * curvature * curvatureDisplayScale;
                    double curvatureVectorY = normalY * curvature * curvatureDisplayScale;

                    Vec2 curvatureEndPoint = new Vec2(
                            currentPoint.x + curvatureVectorX,
                            currentPoint.y + curvatureVectorY);

                    Point2D screenCurrentPoint = worldToScreenTransform.transform(
                            new Point2D.Double(currentPoint.x, currentPoint.y), null);

                    Point2D screenCurvatureEndPoint = worldToScreenTransform.transform(
                            new Point2D.Double(curvatureEndPoint.x, curvatureEndPoint.y), null);

                    graphics2D.setColor(Color.GREEN);
                    graphics2D.draw(new Line2D.Double(screenCurrentPoint, screenCurvatureEndPoint));

                }
            }

        }

        // Curvature-Flow
        private void applyCurvatureFlow(double timeStep) {

            for (int curveIndex = 0; curveIndex < curves.size(); curveIndex++) {

                List<Vec2> curve = curves.get(curveIndex);
                int numberOfPoints = curve.size();

                if (numberOfPoints < 3) {
                    continue;
                }

                List<Vec2> updatedCurve = new java.util.ArrayList<>();

                for (int pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {

                    int previousIndex = (pointIndex - 1 + numberOfPoints) % numberOfPoints;
                    int nextIndex = (pointIndex + 1) % numberOfPoints;

                    Vec2 previousPoint = curve.get(previousIndex);
                    Vec2 currentPoint = curve.get(pointIndex);
                    Vec2 nextPoint = curve.get(nextIndex);

                    // tangent 
                    double tangentX = nextPoint.x - previousPoint.x;
                    double tangentY = nextPoint.y - previousPoint.y;

                    double tangentLength = Math.sqrt(tangentX * tangentX + tangentY * tangentY);

                    if (tangentLength == 0) {
                        updatedCurve.add(currentPoint);
                        continue;
                    }

                    tangentX = tangentX / tangentLength;
                    tangentY = tangentY / tangentLength;

                    // normal 
                    double normalX = -tangentY;
                    double normalY = tangentX;

                    // curvature calculate
                    double edge1X = currentPoint.x - previousPoint.x;
                    double edge1Y = currentPoint.y - previousPoint.y;

                    double edge2X = nextPoint.x - currentPoint.x;
                    double edge2Y = nextPoint.y - currentPoint.y;

                    double edge1Length = Math.sqrt(edge1X * edge1X + edge1Y * edge1Y);
                    double edge2Length = Math.sqrt(edge2X * edge2X + edge2Y * edge2Y);

                    if (edge1Length == 0 || edge2Length == 0) {
                        updatedCurve.add(currentPoint);
                        continue;
                    }

                    double dotProduct = edge1X * edge2X + edge1Y * edge2Y;

                    double crossProduct = edge1X * edge2Y - edge1Y * edge2X;

                    double turningAngle = Math.atan2(crossProduct, dotProduct);

                    double averageEdgeLength = (edge1Length + edge2Length) / 2.0;

                    double curvature = turningAngle / averageEdgeLength;

                    // steps
                    double displacementX = normalX * curvature * timeStep;

                    double displacementY = normalY * curvature * timeStep;

                    Vec2 newPoint = new Vec2(
                            currentPoint.x + displacementX,
                            currentPoint.y + displacementY);

                    updatedCurve.add(newPoint);
                }

                curves.set(curveIndex, updatedCurve);

            }

        }

    }
}

Project Documentation  Java 2D / 3D – Project 1 Steffen Herweg

Overview
This project was developed as part of the module Java 2D / 3D.  
The goal of the project is to load, visualize, and analyze discrete curves 

The curves are provided as point data in `.vert` files and are rendered using Java 2D.  
Tangents, normals, curvature, and a discrete curvature flow are computed and visualized.

Data Format (`.vert` files)
Each `.vert` file contains:
1. The number of curve components  
2. The number of points per component  
3. The point coordinates `(x y)`  

The curves are treated as closed curves, meaning that the first and the last point are connected.

Curve Rendering
The curves are rendered using Java Swing and Java 2D
- A bounding box is computed for all points
- The curve is scaled and centered to fit the window
- A world-to-screen transformation is applied
- Curves are drawn using `Path2D`

Tangent Computation
The tangent at a point is approximated using a central difference scheme:
tangent = nextPoint − previousPoint
The resulting vector is normalized to unit length.  
The tangent represents the local direction of the curve.

Normal Computation
normal = (−tangentY, tangentX)
The normal vector is obtained by rotating the normalized tangent by 90 degrees:
The normal indicates the direction perpendicular to the curve.

Curvature Computation
Curvature measures how strongly the curve bends at a point.
Using three consecutive points, two edge vectors are defined:
edge1 = current − previous
edge2 = next − curren

The turning angle between the edges is computed using:
atan2(cross, dot)
The curvature is then calculated as: curvature = turningAngle / averageEdgeLength
This gives a discrete approximation of curvature as the change in direction per unit length.

Curvature Visualization
The curvature is visualized as a vector along the normal direction:
Direction: normal vector
Length: proportional to the curvature value
This visualies high and low curvature

Curvature Flow
A discrete curvature flow step is to smooth the curve.
Each point is displaced according to:
newPoint = oldPoint + timeStep × curvature × normal
This moves points in the direction of the normal with a speed proportional to the curvature.
Only a small number of curvature flow steps are applied to demonstrate the effect. 

Numerical Considerations
The curvature flow is implemented using explicit time integration.  
For discrete curves with irregular point spacing or sharp corners, this method can become numerically unstable for large time steps or many iterations.
For this reason, the project demonstrates curvature flow only in a limited number of steps.

Conclusion
This project demonstrates how continuous geometric concepts such as tangents, normals, curvature, and curvature flow can be approximated and visualized in a discrete setting using Java 2D.
It also highlights the influence of point distribution and numerical stability in discrete geometric computations.



/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */

void main()
{
	gl_Position = gl_Vertex;
	gl_FrontColor = gl_Color;
}

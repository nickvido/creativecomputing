/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */

varying out vec3 indir;

void main()
{
	gl_Position = gl_Vertex;
	indir = gl_Normal;
}
